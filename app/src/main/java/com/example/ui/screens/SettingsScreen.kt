package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiService
import com.example.ui.components.InfoBanner
import com.example.ui.components.StudyCard
import com.example.ui.components.StudyInputField
import com.example.ui.components.StudyTopAppBar
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyBuddyViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val customKey by viewModel.customApiKey.collectAsState()
    val isConfigured by viewModel.isGeminiConfigured.collectAsState()

    var inputKey by remember { mutableStateOf(customKey) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testSuccess by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "AI Engine & Configuration",
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
                    text = "AI Study Buddy integrates Google Gemini (gemini-3.5-flash) to dynamically formulate explanations, generate study notes, oral viva Q&As, quizzes, and code walkthroughs."
                )
            }

            // Status Card
            item {
                StudyCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isConfigured) SuccessContainer else BlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isConfigured) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isConfigured) SuccessGreen else BluePrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = if (isConfigured) "Gemini AI: Connected" else "Dual-Mode: Online AI & Academic Fallback",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isConfigured) SuccessGreen else BluePrimaryDark
                                )
                            )
                            Text(
                                text = if (isConfigured) "Ready to generate dynamic academic material" else "Active with offline curriculum knowledge + ready for API key",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // API Configuration Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = BluePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini API Key Setup",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            )
                        }

                        Text(
                            text = "AI Studio automatically injects your API key via the Secrets panel. You can also paste your Gemini API key below to override or test directly:",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        StudyInputField(
                            value = inputKey,
                            onValueChange = { inputKey = it },
                            label = "Gemini API Key (Optional)",
                            placeholder = "AIzaSy...",
                            testTag = "input_custom_api_key"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateApiKey(inputKey)
                                    Toast.makeText(context, "API Key saved successfully!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_save_key")
                            ) {
                                Text("Save Key", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    isTestingConnection = true
                                    testResult = null
                                    testSuccess = null
                                    coroutineScope.launch {
                                        val service = GeminiService()
                                        val res = service.generateRawContent(
                                            prompt = "Hello! Confirm in 10 words that AI Study Buddy is operational.",
                                            apiKey = inputKey
                                        )
                                        isTestingConnection = false
                                        res.onSuccess {
                                            testSuccess = true
                                            testResult = "Connection successful: $it"
                                        }.onFailure {
                                            testSuccess = false
                                            testResult = "Connection test: ${it.message}"
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isTestingConnection,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_test_connection")
                            ) {
                                if (isTestingConnection) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.NetworkCheck,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = BluePrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test Ping", color = BluePrimary, fontSize = 12.sp)
                                }
                            }
                        }

                        // Test Result Display
                        if (testResult != null) {
                            val isSuccess = testSuccess == true
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSuccess) SuccessContainer else Color(0xFFFEF2F2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = testResult ?: "",
                                    color = if (isSuccess) SuccessGreen else Color(0xFF991B1B),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Security & Privacy Card
            item {
                StudyCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BluePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Security & Architecture",
                                fontWeight = FontWeight.Bold,
                                color = BluePrimaryDark,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "• Model: gemini-3.5-flash (Google DeepMind)\n• Secrets Management: Injected securely at build/runtime without exposure in frontend code\n• Local Data: Study plans, quiz records, and saved notes are persisted securely on your device with Room SQLite Database",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
