import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ui.components.DragDropArea
import ui.components.EmptyStateMessage
import ui.components.FileListItem
import utils.handleClipboardPaste
import utils.openFileInFinder

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun App(mainViewModel: MainViewModel) {
    val mainState = mainViewModel.state.collectAsState()

    MaterialTheme(
        colors = lightColors(
            primary = Color(0xfff5f5f5),
            background = Color(0xFF_F9FAFA),
            surface = Color(0xFF_EFF0F0),
        ),
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(4.dp),
        ),
        typography = MaterialTheme.typography.copy(
            caption = MaterialTheme.typography.caption.copy(
                fontFamily = FontFamily.Monospace,
            )
        ),
    ) {
        DragDropArea(
            onFilesDropped = { paths -> mainViewModel.addMultiple(paths) },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            if (mainState.value.files.isEmpty()) {
                EmptyStateMessage()
            } else {
                LazyColumn {
                    items(
                        items = mainState.value.files.asSequence().toList(),
                        key = { (key, _) -> key }
                    ) { (filePath, fileTarget) ->
                        FileListItem(
                            filePath = filePath,
                            fileTarget = fileTarget,
                            onItemClick = { path -> openFileInFinder(path) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val mainViewModel = MainViewModel()

    val errorHandler = Thread.UncaughtExceptionHandler { _, throwable ->
        println(throwable.message)
    }

    Thread.currentThread().uncaughtExceptionHandler = errorHandler

    Window(
        title = "Shrinkwrap",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 400.dp,
            height = 200.dp,
        ),
        resizable = false,
        onCloseRequest = ::exitApplication,
        onPreviewKeyEvent = { keyEvent ->
            handleClipboardPaste(keyEvent, mainViewModel)
        }
    ) {
        App(mainViewModel)
    }
}
