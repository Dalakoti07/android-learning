package com.dalakoti07.android.dbcache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dalakoti07.android.modelclass.RepoModel

@Database(
        entities = [RepoModel::class],
        version = 1,
        exportSchema = false
)
abstract class RepoDb : RoomDatabase() {

    abstract fun reposDao(): RepoDao

    companion object {

        @Volatile
        private var INSTANCE: RepoDb? = null

        fun getInstance(context: Context): RepoDb =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RepoDb::class.java, "Github.db")
                        .build()
    }
}