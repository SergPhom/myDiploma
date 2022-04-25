package ru.netology.nmedia.dto

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
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