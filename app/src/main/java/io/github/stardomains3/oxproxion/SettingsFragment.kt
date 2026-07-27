
package io.github.stardomains3.oxproxion

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.core.widget.doAfterTextChanged

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        val prefs = SharedPreferencesHelper(requireContext())

        val viewModel: ChatViewModel by activityViewModels()

        val themeToggleGroup = view.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.themeToggleGroup)
        val inferenceParamsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.inferenceParamsButton)
        val watermarkSttSwitch = view.findViewById<MaterialSwitch>(R.id.watermarkSttSwitch)
        val chatMemoryButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.chatMemoryButton)
        val toolsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.toolsButton)
        val animateBarOnErrorSwitch = view.findViewById<MaterialSwitch>(R.id.animateBarOnErrorSwitch)
        val showCitationsSwitch = view.findViewById<MaterialSwitch>(R.id.showCitationsSwitch)
        val autoSaveChatsSwitch = view.findViewById<MaterialSwitch>(R.id.autoSaveChatsSwitch)
        val extendedTopBarSwitch = view.findViewById<MaterialSwitch>(R.id.extendedTopBarSwitch)
        val copyOrDismissSwitch = view.findViewById<MaterialSwitch>(R.id.copyOrdismissSwitch)
        val expandableInputSwitch = view.findViewById<MaterialSwitch>(R.id.expandableInputSwitch)
        val copyOrOpenSwitch = view.findViewById<MaterialSwitch>(R.id.copyOropenSwitch)
        val autoDisableWebSearchSwitch = view.findViewById<MaterialSwitch>(R.id.autoDisableWebSearchSwitch)
        val biometricsSwitch = view.findViewById<MaterialSwitch>(R.id.biometricsSwitch)
        val notificationsSwitch = view.findViewById<MaterialSwitch>(R.id.notificationsSwitch)
        val keepScreenOnSwitch = view.findViewById<MaterialSwitch>(R.id.keepScreenOnSwitch)
        val scrollButtonsSwitch = view.findViewById<MaterialSwitch>(R.id.scrollButtonsSwitch)
        val volumeScrollSwitch = view.findViewById<MaterialSwitch>(R.id.volumeScrollSwitch)
        val timeoutButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.timeoutButton)
        val extendedDockSwitch = view.findViewById<MaterialSwitch>(R.id.extendedDockSwitch)
        val presetsExtendedSwitch = view.findViewById<MaterialSwitch>(R.id.presetsExtendedSwitch)
        val scrollProgressSwitch = view.findViewById<MaterialSwitch>(R.id.scrollProgressSwitch)
        val apiKeyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.apiKeyButton)
        val braveApiKeyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.braveApiKeyButton)
        val promptsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.promptsButton)
        val creditsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.creditsButton)
        val helpButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.helpButton)
        val maxTokensButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.maxTokensButton)
        val lanButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.lanButton)
        val trustSelfSignedLanSwitch = view.findViewById<MaterialSwitch>(R.id.trustSelfSignedLanSwitch)
        val allowDestructiveToolsSwitch = view.findViewById<MaterialSwitch>(R.id.allowDestructiveToolsSwitch)
        val openRouterTransformsSwitch = view.findViewById<MaterialSwitch>(R.id.openRouterTransformsSwitch)
        val voiceModelEdit = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.voiceInputModelEdit)
        val voiceProviderToggle = view.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.voiceInputProviderToggle)
        biometricsSwitch.isChecked = prefs.getBiometricEnabled()
        notificationsSwitch.isChecked = prefs.getNotiPreference()
        autoSaveChatsSwitch.isChecked = prefs.getAutoSaveChats()
        val memoryCount = prefs.getChatMemoryCount()
        chatMemoryButton.text = if (memoryCount == Int.MAX_VALUE) "All messages" else "$memoryCount messages"
        val savedMode = prefs.getThemeMode()
        when (savedMode) {
            SharedPreferencesHelper.THEME_LIGHT -> themeToggleGroup.check(R.id.btnThemeLight)
            SharedPreferencesHelper.THEME_DARK -> themeToggleGroup.check(R.id.btnThemeDark)
            else -> themeToggleGroup.check(R.id.btnThemeSystem)
        }
        watermarkSttSwitch.isChecked = prefs.getWatermarkSttEnabled()
        keepScreenOnSwitch.isChecked = prefs.getKeepScreenOnPreference()
        copyOrDismissSwitch.isChecked = prefs.getUseCopyButton2()
        animateBarOnErrorSwitch.isChecked = prefs.getAnimateBarOnError()
        scrollButtonsSwitch.isChecked = viewModel.isScrollersEnabled.value ?: false
        volumeScrollSwitch.isChecked = viewModel.isVolumeScrollEnabled.value ?: false
        expandableInputSwitch.isChecked = viewModel.isExpandableInputEnabled.value ?: false
        extendedDockSwitch.isChecked = viewModel.isExtendedDockEnabled.value ?: false
        presetsExtendedSwitch.isChecked = viewModel.isPresetsExtendedEnabled.value ?: false
        scrollProgressSwitch.isChecked = viewModel.isScrollProgressEnabled.value ?: true
        extendedTopBarSwitch.isChecked = prefs.getExtendedTopBarEnabled()
        copyOrOpenSwitch.isChecked = prefs.getUseCopyButton()
        autoDisableWebSearchSwitch.isChecked = prefs.getDisableWebSearchAfterSend()
        openRouterTransformsSwitch.isChecked = prefs.getOpenRouterTransformsEnabled()
        trustSelfSignedLanSwitch.isChecked = prefs.getTrustSelfSignedLan()
        allowDestructiveToolsSwitch.isChecked = prefs.getAllowDestructiveTools()
        showCitationsSwitch.isChecked = prefs.getShowCitations()
        voiceModelEdit.setText(prefs.getVoiceInputModel())
        when (prefs.getVoiceInputProvider()) {
            "cloud" -> voiceProviderToggle.check(R.id.providerCloudButton)
            "off" -> voiceProviderToggle.check(R.id.providerOffButton)
            else -> voiceProviderToggle.check(R.id.providerLanButton)
        }
        apiKeyButton.setOnClickListener {
            val dialog = SaveApiDialogFragment()
            dialog.show(childFragmentManager, "SaveApiDialogFragment")
        }
        toolsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, ToolsFragment())
                .addToBackStack(null)
                .commit()
        }
        braveApiKeyButton.setOnClickListener {
            val dialog = SaveBraveApiDialogFragment()
            dialog.show(childFragmentManager, SaveBraveApiDialogFragment.TAG)
        }
        chatMemoryButton.setOnClickListener {
            val dialog = ChatMemoryDialogFragment()
            dialog.show(childFragmentManager, "ChatMemoryDialogFragment")
        }

        autoSaveChatsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveAutoSaveChats(isChecked)
        }
        extendedDockSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleExtendedDock()  // VM saves + notifies Chat
        }
        watermarkSttSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveWatermarkSttEnabled(isChecked)
        }
        copyOrDismissSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveUseCopyButton2(isChecked)  // true = Copy button, false = Dismiss button
        }
        animateBarOnErrorSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveAnimateBarOnError(isChecked)
        }

        presetsExtendedSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.togglePresetsExtended()  // VM saves + notifies Chat
        }
        viewModel.isPresetsExtendedEnabled.observe(viewLifecycleOwner) { enabled ->
            presetsExtendedSwitch.isChecked = enabled
        }
        viewModel.isVolumeScrollEnabled.observe(viewLifecycleOwner) { enabled ->
            volumeScrollSwitch.isChecked = enabled
        }
        openRouterTransformsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveOpenRouterTransformsEnabled(isChecked)
        }
        themeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) { // Only trigger when a button is selected
                val mode = when (checkedId) {
                    R.id.btnThemeLight -> SharedPreferencesHelper.THEME_LIGHT
                    R.id.btnThemeDark -> SharedPreferencesHelper.THEME_DARK
                    else -> SharedPreferencesHelper.THEME_SYSTEM
                }

                // Save the choice
                prefs.saveThemeMode(mode)

                // Determine the AppCompatDelegate mode
                val appMode = when (mode) {
                    SharedPreferencesHelper.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    SharedPreferencesHelper.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }

                // Apply and Recreate
                if (AppCompatDelegate.getDefaultNightMode() != appMode) {
                    AppCompatDelegate.setDefaultNightMode(appMode)
                    requireActivity().recreate()
                }
            }
        }
        showCitationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveShowCitations(isChecked)
        }
        copyOrOpenSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveUseCopyButton(isChecked)  // true = Copy button, false = Open button
        }
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveNotiPreference(isChecked)
        }
        autoDisableWebSearchSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveDisableWebSearchAfterSend(isChecked)
        }
        expandableInputSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleExpandableInput()
        }
        inferenceParamsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, InferenceParametersFragment())
                .addToBackStack(null)
                .commit()
        }
        extendedTopBarSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleExtendedTopBar()  // VM saves + notifies Chat
        }


        scrollProgressSwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.toggleScrollProgress() }
        viewModel.isScrollProgressEnabled.observe(viewLifecycleOwner) { enabled -> scrollProgressSwitch.isChecked = enabled }
        creditsButton.setOnClickListener {
            if (viewModel.activeChatApiKey.isBlank()) {
                Toast.makeText(requireContext(), "API Key is not set.", Toast.LENGTH_SHORT).show()
            } else {
                parentFragmentManager.popBackStack()
                viewModel.checkRemainingCredits()
            }
        }
        scrollButtonsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleScrollers()  // 🔥 VM saves prefs + notifies Chat instantly
        }
        volumeScrollSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleVolumeScroll()
        }
        keepScreenOnSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveKeepScreenOnPreference(isChecked)  // Save forever

            val window = requireActivity().window
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        helpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, HelpFragment())
                .addToBackStack(null)
                .commit()
        }
        promptsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, PromptLibraryFragment())
                .addToBackStack(null)
                .commit()
        }
        maxTokensButton.setOnClickListener {
            val dialog = MaxTokensDialogFragment()
            dialog.show(childFragmentManager, "MaxTokensDialogFragment")
        }
        lanButton.setOnClickListener {
            SaveLANDialogFragment().show(childFragmentManager, SaveLANDialogFragment.TAG)
        }
        trustSelfSignedLanSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveTrustSelfSignedLan(isChecked)
            viewModel.refreshLanHttpClient()
        }
        allowDestructiveToolsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveAllowDestructiveTools(isChecked)
        }
        timeoutButton.setOnClickListener {
            TimeoutDialogFragment().show(childFragmentManager, TimeoutDialogFragment.TAG)
        }
        biometricsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val bm = BiometricManager.from(requireContext())
                when (bm.canAuthenticate(BIOMETRIC_STRONG)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        prefs.saveBiometricEnabled(true)
                    }
                    else -> {
                        biometricsSwitch.isChecked = false
                        Toast.makeText(requireContext(), "No biometrics available", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                prefs.saveBiometricEnabled(false)
            }
        }

        voiceModelEdit.doAfterTextChanged { text ->
            prefs.setVoiceInputModel(text?.toString() ?: "")
        }

