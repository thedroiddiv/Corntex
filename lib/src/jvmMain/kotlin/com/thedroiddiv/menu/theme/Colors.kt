package com.thedroiddiv.menu.theme

import androidx.compose.ui.graphics.Color

/**
 * Defines the color scheme for context menu components.
 *
 * This class encapsulates all color properties needed to properly style a context menu,
 * including colors for different states such as enabled, selected, and disabled.
 *
 * @property containerColor The background color of the context menu container.
 * @property contentColor The default text and icon color for menu items.
 * @property selectedContainerColor The background color when a menu item is selected or hovered.
 * @property disabledContainerColor The background color for disabled menu items.
 * @property disableContentColor The text and icon color for disabled menu items.
 */
class ContextMenuColor(
    val containerColor: Color,
    val contentColor: Color,
    val selectedContainerColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val borderColor: Color,
    val dividerColor: Color
)

val darkContextMenuColor = ContextMenuColor(
    containerColor = Color(0xFF242328),
    contentColor = Color(0xFFDDE1E2),
    selectedContainerColor = Color(0xFF1B4173),
    disabledContainerColor = Color(0xFF242328), // TODO: currently same as enabled, check if needs to change
    disableContentColor = Color(0xFF6E6E73),
    borderColor = Color(0xFF414141),
    dividerColor = Color(0xFF414141)
)

val lightContextMenuColor = ContextMenuColor(
    containerColor = Color(0xFFEDECEC),
    contentColor = Color(0xFF242424),
    selectedContainerColor = Color(0xFF59A2FF),
    disabledContainerColor = Color(0xFFEDECEC), // TODO: currently same as enabled, check if needs to change
    disableContentColor = Color(0xFFB0AFAF),
    borderColor = Color(0xFFD5D4D4),
    dividerColor = Color(0xFFD5D4D4)
)

