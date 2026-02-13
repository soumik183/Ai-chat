package com.app.ai.mclint.core.navigation

import com.app.ai.mclint.R

/**
 * Navigation routes for the application
 */
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object FileManager : Screen("file_manager")
    object FileBrowser : Screen("file_browser?path={path}") {
        fun createRoute(path: String = "/"): String {
            return "file_browser?path=${java.net.URLEncoder.encode(path, "UTF-8")}"
        }
    }
    object CodeEditor : Screen("code_editor?filePath={filePath}") {
        fun createRoute(filePath: String): String {
            return "code_editor?filePath=${java.net.URLEncoder.encode(filePath, "UTF-8")}"
        }
    }
    object AIChat : Screen("ai_chat")
    object Settings : Screen("settings")
}

/**
 * Bottom navigation items
 */
enum class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val iconRes: Int
) {
    FILES(
        route = "file_manager",
        titleRes = R.string.nav_files,
        iconRes = R.drawable.ic_folder
    ),
    AI_CHAT(
        route = "ai_chat",
        titleRes = R.string.nav_ai_chat,
        iconRes = R.drawable.ic_chat
    ),
    SETTINGS(
        route = "settings",
        titleRes = R.string.nav_settings,
        iconRes = R.drawable.ic_settings
    )
}
