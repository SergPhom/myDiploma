package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.nework.entity.JobEntity
import ru.netology.nmedia.api.JobApiService
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dao.JobRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.UserJob
import ru.netology.nmedia.entity.JobRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class JobRemoteMediator @Inject constructor(
    private val apiService: JobApiService,
    private val dao: JobDao,
    private val jobRemoteKeyDao: JobRemoteKeyDao,
    private val db: AppDb,
    private val userId: Long
): RemoteMediator<Int, JobEntity>() {

    override suspend fun initialize(): InitializeAction =
        if(dao.isEmpty()){
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            dao.removeAll()
            jobRemoteKeyDao.removeAll()
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    private fun checkResponse(
        response: Response<List<UserJob>>): List<UserJob> {
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, JobEntity>
    ): MediatorResult {
        try {
            val response = when (loadType){
                LoadType.REFRESH -> {
                    if (jobRemoteKeyDao.isEmpty()){
                        apiService.getJobs(userId)
                    }else{
                        val id = jobRemoteKeyDao.max() ?:
                        return MediatorResult.Success(false)
                        apiService.getJobs(userId)
                    }
                }
                LoadType.APPEND -> {
                    return MediatorResult.Success(false)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
            }
            val jobs = checkResponse(response)
            db.withTransaction {
                when(loadType){
                    LoadType.REFRESH -> {
                        val before = if(jobRemoteKeyDao.min() != null){
                            jobRemoteKeyDao.min()!!
                        } else jobs.last().id
                        jobRemoteKeyDao.insert(
                            listOf(
                                JobRemoteKeyEntity(
                                    type = JobRemoteKeyEntity.KeyType.AFTER,
                                    id = jobs.first().id),
                                JobRemoteKeyEntity(
                                    type = JobRemoteKeyEntity.KeyType.BEFORE,
                                    id = before
                                )
                            )
                        )
                    }
                }
                dao.insert(jobs.map(JobEntity::fromDto))
            }
            return MediatorResult.Success(jobs.isEmpty())
        }catch (t: Throwable){
            return MediatorResult.Error(t)
        }
    }
}