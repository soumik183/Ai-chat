package com.app.ai.mclint.feature_editor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ai.mclint.feature_editor.domain.model.EditorState
import com.app.ai.mclint.feature_filemanager.domain.model.FileOperationResult
import com.app.ai.mclint.feature_filemanager.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for Code Editor screen
 */
@HiltViewModel
class CodeEditorViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorState())
    val uiState: StateFlow<EditorState> = _uiState.asStateFlow()

    /**
     * Load a file into the editor
     */
    fun loadFile(filePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = fileRepository.readText(filePath)) {
                is FileOperationResult.Success -> {
                    val content = result.data
                    _uiState.update { state ->
                        state.copy(
                            filePath = filePath,
                            fileName = File(filePath).name,
                            content = content,
                            originalContent = content,
                            isModified = false,
                            isLoading = false,
                            lineCount = content.lines().size,
                            undoStack = emptyList(),
                            redoStack = emptyList(),
                            canUndo = false,
                            canRedo = false
                        )
                    }
                }
                is FileOperationResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.message
                    )}
                }
            }
        }
    }

    /**
     * Update editor content
     */
    fun updateContent(newContent: String) {
        _uiState.update { state ->
            val newUndoStack = state.undoStack + state.content
            state.copy(
                content = newContent,
                isModified = newContent != state.originalContent,
                lineCount = newContent.lines().size,
                undoStack = newUndoStack.takeLast(50), // Keep last 50 states
                redoStack = emptyList(),
                canUndo = newUndoStack.isNotEmpty(),
                canRedo = false
            )
        }
    }

    /**
     * Update cursor position
     */
    fun updateCursor(line: Int, column: Int) {
        _uiState.update { it.copy(
            cursorLine = line,
            cursorColumn = column
        )}
    }

    /**
     * Undo last change
     */
    fun undo() {
        _uiState.update { state ->
            if (state.undoStack.isEmpty()) return@update state
            
            val previousContent = state.undoStack.last()
            val newUndoStack = state.undoStack.dropLast(1)
            val newRedoStack = state.redoStack + state.content
            
            state.copy(
                content = previousContent,
                isModified = previousContent != state.originalContent,
                lineCount = previousContent.lines().size,
                undoStack = newUndoStack,
                redoStack = newRedoStack,
                canUndo = newUndoStack.isNotEmpty(),
                canRedo = newRedoStack.isNotEmpty()
            )
        }
    }

    /**
     * Redo last undone change
     */
    fun redo() {
        _uiState.update { state ->
            if (state.redoStack.isEmpty()) return@update state
            
            val nextContent = state.redoStack.last()
            val newRedoStack = state.redoStack.dropLast(1)
            val newUndoStack = state.undoStack + state.content
            
            state.copy(
                content = nextContent,
                isModified = nextContent != state.originalContent,
                lineCount = nextContent.lines().size,
                undoStack = newUndoStack,
                redoStack = newRedoStack,
                canUndo = newUndoStack.isNotEmpty(),
                canRedo = newRedoStack.isNotEmpty()
            )
        }
    }

    /**
     * Save the current file
     */
    fun saveFile() {
        val filePath = _uiState.value.filePath ?: return
        
        viewModelScope.launch {
            when (val result = fileRepository.writeText(filePath, _uiState.value.content)) {
                is FileOperationResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            originalContent = state.content,
                            isModified = false
                        )
                    }
                }
                is FileOperationResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    /**
     * Save file as a new file
     */
    fun saveFileAs(newPath: String) {
        viewModelScope.launch {
            when (val result = fileRepository.writeText(newPath, _uiState.value.content)) {
                is FileOperationResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            filePath = newPath,
                            fileName = File(newPath).name,
                            originalContent = state.content,
                            isModified = false
                        )
                    }
                }
                is FileOperationResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    /**
     * Edit a specific line
     */
    fun editLine(lineNumber: Int, newContent: String) {
        _uiState.update { state ->
            val lines = state.content.lines().toMutableList()
            if (lineNumber in 1..lines.size) {
                lines[lineNumber - 1] = newContent
                val newContent = lines.joinToString("\n")
                
                state.copy(
                    content = newContent,
                    isModified = true,
                    lineCount = lines.size,
                    undoStack = state.undoStack + state.content,
                    redoStack = emptyList(),
                    canUndo = true,
                    canRedo = false
                )
            } else {
                state
            }
        }
    }

    /**
     * Insert a line at a specific position
     */
    fun insertLine(lineNumber: Int, content: String) {
        _uiState.update { state ->
            val lines = state.content.lines().toMutableList()
            val insertIndex = (lineNumber - 1).coerceIn(0, lines.size)
            lines.add(insertIndex, content)
            val newContent = lines.joinToString("\n")
            
            state.copy(
                content = newContent,
                isModified = true,
                lineCount = lines.size,
                undoStack = state.undoStack + state.content,
                redoStack = emptyList(),
                canUndo = true,
                canRedo = false
            )
        }
    }

    /**
     * Delete a specific line
     */
    fun deleteLine(lineNumber: Int) {
        _uiState.update { state ->
            val lines = state.content.lines().toMutableList()
            if (lineNumber in 1..lines.size) {
                lines.removeAt(lineNumber - 1)
                val newContent = lines.joinToString("\n")
                
                state.copy(
                    content = newContent,
                    isModified = true,
                    lineCount = lines.size,
                    undoStack = state.undoStack + state.content,
                    redoStack = emptyList(),
                    canUndo = true,
                    canRedo = false
                )
            } else {
                state
            }
        }
    }

    /**
     * Apply AI suggestion
     */
    fun applyAiSuggestion(suggestion: String) {
        // For now, just append the suggestion
        // In a real implementation, this would intelligently merge the suggestion
        updateContent(_uiState.value.content + "\n" + suggestion)
    }

    /**
     * Clear error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
