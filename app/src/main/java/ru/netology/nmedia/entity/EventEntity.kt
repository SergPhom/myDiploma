package ru.netology.nework.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Event
import java.sql.Types

@Entity
data class EventEntity(
    @PrimaryKey
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String?,
    val published: String?,
    @Embedded
    val coords: Coordinates? = null,
    val eventType: EventType?,
    var likeOwnerIds: List<Long> = emptyList(),
    @ColumnInfo(typeAffinity = Types.INTEGER)
    val likedByMe: Boolean,
    val speakerIds: List<Long> = emptyList(),
    val participantsIds: List<Long> = emptyList(),
    @ColumnInfo(typeAffinity = Types.INTEGER)
    val participatedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val link: String? = null,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDto() = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        datetime = datetime ?: "",
        published = published ?: "",
        coords = coords,
        type = eventType?.name ?: "",
        likeOwnerIds = likeOwnerIds,
        likedByMe = likedByMe,
        speakerIds = speakerIds,
        participantsIds = participantsIds,
        participatedByMe = participatedByMe,
        link = link,
        attachment = attachment
    )

    companion object {
        fun fromDto(dto: Event) = EventEntity(
            id = dto.id,
            authorId = dto.authorId,
            author = dto.author,
            authorAvatar = dto.authorAvatar,
            content = dto.content,
            datetime = dto.datetime,
            published = dto.published,
            coords = dto.coords,
            eventType = dto.type?.let { EventType.valueOf(it) },
            likeOwnerIds = dto.likeOwnerIds,
            likedByMe = dto.likedByMe,
            speakerIds = dto.speakerIds,
            participantsIds = dto.participantsIds,
            participatedByMe = dto.participatedByMe,
            link = dto.link,
            attachment = dto.attachment,
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun List<EventEntity>.toDto(myId: Long): List<Event> = map { it.toDto() }

fun List<Event>.fromDto(): List<EventEntity> = map(EventEntity::fromDto)