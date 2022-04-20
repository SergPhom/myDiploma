package ru.netology.nmedia.api.di

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.TOKEN_KEY
import ru.netology.nmedia.USER_ID
import ru.netology.nmedia.api.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

//    @Provides
//    @Singleton
//    fun provideUserPrefs(@ApplicationContext context: Context): SharedPreferences =
//        context.getSharedPreferences("user", Context.MODE_PRIVATE)

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
                        .also { println("Request url is ${chain.request().url}") }
                    return@addInterceptor chain.proceed(newRequest)
                }
                chain.proceed(chain.request())
                    .also { println("Request url is ${chain.request().url}") }
            }
            .addInterceptor { chain ->
                authPrefs.getString(USER_ID, null)?.let { userId ->
                    val newRequest = chain.request().newBuilder()
                        .url("https://nework-diploma.herokuapp.com/api/$userId/wall/latest?count=30")
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
    fun provideEventApiService(
        retrofit: Retrofit
    ): EventApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideJobApiService(
        retrofit: Retrofit
    ): JobApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideMediaApiService(
        retrofit: Retrofit
    ): MediaApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideWallApiService(
        retrofit: Retrofit
    ): WallApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideGoogleApiAvailability(

    ): GoogleApiAvailability = GoogleApiAvailability.getInstance()
}
