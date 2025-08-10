package org.thedroiddiv.corntex.menu.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter

/**
 * Represents an item in a context menu hierarchy.
 *
 * This sealed class defines the structure for context menu items, supporting both
 * simple clickable items and nested submenus. All menu items share common properties
 * like labels, icons, and enabled state.
 *
 * @param label The display text for the menu item.
 * @param leadingIcon Optional icon displayed before the label (e.g., file type icons).
 * @param trailingIcon Optional icon displayed after the label (e.g., keyboard shortcuts, submenu indicators).
 * @param enabled Whether the menu item is interactive. Disabled items are visually muted and non-clickable.
 */
sealed class ContextMenuTreeItem(
    val label: String,
    val leadingIcon: Painter? = null,
    val trailingIcon: Painter? = null,
    val enabled: Boolean = true,
) {
    /**
     * A simple menu item that performs an action when clicked.
     *
     * Single items represent terminal actions in the menu hierarchy, such as
     * "New File", "Copy", "Delete", etc. They trigger immediate actions when
     * the user clicks on them.
     *
     * @param label The display text for the menu item.
     * @param leadingIcon Optional icon displayed before the label.
     * @param trailingIcon Optional icon displayed after the label (commonly used for keyboard shortcuts).
     * @param enabled Whether the menu item can be clicked.
     * @param onClick Callback invoked when the item is clicked. The [Offset] parameter
     *                represents the top-right of the click item, relative to it's parent in menu-tree hierarchy,
     *                which can be useful for positioning dialogs or other UI elements relative to the click.
     */
    class Single(
        label: String,
        leadingIcon: Painter?,
        trailingIcon: Painter?,
        enabled: Boolean,
        val onClick: (Offset) -> Unit,
    ) : ContextMenuTreeItem(label, leadingIcon, trailingIcon, enabled)

    /**
     * A menu item that contains nested sub-items.
     *
     * Submenu items create hierarchical menu structures by containing additional
     * menu items. They typically display a right-pointing arrow or similar indicator
     * and show their sub-items when hovered over or clicked.
     *
     * @param label The display text for the submenu item.
     * @param leadingIcon Optional icon displayed before the label.
     * @param trailingIcon Optional icon displayed after the label (commonly a right arrow for submenus).
     * @param enabled Whether the submenu can be opened.
     * @param onHover Callback invoked when the user hovers over or leaves the submenu item.
     *                The [Hover] parameter indicates the hover event type (ENTER or EXIT).
     *                The [Offset] parameter represents the top-right of hovered item, relative to it's parent in menu-tree hierarchy,
     *                useful for positioning the submenu relative to the parent item.
     * @param subMenuItems The list of menu items contained within this submenu. Can include
     *                     both [Single] items and nested [Submenu] items for multi-level hierarchies.
     */
    class Submenu(
        label: String,
        leadingIcon: Painter?,
        trailingIcon: Painter?,
        enabled: Boolean,
        val onHover: (Hover, Offset) -> Unit,
        val subMenuItems: List<ContextMenuTreeItem>,
    ) : ContextMenuTreeItem(label, leadingIcon, trailingIcon, enabled)
}

