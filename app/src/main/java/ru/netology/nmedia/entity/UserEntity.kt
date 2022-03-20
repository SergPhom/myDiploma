package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.User


@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var login: String,
    var name: String,
    var avatar: String? = null,
) {
    fun toDto() = User (id = id,
        login = login,
        name = name,
        avatar = avatar)

    companion object {
        fun fromDto(dto: User) =
            UserEntity(id =dto.id,
                login = dto.login,
                name = dto.name,
                avatar = dto.avatar)

    }
}