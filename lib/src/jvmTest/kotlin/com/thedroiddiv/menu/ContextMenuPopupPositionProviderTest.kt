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

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Test

class ContextMenuPopupPositionProviderTest {

    private lateinit var popupPositionProvider: ContextMenuPopupPositionProvider
    private val paddingPx = 8 // 8dp at density=1f

    private fun adjust(
        originalOffset: IntOffset,
        menuSize: IntSize
    ): IntOffset {
        val windowSize = IntSize(200, 200)
        popupPositionProvider = ContextMenuPopupPositionProvider(
            positionPx = originalOffset,
            offsetPx = IntOffset.Zero,
            windowMarginPx = paddingPx,
        )

        return popupPositionProvider.calculateAdjustedOffset(
            menuSize = menuSize,
            screenBounds = IntRect(0, 0, windowSize.width, windowSize.height)
        )
    }

    @Test
    fun `menu fits fully - no adjustment`() {
        val result = adjust(IntOffset(50, 50), IntSize(50, 50))
        assertEquals(IntOffset(50, 50), result)
    }

    @Test
    fun `root menu off right edge - shift left to fit`() {
        val result = adjust(IntOffset(180, 50), IntSize(50, 50))
        assertEquals(IntOffset(200 - 50 - paddingPx, 50), result)
    }

    @Test
    fun `submenu off right edge - shift left`() {
        val result = adjust(IntOffset(180, 50), IntSize(50, 50))
        assertEquals(IntOffset(200 - 50 - paddingPx, 50), result)
    }

    @Test
    fun `menu off left edge - shift right to padding`() {
        val result = adjust(IntOffset(-20, 50), IntSize(40, 40))
        assertEquals(IntOffset(paddingPx, 50), result)
    }

    @Test
    fun `menu off bottom edge - shift up`() {
        val result = adjust(IntOffset(50, 190), IntSize(40, 40))
        assertEquals(IntOffset(50, 200 - 40 - paddingPx), result)
    }

    @Test
    fun `menu off top edge - shift down`() {
        val result = adjust(IntOffset(50, -10), IntSize(40, 40))
        assertEquals(IntOffset(50, paddingPx), result)
    }

    @Test
    fun `calculatePosition returns same result as calculateAdjustedOffset`() {

        val originalOffset = IntOffset(50, -10)
        val menuSize = IntSize(40, 40)
        val windowSize = IntSize(200, 200)
        popupPositionProvider = ContextMenuPopupPositionProvider(
            positionPx = originalOffset,
            offsetPx = IntOffset.Zero,
            windowMarginPx = paddingPx,
        )

        val expectedResult = popupPositionProvider.calculateAdjustedOffset(
            menuSize = menuSize,
            screenBounds = IntRect(0, 0, windowSize.width, windowSize.height)
        )

        val actualResult = popupPositionProvider.calculatePosition(
            anchorBounds = IntRect(0, 0, 0, 0),
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = menuSize,
        )

        assertEquals(expectedResult, actualResult)
    }
}
