package com.example.jeopardystudyapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Clue::class, ClueSettings::class, AnswerLog::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clueDao(): ClueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jeopardy_database"
                )
                    .createFromAsset("jeopardy.db")
                    // CRITICAL: This wipes the DB when schema changes (fine for dev)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}