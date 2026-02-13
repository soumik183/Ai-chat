package com.app.ai.mclint.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * HuggingFace API Request
 */
@Serializable
data class HuggingFaceRequest(
    val inputs: String,
    val parameters: HuggingFaceParameters? = null,
    val options: HuggingFaceOptions? = null
)

/**
 * Parameters for HuggingFace text generation
 */
@Serializable
data class HuggingFaceParameters(
    @SerialName("max_new_tokens")
    val maxNewTokens: Int? = 1024,
    @SerialName("max_length")
    val maxLength: Int? = null,
    @SerialName("min_length")
    val minLength: Int? = null,
    @SerialName("temperature")
    val temperature: Double? = 0.7,
    @SerialName("top_p")
    val topP: Double? = 0.95,
    @SerialName("top_k")
    val topK: Int? = 50,
    @SerialName("do_sample")
    val doSample: Boolean? = true,
    @SerialName("repetition_penalty")
    val repetitionPenalty: Double? = 1.1,
    @SerialName("return_full_text")
    val returnFullText: Boolean? = false,
    val stop: List<String>? = null
)

/**
 * Options for HuggingFace API
 */
@Serializable
data class HuggingFaceOptions(
    @SerialName("wait_for_model")
    val waitForModel: Boolean? = true,
    @SerialName("use_cache")
    val useCache: Boolean? = true
)

/**
 * HuggingFace API Response
 */
@Serializable
data class HuggingFaceResponse(
    @SerialName("generated_text")
    val generatedText: String,
    @SerialName("token")
    val token: HuggingFaceToken? = null
)

/**
 * Token information in HuggingFace response
 */
@Serializable
data class HuggingFaceToken(
    val id: Int,
    val text: String,
    val logprob: Double? = null,
    val special: Boolean? = null
)

/**
 * HuggingFace Error Response
 */
@Serializable
data class HuggingFaceError(
    val error: String,
    @SerialName("estimated_time")
    val estimatedTime: Double? = null
)
