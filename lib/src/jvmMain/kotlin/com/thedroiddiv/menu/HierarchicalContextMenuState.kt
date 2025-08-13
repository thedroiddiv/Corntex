package com.thedroiddiv.menu

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset

/**
 * State holder for hierarchical context menus that supports nested submenus.
 */
@Stable
class HierarchicalContextMenuState {

    /**
     * Currently open menu levels, ordered from root to deepest submenu.
     */
    var openMenus by mutableStateOf<List<MenuLevel>>(emptyList())
        private set

    /**
     * Shows a context menu at the specified position.
     *
     * @param position Screen coordinates where the menu should appear
     * @param items Menu items to display
     */
    fun show(position: IntOffset, items: List<ContextMenuEntry>) {
        openMenus = listOf(MenuLevel(items = items, focused = null, position = position))
    }

    /**
     * Hides all currently visible context menus.
     */
    fun hide() {
        openMenus = emptyList()
    }

    /**
     * Handles hovering over a menu item, opening submenus as needed.
     *
     * @param item The menu item being hovered
     * @param bottomRight Position for submenu placement
     */
    fun onItemHover(item: ContextMenuEntry, bottomRight: IntOffset) {
        var itemIndex = -1
        val itemLevelIndex = openMenus.indexOfFirst {
            itemIndex = it.items.indexOfFirst { it === item }
            itemIndex != -1
        }
        if (itemLevelIndex == -1) return

        val newMenuStack = openMenus.subList(0, itemLevelIndex) + openMenus[itemLevelIndex].copy(focused = itemIndex)

        openMenus = if (item is ContextMenuEntry.Submenu && item.enabled) {
            val isAlreadyOpen = newMenuStack.size > itemLevelIndex + 1 &&
                    newMenuStack.getOrNull(itemLevelIndex + 1)?.items == item.submenuItems

            if (!isAlreadyOpen) {
                val prev = newMenuStack.lastOrNull()
                val subMenuPosition =if (prev == null) {
                    IntOffset(bottomRight.x, bottomRight.y)
                } else {
                    IntOffset(prev.position.x + bottomRight.x, prev.position.y + bottomRight.y)
                }
                newMenuStack + MenuLevel(items = item.submenuItems, focused = null, position = subMenuPosition)
            } else {
                newMenuStack
            }
        } else {
            newMenuStack
        }
    }
}
