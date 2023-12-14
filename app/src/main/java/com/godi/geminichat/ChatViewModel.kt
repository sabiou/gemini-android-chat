package com.godi.geminichat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    // set your API key here
    private val apiKey = BuildConfig.GOOGLE_AI_API_KEY

    private val generativeModel by lazy {
        GenerativeModel(
            // For text-only input, use the gemini-pro model
            modelName = "gemini-pro",
            apiKey = apiKey
        ).apply {
            startChat()
        }
    }

    val conversations = mutableStateListOf<Pair<String, String>>()

    fun sendMessage(prompt: String) {
        conversations.add(Pair("sent", prompt))
        conversations.add(Pair("received", ""))

        val inputContent = content {
            text(prompt)
        }

        viewModelScope.launch {
            generativeModel.generateContentStream(inputContent).collect { chunk ->
                conversations[conversations.lastIndex] = Pair(
                    "received",
                    conversations.last().second + chunk.text
                )
            }
        }
    }
}