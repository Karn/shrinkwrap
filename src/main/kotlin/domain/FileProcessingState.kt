package domain

import java.nio.file.Path

data class FileProcessingState(
    val files: Map<Path, CompressedFileTarget> = emptyMap()
)

sealed class CompressedFileTarget {
    abstract val path: Path

    data class Pending(
        override val path: Path
    ) : CompressedFileTarget()

    data class Processing(
        override val path: Path
    ) : CompressedFileTarget()

    data class Completed(
        override val path: Path,
        val initialSizeBytes: Long,
        val finalSizeBytes: Long
    ) : CompressedFileTarget()

    data class InvalidFile(
        override val path: Path
    ) : CompressedFileTarget()

    data class Error(
        override val path: Path,
        val message: String
    ) : CompressedFileTarget()
} 