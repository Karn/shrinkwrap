package utils

import java.io.File
import java.nio.file.Path

fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0

    return when {
        mb >= 1.0 -> "%.1f MB".format(mb)
        kb >= 1.0 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}

fun openFileInFinder(path: Path) {
    try {
        ProcessBuilder("open", "-R", path.toString())
            .start()
    } catch (e: Exception) {
        // Handle error silently or log it
        e.printStackTrace()
    }
} 