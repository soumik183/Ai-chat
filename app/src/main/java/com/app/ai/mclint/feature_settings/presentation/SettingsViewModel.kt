package com.app.ai.mclint.feature_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ai.mclint.BuildConfig
import com.app.ai.mclint.core.theme.ThemeMode
import com.app.ai.mclint.core.util.Constants
import com.app.ai.mclint.feature_filemanager.domain.model.ViewMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Settings
 */
data class SettingsUiState(
    val huggingFaceApiKey: String? = null,
    val openRouterApiKey: String? = null,
    val defaultProvider: String = "huggingface",
    val defaultModel: String = Constants.DEFAULT_HUGGINGFACE_MODEL,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultView: ViewMode = ViewMode.LIST,
    val showHiddenFiles: Boolean = false,
    val version: String = "1.0.0"
)

/**
 * ViewModel for Settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    // TODO: Inject DataStore for persistence
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load settings from DataStore
     */
    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Load from DataStore
            _uiState.update { state ->
                state.copy(
                    version = BuildConfig.VERSION_NAME
                )
            }
        }
    }

    /**
     * Set HuggingFace API key
     */
    fun setHuggingFaceApiKey(key: String) {
        viewModelScope.launch {
            // TODO: Save to DataStore with encryption
            _uiState.update { it.copy(huggingFaceApiKey = key) }
        }
    }

    /**
     * Set OpenRouter API key
     */
    fun setOpenRouterApiKey(key: String) {
        viewModelScope.launch {
            // TODO: Save to DataStore with encryption
            _uiState.update { it.copy(openRouterApiKey = key) }
        }
    }

    /**
     * Set default AI provider
     */
    fun setDefaultProvider(provider: String) {
        viewModelScope.launch {
            // TODO: Save to DataStore
            _uiState.update { state ->
                state.copy(
                    defaultProvider = provider,
                    defaultModel = when (provider) {
                        "huggingface" -> Constants.DEFAULT_HUGGINGFACE_MODEL
                        "openrouter" -> Constants.DEFAULT_OPENROUTER_MODEL
                        else -> state.defaultModel
                    }
                )
            }
        }
    }

    /**
     * Set default model
     */
    fun setDefaultModel(model: String) {
        viewModelScope.launch {
            // TODO: Save to DataStore
            _uiState.update { it.copy(defaultModel = model) }
        }
    }

    /**
     * Set theme mode
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            // TODO: Save to DataStore
            _uiState.update { it.copy(themeMode = mode) }
        }
    }

    /**
     * Set default view mode
     */
    fun setDefaultView(viewMode: ViewMode) {
        viewModelScope.launch {
            // TODO: Save to DataStore
            _uiState.update { it.copy(defaultView = viewMode) }
        }
    }

    /**
     * Set show hidden files
     */
    fun setShowHiddenFiles(show: Boolean) {
        viewModelScope.launch {
            // TODO: Save to DataStore
            _uiState.update { it.copy(showHiddenFiles = show) }
        }
    }

    /**
     * Clear all API keys
     */
    fun clearApiKeys() {
        viewModelScope.launch {
            // TODO: Clear from DataStore
            _uiState.update { state ->
                state.copy(
                    huggingFaceApiKey = null,
                    openRouterApiKey = null
                )
            }
        }
    }
}
