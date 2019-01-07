package ca.warp7.frc2019

import ca.warp7.frckt.NothingAction

@Suppress("unused")
object Autonomous {

    val mode get() = nothingMode

    private val nothingMode = { NothingAction() }
}