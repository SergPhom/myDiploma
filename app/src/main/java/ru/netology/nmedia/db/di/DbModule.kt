package ru.netology.nmedia.db.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDb  = AppDb.buildDatabase(context)


    @Provides
    fun providePostDao(
        appDb: AppDb
    ):PostDao  = appDb.postDao()

    @Provides
    fun providePostREmoteKeyDao(
        appDb: AppDb
    ): PostRemoteKeyDao = appDb.postRemoteKeyDao()
}