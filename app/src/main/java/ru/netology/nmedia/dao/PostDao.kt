package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getById(id: Long): Flow<PostEntity>

    @Query("SELECT * FROM PostEntity WHERE id >= :id limit :size")
    fun getByIdAndSize(id: Long, size: Int): Flow<List<PostEntity>>

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun onRemoveClick(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String?)

    suspend fun onSaveButtonClick(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)


}


