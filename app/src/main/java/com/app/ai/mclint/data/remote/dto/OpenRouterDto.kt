package com.app.ai.mclint.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenRouter Chat Completion Request
 */
@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val stream: Boolean? = false,
    val temperature: Double? = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int? = 1024,
    @SerialName("top_p")
    val topP: Double? = 0.95,
    @SerialName("top_k")
    val topK: Int? = 50,
    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = 0.0,
    @SerialName("presence_penalty")
    val presencePenalty: Double? = 0.0,
    val stop: List<String>? = null
)

/**
 * Message for OpenRouter request
 */
@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String,
    val name: String? = null
)

/**
 * OpenRouter Chat Completion Response
 */
@Serializable
data class OpenRouterResponse(
    val id: String,
    val choices: List<OpenRouterChoice>,
    val created: Long,
    val model: String,
    @SerialName("object")
    val objectType: String,
    val usage: OpenRouterUsage? = null
)

/**
 * Choice in OpenRouter response
 */
@Serializable
data class OpenRouterChoice(
    val index: Int,
    val message: OpenRouterMessageResponse,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

/**
 * Message in OpenRouter response
 */
@Serializable
data class OpenRouterMessageResponse(
    val role: String,
    val content: String
)

/**
 * Token usage information
 */
@Serializable
data class OpenRouterUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

/**
 * OpenRouter Error Response
 */
@Serializable
data class OpenRouterError(
    val error: OpenRouterErrorDetail
)

@Serializable
data class OpenRouterErrorDetail(
    val message: String,
    val type: String? = null,
    val code: String? = null
)
