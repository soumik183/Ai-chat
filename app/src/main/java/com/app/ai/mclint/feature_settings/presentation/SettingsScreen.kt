package com.app.ai.mclint.feature_settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.ai.mclint.R
import com.app.ai.mclint.core.theme.ThemeMode
import com.app.ai.mclint.feature_filemanager.domain.model.ViewMode

/**
 * Settings Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showApiKeyDialog by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // AI Configuration Section
            SettingsSection(title = stringResource(R.string.settings_ai)) {
                // HuggingFace API Key
                SettingsItem(
                    title = stringResource(R.string.settings_huggingface_key),
                    subtitle = if (uiState.huggingFaceApiKey != null) "••••••••" else "Not set",
                    icon = Icons.Outlined.Key,
                    onClick = { showApiKeyDialog = "huggingface" }
                )
                
                HorizontalDivider()
                
                // OpenRouter API Key
                SettingsItem(
                    title = stringResource(R.string.settings_openrouter_key),
                    subtitle = if (uiState.openRouterApiKey != null) "••••••••" else "Not set",
                    icon = Icons.Outlined.Key,
                    onClick = { showApiKeyDialog = "openrouter" }
                )
                
                HorizontalDivider()
                
                // Default Provider
                var showProviderMenu by remember { mutableStateOf(false) }
                SettingsItem(
                    title = stringResource(R.string.settings_default_provider),
                    subtitle = uiState.defaultProvider,
                    icon = Icons.Outlined.Cloud,
                    onClick = { showProviderMenu = true }
                )
                
                DropdownMenu(
                    expanded = showProviderMenu,
                    onDismissRequest = { showProviderMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("HuggingFace") },
                        onClick = {
                            viewModel.setDefaultProvider("huggingface")
                            showProviderMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("OpenRouter") },
                        onClick = {
                            viewModel.setDefaultProvider("openrouter")
                            showProviderMenu = false
                        }
                    )
                }
                
                HorizontalDivider()
                
                // Default Model
                SettingsItem(
                    title = stringResource(R.string.settings_default_model),
                    subtitle = uiState.defaultModel,
                    icon = Icons.Outlined.Memory,
                    onClick = { /* Show model selection dialog */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Appearance Section
            SettingsSection(title = stringResource(R.string.settings_appearance)) {
                // Theme
                var showThemeMenu by remember { mutableStateOf(false) }
                SettingsItem(
                    title = stringResource(R.string.settings_theme),
                    subtitle = when (uiState.themeMode) {
                        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
                        ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
                        ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_system)
                    },
                    icon = Icons.Outlined.Palette,
                    onClick = { showThemeMenu = true }
                )
                
                DropdownMenu(
                    expanded = showThemeMenu,
                    onDismissRequest = { showThemeMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_theme_light)) },
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.LIGHT)
                            showThemeMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_theme_dark)) },
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.DARK)
                            showThemeMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_theme_system)) },
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.SYSTEM)
                            showThemeMenu = false
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // File Manager Section
            SettingsSection(title = stringResource(R.string.settings_file_manager)) {
                // Default View
                var showViewMenu by remember { mutableStateOf(false) }
                SettingsItem(
                    title = stringResource(R.string.settings_default_view),
                    subtitle = when (uiState.defaultView) {
                        ViewMode.LIST -> "List"
                        ViewMode.GRID -> "Grid"
                    },
                    icon = Icons.Outlined.ViewList,
                    onClick = { showViewMenu = true }
                )
                
                DropdownMenu(
                    expanded = showViewMenu,
                    onDismissRequest = { showViewMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("List") },
                        onClick = {
                            viewModel.setDefaultView(ViewMode.LIST)
                            showViewMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Grid") },
                        onClick = {
                            viewModel.setDefaultView(ViewMode.GRID)
                            showViewMenu = false
                        }
                    )
                }
                
                HorizontalDivider()
                
                // Show Hidden Files
                SettingsItem(
                    title = stringResource(R.string.settings_show_hidden),
                    icon = Icons.Outlined.Visibility,
                    trailing = {
                        Switch(
                            checked = uiState.showHiddenFiles,
                            onCheckedChange = { viewModel.setShowHiddenFiles(it) }
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // About Section
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingsItem(
                    title = stringResource(R.string.settings_version),
                    subtitle = uiState.version,
                    icon = Icons.Outlined.Info,
                    onClick = { }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // API Key Dialog
        showApiKeyDialog?.let { provider ->
            ApiKeyDialog(
                provider = provider,
                currentKey = if (provider == "huggingface") {
                    uiState.huggingFaceApiKey ?: ""
                } else {
                    uiState.openRouterApiKey ?: ""
                },
                onDismiss = { showApiKeyDialog = null },
                onSave = { key ->
                    if (provider == "huggingface") {
                        viewModel.setHuggingFaceApiKey(key)
                    } else {
                        viewModel.setOpenRouterApiKey(key)
                    }
                    showApiKeyDialog = null
                }
            )
        }
    }
}

/**
 * Settings section
 */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(content = content)
        }
    }
}

/**
 * Settings item
 */
@Composable
private fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * API Key Dialog
 */
@Composable
private fun ApiKeyDialog(
    provider: String,
    currentKey: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var key by remember { mutableStateOf(currentKey) }
    var showKey by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (provider == "huggingface") {
                    stringResource(R.string.settings_huggingface_key)
                } else {
                    stringResource(R.string.settings_openrouter_key)
                }
            )
        },
        text = {
            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("API Key") },
                singleLine = true,
                visualTransformation = if (showKey) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { showKey = !showKey }) {
                        Icon(
                            imageVector = if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showKey) "Hide" else "Show"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(key) },
                enabled = key.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
