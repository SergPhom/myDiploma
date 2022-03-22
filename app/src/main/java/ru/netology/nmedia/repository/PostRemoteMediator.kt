package ru.netology.nmedia.repository

import androidx.activity.viewModels
import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.lang.Exception
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val apiService: PostsApiService,
    private val dao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val db: AppDb
): RemoteMediator<Int, PostEntity>() {

    override suspend fun initialize(): InitializeAction =
        if(dao.isEmpty()){
            println("remMedi DB is empty")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            println("remMedi DB is NOT empty")
            InitializeAction.SKIP_INITIAL_REFRESH
        }

    private fun checkResponce(response: Response<List<Post>>): List<Post>{
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val posts = response.body() ?: throw ApiError(response.code(), response.message())
        return posts
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try{
            println("remMedi load work")
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    println("remMedi load work refresh")
                    if (postRemoteKeyDao.isEmpty()) {
                        apiService.getLatest(state.config.pageSize)
                    } else{
                        val id = postRemoteKeyDao.max() ?:
                            return  MediatorResult.Success(false)
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }
                LoadType.APPEND -> {
                    println("remMedi load work append")
                    val id = postRemoteKeyDao.min() ?:
                        return MediatorResult.Success(false)
                    apiService.getBefore(
                        id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
            }
            var posts = checkResponce(response)
            println("remMedi load work posts granted ${posts.size}")
            db.withTransaction {
                when(loadType){
                    LoadType.REFRESH->{
                        val before = if(postRemoteKeyDao.min() != null){
                            postRemoteKeyDao.min()!!
                        } else posts.last().id
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    id = posts.first().id),
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    id = before
                                )
                            )
                        )
                    }
                    LoadType.APPEND->{
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = posts.last().id)
                        )
                    }
                }
                dao.insert(posts.map(PostEntity::fromDto))
            }
            return MediatorResult.Success(posts.isEmpty())
        }catch (e: Exception){
            return MediatorResult.Error(e)
        }
    }
}

