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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenuArea(
    items: () -> List<ContextMenuEntry>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberContextMenuState()
    ContextMenuArea(
        items = items,
        state = state,
        modifier = modifier,
        content = content
    )
}

/**
 * Defines a container where context menu is available. Representation of menu is defined by
 * [LocalContextMenuRepresentation].
 *
 * This overload does not trigger the opening of the context menu; it's up to the caller to do so
 * by passing a [modifier] that does so.
 *
 * @param items List of context menu items. Final context menu contains all items from descendant [ContextMenuArea].
 * @param state [HierarchicalContextMenuState] of menu controlled by this area.
 * @param modifier The modifier to attach to the element; this should include the trigger that opens
 * the context menu (e.g. on right-click).
 * @param content The content of the [ContextMenuArea].
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContextMenuArea(
    items: () -> List<ContextMenuEntry>,
    state: HierarchicalContextMenuState,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.contextMenuOpenDetector(state, items)
    ) {
        content()
        ContextMenuTheme { LocalContextMenuRepresentation.current.Representation(state) }
    }
}


@Stable
data class MenuLevel(
    val items: List<ContextMenuEntry>,
    val position: IntOffset,
)

@Stable
class HierarchicalContextMenuState {
    var openMenus by mutableStateOf<List<MenuLevel>>(emptyList())
        private set

    fun show(position: IntOffset, items: List<ContextMenuEntry>) {
        openMenus = listOf(MenuLevel(items = items, position = position))
    }

    /**
     * Hides all currently visible context menus.
     */
    fun hide() {
        openMenus = emptyList()
    }

    fun onItemHover(item: ContextMenuEntry, bottomRight: IntOffset) {
        val itemLevelIndex = openMenus.indexOfFirst { it.items.contains(item) }
        if (itemLevelIndex == -1) return

        val newMenuStack = openMenus.subList(0, itemLevelIndex + 1)

        openMenus = if (item is ContextMenuEntry.Submenu && item.enabled) {
            val isAlreadyOpen = newMenuStack.size > itemLevelIndex + 1 &&
                    newMenuStack.getOrNull(itemLevelIndex + 1)?.items == item.submenuItems

            if (!isAlreadyOpen) {
                val subMenuPosition = IntOffset(bottomRight.x, bottomRight.y)
                newMenuStack + MenuLevel(items = item.submenuItems, position = subMenuPosition)
            } else {
                newMenuStack
            }
        } else {
            newMenuStack
        }
    }
}

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
    DefaultContextMenuRepresentation()
}
