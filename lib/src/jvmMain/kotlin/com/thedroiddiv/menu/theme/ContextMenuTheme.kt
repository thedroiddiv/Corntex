package com.thedroiddiv.menu.theme

import androidx.compose.material.MaterialTheme
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
        content = content
    )
}
