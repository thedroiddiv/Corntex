package org.thedroiddiv.corntex.menu.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.thedroiddiv.corntex.menu.models.ContextMenuColor
import org.thedroiddiv.corntex.menu.models.ContextMenuTreeItem

@Composable
fun ContextSubmenuItem(
    modifier: Modifier = Modifier,
    color: ContextMenuColor,
    data: ContextMenuTreeItem.Submenu
) {
    ContextMenuItemImpl(
        modifier = modifier,
        enabled = data.enabled,
        label = data.label,
        leadingIcon = null,
        trailingIcon = null, // fixme submenu arrow
        onHover = data.onHover,
        color = color
    )
}
