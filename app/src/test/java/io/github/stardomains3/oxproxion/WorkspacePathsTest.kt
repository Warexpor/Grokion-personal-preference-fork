package io.github.stardomains3.oxproxion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkspacePathsTest {
    @Test
    fun primaryFolderNameIsGrokion() {
        assertEquals("grokion", WorkspacePaths.FOLDER_GROKION)
    }

    @Test
    fun legacyFolderNameIsOxproxion() {
        assertEquals("oxproxion", WorkspacePaths.FOLDER_LEGACY)
    }

    @Test
    fun mediaStoreRelativePathUsesPrimary() {
        val path = WorkspacePaths.mediaStoreRelativePath("")
        assertTrue(path.contains("grokion"))
    }
}
