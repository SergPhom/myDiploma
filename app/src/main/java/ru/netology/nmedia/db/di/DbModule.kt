package ru.netology.nmedia.db.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.*
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
    fun providePostRemoteKeyDao(
        appDb: AppDb
    ): PostRemoteKeyDao = appDb.postRemoteKeyDao()

    @Provides
    fun provideUserDao(
        appDb: AppDb
    ): UserDao = appDb.userDao()

    @Provides
    fun provideEventDao(
        appDb: AppDb
    ): EventDao = appDb.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(
        appDb: AppDb
    ): EventRemoteKeyDao = appDb.eventRemoteKeyDao()

    @Provides
    fun provideJobDao(
        appDb: AppDb
    ): JobDao = appDb.jobDao()

    @Provides
    fun provideJobRemoteKeyDao(
        appDb: AppDb
    ): JobRemoteKeyDao = appDb.jobRemoteKeyDao()

    @Provides
    fun provideUserWallDao(
        appDb: AppDb
    ): UserWallDao = appDb.userWallDao()

    @Provides
    fun provideUserWallRemoteKeyDao(
        appDb: AppDb
    ): UserWallRemoteKeyDao = appDb.userWallRemoteKeyDao()
}