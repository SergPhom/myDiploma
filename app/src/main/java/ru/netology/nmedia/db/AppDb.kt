package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.dao.UserDao
import ru.netology.nmedia.entity.ListConverter
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity


@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, UserEntity::class, EventEntity::class],  version = 5, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract  class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    companion object{
        fun buildDatabase(context: Context): AppDb{
            lateinit var db: AppDb
            try {
                db = Room.databaseBuilder(context, AppDb::class.java, "app.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }catch (t: Throwable){
                println("DB construction error is $t")
            }
            println("DB construction passed. result is - $db")
            return db
        }
    }
}
