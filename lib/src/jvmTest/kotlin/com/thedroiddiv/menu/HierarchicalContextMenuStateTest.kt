package com.thedroiddiv.menu

import androidx.compose.ui.unit.IntOffset
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

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
}
