package io.github.stardomains3.oxproxion

import android.provider.Settings
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolExecutorPolicyTest {
    @Test
    fun allowsKnownWifiSettings() {
        assertTrue(ToolExecutorPolicy.isAllowedSettingsAction(Settings.ACTION_WIFI_SETTINGS))
    }

    @Test
    fun rejectsBlankAndUnknown() {
        assertFalse(ToolExecutorPolicy.isAllowedSettingsAction(null))
        assertFalse(ToolExecutorPolicy.isAllowedSettingsAction(""))
        assertFalse(ToolExecutorPolicy.isAllowedSettingsAction("com.evil.OPEN_EVERYTHING"))
    }

    @Test
    fun marksDestructiveFileTools() {
        assertTrue(ToolExecutorPolicy.isDestructiveFileTool("delete_files"))
        assertTrue(ToolExecutorPolicy.isDestructiveFileTool("edit_file"))
        assertFalse(ToolExecutorPolicy.isDestructiveFileTool("list_gradation_files"))
    }
}
