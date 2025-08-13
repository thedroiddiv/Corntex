package com.thedroiddiv.menu

import org.jetbrains.compose.resources.DrawableResource


/**
 * Base sealed class for any entry that can be displayed in a context menu.
 */
sealed interface ContextMenuEntry {

    /**
     * A clickable menu item with optional icon.
     *
     * @param label Display text for the menu item
     * @param leadingIcon Optional icon to display pre to label
     * @param trailingIcon Optional icon to display post to label
     * @param enabled Whether the item is interactive
     * @param onClick Action to perform when clicked
     */
    data class Single(
        val label: String,
        val leadingIcon: DrawableResource? = null,
        val trailingIcon: DrawableResource? = null,
        val enabled: Boolean = true,
        val onClick: () -> Unit
    ) : ContextMenuEntry

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
        val icon: DrawableResource? = null,
        val enabled: Boolean = true
    ) : ContextMenuEntry

    /**
     * A visual separator line between menu items.
     */
    object Divider : ContextMenuEntry
}
