# Compose Desktop Context Menu
  
A flexible and hierarchical context menu library for Compose Desktop applications that supports nested submenus, keyboard navigation, and customizable styling.  


https://github.com/user-attachments/assets/c6fa77aa-65b2-4ae3-9e4d-a849c937c82f


  
## Features  
  
- **Right-click Detection**: Automatic context menu triggering on right-click  
- **Hierarchical Menus**: Support for nested submenus with unlimited depth  
- **Customizable Styling**: Theme-aware design with customizable colors and dimensions  
- **Flexible Items**: Support for clickable items, submenus, and visual dividers  
  
## Quick Start  

### 📦 Installation

```kotlin
dependencies {
    implementation("io.github.thedroiddiv:corntex:<latest-version>")
}
```
### Basic Usage  
  
```kotlin  
@Composable  
fun MyComponent() {  
    ContextMenuArea(  
        items = {  
			      listOf(  
                ContextMenuEntry.Single(label = "Cut", onClick = { /* Handle cut */ }),  
                ContextMenuEntry.Single(label = "Copy", onClick = { /* Handle copy */ }),  
                ContextMenuEntry.Single(label = "Paste", onClick = { /* Handle paste */ })  
            )  
        }  
  ) {  
		  Text("Right-click me for context menu!")  
    }  
}
```  
  
### Advanced Usage with Submenus  
  
```kotlin  
@Composable  
@Composable  
fun AdvancedContextMenu() {
    ContextMenuArea(items = {
        listOf(
            ContextMenuEntry.Single(
                label = "New File",
                icon = painterResource("icons/file.png"),
                onClick = { /* Create new file */ }
            ),
            ContextMenuEntry.Submenu(
                label = "Export As",
                icon = painterResource("icons/export.png"),
                submenuItems = listOf(
                    ContextMenuEntry.Single("PDF", onClick = { /* Export as PDF */ }),
                    ContextMenuEntry.Single("PNG", onClick = { /* Export as PNG */ }),
                    ContextMenuEntry.Divider,
                    ContextMenuEntry.Single("Other...", onClick = { /* Show export dialog */ })
                )
            ),
            ContextMenuEntry.Divider, ContextMenuEntry.Single(
                label = "Delete",
                enabled = false, // Disabled item  
                onClick = { /* Handle delete */ }
            )
        )
    }
    ) {
        // Your content here
        Card(modifier = Modifier.size(200.dp)) {
            Text("Right-click for advanced menu")
        }
    }
}
```  
  
### Manual State Management  
  
For more control over menu behavior, you can manage the state manually:  
  
```kotlin  
@Composable
fun ManualStateExample() {
    val contextMenuState = rememberContextMenuState()
    var selectedItem by remember { mutableStateOf<String?>(null) }
    ContextMenuArea(state = contextMenuState) {
        ListItem(
            modifier = Modifier.onClick(
                enabled = true,
                interactionSource = MutableInteractionSource()
            ) {
                selectedItem = "Hello World"
                contextMenuState.show(
                    position = IntOffset(0, 0),
                    items = listOf(
                        ContextMenuEntry.Single(
                            "Option 1",
                            onClick = { /* Handle option 1 click for item */ }),
                        ContextMenuEntry.Single(
                            "Option 2",
                            onClick = { /* Handle option 2 click for item. */ })
                    )
                )
            }
        ) {
            Text("Hello World")
        }
    }
}
```  
  
## API Reference  
  
### ContextMenuArea  
  
The main composable for creating context menu areas.  
  
```kotlin  
@Composable  
fun ContextMenuArea(  
 items: () -> List<ContextMenuEntry>,
 modifier: Modifier = Modifier,
 content: @Composable () -> Unit
)  
  
@Composable
fun ContextMenuArea(
    modifier: Modifier = Modifier,
    state: HierarchicalContextMenuState,
    content: @Composable () -> Unit
)
```  
  
### HierarchicalContextMenuState  
  
State holder for managing context menu visibility and hierarchy.  
  
```kotlin  
class HierarchicalContextMenuState {  
	val openMenus: List<MenuLevel>
 	fun show(position: IntOffset, items: List<ContextMenuEntry>)  
 	fun hide()
 	fun onItemHover(item: ContextMenuEntry, bottomRight: IntOffset)
}  
```  

### Utility Functions  
  
#### rememberContextMenuState  
```kotlin  
@Composable  
fun rememberContextMenuState(): HierarchicalContextMenuState  
```  
Creates and remembers a context menu state for the current composition.  
  
#### contextMenuOpenDetector  
```kotlin  
fun Modifier.contextMenuOpenDetector(  
	state: HierarchicalContextMenuState,
	items: () -> List<ContextMenuEntry>
): Modifier  
```  
Modifier that detects right-click events and shows the context menu.  
  
## Customization  
  
### Theme Customization  
  
The library uses a theme system for consistent styling. You can customize colors, dimensions and typography through the `ContextMenuTheme`:  
```kotlin
ContextMenuTheme(
    colors = darkContextMenuColor // or provide a .copy/custom instance,
    tokens = defaultContextMenuTokens // or provide a .copy/ustom instance,
    typography = defaultContextMenuTypography // or provide a .copy/custom instance
) {
    DesktopApp()
}
```
  
### Custom Representation  
  
For complete customization, implement your own `ContextMenuRepresentation`:  
  
```kotlin  
class CustomContextMenuRepresentation : ContextMenuRepresentation {  
   @Composable override fun Representation(state: HierarchicalContextMenuState) {
    // Your custom menu implementation
   }
}  
```
