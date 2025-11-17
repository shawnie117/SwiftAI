package com.swiftai.app.ui.screens.aitools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftai.app.data.remote.api.HuggingFaceApi
import com.swiftai.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolDetailScreen(
    navController: NavController,
    toolId: String,
    viewModel: AIToolDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Tool",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Tool ID: $toolId",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = viewModel::updateInputText,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter your prompt") },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceVariantDark,
                    unfocusedContainerColor = SurfaceVariantDark
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.generate(uiState.inputText, toolId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.inputText.isNotBlank() && !uiState.isLoading
            ) {
                Text("Generate")
            }

            if (uiState.output.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceVariantDark
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Result:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.output,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@HiltViewModel
class AIToolDetailViewModel @Inject constructor(
    private val huggingFaceApi: HuggingFaceApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIToolDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun generate(prompt: String, model: String) {
        viewModelScope.launch {
            val result = huggingFaceApi.generate(prompt, model)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(output = result.getOrNull() ?: "Generated")
            } else {
                _uiState.value = _uiState.value.copy(output = "Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}

data class AIToolDetailUiState(
    val inputText: String = "",
    val output: String = "",
    val isLoading: Boolean = false
)
