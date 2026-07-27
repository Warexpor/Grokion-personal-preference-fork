package io.github.stardomains3.oxproxion

import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.google.android.material.materialswitch.MaterialSwitch

fun MaterialSwitch.applyGrokionSwitchStyle() {
    thumbTintList = ContextCompat.getColorStateList(context, R.color.switch_thumb_tint)
    trackTintList = ContextCompat.getColorStateList(context, R.color.switch_track_tint)
    thumbTintMode = PorterDuff.Mode.SRC_ATOP
    trackTintMode = PorterDuff.Mode.SRC_ATOP
}
