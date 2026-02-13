package com.app.ai.mclint

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for McLint AI File Manager
 * 
 * This class serves as the entry point for the application and
 * is annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class AiFileManagerApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization will be added here
    }
}
