package com.thedroiddiv.menu


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import java.awt.event.KeyEvent

/**
 * Representation of a context menu that is suitable for light themes of the application.
 */
val LightDefaultContextMenuRepresentation = DefaultContextMenuRepresentation(lightContextMenuColor)

/**
 * Representation of a context menu that is suitable for dark themes of the application.
 */
val DarkDefaultContextMenuRepresentation = DefaultContextMenuRepresentation(darkContextMenuColor)

/**
 * Custom representation of a context menu that allows to specify different colors.
 *
 * @param colors Set of colors for a context menu.
 */
class DefaultContextMenuRepresentation(
    private val colors: ContextMenuColor
) : ContextMenuRepresentation {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Representation(state: ContextMenuState, items: List<ContextMenuItem>) {
        ContextMenu(colors, state, items)
    }
}

class MenuLevel(
    val items: List<ContextMenuItem>,
    val position: Offset
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ContextMenu(
    colors: ContextMenuColor,
    rootState: ContextMenuState,
    rootItems: List<ContextMenuItem>
) {
    val status = rootState.status
    if (status !is ContextMenuState.Status.Open) return

    var menuStack by remember {
        mutableStateOf(listOf(MenuLevel(items = rootItems, position = status.rect.center)))
    }

    fun openSubmenu(levelIndex: Int, submenu: ContextSubmenuItem, position: Offset) {
        menuStack = menuStack.take(levelIndex + 1) + MenuLevel(
            items = submenu.submenuItems,
            position = position
        )
    }

    fun closeFromLevel(levelIndex: Int) {
        if(menuStack.size != 1) {
            menuStack = menuStack.take(levelIndex)
        } else {
            rootState.status = ContextMenuState.Status.Closed
        }
    }

    menuStack.forEachIndexed { levelIndex, menuLevel ->
        MenuPopup(
            position = menuLevel.position,
            items = menuLevel.items,
            onItemClick = { item, offset ->
                if (item is ContextSubmenuItem) {
                    openSubmenu(levelIndex, item, offset)
                } else {
                    rootState.status = ContextMenuState.Status.Closed
                    item.onClick()
                }
            },
            colors = colors,
            onSubmenuExit = { closeFromLevel(levelIndex) },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuPopup(
    position: Offset,
    colors: ContextMenuColor,
    items: List<ContextMenuItem>,
    onItemClick: (ContextMenuItem, Offset) -> Unit,
    onSubmenuExit: () -> Unit
) {
    var focusManager: FocusManager? by remember { mutableStateOf(null) }
    var inputModeManager: InputModeManager? by remember { mutableStateOf(null) }

    Popup(
        properties = PopupProperties(focusable = true),
        popupPositionProvider = rememberPopupPositionProviderAtPosition(positionPx = position),
        onDismissRequest = onSubmenuExit,
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown) {
                when (it.key.nativeKeyCode) {
                    KeyEvent.VK_DOWN -> {
                        inputModeManager?.requestInputMode(InputMode.Keyboard)
                        focusManager?.moveFocus(FocusDirection.Next)
                        true
                    }

                    KeyEvent.VK_UP -> {
                        inputModeManager?.requestInputMode(InputMode.Keyboard)
                        focusManager?.moveFocus(FocusDirection.Previous)
                        true
                    }

                    else -> false
                }
            } else false
        }
    ) {
        focusManager = LocalFocusManager.current
        inputModeManager = LocalInputModeManager.current

        Column(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(9.dp))
                .clip(RoundedCornerShape(9.dp))
                .background(colors.containerColor, RoundedCornerShape(9.dp))
                .border(1.dp, colors.borderColor, RoundedCornerShape(9.dp))
                .padding(6.dp)
                .width(IntrinsicSize.Max)
                .verticalScroll(rememberScrollState())
        ) {
            items.forEachIndexed { idx, item ->
                when (item) {
                    is ContextSubmenuItem -> {
                        MenuItemContent(
                            itemHoverColor = colors.selectedContainerColor,
                            onClick = { positionInParent ->
                                val offset = Offset(
                                    position.x + positionInParent.x,
                                    position.y + positionInParent.y
                                )
                                onItemClick(item, offset)
                            },
                            onHover = { hovered, relativePosition ->
                                // TODO: Open submenu
                            }
                        ) {
                            BasicText(item.label, style = TextStyle(color = colors.contentColor))
                        }
                    }

                    is Divider -> Divider(
                        color = colors.dividerColor,
                        modifier = Modifier.height(1.dp)
                    ) // TODO: Do not use from androidx.compose.material

                    else -> {
                        MenuItemContent(
                            itemHoverColor = colors.selectedContainerColor,
                            onClick = { positionInParent ->
                                val offset = Offset(
                                    position.x + positionInParent.x,
                                    position.y + positionInParent.y
                                )
                                onItemClick(item, offset)
                            },
                            onHover = { _, _ -> }
                        ) {
                            BasicText(item.label, style = TextStyle(color = colors.contentColor))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItemContent(
    modifier: Modifier = Modifier,
    itemHoverColor: Color,
    onClick: (Offset) -> Unit,
    onHover: (Boolean, Offset) -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    var hovered by remember { mutableStateOf(false) }
    var positionInParent by remember { mutableStateOf(Offset.Zero) }
    Row(
        modifier = modifier
            .clickable(onClick = { onClick(positionInParent) })
            .onGloballyPositioned { coordinates ->
                positionInParent = coordinates.positionInParent().run {
                    Offset(x + coordinates.size.width, y)
                }
            }
            .onHover {
                if (hovered != it) {
                    hovered = it
                    onHover(it, positionInParent)
                }
            }
            .clip(RoundedCornerShape(4.dp))
            .background(if (hovered) itemHoverColor else Color.Transparent)
            .fillMaxWidth()
            .sizeIn(
                minWidth = 112.dp,
                maxWidth = 280.dp,
                minHeight = 32.dp
            )
            .padding(
                PaddingValues(
                    horizontal = 16.dp,
                    vertical = 0.dp
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

private fun Modifier.onHover(onHover: (Boolean) -> Unit) = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            when (event.type) {
                PointerEventType.Enter -> onHover(true)
                PointerEventType.Exit -> onHover(false)
            }
        }
    }
}
