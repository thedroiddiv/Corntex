package com.thedroiddiv.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider


/**
 * A [PopupPositionProvider] that positions the popup at the given offsets and alignment.
 *
 * @param positionPx The offset of the popup's location, in pixels.
 * [calculatePosition]. If `false`, it is relative to the window.
 * @param offsetPx Extra offset to be added to the position of the popup, in pixels.
 * @param windowMarginPx Defines the area within the window that limits the placement of the popup,
 * in pixels.
 */
class ContextMenuPopupPositionProvider(
    val menuIndex: Int,
    val positionPx: IntOffset,
    val offsetPx: IntOffset = IntOffset.Zero, // todo: remove if not needed
    val windowMarginPx: Int,
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return calculateAdjustedOffset(
            menuSize = popupContentSize,
            screenBounds = IntRect(0, 0, windowSize.width, windowSize.height)
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
     * @return The adjusted [IntOffset] for the popup.
     */
    internal fun calculateAdjustedOffset(
        menuSize: IntSize,
        screenBounds: IntRect
    ): IntOffset = runCatching {
        var adjustedX = positionPx.x
        var adjustedY = positionPx.y

        // If menu's size is more than screen bounds, return initial position
        if (menuSize.height + windowMarginPx > screenBounds.size.height
            || menuSize.width + windowMarginPx > screenBounds.size.width
        ) {
            return positionPx
        }

        when {
            positionPx.x + menuSize.width > screenBounds.right -> {
                if (menuIndex > 0) {
                    val leftPosition = positionPx.x - menuSize.width - windowMarginPx
                    adjustedX = if (leftPosition >= screenBounds.left) {
                        leftPosition
                    } else {
                        maxOf(
                            screenBounds.left + windowMarginPx,
                            minOf(
                                screenBounds.right - menuSize.width - windowMarginPx,
                                positionPx.x
                            )
                        )
                    }
                } else {
                    adjustedX = screenBounds.right - menuSize.width - windowMarginPx
                }
            }

            positionPx.x < screenBounds.left -> {
                adjustedX = screenBounds.left + windowMarginPx
            }
        }

        when {
            positionPx.y + menuSize.height > screenBounds.bottom -> {
                adjustedY = screenBounds.bottom - menuSize.height - windowMarginPx
            }

            positionPx.y < screenBounds.top -> {
                adjustedY = screenBounds.top + windowMarginPx
            }
        }

        adjustedX = adjustedX.coerceIn(
            screenBounds.left + windowMarginPx,
            screenBounds.right - menuSize.width - windowMarginPx
        )
        adjustedY = adjustedY.coerceIn(
            screenBounds.top + windowMarginPx,
            screenBounds.bottom - menuSize.height - windowMarginPx
        )

        return IntOffset(adjustedX, adjustedY)
    }.getOrElse { f ->
        f.printStackTrace()
        positionPx
    }
}

/**
 * A [PopupPositionProvider] that positions the popup at the given position relative to the anchor.
 *
 * @param menuIndex the index of the menu in a cascading menu structure.
 * @param positionPx the offset, in pixels, relative to the anchor, to position the popup at.
 * @param offset [DpOffset] to be added to the position of the popup.
 * @param windowMargin Defines the area within the window that limits the placement of the popup.
 */
@Composable
fun rememberContextMenuPopupPositionProvider(
    menuIndex: Int,
    positionPx: IntOffset,
    offset: DpOffset = DpOffset.Zero,
    windowMargin: Dp = 4.dp
): PopupPositionProvider = with(LocalDensity.current) {
    val offsetPx = IntOffset(offset.x.toPx().toInt(), offset.y.toPx().toInt())
    val windowMarginPx = windowMargin.roundToPx()
    remember(positionPx, offsetPx, windowMarginPx) {
        ContextMenuPopupPositionProvider(
            menuIndex = menuIndex,
            positionPx = positionPx,
            offsetPx = offsetPx,
            windowMarginPx = windowMarginPx
        )
    }
}

