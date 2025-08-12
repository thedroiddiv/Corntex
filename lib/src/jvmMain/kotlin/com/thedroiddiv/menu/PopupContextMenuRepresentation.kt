package com.thedroiddiv.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.thedroiddiv.menu.components.MenuLevelContent

class PopupContextMenuRepresentation : ContextMenuRepresentation {
    @Composable
    override fun Representation(state: HierarchicalContextMenuState) {
        if (state.openMenus.isNotEmpty()) {
            state.openMenus.forEachIndexed { idx, menuLevel ->
                Popup(
                    alignment = Alignment.TopStart,
                    offset = menuLevel.position,
                    onDismissRequest = { state.hide() },
                    properties = PopupProperties(
                        focusable = idx == 0,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        clippingEnabled = false
                    ),
                    onPreviewKeyEvent = { false },
                    content = {
                        Box(
                            modifier = Modifier.pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent(PointerEventPass.Main)
                                    }
                                }
                            }
                        ) {
                            MenuLevelContent(
                                items = menuLevel.items,
                                state = state
                            )
                        }
                    }
                )
            }
        }
    }
}