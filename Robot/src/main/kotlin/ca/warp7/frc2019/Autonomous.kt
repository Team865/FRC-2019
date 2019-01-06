package ca.warp7.frc2019

import ca.warp7.frckt.autonomousMode
import ca.warp7.frckt.driver
import ca.warp7.frckt.operator
import kotlin.math.abs

@Suppress("unused")
object Autonomous {

    val exitCondition = { abs(driver.leftXAxis) > 0.8 || abs(operator.leftYAxis) > 0.8 }

    val mode get() = nothingMode

    private val nothingMode = autonomousMode { exec { } }
}