// Save provider on toggle change
        voiceProviderToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val provider = when (checkedId) {
                    R.id.providerCloudButton -> "cloud"
                    R.id.providerOffButton -> "off"
                    else -> "lan"
                }
                prefs.setVoiceInputProvider(provider)
            }
        }
        // 🔥 STYLE ALL SWITCHES (your exact code → reusable)
        listOf(
            R.id.watermarkSttSwitch,
                    R.id.scrollButtonsSwitch,
            R.id.volumeScrollSwitch,
            R.id.expandableInputSwitch,
            R.id.scrollProgressSwitch,
            R.id.keepScreenOnSwitch,
            R.id.biometricsSwitch,
            R.id.copyOropenSwitch,
            R.id.extendedDockSwitch,
            R.id.extendedDockSwitch,
            R.id.expandableInputSwitch,
            R.id.scrollProgressSwitch,
            R.id.scrollButtonsSwitch,
            R.id.volumeScrollSwitch,
            R.id.presetsExtendedSwitch,
            R.id.keepScreenOnSwitch,
            R.id.biometricsSwitch,
            R.id.notificationsSwitch,
            R.id.autoDisableWebSearchSwitch,
            R.id.extendedTopBarSwitch,
            R.id.showCitationsSwitch,
            R.id.copyOrdismissSwitch,
            R.id.copyOropenSwitch,
            R.id.autoSaveChatsSwitch,
            R.id.openRouterTransformsSwitch,
            R.id.trustSelfSignedLanSwitch,
            R.id.allowDestructiveToolsSwitch,
            R.id.animateBarOnErrorSwitch,
            R.id.watermarkSttSwitch
        ).forEach { id ->
            view.findViewById<MaterialSwitch>(id)?.styleSwitch()
        }
    }

    // 🔥 HELPER: Grokion switch style from color resources
    private fun MaterialSwitch.styleSwitch() = applyGrokionSwitchStyle()
}
