package com.thedroiddiv.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider

/**
 * A [PopupPositionProvider] that adjusts the position of a popup to ensure it stays
 * within the screen bounds. It also handles positioning for cascading menus.
 *
 * @param positionPx The initial desired pixel offset for the popup.
 * @param menuIndex The index of the menu in a cascading menu structure.
 *                  0 for the primary menu, 1 for the first submenu, and so on.
 * @param density The current screen density.
 */
class AdjustedOffsetPopupPositionProvider(
    private val positionPx: IntOffset,
    private val menuIndex: Int,
    private val density: Density
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val screenBounds = IntRect(0, 0, windowSize.width, windowSize.height)
        return calculateAdjustedOffset(
            menuSize = popupContentSize,
            screenBounds = screenBounds,
        )
    }

    /**
     * Calculates the adjusted offset for the popup, ensuring it fits within the screen
     * and considering its position in a cascading menu.
     *
     * @param positionPx The initial desired pixel offset for the popup.
     * @param menuSize The size of the popup content.
     * @param screenBounds The boundaries of the screen.
     * @param menuIndex The index of the menu in a cascading menu structure.
     * @param density The current screen density.
     * @return The adjusted [IntOffset] for the popup.
     */
    internal fun calculateAdjustedOffset(
        menuSize: IntSize,
        screenBounds: IntRect
    ): IntOffset {
        with(density) {
            val padding = 8.dp.toPx().toInt()
            var adjustedX = positionPx.x
            var adjustedY = positionPx.y

            when {
                positionPx.x + menuSize.width > screenBounds.right -> {
                    if (menuIndex > 0) {
                        val leftPosition = positionPx.x - menuSize.width - padding
                        adjustedX = if (leftPosition >= screenBounds.left) {
                            leftPosition
                        } else {
                            maxOf(
                                screenBounds.left + padding,
                                minOf(
                                    screenBounds.right - menuSize.width - padding,
                                    positionPx.x
                                )
                            )
                        }
                    } else {
                        adjustedX = screenBounds.right - menuSize.width - padding
                    }
                }

                positionPx.x < screenBounds.left -> {
                    adjustedX = screenBounds.left + padding
                }
            }

            when {
                positionPx.y + menuSize.height > screenBounds.bottom -> {
                    adjustedY = screenBounds.bottom - menuSize.height - padding
                }

                positionPx.y < screenBounds.top -> {
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

/**
 * Remembers an [AdjustedOffsetPopupPositionProvider] instance.
 *
 * This function is useful for creating and remembering a [PopupPositionProvider]
 * that adjusts its position to stay within screen bounds, particularly for cascading menus.
 *
 * @param positionPx The initial desired pixel offset for the popup.
 * @param menuIndex The index of the menu in a cascading menu structure.
 *                  0 for the primary menu, 1 for the first submenu, and so on.
 * @return A remembered [AdjustedOffsetPopupPositionProvider] instance.
 */
@Composable
fun rememberAdjustedOffsetPopupPositionProvider(
    positionPx: IntOffset,
    menuIndex: Int,
): PopupPositionProvider = with(LocalDensity.current) {
    val density = LocalDensity.current
    remember(positionPx, menuIndex) {
        AdjustedOffsetPopupPositionProvider(
            positionPx = positionPx,
            menuIndex = menuIndex,
            density = density
        )
    }
}

