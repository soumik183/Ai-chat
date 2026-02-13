package com.app.ai.mclint.core.util

/**
 * Application-wide constants
 */
object Constants {
    
    // API URLs
    const val HUGGINGFACE_BASE_URL = "https://api-inference.huggingface.co/"
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    
    // Default AI Models
    const val DEFAULT_HUGGINGFACE_MODEL = "mistralai/Mistral-7B-Instruct-v0.2"
    const val DEFAULT_OPENROUTER_MODEL = "openai/gpt-3.5-turbo"
    
    // File Operations
    const val FILE_OPERATION_REQUEST_CODE = 1001
    const val PERMISSION_REQUEST_CODE = 1002
    
    // File Types
    const val FILE_TYPE_DOCUMENT = "document"
    const val FILE_TYPE_IMAGE = "image"
    const val FILE_TYPE_VIDEO = "video"
    const val FILE_TYPE_AUDIO = "audio"
    const val FILE_TYPE_CODE = "code"
    const val FILE_TYPE_ARCHIVE = "archive"
    const val FILE_TYPE_FOLDER = "folder"
    const val FILE_TYPE_OTHER = "other"
    
    // Code File Extensions
    val CODE_EXTENSIONS = setOf(
        "kt", "java", "py", "js", "ts", "jsx", "tsx", "c", "cpp", "h", "hpp",
        "cs", "go", "rs", "rb", "php", "swift", "m", "scala", "groovy", "sh",
        "bash", "zsh", "json", "xml", "yaml", "yml", "toml", "ini", "cfg",
        "html", "css", "scss", "sass", "less", "sql", "md", "txt"
    )
    
    // Image Extensions
    val IMAGE_EXTENSIONS = setOf(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "tiff", "heic"
    )
    
    // Video Extensions
    val VIDEO_EXTENSIONS = setOf(
        "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "m4v", "3gp"
    )
    
    // Audio Extensions
    val AUDIO_EXTENSIONS = setOf(
        "mp3", "wav", "ogg", "m4a", "flac", "aac", "wma", "opus"
    )
    
    // Archive Extensions
    val ARCHIVE_EXTENSIONS = setOf(
        "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "tgz"
    )
    
    // Document Extensions
    val DOCUMENT_EXTENSIONS = setOf(
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp",
        "rtf", "csv", "tsv"
    )
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 50
    
    // Chat
    const val MAX_MESSAGE_LENGTH = 8000
    const val MAX_ATTACHMENTS = 5
    
    // Editor
    const val MAX_FILE_SIZE_EDITOR = 10 * 1024 * 1024 // 10 MB
    const val TAB_SIZE = 4
    
    // Preferences
    const val PREFERENCES_NAME = "ai_file_manager_prefs"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_DEFAULT_VIEW = "default_view"
    const val KEY_SHOW_HIDDEN = "show_hidden"
    const val KEY_HUGGINGFACE_API_KEY = "huggingface_api_key"
    const val KEY_OPENROUTER_API_KEY = "openrouter_api_key"
    const val KEY_DEFAULT_PROVIDER = "default_provider"
    const val KEY_DEFAULT_MODEL = "default_model"
}
