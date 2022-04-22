package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nmedia.activity.userAvatar
import ru.netology.nmedia.activity.userId
import ru.netology.nmedia.activity.userName
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.UserRepositoryImpl
import ru.netology.nmedia.repository.UserWallRepository
import javax.inject.Inject



@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: UserWallRepository,
    private val userRepo: UserRepositoryImpl,
    private val appAuth: AppAuth
): ViewModel() {

    fun userId(): Long{
        return savedStateHandle.userId
    }
    fun avatar(): String{
        return savedStateHandle.userAvatar
    }
    fun userName(): String{
        return savedStateHandle.userName
    }

    private val cashed = repository.userWallData(userId = userId())
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> =
        cashed

    fun clearData(){
        viewModelScope.launch {
            repository.clearData()
        }
    }
}