package com.thedroiddiv.menu

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.IntOffset
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(InternalComposeUiApi::class)
class HierarchicalContextMenuStateTest {

    private lateinit var state: HierarchicalContextMenuState
    private lateinit var clickSpy: MutableList<String>

    @Before
    fun setup() {
        state = HierarchicalContextMenuState()
        clickSpy = mutableListOf()
    }

    private fun single(label: String, enabled: Boolean = true) =
        ContextMenuEntry.Single(
            label = label,
            enabled = enabled,
            onClick = { clickSpy.add(label) }
        )

    private fun submenu(label: String, vararg items: ContextMenuEntry, enabled: Boolean = true) =
        ContextMenuEntry.Submenu(
            label = label,
            submenuItems = items.toList(),
            enabled = enabled
        )

    @Test
    fun show_sets_openMenus_with_given_items_and_position() {
        val items = listOf(single("A"), single("B"))
        val pos = IntOffset(10, 20)
        state.show(pos, items)
        assertEquals(1, state.openMenus.size)
        assertEquals(items, state.openMenus[0].items)
        assertEquals(pos, state.openMenus[0].position)
    }

    @Test
    fun hide_clears_all_open_menus() {
        state.show(IntOffset(1, 1), listOf(single("X")))
        assertTrue(state.openMenus.isNotEmpty())
        state.hide()
        assertTrue(state.openMenus.isEmpty())
    }

    @Test
    fun onItemHover_opens_submenu_for_enabled_submenu_item() {
        val submenuItems = listOf(single("Child 1"))
        val rootMenu = listOf(submenu("Parent", *submenuItems.toTypedArray()))
        state.show(IntOffset(5, 5), rootMenu)
        state.onItemHover(rootMenu[0], IntOffset(10, 20))
        assertEquals(2, state.openMenus.size)
        assertEquals(submenuItems, state.openMenus[1].items)
    }

    @Test
    fun onItemHover_does_nothing_for_disabled_submenu() {
        val submenuItems = listOf(single("Child"))
        val rootMenu = listOf(submenu("Parent", *submenuItems.toTypedArray(), enabled = false))
        state.show(IntOffset(0, 0), rootMenu)
        state.onItemHover(rootMenu[0], IntOffset(1, 1))
        assertEquals(1, state.openMenus.size)
    }

    @Test
    fun onItemHover_replaces_submenu_when_different_submenu_hovered() {
        val sub1Items = listOf(single("C1"))
        val sub2Items = listOf(single("C2"))
        val rootMenu = listOf(
            submenu("Sub1", *sub1Items.toTypedArray()),
            submenu("Sub2", *sub2Items.toTypedArray())
        )
        state.show(IntOffset(0, 0), rootMenu)
        state.onItemHover(rootMenu[0], IntOffset(1, 1))
        state.onItemHover(rootMenu[1], IntOffset(2, 2))
        assertEquals(sub2Items, state.openMenus[1].items)
    }

    @Test
    fun onItemHover_does_not_duplicate_submenu_if_already_open() {
        val submenuItems = listOf(single("Child"))
        val rootMenu = listOf(submenu("Parent", *submenuItems.toTypedArray()))
        state.show(IntOffset(0, 0), rootMenu)
        state.onItemHover(rootMenu[0], IntOffset(1, 1))
        val firstOpenMenus = state.openMenus
        state.onItemHover(rootMenu[0], IntOffset(1, 1))
        val secondOpenMenus = state.openMenus
        assertSame(firstOpenMenus, secondOpenMenus)
    }

    @Test
    fun onItemHover_closes_deeper_menus_when_hovering_nonSubmenu_item() {
        val childMenu = listOf(single("Child"))
        val rootMenu = listOf(submenu("Parent", *childMenu.toTypedArray()))
        state.show(IntOffset(0, 0), rootMenu)
        state.onItemHover(rootMenu[0], IntOffset(1, 1))
        assertEquals(2, state.openMenus.size)
        val simpleItem = single("Simple")
        val newRoot = listOf(simpleItem)
        state.show(IntOffset(0, 0), newRoot)
        state.onItemHover(simpleItem, IntOffset(2, 2))
        assertEquals(1, state.openMenus.size)
    }

