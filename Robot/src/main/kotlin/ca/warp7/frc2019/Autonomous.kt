package ca.warp7.frc2019

import ca.warp7.frckt.autonomousMode

@Suppress("unused")
object Autonomous {

    fun getMode() = nothingMode

    private val nothingMode = autonomousMode { exec { } }
}