package org.thedroiddiv.corntex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.thedroiddiv.menu.theme.ContextMenuTheme
import com.thedroiddiv.menu.theme.darkContextMenuColor
import com.thedroiddiv.menu.theme.lightContextMenuColor

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Corntex",
    ) {
        Box(Modifier.fillMaxSize().background(Color(0x00000000))){
            ContextMenuTheme(colors = darkContextMenuColor) {
                DesktopApp()
            }
        }
    }
}