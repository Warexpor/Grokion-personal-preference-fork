package io.github.stardomains3.oxproxion

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object BiometricGateHelper {

    fun gateIfNeeded(
        activity: AppCompatActivity,
        onUnlocked: () -> Unit
    ) {
        val prefs = SharedPreferencesHelper(activity)
        if (!prefs.getBiometricEnabled()) {
            onUnlocked()
            return
        }

        when (BiometricManager.from(activity).canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> showPrompt(activity, onUnlocked)
            else -> {
                prefs.saveBiometricEnabled(false)
                AppToast.makeText(
                    activity,
                    "Biometrics unavailable—proceeding without lock.",
                    AppToast.LENGTH_LONG
                ).show()
                onUnlocked()
            }
        }
    }

    private fun showPrompt(
        activity: AppCompatActivity,
        onUnlocked: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    AppToast.makeText(activity, "Authentication error", AppToast.LENGTH_SHORT).show()
                    activity.finish()
                }

                override fun onAuthenticationFailed() {
                    AppToast.makeText(activity, "Authentication failed", AppToast.LENGTH_SHORT).show()
                    activity.finish()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onUnlocked()
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock GradatiON")
            .setSubtitle("Use your biometric credential")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()

        prompt.authenticate(info)
    }
}
