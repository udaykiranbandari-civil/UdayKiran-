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
fun NotebooksTab(viewModel: MainViewModel) {
    val activeTab by viewModel.activeNotebookTab.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable Subtab selectors
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            edgePadding = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = activeTab == 0, onClick = { viewModel.selectNotebookTab(0) }) {
                Text("Vokabeln (Vocabulary)", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 1, onClick = { viewModel.selectNotebookTab(1) }) {
                Text("Grammatik (Grammar Academy)", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 2, onClick = { viewModel.selectNotebookTab(2) }) {
                Text("Pre-Germany Mode", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 3, onClick = { viewModel.selectNotebookTab(3) }) {
                Text("Verbtraining (Verbs)", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 4, onClick = { viewModel.selectNotebookTab(4) }) {
                Text("Tagebuch (Journal)", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                0 -> VocabularySubtab(viewModel)
                1 -> GrammarSubtab()
                2 -> GermanyPrepSubtab(viewModel)
                3 -> VerbTrainerSubtab()
                4 -> JournalSubtab(viewModel)
            }
        }
    }
}

// ==================== 0. VOCABULARY SUBTAB ====================
@Composable
fun VocabularySubtab(viewModel: MainViewModel) {
    val vocabList by viewModel.vocabularyList.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var activeSrsMode by remember { mutableStateOf(false) }

    val filteredList = remember(searchQuery, vocabList) {
        if (searchQuery.isBlank()) vocabList
        else vocabList.filter {
            it.word.contains(searchQuery, ignoreCase = true) ||
                    it.meaning.contains(searchQuery, ignoreCase = true)
        }
    }

    if (activeSrsMode) {
        // SRS FLASHCARDS SECTOR
        val srsQueue = remember(vocabList) {
            vocabList.filter { it.status != "MASTERED" || it.nextReviewTimeMillis <= System.currentTimeMillis() }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (srsQueue.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.WorkspacePremium, contentDescription = "Done", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(64.dp))
                    Text("Super Arbeit! 🎉", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                    Text("Currently no pending SRS spaced repetitions cards. Complete lessons to add more vocabulary parameters.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { activeSrsMode = false }) {
                        Text("Zurück zum Vokabelheft")
                    }
                }
            } else {
                var currentIndex by remember { mutableStateOf(0) }
                var cardFlipped by remember { mutableStateOf(false) }

                if (currentIndex >= srsQueue.size) {
                    currentIndex = 0
                }

                val vocab = srsQueue.getOrNull(currentIndex)
                if (vocab != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SRS Leitfaden ($currentIndex / ${srsQueue.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // The Interactive Flashcard
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clickable { cardFlipped = !cardFlipped }
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (cardFlipped) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (cardFlipped) {
                                    // BACK SIDE
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = vocab.meaning,
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Plural: ${vocab.plural}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Beispiel: \"${vocab.exampleSentence}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    // FRONT SIDE (German word)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (vocab.article != "-") {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    vocab.article,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        Text(
                                            text = vocab.word,
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Tippen zum Umdrehen 🔄",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateVocabStatus(vocab, "LEARNING", updateSrs = false)
                                    cardFlipped = false
                                    currentIndex++
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Vergessen ✗", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    viewModel.updateVocabStatus(vocab, "MASTERED", updateSrs = true)
                                    cardFlipped = false
                                    currentIndex++
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Gewusst ✓", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { activeSrsMode = false }) {
                            Text("Zurück zum Vokabelheft")
                        }
                    }
                }
            }
        }
    } else {
        // STANDARD VOCAB LISTING
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Suche Wortschatz...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Suchen") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = { activeSrsMode = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("💡 SRS")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredList.size} Vokabeln im Heft",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { showAddDialog = true }, modifier = Modifier.testTag("add_vocab_button")) {
                    Text("+ Hinzufügen")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val listScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(listScroll)
            ) {
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dein Vokabelheft ist leer. Tippe auf '+ Hinzufügen' oder speichere neue Vokabeln direkt aus den Lektionen!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    filteredList.forEach { v ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val bColor = when (v.article) {
                                        "der" -> Color(0xFF1565C0)
                                        "die" -> Color(0xFFC2185B)
                                        "das" -> Color(0xFF2E7D32)
                                        else -> Color.DarkGray
                                    }
                                    if (v.article != "-") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(bColor)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(v.article, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Column {
                                        Text(text = v.word, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold))
                                        Text(text = "Plural: ${v.plural}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        Text(text = v.meaning, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (v.status == "MASTERED") MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = v.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (v.status == "MASTERED") MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                    IconButton(onClick = { viewModel.removeVocab(v) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var word by remember { mutableStateOf("") }
        var article by remember { mutableStateOf("der") }
        var plural by remember { mutableStateOf("") }
        var meaning by remember { mutableStateOf("") }
        var sentence by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Wort hinzufügen 📝") },
            text = {
                val diagScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(diagScroll)
                        .padding(bottom = 8.dp)
                ) {
                    OutlinedTextField(value = word, onValueChange = { word = it }, label = { Text("Word") })
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Article", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("der", "die", "das", "-").forEach { art ->
                            FilterChip(
                                selected = article == art,
                                onClick = { article = art },
                                label = { Text(art) }
                            )
                        }
                    }

                    OutlinedTextField(value = plural, onValueChange = { plural = it }, label = { Text("Plural (die...)") })
                    OutlinedTextField(value = meaning, onValueChange = { meaning = it }, label = { Text("Meaning (English)") })
                    OutlinedTextField(value = sentence, onValueChange = { sentence = it }, label = { Text("Example Sentence") })
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (Option)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (word.isNotBlank() && meaning.isNotBlank()) {
                            viewModel.addVocabWord(word, article, plural, meaning, sentence, "Pronunciation_Mock", notes)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Speichern")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Abbrechen") }
            }
        )
    }
}

// ==================== 1. GRAMMAR SUBTAB ====================
@Composable
fun GrammarSubtab() {
    val scroll = rememberScrollState()
    val topics = remember { getGrammarTopics() }
    var expandedTopicId by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(16.dp)
    ) {
        Text(
            text = "DEUTSCHE GRAMMATIK AKADEMIE",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        topics.forEach { element ->
            val isExpanded = expandedTopicId == element.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { expandedTopicId = if (isExpanded) -1 else element.id },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = element.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = "Expand"
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text(
                                text = element.explanation,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "💡 Beispiele:\n${element.examples}\n\n⚠️ Häufiger Fehler:\n${element.mistakes}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class GrammarTopic(
    val id: Int,
    val title: String,
    val explanation: String,
    val examples: String,
    val mistakes: String
)

private fun getGrammarTopics() = listOf(
    GrammarTopic(1, "Personal Pronouns (Personalpronomen)", "German pronouns change according to Case: Nominative (Subject), Accusative (Direct Object), and Dative (Indirect Object).", "ich (I) -> mich (me Acc) -> mir (me Dat)\ndu (you) -> dich (you Acc) -> dir (you Dat)", "Writing 'Ich liebe du' instead of 'Ich liebe dich' (Accusative)."),
    GrammarTopic(2, "Verb Conjugation (Konjugation)", "Regular verbs end in -en and take endings corresponding to pronouns: -e, -st, -t, -en, -t, -en.", "ich lerne, du lernst, er/sie/es lernt,\nwir lernen, ihr lernt, sie lernen", "Conjugating irregular exceptions as regular ones ('du schreibst' is regular, but 'du läufst' is stem-changing)."),
    GrammarTopic(3, "Subordinate Clauses (Nebensätze)", "In German subordinate clauses (because, that, when), the conjugated verb is pushed to the absolute end of the clause.", "Weil ich heute Deutsch lerne. (Because I am learning German today.)", "Putting the verb next to the conjunction: 'weil ich liebe..' instead of 'weil ich .. liebe'."),
    GrammarTopic(4, "Relative Clauses (Relativsätze)", "A relative clause describes a noun closer. The relative pronoun matches the noun's gender and number, but takes the case of its role in the sub-clause.", "Der Student, der aus Indien kommt. (The student who comes from India.)", "Conjugating verbs out of position in relative clauses.")
)

// ==================== 2. GERMANY PREP SUBTAB ====================
@Composable
fun GermanyPrepSubtab(viewModel: MainViewModel) {
    val selectedModuleId by viewModel.selectedPrepModuleId.collectAsStateWithLifecycle()

    val modules = remember { getPrepModules() }

    if (selectedModuleId != null) {
        val activeMod = modules.find { it.id == selectedModuleId }
        if (activeMod != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.selectPrepModule(null) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        activeMod.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🎯 IMPORTANT VOCABULARY", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        activeMod.vocabs.forEach { voc ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("📌 ", color = MaterialTheme.colorScheme.secondary)
                                Column {
                                    Text(voc.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(voc.second, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🗣️ PRACTICAL ROLEPLAY CONVERSATION", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        activeMod.dialogues.forEach { dialog ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(dialog.first, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                Text("\"" + dialog.second + "\"", fontSize = 14.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.selectPrepModule(null) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Zurück zur Modulliste")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "DEUTSCHLAND-VORDEREBEREITUNG (Visa & University)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            modules.forEach { mod ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.selectPrepModule(mod.id) },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.CardTravel, contentDescription = "Mod", tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mod.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                            Text(mod.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = "View")
                    }
                }
            }
        }
    }
}

data class PrepModule(
    val id: String,
    val title: String,
    val description: String,
    val vocabs: List<Pair<String, String>>,
    val dialogues: List<Pair<String, String>>
)

private fun getPrepModules() = listOf(
    PrepModule(
        "dep_uni", "University Enrollment (Immatrikulation)", "Vocab and dialogue scripts needed to register at German engineering departments.",
        listOf(
            "die Einschreibung" to "Enrollment",
            "die Matrikelnummer" to "Student ID number",
            "die Studienbescheinigung" to "Certificate of enrollment"
        ),
        listOf(
            "Uday" to "Guten Tag! Ich möchte mich heute für den Studiengang Maschinenbau immatrikulieren.",
            "Sekretärin" to "Guten Tag! Bitte zeigen Sie mir Ihren Zulassungsbescheid und den Nachweis Ihrer Krankenversicherung."
        )
    ),
    PrepModule(
        "dep_housing", "WG/Housing Hunt (Wohnungssuche)", "Search terms, cold rent, warm rent, security deposit terms.",
        listOf(
            "die Kaltmiete" to "Base rent (excluding utilities)",
            "die Nebenkosten" to "Utility expenses",
            "die Kaution" to "Security deposit"
        ),
        listOf(
            "Uday" to "Ist die Kaution in Raten zahlbar?",
            "Vermieter" to "Ja, Sie können die Kaution in drei Teilen überweisen."
        )
    ),
    PrepModule(
        "dep_permit", "Residence Permit & Anmeldung", "Registration layout at Bürgeramt within 14 days of arriving.",
        listOf(
            "die Anmeldung" to "Citizen registration",
            "die Wohnungsgeberbestätigung" to "Landlord certificate",
            "die Aufenthaltserlaubnis" to "Residence permit"
        ),
        listOf(
            "Uday" to "Guten Tag! Ich bin neu in Heidelberg und möchte meine Adresse anmelden.",
            "Beamter" to "Gerne! Haben Sie Ihren Pass und das ausgefüllte Formular des Vermieters dabei?"
        )
    )
)

// ==================== 3. VERB TRAINER SUBTAB ====================
@Composable
fun VerbTrainerSubtab() {
    var query by remember { mutableStateOf("") }
    val verbs = remember { getTrainerVerbs() }

    val filteredVerbs = remember(query) {
        if (query.isBlank()) verbs
        else verbs.filter { it.infinitiv.contains(query, ignoreCase = true) || it.translation.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Verb suchen (e.g. sein, haben)...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
        ) {
            filteredVerbs.forEach { verb ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                verb.infinitiv,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                verb.translation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        val tableGridScroll = rememberScrollState()
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("ich ${verb.conjugations["ich"]}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("wir ${verb.conjugations["wir"]}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("du ${verb.conjugations["du"]}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("ihr ${verb.conjugations["ihr"]}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("er/sie ${verb.conjugations["er"]}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("sie/Sie ${verb.conjugations["sie"]}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class GermanVerb(
    val infinitiv: String,
    val translation: String,
    val conjugations: Map<String, String>
)

private fun getTrainerVerbs() = listOf(
    GermanVerb("sein", "to be", mapOf("ich" to "bin", "du" to "bist", "er" to "ist", "wir" to "sind", "ihr" to "seid", "sie" to "sind")),
    GermanVerb("haben", "to have", mapOf("ich" to "habe", "du" to "hast", "er" to "hat", "wir" to "haben", "ihr" to "habt", "sie" to "haben")),
    GermanVerb("werden", "to become", mapOf("ich" to "werde", "du" to "wirst", "er" to "wird", "wir" to "werden", "ihr" to "werdet", "sie" to "werden")),
    GermanVerb("müssen", "to must/have to", mapOf("ich" to "muss", "du" to "musst", "er" to "muss", "wir" to "müssen", "ihr" to "müsst", "sie" to "müssen")),
    GermanVerb("können", "to can/be able to", mapOf("ich" to "kann", "du" to "kannst", "er" to "kann", "wir" to "können", "ihr" to "könnt", "sie" to "können"))
)

// ==================== 4. JOURNAL SUBTAB ====================
@Composable
fun JournalSubtab(viewModel: MainViewModel) {
    val journalHits by viewModel.journalEntries.collectAsStateWithLifecycle()

    var composeMode by remember { mutableStateOf(false) }

    if (composeMode) {
        var content by remember { mutableStateOf("") }
        var vocabularyText by remember { mutableStateOf("") }
        var mistakesText by remember { mutableStateOf("") }
        var sentenceText by remember { mutableStateOf("") }
        var reflectionText by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("HEUTIGER EINTRAG SCHREIBEN ✍️", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Practice actual German writing. Submitting evaluates your entries automatically using the AI Examiner!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("What I learned today (Explain in German/English)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = sentenceText,
                onValueChange = { sentenceText = it },
                label = { Text("Practiced German sentences (Ich lerne heute...)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = vocabularyText,
                onValueChange = { vocabularyText = it },
                label = { Text("New vocabulary gathered (word + meaning)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = mistakesText,
                onValueChange = { mistakesText = it },
                label = { Text("Mistakes noted during workout") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = reflectionText,
                onValueChange = { reflectionText = it },
                label = { Text("Personal goals for tomorrow") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        viewModel.submitJournalEntry(content, vocabularyText, mistakesText, sentenceText, reflectionText)
                        composeMode = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Tagebuch speichern und bewerten (+50 XP)")
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { composeMode = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abbrechen")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("GERMAN LEARNING JOURNAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(onClick = { composeMode = true }) {
                    Text("+ Schreiben")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val scroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
            ) {
                if (journalHits.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("No journal entries yet. Complete daily write ups to fast-track your B1 active writing!", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                } else {
                    journalHits.forEach { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(entry.dateString, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    IconButton(
                                        onClick = { viewModel.deleteJournal(entry) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Text(entry.content, modifier = Modifier.padding(vertical = 6.dp), fontSize = 13.sp)
                                if (entry.sentences.isNotBlank()) {
                                    Text("📝 Sentences: ${entry.sentences}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                                }
                                if (entry.newWords.isNotBlank()) {
                                    Text("📌 New Vocabs: ${entry.newWords}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                                }
                                if (entry.mistakes.isNotBlank()) {
                                    Text("⚠️ Mistakes: ${entry.mistakes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
