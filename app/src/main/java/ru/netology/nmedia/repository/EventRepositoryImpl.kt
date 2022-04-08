package ru.netology.nmedia.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.fromDto
import ru.netology.nmedia.api.EventApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao,
    pager: Pager<Int, EventEntity>
): EventRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalPagingApi::class)
    override val data = pager
        .flow
        .map { it.map(EventEntity::toDto) }
        .catch { println("Event repo error $it") }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = eventApiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            println("Event repos. body is $body")
            eventDao.insert(body.fromDto())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun dislikeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun saveEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun saveEventWithAttachment(event: Event, upload: MediaUpload) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun upload(upload: MediaUpload): Media {
        TODO("Not yet implemented")
    }
}