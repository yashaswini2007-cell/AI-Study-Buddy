package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.ui.components.ErrorBanner
import com.example.ui.components.FilterChipSelector
import com.example.ui.components.FormattedStudyContent
import com.example.ui.components.InfoBanner
import com.example.ui.components.PrimaryActionButton
import com.example.ui.components.StudyCard
import com.example.ui.components.StudyInputField
import com.example.ui.components.StudyTopAppBar
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyBuddyViewModel

@Composable
fun TopicExplainerScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val topic by viewModel.explainerTopic.collectAsState()
    val academicContext by viewModel.explainerContext.collectAsState()
    val isLoading by viewModel.explainerLoading.collectAsState()
    val resultText by viewModel.explainerResult.collectAsState()
    val errorText by viewModel.explainerError.collectAsState()

    val contextOptions = listOf(
        "Computer Science",
        "Electronics & Electrical",
        "Physics & Chemistry",
        "Mathematics",
        "Business & Economics",
        "General Science"
    )

    val sampleTopics = listOf(
        "Deadlock in OS",
        "Binary Search Trees",
        "Database Normalization",
        "TCP vs UDP Protocols",
        "Gradient Descent"
    )

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "AI Topic Explainer",
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
                    text = "Explains academic topics in simple English with intuitive analogies, structured bullet points, and practical real-world applications."
                )
            }

            // Input Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Select Academic Domain",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        FilterChipSelector(
                            options = contextOptions,
                            selected = academicContext,
                            onSelect = { viewModel.explainerContext.value = it }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        StudyInputField(
                            value = topic,
                            onValueChange = { viewModel.explainerTopic.value = it },
                            label = "Academic Topic",
                            placeholder = "e.g., Binary Search Trees, Normalization, Photosynthesis",
                            leadingIcon = Icons.AutoMirrored.Filled.MenuBook,
                            isError = errorText != null && topic.isBlank(),
                            errorMessage = errorText,
                            testTag = "input_explainer_topic"
                        )

                        // Sample Quick-Fill Chips
                        Column {
                            Text(
                                text = "Popular Suggestions:",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sampleTopics.forEach { sample ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .clickable { viewModel.explainerTopic.value = sample }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = sample,
                                            fontSize = 12.sp,
                                            color = BluePrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        PrimaryActionButton(
                            text = "Explain in Simple English",
                            onClick = { viewModel.explainTopic() },
                            isLoading = isLoading,
                            testTag = "btn_explain_topic"
                        )
                    }
                }
            }

            // Error Message
            if (errorText != null && topic.isNotBlank()) {
                item {
                    ErrorBanner(message = errorText ?: "An error occurred.")
                }
            }

            // Output Display Card
            if (resultText != null) {
                item {
                    StudyCard(
                        backgroundColor = Color(0xFFFAFCFF),
                        borderColor = Color(0xFFBFDBFE)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = BluePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Explanation & Practical Breakdown",
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary,
                                    fontSize = 16.sp
                                )
                            }

                            FormattedStudyContent(content = resultText ?: "")
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
