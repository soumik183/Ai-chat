package com.app.ai.mclint.feature_filemanager.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.ai.mclint.R
import com.app.ai.mclint.core.theme.*
import com.app.ai.mclint.feature_filemanager.domain.model.FileItem
import com.app.ai.mclint.feature_filemanager.domain.model.SortOption
import com.app.ai.mclint.feature_filemanager.domain.model.ViewMode

/**
 * File Manager Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel = hiltViewModel(),
    initialPath: String? = null,
    onFileClick: (FileItem) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Load initial path if provided
    LaunchedEffect(initialPath) {
        initialPath?.let { viewModel.navigateToDirectory(it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.isSearchMode) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it
                                viewModel.searchFiles(it)
                            },
                            placeholder = { Text(stringResource(R.string.search_files)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true
                        )
                    } else {
                        Column {
                            Text(
                                text = stringResource(R.string.file_manager_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = uiState.currentPath,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Search
                    IconButton(onClick = { 
                        if (uiState.isSearchMode) {
                            viewModel.clearSearch()
                            searchQuery = ""
                        }
                        showSearchBar = !showSearchBar
                    }) {
                        Icon(
                            imageVector = if (uiState.isSearchMode) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_files)
                        )
                    }
                    
                    // Sort
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.setSortOption(option)
                                    showSortMenu = false
                                },
                                trailingIcon = {
                                    if (uiState.sortOption == option) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                    
                    // View mode
                    IconButton(onClick = { 
                        viewModel.setViewMode(
                            if (uiState.viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                        )
                    }) {
                        Icon(
                            imageVector = if (uiState.viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = "Toggle view"
                        )
                    }
                    
                    // Settings
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Selection mode toolbar
            AnimatedVisibility(
                visible = uiState.selectionState.isSelectionMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SelectionToolbar(
                    selectedCount = uiState.selectionState.selectedFiles.size,
                    onSelectAll = { viewModel.selectAll() },
                    onDelete = { /* Show delete confirmation */ },
                    onCopy = { /* Show copy dialog */ },
                    onMove = { /* Show move dialog */ },
                    onClear = { viewModel.clearSelection() }
                )
            }
            
            // Breadcrumb navigation
            if (!uiState.isSearchMode) {
                BreadcrumbNavigation(
                    currentPath = uiState.currentPath,
                    onNavigate = { viewModel.navigateToDirectory(it) }
                )
            }
            
            // File list
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.refreshCurrentDirectory() }
                    )
                }
                uiState.isSearchMode -> {
                    if (uiState.searchResults.isEmpty()) {
                        EmptyView(message = stringResource(R.string.empty_search))
                    } else {
                        FileList(
                            files = uiState.searchResults,
                            viewMode = uiState.viewMode,
                            selectionState = uiState.selectionState,
                            onFileClick = onFileClick,
                            onFileLongClick = { viewModel.toggleFileSelection(it) },
                            onToggleSelection = { viewModel.toggleFileSelection(it) }
                        )
                    }
                }
                uiState.files.isEmpty() -> {
                    EmptyView(message = stringResource(R.string.empty_folder))
                }
                else -> {
                    FileList(
                        files = uiState.files,
                        viewMode = uiState.viewMode,
                        selectionState = uiState.selectionState,
                        onFileClick = onFileClick,
                        onFileLongClick = { viewModel.toggleFileSelection(it) },
                        onToggleSelection = { viewModel.toggleFileSelection(it) }
                    )
                }
            }
        }
        
        // Create dialog
        if (showCreateDialog) {
            CreateFileDialog(
                onDismiss = { showCreateDialog = false },
                onCreateFile = { name ->
                    viewModel.createFile(name) { success, _ ->
                        if (success) showCreateDialog = false
                    }
                },
                onCreateFolder = { name ->
                    viewModel.createDirectory(name) { success, _ ->
                        if (success) showCreateDialog = false
                    }
                }
            )
        }
    }
}

/**
 * Selection mode toolbar
 */
