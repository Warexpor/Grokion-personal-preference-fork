package io.github.stardomains3.oxproxion

import android.graphics.Color
import android.os.SystemClock
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

/** Blinking block cursor ▌ (~530ms on/off) for streaming parity. */
class StreamCursorSpan(
    private val color: Int,
    private val periodMs: Long = 1060L
) : CharacterStyle(), UpdateAppearance {
    override fun updateDrawState(tp: TextPaint) {
        val phase = (SystemClock.uptimeMillis() % periodMs) < (periodMs / 2)
        tp.color = if (phase) color else Color.TRANSPARENT
        tp.bgColor = Color.TRANSPARENT
    }
}
