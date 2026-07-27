package io.github.stardomains3.oxproxion

import android.content.Context
import android.widget.Toast

/**
 * Global toast gate. All app toasts are silenced — answer-ready
 * system notifications are the only status surface.
 *
 * Call sites keep `AppToast.makeText(...).show()` so they can be
 * re-enabled later without hunting through the codebase.
 */
object AppToast {
    const val LENGTH_SHORT: Int = Toast.LENGTH_SHORT
    const val LENGTH_LONG: Int = Toast.LENGTH_LONG

    @JvmStatic
    fun makeText(context: Context?, text: CharSequence?, duration: Int): Handle = Handle

    object Handle {
        fun show() {
            // ponytail: silenced — re-enable here if a toast class must return
        }
    }
}
