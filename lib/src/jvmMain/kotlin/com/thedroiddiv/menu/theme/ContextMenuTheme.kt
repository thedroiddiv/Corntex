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

package com.thedroiddiv.menu.theme

import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalColors = staticCompositionLocalOf { darkContextMenuColor }
internal val LocalTokens = staticCompositionLocalOf { defaultContextMenuTokens }
internal val LocalTypography = staticCompositionLocalOf { ContextMenuTypography() }

object ContextMenuTheme {
    val colors: ContextMenuColor
        @Composable get() = LocalColors.current

    val tokens: ContextMenuTokens
        @Composable get() = LocalTokens.current

    val typography: ContextMenuTypography
        @Composable get() = LocalTypography.current
}

@Composable
fun ContextMenuTheme(
    colors: ContextMenuColor = darkContextMenuColor,
    tokens: ContextMenuTokens = defaultContextMenuTokens,
    typography: ContextMenuTypography = defaultContextMenuTypography,
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTokens provides tokens,
        LocalTypography provides typography,
        LocalContentColor provides LocalColors.current.contentColor,
        content = content
    )
}
