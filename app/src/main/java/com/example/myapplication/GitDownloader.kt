package com.example.myapplication
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class GitDownloader {

    private val githubToken = BuildConfig.GITHUB_TOKEN
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer $githubToken")
                .header("User-Agent", "AndroidApp/1.0")
                .build()
            chain.proceed(request)
        }
        .build()
    suspend fun loadTextFile(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).get().build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                Result.success(body)
            }
            else {
                Result.failure(IOException("Ошибка Загрузки"))
            }
        } catch (_: Exception) {
            Result.failure(IOException("Ошибка Загрузки"))
        }
    }
    suspend fun loadBooks(): Result<List<ImageItem>> = withContext(Dispatchers.IO) {
        try {
            val url = "https://raw.githubusercontent.com/Vladimir-Savvateev/books/refs/heads/main/books.json"
            val jsonString = loadTextFile(url).getOrNull()
            if (jsonString != null) {
                val response = Gson().fromJson(jsonString, Books::class.java)
                Result.success(response.books)
            } else {
                Result.failure(IOException("Ошибка Загрузки"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun uploadFile(username: String, fileName: String, contentToUpload: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val content = Base64.encodeToString(contentToUpload.toByteArray(), Base64.NO_WRAP)
            val url = "https://api.github.com/repos/Vladimir-Savvateev/UserDataForMyBooks/contents/$username/$fileName"
            val getResponse = client.newCall(Request.Builder().url(url).get().build()).execute()
            val sha = if (getResponse.isSuccessful) {
                getResponse.body?.string()?.substringAfter("\"sha\":\"")?.substringBefore("\"")
            } else {
                null
            }
            val json =   if(sha == null) {
                """
            {
                "message": "Получены данные от пользователя",
                "content": "$content",
                "branch": "main"
            }
        """.trimIndent()
            }
            else {
                """
            {
            "message": "Получены данные от пользователя",
            "content": "$content",
            "sha": "$sha",
            "branch": "main"
            }   
        """.trimIndent()
            }

            val request = Request.Builder()
                .url(url)
                .put(json.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful

        } catch (_: Exception) {
            false
        }
    }
}