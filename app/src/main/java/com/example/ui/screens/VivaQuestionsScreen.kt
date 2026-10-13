package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.VivaQuestion
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
fun VivaQuestionsScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val topic by viewModel.vivaTopic.collectAsState()
    val difficulty by viewModel.vivaDifficulty.collectAsState()
    val isLoading by viewModel.vivaLoading.collectAsState()
    val questionsList by viewModel.vivaList.collectAsState()
    val revealedIds by viewModel.revealedVivaIds.collectAsState()
    val masteredIds by viewModel.masteredVivaIds.collectAsState()
    val errorText by viewModel.vivaError.collectAsState()

    val difficultyOptions = listOf("All", "Beginner", "Intermediate", "Advanced")

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "Viva Question Generator",
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
                    text = "Prepares you for oral / lab examinations with high-frequency examiner questions, ideal concise model answers, and interactive reveal mode."
                )
            }

            // Input Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Difficulty Level",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        FilterChipSelector(
                            options = difficultyOptions,
                            selected = difficulty,
                            onSelect = { viewModel.vivaDifficulty.value = it }
                        )

                        StudyInputField(
                            value = topic,
                            onValueChange = { viewModel.vivaTopic.value = it },
                            label = "Viva Examination Topic",
                            placeholder = "e.g., Virtual Memory, Linked Lists, Normalization, OSI Model",
                            leadingIcon = Icons.Default.RecordVoiceOver,
                            isError = errorText != null && topic.isBlank(),
                            errorMessage = errorText,
                            testTag = "input_viva_topic"
                        )

                        PrimaryActionButton(
                            text = "Generate Viva Questions",
                            onClick = { viewModel.generateViva() },
                            isLoading = isLoading,
                            testTag = "btn_generate_viva"
                        )
                    }
                }
            }

            if (errorText != null && topic.isNotBlank()) {
                item {
                    ErrorBanner(message = errorText ?: "Could not generate viva questions.")
                }
            }

            // Questions List
            if (questionsList.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Oral Exam Questions (${questionsList.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimaryDark
                            )
                        )
                        Text(
                            text = "Mastered: ${masteredIds.size}/${questionsList.size}",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(questionsList.size) { index ->
                    val q = questionsList[index]
                    val isRevealed = revealedIds.contains(q.id)
                    val isMastered = masteredIds.contains(q.id)

                    VivaQuestionCard(
                        question = q,
                        isRevealed = isRevealed,
                        isMastered = isMastered,
                        onToggleReveal = { viewModel.toggleRevealViva(q.id) },
                        onToggleMastered = { viewModel.toggleMasteredViva(q.id) }
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
fun VivaQuestionCard(
    question: VivaQuestion,
    isRevealed: Boolean,
    isMastered: Boolean,
    onToggleReveal: () -> Unit,
    onToggleMastered: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMastered) Color(0xFFF0FDF4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Question number, difficulty chip, mastered button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Q${question.id}",
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val diffColor = when (question.difficulty.lowercase()) {
                        "beginner" -> SuccessGreen
                        "advanced" -> Color(0xFFDC2626)
                        else -> WarningAmber
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(diffColor.copy(alpha = 0.1f))
                            .border(1.dp, diffColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = question.difficulty,
                            color = diffColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mastered Checkbox Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleMastered() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isMastered) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Mastered",
                        tint = if (isMastered) SuccessGreen else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMastered) "Mastered" else "Mark Done",
                        fontSize = 12.sp,
                        color = if (isMastered) SuccessGreen else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reveal Button
            OutlinedButton(
                onClick = onToggleReveal,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(
                    imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = BluePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRevealed) "Hide Examiner Answer" else "Reveal Examiner Answer",
                    fontSize = 13.sp,
                    color = BluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Answer Content (Animated)
            AnimatedVisibility(visible = isRevealed) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Expected Examiner Answer:",
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.answer,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
