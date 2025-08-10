package org.thedroiddiv.corntex.menu


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.JPopupTextMenu
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import org.thedroiddiv.corntex.menu.models.ContextMenuColor
import org.thedroiddiv.corntex.menu.models.darkContextMenuColor
import org.thedroiddiv.corntex.menu.models.lightContextMenuColor
import java.awt.Component
import java.awt.MouseInfo
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

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
    override fun Representation(state: ContextMenuState, items: () -> List<ContextMenuItem>) {
        ContextMenuImpl(state, items)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun ContextMenuImpl(state: ContextMenuState, items: () -> List<ContextMenuItem>) {
        val status = state.status
        if (status is ContextMenuState.Status.Open) {
            var focusManager: FocusManager? by mutableStateOf(null)
            var inputModeManager: InputModeManager? by mutableStateOf(null)

            Popup(
                properties = PopupProperties(focusable = true),
                onDismissRequest = { state.status = ContextMenuState.Status.Closed },
                popupPositionProvider = rememberPopupPositionProviderAtPosition(
                    positionPx = status.rect.center
                ),
                onKeyEvent = {
                    if (it.type == KeyEventType.KeyDown) {
                        when (it.key.nativeKeyCode) {
                            java.awt.event.KeyEvent.VK_DOWN -> {
                                inputModeManager!!.requestInputMode(InputMode.Keyboard)
                                focusManager!!.moveFocus(FocusDirection.Next)
                                true
                            }

                            java.awt.event.KeyEvent.VK_UP -> {
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
                        .shadow(8.dp, RoundedCornerShape(9.dp))
                        .clip(RoundedCornerShape(9.dp))
                        .background(colors.containerColor, RoundedCornerShape(9.dp))
                        .padding(6.dp)
                        .width(IntrinsicSize.Max)
                        .verticalScroll(rememberScrollState())
                ) {
                    items().forEach { item ->
                        if (item is ContextSubmenuItem) {
                            MenuItemContent(
                                itemHoverColor = colors.selectedContainerColor,
                                onClick = {
                                    state.status = ContextMenuState.Status.Closed
                                    item.onClick()
                                }
                            // TODO: add hover state
                            ) {
                                BasicText(
                                    text = item.label,
                                    style = TextStyle(color = colors.contentColor)
                                )
                            }
                        } else {
                            MenuItemContent(
                                itemHoverColor = colors.selectedContainerColor,
                                onClick = {
                                    state.status = ContextMenuState.Status.Closed
                                    item.onClick()
                                }
                            ) {
                                BasicText(
                                    text = item.label,
                                    style = TextStyle(color = colors.contentColor)
                                )
                            }
                        }
                    }
                }
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
            .clickable(onClick = onClick)
            .onHover { hovered = it }
            .clip(RoundedCornerShape(4.dp))
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

/**
 * [ContextMenuRepresentation] that uses [JPopupMenu] to show a context menu for [ContextMenuArea].
 *
 * You can use it by overriding [LocalContextMenuRepresentation] on the top level of your application.
 *
 * See also [JPopupTextMenu] that allows more specific customization for the text context menu.
 *
 * @param owner The root component that owns a context menu. Usually it is [ComposeWindow] or [ComposePanel].
 * @param createMenu Describes how to create [JPopupMenu] from list of [ContextMenuItem]
 */
@ExperimentalFoundationApi
class JPopupContextMenuRepresentation(
    private val owner: Component,
    private val createMenu: (List<ContextMenuItem>) -> JPopupMenu = { items ->
        JPopupMenu().apply {
            for (item in items) {
                add(
                    JMenuItem(item.label).apply {
                        addActionListener { item.onClick() }
                    }
                )
            }
        }
    },
) : ContextMenuRepresentation {
    @Composable
    override fun Representation(state: ContextMenuState, items: () -> List<ContextMenuItem>) {
        val isOpen = state.status is ContextMenuState.Status.Open
        if (isOpen) {
            val menu = remember {
                createMenu(items()).apply {
                    addPopupMenuListener(object : PopupMenuListener {
                        override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) = Unit

                        override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent?) {
                            state.status = ContextMenuState.Status.Closed
                        }

                        override fun popupMenuCanceled(e: PopupMenuEvent?) = Unit
                    })
                }
            }

            DisposableEffect(Unit) {
                val mousePosition = MouseInfo.getPointerInfo().location
                SwingUtilities.convertPointFromScreen(mousePosition, owner)
                menu.show(owner, mousePosition.x, mousePosition.y)
                onDispose {
                    menu.isVisible = false
                }
            }
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
