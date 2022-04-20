package ru.netology.nmedia.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.repository.event.EventRepository
import ru.netology.nmedia.repository.event.EventRepositoryImpl
import ru.netology.nmedia.repository.post.PostRepository
import ru.netology.nmedia.repository.post.PostRepositoryImpl
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    fun bindJobRepository(impl: JobRepositoryImpl): JobRepository

    @Binds
    @Singleton
    fun bindUserWallRepository(impl: UserWallRepositoryImpl): UserWallRepository

}

