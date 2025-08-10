package org.thedroiddiv.corntex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.thedroiddiv.corntex.menu.components.ContextMenuItem
import org.thedroiddiv.corntex.menu.components.ContextSubmenuItem
import org.thedroiddiv.corntex.menu.models.ContextMenuColor
import org.thedroiddiv.corntex.menu.models.ContextMenuTreeItem
import org.thedroiddiv.corntex.menu.models.darkContextMenuColor
import org.thedroiddiv.corntex.menu.models.lightContextMenuColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    Surface(Modifier.padding(16.dp)) {
        Column(Modifier.width(200.dp)) {
            ContextMenuItem(
                color = darkContextMenuColor,
                data = ContextMenuTreeItem.Single(
                    label = "Delete",
                    leadingIcon = rememberVectorPainter(Icons.Default.Add),
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
