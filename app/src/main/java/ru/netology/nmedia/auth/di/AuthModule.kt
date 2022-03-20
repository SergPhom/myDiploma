package ru.netology.nmedia.auth.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthPrefs(@ApplicationContext context: Context): SharedPreferences =
       context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAppAuth(
        service: PostsApiService,
        prefs: SharedPreferences
    ): AppAuth = AppAuth(service, prefs)
}