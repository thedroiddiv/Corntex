package com.thedroiddiv.menu.components


import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thedroiddiv.menu.ContextMenuEntry
import com.thedroiddiv.menu.HierarchicalContextMenuState
import com.thedroiddiv.menu.theme.ContextMenuTheme

@Composable
fun MenuLevelContent(
    items: List<ContextMenuEntry>,
    state: HierarchicalContextMenuState,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 280.dp,
    maxHeight: Dp = 600.dp
) {
    Surface(
        modifier = modifier
            .widthIn(max = maxWidth)
            .shadow(
                ContextMenuTheme.tokens.menuElevation,
                ContextMenuTheme.tokens.menuContainerShape
            )
            .border(
                ContextMenuTheme.tokens.menuOutlineWidth,
                ContextMenuTheme.colors.borderColor,
                ContextMenuTheme.tokens.menuContainerShape
            ),
        shape = ContextMenuTheme.tokens.menuContainerShape,
        color = ContextMenuTheme.colors.containerColor,
        contentColor = ContextMenuTheme.colors.contentColor
    ) {
        Box(modifier = Modifier.heightIn(max = maxHeight).padding(ContextMenuTheme.tokens.menuContainerPadding)) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .focusTarget()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(ContextMenuTheme.tokens.menuItemsSpacing)
            ) {
                items.forEach { item ->
                    MenuItem(
                        entry = item,
                        state = state,
                        scrollState = scrollState,
                    )
                }
            }
        }
    }
}


@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    entry: ContextMenuEntry,
    state: HierarchicalContextMenuState,
    scrollState: ScrollState
) {
    val interactionSource = remember { MutableInteractionSource() }
    var positionInParent by remember { mutableStateOf(IntOffset.Zero) }

    val isHovered = interactionSource.collectIsHoveredAsState().value
    LaunchedEffect(isHovered) {
        if (isHovered) {
            state.onItemHover(entry, positionInParent.let { it.copy(y = it.y - scrollState.value) })
        }
    }

    val isFocused = interactionSource.collectIsFocusedAsState().value
    LaunchedEffect(isFocused) {
        if (isFocused) {
            state.onItemHover(entry, positionInParent.let { it.copy(y = it.y - scrollState.value) })
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            positionInParent = coordinates.positionInParent().run {
                IntOffset((x + coordinates.size.width).toInt(), y.toInt())
            }
        }
    ) {
        when (entry) {
            is ContextMenuEntry.Single -> MenuItemContent(
                label = entry.label,
                onClick = {
                    entry.onClick()
                    state.hide()
                },
                icon = entry.icon,
                enabled = entry.enabled,
                isSubmenu = false,
                interactionSource = interactionSource
            )

            is ContextMenuEntry.Submenu -> MenuItemContent(
                label = entry.label,
                onClick = {
                    if (entry.enabled) {
                        state.onItemHover(entry, positionInParent)
                    }
                },
                icon = entry.icon,
                enabled = entry.enabled,
                isSubmenu = true,
                interactionSource = interactionSource
            )

            is ContextMenuEntry.Divider -> Divider(color = ContextMenuTheme.colors.borderColor)
        }
    }
}

@Composable
fun MenuItemContent(
    label: String,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    enabled: Boolean = true,
    isSubmenu: Boolean = false,
) {
    val isFocusedOrHovered = interactionSource.collectIsFocusedAsState().value ||
            interactionSource.collectIsHoveredAsState().value

    val backgroundColor = when {
        !enabled -> Color.Transparent
        isFocusedOrHovered -> ContextMenuTheme.colors.selectedContainerColor
        else -> Color.Transparent
    }

    val contentColor = when {
        !enabled -> ContextMenuTheme.colors.disableContentColor
        else -> ContextMenuTheme.colors.contentColor
    }

    Row(
        modifier = modifier
            .clip(ContextMenuTheme.tokens.menuItemShape)
            .background(backgroundColor)
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .sizeIn(
                minWidth = 112.dp,
                maxWidth = 280.dp,
                minHeight = 32.dp
            )
            .padding(ContextMenuTheme.tokens.menuItemPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(ContextMenuTheme.tokens.menuItemIconSize),
                tint = contentColor
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = contentColor
        )
        if (isSubmenu) {
            Spacer(Modifier.width(12.dp))
            Text(">")
        }
    }
}
