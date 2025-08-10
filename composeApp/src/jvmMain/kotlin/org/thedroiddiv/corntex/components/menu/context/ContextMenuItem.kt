package org.thedroiddiv.corntex.components.menu.context

import androidx.compose.ui.graphics.painter.Painter

sealed class ContextMenuTreeItem {
    data class Single(
        val label: String,
        val leadingIcon: Painter? = null,
        val trailingIcon: Painter? = null,
        val enabled: Boolean = true,
        val onClick: () -> Unit,
    ) : ContextMenuTreeItem()

    data class Submenu(
        val label: String,
        val leadingIcon: Painter? = null,
        val trailingIcon: Painter? = null,
        val enabled: Boolean = true,
        val subMenuItems: List<ContextMenuTreeItem>,
    ) : ContextMenuTreeItem()
}