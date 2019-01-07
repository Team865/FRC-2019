@file:Suppress("unused")

package ca.warp7.frckt


fun runRobot() = Unit

fun mainLoop() = Unit

fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))