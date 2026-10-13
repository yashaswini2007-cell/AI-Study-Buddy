package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StudyNote
import com.example.ui.components.FormattedStudyContent
import com.example.ui.components.PrimaryActionButton
import com.example.ui.components.StudyTopAppBar
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyBuddyViewModel

@Composable
fun SavedNotesScreen(
    viewModel: StudyBuddyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo("dashboard")
    }

    val savedNotes by viewModel.savedNotes.collectAsState()

    Scaffold(
        topBar = {
            StudyTopAppBar(
                title = "My Saved Notes",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateTo("dashboard") }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (savedNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(BlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Saved Notes Yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generate study notes or revision sheets in 'Smart Notes' and click 'Save Note' to store them in your offline notebook.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryActionButton(
                        text = "Go to Smart Notes Generator",
                        onClick = { viewModel.navigateTo("smart_notes") }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Saved Notebook (${savedNotes.size} Notes)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    )
                }

                items(savedNotes.size) { index ->
                    val note = savedNotes[index]
                    SavedNoteCard(
                        note = note,
                        onDelete = { viewModel.deleteSavedNote(note) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SavedNoteCard(
    note: StudyNote,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BlueContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = note.noteType,
                                color = BluePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = note.subject,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = note.topic,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Row {
                    // Copy
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("note", note.content)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Note copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = BluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Expand/Collapse
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Expanded Note Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    FormattedStudyContent(content = note.content)
                }
            }
        }
    }
}
