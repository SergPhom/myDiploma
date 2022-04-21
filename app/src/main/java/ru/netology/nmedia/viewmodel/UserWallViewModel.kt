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
import ru.netology.nmedia.activity.userId
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.UserWallRepository
import javax.inject.Inject



@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: UserWallRepository,
    private val appAuth: AppAuth
): ViewModel() {

    fun userId(): Long{
        return savedStateHandle.userId.also{
            println("userVM get userId - ${savedStateHandle.userId}")
        }
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