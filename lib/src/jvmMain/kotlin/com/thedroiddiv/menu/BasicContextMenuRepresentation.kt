package com.thedroiddiv.menu


import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi

/**
 * Representation of a context menu that is suitable for light themes of the application.
 */
val LightDefaultContextMenuRepresentation = DefaultContextMenuRepresentation(lightContextMenuColor)

/**
 * Representation of a context menu that is suitable for dark themes of the application.
 */
val DarkDefaultContextMenuRepresentation = DefaultContextMenuRepresentation(darkContextMenuColor)

/**
 * Custom representation of a context menu that allows to specify different colors.
 *
 * @param colors Set of colors for a context menu.
 */
class DefaultContextMenuRepresentation(
    private val colors: ContextMenuColor
) : ContextMenuRepresentation {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Representation(state: HierarchicalContextMenuState) {
        ContextMenu(colors, state)
    }
}
