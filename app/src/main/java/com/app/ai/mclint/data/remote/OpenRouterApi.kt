package com.app.ai.mclint.data.remote

import com.app.ai.mclint.data.remote.dto.OpenRouterRequest
import com.app.ai.mclint.data.remote.dto.OpenRouterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * OpenRouter API interface
 * OpenRouter provides unified access to multiple LLM providers
 */
interface OpenRouterApi {
    
    /**
     * Send a chat completion request
     * 
     * @param authorization Bearer token with API key
     * @param httpReferer Optional HTTP referer for rankings
     * @param xTitle Optional app name for rankings
     * @param request The chat completion request
     * @return OpenRouterResponse with generated content
     */
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") httpReferer: String? = null,
        @Header("X-Title") xTitle: String? = null,
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>
}
