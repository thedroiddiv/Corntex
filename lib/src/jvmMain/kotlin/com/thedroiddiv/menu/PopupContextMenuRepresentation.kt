package com.thedroiddiv.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.thedroiddiv.menu.components.MenuLevelContent
import com.thedroiddiv.menu.theme.ContextMenuTheme

class PopupContextMenuRepresentation : ContextMenuRepresentation {
    @Composable
    override fun Representation(state: HierarchicalContextMenuState) {
        if (state.openMenus.isNotEmpty()) {
            state.openMenus.forEachIndexed { idx, menuLevel ->
                // TODO: Decide which is best. Have multiple options here, position provider, cursor provider, etc
                val menuSize = rememberMenuSizeConstraints(idx, menuLevel.position)
                Popup(
                    popupPositionProvider = rememberContextMenuPopupPositionProvider(
                        positionPx = menuLevel.position,
                        menuIndex = idx
                    ),
                    onDismissRequest = { state.hide() },
                    properties = PopupProperties(
                        focusable = idx == 0,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    ),
                    onPreviewKeyEvent = { false },
                    content = {
                        MenuLevelContent(
                            items = menuLevel.items,
                            state = state,
                            focusedIdx = menuLevel.focused,
                            maxWidth = menuSize.width,
                            maxHeight = menuSize.height
                        )
                    }
                )
            }
        }
    }

    /**
     * Calculates and remembers the maximum size constraints for a context menu based on
     * the available space in the current window and the position of the menu.
     *
     * @param menuIndex The zero-based index of the menu level (0 = root menu, >0 = sub-menu).
     * @param position The position of the menu's anchor point in pixels, relative to the container.
     * @return A [DpSize] representing the maximum width and height the menu can occupy.
     */
    @Composable
    fun rememberMenuSizeConstraints(
        menuIndex: Int,
        position: IntOffset
    ): DpSize {
        val density = LocalDensity.current
        val containerSize = LocalWindowInfo.current.containerSize
        val maxH = ContextMenuTheme.tokens.menuMaxHeight
        val maxW = ContextMenuTheme.tokens.menuMaxWidth
        return remember(density, containerSize, menuIndex) {
            val maxHeight = min(
                maxH,
                with(density) {
                    if (menuIndex == 0) {
                        containerSize.height.toDp() - position.y.toDp()
                    } else {
                        containerSize.height.toDp()
                    }
                }
            )
            val maxWidth = min(
                maxW,
                with(density) {
                    if(menuIndex == 0){
                        containerSize.width.toDp() - position.x.toDp()
                    } else {
                        containerSize.width.toDp()
                    }
                }
            )
            DpSize(maxWidth, maxHeight)
        }
    }
}


