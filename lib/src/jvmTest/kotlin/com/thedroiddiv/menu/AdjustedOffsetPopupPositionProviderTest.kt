package com.thedroiddiv.menu

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Test

class AdjustedOffsetPopupPositionProviderTest {

    private lateinit var popupPositionProvider: AdjustedOffsetPopupPositionProvider
    private val paddingPx = 8 // 8dp at density=1f

    private fun adjust(
        originalOffset: IntOffset,
        menuSize: IntSize,
        menuIndex: Int
    ): IntOffset {
        val windowSize = IntSize(200, 200)
        popupPositionProvider = AdjustedOffsetPopupPositionProvider(
            positionPx = originalOffset,
            menuIndex = menuIndex,
            density = Density(1f)
        )

        return popupPositionProvider.calculateAdjustedOffset(
            menuSize = menuSize,
            screenBounds = IntRect(0, 0, windowSize.width, windowSize.height)
        )
    }

    @Test
    fun `menu fits fully - no adjustment`() {
        val result = adjust(IntOffset(50, 50), IntSize(50, 50), 0)
        assertEquals(IntOffset(50, 50), result)
    }

    @Test
    fun `root menu off right edge - shift left to fit`() {
        val result = adjust(IntOffset(180, 50), IntSize(50, 50), 0)
        assertEquals(IntOffset(200 - 50 - paddingPx, 50), result)
    }

    @Test
    fun `submenu off right edge - place to left of parent`() {
        val result = adjust(IntOffset(180, 50), IntSize(50, 50), 1)
        assertEquals(IntOffset(180 - 50 - paddingPx, 50), result)
    }

    @Test
    fun `menu off left edge - shift right to padding`() {
        val result = adjust(IntOffset(-20, 50), IntSize(40, 40), 0)
        assertEquals(IntOffset(paddingPx, 50), result)
    }

    @Test
    fun `menu off bottom edge - shift up`() {
        val result = adjust(IntOffset(50, 190), IntSize(40, 40), 0)
        assertEquals(IntOffset(50, 200 - 40 - paddingPx), result)
    }

    @Test
    fun `menu off top edge - shift down`() {
        val result = adjust(IntOffset(50, -10), IntSize(40, 40), 0)
        assertEquals(IntOffset(50, paddingPx), result)
    }

//    // FIXME: when adjustments still exceed bounds, make sure it doesn't exceed the visible area bounds
//    @Test
//    fun `adjustment clamps inside screen bounds`() {
//        val result = adjust(IntOffset(500, 500), IntSize(300, 300), 0)
//        assertEquals(IntOffset(paddingPx, paddingPx), result)
//    }
}
