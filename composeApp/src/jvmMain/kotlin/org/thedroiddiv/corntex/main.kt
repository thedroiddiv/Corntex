package org.thedroiddiv.corntex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Corntex",
    ) {
        Box(Modifier.fillMaxSize().background(Color(0x00000000))){
            DesktopApp()
        }
    }
}