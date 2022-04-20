package ru.netology.nmedia.repository.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.nework.entity.EventEntity
import ru.netology.nmedia.api.EventApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.EventRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.lang.Exception
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator @Inject constructor(
    private val apiService: EventApiService,
    private val dao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val db: AppDb
): RemoteMediator <Int, EventEntity>(){

    override suspend fun initialize(): InitializeAction =
        if(dao.isEmpty()){
            println("Event remMedi DB is empty")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            println("Event remMedi DB is NOT empty")
            InitializeAction.SKIP_INITIAL_REFRESH
        }

    private fun checkResponse(response: Response<List<Event>>): List<Event> {
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try{
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (eventRemoteKeyDao.isEmpty()) {
                        apiService.getLatest(state.config.pageSize)
                    } else{
                        val id = eventRemoteKeyDao.max() ?:
                        return  MediatorResult.Success(false)
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?:
                    return MediatorResult.Success(false)
                    apiService.getBefore(
                        id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
            }
            var events = checkResponse(response)

            db.withTransaction {
                when(loadType){
                    LoadType.REFRESH->{
                        val before = if(eventRemoteKeyDao.min() != null){
                            eventRemoteKeyDao.min()!!
                        } else events.last().id
                        eventRemoteKeyDao.insert(
                            listOf(
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.AFTER,
                                    id = events.first().id),
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.BEFORE,
                                    id = before
                                )
                            )
                        )
                    }
                    LoadType.APPEND->{
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.BEFORE,
                                id = events.last().id)
                        )
                    }
                }
                dao.insert(events.map(EventEntity::fromDto))
            }
            return MediatorResult.Success(events.isEmpty())
        }catch (e: Exception){
            return MediatorResult.Error(e)
        }
    }
}