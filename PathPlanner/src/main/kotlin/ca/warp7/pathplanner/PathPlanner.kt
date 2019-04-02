package ca.warp7.pathplanner

import ca.warp7.frc.*
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.solve
import ca.warp7.frc.geometry.*
import ca.warp7.frc.path.*
import ca.warp7.frc.trajectory.TimedConstraints
import ca.warp7.frc.trajectory.timedTrajectory
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import javax.imageio.ImageIO
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate")
class PathPlanner : PApplet() {

    data class ControlPoint(
            var pos: Translation2D,
            var heading: Translation2D,
            var dir: Translation2D
    )

    override fun settings() = size(910, 512)

    val kTriangleRatio = 1 / sqrt(3.0)
    val step = 0.0254 * 12 / 10
    val pAng = Rotation2D.fromDegrees(1.0)
    val nAng = Rotation2D.fromDegrees(-1.0)
    val reversedRotation = Rotation2D(-1.0, 0.0)

    val wheelBaseRadius = inchesToMeters(12.4)
    val maxVel = feetToMeters(12.0)

    val kPixelsPerMeter = 494 / 8.2296
    val Double.my2x: Double get() = (17.0 + (512.0 - 17.0) / 2 + kPixelsPerMeter * this)
    val Double.mx2y: Double get() = (493.0 - kPixelsPerMeter * this)
    val Double.px2y: Double get() = (this - 17.0 - (512.0 - 17.0) / 2) / kPixelsPerMeter
    val Double.py2x: Double get() = (493.0 - this) / kPixelsPerMeter
    val Translation2D.newXY get() = Translation2D(y.my2x, x.mx2y)
    val Translation2D.newXYNoOffset get() = Translation2D(kPixelsPerMeter * y, -kPixelsPerMeter * x)
    val Translation2D.oldXY get() = Translation2D(y.py2x, x.px2y)
    val Translation2D.oldXYNoOffset get() = Translation2D(y / kPixelsPerMeter, -x / kPixelsPerMeter)

    var waypoints: Array<Pose2D> = emptyArray()
    var intermediate: List<QuinticSegment2D> = emptyList()
    var splines: List<CurvatureState<Pose2D>> = emptyList()
    var trajectory: List<TimedConstraints> = emptyList()
    var controlPoints = mutableListOf<ControlPoint>()

    var curvatureSum = 0.0
    var arcLength = 0.0
    var trajectoryTime = 0.0
    var maxK = 0.0
    var maxAngular = 0.0

    var selectedIndex = -1
    var selectionChanged = false

    fun lineTo(a: Translation2D, b: Translation2D) = line(a.x.toFloat(), a.y.toFloat(), b.x.toFloat(), b.y.toFloat())
    fun vertex(a: Translation2D) = vertex(a.x.toFloat(), a.y.toFloat())
    val bg = PImage(ImageIO.read(PathPlanner::class.java.getResource("/field.PNG")))

    val model = DifferentialDriveModel(
            wheelRadius = 0.0,
            wheelbaseRadius = wheelBaseRadius * 2.0,
            maxVelocity = maxVel,
            maxAcceleration = feetToMeters(9.0),
            maxFreeSpeed = 0.0,
            speedPerVolt = 0.0,
            torquePerVolt = 0.0,
            frictionVolts = 0.0,
            linearInertia = 0.0,
            angularInertia = 0.0,
            maxVolts = 0.0
    )

    var draggingPoint = false
    var draggingAngle = false
    var draggedControlPoint: ControlPoint? = null

    var simulating = false
    var simTime = 0.0
    var simIndex = 0
    var simMsg = ""
    var simFrames = 0

    fun ControlPoint.drawArrow() {
        val r1 = dir.scaled(0.1524 * kTriangleRatio * 2)
        val r2 = r1.rotate(Rotation2D(0.0, 1.0)).scaled(kTriangleRatio)
        val r3 = r1.rotate(Rotation2D(0.0, -1.0)).scaled(kTriangleRatio)
        ellipse(pos.x.toFloat(), pos.y.toFloat(), 12f, 12f)
        val start = pos + dir.scaled(6.0).run { Translation2D(y, -x) }
        lineTo(start, heading)
        val a1 = heading + r1.newXYNoOffset
        val a2 = heading + r2.newXYNoOffset
        val a3 = heading + r3.newXYNoOffset
        beginShape()
        vertex(a1)
        vertex(a2)
        vertex(a3)
        vertex(a1)
        endShape()
    }

