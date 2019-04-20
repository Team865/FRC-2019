package ca.warp7.frc

import java.io.PrintWriter

internal class CSVLoggerImpl(private val writer: PrintWriter) : CSVLogger {

    internal var isDone = false
    private var count = 0

    override fun writeHeaders(vararg headers: String) {
        writer.println(headers.joinToString(", "))
        writer.flush()
    }

    override fun writeData(vararg data: Number) {
        writer.println(data.joinToString(", "))
        count++
        if (count > 50) {
            writer.flush()
            count = 0
        }
    }

    override fun close() {
        if (!isDone) {
            isDone = true
            writer.close()
        }
    }
}