package ru.netology.nmedia.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.post.PostRepository
import java.io.File
import java.lang.Exception
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val empty = Post(
    id = 0,
    content = "",
    authorId = 1L,
    author = "",
    authorAvatar = "",
    likedByMe = false,
    published = "",
    attachment = null,
    ownedByMe = true,
)
private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
): ViewModel(){

    val authenticated = appAuth
        .authStateFlow.map {
            it.id != 0L
        }
        .asLiveData(Dispatchers.Default)
    val edited = MutableLiveData(empty)
    var draft = ""
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    //data
    private val cashed = repository.data
        .cachedIn(viewModelScope)
    val data: Flow<PagingData<FeedItem>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            cashed.map { posts ->
                posts.map {
                    it.copy(ownedByMe = it.authorId == myId, likedByMe = it.likeOwnerIds.contains(myId))
                }
            }
        }

    fun forAuthenticated() {
        edited.postValue(empty.copy(authorId = appAuth.authStateFlow.value.id))
    }

    //CRUD operations
    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
    fun onEdit(post: Post){
        edited.value = post
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun save() {
        edited.value?.let {
            it.published = Instant.now().toString()
            viewModelScope.launch {
                try {
                    when(_photo.value) {
                        noPhoto -> repository.savePost(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.savePostWithAttachment(it, MediaUpload(file))
                        }
                    }
                } catch (e: Exception) {
                    println("saving post error is $e")
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun onLiked(post: Post) {
        if (post.likedByMe) {
           viewModelScope.launch {
               try {
                   repository.dislikeById(post.id)
               } catch (e: Exception) {
                   println("liking post error is $e")
               }
           }
        } else{
            viewModelScope.launch {
                try {
                    repository.likeById(post.id)
                } catch (e: Exception) {
                    println("liking post error is $e")
                }
            }
        }
    }

    fun onRemove(post: Post) {
    viewModelScope.launch {
        try {
            repository.removeById(post.id)
        } catch (e: Exception) {
            println("removing post error is $e")
        }
    }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun cancel(){
        edited.value = empty
    }
}