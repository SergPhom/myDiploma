package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserJob


interface JobRepository {

    fun userProfileData(userId: Long): Flow<PagingData<UserJob>>

    suspend fun getAll(id: Long)

    suspend fun saveJob(userJob: UserJob)

    suspend fun removeJob(userJob: UserJob)
}
