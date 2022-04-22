package ru.netology.nmedia.repository.post

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
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }

    private fun checkResponse(response: Response<List<Post>>): List<Post> {
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body()
        return body ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try{
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (postRemoteKeyDao.isEmpty()) {
                        apiService.getLatest(state.config.pageSize)
                    } else{
                        val id = postRemoteKeyDao.max() ?:
                            return  MediatorResult.Success(false)
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?:
                        return MediatorResult.Success(false)
                    apiService.getBefore(
                        id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
            }
            var posts = checkResponse(response)
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

