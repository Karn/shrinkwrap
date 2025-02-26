import domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.file.Path

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val compressionService: CompressionService = GhostscriptCompressionService()
) {
    private val _state = MutableStateFlow(FileProcessingState())
    val state = _state.asStateFlow()

    private val processor = Channel<Path>(Channel.BUFFERED)

    init {
        val scope = CoroutineScope(Dispatchers.IO.limitedParallelism(3) + SupervisorJob())
        repeat(5) {
            scope.launch {
                for (filePath in processor) {
                    // Mark as processing
                    _state.update {
                        it.copy(
                            files = it.files + (filePath to CompressedFileTarget.Processing(filePath))
                        )
                    }

                    val result = compressionService.compressFile(filePath, CompressionQuality.MEDIUM)
                    _state.update {
                        it.copy(
                            files = it.files + (filePath to when (result) {
                                is CompressionResult.Success -> CompressedFileTarget.Completed(
                                    path = filePath,
                                    initialSizeBytes = result.initialSizeBytes,
                                    finalSizeBytes = result.finalSizeBytes
                                )
                                is CompressionResult.InvalidFile -> CompressedFileTarget.InvalidFile(filePath)
                                is CompressionResult.Error -> CompressedFileTarget.Error(filePath, result.message)
                            })
                        )
                    }
                }
            }
        }
    }

    fun add(filePath: Path) {
        _state.update {
            it.copy(
                files = mapOf(
                    filePath to CompressedFileTarget.Pending(filePath)
                ) + it.files
            )
        }

        processor.trySend(filePath)
    }

    fun addMultiple(filePaths: List<Path>) {
        filePaths.forEach { add(it) }
    }
} 