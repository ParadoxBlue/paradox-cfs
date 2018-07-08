package blue.paradox.cfs.core.writer

import blue.paradox.cfs.Config
import java.io.BufferedWriter
import java.io.File

internal object ConfigWriter {

    fun toFile(config: Config, file: File) {
        val content = config.content
        val fileWriter = file.bufferedWriter()
        writeMap(content, fileWriter, 0)
        fileWriter.close()
    }

    private fun writeValue(value: Any, writer: BufferedWriter, depth: Int) {
        when (value) {
            is String -> writer.write("\"$value\"")
            is Boolean, is Number -> writer.write("$value")

            is List<*> -> {
                for (element in value)
                    writeValue(element ?: continue, writer, depth)
            }

            is Map<*, *> -> {
                writer.appendln("[")
                writeMap(value, writer, depth + 1)
                writer.write("\t".repeat(depth))
                writer.write("]")
            }

            is Config -> writeValue(value.content, writer, depth)
        }
    }

    private fun writeMap(map: Map<*, *>, writer: BufferedWriter, depth: Int) {
        for ((key, value) in map) {
            writer.write("\t".repeat(depth))
            writer.write("$key: ")
            writeValue(value ?: continue, writer, depth)
            writer.appendln()
        }
    }

}