    fun drawText(t: String) {
        fill(255f, 255f, 255f)
        noStroke()
        textSize(15f)
        text(t, 529f, 15f)
    }

    override fun setup() {
        waypoints = arrayOf(
                waypoint(6, -4, 0),
                waypoint(10, -4, 0),
                waypoint(14, -8, -75),
                waypoint(17.5, -11.5, -30)
        )
        regenerate()
    }

    fun regenerate() {
        controlPoints.clear()
        waypoints.forEach {
            val pos = it.translation.newXY
            val heading = (it.translation + it.rotation.translation.scaled(0.5)).newXY
            val dir = it.rotation.norm.translation
            controlPoints.add(ControlPoint(pos, heading, dir))
        }
        intermediate = quinticSplinesOf(*waypoints)
        curvatureSum = intermediate.sumDCurvature2()
        splines = intermediate.parameterized()
        arcLength = splines.zipWithNext { a: CurvatureState<Pose2D>, b: CurvatureState<Pose2D> ->
            (b.state.translation - a.state.translation).mag
        }.sum()
        trajectory = splines.timedTrajectory(model, 0.0, 0.0)
        trajectoryTime = trajectory.last().t
        maxK = splines.maxBy { it.curvature.absoluteValue }?.curvature?.absoluteValue ?: 1.0
        maxAngular = trajectory.map { it.velocity * it.state.curvature.absoluteValue }.max() ?: 1.0
        redrawScreen()
    }

    fun redrawBackground() {
        background(0f, 0f, 0f)
        image(bg, 17f, 0f)
        noFill()
        stroke(90f, 138f, 222f)
        strokeWeight(1f)
        line(17f, 0f, 17f, 493f)
        line(512f, 0f, 512f, 493f)
        strokeWeight(2f)
        line(17f, 492f, 512f, 492f)
    }