    // When two or more nested menus are open, and the user hovers a menu below in stack, position is calculated correctly
    @Test
    fun `submenu position is calculated correctly when multiple menus are open`() {
        val menu = listOf(
            single("A"),
            submenu(
                "B",
                single("B.1"),
                submenu(
                    "B.2",
                    single("B.2.a")
                )
            ),
            submenu(
                "C",
                single("C.1"),
                single("C.2")

            )
        )

        // Hover on B -> B.2 -> B.2.a
        // Verify top level menu contains only one single item with label B.2.a
        // open menu, top menu at 0,0
        state.show(IntOffset(0, 0), menu)
        // hover on B, top menu at 1, 1
        state.onItemHover(menu[1], IntOffset(1, 1))
        assertEquals(
            (menu[1] as ContextMenuEntry.Submenu).submenuItems,
            state.openMenus.last().items
        )
        assertEquals(IntOffset(1, 1), state.openMenus.last().position)
        // hover on B.2, top menu at (1+2, 1+2)
        state.onItemHover((menu[1] as ContextMenuEntry.Submenu).submenuItems[1], IntOffset(2, 2))
        assertEquals(
            ((menu[1] as ContextMenuEntry.Submenu).submenuItems[1] as ContextMenuEntry.Submenu).submenuItems,
            state.openMenus.last().items
        )
        assertEquals(IntOffset(3, 3), state.openMenus.last().position)

        // Now hover on C, offset should be (0+1, 0+3)
        // Verify top level menu contains only on two single items with label C.1 and C.2, with correct offset
        state.onItemHover(menu[2], IntOffset(1, 3))
        assertEquals(
            (menu[2] as ContextMenuEntry.Submenu).submenuItems,
            state.openMenus.last().items
        )
        assertEquals(IntOffset(1, 3), state.openMenus.last().position)
    }

    @Test
    fun `submenu position is bottomRight when no previous menu`() {
        val submenuItems = listOf(single("Child"))
        val rootMenu = listOf(submenu("Parent", *submenuItems.toTypedArray()))
        state.show(IntOffset.Zero, rootMenu)
        val bottomRight = IntOffset(15, 25)
        state.onItemHover(rootMenu[0], bottomRight)
        assertEquals(bottomRight, state.openMenus[1].position)
    }

    @Test
    fun `submenu position is offset from previous menu position`() {
        /* Case: prev != null */
        val sub2Items = listOf(single("SubChild"))
        val sub1 = submenu("Sub1", submenu("Sub2", *sub2Items.toTypedArray()))
        state.show(IntOffset(10, 10), listOf(sub1))

        /* Open Sub1 first */
        state.onItemHover(sub1, IntOffset(5, 5))
        assertEquals(IntOffset(15, 15), state.openMenus[1].position)

        /* Now hover Sub2 inside Sub1 */
        val sub2 = sub1.submenuItems[0] as ContextMenuEntry.Submenu
        val sub2BottomRight = IntOffset(7, 8)
        state.onItemHover(sub2, sub2BottomRight)

        val expectedPos = IntOffset(15 + sub2BottomRight.x, 15 + sub2BottomRight.y)
        assertEquals(expectedPos, state.openMenus[2].position)
    }

