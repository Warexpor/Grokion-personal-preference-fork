package io.github.stardomains3.oxproxion

import android.graphics.Color
import android.os.SystemClock
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

/** Opacity 0→1 ease-out over [durationMs] — Streamdown/Grok-style word fade-in. */
class StreamFadeSpan(
    private val startMs: Long = SystemClock.uptimeMillis(),
    private val durationMs: Long = 160L
) : CharacterStyle(), UpdateAppearance {

    fun isDone(now: Long = SystemClock.uptimeMillis()): Boolean =
        now - startMs >= durationMs

    override fun updateDrawState(tp: TextPaint) {
        val t = ((SystemClock.uptimeMillis() - startMs).toFloat() / durationMs).coerceIn(0f, 1f)
        val eased = 1f - (1f - t) * (1f - t) // ease-out quad
        val base = tp.color
        val a = (eased * Color.alpha(base).coerceAtLeast(255)).toInt().coerceIn(0, 255)
        tp.color = Color.argb(a, Color.red(base), Color.green(base), Color.blue(base))
    }
}
