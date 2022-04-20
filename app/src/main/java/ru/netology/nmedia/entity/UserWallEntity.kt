package ru.netology.nmedia.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Post
import java.sql.Types

@Entity
data class UserWallEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val authorId: Long,
    val author: String,
    var authorAvatar: String? = "",
    val content: String,
    val published: String,
    @ColumnInfo(typeAffinity = Types.INTEGER)
    val likedByMe: Boolean,
    var likeOwnerIds: List<Long> = emptyList(),
    val link: String? = "",
    val mentionIds: List<Long> = emptyList(),
    @ColumnInfo(typeAffinity = Types.INTEGER)
    val mentionedMe: Boolean = false,
    @Embedded
    val attachment: Attachment? = null,
    @Embedded
    val coords: Coordinates? = null,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDto() = Post(id = id,
        authorId = authorId,
        author = author,
        authorAvatar = if(authorAvatar != null) authorAvatar else "" ,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likeOwnerIds = likeOwnerIds,
        link = link,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        attachment = attachment,
        coords = coords)

    companion object {
        fun fromDto(dto: Post): UserWallEntity{
            lateinit var entity: UserWallEntity
            try {
                entity = UserWallEntity(id =dto.id,
                    authorId = dto.authorId,
                    author = dto.author,
                    authorAvatar = dto.authorAvatar ?: "",
                    content = dto.content,
                    published = dto.published,
                    likedByMe = dto.likedByMe,
                    likeOwnerIds = dto.likeOwnerIds,
                    attachment = dto.attachment,
                    coords = dto.coords,
                    link = "")
            }catch (t: Throwable){
                println("post entity error is $t")
            }
            println("Entity is $entity")
            return entity
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<UserWallEntity>.toDto(): List<Post> = map(UserWallEntity::toDto)
fun List<Post>.toUserWallEntity(): List<UserWallEntity> = map(UserWallEntity::fromDto)


