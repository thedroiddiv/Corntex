package com.thedroiddiv.menu.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ContextMenuTokens(
    val menuContainerCornerRadius: Dp,
    val menuItemCornerRadius: Dp,
    val menuContainerPadding: Dp,
    val menuItemPadding: Dp,
    val menuItemsSpacing: Dp,
    val menuItemIconSize: Dp,
    val menuElevation: Dp,
    val menuOutlineWidth: Dp,
) {
    val menuContainerShape = RoundedCornerShape(menuContainerCornerRadius)
    val menuItemShape = RoundedCornerShape(menuItemCornerRadius)
}

val defaultContextMenuTokens = ContextMenuTokens(
    menuContainerCornerRadius = 8.dp,  // contentRad + padding / 2
    menuItemCornerRadius = 4.dp,
    menuContainerPadding = 8.dp,
    menuItemPadding = 4.dp,
    menuItemsSpacing = 4.dp,
    menuItemIconSize = 12.dp,
    menuElevation = 16.dp,
    menuOutlineWidth = (1).dp
)
