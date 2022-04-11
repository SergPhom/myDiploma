package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.JobRemoteKeyEntity


@Dao
interface JobRemoteKeyDao {
    @Query("SELECT COUNT(*) == 0 FROM JobRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(id) FROM JobRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT MIN(id) FROM JobRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: JobRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<JobRemoteKeyEntity>)

    @Query("DELETE FROM JobRemoteKeyEntity")
    suspend fun removeAll()

}
