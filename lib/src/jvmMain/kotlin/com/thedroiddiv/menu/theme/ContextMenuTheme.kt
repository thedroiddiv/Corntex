package com.thedroiddiv.menu.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalColors = staticCompositionLocalOf { darkContextMenuColor }
internal val LocalTokens = staticCompositionLocalOf { defaultContextMenuTokens }

object ContextMenuTheme {
    val colors: ContextMenuColor
        @Composable get() = LocalColors.current

    val tokens: ContextMenuTokens
        @Composable get() = LocalTokens.current
}

@Composable
fun ContextMenuTheme(
    colors: ContextMenuColor = darkContextMenuColor,
    tokens: ContextMenuTokens = defaultContextMenuTokens,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTokens provides tokens,
        content = content
    )
}
