package ru.netology.nmedia.repository.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.JobEntity
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.UserWallDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.UserWallEntity
import ru.netology.nmedia.repository.event.EventRemoteMediator
import ru.netology.nmedia.repository.JobRemoteMediator
import ru.netology.nmedia.repository.post.PostRemoteMediator
import ru.netology.nmedia.repository.UserWallRemoteMediator
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PagerModule{
    @ExperimentalPagingApi
    @Provides
    @Singleton
    fun providePostPager(dao: PostDao, postRemoteMediator: PostRemoteMediator): Pager<Int, PostEntity> = Pager(
        config = PagingConfig(pageSize = 30, enablePlaceholders = false),
        remoteMediator = postRemoteMediator,
        pagingSourceFactory = { dao.getAll() }
    )

    @ExperimentalPagingApi
    @Provides
    @Singleton
    fun provideEventPager(
        dao: EventDao, eventRemoteMediator: EventRemoteMediator
    ): Pager<Int, EventEntity> = Pager(
        config = PagingConfig(pageSize = 30, enablePlaceholders = false),
        remoteMediator = eventRemoteMediator,
        pagingSourceFactory = { dao.getAll() }
    )

    @ExperimentalPagingApi
    @Provides
    @Singleton
    fun provideUserJobsPager(
        dao: JobDao, jobRemoteMediator: JobRemoteMediator
    ): Pager<Int, JobEntity> = Pager(
        config = PagingConfig(pageSize = 30, enablePlaceholders = false),
        remoteMediator = jobRemoteMediator,
        pagingSourceFactory = { dao.getAll() }
    )

//    @ExperimentalPagingApi
//    @Provides
//    @Singleton
//    fun provideUserWallPager(
//        dao: UserWallDao, userWallRemoteMediator: UserWallRemoteMediator
//    ): Pager<Int, UserWallEntity> = Pager(
//        config = PagingConfig( pageSize = 30, enablePlaceholders = false),
//        remoteMediator = userWallRemoteMediator,
//        pagingSourceFactory = { dao.getAll() }
//    )
}