package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.dto.Event

interface EventApiService {
    @GET("events")
    suspend fun getAll(): Response<List<Event>>

    @GET("events/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>
}