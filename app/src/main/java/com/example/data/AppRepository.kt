package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val userStatsFlow: Flow<UserStats?> = appDao.getUserStatsFlow()
    val allLessonsProgress: Flow<List<LessonProgress>> = appDao.getAllLessonsProgressFlow()
    val allVocabulary: Flow<List<VocabularyWord>> = appDao.getAllVocabularyFlow()
    val allJournalEntries: Flow<List<JournalEntry>> = appDao.getAllJournalEntriesFlow()
    val allChatMessages: Flow<List<ChatMessage>> = appDao.getAllChatMessagesFlow()

    suspend fun getUserStatsDirect(): UserStats? {
        return appDao.getUserStatsDirect()
    }

    suspend fun saveUserStats(stats: UserStats) {
        appDao.insertOrUpdateStats(stats)
    }

    suspend fun updateLessonProgress(lessonId: String, status: String, notes: String = "") {
        appDao.updateLessonProgress(LessonProgress(lessonId, status, notes))
    }

    suspend fun getLessonProgress(lessonId: String): LessonProgress? {
        return appDao.getLessonProgress(lessonId)
    }

    suspend fun saveVocabulary(word: VocabularyWord) {
        appDao.insertOrUpdateVocab(word)
    }

    suspend fun deleteVocabulary(word: VocabularyWord) {
        appDao.deleteVocab(word)
    }

    suspend fun saveJournalEntry(entry: JournalEntry) {
        appDao.insertOrUpdateJournal(entry)
    }

    suspend fun deleteJournalEntry(entry: JournalEntry) {
        appDao.deleteJournal(entry)
    }

    suspend fun saveChatMessage(role: String, content: String) {
        appDao.insertChatMessage(ChatMessage(role = role, content = content))
    }

    suspend fun clearChatHistory() {
        appDao.clearChatMessages()
    }
}
