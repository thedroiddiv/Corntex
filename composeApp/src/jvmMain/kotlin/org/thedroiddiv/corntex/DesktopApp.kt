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
import org.thedroiddiv.corntex.menu.Divider

@Composable
fun DesktopApp() {
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("New file") { },
            ContextMenuItem("New Folder") { },
            Divider,
            ContextMenuItem("Open In Split") { },
            ContextSubmenuItem(
                label = "Open In",
                submenuItems = listOf(
                    ContextMenuItem("Finder") { },
                    ContextMenuItem("Terminal") { },
                    ContextMenuItem("Associated Applications") { },
                    ContextMenuItem("Browser") { },
                )
            ),
            Divider,
            ContextMenuItem("Rename...") { },
            ContextMenuItem("Delete") { },
            Divider,
            ContextMenuItem("Cut") { },
            ContextMenuItem("Copy") { },
            ContextMenuItem("Copy Path") { },
            ContextMenuItem("Copy Relative Path") { },
            ContextMenuItem("Paste") { },
            Divider,
            ContextSubmenuItem(
                label = "Git",
                submenuItems = listOf(
                    ContextMenuItem("Rebase") { },
                    ContextMenuItem("Merge") { },
                    ContextSubmenuItem(
                        label = "GitHub",
                        submenuItems = listOf(
                            ContextMenuItem("Pull") { },
                            ContextMenuItem("Push") { }
                        )
                    )
                )
            )
        )
    }) {
        Box(modifier = Modifier.background(Color.Blue).height(100.dp).width(100.dp))
    }
}