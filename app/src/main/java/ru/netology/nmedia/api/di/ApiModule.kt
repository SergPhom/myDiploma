package ru.netology.nmedia.api.di

import android.content.SharedPreferences
import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.TOKEN_KEY
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.api.UsersApiService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

    @Provides
    @Singleton
    fun provideLogging() = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkhttp(
        logging: HttpLoggingInterceptor,
        authPrefs: SharedPreferences
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                authPrefs.getString(TOKEN_KEY, null)?.let { token ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", token)
                        .build()
                    return@addInterceptor chain.proceed(newRequest)
                }
                chain.proceed(chain.request())
            }
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okhttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttpClient)
        .build()

    @Provides
    @Singleton
    fun providePostApiService(
        retrofit: Retrofit
    ): PostsApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideUserApiService(
        retrofit: Retrofit
    ): UsersApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideGoogleApiAvailability(): GoogleApiAvailability = GoogleApiAvailability.getInstance()
}
