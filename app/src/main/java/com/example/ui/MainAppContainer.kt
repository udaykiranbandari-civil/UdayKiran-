package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.AppTab
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    
    var showDayPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "UDAYS WEG",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "MISSION: B1 READINESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                },
                actions = {
                    // Level Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "LVL ${stats.level}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Active Streak Indicator Capsule (Bento Amber Capsule)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                        shape = CircleShape,
                        modifier = Modifier
                            .clickable { showDayPicker = true }
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${stats.currentStreak}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Current Program Day Button
                    Button(
                        onClick = { showDayPicker = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("day_picker_button")
                    ) {
                        Text(
                            text = "Tag ${stats.currentDayIndex}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { viewModel.selectTab(AppTab.HOME) },
                    icon = { Icon(if (currentTab == AppTab.HOME) Icons.Filled.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_home")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MISSION,
                    onClick = { viewModel.selectTab(AppTab.MISSION) },
                    icon = { Icon(if (currentTab == AppTab.MISSION) Icons.Filled.DoneOutline else Icons.Outlined.DoneOutline, contentDescription = "Mission") },
                    label = { Text("Missions", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_mission")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.CURRICULUM,
                    onClick = { viewModel.selectTab(AppTab.CURRICULUM) },
                    icon = { Icon(if (currentTab == AppTab.CURRICULUM) Icons.Filled.MenuBook else Icons.Outlined.MenuBook, contentDescription = "Nicos Weg") },
                    label = { Text("Curriculum", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_curriculum")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.NOTEBOOKS,
                    onClick = { viewModel.selectTab(AppTab.NOTEBOOKS) },
                    icon = { Icon(if (currentTab == AppTab.NOTEBOOKS) Icons.Filled.FolderZip else Icons.Outlined.FolderZip, contentDescription = "Notebooks") },
                    label = { Text("Notebooks", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_notebooks")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.AI_COACH,
                    onClick = { viewModel.selectTab(AppTab.AI_COACH) },
                    icon = { Icon(if (currentTab == AppTab.AI_COACH) Icons.Filled.Psychology else Icons.Outlined.Psychology, contentDescription = "AI Coach") },
                    label = { Text("AI Coach", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_ai_coach")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (slideInHorizontally { width -> width / 3 } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width / 3 } + fadeOut())
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.HOME -> HomeTab(viewModel)
                    AppTab.MISSION -> DailyMissionTab(viewModel)
                    AppTab.CURRICULUM -> CurriculumTab(viewModel)
                    AppTab.NOTEBOOKS -> NotebooksTab(viewModel)
                    AppTab.AI_COACH -> CoachAiTab(viewModel)
                }
            }
        }
    }

    if (showDayPicker) {
        AlertDialog(
            onDismissRequest = { showDayPicker = false },
            title = { Text("Wähle deinen Trainingstag (Day 1 - 60)") },
            text = {
                Column {
                    Text(
                        text = "Customize Uday's study progress. Jump directly between curriculum units from A1 toward senior B1 level metrics.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { viewModel.setDay(1); showDayPicker = false }) { Text("Day 1 (A1 Starter)") }
                        Button(onClick = { viewModel.setDay(16); showDayPicker = false }) { Text("Day 16 (A2 Entry)") }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { viewModel.setDay(36); showDayPicker = false }) { Text("Day 36 (B1 Entry)") }
                        Button(onClick = { viewModel.setDay(59); showDayPicker = false }) { Text("Day 59 (Final Exams)") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Slider to pick custom days
                    Text("Day Slider: Tag ${stats.currentDayIndex}")
                    Slider(
                        value = stats.currentDayIndex.toFloat(),
                        onValueChange = { viewModel.setDay(it.toInt()) },
                        valueRange = 1f..60f,
                        steps = 58
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDayPicker = false }) {
                    Text("Verstanden")
                }
            }
        )
    }
}
