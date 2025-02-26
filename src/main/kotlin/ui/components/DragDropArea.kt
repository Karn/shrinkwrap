package ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.*
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ui.dashedBorder
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.toPath

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DragDropArea(
    onFilesDropped: (List<Path>) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    var isDragTargetActive by remember { mutableStateOf(false) }
    
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onEntered(event: DragAndDropEvent) {
                isDragTargetActive = true
            }

            override fun onExited(event: DragAndDropEvent) {
                isDragTargetActive = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDragTargetActive = false
                val paths = (event.dragData() as? DragData.FilesList)
                    ?.readFiles()
                    ?.map(::URI)
                    ?.map(URI::toPath)
                    ?: return false

                onFilesDropped(paths)
                return true
            }
        }
    }

    Box(
        modifier = modifier
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            )
    ) {
        content()

        if (isDragTargetActive) {
            DragOverlay()
        }
    }
}

@Composable
private fun DragOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = 2.dp,
                color = MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.small,
            )
            .background(
                color = MaterialTheme.colors.surface.copy(alpha = 0.87f),
                shape = MaterialTheme.shapes.small,
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Drop files here", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun EmptyStateMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .dashedBorder(
                color = MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.small,
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Drag & drop or Browse", fontFamily = FontFamily.Monospace)
            Text(
                text = "⌘+V to paste from clipboard",
                style = MaterialTheme.typography.caption,
            )
        }
    }
} 