package ru.netology.nmedia.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.WallApiService
import ru.netology.nmedia.dao.UserWallDao
import ru.netology.nmedia.dao.UserWallRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.UserWallEntity
import ru.netology.nmedia.entity.toUserWallEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWallRepositoryImpl @Inject constructor(
    private val dao: UserWallDao,
    private val keyDao: UserWallRemoteKeyDao,
    private val userWallApiService: WallApiService,
    val apiService: WallApiService,
    val db: AppDb
): UserWallRepository {

//    override  var userId = 0L
//    @OptIn(ExperimentalPagingApi::class)
//    val customPager = Pager(
//        config =  PagingConfig(30),
//        remoteMediator = UserWallRemoteMediator(apiService,dao,remoteKeyDao,db,userId),
//        pagingSourceFactory = { dao.getAll()}
//    )

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalPagingApi::class)
    override fun userWallData(userId: Long): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = UserWallRemoteMediator(userWallApiService, dao, keyDao, db, userId),
        pagingSourceFactory = { dao.getAll() },
    )
        .flow
        .map { it.map(UserWallEntity::toDto) }
        .catch { println("UserWall error in repository data is - $it") }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(id: Long) {
        try{
            val response = userWallApiService.getAll(id)
            if(!response.isSuccessful) {
                println("UserWall response error is ${response.code()}")
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toUserWallEntity())
        } catch (e: IOException){
            println("UserWall error in repository IO is - $e")
        } catch (t: Throwable){
            println("UserWall error in repository IO is - $t")
        }
    }

    override suspend fun clearData(){
        dao.removeAll()
        keyDao.removeAll()
    }
}
