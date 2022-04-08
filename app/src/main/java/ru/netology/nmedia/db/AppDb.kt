package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nmedia.dao.*
import ru.netology.nmedia.entity.EventRemoteKeyEntity
import ru.netology.nmedia.entity.ListConverter
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity


@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, UserEntity::class,
    EventEntity::class, EventRemoteKeyEntity::class
    //, JobEntity::class
                     ],  version = 7, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract  class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun jobDao(): JobDao
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
            return db
        }
    }
}
