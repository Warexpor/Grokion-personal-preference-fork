package io.github.stardomains3.oxproxion

import io.github.stardomains3.oxproxion.Motion.withGrokStackAnimations

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.MaterialToolbar

class SettingsDetailFragment : Fragment(R.layout.fragment_settings_detail) {

    private val section: String
        get() = requireArguments().getString(ARG_SECTION) ?: SECTION_APPEARANCE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = sectionTitle(section)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        bindAllControls(view)
        applySectionVisibility(view, section)
    }

    private fun sectionTitle(section: String): String = when (section) {
        SECTION_APPEARANCE -> getString(R.string.settings_section_appearance)
        SECTION_VOICE -> getString(R.string.settings_section_voice)
        SECTION_HAPTICS -> getString(R.string.settings_section_haptics)
        SECTION_MODELS -> getString(R.string.settings_section_models)
        SECTION_ADVANCED -> getString(R.string.settings_section_advanced)
        SECTION_DATA -> getString(R.string.settings_section_data)
        else -> getString(R.string.settings_title)
    }

    private fun bindAllControls(view: View) {
        val prefs = SharedPreferencesHelper(requireContext())
        val viewModel: ChatViewModel by activityViewModels()

        val themeToggleGroup = view.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.themeToggleGroup)
        val inferenceParamsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.inferenceParamsButton)
        val watermarkSttSwitch = view.findViewById<SwitchCompat>(R.id.watermarkSttSwitch)
        val chatMemoryButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.chatMemoryButton)
        val toolsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.toolsButton)
        val animateBarOnErrorSwitch = view.findViewById<SwitchCompat>(R.id.animateBarOnErrorSwitch)
        val showCitationsSwitch = view.findViewById<SwitchCompat>(R.id.showCitationsSwitch)
        val autoSaveChatsSwitch = view.findViewById<SwitchCompat>(R.id.autoSaveChatsSwitch)
        val extendedTopBarSwitch = view.findViewById<SwitchCompat>(R.id.extendedTopBarSwitch)
        val copyOrDismissSwitch = view.findViewById<SwitchCompat>(R.id.copyOrdismissSwitch)
        val expandableInputSwitch = view.findViewById<SwitchCompat>(R.id.expandableInputSwitch)
        val copyOrOpenSwitch = view.findViewById<SwitchCompat>(R.id.copyOropenSwitch)
        val autoDisableWebSearchSwitch = view.findViewById<SwitchCompat>(R.id.autoDisableWebSearchSwitch)
        val biometricsSwitch = view.findViewById<SwitchCompat>(R.id.biometricsSwitch)
        val notificationsSwitch = view.findViewById<SwitchCompat>(R.id.notificationsSwitch)
        val keepScreenOnSwitch = view.findViewById<SwitchCompat>(R.id.keepScreenOnSwitch)
        val scrollButtonsSwitch = view.findViewById<SwitchCompat>(R.id.scrollButtonsSwitch)
        val volumeScrollSwitch = view.findViewById<SwitchCompat>(R.id.volumeScrollSwitch)
        val timeoutButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.timeoutButton)
        val extendedDockSwitch = view.findViewById<SwitchCompat>(R.id.extendedDockSwitch)
        val presetsExtendedSwitch = view.findViewById<SwitchCompat>(R.id.presetsExtendedSwitch)
        val scrollProgressSwitch = view.findViewById<SwitchCompat>(R.id.scrollProgressSwitch)
        val apiKeyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.apiKeyButton)
        val braveApiKeyButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.braveApiKeyButton)
        val promptsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.promptsButton)
        val presetsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.presetsButton)
        val systemMessagesButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.systemMessagesButton)
        val advancedReasoningButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.advancedReasoningButton)
        val creditsButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.creditsButton)
        val helpButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.helpButton)
        val licensesButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.licensesButton)
        val maxTokensButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.maxTokensButton)
        val lanButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.lanButton)
        val trustSelfSignedLanSwitch = view.findViewById<SwitchCompat>(R.id.trustSelfSignedLanSwitch)
        val allowDestructiveToolsSwitch = view.findViewById<SwitchCompat>(R.id.allowDestructiveToolsSwitch)
        val openRouterTransformsSwitch = view.findViewById<SwitchCompat>(R.id.openRouterTransformsSwitch)
        val voiceModelEdit = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.voiceInputModelEdit)
        val voiceProviderToggle = view.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.voiceInputProviderToggle)
        val hapticButtonsSwitch = view.findViewById<SwitchCompat>(R.id.hapticButtonsSwitch)
        val hapticRespondingSwitch = view.findViewById<SwitchCompat>(R.id.hapticRespondingSwitch)

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
        hapticButtonsSwitch.isChecked = prefs.getHapticButtons()
        hapticRespondingSwitch.isChecked = prefs.getHapticResponding()
        voiceModelEdit.setText(prefs.getVoiceInputModel())
        when (prefs.getVoiceInputProvider()) {
            "cloud" -> voiceProviderToggle.check(R.id.providerCloudButton)
            "off" -> voiceProviderToggle.check(R.id.providerOffButton)
            else -> voiceProviderToggle.check(R.id.providerLanButton)
        }

        apiKeyButton.setOnClickListener {
            SaveApiDialogFragment().show(childFragmentManager, "SaveApiDialogFragment")
        }
        toolsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, ToolsFragment())
                .addToBackStack(null)
                .commit()
        }
        braveApiKeyButton.setOnClickListener {
            SaveBraveApiDialogFragment().show(childFragmentManager, SaveBraveApiDialogFragment.TAG)
        }
        chatMemoryButton.setOnClickListener {
            ChatMemoryDialogFragment().show(childFragmentManager, "ChatMemoryDialogFragment")
        }
        autoSaveChatsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveAutoSaveChats(isChecked)
        }
        extendedDockSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleExtendedDock()
        }
        watermarkSttSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveWatermarkSttEnabled(isChecked)
        }
        copyOrDismissSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveUseCopyButton2(isChecked)
        }
        animateBarOnErrorSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveAnimateBarOnError(isChecked)
        }
        presetsExtendedSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.togglePresetsExtended()
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
            if (isChecked) {
                val mode = when (checkedId) {
                    R.id.btnThemeLight -> SharedPreferencesHelper.THEME_LIGHT
                    R.id.btnThemeDark -> SharedPreferencesHelper.THEME_DARK
                    else -> SharedPreferencesHelper.THEME_SYSTEM
                }
                prefs.saveThemeMode(mode)
                val appMode = when (mode) {
                    SharedPreferencesHelper.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    SharedPreferencesHelper.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
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
            prefs.saveUseCopyButton(isChecked)
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
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, InferenceParametersFragment())
                .addToBackStack(null)
                .commit()
        }
        extendedTopBarSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleExtendedTopBar()
        }
        scrollProgressSwitch.setOnCheckedChangeListener { _, _ -> viewModel.toggleScrollProgress() }
        viewModel.isScrollProgressEnabled.observe(viewLifecycleOwner) { enabled ->
            scrollProgressSwitch.isChecked = enabled
        }
        creditsButton.setOnClickListener {
            if (viewModel.activeChatApiKey.isBlank()) {
                Toast.makeText(requireContext(), "API Key is not set.", Toast.LENGTH_SHORT).show()
            } else {
                parentFragmentManager.popBackStack()
                viewModel.checkRemainingCredits()
            }
        }
        scrollButtonsSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleScrollers()
        }
        volumeScrollSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleVolumeScroll()
        }
        keepScreenOnSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveKeepScreenOnPreference(isChecked)
            val window = requireActivity().window
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        helpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, HelpFragment())
                .addToBackStack(null)
                .commit()
        }
        licensesButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, LicenseListFragment())
                .addToBackStack(null)
                .commit()
        }
        promptsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, PromptLibraryFragment())
                .addToBackStack(null)
                .commit()
        }
        presetsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, PresetsListFragment())
                .addToBackStack(null)
                .commit()
        }
        systemMessagesButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, SystemMessageLibraryFragment())
                .addToBackStack(null)
                .commit()
        }
        advancedReasoningButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, AdvancedReasoningFragment())
                .addToBackStack(null)
                .commit()
        }
        maxTokensButton.setOnClickListener {
            MaxTokensDialogFragment().show(childFragmentManager, "MaxTokensDialogFragment")
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
                    BiometricManager.BIOMETRIC_SUCCESS -> prefs.saveBiometricEnabled(true)
                    else -> {
                        biometricsSwitch.isChecked = false
                        Toast.makeText(requireContext(), "No biometrics available", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                prefs.saveBiometricEnabled(false)
            }
        }
        hapticButtonsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveHapticButtons(isChecked)
        }
        hapticRespondingSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.saveHapticResponding(isChecked)
        }
        voiceModelEdit.doAfterTextChanged { text ->
            prefs.setVoiceInputModel(text?.toString() ?: "")
        }
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
            R.id.presetsExtendedSwitch,
            R.id.notificationsSwitch,
            R.id.autoDisableWebSearchSwitch,
            R.id.extendedTopBarSwitch,
            R.id.showCitationsSwitch,
            R.id.copyOrdismissSwitch,
            R.id.autoSaveChatsSwitch,
            R.id.openRouterTransformsSwitch,
            R.id.trustSelfSignedLanSwitch,
            R.id.allowDestructiveToolsSwitch,
            R.id.animateBarOnErrorSwitch,
            R.id.hapticButtonsSwitch,
            R.id.hapticRespondingSwitch
        ).forEach { id ->
            view.findViewById<SwitchCompat>(id)?.applyGrokionSwitchStyle()
        }
    }

    private fun applySectionVisibility(view: View, section: String) {
        val sectionViewIds = mapOf(
            SECTION_APPEARANCE to listOf(
                R.id.appearanceSectionLabel,
                R.id.appearancePreviewCard,
                R.id.themeModeRow,
                R.id.themeToggleGroup,
                R.id.btnThemeSystem,
                R.id.btnThemeLight,
                R.id.btnThemeDark
            ),
            SECTION_VOICE to listOf(
                R.id.voiceSectionHeader,
                R.id.voiceModelInputLayout,
                R.id.voiceInputModelEdit,
                R.id.voiceInputProviderToggle,
                R.id.providerLanButton,
                R.id.providerCloudButton,
                R.id.providerOffButton,
                R.id.watermarkSttSwitch
            ),
            SECTION_HAPTICS to listOf(R.id.hapticsSection),
            SECTION_MODELS to listOf(
                R.id.lanButton,
                R.id.trustSelfSignedLanSwitch,
                R.id.apiKeyButton,
                R.id.braveApiKeyButton,
                R.id.creditsButton
            ),
            SECTION_ADVANCED to listOf(
                R.id.toolsButton,
                R.id.promptsButton,
                R.id.presetsButton,
                R.id.systemMessagesButton,
                R.id.advancedReasoningButton,
                R.id.timeoutButton,
                R.id.maxTokensButton,
                R.id.inferenceParamsButton,
                R.id.chatMemoryRow,
                R.id.chatMemoryButton,
                R.id.openRouterTransformsSwitch,
                R.id.autoDisableWebSearchSwitch,
                R.id.extendedDockSwitch,
                R.id.extendedTopBarSwitch,
                R.id.expandableInputSwitch,
                R.id.scrollButtonsSwitch,
                R.id.scrollProgressSwitch,
                R.id.volumeScrollSwitch,
                R.id.presetsExtendedSwitch,
                R.id.animateBarOnErrorSwitch,
                R.id.showCitationsSwitch
            ),
            SECTION_DATA to listOf(
                R.id.biometricsSwitch,
                R.id.autoSaveChatsSwitch,
                R.id.notificationsSwitch,
                R.id.copyOrdismissSwitch,
                R.id.copyOropenSwitch,
                R.id.allowDestructiveToolsSwitch,
                R.id.keepScreenOnSwitch,
                R.id.helpButton,
                R.id.licensesButton
            )
        )

        val visibleIds = sectionViewIds[section].orEmpty().toSet()
        val allIds = sectionViewIds.values.flatten().toSet()
        allIds.forEach { id ->
            view.findViewById<View>(id)?.setSectionVisible(id in visibleIds)
        }
    }

    private fun View.setSectionVisible(show: Boolean) {
        visibility = if (show) View.VISIBLE else View.GONE
        (parent as? View)?.takeIf { it is LinearLayout && (it as ViewGroup).childCount <= 3 }?.visibility = visibility
    }

    companion object {
        const val ARG_SECTION = "section"
        const val SECTION_APPEARANCE = "appearance"
        const val SECTION_VOICE = "voice"
        const val SECTION_HAPTICS = "haptics"
        const val SECTION_MODELS = "models"
        const val SECTION_ADVANCED = "advanced"
        const val SECTION_DATA = "data"

        fun newInstance(section: String): SettingsDetailFragment =
            SettingsDetailFragment().apply {
                arguments = bundleOf(ARG_SECTION to section)
            }
    }
}
