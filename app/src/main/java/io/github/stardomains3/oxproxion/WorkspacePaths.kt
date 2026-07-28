package io.github.stardomains3.oxproxion

import android.os.Environment
import java.io.File

object WorkspacePaths {
    const val FOLDER_GRADATION = "gradation"
    /** @deprecated Prefer [FOLDER_GRADATION]; kept for existing installs. */
    const val FOLDER_GROKION = "grokion"
    const val FOLDER_LEGACY = "oxproxion"

    private fun downloadsRoot(): File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    /** Writes always go to Download/gradation (created if missing). */
    fun workspaceDirForWrite(): File {
        val dir = File(downloadsRoot(), FOLDER_GRADATION)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** Reads prefer gradation, then grokion, then legacy oxproxion. */
    fun workspaceDirForRead(): File {
        val preferred = File(downloadsRoot(), FOLDER_GRADATION)
        if (preferred.exists()) return preferred
        val grokion = File(downloadsRoot(), FOLDER_GROKION)
        if (grokion.exists()) return grokion
        return File(downloadsRoot(), FOLDER_LEGACY)
    }

    fun ensureWorkspaceExists(): File = workspaceDirForWrite()

    fun mediaStoreRelativePath(subfolder: String = ""): String {
        val base = "${Environment.DIRECTORY_DOWNLOADS}/$FOLDER_GRADATION"
        val safe = subfolder.trim().removePrefix("/").removeSuffix("/")
        return if (safe.isNotBlank()) "$base/$safe" else base
    }

    fun displayFolderLabel(): String = "Download/$FOLDER_GRADATION"

    fun displayPath(filename: String, subfolder: String = ""): String {
        val safe = subfolder.trim().removePrefix("/").removeSuffix("/")
        return when {
            safe.isNotBlank() -> "$FOLDER_GRADATION/$safe/$filename"
            else -> "$FOLDER_GRADATION/$filename"
        }
    }
}
