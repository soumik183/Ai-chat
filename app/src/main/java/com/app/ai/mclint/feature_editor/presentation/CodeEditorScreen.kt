package com.app.ai.mclint.feature_editor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.ai.mclint.R
import com.app.ai.mclint.core.theme.EditorBackground
import com.app.ai.mclint.core.theme.EditorLineNumber
import com.app.ai.mclint.feature_editor.domain.model.EditorState
import kotlinx.coroutines.launch

/**
 * Code Editor Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
    viewModel: CodeEditorViewModel = hiltViewModel(),
    filePath: String?,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var showAiAssist by remember { mutableStateOf(false) }
    
    // Load file when screen opens
    LaunchedEffect(filePath) {
        filePath?.let { viewModel.loadFile(it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.fileName ?: stringResource(R.string.editor_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.isModified) {
                            Text(
                                text = "Modified",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Undo
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = uiState.canUndo
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = stringResource(R.string.editor_undo))
                    }
                    
                    // Redo
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = uiState.canRedo
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = stringResource(R.string.editor_redo))
                    }
                    
                    // AI Assist
                    IconButton(onClick = { showAiAssist = true }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = stringResource(R.string.editor_ai_assist))
                    }
                    
                    // Save
                    IconButton(
                        onClick = { viewModel.saveFile() },
                        enabled = uiState.isModified
                    ) {
                        Icon(Icons.Default.Save, contentDescription = stringResource(R.string.editor_save))
                    }
                    
                    // Save As
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(Icons.Default.SaveAs, contentDescription = stringResource(R.string.editor_save_as))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.editor_line, uiState.cursorLine),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.editor_column, uiState.cursorColumn),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${uiState.lineCount} lines",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            CodeEditor(
                content = uiState.content,
                onContentChange = { viewModel.updateContent(it) },
                onCursorChange = { line, column -> viewModel.updateCursor(line, column) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
        
        // AI Assist Dialog
        if (showAiAssist) {
            AiAssistDialog(
                onDismiss = { showAiAssist = false },
                onApply = { suggestion ->
                    viewModel.applyAiSuggestion(suggestion)
                    showAiAssist = false
                }
            )
        }
        
        // Save As Dialog
        if (showSaveDialog) {
            SaveAsDialog(
                currentPath = uiState.filePath,
                onDismiss = { showSaveDialog = false },
                onSave = { path ->
                    viewModel.saveFileAs(path)
                    showSaveDialog = false
                }
            )
        }
    }
}

/**
 * Code Editor component with line numbers and syntax highlighting
 */
@Composable
private fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit,
    onCursorChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lines = content.split("\n")
    val scrollState = rememberScrollState()
    val lazyListState = rememberLazyListState()
    
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(EditorBackground)
    ) {
        // Line numbers
        Column(
            modifier = Modifier
                .width(50.dp)
                .fillMaxHeight()
                .horizontalScroll(scrollState)
        ) {
            lines.forEachIndexed { index, _ ->
                Text(
                    text = "${index + 1}",
                    color = EditorLineNumber,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        
        // Code content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // TODO: Implement proper code editor with syntax highlighting
            // For now, using a simple text field
            androidx.compose.foundation.text.BasicTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = Color.White
                ),
                onTextLayout = { textLayoutResult ->
                    // Calculate cursor position
                }
            )
        }
    }
}

/**
 * AI Assist Dialog
 */
@Composable
private fun AiAssistDialog(
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.editor_ai_assist)) },
        text = {
            Column {
                Text(
                    text = "Describe what you want the AI to do with your code:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("e.g., Add error handling to this function") },
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // TODO: Call AI API
                    onApply("// AI suggestion placeholder")
                },
                enabled = prompt.isNotBlank()
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

/**
 * Save As Dialog
 */
@Composable
private fun SaveAsDialog(
    currentPath: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var path by remember { mutableStateOf(currentPath ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.editor_save_as)) },
        text = {
            OutlinedTextField(
                value = path,
                onValueChange = { path = it },
                label = { Text("File path") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(path) },
                enabled = path.isNotBlank()
            ) {
                Text(stringResource(R.string.editor_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
