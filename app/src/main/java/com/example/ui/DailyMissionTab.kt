package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.MainViewModel

@Composable
fun DailyMissionTab(viewModel: MainViewModel) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()

    val currentDayIndex = stats.currentDayIndex
    // Find the corresponding roadmap day
    val roadmapDay = Curriculum.roadmap.find { it.dayIndex == currentDayIndex }
        ?: Curriculum.roadmap.first()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- ROADMAP HEADER BAR (BENTO BOX) ---
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DEINE INTENSIVE MISSION 🎯",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = roadmapDay.dateString.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tag $currentDayIndex von 60",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = roadmapDay.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = roadmapDay.objectives,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Estimated time label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Geschätzte Lernzeit: ${roadmapDay.estimatedStudyTimeMinutes} Minuten",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // --- THE LIST OF DAILY CHECKS ---
        Text(
            text = "HEUTIGE LERNZIELE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        val agendaList = remember(currentDayIndex) {
            listOf(
                MissionTaskItem(
                    id = "task_lesson",
                    title = "Nicos Weg Unterricht",
                    desc = if (roadmapDay.lessonIds.isNotEmpty()) "Lerne Lektion: ${roadmapDay.title}" else "Weekly System consolidation - reviews",
                    icon = Icons.Filled.CollectionsBookmark,
                    actionLabel = if (roadmapDay.lessonIds.isNotEmpty()) "Lektion starten" else ""
                ),
                MissionTaskItem(
                    id = "task_vocab",
                    title = "Vocabulary Target",
                    desc = roadmapDay.vocabularyGoals,
                    icon = Icons.Filled.Spellcheck
                ),
                MissionTaskItem(
                    id = "task_grammar",
                    title = "Grammar Application",
                    desc = roadmapDay.grammarGoals,
                    icon = Icons.Filled.LibraryBooks
                ),
                MissionTaskItem(
                    id = "task_listen",
                    title = "Listening Practice",
                    desc = roadmapDay.listeningTasks,
                    icon = Icons.Filled.Headphones
                ),
                MissionTaskItem(
                    id = "task_speak",
                    title = "Speaking Output",
                    desc = roadmapDay.speakingTasks,
                    icon = Icons.Filled.Mic
                ),
                MissionTaskItem(
                    id = "task_write",
                    title = "Writing & Reflections",
                    desc = roadmapDay.writingTasks,
                    icon = Icons.Filled.Create
                ),
                MissionTaskItem(
                    id = "task_revise",
                    title = "Spaced Repetition Review",
                    desc = roadmapDay.revisionTasks,
                    icon = Icons.Filled.Cached
                )
            )
        }

        agendaList.forEach { task ->
            val isChecked = completedTasks[task.id] ?: false
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleTask(task.id, !isChecked) }
                    .testTag(task.id),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { viewModel.toggleTask(task.id, it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            ),
                            color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = task.desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = task.icon,
                        contentDescription = "Task Icon",
                        tint = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- COMPLETED REWARD BLOCK (BENTO CARD) ---
        val checkedCount = completedTasks.values.count { it }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Score: $checkedCount / 7 erledigt",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "+20 XP per checkbox!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        // Navigate to active lesson page if one exists, otherwise open notebooks tab
                        if (roadmapDay.lessonIds.isNotEmpty()) {
                            viewModel.selectTab(AppTab.CURRICULUM)
                            viewModel.selectLesson(roadmapDay.lessonIds.first())
                        } else {
                            viewModel.selectTab(AppTab.NOTEBOOKS)
                            viewModel.selectNotebookTab(0)
                        }
                    },
                    enabled = checkedCount < 7,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = if (roadmapDay.lessonIds.isNotEmpty()) "Studium Starten" else "Revision",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class MissionTaskItem(
    val id: String,
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val actionLabel: String = ""
)
