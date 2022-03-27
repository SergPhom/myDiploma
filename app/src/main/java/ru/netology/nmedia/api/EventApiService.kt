package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.GET
import ru.netology.nmedia.dto.Event

interface EventApiService {
    @GET("events")
    suspend fun getAll(): Response<List<Event>>
}