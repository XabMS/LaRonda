package com.lostandfondue.ronda

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Pads this view by the system bars (status bar / navigation bar) insets.
 *
 * From targetSdk 36 onward, edge-to-edge is enforced unconditionally (no opt-out
 * flag or theme attribute works anymore), so every legacy (non-Compose) screen has
 * to apply system bar insets manually or its content draws behind the status bar
 * and action bar.
 */
fun View.applySystemBarInsetsAsPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
        WindowInsetsCompat.CONSUMED
    }
}
