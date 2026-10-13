package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val topic: String,
    val noteType: String, // "Revision" or "Detailed"
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_plans")
data class StudyPlanItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val topic: String,
    val examDate: String,
    val priority: String, // "High", "Medium", "Low"
    val isCompleted: Boolean = false,
    val suggestedMilestones: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_scores")
data class QuizScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)
