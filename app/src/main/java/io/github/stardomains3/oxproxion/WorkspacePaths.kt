package io.github.stardomains3.oxproxion

import android.os.Environment
import java.io.File

object WorkspacePaths {
    const val FOLDER_GROKION = "grokion"
    const val FOLDER_LEGACY = "oxproxion"

    private fun downloadsRoot(): File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    /** Writes always go to Download/grokion (created if missing). */
    fun workspaceDirForWrite(): File {
        val dir = File(downloadsRoot(), FOLDER_GROKION)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** Reads prefer grokion; fall back to legacy oxproxion if grokion is absent. */
    fun workspaceDirForRead(): File {
        val grokion = File(downloadsRoot(), FOLDER_GROKION)
        if (grokion.exists()) return grokion
        return File(downloadsRoot(), FOLDER_LEGACY)
    }

    fun ensureWorkspaceExists(): File = workspaceDirForWrite()

    fun mediaStoreRelativePath(subfolder: String = ""): String {
        val base = "${Environment.DIRECTORY_DOWNLOADS}/$FOLDER_GROKION"
        val safe = subfolder.trim().removePrefix("/").removeSuffix("/")
        return if (safe.isNotBlank()) "$base/$safe" else base
    }

    fun displayFolderLabel(): String = "Download/$FOLDER_GROKION"

    fun displayPath(filename: String, subfolder: String = ""): String {
        val safe = subfolder.trim().removePrefix("/").removeSuffix("/")
        return when {
            safe.isNotBlank() -> "$FOLDER_GROKION/$safe/$filename"
            else -> "$FOLDER_GROKION/$filename"
        }
    }
}
