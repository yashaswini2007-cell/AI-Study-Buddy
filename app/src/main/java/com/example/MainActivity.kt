package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProgrammingAssistantScreen
import com.example.ui.screens.QuizGeneratorScreen
import com.example.ui.screens.SavedNotesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartNotesScreen
import com.example.ui.screens.StudyPlannerScreen
import com.example.ui.screens.TopicExplainerScreen
import com.example.ui.screens.VivaQuestionsScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyBuddyViewModel

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudyBuddyApp()
            }
        }
    }
}

@Composable
fun StudyBuddyApp(
    viewModel: StudyBuddyViewModel = viewModel()
) {
    val currentRoute by viewModel.currentRoute.collectAsState()

    val navItems = listOf(
        BottomNavItem("dashboard", "Home", Icons.Default.Home, "nav_item_home"),
        BottomNavItem("topic_explainer", "Explain", Icons.AutoMirrored.Filled.MenuBook, "nav_item_explain"),
        BottomNavItem("smart_notes", "Notes", Icons.Default.Description, "nav_item_notes"),
        BottomNavItem("quiz_generator", "Quiz", Icons.Default.Quiz, "nav_item_quiz"),
        BottomNavItem("code_assistant", "Code", Icons.Default.Code, "nav_item_code"),
        BottomNavItem("study_planner", "Planner", Icons.Default.CalendarMonth, "nav_item_planner")
    )

    val showBottomBar = currentRoute in navItems.map { it.route }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = BluePrimary
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.route) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) BluePrimary else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BluePrimary else TextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFEFF6FF)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = currentRoute,
            modifier = Modifier.padding(innerPadding)
        ) { route ->
            when (route) {
                "dashboard" -> DashboardScreen(viewModel = viewModel)
                "topic_explainer" -> TopicExplainerScreen(viewModel = viewModel)
                "smart_notes" -> SmartNotesScreen(viewModel = viewModel)
                "viva_prep" -> VivaQuestionsScreen(viewModel = viewModel)
                "quiz_generator" -> QuizGeneratorScreen(viewModel = viewModel)
                "code_assistant" -> ProgrammingAssistantScreen(viewModel = viewModel)
                "study_planner" -> StudyPlannerScreen(viewModel = viewModel)
                "saved_notes" -> SavedNotesScreen(viewModel = viewModel)
                "settings" -> SettingsScreen(viewModel = viewModel)
                else -> DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
