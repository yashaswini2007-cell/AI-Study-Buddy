package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CodeText
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BluePrimary
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = BluePrimary
        )
    )
}

@Composable
fun StudyCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun StudyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    leadingIcon: ImageVector? = null,
    testTag: String = "input_field"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder, color = Color.Gray) },
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = BluePrimary) }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA),
                errorBorderColor = ErrorRed
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = ErrorRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String = "primary_action_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF93C5FD)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("AI is generating...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun FilterChipSelector(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.equals(selected, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) BluePrimary else Color(0xFFF1F5F9))
                    .clickable { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CodeSnippetCard(
    language: String,
    code: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CodeBackground,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.uppercase(),
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("code", code)
                            clipboard.setPrimaryClip(clip)
                            copied = true
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (copied) SuccessGreen else Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "Copied" else "Copy",
                        color = if (copied) SuccessGreen else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = code,
                    color = CodeText,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun FormattedStudyContent(
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val lines = content.split("\n")
        var inCodeBlock = false
        val currentCode = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    // End code block
                    CodeSnippetCard(
                        language = "code",
                        code = currentCode.toString().trim()
                    )
                    currentCode.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                }
                continue
            }

            if (inCodeBlock) {
                currentCode.append(line).append("\n")
                continue
            }

            when {
                trimmed.startsWith("# ") -> {
                    Text(
                        text = trimmed.removePrefix("# ").trim(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    )
                }
                trimmed.startsWith("## ") -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trimmed.removePrefix("## ").trim(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    )
                }
                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.removePrefix("### ").trim(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BluePrimary
                        )
                    )
                }
                trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ") -> {
                    val bulletText = trimmed.substring(2).trim()
                    Row(modifier = Modifier.padding(start = 6.dp)) {
                        Text("• ", color = BluePrimary, fontWeight = FontWeight.Bold)
                        Text(
                            text = bulletText.replace("**", ""),
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
                trimmed.isNotEmpty() -> {
                    Text(
                        text = trimmed.replace("**", ""),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        if (inCodeBlock && currentCode.isNotEmpty()) {
            CodeSnippetCard(
                language = "code",
                code = currentCode.toString().trim()
            )
        }
    }
}

@Composable
fun InfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BlueContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = BluePrimary,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ErrorContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
