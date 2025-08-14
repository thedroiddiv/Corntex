package com.thedroiddiv.menu

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.IntOffset
import java.util.IdentityHashMap

/**
 * State holder for hierarchical context menus that supports nested submenus.
 */
// TODO: Tests for keyboard interactions
@Stable
class HierarchicalContextMenuState {

    /**
     * Currently open menu levels, ordered from root to deepest submenu.
     */
    var openMenus by mutableStateOf<List<MenuLevel>>(emptyList())
        private set

    /**
     * Stores the layout position of each menu item to allow opening submenus
     * from keyboard interactions. Using IdentityHashMap to rely on referential equality.
     */
    private val itemOffsets = IdentityHashMap<ContextMenuEntry, IntOffset>()

    /**
     * Stores the current scroll position of the top-most menu.
     */
    private var topMenuScroll: Int = 0

    /**
     * Shows a context menu at the specified position.
     *
     * @param position Screen coordinates where the menu should appear
     * @param items Menu items to display
     */
    fun show(position: IntOffset, items: List<ContextMenuEntry>) {
        clearVolatileState()
        openMenus = listOf(MenuLevel(items = items, focused = null, position = position))
    }

    /**
     * Hides all currently visible context menus.
     */
    fun hide() {
        clearVolatileState()
        openMenus = emptyList()
    }

    /**
     * Clears transient state like item positions and scroll state.
     */
    private fun clearVolatileState() {
        itemOffsets.clear()
        topMenuScroll = 0
    }

    /**
     * Callback for composables to report their position within their parent.
     * This is used to correctly position submenus when opened via keyboard.
     */
    fun reportItemOffset(item: ContextMenuEntry, offset: IntOffset) {
        itemOffsets[item] = offset
    }

    /**
     * Callback for the top-most menu to report its scroll position.
     */
    fun reportTopMenuScroll(scroll: Int) {
        topMenuScroll = scroll
    }

    /**
     * Handles key events to enable keyboard navigation.
     *
     * @param event The KeyEvent captured by the menu.
     * @return `true` if the event was handled, `false` otherwise.
     */
    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        if (openMenus.isEmpty()) return false

        return when (event.key) {
            Key.DirectionDown -> {
                moveFocus(1)
                true
            }

            Key.DirectionUp -> {
                moveFocus(-1)
                true
            }

            Key.DirectionLeft -> {
                closeSubMenu()
                true
            }

            Key.DirectionRight -> {
                openSubMenu()
                true
            }

            Key.Enter -> {
                performItemClick()
                true
            }

            else -> false
        }
    }

    /**
     * Move focus by the specified direction in the top-most menu in stack
     * @param direction 1 for down, -1 for up
     */
    private fun moveFocus(direction: Int) {
        val lastMenuLevel = openMenus.lastOrNull() ?: return
        val items = lastMenuLevel.items
        if (items.isEmpty()) return

        val enabledIndices = items.indices.filter {
            val item = items[it]
            (item is ContextMenuEntry.Single && item.enabled) || (item is ContextMenuEntry.Submenu && item.enabled)
        }
        if (enabledIndices.isEmpty()) return

        val currentFocusedAbsoluteIndex = lastMenuLevel.focused
        val currentFocusedInEnabledList = if (currentFocusedAbsoluteIndex != null) {
            enabledIndices.indexOf(currentFocusedAbsoluteIndex)
        } else {
            -1
        }

        val nextIndexInEnabledList = when {
            currentFocusedInEnabledList == -1 && direction == 1 -> 0 // No focus, down arrow: focus first
            currentFocusedInEnabledList == -1 && direction == -1 -> enabledIndices.size - 1 // No focus, up arrow: focus last
            else -> (currentFocusedInEnabledList + direction + enabledIndices.size) % enabledIndices.size
        }

        val newFocusedAbsoluteIndex = enabledIndices[nextIndexInEnabledList]

        openMenus = openMenus.dropLast(1) + lastMenuLevel.copy(focused = newFocusedAbsoluteIndex)
    }

    /**
     * Closes the top-most submenu
     */
    private fun closeSubMenu() {
        if (openMenus.size > 1) {
            openMenus = openMenus.dropLast(1)
        }
    }

    /**
     * Open the submenu if focused item is [ContextMenuEntry.Submenu]  and it's enabled
     */
    private fun openSubMenu() {
        val item = getFocusedItem() ?: return
        if (item is ContextMenuEntry.Submenu && item.enabled) {
            val itemOffset = itemOffsets[item] ?: return
            val isAlreadyOpen = openMenus.size > openMenus.size &&
                    openMenus.getOrNull(openMenus.size)?.items == item.submenuItems
            if (isAlreadyOpen) return
            val parentMenuLevel = openMenus.last()
            val subMenuPosition = IntOffset(
                parentMenuLevel.position.x + itemOffset.x,
                parentMenuLevel.position.y + itemOffset.y
            )
            openMenus = openMenus + MenuLevel(
                items = item.submenuItems,
                focused = null,
                position = subMenuPosition
            )
        }
    }

    /**
     * Perform click on an item
     */
    private fun performItemClick() {
        val item = getFocusedItem() ?: return
        if (item is ContextMenuEntry.Submenu && item.enabled) openSubMenu()
        if (item is ContextMenuEntry.Single && item.enabled) {
            item.onClick()
            hide()
        }
    }

    /**
     * Returns the focused item in top-most menu
     */
    private fun getFocusedItem(): ContextMenuEntry? {
        val lastMenuLevel = openMenus.lastOrNull() ?: return null
        val focusedIndex = lastMenuLevel.focused ?: return null
        val item = lastMenuLevel.items.getOrNull(focusedIndex) ?: return null
        return item
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

        val newMenuStack = openMenus.subList(
            0,
            itemLevelIndex
        ) + openMenus[itemLevelIndex].copy(focused = itemIndex)

        openMenus = if (item is ContextMenuEntry.Submenu && item.enabled) {
            val isAlreadyOpen = newMenuStack.size > itemLevelIndex + 1 &&
                    newMenuStack.getOrNull(itemLevelIndex + 1)?.items == item.submenuItems

            if (!isAlreadyOpen) {
                val prev = newMenuStack.lastOrNull()
                val subMenuPosition = if (prev == null) {
                    IntOffset(bottomRight.x, bottomRight.y)
                } else {
                    IntOffset(prev.position.x + bottomRight.x, prev.position.y + bottomRight.y)
                }
                newMenuStack + MenuLevel(
                    items = item.submenuItems,
                    focused = null,
                    position = subMenuPosition
                )
            } else {
                newMenuStack
            }
        } else {
            newMenuStack
        }
    }
}
