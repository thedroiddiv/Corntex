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
import org.thedroiddiv.corntex.menu.ContextSubmenuItem

@Composable
fun DesktopApp() {
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("New file") {
                // Custom action
            },
            ContextMenuItem("New Folder") {
                // Another custom action
            },
            // TODO: Add divider
            ContextMenuItem("Open In Split") {
                // Another custom action
            },
            ContextSubmenuItem(
                label = "Open In",
                submenuItems = listOf(
                    ContextMenuItem("Finder") {
                        // Custom action
                    },
                    ContextMenuItem("Terminal") {
                        // Another custom action
                    },
                    ContextMenuItem("Associated Applications") {
                        // Another custom action
                    },
                    ContextMenuItem("Browser") {
                        // Another custom action
                    },
                )
            ),
            // TODO: Divide
            ContextMenuItem("Rename...") {
                // Another custom action
            },
            ContextMenuItem("Delete") {
                // Another custom action
            },
            // TODO: Divider
            ContextMenuItem("Cut") {
                // Another custom action
            },
            ContextMenuItem("Copy") {
                // Another custom action
            },
            ContextMenuItem("Copy Path") {
                // Another custom action
            },
            ContextMenuItem("Copy Relative Path") {
                // Another custom action
            },
            ContextMenuItem("Paste") {
                // Another custom action
            },
            // TODO: Divider
            ContextSubmenuItem(
                label = "Git",
                submenuItems = listOf(
                    ContextMenuItem("Rebase") {
                        // Custom action
                    },
                    ContextMenuItem("Merge") {
                        // Another custom action
                    }
                )
            )
        )
    }) {
        // Blue box where context menu will be available
        Box(modifier = Modifier.background(Color.Blue).height(100.dp).width(100.dp))
    }
}