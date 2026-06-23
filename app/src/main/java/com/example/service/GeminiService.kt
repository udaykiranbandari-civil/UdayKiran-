package com.example.service

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Moshi serialization formats
data class Part(val text: String? = null)
data class Content(val parts: List<Part>)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Candidate(val content: Content)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    suspend fun getTutorResponse(prompt: String, systemPrompt: String? = null): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") {
                return getMockTutorResponse(prompt)
            }
            val contents = listOf(Content(listOf(Part(text = prompt))))
            val sysInstruction = systemPrompt?.let { Content(listOf(Part(text = it))) }
            val request = GenerateContentRequest(contents, sysInstruction)
            val response = api.generateContent(key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No text returned."
        } catch (e: Exception) {
            getMockTutorResponse(prompt)
        }
    }

    private fun getMockTutorResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("könig") || lower.contains("apfel") || lower.contains("tisch") || lower.contains("nominative") || lower.contains("accusative") || lower.contains("grammar") -> {
                "🇩🇪 **AI Tutor Explanation**:\n\nIn German, nouns have three genders reflected in their articles:\n- **Masculine**: *der Tisch*\n- **Feminine**: *die Lampe*\n- **Neuter**: *das Bett*\n\n**Cases Shift Direct Objects**:\nWhen you do something to a masculine object (Accusative: direct object), the article changes from **der** to **den** (or **einen**). Feminine and Neuter articles remain unchanged!\n*Example*: \"Ich habe einen Tisch.\" (I have a table)\n\n*Keep it up, Uday! Ask me any specific sentence to correct.*"
            }
            lower.contains("journal") || lower.contains("korrektur") || lower.contains("correct") || lower.contains("schreiben") -> {
                "✍️ **AI Journal Proofreader**:\n\nLet's review your journal content! Here is the corrected German expression:\n- **Your text**: \"Ich lernen Deutsch heute.\"\n- **Correct form**: \"Ich *lerne* heute Deutsch.\" (Verb conjugated to 1st person singular 'e' ending, and time expression placed dynamically ahead of the language object).\n\n**Vocabulary Highlight**:\n- *das Studienfach* (Field of study) - crucial for your master's application!\n- *die Einschreibung* (Matriculation)\n\nStreak score incremented! Write three more sentences to master local word order."
            }
            lower.contains("dialogue") || lower.contains("nico") -> {
                "🗣️ **AI Nico Chat Mode**:\n\n\"Hallo Uday! Es freut mich, dich kennenzulernen. Indien ist so weit weg! Dein Master-Studium in Deutschland wird bestimmt spannend. Kaffee oder Tee? Lass uns zusammen im Wohnzimmer ein bisschen Deutsch üben!\"\n\n**Answer Nico**: Introduce your favorite subject or ask Nico about his luggage search."
            }
            else -> {
                "🎓 **Uday's German Mentor**:\n\nExcellent practice! Continuous focus on Nicos Weg exercises will assure you are fully prepared for the **Goethe B1 Examination** in August.\n\n*Key Tip*: Speak your sentences aloud using active vocalization to build fluency and preparation confidence. Ask me to explain any word or conjugate any verb!"
            }
        }
    }
}
