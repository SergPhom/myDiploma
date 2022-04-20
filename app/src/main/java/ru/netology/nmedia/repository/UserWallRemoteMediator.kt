package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.nmedia.api.WallApiService
import ru.netology.nmedia.dao.UserWallDao
import ru.netology.nmedia.dao.UserWallRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.UserWallEntity
import ru.netology.nmedia.entity.UserWallRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class UserWallRemoteMediator @Inject constructor(
    private val apiService: WallApiService,
    private val dao: UserWallDao,
    private val userWallRemoteKeyDao: UserWallRemoteKeyDao,
    private val db: AppDb,
    userId: Long
): RemoteMediator<Int, UserWallEntity>() {

    override suspend fun initialize(): InitializeAction =
        if(dao.isEmpty()){
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }

    private fun checkResponse(response: Response<List<Post>>): List<Post> {
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body()
        //body?.forEach { println("Response is $it") }
        return body ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserWallEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (userWallRemoteKeyDao.isEmpty()) {
                        apiService.getLatest( 0L, state.config.pageSize)
                    } else{
                        val id = userWallRemoteKeyDao.max() ?:
                        return  MediatorResult.Success(false)
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }
                LoadType.APPEND -> {
                    println("append working")
                    val id = userWallRemoteKeyDao.min() ?:
                    return MediatorResult.Success(false)
                    apiService.getBefore(
                        id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    println("prepend  working")
                    return MediatorResult.Success(false)
                }
            }
            var posts = checkResponse(response)
            db.withTransaction {
                when(loadType){
                    LoadType.REFRESH->{
                        val before = if(userWallRemoteKeyDao.min() != null){
                            userWallRemoteKeyDao.min()!!
                        } else posts.last().id
                        userWallRemoteKeyDao.insert(
                            listOf(
                                UserWallRemoteKeyEntity(
                                    type = UserWallRemoteKeyEntity.KeyType.AFTER,
                                    id = posts.first().id),
                                UserWallRemoteKeyEntity(
                                    type = UserWallRemoteKeyEntity.KeyType.BEFORE,
                                    id = before
                                )
                            )
                        )
                    }
                    LoadType.APPEND->{
                        userWallRemoteKeyDao.insert(
                            UserWallRemoteKeyEntity(
                                type = UserWallRemoteKeyEntity.KeyType.BEFORE,
                                id = posts.last().id)
                        )
                    }
                }
                dao.insert(posts.map(UserWallEntity::fromDto))
            }
            return MediatorResult.Success(posts.isEmpty())

        } catch (t: Throwable) {
            println("WallRemoteMedi error is $t")
            return MediatorResult.Error(t)
        }
    }
}
