package com.app.ai.mclint.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.ai.mclint.feature_aichat.presentation.AIChatScreen
import com.app.ai.mclint.feature_editor.presentation.CodeEditorScreen
import com.app.ai.mclint.feature_filemanager.presentation.FileManagerScreen
import com.app.ai.mclint.feature_settings.presentation.SettingsScreen
import java.net.URLDecoder

/**
 * Main navigation graph for the application
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.FileManager.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // File Manager Screen
        composable(route = Screen.FileManager.route) {
            FileManagerScreen(
                onFileClick = { fileItem ->
                    if (fileItem.isDirectory) {
                        // Navigate to browse the directory
                        navController.navigate(Screen.FileBrowser.createRoute(fileItem.path))
                    } else {
                        // Open file in editor
                        navController.navigate(Screen.CodeEditor.createRoute(fileItem.path))
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // File Browser Screen with path argument
        composable(
            route = Screen.FileBrowser.route,
            arguments = listOf(
                navArgument("path") {
                    type = NavType.StringType
                    defaultValue = "/"
                }
            )
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("path") ?: "/"
            val path = URLDecoder.decode(encodedPath, "UTF-8")
            
            FileManagerScreen(
                initialPath = path,
                onFileClick = { fileItem ->
                    if (fileItem.isDirectory) {
                        navController.navigate(Screen.FileBrowser.createRoute(fileItem.path))
                    } else {
                        navController.navigate(Screen.CodeEditor.createRoute(fileItem.path))
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Code Editor Screen with file path argument
        composable(
            route = Screen.CodeEditor.route,
            arguments = listOf(
                navArgument("filePath") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("filePath")
            val filePath = encodedPath?.let { URLDecoder.decode(it, "UTF-8") }
            
            CodeEditorScreen(
                filePath = filePath,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // AI Chat Screen
        composable(route = Screen.AIChat.route) {
            AIChatScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Settings Screen
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
