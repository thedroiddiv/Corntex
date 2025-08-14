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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.thedroiddiv.menu.ContextMenuEntry
import com.thedroiddiv.menu.HierarchicalContextMenuState
import com.thedroiddiv.menu.Res
import com.thedroiddiv.menu.arrow_forward
import com.thedroiddiv.menu.components.material.Divider
import com.thedroiddiv.menu.components.material.Icon
import com.thedroiddiv.menu.theme.ContextMenuTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun MenuLevelContent(
    items: List<ContextMenuEntry>,
    focusedIdx: Int?,
    state: HierarchicalContextMenuState,
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    maxHeight: Dp
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .widthIn(max = maxWidth)
            .heightIn(max = maxHeight)
            .shadow(
                ContextMenuTheme.tokens.menuElevation,
                ContextMenuTheme.tokens.menuContainerShape
            )
            .border(
                ContextMenuTheme.tokens.menuOutlineWidth,
                ContextMenuTheme.colors.borderColor,
                ContextMenuTheme.tokens.menuContainerShape
            )
            .clip(ContextMenuTheme.tokens.menuContainerShape)
            .background(
                ContextMenuTheme.colors.containerColor,
                ContextMenuTheme.tokens.menuContainerShape
            )
            .focusTarget()
            .padding(ContextMenuTheme.tokens.menuContainerPadding)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(ContextMenuTheme.tokens.menuItemsSpacing)
    ) {
        items.forEachIndexed { idx, item ->
            MenuItem(
                entry = item,
                state = state,
                scrollState = scrollState,
                focused = idx == focusedIdx
            )
        }
    }
}


@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    focused: Boolean,
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
                leadingIcon = entry.leadingIcon?.let { painterResource(it) },
                trailingIcon = entry.trailingIcon?.let { painterResource(it) },
                enabled = entry.enabled,
                interactionSource = interactionSource,
                focused = focused
            )

            is ContextMenuEntry.Submenu -> MenuItemContent(
                label = entry.label,
                onClick = {
                    if (entry.enabled) {
                        state.onItemHover(entry, positionInParent)
                    }
                },
                leadingIcon = entry.icon?.let { painterResource(it) },
                trailingIcon = painterResource(Res.drawable.arrow_forward),
                enabled = entry.enabled,
                interactionSource = interactionSource,
                focused = focused
            )

            is ContextMenuEntry.Divider -> Divider(color = ContextMenuTheme.colors.borderColor)
        }
    }
}

@Composable
fun MenuItemContent(
    label: String,
    focused: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
) {
    val backgroundColor = when {
        !enabled -> Color.Transparent
        focused -> ContextMenuTheme.colors.selectedContainerColor
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
        Box(Modifier.size(ContextMenuTheme.tokens.menuItemIconSize)) {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .testTag("leadingIcon")
                        .size(ContextMenuTheme.tokens.menuItemIconSize),
                    tint = contentColor
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        BasicText(
            text = label,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            color = { contentColor },
            style = ContextMenuTheme.typography.label
        )
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(ContextMenuTheme.tokens.menuItemIconSize)) {
            if (trailingIcon != null) {
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .testTag("trailingIcon")
                        .size(ContextMenuTheme.tokens.menuItemIconSize),
                    tint = contentColor
                )
            }
        }
    }
}
