package com.thedroiddiv.menu

import androidx.compose.ui.graphics.painter.Painter


/**
 * Base sealed class for any entry that can be displayed in a context menu.
 */
sealed class ContextMenuEntry {
    data class Single(
        val label: String,
        val icon: Painter? = null,
        val enabled: Boolean = true,
        val onClick: () -> Unit
    ) : ContextMenuEntry()

    data class Submenu(
        val label: String,
        val submenuItems: List<ContextMenuEntry>,
        val icon: Painter? = null,
        val enabled: Boolean = true
    ) : ContextMenuEntry()

    object Divider : ContextMenuEntry()
}
