package ru.netology.nmedia.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.repository.event.EventRepository
import java.lang.Exception
import java.time.Instant
import java.time.OffsetDateTime
import javax.inject.Inject

private val empty = Event(
    id = 0,
    content = "",
    authorId = 1L,
    datetime = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    published = "",
    attachment = null,
    type = EventType.ONLINE.toString()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val appAuth: AppAuth,
): ViewModel() {

    val edited = MutableLiveData(empty)
    val datetime = MutableLiveData(OffsetDateTime.MIN)
    val type = MutableLiveData(EventType.NONE)
    private val cashed = repository.data
        .cachedIn(viewModelScope)
    val data: Flow<PagingData<Event>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId,_) ->
            cashed.map { event ->
                event.map {
                    it.copy(ownedByMe = it.authorId == myId,
                        likedByMe = it.likeOwnerIds.contains(myId))
                }
            }
        }

    val authenticated = appAuth
        .authStateFlow.map {
            it.id != 0L
        }
        .asLiveData(Dispatchers.Default)

    @RequiresApi(Build.VERSION_CODES.O)
    fun clearEvent(){
        datetime.value = OffsetDateTime.MIN
        type.value = EventType.NONE
        edited.value = empty
    }

    fun changeEvent(content: String,
                    link: String?,
                    coords: Coordinates?
    ) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text,
            datetime = datetime.value.toString(),
            type = type.value.toString(),
            link = link,
            coords = coords
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun save() {
        edited.value?.let {
            it.published = Instant.now().toString()
            println("Instant is   ${Instant.now()} ")
            viewModelScope.launch {
                repository.saveEvent(it)
//                try {
//                    when(_photo.value) {
//                        noPhoto -> repository.savePost(it)
//                        else -> _photo.value?.file?.let { file ->
//                            repository.savePostWithAttachment(it, MediaUpload(file))
//                        }
//                    }
//                    _dataState.value = FeedModelState()
//                } catch (e: Exception) {
//                    _dataState.value = FeedModelState( msg = "Saving error")
//                }
            }
        }
        edited.value = empty
        //_photo.value = noPhoto
    }

    fun removeEvent(event: Event) {
        viewModelScope.launch {
            try {
                repository.removeById(event.id)
            } catch (e: Exception) {
                println("removeEvent error is $e")
            }
        }
    }
}