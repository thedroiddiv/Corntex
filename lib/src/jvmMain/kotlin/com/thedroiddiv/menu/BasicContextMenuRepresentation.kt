package com.thedroiddiv.menu


import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi

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
        ContextMenuInternalImpl(colors, state)
    }
}
