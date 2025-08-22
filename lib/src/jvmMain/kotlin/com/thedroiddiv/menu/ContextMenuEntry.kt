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

import org.jetbrains.compose.resources.DrawableResource


/**
 * Base sealed class for any entry that can be displayed in a context menu.
 */
sealed interface ContextMenuEntry {
    val enabled: Boolean

    /**
     * A clickable menu item with optional icon.
     *
     * @param label Display text for the menu item
     * @param leadingIcon Optional icon to display pre to label
     * @param trailingIcon Optional icon to display post to label
     * @param enabled Whether the item is interactive
     * @param onClick Action to perform when clicked
     */
    data class Single(
        val label: String,
        val leadingIcon: DrawableResource? = null,
        val trailingIcon: DrawableResource? = null,
        override val enabled: Boolean = true,
        val onClick: () -> Unit
    ) : ContextMenuEntry

    /**
     * A menu item that expands to show nested menu items.
     *
     * @param label Display text for the submenu
     * @param submenuItems List of entries to display in the submenu
     * @param icon Optional icon to display alongside the label
     * @param enabled Whether the submenu can be opened
     */
    data class Submenu(
        val label: String,
        val submenuItems: List<ContextMenuEntry>,
        val icon: DrawableResource? = null,
        override val enabled: Boolean = true
    ) : ContextMenuEntry

    /**
     * A visual separator line between menu items.
     */
    object Divider : ContextMenuEntry {
        // By default Ignore any interactions on divider
        override val enabled: Boolean = false
    }
}
