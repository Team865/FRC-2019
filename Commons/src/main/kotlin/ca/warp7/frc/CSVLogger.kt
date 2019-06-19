package ca.warp7.frc

interface CSVLogger : AutoCloseable {
    fun withHeaders(vararg headers: String): CSVLogger
    fun writeData(vararg data: Number)
}