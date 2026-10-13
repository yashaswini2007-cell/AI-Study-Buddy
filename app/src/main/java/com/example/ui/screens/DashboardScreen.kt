package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyBuddyViewModel

data class FeatureItem(
    val route: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String,
    val testTag: String
)

@Composable
fun DashboardScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val savedNotes by viewModel.savedNotes.collectAsState()
    val savedPlans by viewModel.savedPlans.collectAsState()
    val isConfigured by viewModel.isGeminiConfigured.collectAsState()

    val features = listOf(
        FeatureItem(
            route = "topic_explainer",
            title = "Topic Explainer",
            subtitle = "Simple English explanations with practical real-world applications & examples",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            badge = "Academic AI",
            testTag = "card_topic_explainer"
        ),
        FeatureItem(
            route = "smart_notes",
            title = "Smart Notes",
            subtitle = "Generate organized revision cheat sheets & in-depth exam study notes",
            icon = Icons.Default.Description,
            badge = "High Yield",
            testTag = "card_smart_notes"
        ),
        FeatureItem(
            route = "viva_prep",
            title = "Viva Questions",
            subtitle = "Oral examination Q&A across beginner, intermediate & advanced levels",
            icon = Icons.Default.RecordVoiceOver,
            badge = "Oral Exam",
            testTag = "card_viva_prep"
        ),
        FeatureItem(
            route = "quiz_generator",
            title = "Quiz Generator",
            subtitle = "Interactive 4-option multiple-choice tests with real-time scoring & feedback",
            icon = Icons.Default.Quiz,
            badge = "Self Test",
            testTag = "card_quiz_generator"
        ),
        FeatureItem(
            route = "code_assistant",
            title = "Programming Assistant",
            subtitle = "C, Python, Java & SQL code generation, line-by-line walk-throughs & error debugging",
            icon = Icons.Default.Code,
            badge = "4 Languages",
            testTag = "card_code_assistant"
        ),
        FeatureItem(
            route = "study_planner",
            title = "Study Planner",
            subtitle = "Intelligent exam timetable creation with progress checkboxes & daily milestones",
            icon = Icons.Default.CalendarMonth,
            badge = "Time Table",
            testTag = "card_study_planner"
        )
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        val isWide = maxWidth >= 600.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Welcome Card (Deep Blue Gradient)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BluePrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(BluePrimaryDark, BluePrimary, Color(0xFF2563EB))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "AI Study Buddy",
                                            color = Color.White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "College Assistant & Exam Prep",
                                            color = Color(0xFFDBEAFE),
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.navigateTo("settings") },
                                    modifier = Modifier.testTag("dashboard_settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Learn subjects easily, clear oral viva exams, test yourself with quizzes, and debug code line-by-line.",
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Quick Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatChip(
                                    label = "Saved Notes",
                                    value = "${savedNotes.size}",
                                    onClick = { viewModel.navigateTo("saved_notes") }
                                )
                                StatChip(
                                    label = "Study Plans",
                                    value = "${savedPlans.size}",
                                    onClick = { viewModel.navigateTo("study_planner") }
                                )
                                StatChip(
                                    label = "AI Engine",
                                    value = if (isConfigured) "Active" else "Ready",
                                    onClick = { viewModel.navigateTo("settings") }
                                )
                            }
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Academic Toolkit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimaryDark
                        )
                    )
                    Text(
                        text = "6 Core Features",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Feature Cards Grid/List
            if (isWide) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (pair in features.chunked(2)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for (feature in pair) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        FeatureCard(feature = feature) {
                                            viewModel.navigateTo(feature.route)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                items(features.size) { index ->
                    val feature = features[index]
                    FeatureCard(feature = feature) {
                        viewModel.navigateTo(feature.route)
                    }
                }
            }

            // Study Tip Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BlueContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pro Study Tip: Active Recall",
                                fontWeight = FontWeight.Bold,
                                color = BluePrimaryDark,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Use the Viva Questions and Quiz Generator immediately after reading notes to reinforce memory retention by 300%.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.15f),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = label,
                color = Color(0xFFDBEAFE),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(feature.testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = BluePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF6FF))
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = feature.badge,
                            color = BluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = feature.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
