package ca.warp7.pathplanner

import ca.warp7.frc.*
import ca.warp7.frc.geometry.*
import ca.warp7.frc.path.*
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import javax.imageio.ImageIO
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate")
class PathPlanner : PApplet() {

    override fun settings() {
        size(768, 512)
    }

    val wheelBaseRadius = inchesToMeters(12.4)
    val maxVel = feetToMeters(12.0)

    val kPixelsPerMeter = 494 / 8.2296

    val Double.my2x: Double
        get() = (17.0 + (512.0 - 17.0) / 2 + kPixelsPerMeter * this)

    val Double.mx2y: Double
        get() = (493.0 - kPixelsPerMeter * this)

    val Translation2D.newXY get() = Translation2D(y.my2x, x.mx2y)

    var waypoints: Array<Pose2D> = emptyArray()

    var intermediate: List<QuinticSegment2D> = emptyList()

    var splines: List<CurvatureState<Pose2D>> = listOf()

    var controlPoints = mutableListOf<ControlPoint>()

    var curvatureSum = 0.0

    var arcLength = 0.0

    data class ControlPoint(var pos: Translation2D, var heading: Translation2D, var dir: Translation2D)

    fun lineTo(a: Translation2D, b: Translation2D) =
            line(a.x.toFloat(), a.y.toFloat(), b.x.toFloat(), b.y.toFloat())

    fun vertex(a: Translation2D) = vertex(a.x.toFloat(), a.y.toFloat())

    val kTriangleRatio = 1 / sqrt(3.0)

    val bg = PImage(ImageIO.read(PathPlanner::class.java.getResource("/field.PNG")))

    override fun setup() {
        waypoints = arrayOf(
                waypoint(6, -4, 0),
                waypoint(10, -4, 0),
                waypoint(14, -8, -75),
                waypoint(17.5, -11.5, -30)
        )
        regenerateSplines()
    }

    fun regenerateSplines() {
        controlPoints.clear()
        waypoints.forEach {
            val pos = it.translation.newXY
            val heading = (it.translation + it.rotation.translation.scaled(0.5))
            val dir = it.rotation.norm.translation
            controlPoints.add(ControlPoint(pos, heading, dir))
        }
        intermediate = quinticSplinesOf(*waypoints)
        curvatureSum = intermediate.sumDCurvature2()
        splines = intermediate.parameterized()
        arcLength = splines.zipWithNext { a: CurvatureState<Pose2D>, b: CurvatureState<Pose2D> ->
            (b.state.translation - a.state.translation).mag
        }.sum()
        redrawScreen()
    }

    fun ControlPoint.drawArrow() {
        val r1 = dir.scaled(0.1524 * kTriangleRatio * 2)
        val r2 = r1.rotate(Rotation2D(0.0, 1.0)).scaled(kTriangleRatio)
        val r3 = r1.rotate(Rotation2D(0.0, -1.0)).scaled(kTriangleRatio)
        ellipse(pos.x.toFloat(), pos.y.toFloat(), 12f, 12f)
        val start = pos + dir.scaled(6.0).run { Translation2D(y, -x) }
        lineTo(start, heading.newXY)
        val a1 = (heading + r1).newXY
        val a2 = (heading + r2).newXY
        val a3 = (heading + r3).newXY
        beginShape()
        vertex(a1)
        vertex(a2)
        vertex(a3)
        vertex(a1)
        endShape()
    }

