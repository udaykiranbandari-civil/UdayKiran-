package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.viewmodel.*

@Composable
fun CurriculumTab(viewModel: MainViewModel) {
    val selectedId by viewModel.selectedLessonId.collectAsStateWithLifecycle()
    val progressList by viewModel.lessonsProgress.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = selectedId,
        transitionSpec = {
            if (targetState != null) {
                (slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width / 3 } + fadeOut())
            } else {
                (slideInHorizontally { width -> -width / 3 } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut())
            }
        },
        label = "LessonTransition"
    ) { activeId ->
        if (activeId != null) {
            val currLesson = Curriculum.getLessonById(activeId)
            val prog = progressList.find { it.lessonId == activeId }
            if (currLesson != null) {
                LessonDetailPage(
                    lesson = currLesson,
                    currentStatus = prog?.status ?: "NOT_STARTED",
                    onBack = { viewModel.selectLesson(null) },
                    onStatusChange = { nextStatus ->
                        viewModel.setLessonStatus(activeId, nextStatus)
                    },
                    onAddVocab = { word, article, plural, meaning, sentence, pron ->
                        viewModel.addVocabWord(word, article, plural, meaning, sentence, pron)
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lektion nicht gefunden.")
                }
            }
        } else {
            CurriculumLessonsList(viewModel, progressList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumLessonsList(viewModel: MainViewModel, progressList: List<LessonProgress>) {
    var selectedLevelTab by remember { mutableStateOf(0) } // 0: A1, 1: A2, 2: B1, 3: B2 Academic
    val levelKey = when (selectedLevelTab) {
        0 -> "A1"
        1 -> "A2"
        2 -> "B1"
        else -> "B2"
    }

    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Multi-level switcher tabs
        PrimaryTabRow(selectedTabIndex = selectedLevelTab) {
            Tab(selected = selectedLevelTab == 0, onClick = { selectedLevelTab = 0 }) {
                Text("A1-Anfänger", modifier = Modifier.padding(16.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedLevelTab == 1, onClick = { selectedLevelTab = 1 }) {
                Text("A2-Fortg.", modifier = Modifier.padding(16.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedLevelTab == 2, onClick = { selectedLevelTab = 2 }) {
                Text("B1-Prüfung", modifier = Modifier.padding(16.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedLevelTab == 3, onClick = { selectedLevelTab = 3 }) {
                Text("B2 Acad.", modifier = Modifier.padding(16.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (selectedLevelTab == 3 && stats.level < 4) {
            // LOCKED SCREEN FOR B2 EXPLAINED
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "🔒",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Level B2 Goethe Academy Locked",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "To unlock premium scholarly university lessons in Phase 2, advance Uday's statistics to Level 4 (or Day 60 B1 syllabus) by completing standard A1/A2/B1 modules, writing journals daily, and logging study minutes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Intro Label
                Text(
                    text = if (selectedLevelTab == 3) "🔥 PHASE 2: B2 ACADEMY MODULES" else "$levelKey Lehrplan (Nicos Weg Core Curriculum)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render Lessons matching selected level key
                val listToRender = if (selectedLevelTab == 3) {
                    // Populate some dummy B2 advanced lessons on completion
                    listOf(
                        CurriculumLesson(
                            id = "B2_L1",
                            level = "B2",
                            number = 1,
                            title = "Wissenschaft und Forschung an der Uni",
                            description = "Learn academic structures and research communication in engineering colleges.",
                            objectives = listOf("Formulate hypothesis", "Discuss technological advances"),
                            vocabulary = emptyList(),
                            grammarNotes = "Passive Voice Substitutes (sein + zu, -bar, -lich)",
                            importantSentences = emptyList(),
                            dialogues = emptyList(),
                            importantVerbs = listOf("forschen", "entwickeln"),
                            speakingTask = "",
                            writingTask = "",
                            exercises = emptyList()
                        ),
                        CurriculumLesson(
                            id = "B2_L2",
                            level = "B2",
                            number = 2,
                            title = "Akademisches Debattieren",
                            description = "Debate and argue on public issues confidently in Germany.",
                            objectives = listOf("Express formal arguments", "Handle counter opinions"),
                            vocabulary = emptyList(),
                            grammarNotes = "Subjunctive II in polite professional formulations",
                            importantSentences = emptyList(),
                            dialogues = emptyList(),
                            importantVerbs = listOf("argumentieren", "meinen"),
                            speakingTask = "",
                            writingTask = "",
                            exercises = emptyList()
                        )
                    )
                } else {
                    Curriculum.lessons.filter { it.level == levelKey }
                }

                if (listToRender.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.MenuBook, contentDescription = "Syllabus", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Additional Nicos Weg $levelKey units will occur as we advance Uday's study day calendars. Complete your active mission today!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    listToRender.forEach { l ->
                        val lessonProg = progressList.find { it.lessonId == l.id }
                        val status = lessonProg?.status ?: "NOT_STARTED"
                        
                        LessonRowCard(
                            lesson = l,
                            status = status,
                            onClick = { viewModel.selectLesson(l.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonRowCard(
    lesson: CurriculumLesson,
    status: String,
    onClick: () -> Unit
) {
    val statusColor = when (status) {
        "COMPLETED" -> MaterialTheme.colorScheme.tertiary
        "IN_PROGRESS" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }

    val statusLabel = when (status) {
        "COMPLETED" -> "Abgeschlossen"
        "IN_PROGRESS" -> "In Bearbeitung"
        else -> "Nicht begonnen"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
            .testTag("lesson_card_${lesson.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${lesson.number}",
                    fontWeight = FontWeight.Black,
                    color = statusColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2
                )

                // Row with status pill
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun LessonDetailPage(
    lesson: CurriculumLesson,
    currentStatus: String,
    onBack: () -> Unit,
    onStatusChange: (String) -> Unit,
    onAddVocab: (String, String, String, String, String, String) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedQuizIndex by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) } // QuestionIndex to selectedOptionIndex
    var feedbackText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showVocabAddedNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Core Lesson Detail Sticky Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Lektion ${lesson.number} • ${lesson.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // OBJECTIVES
                Text(
                    text = "LERNZIELE (Objectives)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        lesson.objectives.forEach { obj ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("🎯", modifier = Modifier.padding(end = 8.dp))
                                Text(text = obj, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // DIALOGUES
                if (lesson.dialogues.isNotEmpty()) {
                    Text(
                        text = "DIALOG - NICOS WEG",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            lesson.dialogues.forEach { speak ->
                                val alignRight = speak.first == "Uday"
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalAlignment = if (alignRight) Alignment.End else Alignment.Start
                                ) {
                                    Text(
                                        text = speak.first,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (alignRight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp,
                                                    bottomStart = if (alignRight) 12.dp else 0.dp,
                                                    bottomEnd = if (alignRight) 0.dp else 12.dp
                                                )
                                            )
                                            .background(
                                                if (alignRight) MaterialTheme.colorScheme.secondaryContainer
                                                else MaterialTheme.colorScheme.surface
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Text(text = speak.second, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                // GRAMMAR SECTION
                Text(
                    text = "GRAMMATIK-TIPS (Grammar notes)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = lesson.grammarNotes,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp)
                        )
                    }
                }

                // VOCABULARY WORD LIST
                if (lesson.vocabulary.isNotEmpty()) {
                    Text(
                        text = "DEUTSCHE WORTSCHATZ (Lesson Words)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    lesson.vocabulary.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Article badge color
                                    val badgeColor = when (item.second) {
                                        "der" -> Color(0xFF1565C0)
                                        "die" -> Color(0xFFC2185B)
                                        "das" -> Color(0xFF2E7D32)
                                        else -> Color.Gray
                                    }
                                    if (item.second != "-") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(badgeColor)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                item.second,
                                                fontSize = 10.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Column {
                                        Text(
                                            text = item.first,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = item.third,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Row {
                                    // Save Button
                                    IconButton(
                                        onClick = {
                                            val plur = if (item.second == "der") "die " + item.first + "e" else "die " + item.first + "en"
                                            onAddVocab(item.first, item.second, plur, item.third, "Das ist wichtig für ${item.first}.", "pronunciation_string")
                                            showVocabAddedNotice = item.first
                                        },
                                        modifier = Modifier.testTag("save_vocab_${item.first}")
                                    ) {
                                        Icon(Icons.Filled.BookmarkBorder, contentDescription = "Save word to Spaced Repetition", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    // Listening pronunciation simulation
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "Audio Pronounce", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // QUIZ EXERCISES
                if (lesson.exercises.isNotEmpty()) {
                    Text(
                        text = "LEKTIONSQUIST (Interactive Quiz)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    lesson.exercises.forEachIndexed { qIdx, exercise ->
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
                                Text(
                                    text = "Quiz: ${exercise.first}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val selectedAnsIndex = selectedQuizIndex[qIdx]
                                exercise.third.forEachIndexed { optIdx, option ->
                                    val isOptionSelected = selectedAnsIndex == optIdx
                                    val isCorrect = option.lowercase() == exercise.second.lowercase()
                                    
                                    val surfaceColor = when {
                                        isOptionSelected && isCorrect -> Color(0xFFE8F5E9)
                                        isOptionSelected && !isCorrect -> Color(0xFFFFEBEE)
                                        else -> MaterialTheme.colorScheme.surface
                                    }

                                    val textColor = when {
                                        isOptionSelected && isCorrect -> Color(0xFF2E7D32)
                                        isOptionSelected && !isCorrect -> Color(0xFFC62828)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(surfaceColor)
                                            .border(
                                                width = 1.dp,
                                                color = if (isOptionSelected) textColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                val nextMap = selectedQuizIndex.toMutableMap()
                                                nextMap[qIdx] = optIdx
                                                selectedQuizIndex = nextMap
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Text(text = option, style = MaterialTheme.typography.bodySmall, color = textColor)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // SUBMIT OR STATUS ACTION FOOTER
                Text(
                    text = "DEIN STATUS ZU DIESEM UNTERRICHT",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStatusChange("COMPLETED") },
                        enabled = currentStatus != "COMPLETED",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("complete_lesson_button")
                    ) {
                        Text("Als Abgeschlossen markieren (+80 XP)")
                    }

                    if (currentStatus != "IN_PROGRESS" && currentStatus != "COMPLETED") {
                        Button(
                            onClick = { onStatusChange("IN_PROGRESS") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("In Bearbeitung markieren")
                        }
                    }
                }
            }
        }
    }

    if (showVocabAddedNotice != null) {
        AlertDialog(
            onDismissRequest = { showVocabAddedNotice = null },
            title = { Text("Wortschatz gespeichert! 💾") },
            text = { Text("Das Wort '${showVocabAddedNotice}' wurde sicher in dein Vokabelheft mit Spaced Repetition (SRS) gespeichert. Lerne es in der Flashcards-Box!") },
            confirmButton = {
                TextButton(onClick = { showVocabAddedNotice = null }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
