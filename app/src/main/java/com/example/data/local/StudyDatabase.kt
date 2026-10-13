package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StudyNote::class, StudyPlanItem::class, QuizScoreRecord::class],
    version = 1,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun studyNoteDao(): StudyNoteDao
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun quizScoreDao(): QuizScoreDao

    companion object {
        @Volatile
        private var INSTANCE: StudyDatabase? = null

        fun getInstance(context: Context): StudyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "ai_study_buddy.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
