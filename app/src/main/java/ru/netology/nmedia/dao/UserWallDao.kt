package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.UserWallEntity

@Dao
interface UserWallDao {

    @Query("SELECT COUNT(*) == 0 FROM UserWallEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM UserWallEntity ORDER BY id DESC")
    fun getAll(): PagingSource<Int, UserWallEntity>

    @Query("SELECT * FROM UserWallEntity WHERE id = :id")
    fun getById(id: Long): Flow<UserWallEntity>

    @Query("DELETE FROM UserWallEntity")
    suspend fun removeAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: UserWallEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<UserWallEntity>)
}