package io.github.stardomains3.oxproxion

import android.provider.Settings

/**
 * Allowlists and gates for LLM tool execution (extracted from ChatViewModel surface).
 */
object ToolExecutorPolicy {
    val ALLOWED_SETTINGS_ACTIONS: Set<String> = setOf(
        Settings.ACTION_WIFI_SETTINGS,
        Settings.ACTION_AIRPLANE_MODE_SETTINGS,
        Settings.ACTION_BLUETOOTH_SETTINGS,
        Settings.ACTION_DATA_ROAMING_SETTINGS,
        Settings.ACTION_DATE_SETTINGS,
        Settings.ACTION_DISPLAY_SETTINGS,
        Settings.ACTION_LOCALE_SETTINGS,
        Settings.ACTION_LOCATION_SOURCE_SETTINGS,
        Settings.ACTION_NETWORK_OPERATOR_SETTINGS,
        Settings.ACTION_SECURITY_SETTINGS,
        Settings.ACTION_SOUND_SETTINGS,
        Settings.ACTION_SYNC_SETTINGS,
        Settings.ACTION_APPLICATION_SETTINGS,
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS,
        Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS,
        Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS,
        Settings.ACTION_BATTERY_SAVER_SETTINGS,
        Settings.ACTION_NFC_SETTINGS,
        Settings.ACTION_PRIVACY_SETTINGS,
        Settings.ACTION_ACCESSIBILITY_SETTINGS,
        Settings.ACTION_CAPTIONING_SETTINGS,
        Settings.ACTION_CAST_SETTINGS,
        Settings.ACTION_DEVICE_INFO_SETTINGS,
        Settings.ACTION_DREAM_SETTINGS,
        Settings.ACTION_INPUT_METHOD_SETTINGS,
        Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
        Settings.ACTION_MEMORY_CARD_SETTINGS,
        Settings.ACTION_PRINT_SETTINGS,
        Settings.ACTION_SEARCH_SETTINGS,
        Settings.ACTION_SETTINGS,
        Settings.ACTION_WIRELESS_SETTINGS,
    )

    fun isAllowedSettingsAction(action: String?): Boolean {
        if (action.isNullOrBlank()) return false
        return action in ALLOWED_SETTINGS_ACTIONS
    }

    fun isDestructiveFileTool(name: String): Boolean =
        name == "delete_files" || name == "edit_file"
}
