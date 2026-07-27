package io.github.stardomains3.oxproxion

import android.content.Context
import android.provider.Settings
import android.view.animation.PathInterpolator
import androidx.fragment.app.FragmentTransaction

object Motion {
    val easeOut = PathInterpolator(0.2f, 0f, 0f, 1f)

    fun areAnimationsEnabled(context: Context): Boolean {
        return try {
            val durationScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            durationScale != 0.0f
        } catch (_: Exception) {
            true
        }
    }

    fun FragmentTransaction.withGrokStackAnimations(): FragmentTransaction {
        return setCustomAnimations(
            R.anim.fragment_open_enter,
            R.anim.fragment_open_exit,
            R.anim.fragment_close_enter,
            R.anim.fragment_close_exit
        )
    }

    fun FragmentTransaction.withGrokFadeAnimations(): FragmentTransaction {
        return setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
    }
}
