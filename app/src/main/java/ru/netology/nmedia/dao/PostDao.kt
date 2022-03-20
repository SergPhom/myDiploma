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


////////////////////////////////////////////////////////////////////////////        CRUD
//@Query("""
//        UPDATE PostEntity SET
//        likeOwnerIds = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
//        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
//        WHERE id = :id
//        """)
//suspend fun onLikeButtonClick(id: Long)

//    @Query(
//        """
//           UPDATE PostEntity
//           SET  shares = shares + CASE WHEN 0 THEN 1 ELSE 1 END
//           WHERE id = :id
//        """
//    )
//    suspend fun onShareButtonClick(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun onRemoveClick(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>){
        println("post dao work insert get posts $posts")
    }

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String?)

    suspend fun onSaveButtonClick(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)


}


