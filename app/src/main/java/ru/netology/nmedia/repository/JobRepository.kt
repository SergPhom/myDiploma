package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Job

interface JobRepository {

    val data: Flow<Job>

    suspend fun getAll(id: Long)

    suspend fun saveJob(job: Job)

    suspend fun removeJob(job: Job)
}
