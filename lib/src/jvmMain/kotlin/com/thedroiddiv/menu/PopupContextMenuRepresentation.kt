package com.thedroiddiv.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.thedroiddiv.menu.components.MenuLevelContent

class PopupContextMenuRepresentation : ContextMenuRepresentation {
    @Composable
    override fun Representation(state: HierarchicalContextMenuState) {
        if (state.openMenus.isNotEmpty()) {
            state.openMenus.forEachIndexed { idx, menuLevel ->
                Popup(
                    popupPositionProvider = rememberAdjustedOffsetPopupPositionProvider(
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
                            focusedIdx = menuLevel.focused
                        )
                    }
                )
            }
        }
    }
}

