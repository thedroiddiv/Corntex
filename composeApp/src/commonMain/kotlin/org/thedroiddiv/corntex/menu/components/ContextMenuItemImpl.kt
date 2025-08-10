package org.thedroiddiv.corntex.menu.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.thedroiddiv.corntex.menu.models.ContextMenuColor
import org.thedroiddiv.corntex.menu.models.Hover

@Composable
internal fun ContextMenuItemImpl(
    modifier: Modifier,
    enabled: Boolean,
    label: String,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    onHover: ((Hover, Offset) -> Unit)? = null,
    color: ContextMenuColor
) {
    var isHovered by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> color.disabledContainerColor
            isHovered -> color.selectedContainerColor
            else -> color.containerColor
        },
        animationSpec = tween(150),
        label = "backgroundColor"
    )

    val contentColor = when {
        !enabled -> color.disableContentColor
        else -> color.contentColor
    }

    // fixme: add onHover listener
    Row(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Box(Modifier.size(16.dp)) {
            leadingIcon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = label,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Box(Modifier.size(16.dp)) {
            trailingIcon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
