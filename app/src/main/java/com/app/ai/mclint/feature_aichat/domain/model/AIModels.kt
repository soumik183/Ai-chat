package com.app.ai.mclint.feature_aichat.domain.model

import kotlinx.serialization.Serializable

/**
 * Chat message
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val attachedFiles: List<String> = emptyList(),
    val isTyping: Boolean = false
)

/**
 * AI Provider type
 */
sealed class AIProvider {
    data class HuggingFace(
        val model: String = "mistralai/Mistral-7B-Instruct-v0.2",
        val apiKey: String
    ) : AIProvider()
    
    data class OpenRouter(
        val model: String = "openai/gpt-3.5-turbo",
        val apiKey: String
    ) : AIProvider()
}

/**
 * AI Request
 */
data class AIRequest(
    val prompt: String,
    val context: String? = null,
    val attachedFiles: List<String> = emptyList(),
    val provider: AIProvider
)

/**
 * AI Response
 */
data class AIResponse(
    val content: String,
    val provider: String,
    val model: String,
    val tokensUsed: Int? = null
)

/**
 * Parsed file command from AI
 */
data class FileCommand(
    val action: FileAction,
    val targetPath: String? = null,
    val destinationPath: String? = null,
    val content: String? = null,
    val lineNumber: Int? = null,
    val searchQuery: String? = null
)

/**
 * File action types
 */
enum class FileAction {
    CREATE_FILE,
    CREATE_FOLDER,
    DELETE,
    COPY,
    MOVE,
    RENAME,
    READ,
    WRITE,
    EDIT_LINE,
    SEARCH,
    LIST,
    BATCH_DELETE,
    BATCH_COPY,
    BATCH_MOVE
}

/**
 * Chat history entity for database
 */
@Serializable
data class ChatHistoryEntity(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val attachedFiles: String = ""
)
