package com.app.ai.mclint

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.ai.mclint.core.permission.PermissionManager
import com.app.ai.mclint.core.permission.StoragePermissionState
import com.app.ai.mclint.core.theme.AiFileManagerTheme
import com.app.ai.mclint.core.navigation.BottomNavItem
import com.app.ai.mclint.core.navigation.NavigationGraph
import com.app.ai.mclint.core.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity for McLint AI File Manager
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    private var hasStoragePermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            hasStoragePermission = true
        }
    }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasStoragePermission = permissionManager.hasStoragePermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check and request permissions
        checkAndRequestPermissions()
        
        setContent {
            AiFileManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasStoragePermission) {
                        MainContent()
                    } else {
                        PermissionRequestScreen(
                            onRequestPermission = { checkAndRequestPermissions() },
                            onOpenSettings = { permissionManager.openAppSettings(this) }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check permission when returning to app
        hasStoragePermission = permissionManager.hasStoragePermission()
    }

    private fun checkAndRequestPermissions() {
        when (permissionManager.checkStoragePermission()) {
            StoragePermissionState.GRANTED -> {
                hasStoragePermission = true
            }
            StoragePermissionState.MANAGE_APP_FILES -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    manageStorageLauncher.launch(
                        android.content.Intent(
                            android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            android.net.Uri.parse("package:$packageName")
                        )
                    )
                }
            }
            StoragePermissionState.LEGACY_STORAGE -> {
                permissionLauncher.launch(permissionManager.getRequiredPermissions())
            }
            StoragePermissionState.DENIED -> {
                hasStoragePermission = false
            }
        }
    }
}

/**
 * Main content with navigation
 */
@Composable
private fun MainContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine if bottom bar should be shown
    val showBottomBar = currentRoute in listOf(
        Screen.FileManager.route,
        Screen.AIChat.route,
        Screen.Settings.route
    )
    
    androidx.compose.material3.Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.FileManager.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Bottom navigation bar
 */
@Composable
private fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.FILES,
        BottomNavItem.AI_CHAT,
        BottomNavItem.SETTINGS
    )
    
    androidx.compose.material3.NavigationBar {
        items.forEach { item ->
            androidx.compose.material3.NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = if (currentRoute == item.route) {
                            androidx.compose.material.icons.Icons.Filled.Folder
                        } else {
                            androidx.compose.material.icons.Icons.Outlined.Folder
                        },
                        contentDescription = null
                    )
                },
                label = {
                    androidx.compose.material3.Text(
                        text = androidx.compose.ui.res.stringResource(item.titleRes)
                    )
                }
            )
        }
    }
}

/**
 * Permission request screen
 */
@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = androidx.compose.ui.Modifier.padding(32.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Outlined.FolderOpen,
                contentDescription = null,
                modifier = androidx.compose.ui.Modifier.size(80.dp),
                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
            
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(24.dp)
            )
            
            androidx.compose.material3.Text(
                text = androidx.compose.ui.res.stringResource(com.app.ai.mclint.R.string.permission_title),
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
            
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(16.dp)
            )
            
            androidx.compose.material3.Text(
                text = androidx.compose.ui.res.stringResource(com.app.ai.mclint.R.string.permission_message),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(32.dp)
            )
            
            androidx.compose.material3.Button(
                onClick = onRequestPermission
            ) {
                androidx.compose.material3.Text(
                    text = androidx.compose.ui.res.stringResource(com.app.ai.mclint.R.string.permission_grant)
                )
            }
            
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(8.dp)
            )
            
            androidx.compose.material3.TextButton(
                onClick = onOpenSettings
            ) {
                androidx.compose.material3.Text(
                    text = androidx.compose.ui.res.stringResource(com.app.ai.mclint.R.string.permission_settings)
                )
            }
        }
    }
}
