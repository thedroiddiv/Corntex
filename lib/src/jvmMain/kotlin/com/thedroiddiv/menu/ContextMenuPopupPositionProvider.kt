/**
 * Copyright (c) Divyansh Kushwaha <thedroiddiv@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
               adjustedX = screenBounds.right - menuSize.width - windowMarginPx
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
            positionPx = positionPx,
            offsetPx = offsetPx,
            windowMarginPx = windowMarginPx
        )
    }
}

