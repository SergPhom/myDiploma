package ru.netology.nmedia.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data: Flow<PagingData<Post>>

    suspend fun  getUsers()
//                 **************              READ
    suspend fun getAll()

    fun getNewerCount(): Flow<Int>

//             ***************                UPDATE

    suspend fun likeById(id: Long)

    suspend fun dislikeById(id: Long)

    suspend fun savePost(post: Post)
    suspend fun savePostWithAttachment(post: Post, upload: MediaUpload)

    suspend fun sharePost(id: Long)

//    suspend fun newerPostsViewed()
//            ***************               REMOVE

    suspend fun removeById(id: Long)

    suspend fun upload(upload: MediaUpload): Media

//    suspend fun fillInDb()
}
