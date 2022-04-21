package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.netology.nmedia.api.UsersApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import javax.inject.Inject

private val noPhoto = PhotoModel()

@HiltViewModel
class SignUpModel @Inject constructor(
    private val usersApiService: UsersApiService,
    private val appAuth: AppAuth
): ViewModel() {
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo


    fun setAuth(id: Long, token: String){
        appAuth.setAuth(id,token)
    }
    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
        println(" photo is ${_photo.value}")
    }
    fun registrate(login: String,
                   password: String,
                   name: String,
                   file: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            try {
                val response = if(file != null){
                    usersApiService
                        .registrationWithAvatar(login, password, name, file)
                } else usersApiService.registration(login,password, name)

                if (!response.isSuccessful) {

                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                setAuth(body.id, body.token)
            } catch (t: Throwable) {
                println("registration error is $t")
                //throw NetworkError
            } catch (t: Throwable) {
                println("registration error is $t")
                //throw UnknownError
            } catch (e: Throwable) {
                println ("AVM e 2 $e")
            }
        }
    }
}