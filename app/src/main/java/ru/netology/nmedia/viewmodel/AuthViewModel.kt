package ru.netology.nmedia.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import ru.netology.nmedia.R
import ru.netology.nmedia.api.UsersApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.UserRepository
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val usersApiService: UsersApiService,
    private val userRepository: UserRepository,
): ViewModel() {
    val data: LiveData<AuthState> = appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    fun setAuth(id: Long, token: String){
        appAuth.setAuth(id,token)
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            userRepository.getUsers()
        } catch (e: java.lang.Exception) {

        }
    }

    suspend fun getToken(login: String, pass: String): Boolean{
        var result = false
        viewModelScope.async{
            try {
                val response = usersApiService.getToken(login,pass)
                if (response.isSuccessful) {
                    println("AuthVM1 is $result")
                    val body = response.body() ?: throw ApiError(response.code(), response.message())
                    setAuth(body.id, body.token)
                    result = true
                    println("AuthVM2 is $result")
                }
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            } catch (e: Throwable) {
                println ("AVM e 2 $e")
            }
        }.await()
        println("AuthVM3 is $result")
        return result
    }
}