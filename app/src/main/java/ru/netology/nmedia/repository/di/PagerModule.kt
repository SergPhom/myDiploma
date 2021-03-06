package ru.netology.nmedia.repository.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.entity.EventEntity
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.repository.event.EventRemoteMediator
import ru.netology.nmedia.repository.post.PostRemoteMediator
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
}