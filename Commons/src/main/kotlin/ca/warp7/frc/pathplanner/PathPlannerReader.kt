package ca.warp7.frc.pathplanner

import java.io.File

fun loadTrajectory(name: String): Any {
    val leftFile = "/home/lvuser/paths/${name}left.csv"
    val rightFile = "/home/lvuser/paths/${name}right.csv"
    val leftData = File(leftFile).readLines()
    val rightData = File(rightFile).readLines()
    val leftArray = leftData.map { it.split(",").map(String::trim).map(String::toDouble).toDoubleArray() }
    val rightArray = rightData.map { it.split(",").map(String::trim).map(String::toDouble).toDoubleArray() }
    return loadTrajectory(leftArray.zip(rightArray))
}

fun loadTrajectory(leftRight: Iterable<Pair<DoubleArray, DoubleArray>>): Any {
    TODO()
}