@Composable
private fun SelectionToolbar(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Row {
                IconButton(onClick = onSelectAll) {
                    Icon(Icons.Default.SelectAll, contentDescription = "Select all")
                }
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                }
                IconButton(onClick = onMove) {
                    Icon(Icons.Default.DriveFileMove, contentDescription = "Move")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

/**
 * Breadcrumb navigation
 */
@Composable
private fun BreadcrumbNavigation(
    currentPath: String,
    onNavigate: (String) -> Unit
) {
    val segments = currentPath.split("/").filter { it.isNotEmpty() }
    var accumulatedPath = ""
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            AssistChip(
                onClick = { onNavigate("/") },
                label = { Text("Root") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Storage,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
        
        items(segments) { segment ->
            accumulatedPath += "/$segment"
            val path = accumulatedPath
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AssistChip(
                    onClick = { onNavigate(path) },
                    label = { Text(segment) }
                )
            }
        }
    }
}

/**
 * File list
 */
@Composable
private fun FileList(
    files: List<FileItem>,
    viewMode: ViewMode,
    selectionState: com.app.ai.mclint.feature_filemanager.domain.model.SelectionState,
    onFileClick: (FileItem) -> Unit,
    onFileLongClick: (FileItem) -> Unit,
    onToggleSelection: (FileItem) -> Unit
) {
    if (viewMode == ViewMode.LIST) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(files, key = { it.id }) { file ->
                FileListItem(
                    file = file,
                    isSelected = selectionState.selectedFiles.contains(file),
                    onClick = {
                        if (selectionState.isSelectionMode) {
                            onToggleSelection(file)
                        } else {
                            onFileClick(file)
                        }
                    },
                    onLongClick = { onFileLongClick(file) }
                )
            }
        }
    } else {
        // Grid view
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            files.chunked(3).forEach { rowFiles ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowFiles.forEach { file ->
                            Box(modifier = Modifier.weight(1f)) {
                                FileGridItem(
                                    file = file,
                                    isSelected = selectionState.selectedFiles.contains(file),
                                    onClick = {
                                        if (selectionState.isSelectionMode) {
                                            onToggleSelection(file)
                                        } else {
                                            onFileClick(file)
                                        }
                                    },
                                    onLongClick = { onFileLongClick(file) }
                                )
                            }
                        }
                        // Fill remaining space
                        repeat(3 - rowFiles.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * File list item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileListItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val fileColor = getFileColor(file)
    
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .then(
                if (isSelected) {
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier
                }
            ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(fileColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getFileIcon(file),
                    contentDescription = null,
                    tint = fileColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        headlineContent = {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = if (file.isDirectory) {
                    "Folder"
                } else {
                    file.getFormattedSize()
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

/**
 * File grid item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileGridItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val fileColor = getFileColor(file)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(fileColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getFileIcon(file),
                    contentDescription = null,
                    tint = fileColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Empty view
 */
@Composable
private fun EmptyView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error view
 */
@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Create file dialog
 */
@Composable
private fun CreateFileDialog(
    onDismiss: () -> Unit,
    onCreateFile: (String) -> Unit,
    onCreateFolder: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isFolder by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isFolder) stringResource(R.string.dialog_create_folder_title) else stringResource(R.string.dialog_create_file_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isFolder,
                        onClick = { isFolder = false },
                        label = { Text("File") }
                    )
                    FilterChip(
                        selected = isFolder,
                        onClick = { isFolder = true },
                        label = { Text("Folder") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        if (isFolder) {
                            onCreateFolder(name)
                        } else {
                            onCreateFile(name)
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.dialog_confirm))
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
 * Get icon for file type
 */
@Composable
private fun getFileIcon(file: FileItem): ImageVector {
    return when (file.getFileType()) {
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_FOLDER -> Icons.Default.Folder
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_CODE -> Icons.Default.Code
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_IMAGE -> Icons.Default.Image
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_VIDEO -> Icons.Default.VideoFile
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_AUDIO -> Icons.Default.AudioFile
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_ARCHIVE -> Icons.Default.Archive
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_DOCUMENT -> Icons.Default.Description
        else -> Icons.Default.InsertDriveFile
    }
}

/**
 * Get color for file type
 */
@Composable
private fun getFileColor(file: FileItem): androidx.compose.ui.graphics.Color {
    return when (file.getFileType()) {
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_FOLDER -> FileFolder
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_CODE -> FileCode
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_IMAGE -> FileImage
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_VIDEO -> FileVideo
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_AUDIO -> FileAudio
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_ARCHIVE -> FileArchive
        com.app.ai.mclint.core.util.Constants.FILE_TYPE_DOCUMENT -> FileDocument
        else -> FileOther
    }
}
