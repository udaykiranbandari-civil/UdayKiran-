package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- USER STATS ---
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStatsDirect(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStats)

    // --- LESSON PROGRESS ---
    @Query("SELECT * FROM lesson_progress")
    fun getAllLessonsProgressFlow(): Flow<List<LessonProgress>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getLessonProgress(lessonId: String): LessonProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLessonProgress(progress: LessonProgress)

    // --- VOCABULARY ---
    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    fun getAllVocabularyFlow(): Flow<List<VocabularyWord>>

    @Query("SELECT * FROM vocabulary WHERE status = :status ORDER BY word ASC")
    fun getVocabularyByStatus(status: String): Flow<List<VocabularyWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVocab(word: VocabularyWord)

    @Delete
    suspend fun deleteVocab(word: VocabularyWord)

    // --- JOURNAL ---
    @Query("SELECT * FROM journal ORDER BY timestamp DESC")
    fun getAllJournalEntriesFlow(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateJournal(entry: JournalEntry)

    @Delete
    suspend fun deleteJournal(entry: JournalEntry)

    // --- CHAT MESSAGES ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()
}
