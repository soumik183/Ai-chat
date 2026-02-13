package com.app.ai.mclint.feature_aichat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ai.mclint.core.util.Constants
import com.app.ai.mclint.data.remote.HuggingFaceApi
import com.app.ai.mclint.data.remote.OpenRouterApi
import com.app.ai.mclint.data.remote.dto.HuggingFaceParameters
import com.app.ai.mclint.data.remote.dto.HuggingFaceRequest
import com.app.ai.mclint.data.remote.dto.OpenRouterMessage
import com.app.ai.mclint.data.remote.dto.OpenRouterRequest
import com.app.ai.mclint.feature_aichat.domain.model.ChatMessage
import com.app.ai.mclint.feature_filemanager.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * UI State for AI Chat
 */
data class AIChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val error: String? = null,
    val currentProvider: String = "huggingface",
    val currentModel: String = Constants.DEFAULT_HUGGINGFACE_MODEL,
    val huggingFaceApiKey: String? = null,
    val openRouterApiKey: String? = null
)

/**
 * ViewModel for AI Chat screen
 */
@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val huggingFaceApi: HuggingFaceApi,
    private val openRouterApi: OpenRouterApi,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    /**
     * Send a message to the AI
     */
    fun sendMessage(content: String, attachedFiles: List<String> = emptyList()) {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true,
            timestamp = System.currentTimeMillis(),
            attachedFiles = attachedFiles
        )
        
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isTyping = true,
                error = null
            )
        }
        
        viewModelScope.launch {
            try {
                val response = when (_uiState.value.currentProvider) {
                    "huggingface" -> sendToHuggingFace(content, attachedFiles)
                    "openrouter" -> sendToOpenRouter(content, attachedFiles)
                    else -> throw IllegalStateException("Unknown provider")
                }
                
                val aiMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = response,
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
                
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + aiMessage,
                        isTyping = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isTyping = false,
                        error = e.message ?: "Failed to get AI response"
                    )
                }
            }
        }
    }

    /**
     * Send request to HuggingFace API
     */
    private suspend fun sendToHuggingFace(prompt: String, attachedFiles: List<String>): String {
        val apiKey = _uiState.value.huggingFaceApiKey
            ?: throw IllegalStateException("HuggingFace API key not configured")
        
        // Build context from attached files
        val context = buildContextFromFiles(attachedFiles)
        val fullPrompt = if (context.isNotEmpty()) {
            "$context\n\nUser request: $prompt"
        } else {
            buildHuggingFacePrompt(prompt)
        }
        
        val request = HuggingFaceRequest(
            inputs = fullPrompt,
            parameters = HuggingFaceParameters(
                maxNewTokens = 1024,
                temperature = 0.7,
                returnFullText = false
            )
        )
        
        val response = huggingFaceApi.generateText(
            model = _uiState.value.currentModel,
            authorization = "Bearer $apiKey",
            request = request
        )
        
        if (response.isSuccessful) {
            return response.body()?.firstOrNull()?.generatedText 
                ?: "No response generated"
        } else {
            throw Exception("API error: ${response.code()} - ${response.message()}")
        }
    }

    /**
     * Send request to OpenRouter API
     */
    private suspend fun sendToOpenRouter(prompt: String, attachedFiles: List<String>): String {
        val apiKey = _uiState.value.openRouterApiKey
            ?: throw IllegalStateException("OpenRouter API key not configured")
        
        // Build context from attached files
        val context = buildContextFromFiles(attachedFiles)
        val systemPrompt = buildSystemPrompt()
        
        val messages = mutableListOf<OpenRouterMessage>()
        messages.add(OpenRouterMessage(role = "system", content = systemPrompt))
        
        if (context.isNotEmpty()) {
            messages.add(OpenRouterMessage(role = "user", content = "Context:\n$context"))
        }
        
        messages.add(OpenRouterMessage(role = "user", content = prompt))
        
        val request = OpenRouterRequest(
            model = _uiState.value.currentModel,
            messages = messages,
            temperature = 0.7
        )
        
        val response = openRouterApi.chatCompletion(
            authorization = "Bearer $apiKey",
            request = request
        )
        
        if (response.isSuccessful) {
            return response.body()?.choices?.firstOrNull()?.message?.content
                ?: "No response generated"
        } else {
            throw Exception("API error: ${response.code()} - ${response.message()}")
        }
    }

    /**
     * Build context from attached files
     */
    private suspend fun buildContextFromFiles(filePaths: List<String>): String {
        if (filePaths.isEmpty()) return ""
        
        val contextBuilder = StringBuilder()
        
        filePaths.forEach { path ->
            val result = fileRepository.readText(path)
            if (result is com.app.ai.mclint.feature_filemanager.domain.model.FileOperationResult.Success) {
                contextBuilder.append("File: $path\n")
                contextBuilder.append("```\n${result.data}\n```\n\n")
            }
        }
        
        return contextBuilder.toString()
    }

    /**
     * Build system prompt for file operations
     */
    private fun buildSystemPrompt(): String {
        return """
            You are an AI file management assistant. You help users manage their files through natural language commands.
            
            You can understand and execute the following types of commands:
            - Create files and folders
            - Delete files and folders
            - Copy and move files
            - Rename files
            - Search for files
            - Read and edit file contents
            - Edit specific lines in code files
            
            When a user asks you to perform a file operation, respond with:
            1. A confirmation of what you will do
            2. The result of the operation
            
            Always be helpful and provide clear feedback about file operations.
        """.trimIndent()
    }

    /**
     * Build prompt for HuggingFace models
     */
    private fun buildHuggingFacePrompt(userPrompt: String): String {
        return """
            <s>[INST] You are an AI file management assistant. Help the user with their file operations.
            
            User: $userPrompt [/INST]
        """.trimIndent()
    }

    /**
     * Retry sending a message
     */
    fun retryMessage(message: ChatMessage) {
        // Remove messages after the retry point
        val messageIndex = _uiState.value.messages.indexOf(message)
        if (messageIndex >= 0) {
            _uiState.update { state ->
                state.copy(
                    messages = state.messages.take(messageIndex)
                )
            }
            sendMessage(message.content, message.attachedFiles)
        }
    }

    /**
     * Set the AI provider
     */
    fun setProvider(provider: String) {
        _uiState.update { state ->
            state.copy(
                currentProvider = provider,
                currentModel = when (provider) {
                    "huggingface" -> Constants.DEFAULT_HUGGINGFACE_MODEL
                    "openrouter" -> Constants.DEFAULT_OPENROUTER_MODEL
                    else -> state.currentModel
                }
            )
        }
    }

    /**
     * Set the model
     */
    fun setModel(model: String) {
        _uiState.update { it.copy(currentModel = model) }
    }

    /**
     * Set API keys
     */
    fun setApiKeys(huggingFaceKey: String?, openRouterKey: String?) {
        _uiState.update { state ->
            state.copy(
                huggingFaceApiKey = huggingFaceKey,
                openRouterApiKey = openRouterKey
            )
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Clear chat history
     */
    fun clearHistory() {
        _uiState.update { it.copy(messages = emptyList()) }
    }
}
