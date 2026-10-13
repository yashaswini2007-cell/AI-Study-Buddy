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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.QuizQuestion
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
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyBuddyViewModel

@Composable
fun QuizGeneratorScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val topic by viewModel.quizTopic.collectAsState()
    val count by viewModel.quizCount.collectAsState()
    val isLoading by viewModel.quizLoading.collectAsState()
    val questions by viewModel.quizQuestions.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val isSubmitted by viewModel.quizSubmitted.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val errorText by viewModel.quizError.collectAsState()

    val countOptions = listOf("3", "5", "8")

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "Quiz Generator",
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
                    text = "Generate 4-option multiple choice quizzes on any subject. Test your understanding, view detailed explanations, and review your final score."
                )
            }

            // Quiz Config Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Number of Questions",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        FilterChipSelector(
                            options = countOptions,
                            selected = count.toString(),
                            onSelect = { viewModel.quizCount.value = it.toIntOrNull() ?: 5 }
                        )

                        StudyInputField(
                            value = topic,
                            onValueChange = { viewModel.quizTopic.value = it },
                            label = "Quiz Topic / Subject",
                            placeholder = "e.g., Python OOP, SQL Joins, CPU Scheduling, Physics Optics",
                            leadingIcon = Icons.Default.Quiz,
                            isError = errorText != null && topic.isBlank(),
                            errorMessage = errorText,
                            testTag = "input_quiz_topic"
                        )

                        PrimaryActionButton(
                            text = "Generate Interactive Quiz",
                            onClick = { viewModel.generateQuiz() },
                            isLoading = isLoading,
                            testTag = "btn_generate_quiz"
                        )
                    }
                }
            }

            if (errorText != null && topic.isNotBlank()) {
                item {
                    ErrorBanner(message = errorText ?: "Could not generate quiz.")
                }
            }

            // Results & Score Summary (Shown when submitted)
            if (isSubmitted && questions.isNotEmpty()) {
                item {
                    val percentage = (score * 100) / questions.size
                    val gradeColor = if (percentage >= 70) SuccessGreen else if (percentage >= 50) Color(0xFFD97706) else ErrorRed

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(2.dp, gradeColor),
                        shadowElevation = 3.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(gradeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = gradeColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Quiz Score: $score / ${questions.size} ($percentage%)",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Text(
                                text = if (percentage >= 80) "Excellent performance! Concepts are well understood."
                                else if (percentage >= 50) "Good effort! Review the explanations below to master weak spots."
                                else "Needs revision. Go through the explanations and try again!",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { viewModel.retakeQuiz() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retake",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retake This Quiz", color = BluePrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Questions List
            if (questions.isNotEmpty()) {
                items(questions.size) { index ->
                    val q = questions[index]
                    val selectedIdx = selectedAnswers[q.id]

                    QuizQuestionItem(
                        question = q,
                        questionIndex = index + 1,
                        selectedIndex = selectedIdx,
                        isSubmitted = isSubmitted,
                        onSelect = { optIdx ->
                            viewModel.selectQuizAnswer(q.id, optIdx)
                        }
                    )
                }

                // Submit Button
                if (!isSubmitted) {
                    item {
                        Button(
                            onClick = { viewModel.submitQuiz() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_submit_quiz")
                        ) {
                            Text(
                                text = "Submit Answers & View Score",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun QuizQuestionItem(
    question: QuizQuestion,
    questionIndex: Int,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    onSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$questionIndex",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Options
            question.options.forEachIndexed { optIdx, optionText ->
                val isSelected = selectedIndex == optIdx
                val isCorrect = question.correctIndex == optIdx

                val (bgColor, borderColor, textColor) = when {
                    !isSubmitted && isSelected -> Triple(BlueContainer, BluePrimary, BluePrimaryDark)
                    !isSubmitted -> Triple(Color(0xFFF8FAFC), Color(0xFFE2E8F0), TextPrimary)
                    isSubmitted && isCorrect -> Triple(SuccessContainer, SuccessGreen, Color(0xFF14532D))
                    isSubmitted && isSelected && !isCorrect -> Triple(ErrorContainer, ErrorRed, Color(0xFF7F1D1D))
                    else -> Triple(Color(0xFFF8FAFC), Color(0xFFE2E8F0), TextSecondary)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                        .clickable(enabled = !isSubmitted) { onSelect(optIdx) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Option Letter (A, B, C, D)
                        val letter = ('A' + optIdx).toString()
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected || (isSubmitted && isCorrect)) borderColor else Color(0xFFCBD5E1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = optionText,
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected || (isSubmitted && isCorrect)) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSubmitted) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Correct",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Incorrect",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Explanation (shown after submission)
            if (isSubmitted) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Explanation:",
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = question.explanation,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
