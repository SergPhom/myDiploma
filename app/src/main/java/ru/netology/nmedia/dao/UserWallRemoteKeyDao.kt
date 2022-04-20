package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.UserWallRemoteKeyEntity

@Dao
interface UserWallRemoteKeyDao {
    @Query("SELECT COUNT(*) == 0 FROM UserWallRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(id) FROM UserWallRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT MIN(id) FROM UserWallRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: UserWallRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<UserWallRemoteKeyEntity>)

    @Query("DELETE FROM UserWallRemoteKeyEntity")
    suspend fun removeAll()
}