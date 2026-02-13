package com.app.ai.mclint.feature_editor.domain.model

/**
 * Editor state
 */
data class EditorState(
    val filePath: String? = null,
    val fileName: String? = null,
    val content: String = "",
    val originalContent: String = "",
    val isModified: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val cursorLine: Int = 1,
    val cursorColumn: Int = 1,
    val lineCount: Int = 0,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val undoStack: List<String> = emptyList(),
    val redoStack: List<String> = emptyList()
)

/**
 * Edit action for line-specific editing
 */
data class EditAction(
    val type: EditType,
    val lineNumber: Int,
    val content: String? = null
)

/**
 * Type of edit action
 */
enum class EditType {
    INSERT_LINE,
    DELETE_LINE,
    REPLACE_LINE,
    APPEND_TO_LINE
}
