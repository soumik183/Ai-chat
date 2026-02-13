package com.app.ai.mclint.data.remote

import com.app.ai.mclint.data.remote.dto.HuggingFaceRequest
import com.app.ai.mclint.data.remote.dto.HuggingFaceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * HuggingFace Inference API interface
 */
interface HuggingFaceApi {
    
    /**
     * Send a text generation request to a HuggingFace model
     * 
     * @param model The model ID (e.g., "mistralai/Mistral-7B-Instruct-v0.2")
     * @param authorization Bearer token with API key
     * @param request The request body containing inputs and parameters
     * @return HuggingFaceResponse with generated text
     */
    @POST("models/{model}")
    suspend fun generateText(
        @Path("model") model: String,
        @Header("Authorization") authorization: String,
        @Body request: HuggingFaceRequest
    ): Response<List<HuggingFaceResponse>>
    
    /**
     * Send a conversational request to a HuggingFace model
     */
    @POST("models/{model}")
    suspend fun conversation(
        @Path("model") model: String,
        @Header("Authorization") authorization: String,
        @Body request: HuggingFaceRequest
    ): Response<HuggingFaceResponse>
}
