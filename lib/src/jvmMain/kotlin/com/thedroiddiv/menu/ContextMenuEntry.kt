package com.thedroiddiv.menu

import androidx.compose.ui.graphics.painter.Painter


/**
 * Base sealed class for any entry that can be displayed in a context menu.
 */
sealed class ContextMenuEntry {

    /**
     * A clickable menu item with optional icon.
     *
     * @param label Display text for the menu item
     * @param icon Optional icon to display alongside the label
     * @param enabled Whether the item is interactive
     * @param onClick Action to perform when clicked
     */
    data class Single(
        val label: String,
        val icon: Painter? = null,
        val enabled: Boolean = true,
        val onClick: () -> Unit
    ) : ContextMenuEntry()

    /**
     * A menu item that expands to show nested menu items.
     *
     * @param label Display text for the submenu
     * @param submenuItems List of entries to display in the submenu
     * @param icon Optional icon to display alongside the label
     * @param enabled Whether the submenu can be opened
     */
    data class Submenu(
        val label: String,
        val submenuItems: List<ContextMenuEntry>,
        val icon: Painter? = null,
        val enabled: Boolean = true
    ) : ContextMenuEntry()

    /**
     * A visual separator line between menu items.
     */
    object Divider : ContextMenuEntry()
}
