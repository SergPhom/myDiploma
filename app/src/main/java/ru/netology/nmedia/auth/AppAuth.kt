package ru.netology.nmedia.auth


import android.content.SharedPreferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.ID
import ru.netology.nmedia.TOKEN_KEY
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    private val postsApiService: PostsApiService,
    val authPrefs: SharedPreferences
) {

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = authPrefs.getLong(ID, 0)
        val token = authPrefs.getString(TOKEN_KEY, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(authPrefs.edit()) {
               clear()
               apply()
           }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }

        sendPushToken()
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(authPrefs.edit()) {
            putLong(ID, id)
            putString(TOKEN_KEY, token)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(authPrefs.edit()) {
            clear()
            commit()
        }
        deletePushToken()
        sendPushToken()
    }

    fun deletePushToken(){
        CoroutineScope(Dispatchers.Default).launch {
            try {
                Firebase.messaging.deleteToken()
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                postsApiService.save(pushToken)
                println("AppAuth  $pushToken")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class AuthState(val id: Long = 0, val token: String? = null)