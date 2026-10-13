package com.example.data.model

data class VivaQuestion(
    val id: Int,
    val question: String,
    val answer: String,
    val difficulty: String // "Beginner", "Intermediate", "Advanced"
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val selectedAnswers: Map<Int, Int> = emptyMap(), // questionId -> optionIndex
    val isSubmitted: Boolean = false,
    val score: Int = 0
)

data class CodeHelpResult(
    val language: String,
    val summary: String,
    val code: String,
    val lineByLineExplanation: List<String>,
    val debuggingAnalysis: String? = null
)

data class StudySchedulePlan(
    val overview: String,
    val recommendedPace: String,
    val milestones: List<ScheduleMilestone>
)

data class ScheduleMilestone(
    val dayOrPeriod: String,
    val target: String,
    val tasks: List<String>,
    val tips: String
)
