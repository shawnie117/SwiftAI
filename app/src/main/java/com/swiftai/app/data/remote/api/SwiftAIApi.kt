package com.swiftai.app.data.remote.api

import android.util.Log
import com.google.gson.Gson
import com.swiftai.app.data.remote.dto.ChatRequest
import com.swiftai.app.data.remote.dto.ChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SwiftAIApi @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val gson = Gson()

    // Base URLs for different models (you can change these later)
    private val modelUrls = mapOf(
        "swiftai-mini" to "https://shawniii-swiftai-api.hf.space",
        "swiftai-standard" to "https://shawniii-swiftai-api.hf.space",
        "swiftai-pro" to "https://shawniii-swiftai-api.hf.space",
        "swiftai-max" to "https://shawniii-swiftai-api.hf.space"
    )

    // Default base URL
    private val defaultBaseUrl = "https://shawniii-swiftai-api.hf.space"

    /**
     * Send a message to the AI and get a response
     * @param prompt The user's message/prompt
     * @param model The AI model to use (swiftai-mini, swiftai-standard, swiftai-pro, swiftai-max)
     * @param maxLength Maximum length of the response
     * @param chatId The chat session ID (null for new chat)
     * @return Result containing ChatResponse or error
     */
    suspend fun sendMessage(
        prompt: String,
        model: String = "swiftai-mini",
        maxLength: Int = 100,
        chatId: String? = null
    ): Result<ChatResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("SwiftAIApi", "Sending message - Model: $model, MaxLength: $maxLength, ChatId: $chatId")

            // Get the appropriate base URL for the model
            val baseUrl = modelUrls[model] ?: defaultBaseUrl

            // Create request object
            val chatRequest = ChatRequest(
                prompt = prompt,
                model = model,
                max_length = maxLength,
                chat_id = chatId
            )

            val jsonBody = gson.toJson(chatRequest)
            Log.d("SwiftAIApi", "Request body: $jsonBody")

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/api/chat")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            Log.d("SwiftAIApi", "Sending request to: $baseUrl/api/chat")

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("SwiftAIApi", "Response received: $responseBody")

                if (responseBody != null) {
                    val chatResponse = gson.fromJson(responseBody, ChatResponse::class.java)
                    Result.success(chatResponse)
                } else {
                    Log.e("SwiftAIApi", "Empty response body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.body?.string() ?: "No error details"
                Log.e("SwiftAIApi", "API Error: ${response.code} - ${response.message} - $errorBody")
                Result.failure(Exception("API Error: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e("SwiftAIApi", "Exception in sendMessage: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get chat history for a specific chat session
     * @param chatId The chat session ID
     * @return Result containing list of messages or error
     */
    suspend fun getHistory(chatId: String): Result<List<ChatResponse>> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = defaultBaseUrl
            val request = Request.Builder()
                .url("$baseUrl/api/chat/history/$chatId")
                .get()
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val history = gson.fromJson(responseBody, Array<ChatResponse>::class.java).toList()
                    Result.success(history)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Failed to fetch history"))
            }
        } catch (e: Exception) {
            Log.e("SwiftAIApi", "Error fetching history: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Get available models (for future use)
     */
    suspend fun getAvailableModels(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$defaultBaseUrl/api/models")
                .get()
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val models = gson.fromJson(responseBody, Array<String>::class.java).toList()
                    Result.success(models)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Failed to fetch models"))
            }
        } catch (e: Exception) {
            Log.e("SwiftAIApi", "Error fetching models: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Check API health status
     */
    suspend fun checkHealth(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$defaultBaseUrl/health")
                .get()
                .build()

            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Log.e("SwiftAIApi", "Health check failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Stream response (for future implementation)
     * This can be used for real-time streaming responses
     */
    suspend fun streamMessage(
        prompt: String,
        model: String = "swiftai-mini",
        maxLength: Int = 100,
        onChunk: (String) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = modelUrls[model] ?: defaultBaseUrl

            val chatRequest = ChatRequest(
                prompt = prompt,
                model = model,
                max_length = maxLength
            )

            val jsonBody = gson.toJson(chatRequest)
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/api/stream")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "text/event-stream")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.charStream()?.use { reader ->
                    reader.forEachLine { line ->
                        if (line.startsWith("data: ")) {
                            val data = line.substring(6)
                            onChunk(data)
                        }
                    }
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Stream failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("SwiftAIApi", "Stream error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Test connection to API
     */
    suspend fun testConnection(model: String = "swiftai-mini"): Result<String> {
        return try {
            val testPrompt = "Hello, this is a connection test."
            val result = sendMessage(testPrompt, model, 50)

            if (result.isSuccess) {
                Result.success("Connection successful!")
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Change model URL dynamically (useful for switching between different API endpoints)
     */
    fun updateModelUrl(model: String, newUrl: String) {
        (modelUrls as MutableMap)[model] = newUrl
        Log.d("SwiftAIApi", "Updated $model URL to: $newUrl")
    }

    /**
     * Get current model URL
     */
    fun getModelUrl(model: String): String {
        return modelUrls[model] ?: defaultBaseUrl
    }

    /**
     * Batch message processing (for multiple prompts)
     */
    suspend fun sendBatchMessages(
        prompts: List<String>,
        model: String = "swiftai-mini",
        maxLength: Int = 100
    ): Result<List<ChatResponse>> = withContext(Dispatchers.IO) {
        try {
            val responses = mutableListOf<ChatResponse>()

            prompts.forEach { prompt ->
                val result = sendMessage(prompt, model, maxLength)
                if (result.isSuccess) {
                    result.getOrNull()?.let { responses.add(it) }
                } else {
                    Log.e("SwiftAIApi", "Batch message failed for prompt: $prompt")
                }
            }

            Result.success(responses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
