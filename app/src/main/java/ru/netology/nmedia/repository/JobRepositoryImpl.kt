package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.api.JobApiService
import ru.netology.nmedia.dto.Job
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    val jobApiService: JobApiService
): JobRepository {
    override val data: Flow<Job>
        get() = TODO("Not yet implemented")

    override suspend fun getAll(id: Long) {
        jobApiService.getJobs(id)
    }

    override suspend fun saveJob(job: Job) {
        jobApiService.saveJob(job)
    }

    override suspend fun removeJob(job: Job) {
        jobApiService.deleteJob(job.id)
    }

}
