package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.GeminiService
import com.example.data.local.QuizScoreRecord
import com.example.data.local.StudyDatabase
import com.example.data.local.StudyNote
import com.example.data.local.StudyPlanItem
import com.example.data.model.CodeHelpResult
import com.example.data.model.QuizQuestion
import com.example.data.model.StudySchedulePlan
import com.example.data.model.VivaQuestion
import kotlinx.coroutines.flow.Flow

class StudyRepository(context: Context) {

    private val database = StudyDatabase.getInstance(context)
    private val geminiService = GeminiService()
    private val prefs: SharedPreferences = context.getSharedPreferences("ai_study_buddy_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"
    }

    // API Key Management
    fun getCustomApiKey(): String {
        return prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString(KEY_CUSTOM_API_KEY, key.trim()).apply()
    }

    fun isGeminiConfigured(): Boolean {
        return geminiService.getEffectiveApiKey(getCustomApiKey()).isNotBlank()
    }

    // 1. Topic Explainer
    suspend fun explainTopic(topic: String, academicContext: String): Result<String> {
        return geminiService.explainTopic(topic, academicContext, getCustomApiKey())
    }

    // 2. Smart Notes
    suspend fun generateSmartNotes(subject: String, topic: String, noteType: String): Result<String> {
        return geminiService.generateSmartNotes(subject, topic, noteType, getCustomApiKey())
    }

    fun getAllSavedNotes(): Flow<List<StudyNote>> {
        return database.studyNoteDao().getAllNotes()
    }

    suspend fun saveNote(subject: String, topic: String, noteType: String, content: String): Long {
        return database.studyNoteDao().insertNote(
            StudyNote(
                subject = subject,
                topic = topic,
                noteType = noteType,
                content = content
            )
        )
    }

    suspend fun deleteNote(note: StudyNote) {
        database.studyNoteDao().deleteNote(note)
    }

    // 3. Viva Questions
    suspend fun generateVivaQuestions(topic: String, difficulty: String): Result<List<VivaQuestion>> {
        return geminiService.generateVivaQuestions(topic, difficulty, getCustomApiKey())
    }

    // 4. Quiz Generator
    suspend fun generateQuiz(topic: String, questionCount: Int): Result<List<QuizQuestion>> {
        return geminiService.generateQuiz(topic, questionCount, getCustomApiKey())
    }

    fun getRecentQuizScores(): Flow<List<QuizScoreRecord>> {
        return database.quizScoreDao().getRecentScores()
    }

    suspend fun recordQuizScore(topic: String, score: Int, totalQuestions: Int) {
        database.quizScoreDao().insertScore(
            QuizScoreRecord(
                topic = topic,
                score = score,
                totalQuestions = totalQuestions
            )
        )
    }

    // 5. Programming Assistant
    suspend fun getProgrammingHelp(language: String, mode: String, query: String, codeSnippet: String): Result<CodeHelpResult> {
        return geminiService.getProgrammingHelp(language, mode, query, codeSnippet, getCustomApiKey())
    }

    // 6. Study Planner
    suspend fun generateStudyPlan(subject: String, topics: String, examDate: String, hoursPerDay: Int): Result<StudySchedulePlan> {
        return geminiService.generateStudyPlan(subject, topics, examDate, hoursPerDay, getCustomApiKey())
    }

    fun getAllStudyPlans(): Flow<List<StudyPlanItem>> {
        return database.studyPlanDao().getAllPlans()
    }

    suspend fun addStudyPlanItem(subject: String, topic: String, examDate: String, priority: String, suggestedMilestones: String): Long {
        return database.studyPlanDao().insertPlan(
            StudyPlanItem(
                subject = subject,
                topic = topic,
                examDate = examDate,
                priority = priority,
                suggestedMilestones = suggestedMilestones
            )
        )
    }

    suspend fun togglePlanItemCompletion(item: StudyPlanItem) {
        database.studyPlanDao().updatePlan(item.copy(isCompleted = !item.isCompleted))
    }

    suspend fun deletePlanItem(item: StudyPlanItem) {
        database.studyPlanDao().deletePlan(item)
    }
}
