package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StudyPlanItem
import com.example.ui.components.ErrorBanner
import com.example.ui.components.FilterChipSelector
import com.example.ui.components.InfoBanner
import com.example.ui.components.PrimaryActionButton
import com.example.ui.components.StudyCard
import com.example.ui.components.StudyInputField
import com.example.ui.components.StudyTopAppBar
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.StudyBuddyViewModel

@Composable
fun StudyPlannerScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val subject by viewModel.planSubject.collectAsState()
    val topics by viewModel.planTopics.collectAsState()
    val examDate by viewModel.planExamDate.collectAsState()
    val hours by viewModel.planDailyHours.collectAsState()
    val priority by viewModel.planPriority.collectAsState()
    val isLoading by viewModel.planLoading.collectAsState()
    val generatedPlan by viewModel.generatedPlan.collectAsState()
    val savedPlans by viewModel.savedPlans.collectAsState()
    val errorText by viewModel.planError.collectAsState()

    val hourOptions = listOf("1", "2", "3", "4", "5+")
    val priorityOptions = listOf("High", "Medium", "Low")

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "Study Planner & Timetable",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateTo("dashboard") }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                InfoBanner(
                    text = "Builds a balanced, realistic study timetable based on your exam date and study hours. Check off completed milestones to stay on track."
                )
            }

            // Planner Generator Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Exam Target & Availability",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        StudyInputField(
                            value = subject,
                            onValueChange = { viewModel.planSubject.value = it },
                            label = "Subject Name",
                            placeholder = "e.g., Computer Networks, Algorithms, Calculus",
                            leadingIcon = Icons.AutoMirrored.Filled.EventNote,
                            isError = errorText != null && subject.isBlank(),
                            errorMessage = if (subject.isBlank()) errorText else null,
                            testTag = "input_plan_subject"
                        )

                        StudyInputField(
                            value = topics,
                            onValueChange = { viewModel.planTopics.value = it },
                            label = "Topics to Cover",
                            placeholder = "e.g., IP addressing, Routing algorithms, Congestion control",
                            singleLine = false,
                            minLines = 2,
                            isError = errorText != null && topics.isBlank(),
                            errorMessage = if (topics.isBlank()) errorText else null,
                            testTag = "input_plan_topics"
                        )

                        StudyInputField(
                            value = examDate,
                            onValueChange = { viewModel.planExamDate.value = it },
                            label = "Exam Date / Target Deadline",
                            placeholder = "e.g., Nov 25, 2026 or Next Friday",
                            leadingIcon = Icons.Default.CalendarMonth,
                            isError = errorText != null && examDate.isBlank(),
                            errorMessage = if (examDate.isBlank()) errorText else null,
                            testTag = "input_plan_date"
                        )

                        Text(
                            text = "Daily Study Hours Available:",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        FilterChipSelector(
                            options = hourOptions,
                            selected = "$hours",
                            onSelect = { viewModel.planDailyHours.value = it.removeSuffix("+").toIntOrNull() ?: 3 }
                        )

                        Text(
                            text = "Exam Priority Level:",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        FilterChipSelector(
                            options = priorityOptions,
                            selected = priority,
                            onSelect = { viewModel.planPriority.value = it }
                        )

                        PrimaryActionButton(
                            text = "Create Smart Study Timetable",
                            onClick = { viewModel.generateStudyPlan() },
                            isLoading = isLoading,
                            testTag = "btn_generate_plan"
                        )
                    }
                }
            }

            if (errorText != null && subject.isNotBlank() && topics.isNotBlank()) {
                item {
                    ErrorBanner(message = errorText ?: "Could not generate timetable.")
                }
            }

            // Generated Timetable Overview
            if (generatedPlan != null) {
                val plan = generatedPlan!!
                item {
                    StudyCard(
                        backgroundColor = Color(0xFFFAFCFF),
                        borderColor = Color(0xFFBFDBFE)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = BluePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Suggested Study Schedule",
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = plan.overview,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Strategy: ${plan.recommendedPace}",
                                color = BluePrimaryDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Milestones list
                items(plan.milestones.size) { index ->
                    val milestone = plan.milestones[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BlueContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = milestone.dayOrPeriod,
                                        color = BluePrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = milestone.target,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            milestone.tasks.forEach { task ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", color = BluePrimary, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = task,
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            if (milestone.tips.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "💡 Tip: ${milestone.tips}",
                                    color = Color(0xFF0369A1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Saved Study Plans / Checklist Section
            if (savedPlans.isNotEmpty()) {
                item {
                    Text(
                        text = "My Active Study Timetable (${savedPlans.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimaryDark
                        )
                    )
                }

                items(savedPlans.size) { index ->
                    val item = savedPlans[index]
                    StudyPlanItemCard(
                        planItem = item,
                        onToggle = { viewModel.togglePlanItem(item) },
                        onDelete = { viewModel.deletePlanItem(item) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StudyPlanItemCard(
    planItem: StudyPlanItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (planItem.isCompleted) Color(0xFFF1F5F9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clickable { onToggle() }
            ) {
                Icon(
                    imageVector = if (planItem.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (planItem.isCompleted) SuccessGreen else BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = planItem.subject,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (planItem.isCompleted) Color(0xFF64748B) else TextPrimary
                        )
                    )

                    val priorityColor = when (planItem.priority.lowercase()) {
                        "high" -> Color(0xFFDC2626)
                        "medium" -> WarningAmber
                        else -> SuccessGreen
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityColor.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = planItem.priority,
                            color = priorityColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = planItem.topic,
                    color = if (planItem.isCompleted) Color(0xFF94A3B8) else TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Exam Target: ${planItem.examDate}",
                        color = BluePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (planItem.suggestedMilestones.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = planItem.suggestedMilestones,
                        color = Color(0xFF475569),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