    fun redrawScreen() {
        background(0f, 0f, 0f)
        image(bg, 17f, 0f)
        noFill()
        stroke(90f, 138f, 222f)
        strokeWeight(1f)
        line(17f, 0f, 17f, 493f)
        line(512f, 0f, 512f, 493f)
        strokeWeight(2f)
        line(17f, 492f, 512f, 492f)

        var t = splines[0].state.translation
        var normal = (splines[0].state.rotation.normal * wheelBaseRadius).translation
        var left = (t - normal).newXY
        var right = (t + normal).newXY

        strokeWeight(2f)

        val maxK = splines.maxBy { it.curvature.absoluteValue }?.curvature?.absoluteValue ?: 1.0
        for (i in 1 until splines.size) {
            t = splines[i].state.translation
            normal = (splines[i].state.rotation.normal * wheelBaseRadius).translation
            val newLeft = (t - normal).newXY
            val newRight = (t + normal).newXY

            val kx = splines[i].curvature.absoluteValue / maxK
            val r = interpolate(0.0, 192.0, kx).toFloat() + 64
            val g = 255 - interpolate(0.0, 192.0, kx).toFloat()
            stroke(r, g, 0f)
            lineTo(left, newLeft)
            lineTo(right, newRight)
            left = newLeft
            right = newRight
        }

        stroke(255f, 255f, 0f)
        noFill()
        val angular = maxVel / (1 / maxK + wheelBaseRadius)
        val linear = maxVel - (angular * wheelBaseRadius)
        val msg = "Σdk: ${curvatureSum.f}\n" +
                "maxK: ${maxK.f}\n" +
                "minRadius: ${metersToFeet(1 / maxK).f}ft\n" +
                "vel@min ${metersToFeet(linear).f}ft/s\n" +
                "arcLength: ${metersToFeet(arcLength).f}ft\n"
        controlPoints.forEachIndexed { index, controlPoint ->
            if (selectedIndex == index) {
                fill(255f, 255f, 255f)
                noStroke()
                val translation = waypoints[index].translation
                val angle = waypoints[index].rotation.degrees
                textSize(16f)
                var cpMsg = "index: $index\n" +
                        "x: ${metersToFeet(translation.x).f}ft\n" +
                        "y: ${metersToFeet(translation.y).f}ft\n" +
                        "θ: ${angle.f}°\n"
                if (index != controlPoints.size - 1) {
                    cpMsg += "ddx0: ${intermediate[index].ddx0.f}\n" +
                            "ddy0: ${intermediate[index].ddy0.f}\n" +
                            "ddx1: ${intermediate[index].ddx1.f}\n" +
                            "ddy1: ${intermediate[index].ddy1.f}\n"
                }
                text(cpMsg + msg, 529f, 34f)
                stroke(90f, 138f, 222f)
                strokeWeight(2f)
                noFill()
            } else {
                stroke(255f, 255f, 0f)
                strokeWeight(2f)
                noFill()
            }
            controlPoint.drawArrow()
        }
        if (selectedIndex == -1) {
            fill(255f, 255f, 255f)
            noStroke()
            textSize(16f)
            text(msg, 529f, 34f)
        }
    }

    override fun draw() {
        if (selectionChanged) {
            redrawScreen()
            selectionChanged = false
        }
    }

    var selectedIndex = -1
    var selectionChanged = false

    override fun mouseClicked() {
        var found = false
        controlPoints.forEachIndexed { index, controlPoint ->
            val mouse = Translation2D(mouseX.toDouble(), mouseY.toDouble())
            val pc = (controlPoint.pos - mouse).mag
            val hc = (controlPoint.heading.newXY - mouse).mag
            if (pc < 16 || hc < 16) {
                selectionChanged = true
                found = true
                selectedIndex = (if (selectedIndex != index) index else -1)
            }
        }
        if (!found && selectedIndex != -1) {
            selectionChanged = true
            selectedIndex = -1
        }
    }

    val step = 0.0254 * 12 / 10
    val pAngStep = Rotation2D.fromDegrees(1.0)
    val nAngStep = Rotation2D.fromDegrees(-1.0)
    val reversedRotation = Rotation2D(-1.0, 0.0)

    override fun keyPressed() {
        if (selectedIndex == -1) processDeselected()
        else processSelected()
    }

