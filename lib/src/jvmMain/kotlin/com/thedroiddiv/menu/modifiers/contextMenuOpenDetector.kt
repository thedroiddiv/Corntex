package com.thedroiddiv.menu.modifiers

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import com.thedroiddiv.menu.ContextMenuEntry
import com.thedroiddiv.menu.HierarchicalContextMenuState

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.contextMenuOpenDetector(
    state: HierarchicalContextMenuState,
    items: () -> List<ContextMenuEntry>
) =
    this.onPointerEvent(PointerEventType.Press) {
        if (it.buttons.isSecondaryPressed) {
            it.changes.forEach { it.consume() }
            val position = it.changes.first().position
            state.show(
                position = IntOffset(position.x.toInt(), position.y.toInt()),
                items = items()
            )
        }
    }
