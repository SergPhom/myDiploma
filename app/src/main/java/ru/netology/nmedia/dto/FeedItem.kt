package ru.netology.nmedia.dto

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.Authorities
import java.time.Instant

sealed interface FeedItem {
    abstract val id: Long
}

data class DateHeader(
    override val id: Long,
    val date: String
): FeedItem

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
): FeedItem

@Parcelize
data class Post(

    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = "",
    val content: String,
    val published: String,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,

    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,

): Parcelable, FeedItem {

    fun count(value: Long): String = when(value){
        in 1000..9999 -> "${value/1000}" +
                "${if ((value%1000L)/100L == 0L) "" else { "." + (value%1000)/100} + "K"}"
        in 10000..999999 ->  "${value/1000}K"
        in 1000000..Int.MAX_VALUE -> "${value/1000000}" +
                "${if ((value%1000000L)/100000L == 0L) "" else { "." + (value%1000000)/100000} + "M"}"
        else -> value.toString()
    }

}

@Parcelize
data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    //val authorities: Authorities?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        //TODO("authorities")
    )

    companion object : Parceler<User> {

        override fun User.write(parcel: Parcel, flags: Int) {
            parcel.writeLong(id)
            parcel.writeString(login)
            parcel.writeString(name)
            parcel.writeString(avatar)
        }

        override fun create(parcel: Parcel): User {
            return User(parcel)
        }
    }
}

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: Instant? = null,
    val published: Instant? = null,
    val coords: Coordinates? = null,
    val type: EventType,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val speakerIds: Set<Long> = emptySet(),
    val participantsIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
)

@Parcelize
data class Attachment(
    val url: String,
    val description: String? = "",
    val type: AttachmentType,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("type")
    )

    companion object : Parceler<Attachment> {

        override fun Attachment.write(parcel: Parcel, flags: Int) {
            parcel.writeString(url)
            if(description != null) parcel.writeString(description) else ""
        }

        override fun create(parcel: Parcel): Attachment {
            return Attachment(parcel)
        }
    }
}

@Parcelize
data class Coordinates(
    val lat: Double? = 0.0,
    val longitude: Double? = 0.0
) : Parcelable{
    constructor(parcel: Parcel) : this(
        lat = parcel.readDouble()!!,
        longitude = parcel.readDouble()!!
    )

    companion object : Parceler<Coordinates> {

        override fun Coordinates.write(parcel: Parcel, flags: Int) {
            if (lat != null) {
                parcel.writeDouble(lat)
            }
            if (longitude != null) {
                parcel.writeDouble(longitude)
            }
        }

        override fun create(parcel: Parcel): Coordinates {
            return Coordinates(parcel)
        }
    }
}