    fun processDeselected() {
        when {
            key.toInt() == CODED -> when (keyCode) {
                PConstants.UP -> {
                    for (i in 0 until waypoints.size) {
                        waypoints[i] = waypoints[i].run {
                            Pose2D(translation.transform(Translation2D(step, 0.0)), rotation)
                        }
                    }
                    regenerateSplines()
                }
                PConstants.DOWN -> {
                    for (i in 0 until waypoints.size) {
                        waypoints[i] = waypoints[i].run {
                            Pose2D(translation.transform(Translation2D(-step, 0.0)), rotation)
                        }
                    }
                    regenerateSplines()
                }
                PConstants.LEFT -> {
                    for (i in 0 until waypoints.size) {
                        waypoints[i] = waypoints[i].run {
                            Pose2D(translation.transform(Translation2D(0.0, -step)), rotation)
                        }
                    }
                    regenerateSplines()
                }
                PConstants.RIGHT -> {
                    for (i in 0 until waypoints.size) {
                        waypoints[i] = waypoints[i].run {
                            Pose2D(translation.transform(Translation2D(0.0, step)), rotation)
                        }
                    }
                    regenerateSplines()
                }
            }
            key == 'r' -> {
                val newWaypoints = waypoints.reversedArray()
                for (i in 0 until newWaypoints.size) {
                    newWaypoints[i] = newWaypoints[i].run {
                        Pose2D(translation, rotation.rotate(reversedRotation))
                    }
                }
                waypoints = newWaypoints
                regenerateSplines()
            }
            key == 'f' -> {
                for (i in 0 until waypoints.size) {
                    waypoints[i] = waypoints[i].run {
                        Pose2D(Translation2D(translation.x, -translation.y), Rotation2D(rotation.cos, -rotation.sin))
                    }
                }
                regenerateSplines()
            }
        }
    }

    fun processSelected() {
        when {
            key.toInt() == CODED -> when (keyCode) {
                PConstants.UP -> {
                    waypoints[selectedIndex] = waypoints[selectedIndex].run {
                        Pose2D(translation.transform(Translation2D(step, 0.0)), rotation)
                    }
                    regenerateSplines()
                }
                PConstants.DOWN -> {
                    waypoints[selectedIndex] = waypoints[selectedIndex].run {
                        Pose2D(translation.transform(Translation2D(-step, 0.0)), rotation)
                    }
                    regenerateSplines()
                }
                PConstants.LEFT -> {
                    waypoints[selectedIndex] = waypoints[selectedIndex].run {
                        Pose2D(translation.transform(Translation2D(0.0, -step)), rotation)
                    }
                    regenerateSplines()
                }
                PConstants.RIGHT -> {
                    waypoints[selectedIndex] = waypoints[selectedIndex].run {
                        Pose2D(translation.transform(Translation2D(0.0, step)), rotation)
                    }
                    regenerateSplines()
                }
            }
            key == 'q' -> {
                waypoints[selectedIndex] = waypoints[selectedIndex].run {
                    Pose2D(translation, rotation.rotate(nAngStep))
                }
                regenerateSplines()
            }
            key == 'w' -> {
                waypoints[selectedIndex] = waypoints[selectedIndex].run {
                    Pose2D(translation, rotation.rotate(pAngStep))
                }
                regenerateSplines()
            }
            key == 'n' -> {
                val newWaypoints = arrayOfNulls<Pose2D>(waypoints.size + 1)
                for (i in 0..selectedIndex) newWaypoints[i] = waypoints[i]
                for (i in selectedIndex + 2..waypoints.size) newWaypoints[i] = waypoints[i - 1]
                val newPoint = waypoints[selectedIndex].run {
                    Pose2D(translation + rotation.norm.scaled(0.75).translation, rotation)
                }
                newWaypoints[selectedIndex + 1] = newPoint
                waypoints = newWaypoints.requireNoNulls()
                regenerateSplines()
            }
            key == 'd' -> {
                val newWaypoints = arrayOfNulls<Pose2D>(waypoints.size - 1)
                for (i in 0 until selectedIndex) newWaypoints[i] = waypoints[i]
                for (i in selectedIndex + 1 until waypoints.size) newWaypoints[i - 1] = waypoints[i]
                waypoints = newWaypoints.requireNoNulls()
                regenerateSplines()
            }
        }
    }
}