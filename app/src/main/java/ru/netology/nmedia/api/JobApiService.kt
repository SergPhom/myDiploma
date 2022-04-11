package ru.netology.nmedia.api

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.UserJob

interface JobApiService {
    @GET("{id}/jobs")
    suspend fun getJobs(@Path("id") id: Long): Response<List<UserJob>>

    @GET("my/jobs")
    suspend fun getMyJobs(): Response<List<UserJob>>

    @POST("my/jobs")
    suspend fun saveJob(@Body userJob: UserJob):Response<UserJob>

    @DELETE("my/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long)

}
