@file:Suppress("unused")

package ca.warp7.frckt

internal object Lifecycle {

    val subsystems: MutableSet<Subsystem> = mutableSetOf()

    fun runRobot() = Unit

    fun mainLoop() = Unit
}


