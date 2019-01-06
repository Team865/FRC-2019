package ca.warp7.frc2019

import ca.warp7.frckt.autonomousMode

@Suppress("unused")
object Autonomous {

    val mode get() = nothingMode

    private val nothingMode = autonomousMode { exec { } }
}