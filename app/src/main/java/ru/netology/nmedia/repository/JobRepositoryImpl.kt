package ru.netology.nmedia.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.entity.fromDto
import ru.netology.nmedia.api.JobApiService
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dao.JobRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.UserJob
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService,
    private val jobDao: JobDao,
    private val jobKeyDao: JobRemoteKeyDao,
    private val db: AppDb,
): JobRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalPagingApi::class)
    override fun userProfileData(userId: Long): Flow<PagingData<UserJob>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = JobRemoteMediator(jobApiService, jobDao,
            jobKeyDao, db, userId),
        pagingSourceFactory = { jobDao.getAll() },
    )
        .flow
        .map { it.map(JobEntity::toDto) }
        .catch { println("UserProfile error in repository data is - $it") }
        .flowOn(Dispatchers.Default)


    override suspend fun getAll(id: Long) {
        try {
            val response = jobApiService.getJobs(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
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
            val jobs = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(jobs.fromDto())
        }catch (t: Throwable){
            println("JobRepository error is $t")
        }
    }

    override suspend fun removeJob(userJob: UserJob) {
        jobApiService.deleteJob(userJob.id)
        jobDao.removeById(userJob.id)
        jobKeyDao.removeAll()
    }

}
