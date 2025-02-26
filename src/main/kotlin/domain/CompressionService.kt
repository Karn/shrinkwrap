package domain

import infrastructure.CommandExecutor
import infrastructure.SystemCommandExecutor
import java.io.IOException
import java.nio.file.AccessDeniedException
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.io.path.isReadable
import kotlin.io.path.isWritable

interface CompressionService {
    suspend fun compressFile(filePath: Path, quality: CompressionQuality): CompressionResult
    suspend fun validatePdfFile(filePath: Path): Boolean
}

enum class CompressionQuality(val gsFormat: String) {
    HIGH("printer"),
    MEDIUM("ebook"),
    LOW("screen")
}

sealed class CompressionResult {
    data class Success(
        val initialSizeBytes: Long,
        val finalSizeBytes: Long
    ) : CompressionResult()

    data object InvalidFile : CompressionResult()

    data class Error(
        val message: String
    ) : CompressionResult()
}

class GhostscriptCompressionService(
    private val commandExecutor: CommandExecutor = SystemCommandExecutor()
) : CompressionService {

    override suspend fun validatePdfFile(filePath: Path): Boolean {
        try {
            // Check file permissions first
            if (!filePath.exists()) {
                return false
            }
            if (!filePath.isReadable()) {
                return false
            }
            
            val tempDir = createTempDirectory("shrinkwrap")
            val command = listOf("head", "-c", "4", "$filePath")
            val result = commandExecutor.execute(command, tempDir.toFile())
            return result?.stdout == "%PDF"
        } catch (e: SecurityException) {
            return false
        } catch (e: AccessDeniedException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun compressFile(filePath: Path, quality: CompressionQuality): CompressionResult {
        try {
            // Check file permissions before proceeding
            if (!filePath.exists()) {
                return CompressionResult.Error("File does not exist")
            }
            if (!filePath.isReadable()) {
                return CompressionResult.Error("Permission denied: Cannot read file. Please grant file access permissions to Shrinkwrap.")
            }
            if (!filePath.isWritable()) {
                return CompressionResult.Error("Permission denied: Cannot write to file. Please ensure the file is not read-only.")
            }

            val tempDir = try {
                createTempDirectory("shrinkwrap")
            } catch (e: SecurityException) {
                return CompressionResult.Error("Permission denied: Cannot create temporary directory. Please grant file system access to Shrinkwrap.")
            } catch (e: IOException) {
                return CompressionResult.Error("Cannot create temporary directory: ${e.message}")
            }

            // Validate PDF file
            if (!validatePdfFile(filePath)) {
                return CompressionResult.InvalidFile
            }

            // Get initial file size
            val initialSize = try {
                filePath.toFile().length()
            } catch (e: SecurityException) {
                return CompressionResult.Error("Permission denied: Cannot read file size")
            } catch (e: Exception) {
                return CompressionResult.Error("Could not read initial file size: ${e.message}")
            }

            // Run compression
            val compressionCommand = buildCompressionCommand(filePath, quality, tempDir)
            val compressionResult = commandExecutor.execute(compressionCommand, tempDir.toFile())
            
            if (compressionResult?.exitValue != 0) {
                val errorMessage = compressionResult?.stderr ?: "Unknown compression error"
                return if (errorMessage.contains("Permission denied") || errorMessage.contains("Operation not permitted")) {
                    CompressionResult.Error("Permission denied: Ghostscript cannot access the file. Please grant necessary permissions to Shrinkwrap.")
                } else {
                    CompressionResult.Error("Compression failed: $errorMessage")
                }
            }

            // Move compressed file back
            val moveCommand = listOf("mv", "${tempDir}/compressed1.pdf", "$filePath")
            val moveResult = commandExecutor.execute(moveCommand, tempDir.toFile())
            
            if (moveResult?.exitValue != 0) {
                val errorMessage = moveResult?.stderr ?: "Unknown move error"
                return if (errorMessage.contains("Permission denied") || errorMessage.contains("Operation not permitted")) {
                    CompressionResult.Error("Permission denied: Cannot replace original file. Please ensure the file is writable.")
                } else {
                    CompressionResult.Error("Could not move compressed file: $errorMessage")
                }
            }

            // Get final file size
            val finalSize = try {
                filePath.toFile().length()
            } catch (e: SecurityException) {
                return CompressionResult.Error("Permission denied: Cannot read final file size")
            } catch (e: Exception) {
                return CompressionResult.Error("Could not read final file size: ${e.message}")
            }

            return CompressionResult.Success(initialSize, finalSize)
        } catch (e: SecurityException) {
            return CompressionResult.Error("Permission denied: ${e.message}. Please grant file access permissions to Shrinkwrap.")
        } catch (e: AccessDeniedException) {
            return CompressionResult.Error("Access denied: ${e.message}. Please check file permissions.")
        }
    }

    private fun buildCompressionCommand(
        path: Path,
        quality: CompressionQuality,
        outputDir: Path
    ): List<String> {
        return listOf(
            "gs",
            "-sDEVICE=pdfwrite",
            "-dPDFSETTINGS=/${quality.gsFormat}",
            "-dNOPAUSE",
            "-dQUIET",
            "-dBATCH",
            "-o",
            "${outputDir}/compressed1.pdf",
            "$path",
        )
    }
} 