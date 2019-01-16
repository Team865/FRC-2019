package ca.warp7.frc2019

import ca.warp7.actionkt.runOnce

@Suppress("unused")
object Autonomous {

    val mode get() = nothingMode

    private val nothingMode = { runOnce { } }
}