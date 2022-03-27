package ru.netology.nmedia.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Event

interface EventRepository {
    val data: Flow<PagingData<Event>>

    suspend fun getAll()
}