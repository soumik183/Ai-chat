# McLint AI - AI-Powered File Manager

An intelligent Android file manager application with AI integration for natural language file operations.

## Features

### 📁 File Manager
- Browse, create, delete, copy, and move files and folders
- List and grid view modes
- Sort by name, date, size, or type
- Search files by name
- Batch operations for multiple files
- Breadcrumb navigation

### ✏️ Code Editor
- Syntax highlighting for common programming languages
- Line numbers
- Undo/Redo functionality
- Line-specific editing
- AI-assisted code suggestions

### 🤖 AI Integration
- **HuggingFace API** - Access to open-source AI models
- **OpenRouter API** - Access to multiple LLM providers
- Natural language file commands
- AI-assisted code editing

### ⚙️ Settings
- API key management
- Theme selection (Light/Dark/System)
- Default view preferences
- Show/hide hidden files

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Storage**: Room Database + DataStore
- **Async**: Kotlin Coroutines & Flow

## Project Structure

```
app/
├── src/main/java/com/app/ai/mclint/
│   ├── AiFileManagerApp.kt          # Application class
│   ├── MainActivity.kt              # Main activity
│   │
│   ├── core/                        # Core utilities
│   │   ├── navigation/              # Navigation routes
│   │   ├── permission/              # Permission handling
│   │   ├── theme/                   # App theme
│   │   └── util/                    # Constants
│   │
│   ├── di/                          # Dependency injection
│   │   ├── AppModule.kt
│   │   ├── NetworkModule.kt
│   │   └── RepositoryModule.kt
│   │
│   ├── data/                        # Data layer
│   │   └── remote/                  # API clients
│   │
│   ├── feature_filemanager/         # File Manager feature
│   │   ├── domain/
│   │   ├── data/
│   │   └── presentation/
│   │
│   ├── feature_editor/              # Code Editor feature
│   │   ├── domain/
│   │   └── presentation/
│   │
│   ├── feature_aichat/              # AI Chat feature
│   │   ├── domain/
│   │   └── presentation/
│   │
│   └── feature_settings/            # Settings feature
│       └── presentation/
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Configure API keys in Settings screen

### API Keys

Get your API keys from:
- **HuggingFace**: https://huggingface.co/settings/tokens
- **OpenRouter**: https://openrouter.ai/keys

## AI Commands Examples

The AI understands natural language commands like:

- "Delete all PDF files in Downloads"
- "Create a new file called notes.txt in Documents"
- "Edit line 5 in config.json and change port to 8080"
- "Move all images to Pictures folder"
- "Find all files larger than 100MB"

## Permissions

The app requires the following permissions:
- `READ_EXTERNAL_STORAGE` - Read files (Android 10 and below)
- `WRITE_EXTERNAL_STORAGE` - Write files (Android 9 and below)
- `MANAGE_EXTERNAL_STORAGE` - Full file access (Android 11+)
- `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO` - Media files (Android 13+)

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## License

This project is licensed under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
