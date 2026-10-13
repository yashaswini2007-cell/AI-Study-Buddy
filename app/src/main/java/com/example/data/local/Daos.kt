package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote): Long

    @Delete
    suspend fun deleteNote(note: StudyNote)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plans ORDER BY isCompleted ASC, id DESC")
    fun getAllPlans(): Flow<List<StudyPlanItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(item: StudyPlanItem): Long

    @Update
    suspend fun updatePlan(item: StudyPlanItem)

    @Delete
    suspend fun deletePlan(item: StudyPlanItem)
}

@Dao
interface QuizScoreDao {
    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC LIMIT 30")
    fun getRecentScores(): Flow<List<QuizScoreRecord>>

    @Insert
    suspend fun insertScore(record: QuizScoreRecord): Long
}
