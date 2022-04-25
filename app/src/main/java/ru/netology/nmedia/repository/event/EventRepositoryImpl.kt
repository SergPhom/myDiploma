package ru.netology.nmedia.repository.event

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.fromDto
import ru.netology.nmedia.api.EventApiService
import ru.netology.nmedia.api.MediaApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val mediaApiService: MediaApiService,
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
        try {
            val response = eventApiService.likeEvent(id)
            if(!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError (response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        }catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = eventApiService.dislikeEvent(id)
            if(!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError (response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        }catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveEvent(event: Event) {
        try {
            val response = eventApiService.saveEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (t: Throwable) {
            println("event repo error is ${t.stackTrace}")
        }
    }

    override suspend fun saveEventWithAttachment(event: Event, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val eventWithAttachment = event.copy(attachment = Attachment(media.url,
                "MyPhoto", AttachmentType.IMAGE)
            )
            saveEvent(eventWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try{
            val response = eventApiService.removeById(id)
            if(!response.isSuccessful){
                throw  ApiError(response.code(), response.message())
            }
            eventDao.removeById(id)
        } catch (t: Throwable){
            println("Event repository error is $t")
        }

    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody())

            val response = mediaApiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}