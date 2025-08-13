package com.thedroiddiv.menu


import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.thedroiddiv.menu.modifiers.contextMenuOpenDetector

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
        LocalContextMenuRepresentation.current.Representation(state)
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
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenuArea(
    modifier: Modifier = Modifier,
    state: HierarchicalContextMenuState,
    content: @Composable () -> Unit
) {
    content()
    LocalContextMenuRepresentation.current.Representation(state)
}

/**
 * Creates and remembers a [HierarchicalContextMenuState] for the current composition.
 */
@Composable
fun rememberContextMenuState() = remember { HierarchicalContextMenuState() }


/**
 * Composition local that keeps [ContextMenuRepresentation] which is used by [ContextMenuArea]s.
 */
val LocalContextMenuRepresentation: ProvidableCompositionLocal<ContextMenuRepresentation> =
    staticCompositionLocalOf { PopupContextMenuRepresentation() }
