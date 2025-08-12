package com.thedroiddiv.menu.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ContextMenuTokens(
    val menuCornerRadius: Dp,
    val menuItemCornerRadius: Dp,
    val menuContainerPadding: Dp,
    val menuItemPadding: Dp,
    val menuElevation: Dp
)

val defaultContextMenuTokens = ContextMenuTokens(
    menuCornerRadius = 8.dp,
    menuItemCornerRadius = 4.dp,
    menuContainerPadding = 4.dp,
    menuItemPadding = 0.dp,
    menuElevation = 8.dp
)