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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.CodeSnippetCard
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.StudyBuddyViewModel

@Composable
fun ProgrammingAssistantScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val language by viewModel.codeLanguage.collectAsState()
    val mode by viewModel.codeMode.collectAsState()
    val query by viewModel.codeQuery.collectAsState()
    val snippet by viewModel.codeSnippet.collectAsState()
    val isLoading by viewModel.codeLoading.collectAsState()
    val codeResult by viewModel.codeResult.collectAsState()
    val errorText by viewModel.codeError.collectAsState()

    val languages = listOf("Python", "C", "Java", "SQL")
    val modeOptions = listOf(
        Pair("Concept", "Concept Explainer"),
        Pair("CodeLineByLine", "Code + Line-by-Line"),
        Pair("Debugger", "Error Debugger")
    )

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "Programming Assistant",
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
                    text = "Specialized engineering tutor supporting C, Python, Java & SQL. Generates programs with line-by-line walkthroughs and decodes compiler errors."
                )
            }

            // Controls Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Language Selection
                        Text(
                            text = "Target Language",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        FilterChipSelector(
                            options = languages,
                            selected = language,
                            onSelect = { viewModel.codeLanguage.value = it }
                        )

                        // Mode Selection
                        Text(
                            text = "Assistance Mode",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            modeOptions.forEach { (key, label) ->
                                val isSelected = mode == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) BluePrimary else Color(0xFFF1F5F9))
                                        .clickable { viewModel.codeMode.value = key }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Query Field
                        StudyInputField(
                            value = query,
                            onValueChange = { viewModel.codeQuery.value = it },
                            label = if (mode == "Debugger") "Error Message / Bug Description" else "Concept or Program Request",
                            placeholder = when (mode) {
                                "Debugger" -> "e.g., Segmentation Fault (core dumped) or IndexError: list index out of range"
                                "CodeLineByLine" -> "e.g., Linked list insertion, Binary search, Banking ATM system"
                                else -> "e.g., Pointers vs References, OOP Polymorphism, JOIN operations"
                            },
                            leadingIcon = if (mode == "Debugger") Icons.Default.BugReport else Icons.Default.Code,
                            isError = errorText != null && query.isBlank(),
                            errorMessage = errorText,
                            testTag = "input_code_query"
                        )

                        // Code snippet field if in Debugger mode
                        if (mode == "Debugger") {
                            StudyInputField(
                                value = snippet,
                                onValueChange = { viewModel.codeSnippet.value = it },
                                label = "Your Code Snippet (Paste here)",
                                placeholder = "Paste your buggy code snippet here...",
                                singleLine = false,
                                minLines = 4,
                                leadingIcon = Icons.Default.IntegrationInstructions,
                                testTag = "input_code_snippet"
                            )
                        }

                        PrimaryActionButton(
                            text = when (mode) {
                                "Debugger" -> "Analyze & Fix Code Error"
                                "CodeLineByLine" -> "Generate Program + Walkthrough"
                                else -> "Explain Concept with Example"
                            },
                            onClick = { viewModel.getProgrammingHelp() },
                            isLoading = isLoading,
                            testTag = "btn_code_assist"
                        )
                    }
                }
            }

            if (errorText != null && query.isNotBlank()) {
                item {
                    ErrorBanner(message = errorText ?: "Could not process code request.")
                }
            }

            // Results Display
            if (codeResult != null) {
                val res = codeResult!!

                // Summary Card
                item {
                    StudyCard(
                        backgroundColor = Color(0xFFFAFCFF),
                        borderColor = Color(0xFFBFDBFE)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = BluePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Overview & Solution Summary",
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = res.summary,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Debugging Analysis if available
                if (!res.debuggingAnalysis.isNullOrBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFFBEB),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = Color(0xFFB45309)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Bug Analysis & Prevention Tips",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = res.debuggingAnalysis ?: "",
                                    color = Color(0xFF92400E),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Code Snippet Card
                if (res.code.isNotBlank()) {
                    item {
                        CodeSnippetCard(
                            language = res.language,
                            code = res.code
                        )
                    }
                }

                // Line by Line Explanation
                if (res.lineByLineExplanation.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Line-by-Line Code Explanation",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimaryDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                res.lineByLineExplanation.forEachIndexed { idx, lineExplanation ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(BlueContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                color = BluePrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = lineExplanation,
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
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
