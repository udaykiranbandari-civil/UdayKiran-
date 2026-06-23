package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val totalStudyMinutes: Int = 0,
    val lastStudyDate: String = "",
    val b1Readiness: Int = 0,
    val currentDayIndex: Int = 1 // Range 1 to 60
)

@Entity(tableName = "lesson_progress")
data class LessonProgress(
    @PrimaryKey val lessonId: String, // e.g., "A1_L1"
    val status: String = "NOT_STARTED", // "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    val notes: String = ""
)

@Entity(tableName = "vocabulary")
data class VocabularyWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val article: String, // "der", "die", "das", or "-"
    val plural: String, // e.g. "die Tische"
    val meaning: String,
    val exampleSentence: String,
    val pronunciation: String,
    val notes: String = "",
    val status: String = "NEW", // "NEW", "LEARNING", "REVIEW", "MASTERED"
    val nextReviewTimeMillis: Long = System.currentTimeMillis(),
    val intervalIndex: Int = 0, // Indexes in Spaced Repetition: 1, 3, 7, 14, 30, 90 days
    val lastReviewedAt: Long = 0
)

@Entity(tableName = "journal")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // "YYYY-MM-DD"
    val content: String,
    val newWords: String = "",
    val mistakes: String = "",
    val sentences: String = "",
    val reflections: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
