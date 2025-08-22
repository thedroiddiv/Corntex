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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thedroiddiv.menu.Res
import com.thedroiddiv.menu.inter_regular
import com.thedroiddiv.menu.jetbrains_mono_regular
import org.jetbrains.compose.resources.Font

@Composable
fun InterFontFamily() = FontFamily(
    Font(Res.font.inter_regular, weight = FontWeight.Light)
    // add more weights
)

@Composable
fun JBMonoFontFamily() = FontFamily(
    Font(Res.font.jetbrains_mono_regular, weight = FontWeight.Light)
    // add more weights
)

@Immutable
data class ContextMenuTypography(
    val label: TextStyle = TextStyle.Default
)

val defaultContextMenuTypography
     @Composable
     get() = ContextMenuTypography(
        label = TextStyle.Default.copy(
            lineHeight = 16.sp,
            fontFamily = InterFontFamily(),
            fontSize = 13.sp
        )
    )

