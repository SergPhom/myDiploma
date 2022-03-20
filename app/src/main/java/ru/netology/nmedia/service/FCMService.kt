package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import java.lang.IllegalArgumentException
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService: FirebaseMessagingService() {

    @Inject
    lateinit var appAuth: AppAuth
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            message.data[action]?.let {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
                    Action.NEW_POST -> handleNewPost(
                        gson.fromJson(
                            message.data[content], NewPost::class.java
                        )
                    )
                }
                return
            }
            handleContent(gson.fromJson(message.data[content], PushMessage::class.java))
        }
        catch (e: IllegalArgumentException){
            println("No such action class, $e")
            return
        }
    }

    private fun handleContent(message: PushMessage) {
        val id = appAuth.authStateFlow.value.id
        when(message.recipientId){
            id,null -> {
                val notification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(message.content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                NotificationManagerCompat.from(this)
                    .notify(Random.nextInt(100_000), notification)
            }
            else -> appAuth.sendPushToken()
        }

    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleNewPost(content: NewPost) {
        var notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_new_post, content.userName))
            .setContentText(content.postText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(content.postText))
            .build()
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}
    enum class Action {
            LIKE,
            NEW_POST,
    }



data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postText: String,
)

data class PushMessage(
    val recipientId: Long?,
    val content: String
)