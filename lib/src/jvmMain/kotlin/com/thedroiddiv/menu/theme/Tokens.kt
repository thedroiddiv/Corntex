package com.thedroiddiv.menu.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ContextMenuTokens(
    val menuContainerCornerRadius: Dp,
    val menuItemCornerRadius: Dp,
    val menuContainerPadding: Dp,
    val menuItemPadding: Dp,
    val menuItemsSpacing: Dp,
    val menuItemIconSize: Dp,
    val menuElevation: Dp,
    val menuOutlineWidth: Dp,
    val menuMaxHeight: Dp,
    val menuMaxWidth: Dp,
    val menuMarginFromWindow: Dp,
) {
    val menuContainerShape = RoundedCornerShape(menuContainerCornerRadius)
    val menuItemShape = RoundedCornerShape(menuItemCornerRadius)
}

val defaultContextMenuTokens = ContextMenuTokens(
    menuContainerCornerRadius = 8.dp,  // contentRad + padding / 2
    menuItemCornerRadius = 4.dp,
    menuContainerPadding = 8.dp,
    menuItemPadding = 6.dp,
    menuItemsSpacing = 2.dp,
    menuItemIconSize = 12.dp,
    menuElevation = 16.dp,
    menuOutlineWidth = (1).dp,
    menuMaxHeight = 600.dp,
    menuMaxWidth = 260.dp,
    menuMarginFromWindow = 8.dp
)
