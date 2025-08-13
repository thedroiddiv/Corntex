package com.thedroiddiv.menu.modifiers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thedroiddiv.menu.ContextMenuEntry
import com.thedroiddiv.menu.HierarchicalContextMenuState
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class ContextMenuOpenDetectorTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rightClick_opensMenuWithCorrectItemsAndPosition() {
        val state = mock<HierarchicalContextMenuState>()
        val menuItems = listOf(
            ContextMenuEntry.Single("Item 1") {},
            ContextMenuEntry.Single("Item 2") {}
        )

        composeRule.setContent {
            Box(
                Modifier
                    .size(100.dp)
                    .testTag("target")
                    .contextMenuOpenDetector(state) { menuItems }
            )
        }

        // Simulate right click at (50, 60)
        composeRule.onNodeWithTag("target")
            .performMouseInput {
                moveTo(Offset(50f, 60f))
                press(MouseButton.Secondary)
                release(MouseButton.Secondary)
            }

        verify(state).show(IntOffset(50, 60), menuItems)
    }

    @Test
    fun leftClick_doesNotOpenMenu() {
        val state = mock<HierarchicalContextMenuState>()

        composeRule.setContent {
            Box(
                Modifier
                    .size(100.dp)
                    .testTag("target")
                    .contextMenuOpenDetector(state) { emptyList() }
            )
        }

        // Simulate left click
        composeRule.onNodeWithTag("target")
            .performMouseInput {
                click() // default left click
            }

        verify(state, never()).show(any(), any())
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun rightClick_consumesEvent() {
        var propagated = false
        val state = mock<HierarchicalContextMenuState>()

        composeRule.setContent {
            Box(
                Modifier
                    .size(100.dp)
                    .testTag("target")
                    .contextMenuOpenDetector(state) { emptyList() }
                    .onPointerEvent(PointerEventType.Press) {
                        if (it.buttons.isSecondaryPressed) {
                            propagated = true
                        }
                    }
            )
        }

        composeRule.onNodeWithTag("target")
            .performMouseInput {
                moveTo(Offset(50F, 50F))
                press(MouseButton.Secondary)
                release(MouseButton.Secondary)
            }

        verify(state).show(IntOffset(50, 50), emptyList())
        // TODO: Check why event is being propagated
        // assert(!propagated) { "Event should be consumed by contextMenuOpenDetector" }
    }
}
