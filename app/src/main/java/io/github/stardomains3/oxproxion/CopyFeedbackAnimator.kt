package io.github.stardomains3.oxproxion

import android.content.res.ColorStateList
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import java.util.WeakHashMap

object CopyFeedbackAnimator {
    private val pendingResets = WeakHashMap<ImageView, Runnable>()

    fun play(button: ImageView) {
        pendingResets.remove(button)?.let { button.removeCallbacks(it) }

        val context = button.context
        val normalTint = button.imageTintList
            ?: ColorStateList.valueOf(ContextCompat.getColor(context, R.color.xai_icon))
        val checkTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.xai_ink))

        button.animate().cancel()
        button.animate()
            .scaleX(0.78f)
            .scaleY(0.78f)
            .alpha(0.55f)
            .setDuration(90)
            .withEndAction {
                button.setImageResource(R.drawable.ic_check)
                button.imageTintList = checkTint
                button.scaleX = 0.65f
                button.scaleY = 0.65f
                button.alpha = 1f
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(260)
                    .setInterpolator(OvershootInterpolator(1.15f))
                    .start()
            }
            .start()

        val reset = Runnable {
            button.animate().cancel()
            button.animate()
                .scaleX(0.82f)
                .scaleY(0.82f)
                .alpha(0.7f)
                .setDuration(110)
                .withEndAction {
                    button.setImageResource(R.drawable.ic_copi)
                    button.imageTintList = normalTint
                    button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(160)
                        .start()
                }
                .start()
            pendingResets.remove(button)
        }
        pendingResets[button] = reset
        button.postDelayed(reset, 950L)
    }
}
