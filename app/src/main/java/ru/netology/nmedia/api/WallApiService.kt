package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

interface WallApiService {
    @GET("{id}/wall")
    suspend fun getAll(
        @Path("id") id: Long
    ): Response<List<Post>>

    @GET("{id}/wall/latest")
    suspend fun getLatest(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{id}/wall/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{id}/wall/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{id}/wall/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("{id}/wall/{postId}")
    suspend fun getPost(
        @Path("id") id: Long,
        @Path("postId") postId: Long
    ): Response<Post>

    @POST("{id}/wall/{postId}/likes")
    suspend fun likePost(
        @Path("id") id: Long,
        @Path("postId") postId: Long
    ): Response<Post>

    @DELETE("{id}/wall/{postId}/likes")
    suspend fun dislikePost(
        @Path("id") id: Long,
        @Path("postId") postId: Long
    ): Response<Post>

   // @GET("my/wall")
}