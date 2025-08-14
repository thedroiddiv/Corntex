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
