package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Job

interface JobApiService {
    @GET("{id}/jobs")
    suspend fun getJobs(@Path("id") id: Long): Response<List<Job>>

    @GET("my/jobs")
    suspend fun getMyJobs():Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job: Job):Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long)

}
