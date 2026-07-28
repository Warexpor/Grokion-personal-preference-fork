package io.github.stardomains3.oxproxion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkspacePathsTest {
    @Test
    fun primaryFolderNameIsGradation() {
        assertEquals("gradation", WorkspacePaths.FOLDER_GRADATION)
    }

    @Test
    fun legacyFolderNamesPreserved() {
        assertEquals("grokion", WorkspacePaths.FOLDER_GROKION)
        assertEquals("oxproxion", WorkspacePaths.FOLDER_LEGACY)
    }

    @Test
    fun mediaStoreRelativePathUsesPrimary() {
        val path = WorkspacePaths.mediaStoreRelativePath("")
        assertTrue(path.contains("gradation"))
    }
}
