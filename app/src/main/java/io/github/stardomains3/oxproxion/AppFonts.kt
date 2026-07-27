package io.github.stardomains3.oxproxion

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

object AppFonts {
    const val SYSTEM_DEFAULT = "system_default"
    const val INTER = "inter_regular"
    const val ICELAND = "iceland_regular"

    /** Maps legacy oxproxion font prefs to a bundled selectable font. */
    fun normalizeSelectable(fontName: String): String = when (fontName) {
        SYSTEM_DEFAULT, INTER -> fontName
        else -> INTER
    }

    fun resolveSelectable(context: Context, fontName: String): Typeface {
        return when (normalizeSelectable(fontName)) {
            SYSTEM_DEFAULT -> Typeface.DEFAULT
            else -> ResourcesCompat.getFont(context, R.font.inter_regular) ?: Typeface.DEFAULT
        }
    }
}
