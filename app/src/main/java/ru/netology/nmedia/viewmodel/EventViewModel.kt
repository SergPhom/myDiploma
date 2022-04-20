package ru.netology.nmedia.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.repository.event.EventRepository
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
    val data: Flow<PagingData<Event>> = cashed

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
}