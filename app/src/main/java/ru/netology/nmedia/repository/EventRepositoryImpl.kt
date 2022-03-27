package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.fromDto
import ru.netology.nmedia.api.EventApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao
): EventRepository {
    override val data: Flow<PagingData<Event>>
        get() = TODO("Not yet implemented")

    override suspend fun getAll() {
        try {
            val response = eventApiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(body.fromDto())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}