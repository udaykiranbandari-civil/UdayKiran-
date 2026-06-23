package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.service.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppTab {
    HOME,
    MISSION,
    CURRICULUM,
    NOTEBOOKS,
    AI_COACH
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database.appDao())

    // --- CO-ORDINATED NAVIGATION STATE ---
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _selectedLessonId = MutableStateFlow<String?>(null)
    val selectedLessonId: StateFlow<String?> = _selectedLessonId.asStateFlow()

    private val _activeNotebookTab = MutableStateFlow(0) // 0: Vocab, 1: Grammar, 2: Germany Prep, 3: Verbs, 4: Journal
    val activeNotebookTab: StateFlow<Int> = _activeNotebookTab.asStateFlow()

    private val _selectedPrepModuleId = MutableStateFlow<String?>(null)
    val selectedPrepModuleId: StateFlow<String?> = _selectedPrepModuleId.asStateFlow()

    // --- AI TUTOR CHAT STATE ---
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _chatPersona = MutableStateFlow("Teacher") // "Nico", "Teacher", "Examiner"
    val chatPersona: StateFlow<String> = _chatPersona.asStateFlow()

    // --- EXAM SIMULATOR STATE ---
    private val _activeMockExamId = MutableStateFlow<String?>(null) // e.g. "B1" or nil
    val activeMockExamId: StateFlow<String?> = _activeMockExamId.asStateFlow()
    private val _selectedExamAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val selectedExamAnswers: StateFlow<Map<Int, String>> = _selectedExamAnswers.asStateFlow()
    private val _examFeedback = MutableStateFlow<String?>(null)
    val examFeedback: StateFlow<String?> = _examFeedback.asStateFlow()
    private val _examScore = MutableStateFlow<Int?>(null)
    val examScore: StateFlow<Int?> = _examScore.asStateFlow()

    // --- SPEAKING LAB RECORDING simulation state ---
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()
    private val _speakingReport = MutableStateFlow<String?>(null)
    val speakingReport: StateFlow<String?> = _speakingReport.asStateFlow()

    // --- ROOM reactive state flows ---
    val userStats: StateFlow<UserStats> = repository.userStatsFlow
        .map { it ?: UserStats(id = 1, xp = 150, level = 1, currentStreak = 4, lastStudyDate = "2026-06-22", b1Readiness = 8, currentDayIndex = 1) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStats())

    val lessonsProgress: StateFlow<List<LessonProgress>> = repository.allLessonsProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vocabularyList: StateFlow<List<VocabularyWord>> = repository.allVocabulary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntry>> = repository.allJournalEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- MISSION DAY completion checkboxes (in-memory per day) ---
    private val _completedTasks = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val completedTasks: StateFlow<Map<String, Boolean>> = _completedTasks.asStateFlow()

    init {
        // Pre-populate data on startup if database is brand new
        viewModelScope.launch {
            val stats = repository.getUserStatsDirect()
            if (stats == null) {
                // Insert initial stats
                val initStats = UserStats(
                    id = 1,
                    xp = 240,
                    level = 1,
                    currentStreak = 5,
                    totalStudyMinutes = 180,
                    lastStudyDate = "2026-06-22",
                    b1Readiness = 12,
                    currentDayIndex = 1
                )
                repository.saveUserStats(initStats)

                // Pre-populate Lesson Progress state
                repository.updateLessonProgress("A1_L1", "COMPLETED", "Practiced my first self-introduction in German!")
                repository.updateLessonProgress("A1_L2", "IN_PROGRESS", "Learning household noun articles.")

                // Pre-populate some rich vocabulary words
                val vocabItems = listOf(
                    VocabularyWord(
                        word = "Tisch", article = "der", plural = "die Tische",
                        meaning = "Table", exampleSentence = "Der Tisch ist groß und modern.",
                        pronunciation = "Teesh", status = "MASTERED", intervalIndex = 2
                    ),
                    VocabularyWord(
                        word = "Bett", article = "das", plural = "die Betten",
                        meaning = "Bed", exampleSentence = "Mein Bett im Studentenheim ist sehr bequem.",
                        pronunciation = "Bet", status = "LEARNING", intervalIndex = 1
                    ),
                    VocabularyWord(
                        word = "Lampe", article = "die", plural = "die Lampen",
                        meaning = "Lamp", exampleSentence = "Die Lampe ist hell und perfekt zum Lernen und Lesen.",
                        pronunciation = "Lam-peh", status = "NEW", intervalIndex = 0
                    ),
                    Triple("Universität", "die", "University"),
                    Triple("Bewerbung", "die", "Application"),
                    Triple("Anmeldung", "die", "Registration of Address")
                )

                // Fill database vocabulary
                vocabItems.forEachIndexed { i, item ->
                    if (item is VocabularyWord) {
                        repository.saveVocabulary(item)
                    } else if (item is Triple<*, *, *>) {
                        repository.saveVocabulary(
                            VocabularyWord(
                                word = item.first as String,
                                article = item.second as String,
                                plural = "die " + item.first + "en",
                                meaning = item.third as String,
                                exampleSentence = "Wir müssen das Thema für die ${item.first} lernen.",
                                pronunciation = item.first as String,
                                status = "NEW"
                            )
                        )
                    }
                }

                // Add starter chat messages
                repository.saveChatMessage("model", "Hallo Uday! I am your AI Coach, here to assist with German nouns, grammar rules, speaking, and preparation of your upcoming journey to Germany. Let's master B1 together in 60 days!")

                // Add initial journal entry
                repository.saveJournalEntry(
                    JournalEntry(
                        dateString = "2026-06-22",
                        content = "Heute habe ich das Projekt 'Udays Weg' gestartet! Ich bin hoch motiviert, in den nächsten 60 Tagen starkes B1 Deutsch zu lernen, um mein Masterstudium in Deutschland erfolgreich zu beginnen. Die Nicos Weg Lektionen und der KI-Tutor machen das Lernen extrem effektiv.",
                        newWords = "der Studiengang (course of study), die Einschreibung (matriculation)",
                        mistakes = "Mistook der Tisch for ein Tisch (Nominative article use)",
                        sentences = "Ich lerne intensiv Deutsch.",
                        reflections = "Starting A1 Unit today! 2 hours logged."
                    )
                )
            }
        }
    }

    // --- NAVIGATION SETTERS ---
    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        _selectedLessonId.value = null
        _selectedPrepModuleId.value = null
    }

    fun selectLesson(lessonId: String?) {
        _selectedLessonId.value = lessonId
    }

    fun selectNotebookTab(index: Int) {
        _activeNotebookTab.value = index
        _selectedPrepModuleId.value = null
    }

    fun selectPrepModule(id: String?) {
        _selectedPrepModuleId.value = id
    }

    // --- USER STATS METHODS ---
    fun setDay(dayIndex: Int) {
        if (dayIndex in 1..60) {
            viewModelScope.launch {
                val stats = userStats.value
                val updated = stats.copy(currentDayIndex = dayIndex)
                repository.saveUserStats(updated)
                // Clear state for completed checkboxes
                _completedTasks.value = emptyMap()
            }
        }
    }

    fun addXp(amount: Int) {
        viewModelScope.launch {
            val stats = userStats.value
            var newXp = stats.xp + amount
            var newLevel = stats.level
            // Simple gamification leveling curve (approx 300 XP per level)
            if (newXp >= newLevel * 350) {
                newXp -= newLevel * 350
                newLevel++
            }
            val b1Progress = calculateReadinessPercentage(newLevel, newXp)
            val updated = stats.copy(xp = newXp, level = newLevel, b1Readiness = b1Progress)
            repository.saveUserStats(updated)
        }
    }

    private fun calculateReadinessPercentage(level: Int, xp: Int): Int {
        // Calculate B1 readiness dynamically based on experience: grows toward 100% as you study
        val points = (level - 1) * 350 + xp
        val pct = (points / 25).coerceIn(4, 98)
        return pct
    }

    fun logStudyTime(minutes: Int) {
        viewModelScope.launch {
            val stats = userStats.value
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val streak = if (stats.lastStudyDate != todayStr) {
                // If consecutive or first of today, keep or increment
                if (stats.lastStudyDate.isEmpty() || stats.lastStudyDate == getYesterdayDateString()) stats.currentStreak + 1 else 1
            } else {
                stats.currentStreak
            }
            val updated = stats.copy(
                totalStudyMinutes = stats.totalStudyMinutes + minutes,
                lastStudyDate = todayStr,
                currentStreak = streak
            )
            repository.saveUserStats(updated)
            addXp(minutes / 2)
        }
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // --- TASK COMPLETION ---
    fun toggleTask(taskId: String, checked: Boolean) {
        val current = _completedTasks.value.toMutableMap()
        current[taskId] = checked
        _completedTasks.value = current

        if (checked) {
            addXp(20)
            logStudyTime(10)
        }
    }

    // --- VOCABULARY METHODS ---
    fun addVocabWord(word: String, article: String, plural: String, meaning: String, sentence: String, pronunciation: String, notes: String = "") {
        viewModelScope.launch {
            val newWord = VocabularyWord(
                word = word,
                article = article,
                plural = plural,
                meaning = meaning,
                exampleSentence = sentence,
                pronunciation = pronunciation,
                notes = notes,
                status = "NEW"
            )
            repository.saveVocabulary(newWord)
            addXp(15)
        }
    }

    fun updateVocabStatus(word: VocabularyWord, nextStatus: String, updateSrs: Boolean = false) {
        viewModelScope.launch {
            var interval = word.intervalIndex
            var nextReview = word.nextReviewTimeMillis
            if (updateSrs) {
                interval = (word.intervalIndex + 1).coerceAtMost(6)
                val daysToAdd = when (interval) {
                    1 -> 1
                    2 -> 3
                    3 -> 7
                    4 -> 14
                    5 -> 30
                    6 -> 90
                    else -> 1
                }
                nextReview = System.currentTimeMillis() + (daysToAdd * 24L * 60L * 60L * 1000L)
            }
            val updated = word.copy(
                status = nextStatus,
                intervalIndex = interval,
                nextReviewTimeMillis = nextReview,
                lastReviewedAt = System.currentTimeMillis()
            )
            repository.saveVocabulary(updated)
            addXp(10)
        }
    }

    fun removeVocab(word: VocabularyWord) {
        viewModelScope.launch {
            repository.deleteVocabulary(word)
        }
    }

    // --- LESSON PROGRESS ---
    fun setLessonStatus(lessonId: String, status: String, notes: String = "") {
        viewModelScope.launch {
            repository.updateLessonProgress(lessonId, status, notes)
            if (status == "COMPLETED") {
                addXp(80)
                logStudyTime(35)
            }
        }
    }

    // --- JOURNAL METHODS ---
    fun submitJournalEntry(content: String, newWords: String, mistakes: String, sentences: String, reflections: String) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val entry = JournalEntry(
                dateString = todayStr,
                content = content,
                newWords = newWords,
                mistakes = mistakes,
                sentences = sentences,
                reflections = reflections
            )
            repository.saveJournalEntry(entry)
            addXp(50)
            logStudyTime(15)

            // Prompt AI to correct journal automatically
            _aiLoading.value = true
            val prompt = "Correct this German journal entry. Provide corrections for grammar or vocabulary, and translate terms where appropriate inside a beautiful concise format.\n\nJournal Content:\n$content\n\nSentences written:\n$sentences"
            val sysInstruction = "You are an expert Goethe B1 German Examiner and friendly tutor named Coach Nico. Correct the user's mistakes, suggest elegant synonyms, and award points for diligence."
            val response = GeminiClient.getTutorResponse(prompt, sysInstruction)
            repository.saveChatMessage("model", "📝 **AI Journal Evaluation**:\n\n$response")
            _aiLoading.value = false
        }
    }

    fun deleteJournal(entry: JournalEntry) {
        viewModelScope.launch {
            repository.deleteJournalEntry(entry)
        }
    }

    // --- AI TUTOR CONVERSATIONS ---
    fun changePersona(persona: String) {
        _chatPersona.value = persona
    }

    fun sendTutorMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.saveChatMessage("user", text)
            _aiLoading.value = true
            
            val personaPrompt = when (_chatPersona.value) {
                "Nico" -> "You are Nico, the famous protagonist of DW Nicos Weg. Speak in simple German (suitable for A1-B1 learners). Be friendly, casual, use informal address ('du'), and help Uday study German. Sometimes refer to your bag or hostel roommates."
                "Examiner" -> "You are a professional Goethe-Institut B1 examiner. You speak mostly in formal German. Keep standard scoring criteria in mind (vocabulary range, correct cases: Accusative/Dative, proper word order in auxiliary clauses). Prompt Uday with exam topics and score his output strictly but constructively."
                else -> "You are 'Coach Nico', Uday's personal German learning mentor who knows he is an engineered graduate moving to Germany. Speak in fluent German interspersed with English/Telugu translations when breaking down complex concepts. Explain grammar patterns (e.g. subordinate clauses, relative pronouns, declensions) meticulously."
            }

            val chatHistoryString = chatMessages.value.takeLast(10).joinToString("\n") { "${it.role}: ${it.content}" }
            val fullPrompt = "$chatHistoryString\nuser: $text"

            val response = GeminiClient.getTutorResponse(fullPrompt, personaPrompt)
            repository.saveChatMessage("model", response)
            _aiLoading.value = false
            addXp(10)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
            repository.saveChatMessage("model", "Conversations reset! Let's start speaking again, Uday. How can I assist you today?")
        }
    }

    // --- ACCESSIBLE LABORATORIES METHODS ---
    fun simulateVoiceRecording(sentenceToRead: String) {
        viewModelScope.launch {
            _isRecording.value = true
            kotlinx.coroutines.delay(2000) // Simulation recording timer
            _isRecording.value = false

            _aiLoading.value = true
            val prompt = "Evaluate the German pronunciation and rhythm of an indian student reading the following sentence: '$sentenceToRead'. Give a score out of 100 and friendly mechanical feedback."
            val evaluation = GeminiClient.getTutorResponse(prompt, "You are a German phonetic analyzer. Analyze sentence rhythm, vowels (especially umlauts ä, ö, ü and 'ch/sch' sounds), and confidence.")
            _speakingReport.value = evaluation
            _aiLoading.value = false
            addXp(30)
            logStudyTime(5)
        }
    }

    fun clearVoiceReport() {
        _speakingReport.value = null
    }

    // --- GOETHE B1 EXAM METHOD ---
    fun startExam(examId: String) {
        _activeMockExamId.value = examId
        _selectedExamAnswers.value = emptyMap()
        _examFeedback.value = null
        _examScore.value = null
    }

    fun selectExamAnswer(questionIndex: Int, selectedOption: String) {
        val current = _selectedExamAnswers.value.toMutableMap()
        current[questionIndex] = selectedOption
        _selectedExamAnswers.value = current
    }

    fun submitExamAnswers() {
        val answers = _selectedExamAnswers.value
        if (answers.isEmpty()) return

        viewModelScope.launch {
            _aiLoading.value = true
            
            // Calculate mock score
            var correctCount = 0
            if (answers[0] == "B") correctCount++ // Q1 keys
            if (answers[1] == "A") correctCount++ // Q2 keys
            if (answers[2] == "C") correctCount++ // Q3 keys
            
            val finalScorePercent = (correctCount * 100) / 3

            val resultsPrompt = "The student completed the Goethe B1 Reading mock portion. Student answers: Q1: ${answers[0]}, Q2: ${answers[1]}, Q3: ${answers[2]}. Correct keys are Q1: B, Q2: A, Q3: C. Present individual analysis, grammar metrics, and a diagnostic summary."
            val reviewText = GeminiClient.getTutorResponse(resultsPrompt, "You are the chief academic officer at a Goethe evaluation department. Write in high-standard professional pedagogical language.")
            
            _examScore.value = finalScorePercent
            _examFeedback.value = reviewText
            _aiLoading.value = false
            
            addXp(120)
            logStudyTime(45)
        }
    }

    fun closeExam() {
        _activeMockExamId.value = null
        _selectedExamAnswers.value = emptyMap()
        _examFeedback.value = null
        _examScore.value = null
    }
}
