package org.thedroiddiv.corntex

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thedroiddiv.corntex.Res
import com.thedroiddiv.corntex.arrow_forward
import com.thedroiddiv.corntex.copy
import com.thedroiddiv.corntex.cut
import com.thedroiddiv.corntex.delete
import com.thedroiddiv.corntex.diff
import com.thedroiddiv.corntex.folder
import com.thedroiddiv.corntex.merge
import com.thedroiddiv.corntex.paste
import com.thedroiddiv.corntex.rebase
import com.thedroiddiv.menu.ContextMenuArea
import com.thedroiddiv.menu.ContextMenuEntry
import com.thedroiddiv.menu.ContextMenuEntry.Divider
import com.thedroiddiv.menu.rememberContextMenuState
import com.thedroiddiv.menu.ContextMenuEntry.Single as ContextMenuItem
import com.thedroiddiv.menu.ContextMenuEntry.Submenu as ContextSubmenuItem

@Composable
fun DesktopApp() {
    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("New file", leadingIcon = Res.drawable.folder) { },
                ContextMenuItem("New Folder", leadingIcon = Res.drawable.folder) { },
                Divider,
                ContextMenuItem("Open In Split") { },
                ContextSubmenuItem(
                    label = "Open In",
                    submenuItems = listOf(
                        ContextMenuItem("Finder") { },
                        ContextMenuItem("Terminal") { },
                        ContextMenuItem("Associated Applications") { },
                        ContextMenuItem("Browser") { },
                        ContextSubmenuItem(
                            label = "Git",
                            icon = Res.drawable.folder,
                            submenuItems = listOf(
                                ContextMenuItem("Rebase" ,leadingIcon = Res.drawable.rebase) { },
                                ContextMenuItem("Merge", leadingIcon = Res.drawable.merge) { },
                                ContextSubmenuItem(
                                    label = "GitHub",
                                    icon = Res.drawable.folder,
                                    submenuItems = listOf(
                                        ContextMenuItem("Pull") { },
                                        ContextMenuItem("Push") { }
                                    )
                                ),
                                ContextSubmenuItem(
                                    label = "Git",
                                    submenuItems = listOf(
                                        ContextMenuItem("Rebase",leadingIcon = Res.drawable.rebase) { },
                                        ContextMenuItem("Merge",leadingIcon = Res.drawable.merge) { },
                                        ContextSubmenuItem(
                                            label = "GitHub",
                                            icon = Res.drawable.folder,
                                            submenuItems = listOf(
                                                ContextMenuItem("Pull") { },
                                                ContextMenuItem("Push") { }
                                            )
                                        ),
                                        ContextSubmenuItem(
                                            label = "Git",
                                            enabled = false,
                                            icon = Res.drawable.folder,
                                            submenuItems = listOf(
                                                ContextMenuItem("Rebase", leadingIcon = Res.drawable.rebase) { },
                                                ContextMenuItem("Merge", leadingIcon = Res.drawable.merge) { },
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
                                )
                            )
                        )
                    )
                ),
                Divider,
                ContextMenuItem("Rename...") { },
                ContextMenuItem("Delete", enabled = false, leadingIcon = Res.drawable.delete) { },
                Divider,
                ContextMenuItem("Cut", leadingIcon = Res.drawable.cut) { },
                ContextMenuItem("Copy", leadingIcon = Res.drawable.copy) { },
                ContextMenuItem("Copy Path", leadingIcon = Res.drawable.copy) { },
                ContextMenuItem("Copy Relative Path") { },
                ContextMenuItem("Paste", leadingIcon = Res.drawable.paste) { },
                Divider,
                ContextSubmenuItem(
                    label = "Git",
                    submenuItems = listOf(
                        ContextMenuItem("Rebase", leadingIcon = Res.drawable.rebase) { },
                        ContextMenuItem("Merge", leadingIcon = Res.drawable.merge) { },
                        ContextMenuItem("Diff", leadingIcon = Res.drawable.diff) { },
                        ContextSubmenuItem(
                            label = "GitHub",
                            icon = Res.drawable.folder,
                            submenuItems = listOf(
                                ContextMenuItem("Pull") { },
                                ContextMenuItem("Push") { }
                            )
                        )
                    )
                )
            )
        }
    ) {
        Box(modifier = Modifier.background(Color.Blue).height(100.dp).width(100.dp))
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ManualStateExample() {
    val contextMenuState = rememberContextMenuState()
    var selectedItem by remember { mutableStateOf<String?>(null) }
    ContextMenuArea(state = contextMenuState) {
        ListItem(
            modifier = Modifier.onClick(
                enabled = true,
                interactionSource = MutableInteractionSource()
            ) {
                selectedItem = "Hello World"
                contextMenuState.show(
                    position = IntOffset(0, 0),
                    items = listOf(
                        ContextMenuEntry.Single(
                            "Option 1",
                            onClick = { println("$selectedItem Option 1") }
                        ),
                        ContextMenuEntry.Single(
                            "Option 2",
                            onClick = { println("$selectedItem Option 2") }
                        )
                    )
                )
            }
        ) {
            Text("Hello World")
        }
    }
}