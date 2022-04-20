package ru.netology.nmedia.repository.post


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data: Flow<PagingData<Post>>
//                 **************              CREATE

    suspend fun savePost(post: Post)
    suspend fun savePostWithAttachment(post: Post, upload: MediaUpload)

//                 **************              READ
    suspend fun getAll()

    fun getNewerCount(): Flow<Int>

//             ***************                UPDATE

    suspend fun likeById(id: Long)

    suspend fun dislikeById(id: Long)

//    suspend fun newerPostsViewed()
//            ***************               DELETE

    suspend fun removeById(id: Long)

    suspend fun upload(upload: MediaUpload): Media

//    suspend fun fillInDb()
}
