# McLint AI - AI-Powered File Manager

An intelligent Android file manager application with AI integration for natural language file operations.

[![Android CI Build](https://github.com/YOUR_USERNAME/mclint-ai/actions/workflows/android-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/mclint-ai/actions/workflows/android-build.yml)

## 📥 Download APK

### Latest Release
Download the latest APK from [GitHub Releases](https://github.com/YOUR_USERNAME/mclint-ai/releases/latest)

### Build Artifacts
You can also download APKs directly from [GitHub Actions](https://github.com/YOUR_USERNAME/mclint-ai/actions):
1. Go to the Actions tab
2. Click on the latest successful workflow run
3. Scroll down to "Artifacts" section
4. Download `mclint-ai-debug` or `mclint-ai-release`

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
   ```bash
   git clone https://github.com/YOUR_USERNAME/mclint-ai.git
   cd mclint-ai
   ```

2. Open in Android Studio

3. Sync Gradle files

4. Configure API keys in Settings screen

### Building from Source

#### Debug Build
```bash
./gradlew assembleDebug
```

#### Release Build
```bash
./gradlew assembleRelease
```

APKs will be generated in:
- Debug: `app/build/outputs/apk/debug/`
- Release: `app/build/outputs/apk/release/`

## API Keys

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

## GitHub Actions CI/CD

This project uses GitHub Actions for automated builds:

- **On Push**: Builds debug and release APKs
- **On Pull Request**: Validates the build
- **On Main/Master**: Creates a release with downloadable APKs

### Manual Build Trigger

You can manually trigger a build:
1. Go to Actions tab
2. Select "Android CI Build"
3. Click "Run workflow"

## Installation

1. Download the APK
2. Enable "Install from unknown sources" in Android settings:
   - Go to Settings > Security
   - Enable "Unknown sources" or "Install unknown apps"
3. Open the APK file to install

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [HuggingFace](https://huggingface.co/) for AI model APIs
- [OpenRouter](https://openrouter.ai/) for LLM access
- [Material Design 3](https://m3.material.io/) for UI components
