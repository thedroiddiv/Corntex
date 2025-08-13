package com.thedroiddiv.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.thedroiddiv.menu.components.MenuLevelContent
import org.jetbrains.annotations.VisibleForTesting

class PopupContextMenuRepresentation : ContextMenuRepresentation {
    @Composable
    override fun Representation(state: HierarchicalContextMenuState) {
        val density = LocalDensity.current
        val windowInfo = LocalWindowInfo.current
        val screenBounds = with(density) {
            IntRect(
                0, 0,
                windowInfo.containerSize.width,
                windowInfo.containerSize.height
            )
        }

        if (state.openMenus.isNotEmpty()) {
            state.openMenus.forEachIndexed { idx, menuLevel ->
                var menuSize by remember { mutableStateOf(IntSize.Zero) }
                val adjustedOffset = if (menuSize != IntSize.Zero) {
                    calculateAdjustedOffset(
                        originalOffset = menuLevel.position,
                        menuSize = menuSize,
                        screenBounds = screenBounds,
                        menuIndex = idx,
                        density = density
                    )
                } else {
                    menuLevel.position
                }
                Popup(
                    alignment = Alignment.TopStart,
                    offset = adjustedOffset,
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
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent(PointerEventPass.Main)
                                        }
                                    }
                                }
                                .onSizeChanged { size ->
                                    menuSize = size
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

    internal fun calculateAdjustedOffset(
        originalOffset: IntOffset,
        menuSize: IntSize,
        screenBounds: IntRect,
        menuIndex: Int,
        density: Density
    ): IntOffset {
        with(density) {
            val padding = 8.dp.toPx().toInt()
            var adjustedX = originalOffset.x
            var adjustedY = originalOffset.y

            when {
                originalOffset.x + menuSize.width > screenBounds.right -> {
                    if (menuIndex > 0) {
                        val leftPosition = originalOffset.x - menuSize.width - padding
                        adjustedX = if (leftPosition >= screenBounds.left) {
                            leftPosition
                        } else {
                            maxOf(screenBounds.left + padding,
                                minOf(screenBounds.right - menuSize.width - padding, originalOffset.x))
                        }
                    } else {
                        adjustedX = screenBounds.right - menuSize.width - padding
                    }
                }
                originalOffset.x < screenBounds.left -> {
                    adjustedX = screenBounds.left + padding
                }
            }

            when {
                originalOffset.y + menuSize.height > screenBounds.bottom -> {
                    adjustedY = screenBounds.bottom - menuSize.height - padding
                }
                originalOffset.y < screenBounds.top -> {
                    adjustedY = screenBounds.top + padding
                }
            }

            adjustedX = adjustedX.coerceIn(
                screenBounds.left + padding,
                screenBounds.right - menuSize.width - padding
            )
            adjustedY = adjustedY.coerceIn(
                screenBounds.top + padding,
                screenBounds.bottom - menuSize.height - padding
            )

            return IntOffset(adjustedX, adjustedY)
        }
    }
}
