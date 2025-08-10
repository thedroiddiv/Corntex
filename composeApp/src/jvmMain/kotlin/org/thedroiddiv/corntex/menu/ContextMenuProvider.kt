package org.thedroiddiv.corntex.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastAll

/**
 * Defines a container where context menu is available. Menu is triggered by right mouse clicks.
 * Representation of menu is defined by [LocalContextMenuRepresentation].
 *
 * @param items List of context menu items. Final context menu contains all items from descendant
 * [ContextMenuArea] and [ContextMenuDataProvider].
 * @param state [ContextMenuState] of menu controlled by this area.
 * @param enabled If false then gesture detector is disabled.
 * @param content The content of the [ContextMenuArea].
 */
@Composable
fun ContextMenuArea(
    items: () -> List<ContextMenuItem>,
    state: ContextMenuState = remember { ContextMenuState() },
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    ContextMenuArea(
        items = items,
        state = state,
        modifier = Modifier.contextMenuOpenDetector(state, enabled),
        content = content
    )
}

/**
 * Defines a container where context menu is available. Representation of menu is defined by
 * [LocalContextMenuRepresentation].
 *
 * This overload does not trigger the opening of the context menu; it's up to the caller to do so
 * by passing a [modifier] that does so.
 *
 * @param items List of context menu items. Final context menu contains all items from descendant
 * [ContextMenuArea] and [ContextMenuDataProvider].
 * @param state [ContextMenuState] of menu controlled by this area.
 * @param modifier The modifier to attach to the element; this should include the trigger that opens
 * the context menu (e.g. on right-click).
 * @param content The content of the [ContextMenuArea].
 */
@Composable
internal fun ContextMenuArea(
    items: () -> List<ContextMenuItem>,
    state: ContextMenuState,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val data = ContextMenuData(items, LocalContextMenuData.current)

    Box(modifier, propagateMinConstraints = true) {
        content()
        LocalContextMenuRepresentation.current.Representation(state) { data.allItems }
    }
}

/**
 * Adds items to the hierarchy of context menu items. Can be used, for example, to customize
 * context menu of text fields.
 *
 * @param items List of context menu items. Final context menu contains all items from descendant
 * [ContextMenuArea] and [ContextMenuDataProvider].
 * @param content The content of the [ContextMenuDataProvider].
 *
 * @see [[ContextMenuArea]]
 */
@Composable
fun ContextMenuDataProvider(
    items: () -> List<ContextMenuItem>,
    content: @Composable () -> Unit
) {
    ContextMenuDataProvider(
        ContextMenuData(items, LocalContextMenuData.current),
        content
    )
}

@Composable
internal fun ContextMenuDataProvider(
    data: ContextMenuData,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalContextMenuData provides data
    ) {
        content()
    }
}

/**
 * The composition local for specifying the local [ContextMenuData].
 */
val LocalContextMenuData = staticCompositionLocalOf<ContextMenuData?> {
    null
}


/**
 * Detects events that open a context menu (mouse right-clicks).
 *
 * @param key The pointer input handling coroutine will be cancelled and **re-started** when
 * [contextMenuOpenDetector] is recomposed with a different [key].
 * @param enabled Whether to enable the detection.
 * @param onOpen Invoked when a context menu opening event is detected, with the local offset it
 * should be opened at.
 */
@ExperimentalFoundationApi
fun Modifier.contextMenuOpenDetector(
    key: Any? = Unit,
    enabled: Boolean = true,
    onOpen: (Offset) -> Unit
): Modifier {
    return if (enabled) {
        this.pointerInput(key) {
            awaitEachGesture {
                val event = awaitEventFirstDown()
                if (event.buttons.isSecondaryPressed) {
                    event.changes.forEach { it.consume() }
                    onOpen(event.changes[0].position)
                }
            }
        }
    } else {
        this
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.contextMenuOpenDetector(
    state: ContextMenuState,
    enabled: Boolean = true
): Modifier = this.contextMenuOpenDetector(
    key = state,
    enabled = enabled && (state.status is ContextMenuState.Status.Closed),
) { pointerPosition ->
    state.status = ContextMenuState.Status.Open(Rect(pointerPosition, 0f))
}

private suspend fun AwaitPointerEventScope.awaitEventFirstDown(): PointerEvent {
    var event: PointerEvent
    do {
        event = awaitPointerEvent()
    } while (
        !event.changes.fastAll { it.changedToDown() }
    )
    return event
}

/**
 * Individual element of context menu.
 *
 * @param label The text to be displayed as a context menu item.
 * @param onClick The action to be executed after click on the item.
 */
open class ContextMenuItem(
    val label: String,
    val onClick: () -> Unit
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ContextMenuItem

        if (label != other.label) return false
        if (onClick != other.onClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + onClick.hashCode()
        return result
    }

    override fun toString(): String {
        return "ContextMenuItem(label='$label')"
    }
}

class ContextSubmenuItem(
    label: String,
    val submenuItems: List<ContextMenuItem>
) : ContextMenuItem(label, {})

data object Divider : ContextMenuItem("", {})

/**
 * Data container contains all [ContextMenuItem]s were defined previously in the hierarchy.
 * [ContextMenuRepresentation] uses it to display context menu.
 */
class ContextMenuData(
    val items: () -> List<ContextMenuItem>,
    val next: ContextMenuData?
) {

    internal val allItems: List<ContextMenuItem>
        get() = buildList {
            var current: ContextMenuData? = this@ContextMenuData
            while (current != null) {
                addAll(current.items())
                current = current.next
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ContextMenuData

        if (items != other.items) return false
        if (next != other.next) return false

        return true
    }

    override fun hashCode(): Int {
        var result = items.hashCode()
        result = 31 * result + (next?.hashCode() ?: 0)
        return result
    }
}

/**
 * Represents a state of context menu in [ContextMenuArea]. [status] is implemented
 * via [androidx.compose.runtime.MutableState] so it's possible to track it inside @Composable
 * functions.
 */
class ContextMenuState {
    sealed class Status {
        class Open(
            val rect: Rect
        ) : Status() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Open

                if (rect != other.rect) return false

                return true
            }

            override fun hashCode(): Int {
                return rect.hashCode()
            }

            override fun toString(): String {
                return "Open(rect=$rect)"
            }
        }

        data object Closed : Status()
    }

    var status: Status by mutableStateOf(Status.Closed)
}

/**
 * Implementations of this interface are responsible for displaying context menus. There are two
 * implementations out of the box: [LightDefaultContextMenuRepresentation] and
 * [DarkDefaultContextMenuRepresentation].
 * To change currently used representation, different value for [LocalContextMenuRepresentation]
 * could be provided.
 */
interface ContextMenuRepresentation {
    @Composable
    @Deprecated(
        "Use another overload that loads items only when they are needed",
        replaceWith = ReplaceWith("Representation(state, { items }")
    )
    fun Representation(state: ContextMenuState, items: List<ContextMenuItem>) =
        Representation(state) { items }

    @Composable
    fun Representation(state: ContextMenuState, items: () -> List<ContextMenuItem>)
}

/**
 * Composition local that keeps [ContextMenuRepresentation] which is used by [ContextMenuArea]s.
 */
val LocalContextMenuRepresentation:
        ProvidableCompositionLocal<ContextMenuRepresentation> = staticCompositionLocalOf {
    DarkDefaultContextMenuRepresentation
}