package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post

interface UserWallRepository {

    fun userWallData(userId: Long): Flow<PagingData<Post>>

   // val data: Flow<PagingData<Post>>

    suspend fun getAll(id: Long)

}
