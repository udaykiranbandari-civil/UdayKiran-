package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.MainViewModel

@Composable
fun CoachAiTab(viewModel: MainViewModel) {
    var subTabSelect by remember { mutableStateOf(0) } // 0: AI Chat Tutor, 1: Voice Lab, 2: Goethe B1 Exam

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtab row
        TabRow(selectedTabIndex = subTabSelect) {
            Tab(selected = subTabSelect == 0, onClick = { subTabSelect = 0 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Psychology, contentDescription = "Tutor")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI-Tutor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = subTabSelect == 1, onClick = { subTabSelect = 1 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.KeyboardVoice, contentDescription = "Speaking")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Speaking Lab", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = subTabSelect == 2, onClick = { subTabSelect = 2 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.WorkspacePremium, contentDescription = "Exam")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Goethe B1 Mock", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (subTabSelect) {
                0 -> ChatTutorSection(viewModel)
                1 -> VoiceLabSection(viewModel)
                2 -> GoetheMockSection(viewModel)
            }
        }
    }
}

// ==================== AI CHAT TUTOR SYSTEM ====================
@Composable
fun ChatTutorSection(viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val activePersona by viewModel.chatPersona.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val chatBoardScroll = rememberScrollState()

    // Smooth scroll chat to bottom when message arrives
    LaunchedEffect(messages.size, isAiLoading) {
        chatBoardScroll.animateScrollTo(chatBoardScroll.maxValue)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Persona selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Coach Persona:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row {
                listOf("Teacher" to "Herr Stein 👨‍🏫", "Nico" to "Nico 🎒", "Examiner" to "Goethe-Auditor 🎙️").forEach { item ->
                    FilterChip(
                        selected = activePersona == item.first,
                        onClick = { viewModel.changePersona(item.first) },
                        label = { Text(item.second, fontSize = 11.sp) },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        // Messages board
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(chatBoardScroll)
                .padding(16.dp)
        ) {
            messages.forEach { msg ->
                val isMe = msg.role == "user"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = if (!isMe) BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant) else null,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Rich Markdown or Plain styling
                            Text(
                                text = msg.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (isAiLoading) {
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "Coach is thinking... ✍️",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Suggested Prompts Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Conjugate standard modal verbs" to "Conjugate the modal verb 'müssen'",
                "Explain Relative Clauses" to "Can you explain relative clauses with accusative and dative nouns?",
                "Simulate a WG Interview" to "Nico, simulate a flatshare interview with me in German!",
                "Check error corrections" to "Correct my sentence: Ich gehen heute zu der Universität."
            ).forEach { item ->
                SuggestionChip(
                    onClick = { textInput = item.second },
                    label = { Text(item.first, fontSize = 10.sp) }
                )
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Ask anything to Coach...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (textInput.isNotEmpty()) {
                        IconButton(onClick = { textInput = "" }) { Icon(Icons.Filled.Clear, "Clear") }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendTutorMessage(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("ai_chat_send_button")
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

// ==================== SPEAKING PRO-LAB ====================
@Composable
fun VoiceLabSection(viewModel: MainViewModel) {
    val report by viewModel.speakingReport.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()

    var targetToRead by remember { mutableStateOf("Ich möchte mich für den deutschen Studiengang immatrikulieren.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔊 TARGET GERMAN EXERCISE SENTENCE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(targetToRead, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Select target benchmark phrase:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf(
                        "Immatrikulieren" to "Ich möchte mich für den deutschen Studiengang immatrikulieren.",
                        "Wohnungssuche" to "Ich suche eine helle WG-Wohnung mit netten Mitbewohnern.",
                        "B1 Exam Speaking" to "Meiner Meinung nach ist das deutsche Universitätssystem sehr exzellent."
                    ).forEach { pair ->
                        FilterChip(
                            selected = targetToRead == pair.second,
                            onClick = { targetToRead = pair.second; viewModel.clearVoiceReport() },
                            label = { Text(pair.first, fontSize = 11.sp) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Recording Pulsator Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (isRecording) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
                .clickable { viewModel.simulateVoiceRecording(targetToRead) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                contentDescription = "Mic Recorder",
                tint = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isRecording) "🗣️ Aufzeichnung läuft... Sprich jetzt!" else "Tippe auf das Mikrofon, um zu sprechen",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Response review Report
        if (isAiLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("KI bewertet deine Aussprache...", style = MaterialTheme.typography.bodySmall)
        }

        if (report != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎯 PRONUNCIATION GRADE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(8.dp)
                        ) {
                            Text("Fluency Match", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = report!!,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.clearVoiceReport() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Verstanden - Nochmal versuchen")
                    }
                }
            }
        }
    }
}

// ==================== GOETHE B1 MOCK EXAM ====================
@Composable
fun GoetheMockSection(viewModel: MainViewModel) {
    val activeExamId by viewModel.activeMockExamId.collectAsStateWithLifecycle()
    val answers by viewModel.selectedExamAnswers.collectAsStateWithLifecycle()
    val feedback by viewModel.examFeedback.collectAsStateWithLifecycle()
    val score by viewModel.examScore.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()

    val currentStats by viewModel.userStats.collectAsStateWithLifecycle()

    val scroll = rememberScrollState()

    if (activeExamId != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.closeExam() }) { Icon(Icons.Filled.ArrowBack, "Back") }
                Text("Goethe B1 Mock-Prüfung", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timed alert
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = "!", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("MOCK SIMULATOR ACTIVE: Complete 3 Reading blocks. Results will determine predicted Goethe graduation certificate readiness.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Reading material Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📖 LESETEXT (Reading Block 1)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Viele Studenten in Deutschland suchen nach bezahlbaren Zimmern. Ein WG-Zimmer bietet oft die beste und günstigste Möglichkeit zu wohnen. Allerdings teilen sich alle WG-Bewohner die Gemeinschaftsräume wie Bad und Küche.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multiple choice Questions
            val questions = listOf(
                "Q1: Was ist laut Text der Hauptvorteil eines WG-Zimmers?" to listOf("A) Man lebt allein.", "B) Es ist oft die günstigste Wohnform.", "C) Es gibt keine Küche."),
                "Q2: Was teilen sich WG-Bewohner?" to listOf("A) Gemeinschaftsräume wie Küche und Bad.", "B) Nur das Schlafzimmer.", "C) Gar nichts."),
                "Q3: Für wen ist ein WG-Zimmer besonders attraktiv?" to listOf("A) Nur für Vermieter.", "B) Für reiche Bürger.", "C) Für Studenten, die eine bezahlbare Wohnung suchen.")
            )

            questions.forEachIndexed { idx, q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(q.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        q.second.forEach { opt ->
                            val isSelected = answers[idx] == opt.take(1)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectExamAnswer(idx, opt.take(1)) }
                                    .padding(12.dp)
                                ) {
                                Text(opt, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isAiLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (score != null && feedback != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "EXAM REPORT CARD",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Score: $score% / Passing mark: 60%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (score!! >= 60) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = feedback!!,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.closeExam() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Mock-Prüfung beenden")
                        }
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.submitExamAnswers() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Antworten abgeben und bewerten (+120 XP)")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = "Goethe B1 Mock Exams", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Goethe-Institut B1-Prüfungssimulator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Test Uday's real-time readiness against strict Goethe criteria (Reading, Speaking, Writing). Earn +120 XP on mock submission trials.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GOETHE B1 CERTIFICATE DIAGNOSTIC", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estimated Exam Passing Readiness:", fontSize = 12.sp)
                    Text("${currentStats.b1Readiness}% Ready", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Goal: Reach 80% predicted score before moving to Heidelberg / Karlsruhe in August.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.startExam("B1_MOCK") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Start Reading Mock Examination")
            }
        }
    }
}
