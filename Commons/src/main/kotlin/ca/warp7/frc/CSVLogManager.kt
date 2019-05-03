package ca.warp7.frc

import edu.wpi.first.wpilibj.Filesystem
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class CSVLogManager {
    private val rootDir: File = File(Filesystem.getOperatingDirectory(), "/RobotLogs/")
    private val format = SimpleDateFormat("yyyy_MM_dd hh_mm_ss")

    init {
        if (!rootDir.exists()) rootDir.mkdir()
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            val crashFile = File(rootDir, "CRASH.txt")
            exception.printStackTrace(crashFile.printWriter())
        }
    }

    private var logDir: File? = null
    private var loggers: MutableList<CSVLogger> = mutableListOf()
    private var loggerNames: MutableMap<String, Int> = mutableMapOf()

    fun startSession(name: String) {
        val dateStr = format.format(Date())
        val dir = File(rootDir, "$dateStr $name/")
        if (!dir.exists()) dir.mkdir()
        logDir = dir
    }

    fun endSession() {
        for (logger in loggers) logger.close()
        loggers.clear()
    }

    fun getLogger(name: String): CSVLogger {
        val count = loggerNames[name] ?: 0 + 1
        loggerNames[name] = count
        val logDir = logDir!!
        val file = File(logDir, "$name-$count.csv")
        val writer = PrintWriter(BufferedWriter(FileWriter(file)), false)
        val logger = CSVLoggerImpl(writer)
        loggers.add(logger)
        return logger
    }
}