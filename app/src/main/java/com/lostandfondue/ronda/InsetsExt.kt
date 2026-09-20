package com.lostandfondue.ronda

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Pads this view by the system bars (status bar / navigation bar) insets, and
 * optionally by the software keyboard (IME) one.
 *
 * From targetSdk 36 onward, edge-to-edge is enforced unconditionally (no opt-out
 * flag or theme attribute works anymore), so every legacy (non-Compose) screen has
 * to apply system bar insets manually or its content draws behind the status bar
 * and action bar.
 *
 * Edge-to-edge also turns `android:windowSoftInputMode="adjustResize"` into a
 * no-op: the window no longer shrinks when the keyboard opens, so anything
 * pinned to the bottom ends up behind it. Screens with text fields pass
 * [includeIme] so the keyboard height is padded in as well; the insets are
 * re-dispatched every time the IME shows or hides, which keeps a bottom button
 * bar on screen. On API < 35 `adjustResize` still does the work and the IME
 * inset is 0, so the same call is correct on both.
 *
 * [onImeVisibilityChanged], when given, is called on every inset dispatch with
 * whether the keyboard is currently showing. This listener is the only place
 * that can tell: it consumes the insets, so children never see them.
 */
fun View.applySystemBarInsetsAsPadding(
    includeIme: Boolean = false,
    onImeVisibilityChanged: ((visible: Boolean) -> Unit)? = null,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        var types = WindowInsetsCompat.Type.systemBars()
        if (includeIme) types = types or WindowInsetsCompat.Type.ime()
        // Combining the types takes the larger of the two bottom insets, so the
        // navigation bar is not counted twice while the keyboard is up.
        val insets = windowInsets.getInsets(types)
        v.updatePadding(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom,
        )
        onImeVisibilityChanged?.invoke(windowInsets.isVisible(WindowInsetsCompat.Type.ime()))
        WindowInsetsCompat.CONSUMED
    }
}
