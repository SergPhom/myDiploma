package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.UserWallRepository
import javax.inject.Inject



@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val repository: UserWallRepository,
    private val appAuth: AppAuth
): ViewModel() {

    var userId = MutableLiveData(0L).also { println("userId now is ${it.value}") }

    private val cashed = repository.userWallData(userId = userId.value ?: 0L)

    val data: Flow<PagingData<Post>> =
        cashed
}