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
class ContextMenuTypography(
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

