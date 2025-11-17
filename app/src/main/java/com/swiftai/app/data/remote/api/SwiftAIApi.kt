package com.swiftai.app.data.remote.api

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
        .build()

    private val gson = Gson()
    private val baseUrl = "https://shawniii-swiftai-api.hf.space"

    suspend fun sendMessage(
        prompt: String,
        model: String = "swiftai-mini",
        maxLength: Int = 100
    ): Result<ChatResponse> = withContext(Dispatchers.IO) {
        try {
            val chatRequest = ChatRequest(prompt, model, maxLength)
            val jsonBody = gson.toJson(chatRequest)

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/api/chat")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val chatResponse = gson.fromJson(responseBody, ChatResponse::class.java)
                    Result.success(chatResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
