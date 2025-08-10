package org.thedroiddiv.corntex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.thedroiddiv.corntex.menu.ContextMenuArea
import org.thedroiddiv.corntex.menu.ContextMenuItem

@Composable
fun DesktopApp() {
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("User-defined action") {
                // Custom action
            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            }
        )
    }) {
        // Blue box where context menu will be available
        Box(modifier = Modifier.background(Color.Blue).height(100.dp).width(100.dp))
    }
}