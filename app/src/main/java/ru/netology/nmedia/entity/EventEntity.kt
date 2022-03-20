package ru.netology.nework.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Entity
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Event
import java.time.Instant

@Entity
data class EventEntity(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String?,
    val published: String?,
    @Embedded
    val coords: CoordinatesEmbeddable? = null,
    val type: EventType,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean,
    val speakerIds: Set<Long> = emptySet(),
    val participantsIds: MutableSet<Long> = mutableSetOf(),
    val participatedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val link: String? = null,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDto(myId: Long) = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        datetime = Instant.parse(datetime),
        published = Instant.parse(published),
        coords = coords?.toCoordinates(),
        type = type,
        likeOwnerIds = likeOwnerIds,
        likedByMe = likeOwnerIds.contains(myId),
        speakerIds = speakerIds,
        participantsIds = participantsIds,
        participatedByMe = participantsIds.contains(myId),
        link = link,
        //attachment TODO
    )

    companion object {
        fun fromDto(dto: Event) = EventEntity(
            id = dto.id,
            authorId = dto.authorId,
            author = dto.author,
            authorAvatar = dto.authorAvatar,
            content = dto.content,
            datetime = dto.datetime.toString(),
            published = dto.published.toString(),
            coords = dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
            type = dto.type,
            likeOwnerIds = dto.likeOwnerIds.toMutableSet(),
            likedByMe = dto.likedByMe,
            speakerIds = dto.speakerIds.toMutableSet(),
            participantsIds = dto.participantsIds.toMutableSet(),
            participatedByMe = dto.participatedByMe,
            link = dto.link,
            //AttachmentEmbeddable.fromDto(dto.attachment),
        )
    }
}
