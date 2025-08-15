package com.thedroiddiv.menu

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset

/**
 * Represents a single level in a hierarchical context menu.
 *
 * @param items The menu items to display at this level
 * @param focused Index of the menu item which is currently in focused state
 * @param position Screen position where this menu level should appear
 */
@Stable
data class MenuLevel(
    val items: List<ContextMenuEntry>,
    val focused: Int?,
    val position: IntOffset,
    val scroll: Int
)
