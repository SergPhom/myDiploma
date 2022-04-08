package ru.netology.nmedia.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface EventRepository {
    val data: Flow<PagingData<Event>>

    suspend fun getAll()

    suspend fun likeById(id: Long)

    suspend fun dislikeById(id: Long)

    suspend fun saveEvent(event: Event)
    suspend fun saveEventWithAttachment(event: Event, upload: MediaUpload)

    suspend fun removeById(id: Long)

    suspend fun upload(upload: MediaUpload): Media
}