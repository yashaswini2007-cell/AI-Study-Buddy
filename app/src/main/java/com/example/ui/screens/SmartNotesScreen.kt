package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.StudyBuddyViewModel

@Composable
fun SmartNotesScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val context = LocalContext.current
    val subject by viewModel.notesSubject.collectAsState()
    val topic by viewModel.notesTopic.collectAsState()
    val noteType by viewModel.notesType.collectAsState()
    val isLoading by viewModel.notesLoading.collectAsState()
    val notesResult by viewModel.notesResult.collectAsState()
    val errorText by viewModel.notesError.collectAsState()
    val isSaved by viewModel.noteSavedSuccess.collectAsState()

    val noteTypeOptions = listOf("Detailed", "Revision")

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "Smart Notes Generator",
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
                    text = "Generates high-scoring study notes formatted with definitions, formulas, high-yield bullet points, practical case studies, and exam tips."
                )
            }

            // Input Form Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Select Notes Format",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            noteTypeOptions.forEach { type ->
                                val isSelected = noteType.equals(type, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) BluePrimary else Color(0xFFF1F5F9))
                                        .clickable { viewModel.notesType.value = type }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (type == "Detailed") "Detailed Notes" else "Revision Sheet",
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        StudyInputField(
                            value = subject,
                            onValueChange = { viewModel.notesSubject.value = it },
                            label = "Subject Name",
                            placeholder = "e.g., Operating Systems, DBMS, Software Engineering",
                            leadingIcon = Icons.Default.Description,
                            isError = errorText != null && subject.isBlank(),
                            errorMessage = if (subject.isBlank()) errorText else null,
                            testTag = "input_notes_subject"
                        )

                        StudyInputField(
                            value = topic,
                            onValueChange = { viewModel.notesTopic.value = it },
                            label = "Topic",
                            placeholder = "e.g., Process Scheduling, ACID Properties, Agile vs Scrum",
                            leadingIcon = Icons.AutoMirrored.Filled.MenuBook,
                            isError = errorText != null && topic.isBlank(),
                            errorMessage = if (topic.isBlank()) errorText else null,
                            testTag = "input_notes_topic"
                        )

                        PrimaryActionButton(
                            text = if (noteType == "Revision") "Generate Quick Revision Sheet" else "Generate Detailed Exam Notes",
                            onClick = { viewModel.generateSmartNotes() },
                            isLoading = isLoading,
                            testTag = "btn_generate_notes"
                        )
                    }
                }
            }

            // Error Display
            if (errorText != null && subject.isNotBlank() && topic.isNotBlank()) {
                item {
                    ErrorBanner(message = errorText ?: "Could not generate notes.")
                }
            }

            // Saved Success Banner
            if (isSaved) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SuccessContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdded,
                                contentDescription = null,
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notes saved to your notebook! View them anytime in 'Saved Notes'.",
                                color = SuccessGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Results Card
            if (notesResult != null) {
                item {
                    StudyCard(
                        backgroundColor = Color(0xFFFAFCFF),
                        borderColor = Color(0xFFBFDBFE)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (noteType == "Revision") "Quick Revision Notes" else "Comprehensive Notes",
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary,
                                    fontSize = 16.sp
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Copy Button
                                    OutlinedButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("study_notes", notesResult)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Notes copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            modifier = Modifier.size(16.dp),
                                            tint = BluePrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", fontSize = 12.sp, color = BluePrimary)
                                    }

                                    // Save Button
                                    Button(
                                        onClick = { viewModel.saveCurrentNotes() },
                                        enabled = !isSaved,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Default.BookmarkAdded else Icons.Default.Bookmark,
                                            contentDescription = "Save",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isSaved) "Saved" else "Save Note", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            FormattedStudyContent(content = notesResult ?: "")
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
