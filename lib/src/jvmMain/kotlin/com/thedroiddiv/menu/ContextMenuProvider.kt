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


import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.thedroiddiv.menu.modifiers.contextMenuOpenDetector

/**
 * Defines a container where context menu is available with automatic right-click detection.
 *
 * @param modifier Modifier to be applied to the container
 * @param items Provider function returning list of context menu items
 * @param content The content to display inside the context menu area
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenuArea(
    modifier: Modifier = Modifier,
    items: () -> List<ContextMenuEntry>,
    content: @Composable () -> Unit
) {
    val state = rememberContextMenuState()
    Box(modifier = modifier.contextMenuOpenDetector(state, items)) {
        content()
        LocalContextMenuRepresentation.current.Representation(state)
    }
}

/**
 * Defines a container where context menu is available. Representation of menu is defined by
 * [LocalContextMenuRepresentation].
 *
 * This doesn't show context automatically, caller needs to call `state.show()` to display context menu
 * on some user action.
 *
 * @param modifier The modifier to attach to the element; this should include the trigger that opens
 * @param state [HierarchicalContextMenuState] of menu controlled by this area.
 * the context menu (e.g. on right-click).
 * @param content The content of the [ContextMenuArea].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenuArea(
    modifier: Modifier = Modifier,
    state: HierarchicalContextMenuState,
    content: @Composable () -> Unit
) {
    content()
    LocalContextMenuRepresentation.current.Representation(state)
}

/**
 * Creates and remembers a [HierarchicalContextMenuState] for the current composition.
 */
@Composable
fun rememberContextMenuState() = remember { HierarchicalContextMenuState() }


/**
 * Composition local that keeps [ContextMenuRepresentation] which is used by [ContextMenuArea]s.
 */
val LocalContextMenuRepresentation: ProvidableCompositionLocal<ContextMenuRepresentation> =
    staticCompositionLocalOf { PopupContextMenuRepresentation() }
