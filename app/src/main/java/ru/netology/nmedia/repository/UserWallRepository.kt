package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface UserWallRepository {

    fun userWallData(userId: Long): Flow<PagingData<Post>>

    suspend fun getAll(id: Long)

    suspend fun clearData()

    suspend fun dislikePost(post: Post)

    suspend fun likePost(post: Post)

}
