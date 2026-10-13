package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuizScoreRecord
import com.example.data.local.StudyNote
import com.example.data.local.StudyPlanItem
import com.example.data.model.CodeHelpResult
import com.example.data.model.QuizQuestion
import com.example.data.model.StudySchedulePlan
import com.example.data.model.VivaQuestion
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyBuddyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudyRepository(application)

    // Current navigation route
    private val _currentRoute = MutableStateFlow("dashboard")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    // ==========================================
    // API & Settings State
    // ==========================================
    private val _customApiKey = MutableStateFlow(repository.getCustomApiKey())
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _isGeminiConfigured = MutableStateFlow(repository.isGeminiConfigured())
    val isGeminiConfigured: StateFlow<Boolean> = _isGeminiConfigured.asStateFlow()

    fun updateApiKey(key: String) {
        repository.setCustomApiKey(key)
        _customApiKey.value = key
        _isGeminiConfigured.value = repository.isGeminiConfigured()
    }

    // ==========================================
    // 1. Topic Explainer State
    // ==========================================
    val explainerTopic = MutableStateFlow("")
    val explainerContext = MutableStateFlow("Computer Science")
    private val _explainerLoading = MutableStateFlow(false)
    val explainerLoading: StateFlow<Boolean> = _explainerLoading.asStateFlow()
    private val _explainerResult = MutableStateFlow<String?>(null)
    val explainerResult: StateFlow<String?> = _explainerResult.asStateFlow()
    private val _explainerError = MutableStateFlow<String?>(null)
    val explainerError: StateFlow<String?> = _explainerError.asStateFlow()

    fun explainTopic() {
        val topic = explainerTopic.value.trim()
        if (topic.isBlank()) {
            _explainerError.value = "Please enter an academic topic to explain."
            return
        }
        _explainerError.value = null
        _explainerLoading.value = true
        _explainerResult.value = null

        viewModelScope.launch {
            val result = repository.explainTopic(topic, explainerContext.value)
            _explainerLoading.value = false
            result.onSuccess {
                _explainerResult.value = it
            }.onFailure {
                _explainerError.value = it.message ?: "Failed to explain topic. Please try again."
            }
        }
    }

    // ==========================================
    // 2. Smart Notes State
    // ==========================================
    val notesSubject = MutableStateFlow("")
    val notesTopic = MutableStateFlow("")
    val notesType = MutableStateFlow("Detailed") // "Detailed" or "Revision"
    private val _notesLoading = MutableStateFlow(false)
    val notesLoading: StateFlow<Boolean> = _notesLoading.asStateFlow()
    private val _notesResult = MutableStateFlow<String?>(null)
    val notesResult: StateFlow<String?> = _notesResult.asStateFlow()
    private val _notesError = MutableStateFlow<String?>(null)
    val notesError: StateFlow<String?> = _notesError.asStateFlow()
    private val _noteSavedSuccess = MutableStateFlow(false)
    val noteSavedSuccess: StateFlow<Boolean> = _noteSavedSuccess.asStateFlow()

    val savedNotes: StateFlow<List<StudyNote>> = repository.getAllSavedNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun generateSmartNotes() {
        val subject = notesSubject.value.trim()
        val topic = notesTopic.value.trim()
        if (subject.isBlank()) {
            _notesError.value = "Please enter the subject name."
            return
        }
        if (topic.isBlank()) {
            _notesError.value = "Please enter the topic to generate notes for."
            return
        }
        _notesError.value = null
        _notesLoading.value = true
        _notesResult.value = null
        _noteSavedSuccess.value = false

        viewModelScope.launch {
            val result = repository.generateSmartNotes(subject, topic, notesType.value)
            _notesLoading.value = false
            result.onSuccess {
                _notesResult.value = it
            }.onFailure {
                _notesError.value = it.message ?: "Could not generate notes. Please check inputs."
            }
        }
    }

    fun saveCurrentNotes() {
        val content = _notesResult.value ?: return
        val subject = notesSubject.value.ifBlank { "General" }
        val topic = notesTopic.value.ifBlank { "Study Topic" }
        viewModelScope.launch {
            repository.saveNote(subject, topic, notesType.value, content)
            _noteSavedSuccess.value = true
        }
    }

    fun deleteSavedNote(note: StudyNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // ==========================================
    // 3. Viva Questions State
    // ==========================================
    val vivaTopic = MutableStateFlow("")
    val vivaDifficulty = MutableStateFlow("All") // "All", "Beginner", "Intermediate", "Advanced"
    private val _vivaLoading = MutableStateFlow(false)
    val vivaLoading: StateFlow<Boolean> = _vivaLoading.asStateFlow()
    private val _vivaList = MutableStateFlow<List<VivaQuestion>>(emptyList())
    val vivaList: StateFlow<List<VivaQuestion>> = _vivaList.asStateFlow()
    private val _revealedVivaIds = MutableStateFlow<Set<Int>>(emptySet())
    val revealedVivaIds: StateFlow<Set<Int>> = _revealedVivaIds.asStateFlow()
    private val _masteredVivaIds = MutableStateFlow<Set<Int>>(emptySet())
    val masteredVivaIds: StateFlow<Set<Int>> = _masteredVivaIds.asStateFlow()
    private val _vivaError = MutableStateFlow<String?>(null)
    val vivaError: StateFlow<String?> = _vivaError.asStateFlow()

    fun generateViva() {
        val topic = vivaTopic.value.trim()
        if (topic.isBlank()) {
            _vivaError.value = "Please enter a topic for viva questions."
            return
        }
        _vivaError.value = null
        _vivaLoading.value = true
        _vivaList.value = emptyList()
        _revealedVivaIds.value = emptySet()
        _masteredVivaIds.value = emptySet()

        viewModelScope.launch {
            val result = repository.generateVivaQuestions(topic, vivaDifficulty.value)
            _vivaLoading.value = false
            result.onSuccess {
                _vivaList.value = it
            }.onFailure {
                _vivaError.value = it.message ?: "Failed to generate viva questions."
            }
        }
    }

    fun toggleRevealViva(id: Int) {
        val current = _revealedVivaIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _revealedVivaIds.value = current
    }

    fun toggleMasteredViva(id: Int) {
        val current = _masteredVivaIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _masteredVivaIds.value = current
    }

    // ==========================================
    // 4. Quiz Generator State
    // ==========================================
    val quizTopic = MutableStateFlow("")
    val quizCount = MutableStateFlow(5)
    private val _quizLoading = MutableStateFlow(false)
    val quizLoading: StateFlow<Boolean> = _quizLoading.asStateFlow()
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()
    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()
    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()
    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()
    private val _quizError = MutableStateFlow<String?>(null)
    val quizError: StateFlow<String?> = _quizError.asStateFlow()

    val recentQuizScores: StateFlow<List<QuizScoreRecord>> = repository.getRecentQuizScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun generateQuiz() {
        val topic = quizTopic.value.trim()
        if (topic.isBlank()) {
            _quizError.value = "Please enter a topic or subject for the quiz."
            return
        }
        _quizError.value = null
        _quizLoading.value = true
        _quizQuestions.value = emptyList()
        _selectedAnswers.value = emptyMap()
        _quizSubmitted.value = false
        _quizScore.value = 0

        viewModelScope.launch {
            val result = repository.generateQuiz(topic, quizCount.value)
            _quizLoading.value = false
            result.onSuccess {
                _quizQuestions.value = it
            }.onFailure {
                _quizError.value = it.message ?: "Failed to generate quiz."
            }
        }
    }

    fun selectQuizAnswer(questionId: Int, optionIndex: Int) {
        if (_quizSubmitted.value) return // Locked after submit
        val map = _selectedAnswers.value.toMutableMap()
        map[questionId] = optionIndex
        _selectedAnswers.value = map
    }

    fun submitQuiz() {
        val questions = _quizQuestions.value
        if (questions.isEmpty()) return
        var correctCount = 0
        val answers = _selectedAnswers.value
        for (q in questions) {
            val selected = answers[q.id]
            if (selected == q.correctIndex) {
                correctCount++
            }
        }
        _quizScore.value = correctCount
        _quizSubmitted.value = true

        viewModelScope.launch {
            repository.recordQuizScore(quizTopic.value.ifBlank { "Quiz" }, correctCount, questions.size)
        }
    }

    fun retakeQuiz() {
        _selectedAnswers.value = emptyMap()
        _quizSubmitted.value = false
        _quizScore.value = 0
    }

    // ==========================================
    // 5. Programming Assistant State
    // ==========================================
    val codeLanguage = MutableStateFlow("Python") // "Python", "C", "Java", "SQL"
    val codeMode = MutableStateFlow("Concept") // "Concept", "CodeLineByLine", "Debugger"
    val codeQuery = MutableStateFlow("")
    val codeSnippet = MutableStateFlow("")
    private val _codeLoading = MutableStateFlow(false)
    val codeLoading: StateFlow<Boolean> = _codeLoading.asStateFlow()
    private val _codeResult = MutableStateFlow<CodeHelpResult?>(null)
    val codeResult: StateFlow<CodeHelpResult?> = _codeResult.asStateFlow()
    private val _codeError = MutableStateFlow<String?>(null)
    val codeError: StateFlow<String?> = _codeError.asStateFlow()

    fun getProgrammingHelp() {
        val query = codeQuery.value.trim()
        val snippet = codeSnippet.value.trim()
        val mode = codeMode.value

        if (mode == "Debugger" && query.isBlank() && snippet.isBlank()) {
            _codeError.value = "Please paste your code snippet and the error message you received."
            return
        } else if (mode != "Debugger" && query.isBlank()) {
            _codeError.value = "Please describe the programming concept or program you want created."
            return
        }

        _codeError.value = null
        _codeLoading.value = true
        _codeResult.value = null

        viewModelScope.launch {
            val result = repository.getProgrammingHelp(
                language = codeLanguage.value,
                mode = mode,
                query = query,
                codeSnippet = snippet
            )
            _codeLoading.value = false
            result.onSuccess {
                _codeResult.value = it
            }.onFailure {
                _codeError.value = it.message ?: "Failed to generate programming assistance."
            }
        }
    }

    // ==========================================
    // 6. Study Planner State
    // ==========================================
    val planSubject = MutableStateFlow("")
    val planTopics = MutableStateFlow("")
    val planExamDate = MutableStateFlow("")
    val planDailyHours = MutableStateFlow(3)
    val planPriority = MutableStateFlow("High") // "High", "Medium", "Low"
    private val _planLoading = MutableStateFlow(false)
    val planLoading: StateFlow<Boolean> = _planLoading.asStateFlow()
    private val _generatedPlan = MutableStateFlow<StudySchedulePlan?>(null)
    val generatedPlan: StateFlow<StudySchedulePlan?> = _generatedPlan.asStateFlow()
    private val _planError = MutableStateFlow<String?>(null)
    val planError: StateFlow<String?> = _planError.asStateFlow()

    val savedPlans: StateFlow<List<StudyPlanItem>> = repository.getAllStudyPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun generateStudyPlan() {
        val subject = planSubject.value.trim()
        val topics = planTopics.value.trim()
        val examDate = planExamDate.value.trim()
        if (subject.isBlank()) {
            _planError.value = "Please enter the subject name."
            return
        }
        if (topics.isBlank()) {
            _planError.value = "Please enter the topics to prepare."
            return
        }
        if (examDate.isBlank()) {
            _planError.value = "Please specify an exam date or target deadline (e.g., Nov 20, 2026)."
            return
        }

        _planError.value = null
        _planLoading.value = true
        _generatedPlan.value = null

        viewModelScope.launch {
            val result = repository.generateStudyPlan(
                subject = subject,
                topics = topics,
                examDate = examDate,
                hoursPerDay = planDailyHours.value
            )
            _planLoading.value = false
            result.onSuccess { plan ->
                _generatedPlan.value = plan
                // Automatically save as an active study plan item in Room
                val milestoneSummary = plan.milestones.joinToString("\n") { m ->
                    "• ${m.dayOrPeriod}: ${m.target}"
                }
                repository.addStudyPlanItem(
                    subject = subject,
                    topic = topics,
                    examDate = examDate,
                    priority = planPriority.value,
                    suggestedMilestones = milestoneSummary
                )
            }.onFailure {
                _planError.value = it.message ?: "Failed to generate study timetable."
            }
        }
    }

    fun togglePlanItem(item: StudyPlanItem) {
        viewModelScope.launch {
            repository.togglePlanItemCompletion(item)
        }
    }

    fun deletePlanItem(item: StudyPlanItem) {
        viewModelScope.launch {
            repository.deletePlanItem(item)
        }
    }
}
