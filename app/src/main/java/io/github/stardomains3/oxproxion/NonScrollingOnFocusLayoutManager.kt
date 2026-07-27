package io.github.stardomains3.oxproxion

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NonScrollingOnFocusLayoutManager(context: Context) : LinearLayoutManager(context) {

    /**
     * Ignore bring-into-view from focus/selection/TextView layout.
     * During streaming, setText + stick-to-bottom scrollBy otherwise race and flash.
     */
    override fun requestChildRectangleOnScreen(
        parent: RecyclerView, child: View, rect: Rect,
        immediate: Boolean, focusedChildVisible: Boolean
    ): Boolean = false
}
