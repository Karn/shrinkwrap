package infrastructure

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

interface CommandExecutor {
    fun execute(command: List<String>, workingDir: File): ProcessResult?
}

data class ProcessResult(
    val exitValue: Int,
    val stdout: String,
    val stderr: String,
)

class SystemCommandExecutor : CommandExecutor {
    override fun execute(command: List<String>, workingDir: File): ProcessResult? {
        return try {
            val proc = ProcessBuilder(*command.toTypedArray())
                .directory(workingDir)
                .start()

            proc.waitFor(10, TimeUnit.SECONDS)

            ProcessResult(
                exitValue = proc.exitValue(),
                stdout = proc.inputStream.bufferedReader().readText(),
                stderr = proc.errorStream.bufferedReader().readText(),
            )
        } catch (e: IOException) {
            println("Command execution error: ${e.message}")
            e.printStackTrace()
            null
        }
    }
} 