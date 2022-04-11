package ru.netology.nmedia.repository

import androidx.paging.Pager
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.entity.fromDto
import ru.netology.nmedia.api.JobApiService
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dto.UserJob
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService,
    private val jobDao: JobDao,
    pager: Pager<Int, JobEntity>
): JobRepository {

    override val data = pager
        .flow
        .map { it.map(JobEntity::toDto) }
        .also { flow -> flow.map { it1 -> it1.map { println("data unit is $it") } } }
        .catch { println("Job repo data error is $it") }
        .flowOn(Dispatchers.Default)


    override suspend fun getAll(id: Long) {
        try {
            val response = jobApiService.getJobs(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            println("Event repos. body is $body")
            jobDao.insert(body.fromDto())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveJob(userJob: UserJob) {
        try {
            val response = jobApiService.saveJob(userJob)
            if(!response.isSuccessful){
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()
            println("Job response is $body")
        }catch (t: Throwable){
            println("JobRepository error is $t")
        }
    }

    override suspend fun removeJob(userJob: UserJob) {
        jobApiService.deleteJob(userJob.id)
    }

}
