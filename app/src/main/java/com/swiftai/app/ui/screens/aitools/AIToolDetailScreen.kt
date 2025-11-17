package com.swiftai.app.ui.screens.aitools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftai.app.domain.model.AIModels
import com.swiftai.app.domain.model.AITools
import com.swiftai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolDetailScreen(
    navController: NavController,
    toolId: String,
    viewModel: AIToolViewModel = hiltViewModel()
) {
    val tool = AITools.getToolById(toolId)
    val model = tool?.let { AIModels.getModelById(it.modelId) }
    val uiState by viewModel.uiState.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var targetLanguage by remember { mutableStateOf("Spanish") }
    var sourceLanguage by remember { mutableStateOf("English") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (tool == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Tool not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = tool.name,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${model?.icon} ${model?.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding() // Fix keyboard overlap
        ) {
            // Result area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tool info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceVariantDark
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Purple.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = tool.icon,
                                        contentDescription = null,
                                        tint = Purple,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = tool.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Result display
                    if (uiState.result.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceVariantDark
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Result:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Purple
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = uiState.result,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    if (uiState.isLoading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceVariantDark
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Purple)
                            }
                        }
                    }

                    if (uiState.error != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // Input area - Fixed at bottom above keyboard
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Translation-specific language selector
                    if (toolId == "translation") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showLanguageDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = SurfaceVariantDark
                                )
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("From", style = MaterialTheme.typography.labelSmall)
                                    Text(sourceLanguage, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text("→", modifier = Modifier.align(Alignment.CenterVertically))

                            OutlinedButton(
                                onClick = { showLanguageDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = SurfaceVariantDark
                                )
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("To", style = MaterialTheme.typography.labelSmall)
                                    Text(targetLanguage, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(when (toolId) {
                                "translation" -> "Enter text to translate..."
                                "text_summarization" -> "Enter text to summarize..."
                                "grammar_check" -> "Enter text to check grammar..."
                                "code_assistant" -> "Describe what code you need..."
                                "code_review" -> "Paste your code here..."
                                "image_generation" -> "Describe the image you want..."
                                "creative_writing" -> "Give me a topic or prompt..."
                                else -> "Enter your input..."
                            })
                        },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceVariantDark,
                            unfocusedContainerColor = SurfaceVariantDark,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Button(
                        onClick = {
                            val finalPrompt = if (toolId == "translation") {
                                "$inputText|$sourceLanguage|$targetLanguage"
                            } else {
                                inputText
                            }
                            viewModel.processInput(toolId, finalPrompt, tool.modelId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = inputText.isNotBlank() && !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isLoading) "Processing..." else when (toolId) {
                                "translation" -> "Translate"
                                "text_summarization" -> "Summarize"
                                "grammar_check" -> "Check Grammar"
                                "code_assistant" -> "Generate Code"
                                "image_generation" -> "Generate Image"
                                else -> "Generate"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Language Selection Dialog for Translation
    if (showLanguageDialog) {
        val languages = listOf(
            "English", "Spanish", "French", "German", "Italian", "Portuguese",
            "Russian", "Chinese", "Japanese", "Korean", "Arabic", "Hindi",
            "Bengali", "Turkish", "Vietnamese", "Thai", "Dutch", "Swedish"
        )

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Languages") },
            text = {
                Column {
                    Text("Source Language:", fontWeight = FontWeight.Bold)
                    languages.forEach { lang ->
                        TextButton(
                            onClick = {
                                sourceLanguage = lang
                            }
                        ) {
                            Text(lang, color = if (lang == sourceLanguage) Purple else TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Target Language:", fontWeight = FontWeight.Bold)
                    languages.forEach { lang ->
                        TextButton(
                            onClick = {
                                targetLanguage = lang
                            }
                        ) {
                            Text(lang, color = if (lang == targetLanguage) Purple else TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}
