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

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.thedroiddiv.menu.theme.LocalTokens
import com.thedroiddiv.menu.theme.defaultContextMenuTokens
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PopupContextMenuRepresentationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultTokens = defaultContextMenuTokens.copy(
        menuMaxHeight = 500.dp,
        menuMaxWidth = 400.dp
    )

    @Test
    fun rootMenu_respectsRemainingSpaceAndMaxTokens() {
        val result = mutableListOf<DpSize>()

        composeTestRule.setContent {
            val fakeWindowInfo = object : WindowInfo {
                override val containerSize = IntSize(800, 600)
                override val isWindowFocused = true
            }

            CompositionLocalProvider(
                LocalWindowInfo provides fakeWindowInfo,
                LocalDensity provides Density(1f),
                LocalTokens provides defaultTokens
            ) {
                val menuSize = PopupContextMenuRepresentation()
                    .rememberMenuSizeConstraints(
                        menuIndex = 0,
                        position = IntOffset(100, 200)
                        // 600 - 200 = 400dp height, 800 - 100 = 700dp width
                    )
                result.add(menuSize)
            }
        }

        composeTestRule.runOnIdle {
            // Height should be min(500dp token max, 400dp available) = 400dp
            // Width should be min(400dp token max, 700dp available) = 400dp
            assertEquals(DpSize(400.dp, 400.dp), result.first())
        }
    }

    @Test
    fun subMenu_usesFullContainerSizeWithMaxTokens() {
        val result = mutableListOf<DpSize>()

        composeTestRule.setContent {
            val fakeWindowInfo = object : WindowInfo {
                override val containerSize = IntSize(800, 600)
                override val isWindowFocused = true
            }

            CompositionLocalProvider(
                LocalWindowInfo provides fakeWindowInfo,
                LocalDensity provides Density(1f),
                LocalTokens provides defaultTokens
            ) {
                val menuSize = PopupContextMenuRepresentation()
                    .rememberMenuSizeConstraints(
                        menuIndex = 1, // Submenu
                        position = IntOffset(300, 400)
                    )
                result.add(menuSize)
            }
        }

        composeTestRule.runOnIdle {
            // Height: min(500dp token max, full height 600dp) = 500dp
            // Width: min(400dp token max, full width 800dp) = 400dp
            assertEquals(DpSize(400.dp, 500.dp), result.first())
        }
    }
}
