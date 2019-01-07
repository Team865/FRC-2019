package ca.warp7.frckt

fun runRobot() = Lifecycle.runRobot()

fun mainLoop() = Lifecycle.mainLoop()

fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))