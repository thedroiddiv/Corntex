package com.thedroiddiv.menu


import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.thedroiddiv.menu.modifiers.contextMenuOpenDetector
import com.thedroiddiv.menu.theme.ContextMenuTheme

/**
 * Defines a container where context menu is available with automatic right-click detection.
 *
 * @param modifier Modifier to be applied to the container
 * @param items Provider function returning list of context menu items
 * @param content The content to display inside the context menu area
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenuArea(
    modifier: Modifier = Modifier,
    items: () -> List<ContextMenuEntry>,
    content: @Composable () -> Unit
) {
    val state = rememberContextMenuState()
    Box(modifier = modifier.contextMenuOpenDetector(state, items)) {
        content()
        ContextMenuTheme { LocalContextMenuRepresentation.current.Representation(state) }
    }
}

/**
 * Defines a container where context menu is available. Representation of menu is defined by
 * [LocalContextMenuRepresentation].
 *
 * This doesn't show context automatically, caller needs to call `state.show()` to display context menu
 * on some user action.
 *
 * @param modifier The modifier to attach to the element; this should include the trigger that opens
 * @param state [HierarchicalContextMenuState] of menu controlled by this area.
 * the context menu (e.g. on right-click).
 * @param content The content of the [ContextMenuArea].
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContextMenuArea(
    modifier: Modifier = Modifier,
    state: HierarchicalContextMenuState,
    content: @Composable () -> Unit
) {
    content()
    ContextMenuTheme { LocalContextMenuRepresentation.current.Representation(state) }
}



/**
 * Represents a single level in a hierarchical context menu.
 *
 * @param items The menu items to display at this level
 * @param position Screen position where this menu level should appear
 */
@Stable
data class MenuLevel(
    val items: List<ContextMenuEntry>,
    val position: IntOffset,
)

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
        openMenus = listOf(MenuLevel(items = items, position = position))
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
        val itemLevelIndex = openMenus.indexOfFirst { it.items.contains(item) }
        if (itemLevelIndex == -1) return

        val newMenuStack = openMenus.subList(0, itemLevelIndex + 1)

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
                newMenuStack + MenuLevel(items = item.submenuItems, position = subMenuPosition)
            } else {
                newMenuStack
            }
        } else {
            newMenuStack
        }
    }
}

/**
 * Creates and remembers a [HierarchicalContextMenuState] for the current composition.
 */
@Composable
fun rememberContextMenuState() = remember { HierarchicalContextMenuState() }

/**
 * Implementations of this interface are responsible for displaying context menus. There is one
 * implementations out of the box: [DefaultContextMenuRepresentation]
 * To change currently used representation, different value for [LocalContextMenuRepresentation]
 * could be provided.
 */
interface ContextMenuRepresentation {
    @Composable
    fun Representation(state: HierarchicalContextMenuState)
}

/**
 * Composition local that keeps [ContextMenuRepresentation] which is used by [ContextMenuArea]s.
 */
val LocalContextMenuRepresentation:
        ProvidableCompositionLocal<ContextMenuRepresentation> = staticCompositionLocalOf {
    PopupContextMenuRepresentation()
}
