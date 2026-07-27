package io.github.stardomains3.oxproxion

import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

/** Grok Ask switch: M3 52×32 track, monochrome thumb/track from theme colors. */
fun SwitchCompat.applyGrokionSwitchStyle() {
    showText = false
    thumbDrawable = ContextCompat.getDrawable(context, R.drawable.switch_thumb)?.mutate()
    trackDrawable = ContextCompat.getDrawable(context, R.drawable.switch_track)?.mutate()
    thumbTintList = null
    trackTintList = null
    switchMinWidth = resources.getDimensionPixelSize(R.dimen.grokion_switch_min_width)
    thumbTextPadding = 0
    // Keep handle inset so the stadium reads like Grok/Material3, not a stretched oval.
    switchPadding = 0
}
