package com.thedroiddiv.menu

import androidx.compose.runtime.Composable

/**
 * Implementations of this interface are responsible for displaying context menus. There is one
 * implementations out of the box: [PopupContextMenuRepresentation]
 * To change currently used representation, different value for [LocalContextMenuRepresentation]
 * could be provided.
 */
interface ContextMenuRepresentation {
    @Composable
    fun Representation(state: HierarchicalContextMenuState)
}
