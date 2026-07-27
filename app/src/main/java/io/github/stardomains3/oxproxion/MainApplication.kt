package io.github.stardomains3.oxproxion

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Fail fast if the native lib is missing; Room open also loads it.
        try {
            System.loadLibrary("sqlcipher")
        } catch (e: UnsatisfiedLinkError) {
            throw IllegalStateException("SQLCipher native library failed to load", e)
        }
        // Kill leftover sticky "Running" FGS notifs from older builds
        ForegroundService.clearLegacyRunningNotification(this)
    }
}
