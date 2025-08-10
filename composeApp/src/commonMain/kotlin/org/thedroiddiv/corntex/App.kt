package org.thedroiddiv.corntex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import corntex.composeapp.generated.resources.Res
import corntex.composeapp.generated.resources.compose_multiplatform
import org.thedroiddiv.corntex.menu.components.ContextMenuItem
import org.thedroiddiv.corntex.menu.components.ContextSubmenuItem
import org.thedroiddiv.corntex.menu.models.ContextMenuTreeItem
import org.thedroiddiv.corntex.menu.models.darkContextMenuColor

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(Modifier.padding(16.dp)) {
                Column(Modifier.width(200.dp)) {
                    ContextMenuItem(
                        color = darkContextMenuColor,
                        data = ContextMenuTreeItem.Single(
                            label = "Delete",
                            leadingIcon = null,
                            trailingIcon = null,
                            enabled = true,
                            onClick = { }
                        )
                    )
                    ContextMenuItem(
                        color = darkContextMenuColor,
                        data = ContextMenuTreeItem.Single(
                            label = "Undo",
                            leadingIcon = null,
                            trailingIcon = null,
                            enabled = true,
                            onClick = { }
                        )
                    )
                    ContextSubmenuItem(
                        color = darkContextMenuColor,
                        data = ContextMenuTreeItem.Submenu(
                            label = "Edit",
                            leadingIcon = null,
                            trailingIcon = null,
                            enabled = true,
                            onHover = { _, _ -> },
                            subMenuItems = listOf()
                        )
                    )
                }
            }
        }
    }
}