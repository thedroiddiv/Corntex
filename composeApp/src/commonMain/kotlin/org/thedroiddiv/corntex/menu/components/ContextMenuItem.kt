package org.thedroiddiv.corntex.menu.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import org.thedroiddiv.corntex.menu.models.ContextMenuColor
import org.thedroiddiv.corntex.menu.models.ContextMenuTreeItem

@Composable
fun ContextMenuItem(
    modifier: Modifier = Modifier,
    color: ContextMenuColor,
    data: ContextMenuTreeItem.Single
) {
    var relativePosition by remember { mutableStateOf<Offset?>(null) }
    ContextMenuItemImpl(
        modifier = modifier
            .onGloballyPositioned {
                relativePosition = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .clickable(
                enabled = data.enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // fixme: check if recomposition is needed to capture latest value of [relativePosition]
                relativePosition?.let { data.onClick(it) }
            },
        enabled = data.enabled,
        label = data.label,
        leadingIcon = data.leadingIcon,
        trailingIcon = data.trailingIcon,
        color = color
    )
}
