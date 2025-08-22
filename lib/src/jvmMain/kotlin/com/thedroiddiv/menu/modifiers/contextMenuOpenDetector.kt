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

package com.thedroiddiv.menu.modifiers

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import com.thedroiddiv.menu.ContextMenuEntry
import com.thedroiddiv.menu.HierarchicalContextMenuState

/**
 * Modifier that detects right-click (secondary button press) events to trigger a context menu.
 *
 * @param state The [HierarchicalContextMenuState] that manages the context menu visibility and positioning
 * @param items A lambda function that provides the list of [ContextMenuEntry] items to display when the menu is opened
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.contextMenuOpenDetector(
    state: HierarchicalContextMenuState,
    items: () -> List<ContextMenuEntry>
) =
    this.onPointerEvent(PointerEventType.Press) {
        if (it.buttons.isSecondaryPressed) {
            it.changes.forEach { e -> e.consume() }
            val position = it.changes.first().position
            state.show(
                position = IntOffset(position.x.toInt(), position.y.toInt()),
                items = items()
            )
        }
    }
