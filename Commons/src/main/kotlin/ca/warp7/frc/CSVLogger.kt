package ca.warp7.frc

interface CSVLogger : AutoCloseable {
    fun writeHeaders(vararg headers: String)
    fun writeData(vararg data: Number)
}