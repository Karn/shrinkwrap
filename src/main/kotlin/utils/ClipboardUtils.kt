package utils

import MainViewModel
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.File


fun handleClipboardPaste(keyEvent: KeyEvent, mainViewModel: MainViewModel): Boolean {
    if (keyEvent.type == KeyEventType.KeyUp && keyEvent.isMetaPressed && keyEvent.key == Key.V) {
        val clipboardManager = Toolkit.getDefaultToolkit().systemClipboard ?: return false

        val paths = clipboardManager.availableDataFlavors
            ?.filter { dataFlavor -> dataFlavor?.isFlavorJavaFileListType == true }
            ?.map { _ ->
                val transferable: Transferable = clipboardManager.getContents(null)
                val fileList = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                fileList.map { it.toPath() }
            }
            ?.flatten()

        mainViewModel.addMultiple(paths.orEmpty())

        return true
    }
    return false
}
