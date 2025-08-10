package org.thedroiddiv.corntex

import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.contextMenuOpenDetector
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import org.thedroiddiv.corntex.components.menu.context.ContextMenuTreeItem
import java.awt.event.KeyEvent

@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Corntex",
    ) {
        ContextMenuArea(
            items = listOf(
                ContextMenuTreeItem.Single(
                    label = "Item 1",
                    onClick = {}
                ),
                ContextMenuTreeItem.Single(
                    label = "Item 2",
                    onClick = {}
                ),
                ContextMenuTreeItem.Submenu(
                    label = "Item 3",
                    subMenuItems = listOf(
                        ContextMenuTreeItem.Single(
                            label = "Item 3.1",
                            onClick = {}
                        ),
                        ContextMenuTreeItem.Single(
                            label = "Item 3.2",
                            onClick = {}
                        ),
                        ContextMenuTreeItem.Submenu(
                            label = "Item 3.2",
                            subMenuItems = listOf(
                                ContextMenuTreeItem.Single(
                                    label = "Item 3.2.1",
                                    onClick = {}
                                ),
                                ContextMenuTreeItem.Single(
                                    label = "Item 3.2.2",
                                    onClick = {}
                                )
                            )
                        ),
                    )
                ),
            )
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Blue)
                    .height(100.dp)
                    .width(100.dp)
            )
        }
    }
}

@Composable
internal fun ContextMenuArea(
    items: List<ContextMenuTreeItem>,
    state: ContextMenuState = remember { ContextMenuState() },
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(Modifier.contextMenuOpenDetector(state, enabled), propagateMinConstraints = true) {
        content()
        LocalContextMenuTreeRepresentation.current.Representation(state, items)
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.contextMenuOpenDetector(
    state: ContextMenuState,
    enabled: Boolean = true
): Modifier = this.contextMenuOpenDetector(
    key = state,
    enabled = enabled && (state.status is ContextMenuState.Status.Closed),
) { pointerPosition ->
    state.status = ContextMenuState.Status.Open(Rect(pointerPosition, 0f))
}

interface ContextMenuTreeRepresentation {
    @Composable
    fun Representation(
        state: ContextMenuState,
        items: List<ContextMenuTreeItem>
    )
}

class DefaultContextMenuTreeRepresentation(
    private val backgroundColor: Color,
    private val textColor: Color,
    private val itemHoverColor: Color
) : ContextMenuTreeRepresentation {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Representation(
        state: ContextMenuState,
        items: List<ContextMenuTreeItem>
    ) {
        ContextMenu(
            state = state,
            items = items
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenu(
    state: ContextMenuState,
    items: List<ContextMenuTreeItem>,
    modifier: Modifier = Modifier
) {
    val status = state.status
    if (status is ContextMenuState.Status.Open) {
        var focusManager: FocusManager? by mutableStateOf(null)
        var inputModeManager: InputModeManager? by mutableStateOf(null)
        var submenuState by remember {
            mutableStateOf<Pair<ContextMenuTreeItem.Submenu, Offset>?>(null)
        }

        Popup(
            properties = PopupProperties(focusable = true),
            onDismissRequest = { state.status = ContextMenuState.Status.Closed },
            popupPositionProvider = rememberPopupPositionProviderAtPosition(
                positionPx = status.rect.center
            ),
            onKeyEvent = {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.key.nativeKeyCode) {
                        KeyEvent.VK_DOWN -> {
                            inputModeManager!!.requestInputMode(InputMode.Keyboard)
                            focusManager!!.moveFocus(FocusDirection.Next)
                            true
                        }

                        KeyEvent.VK_UP -> {
                            inputModeManager!!.requestInputMode(InputMode.Keyboard)
                            focusManager!!.moveFocus(FocusDirection.Previous)
                            true
                        }

                        else -> false
                    }
                } else {
                    false
                }
            },
        ) {
            focusManager = LocalFocusManager.current
            inputModeManager = LocalInputModeManager.current
            Column(
                modifier = Modifier
                    .shadow(8.dp)
                    .padding(vertical = 4.dp)
                    .width(IntrinsicSize.Max)
                    .verticalScroll(rememberScrollState())

            ) {
                items.forEach { item ->
                    when (item) {
                        is ContextMenuTreeItem.Single -> {
                            MenuItemContent(
                                itemHoverColor = Color.Gray,
                                onClick = {
                                    state.status = ContextMenuState.Status.Closed
                                    item.onClick()
                                }
                            ) {
                                BasicText(text = item.label, style = TextStyle(color = Color.Black))
                            }
                        }

                        is ContextMenuTreeItem.Submenu -> {
                            ContextMenuSubmenuItem(
                                item = item,
                                onSubmenuHover = {
                                    submenuState = item to it
                                },
                                onSubmenuLeave = {
                                    submenuState = null
                                },
                            )
                        }
                    }

                }
            }

            submenuState?.let { (submenu, submenuOffset) ->
                val state = remember {
                    val state = ContextMenuState()
                    state.status = ContextMenuState.Status.Open(Rect(submenuOffset, 0F))
                    state
                }
                ContextMenu(
                    items = submenu.subMenuItems,
                    state = state,
                    modifier = modifier,
                )
            }
        }
    }
}


@Composable
private fun MenuItemContent(
    itemHoverColor: Color,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    var hovered by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clickable(
                onClick = onClick,
            )
            .onHover { hovered = it }
            .background(if (hovered) itemHoverColor else Color.Transparent)
            .fillMaxWidth()
            // Preferred min and max width used during the intrinsic measurement.
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

@Composable
private fun ContextMenuSubmenuItem(
    item: ContextMenuTreeItem.Submenu,
    onSubmenuHover: (Offset) -> Unit,
    onSubmenuLeave: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var itemPosition by remember { mutableStateOf(Offset.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isHovered && item.enabled) Color.Gray else Color.Transparent
            )
            .onGloballyPositioned { coordinates ->
                itemPosition = Offset(
                    coordinates.size.width.toFloat(),
                    0f
                )
            }
            .clickable(
                enabled = item.enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (isHovered) {
                    onSubmenuHover(itemPosition)
                } else {
                    onSubmenuLeave()
                }
                isHovered = !isHovered
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        item.leadingIcon?.let { icon ->
            Box(
                modifier = Modifier.size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = icon, contentDescription = item.label)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Text
        BasicText(
            text = item.label,
            style = TextStyle(
                color = if (item.enabled) Color.Black else Color.Black.copy(0.4f),
                fontSize = 14.sp
            ),
            modifier = Modifier.weight(1f)
        )

        // Submenu arrow
        Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "▶",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 10.sp
                )
            )
        }
    }

    // Handle hover effects for submenu
    LaunchedEffect(isHovered) {
        if (isHovered) {
            onSubmenuHover(itemPosition)
        } else {
            onSubmenuLeave()
        }
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


val LightDefaultContextMenuRepresentation = DefaultContextMenuTreeRepresentation(
    backgroundColor = Color.White,
    textColor = Color.Black,
    itemHoverColor = Color.Black.copy(alpha = 0.04f)
)

val LocalContextMenuTreeRepresentation: ProvidableCompositionLocal<ContextMenuTreeRepresentation> =
    staticCompositionLocalOf { LightDefaultContextMenuRepresentation }
