package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class JobRemoteKeyEntity (
    @PrimaryKey
    val type: KeyType,
    val id: Long,
){
   enum class KeyType{
       AFTER, BEFORE
   }
}
