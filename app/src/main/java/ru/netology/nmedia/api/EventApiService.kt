package ru.netology.nmedia.api

import androidx.room.Delete
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post
import javax.persistence.PostLoad

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

    @GET("events/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Event>>

    @GET("events/{id}")
    suspend fun eventById(@Path("id") id: Long):  Response<Event>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @POST("events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): Response<Event>

    @POST("events/{id}/participants")
    suspend fun participateEvent(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeById(@Path("id") id: Long):  Response<Unit>

    @DELETE("events/{id}/likes")
    suspend fun dislikeEvent(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/participants")
    suspend fun unparticipateEvent(@Path("id") id: Long): Response<Event>
}