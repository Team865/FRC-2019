package ca.warp7.frc.pathplanner

import ca.warp7.frc.Data
import ca.warp7.frc.DataKt
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc.trajectory.DifferentialTimedState
import ca.warp7.frc.trajectory.IndexedTrajectory
import ca.warp7.frc.trajectory.TimedView
import java.io.File

fun loadTrajectory(name: String): Any {
    val leftFile = "/home/lvuser/paths/${name}left.csv"
    val rightFile = "/home/lvuser/paths/${name}right.csv"
    try {
        val leftData = File(leftFile).readLines()
        val rightData = File(rightFile).readLines()
        val leftArray = leftData.map { it.split(",").map(String::trim).map(String::toDouble).toDoubleArray() }
        val rightArray = rightData.map { it.split(",").map(String::trim).map(String::toDouble).toDoubleArray() }
        return loadTrajectory(leftArray.zip(rightArray))
    } catch (e: Exception) {
        return loadTrajectory(listOf())
    }
}

fun loadTrajectory(leftRight: List<Pair<DoubleArray, DoubleArray>>) =
        TimedView(IndexedTrajectory(leftRight.map {
            val t = it.first[0]
            val x = (it.first[1] + it.second[1]) / 2
            val y = (it.first[2] + it.second[2]) / 2
            val h = it.first[3]
            val lp = it.first[4]
            val lv = it.first[5]
            val la = it.first[6]
            val rp = it.second[4]
            val rv = it.second[5]
            val ra = it.second[6]
            return@map DifferentialTimedState(Pose2D(Translation2D(x, y), Rotation2D.fromDegrees(h)),
                    t, lp, lv, la, rp, rv, ra)
        }).points)

fun main() {
    val a = loadTrajectory(DataKt.pathLeft.zip(Data.pathRight))
    println(a)
}