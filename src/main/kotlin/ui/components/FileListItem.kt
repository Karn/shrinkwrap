package ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.CompressedFileTarget
import utils.formatFileSize
import java.nio.file.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    filePath: Path,
    fileTarget: CompressedFileTarget,
    onItemClick: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple()
            ) {
                onItemClick(filePath)
            }
            .padding(4.dp)
    ) {
        Text(
            text = when (fileTarget) {
                is CompressedFileTarget.Completed -> "✓"
                is CompressedFileTarget.InvalidFile -> "!"
                is CompressedFileTarget.Error -> "×"
                is CompressedFileTarget.Processing -> "◐"
                is CompressedFileTarget.Pending -> "○"
            },
            style = MaterialTheme.typography.caption,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.weight(1f)
                .then(if (isHovered) Modifier.basicMarquee() else Modifier),
            text = "${filePath.fileName}",
            style = MaterialTheme.typography.caption,
            maxLines = 1,
        )

        Spacer(Modifier.width(8.dp))

        if (isHovered && fileTarget !is CompressedFileTarget.Completed) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.caption,
            )
        } else {
            Text(
                text = when (fileTarget) {
                    is CompressedFileTarget.Completed -> {
                        val compressionRatio =
                            ((fileTarget.initialSizeBytes - fileTarget.finalSizeBytes) * 100 / fileTarget.initialSizeBytes).toInt()
                        "${formatFileSize(fileTarget.finalSizeBytes)} (${compressionRatio}%)"
                    }
                    is CompressedFileTarget.Error -> fileTarget.message
                    is CompressedFileTarget.InvalidFile -> "Not a PDF"
                    else -> formatFileSize(filePath.toFile().length())
                },
                style = MaterialTheme.typography.caption,
            )
        }
    }
} 