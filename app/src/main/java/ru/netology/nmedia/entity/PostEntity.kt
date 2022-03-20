package ru.netology.nmedia.entity

import androidx.room.*
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Post
import java.sql.Types.INTEGER
import java.util.*
import java.util.stream.Collectors
import javax.persistence.ElementCollection

class ListConverter {
    @TypeConverter
    fun fromLikeList(list: List<Long>): String {
        return list.stream()
            .map { it.toString() }
            .collect(Collectors.joining(","))
    }

    @TypeConverter
    fun toLikeList(data: String): List<Long> {
        return data.split(",").map { it.toLong() }
    }
}

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val authorId: Long,
    val author: String,
    var authorAvatar: String? = "",
    val content: String,
    val published: String,
    @ColumnInfo(typeAffinity = INTEGER)
    val likedByMe: Boolean,
    var likeOwnerIds: List<Long> = emptyList(),
    val link: String? = "",
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,
    @Embedded
    val attachment: Attachment? = null,
    @Embedded
    val coords: Coordinates? = null,
) {
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
        fun fromDto(dto: Post): PostEntity{
            lateinit var entity: PostEntity
            try {
                 println("post entity work fromDTO. dto is $dto")
                 entity = PostEntity(id =dto.id,
                    authorId = dto.authorId,
                    author = dto.author,
                    authorAvatar = dto.authorAvatar ?: "",
                    content = dto.content,
                    published = dto.published,
                    likedByMe = dto.likedByMe,
                    attachment = dto.attachment,
                    coords = dto.coords,
                    link = "")
                 println("post entity work from dto. result is ${entity.attachment?.javaClass}")

            }catch (t: Throwable){
                println("post entity error is $t")
            }
            return entity
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)

