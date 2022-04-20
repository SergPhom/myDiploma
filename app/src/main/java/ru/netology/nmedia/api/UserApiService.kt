package ru.netology.nmedia.api


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.User

interface UsersApiService {

    @GET("users")
    suspend fun getAll(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("users/authentication")
    @FormUrlEncoded
    suspend fun getToken (
        @Field("login") login:String,
        @Field("pass") pass: String): Response<UserIdToken>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("users/registration")
    @FormUrlEncoded
    suspend fun registration (
        @Field("login") login:String,
        @Field("pass") pass: String,
        @Field("name") name: String): Response<UserIdToken>


    @POST("users/registration")
    @Multipart
    suspend fun registrationWithAvatar (
        @Part("login") login: String,
        @Part("pass") pass: String,
        @Part("name") name: String,
        @Part file: MultipartBody.Part?): Response<UserIdToken>

}
