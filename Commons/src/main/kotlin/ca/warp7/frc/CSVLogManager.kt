package ca.warp7.frc

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class CSVLogManager {
    private val rootDir: File = File("/home/lvuser/RobotLogs")
    private val format = SimpleDateFormat("yyyy_MM_dd hh_mm_ss")

    init {
        if (!rootDir.exists()) rootDir.mkdir()
    }

    private var logDir: File? = null
    private var loggers: MutableList<CSVLogger> = mutableListOf()

    fun startSession(name: String) {
        val dateStr = format.format(Date())
        logDir = File(rootDir, "$dateStr $name/")
    }

    fun endSession() {
        for (logger in loggers) logger.close()
        loggers.clear()
    }

    fun getLogger(name: String): CSVLogger {
        val logDir = logDir!!
        val file = File(logDir, "$name.csv")
        val writer = PrintWriter(BufferedWriter(FileWriter(file)), false)
        val logger = CSVLoggerImpl(writer)
        loggers.add(logger)
        return logger
    }
}