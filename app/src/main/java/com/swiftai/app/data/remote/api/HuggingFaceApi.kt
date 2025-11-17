package com.swiftai.app.data.remote.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HuggingFaceApi @Inject constructor() {
    private val client = OkHttpClient()
    private val baseUrl = "https://shawniii-swiftai-api.hf.space"

    suspend fun generate(prompt: String, model: String, maxLength: Int = 100): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/api/chat"
            val json = JSONObject().apply {
                put("prompt", prompt)
                put("model", model)
                put("max_length", maxLength)
            }

            val request = Request.Builder()
                .url(url)
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (!response.isSuccessful) {
                Log.e("HuggingFaceApi", "HTTP error: ${response.code}")
                return@withContext Result.failure(Exception("Server error: ${response.code}"))
            }

            if (body.isNullOrBlank()) {
                Log.e("HuggingFaceApi", "Blank response!")
                return@withContext Result.failure(Exception("No response from Hugging Face backend"))
            }

            val result = JSONObject(body)
            val out = result.optString("response")

            if (out.isNotBlank()) {
                Result.success(out)
            } else {
                Result.failure(Exception("No data from Hugging Face API"))
            }
        } catch (e: Exception) {
            Log.e("HuggingFaceApi", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
