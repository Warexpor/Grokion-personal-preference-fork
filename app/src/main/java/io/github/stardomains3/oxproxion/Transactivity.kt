package io.github.stardomains3.oxproxion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * STT disabled — formerly live transcription / hold-to-talk activity.
 * Kept as a no-op so any residual intents finish immediately.
 */
class Transactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // STT disabled
        finish()
    }
}