    fun redrawInfoNoSim() {
        stroke(255f, 255f, 0f)
        noFill()
        val msg = "Σdk^2: ${curvatureSum.f}\n" +
                "maxK: ${maxK.f}\n" +
                "arcLength: ${metersToFeet(arcLength).f}ft\n" +
                "totalTime: ${trajectory.last().t.f}s"
        // draw control points
        controlPoints.forEachIndexed { index, controlPoint ->
            if (selectedIndex == index) {
                fill(255f, 255f, 255f)
                noStroke()
                val translation = waypoints[index].translation
                val angle = waypoints[index].rotation.degrees
                textSize(15f)
                var controlPointMsg = "index: $index\n" +
                        "pos: (${metersToFeet(translation.x).f}ft, ${metersToFeet(translation.y).f}ft)\n" +
                "θ: ${angle.f}°\n"
                if (index != controlPoints.size - 1) {
                    controlPointMsg += "dd0: (${intermediate[index].ddx0.f}, ${intermediate[index].ddy0.f})\n" +
                            "dd1: (${intermediate[index].ddx1.f}, ${intermediate[index].ddy1.f})\n"
                }
                drawText(controlPointMsg + msg)
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
        if (selectedIndex == -1) drawText(msg)
    }

    fun v2T(v: Double, t: Double, max: Double) =
            Translation2D(529 + (t / trajectoryTime) * 350, 400 - (v / max) * 100)

    fun List<Translation2D>.connect() = zipWithNext { a, b -> lineTo(a, b) }

    fun drawGraph(i: Int) {
        if (i == 0) return
        strokeWeight(2f)
        trajectory.subList(0, i + 1).apply {
            stroke(64f, 64f, 64f)
            map { v2T(it.acceleration, it.t, model.maxAcceleration) }.connect()
//            val states = map { it.t to model.solve(ChassisState(it.velocity, it.state.curvature * it.velocity)) }
//            stroke(128f, 0f, 128f)
//            states.map { v2T(it.second.left, it.first, maxVel) }.connect()
//            stroke(0f, 128f, 128f)
//            states.map { v2T(it.second.right, it.first, maxVel) }.connect()
            stroke(255f, 255f, 128f)
            map { v2T(it.state.curvature * it.velocity, it.t, maxAngular) }.connect()
            stroke(128f, 128f, 255f)
            map { v2T(it.velocity, it.t, maxVel) }.connect()
        }
    }

    fun redrawScreen() {
        redrawBackground()
        var t = splines[0].state.translation
        var normal = (splines[0].state.rotation.normal * wheelBaseRadius).translation
        var left = (t - normal).newXY
        var right = (t + normal).newXY

        strokeWeight(2f)

        // draw the curve
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
        if (!simulating) {
            redrawInfoNoSim()
            drawGraph(trajectory.size - 1)
        }
    }

    fun processDeselected() {
        when {
            key.toInt() == CODED -> when (keyCode) {
                PConstants.UP -> translateAll(Translation2D(step, 0.0))
                PConstants.DOWN -> translateAll(Translation2D(-step, 0.0))
                PConstants.LEFT -> translateAll(Translation2D(0.0, -step))
                PConstants.RIGHT -> translateAll(Translation2D(0.0, step))
            }
            key == 'r' -> {
                val newWaypoints = waypoints.reversedArray()
                for (i in 0 until newWaypoints.size) {
                    newWaypoints[i] = newWaypoints[i].run {
                        Pose2D(translation, rotation.rotate(reversedRotation))
                    }
                }
                waypoints = newWaypoints
                regenerate()
            }
            key == 'f' -> {
                for (i in 0 until waypoints.size) {
                    waypoints[i] = waypoints[i].run {
                        Pose2D(Translation2D(translation.x, -translation.y), Rotation2D(rotation.cos, -rotation.sin))
                    }
                }
                regenerate()
            }
        }
    }

    fun processSelected() {
        when {
            key.toInt() == CODED -> when (keyCode) {
                PConstants.UP -> translateSelected(Translation2D(step, 0.0))
                PConstants.DOWN -> translateSelected(Translation2D(-step, 0.0))
                PConstants.LEFT -> translateSelected(Translation2D(0.0, -step))
                PConstants.RIGHT -> translateSelected(Translation2D(0.0, step))
            }
            key == 'q' -> rotateSelected(nAng)
            key == 'w' -> rotateSelected(pAng)
            key == 'n' -> {
                val newWaypoints = arrayOfNulls<Pose2D>(waypoints.size + 1)
                for (i in 0..selectedIndex) newWaypoints[i] = waypoints[i]
                for (i in selectedIndex + 2..waypoints.size) newWaypoints[i] = waypoints[i - 1]
                val newPoint = waypoints[selectedIndex].run {
                    Pose2D(translation + rotation.norm.scaled(0.75).translation, rotation)
                }
                newWaypoints[selectedIndex + 1] = newPoint
                waypoints = newWaypoints.requireNoNulls()
                regenerate()
            }
            key == 'd' -> {
                if (waypoints.size <= 2) return
                val newWaypoints = arrayOfNulls<Pose2D>(waypoints.size - 1)
                for (i in 0 until selectedIndex) newWaypoints[i] = waypoints[i]
                for (i in selectedIndex + 1 until waypoints.size) newWaypoints[i - 1] = waypoints[i]
                waypoints = newWaypoints.requireNoNulls()
                regenerate()
            }
        }
    }

    fun translateSelected(by: Translation2D) {
        waypoints[selectedIndex] = waypoints[selectedIndex].run { Pose2D(translation + by, rotation) }
        regenerate()
    }

    fun rotateSelected(by: Rotation2D) {
        waypoints[selectedIndex] = waypoints[selectedIndex].run { Pose2D(translation, rotation + by) }
        regenerate()
    }

    fun translateAll(by: Translation2D) {
        for (i in 0 until waypoints.size) waypoints[i] = waypoints[i]
                .run { Pose2D(translation + by, rotation) }
        regenerate()
    }

    override fun mouseDragged() {
        if (simulating) return
        if (selectedIndex == -1) {
            mouseClicked()
            if (!selectionChanged) return
        }
        val mouse = Translation2D(mouseX.toDouble(), mouseY.toDouble())
        val controlPoint = controlPoints[selectedIndex]
        val waypoint = waypoints[selectedIndex]
        if ((controlPoint.pos - mouse).mag < 10 && !draggingAngle) draggingPoint = true
        if (draggingPoint) {
            redrawScreen()
            val heading = (mouse.oldXY + waypoint.rotation.translation.scaled(0.5)).newXY
            val dir = waypoint.rotation.norm.translation
            stroke(255f, 128f, 255f)
            strokeWeight(2f)
            draggedControlPoint = ControlPoint(mouse, heading, dir).apply { drawArrow() }
        }
        if ((controlPoint.heading - mouse).mag < 10 && !draggingPoint) draggingAngle = true
        if (draggingAngle) {
            redrawScreen()
            val dir = -(mouse - controlPoint.pos).oldXYNoOffset.norm
            val heading = controlPoint.pos + (mouse - controlPoint.pos).norm.scaled(0.5 * kPixelsPerMeter)
            stroke(255f, 128f, 255f)
            strokeWeight(2f)
            draggedControlPoint = ControlPoint(controlPoint.pos, heading, dir).apply { drawArrow() }
        }
    }

    override fun mousePressed() {
        if (simulating) return
        var found = false
        controlPoints.forEachIndexed { index, controlPoint ->
            val mouse = Translation2D(mouseX.toDouble(), mouseY.toDouble())
            val pc = (controlPoint.pos - mouse).mag
            val hc = (controlPoint.heading - mouse).mag
            if (pc < 12 || hc < 12) {
                selectionChanged = true
                found = true
                selectedIndex = index
            }
        }
        if (!found && selectedIndex != -1) {
            selectionChanged = true
            selectedIndex = -1
        }
    }

    override fun mouseReleased() {
        if (simulating) return
        if (draggingPoint && selectedIndex != -1) {
            draggingPoint = false
            draggedControlPoint?.also {
                waypoints[selectedIndex] = Pose2D(it.pos.oldXY, it.dir.direction)
                regenerate()
            }
        }
        if (draggingAngle && selectedIndex != -1) {
            draggingAngle = false
            redrawScreen()
            draggedControlPoint?.also {
                waypoints[selectedIndex] = Pose2D(it.pos.oldXY, it.dir.direction)
                regenerate()
            }
        }
    }

    override fun keyPressed() {
        if (key == ' ') {
            if (simulating) simulating = false else {
                simulating = true
                simIndex = 0
                simTime = System.currentTimeMillis() / 1000.0
                simFrames = 0
            }
            redrawScreen()
        } else if (key == 's') {
            showForCopy(waypoints.joinToString(",\n") {
                "waypoint(${metersToFeet(it.translation.x).f}, " +
                        "${metersToFeet(it.translation.y).f}, " +
                        "${it.rotation.degrees.f})"
            })
        }
        if (!simulating) {
            if (selectedIndex == -1) processDeselected()
            else processSelected()
        }
    }

    override fun draw() {
        if (simulating) {
            val nt = System.currentTimeMillis() / 1000.0
            val t = nt - simTime
            while (simIndex < trajectory.size - 2 && trajectory[simIndex].t < t) simIndex++
            if (t > trajectoryTime || simIndex >= trajectory.size) {
                simulating = false
                redrawScreen()
                return
            }
            val thisMoment = trajectory[simIndex]
            val nextMoment = trajectory[simIndex + 1]
            val tx = (t - thisMoment.t) / (nextMoment.t - thisMoment.t)
            val pos = thisMoment.state.state.translation.interpolate(nextMoment.state.state.translation, tx).newXY
            redrawScreen()
            fill(255f, 128f, 0f)
            noStroke()
            ellipse(pos.x.toFloat(), pos.y.toFloat(), 12f, 12f)
            val linear = nextMoment.velocity
            val angular = linear * nextMoment.state.curvature
            val wheels = model.solve(ChassisState(linear, angular))
            if (simFrames % 10 == 0) simMsg = "velocity: ${metersToFeet(linear).f}ft/s\n" +
                    "percent: ${(linear / model.maxVelocity * 100).f} %\n" +
                    "angular: ${Math.toDegrees(angular).f}deg/s\n" +
                    "wheelLeft: ${metersToFeet(wheels.left).f}ft/s\n" +
                    "wheelRight: ${metersToFeet(wheels.right).f}ft/s\n" +
                    "acceleration: ${metersToFeet(nextMoment.acceleration).f}ft/s^2\n" +
                    "curvature: ${nextMoment.state.curvature.f} \n" +
                    "dk_ds: ${nextMoment.state.dk_ds.f} \n" +
                    "elapsed: ${t.f}s\n"
            drawGraph(simIndex)
            drawText(simMsg)
            simFrames++

        } else if (selectionChanged) {
            redrawScreen()
            selectionChanged = false
        }
    }
}