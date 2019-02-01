@file:Suppress("unused")

package ca.warp7.actionkt

fun ActionDSL.wait(seconds: Int) = wait(seconds.toDouble())

fun ActionDSL.wait(seconds: Double) = action { finishWhen { elapsed > seconds } }