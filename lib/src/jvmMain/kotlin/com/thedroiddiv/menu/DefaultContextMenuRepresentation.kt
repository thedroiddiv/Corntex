package com.thedroiddiv.menu


import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.thedroiddiv.menu.components.MenuLevelContent

/**
 * Default implementation of [ContextMenuRepresentation]
 */
class DefaultContextMenuRepresentation : ContextMenuRepresentation {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Representation(state: HierarchicalContextMenuState) {
        if (state.openMenus.isNotEmpty()) {
            val focusManager = LocalFocusManager.current
            Popup(
                alignment = Alignment.TopStart,
                offset = state.openMenus[0].position,
                onDismissRequest = { state.hide() },
                properties = PopupProperties(focusable = true),
                onPreviewKeyEvent = { false },
                onKeyEvent = { handleKeyEvent(it, focusManager, { state.hide() }) },
                content = {
                    Layout(
                        content = {
                            state.openMenus.forEachIndexed { idx, menuLevel ->
                                MenuLevelContent(
                                    items = menuLevel.items,
                                    state = state
                                )
                            }
                        },
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { state.hide() }
                    ) { measurables, constraints ->
                        val placeables = measurables.map { measurable ->
                            measurable.measure(constraints)
                        }

                        val zeroPos = state.openMenus[0].position
                        val offsets = state.openMenus.mapIndexed { idx, menuLevel ->
                            if (idx == 0) {
                                IntOffset(0, 0)
                            } else {
                                val prevOffset = state.openMenus[idx - 1].position
                                IntOffset(
                                    (menuLevel.position.x + prevOffset.x - zeroPos.x),
                                    (menuLevel.position.y + prevOffset.y - zeroPos.y)
                                )
                            }
                        }

                        var maxWidth = 0
                        var maxHeight = 0

                        placeables.forEachIndexed { idx, placeable ->
                            offsets.getOrNull(idx)?.let { offset ->
                                maxWidth = maxOf(maxWidth, offset.x + placeable.width)
                                maxHeight = maxOf(maxHeight, offset.y + placeable.height)
                            }
                        }
                        layout(maxWidth, maxHeight) {
                            placeables.forEachIndexed { idx, placeable ->
                                offsets.getOrNull(idx)?.let { offset ->
                                    placeable.placeRelative(offset.x, offset.y)
                                }
                            }
                        }
                    }
                }
            )
        }
    }


    private fun handleKeyEvent(
        keyEvent: KeyEvent,
        focusManager: FocusManager,
        onDismiss: () -> Unit
    ): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) return false
        return when (keyEvent.key) {
            Key.Escape -> {
                onDismiss()
                true
            }

            Key.DirectionDown -> {
                focusManager.moveFocus(FocusDirection.Down)
                true
            }

            Key.DirectionUp -> {
                focusManager.moveFocus(FocusDirection.Up)
                true
            }

            else -> false
        }
    }
}