    @Test
    fun `key handler returns false for non-keydown events`() {
        state.show(IntOffset.Zero, listOf(single("A")))
        assertFalse(state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyUp)))
    }

    @Test
    fun `key handler returns false for unhandled keys`() {
        state.show(IntOffset.Zero, listOf(single("A")))
        assertFalse(state.handleKeyEvent(KeyEvent(Key.A, KeyEventType.KeyDown)))
    }

    @Test
    fun `key handler returns false if no menus are open`() {
        assertFalse(state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown)))
    }

    @Test
    fun `down arrow focuses first enabled item when none is focused`() {
        val items = listOf(single("A"), single("B"))
        state.show(IntOffset.Zero, items)

        val handled = state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))

        assertTrue(handled)
        assertEquals(0, state.openMenus.last().focused)
    }

    @Test
    fun `up arrow focuses last enabled item when none is focused`() {
        val items = listOf(single("A"), single("B"), single("C"))
        state.show(IntOffset.Zero, items)

        
        val handled = state.handleKeyEvent(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown))

        assertTrue(handled)
        assertEquals(2, state.openMenus.last().focused)
    }

    @Test
    fun `arrow keys cycle through enabled items only`() {
        val items = listOf(
            single("A"),
            single("B", enabled = false),
            ContextMenuEntry.Divider,
            single("C")
        )
        state.show(IntOffset.Zero, items)

        // From none to first enabled (A at index 0)
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
        assertEquals(0, state.openMenus.last().focused)

        // From A to next enabled (C at index 3), skipping B and Divider
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
        assertEquals(3, state.openMenus.last().focused)

        // From C wraps around to A
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
        assertEquals(0, state.openMenus.last().focused)

        // From A wraps back to C with Up arrow
        state.handleKeyEvent(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown))
        assertEquals(3, state.openMenus.last().focused)
    }

    @Test
    fun `arrow keys do nothing if no items are enabled`() {
        val items = listOf(
            single("A", enabled = false),
            ContextMenuEntry.Divider,
            single("B", enabled = false)
        )
        state.show(IntOffset.Zero, items)

        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))

        assertNull(state.openMenus.last().focused)
    }

    @Test
    fun `left arrow closes submenu if one is open`() {
        val child = single("Child")
        val parent = submenu("Parent", child)
        state.show(IntOffset.Zero, listOf(parent))
        // Parent >
        //          Child
        // Manually open submenu to simulate state
        state.onItemHover(parent, IntOffset(10, 20))
        assertEquals(2, state.openMenus.size)

        val handled = state.handleKeyEvent(KeyEvent(Key.DirectionLeft, KeyEventType.KeyDown))

        assertTrue(handled)
        assertEquals(1, state.openMenus.size)
    }

    @Test
    fun `left arrow does nothing on root menu`() {
        state.show(IntOffset.Zero, listOf(single("A")))
        assertEquals(1, state.openMenus.size)

        val handled = state.handleKeyEvent(KeyEvent(Key.DirectionLeft, KeyEventType.KeyDown))

        assertTrue(handled) // Event is still "handled" to prevent propagation
        assertEquals(1, state.openMenus.size)
    }

    @Test
    fun `right arrow opens focused and enabled submenu`() {
        val child = single("Child")
        val parent = submenu("Parent", child)
        state.show(IntOffset(10, 20), listOf(parent))

        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
        assertEquals(0, state.openMenus.last().focused)

        val itemOffset = IntOffset(100, 50)
        state.reportItemOffset(parent, itemOffset)

        val handled = state.handleKeyEvent(KeyEvent(Key.DirectionRight, KeyEventType.KeyDown))

        assertTrue(handled)
        assertEquals(2, state.openMenus.size)
        assertEquals(listOf(child), state.openMenus.last().items)

        val expectedPosition = IntOffset(10 + itemOffset.x, 20 + itemOffset.y)
        assertEquals(expectedPosition, state.openMenus.last().position)
    }

    @Test
    fun `right arrow does nothing on a single item`() {
        val item = single("A")
        state.show(IntOffset.Zero, listOf(item))
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown)) // Focus item

        val handled = state.handleKeyEvent(KeyEvent(Key.DirectionRight, KeyEventType.KeyDown))
        assertTrue(handled)
        assertEquals(1, state.openMenus.size)
    }

    @Test
    fun `enter key performs click on focused single item and hides menu`() {
        val items = listOf(single("A"), single("B"))
        state.show(IntOffset.Zero, items)
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))

        assertEquals(1, state.openMenus.last().focused)

        val handled = state.handleKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertTrue(handled)
        assertEquals(listOf("B"), clickSpy)
        assertTrue(state.openMenus.isEmpty())
    }

    @Test
    fun `enter key opens focused submenu`() {
        val child = single("Child")
        val parent = submenu("Parent", child)
        state.show(IntOffset.Zero, listOf(parent))
        state.handleKeyEvent(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
        state.reportItemOffset(parent, IntOffset(100, 50))

        val handled = state.handleKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertTrue(handled)
        assertEquals(2, state.openMenus.size)
        assertEquals(listOf(child), state.openMenus.last().items)
    }
}
