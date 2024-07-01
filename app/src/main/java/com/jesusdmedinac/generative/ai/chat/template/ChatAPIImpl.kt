package com.jesusdmedinac.generative.ai.chat.template

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class ChatAPIImpl {
    private val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with most use cases
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.apiKey,
        systemInstruction = content { text("") },
    )

    suspend fun sendMessage(messages: List<Message>): Message {
        val history = messages
            .map { message -> content(role = message.author) { text(message.body) } }
            .dropLast(1)
        val chat = generativeModel.startChat(
            history = history
        )
        val response = chat.sendMessage(
            messages.lastOrNull()?.body ?: "Empty message"
        )

        return Message(
            id = messages.size + 1,
            author = "model",
            body = response.text ?: "Empty response"
        )
    }
}