package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.toEntity
import ru.netology.nmedia.api.UsersApiService
import ru.netology.nmedia.dao.UserDao
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersApiService: UsersApiService,
    private val userDao: UserDao,
): UserRepository {

    override val data: Flow<User>
    //= UsersApiService.getAll().flow
        get() = TODO("Not yet implemented")

    override suspend fun getUsers(){
        try {
            val response = usersApiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Throwable) {
            println("Repository Error is ${e}")
            throw UnknownError
        }
    }
}