package ca.warp7.pathplanner

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.f
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.*
import ca.warp7.frc.interpolate
import ca.warp7.frc.kMetersToFeet
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.parameterized
import ca.warp7.frc.path.quinticSplinesOf
import ca.warp7.frc.path.sumDCurvature2
import ca.warp7.frc.trajectory.TrajectoryPoint
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

    override fun settings() {
        size(1024, 540)
        noSmooth()
    }

    val kTriangleRatio = 1 / sqrt(3.0)
    val step = 0.0254 * 12 / 10
    val pAng = Rotation2D.fromDegrees(1.0)
    val nAng = Rotation2D.fromDegrees(-1.0)
    val reversedRotation = Rotation2D(-1.0, 0.0)

    val wheelBaseRadius = kInchesToMeters * 12.4
    val robotLength = wheelBaseRadius * 1.3
    val robotDrawCenter = Translation2D(768.0, 100.0)

    val kPixelsPerMeter = 494 / 8.2296
    val yCenterPx = 17.0 + (512.0 - 17.0) / 2
    val Double.my2x: Double get() = (yCenterPx - kPixelsPerMeter * this)
    val Double.mx2y: Double get() = (493.0 - kPixelsPerMeter * this)
    val Double.px2y: Double get() = (yCenterPx - this) / kPixelsPerMeter
    val Double.py2x: Double get() = (493.0 - this) / kPixelsPerMeter
    val Translation2D.newXY get() = Translation2D(y.my2x, x.mx2y)
    val Translation2D.newXYNoOffset get() = Translation2D(-kPixelsPerMeter * y, -kPixelsPerMeter * x)
    val Translation2D.oldXY get() = Translation2D(y.py2x, x.px2y)
    val Translation2D.oldXYNoOffset get() = Translation2D(-y / kPixelsPerMeter, -x / kPixelsPerMeter)

    var waypoints: Array<Pose2D> = emptyArray()
    var intermediate: List<QuinticSegment2D> = emptyList()
    var splines: List<CurvatureState<Pose2D>> = emptyList()
    var trajectory: List<TrajectoryPoint> = emptyList()
    var controlPoints: MutableList<ControlPoint> = mutableListOf()
    var dynamics: List<Triple<WheelState, DynamicState, Double>> = emptyList()

    var maxVRatio = 1.0
    var maxARatio = 1.0
    var optimizing = false

    var curvatureSum = 0.0
    var arcLength = 0.0
    var trajectoryTime = 0.0
    var maxK = 0.0
    var maxAngular = 0.0
    var maxAngularAcc = 0.0

    var selectedIndex = -1
    var selectionChanged = false

    fun lineTo(a: Translation2D, b: Translation2D) = line(a.x.toFloat(), a.y.toFloat(), b.x.toFloat(), b.y.toFloat())
    fun vertex(a: Translation2D) = vertex(a.x.toFloat(), a.y.toFloat())
    val bg = PImage(ImageIO.read(PathPlanner::class.java.getResource("/field.PNG")))

    val model = DifferentialDriveModel(
            wheelRadius = kWheelRadius,
            wheelbaseRadius = kEffectiveWheelBaseRadius,
            maxVelocity = kMaxVelocity,
            maxAcceleration = kMaxAcceleration,
            maxFreeSpeed = kMaxFreeSpeed,
            speedPerVolt = kSpeedPerVolt,
            torquePerVolt = kTorquePerVolt,
            frictionVoltage = kFrictionVoltage,
            linearInertia = kLinearInertia,
            angularInertia = kAngularInertia,
            maxVoltage = kMaxVolts,
            angularDrag = kAngularDrag
    )

    var draggingPoint = false
    var draggingAngle = false
    var draggedControlPoint: ControlPoint? = null

    var simulating = false
    var simPaused = false
    var simElapsed = 0.0
    var simIndex = 0
    var simElapsedChanged = false

    var lastTime = 0.0
    var dt = 0.0

    fun drawArrow(point: ControlPoint): ControlPoint {
        val r1 = point.dir.scaled(0.1524 * kTriangleRatio * 2)
        val r2 = r1.rotate(Rotation2D(0.0, 1.0)).scaled(kTriangleRatio)
        val r3 = r1.rotate(Rotation2D(0.0, -1.0)).scaled(kTriangleRatio)
        ellipse(point.pos.x.toFloat(), point.pos.y.toFloat(), 12f, 12f)
        val start = point.pos - point.dir.scaled(6.0).transposed
        lineTo(start, point.heading)
        val a1 = point.heading + r1.newXYNoOffset
        val a2 = point.heading + r2.newXYNoOffset
        val a3 = point.heading + r3.newXYNoOffset
        beginShape()
        vertex(a1)
        vertex(a2)
        vertex(a3)
        vertex(a1)
        endShape()
        return point
    }

    fun drawText(t: String) {
        fill(192f, 192f, 192f)
        noStroke()
        textSize(18f)
        text(t, 17f, 525f)
    }

    fun drawRobot(pos: Translation2D, heading: Rotation2D) {
        val a = Translation2D(robotLength, wheelBaseRadius).rotate(heading).newXYNoOffset
        val b = Translation2D(robotLength, -wheelBaseRadius).rotate(heading).newXYNoOffset
        val p1 = pos + a
        val p2 = pos + b
        val p3 = pos - a
        val p4 = pos - b
        stroke(60f, 92f, 148f)
        fill(90f, 138f, 222f)
        beginShape()
        vertex(p1)
        vertex(p2)
        vertex(p3)
        vertex(p4)
        vertex(p1)
        endShape()
    }

    override fun setup() {
        surface.setIcon(PImage(ImageIO.read(this::class.java.getResource("/icon.png"))))
        waypoints = arrayOf(
                Pose2D(6.feet, 4.feet, 0.degrees),
                Pose2D(16.8.feet, 11.2.feet, 32.degrees)
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
        intermediate = quinticSplinesOf(*waypoints, optimizePath = optimizing)
        curvatureSum = intermediate.sumDCurvature2()
        splines = intermediate.parameterized()
        arcLength = splines.zipWithNext { a: CurvatureState<Pose2D>, b: CurvatureState<Pose2D> ->
            (b.state.translation - a.state.translation).mag
        }.sum()
        trajectory = splines.timedTrajectory(model, 0.0, 0.0,
                model.maxVelocity * maxVRatio, model.maxAcceleration * maxARatio)
        trajectoryTime = trajectory.last().t
        dynamics = trajectory.map {
            val velocity = ChassisState(it.velocity, it.velocity * it.state.curvature)
            val acceleration = ChassisState(it.acceleration, it.acceleration * it.state.curvature)
            val wv = model.solve(velocity) * (217.5025513493939 / 1023 * 12)
            val wa = model.solve(acceleration) * (6.0 / 1023 * 12)
            Triple(WheelState(wv.left + wa.left, wv.right + wa.right),
                    model.solve(velocity, acceleration), it.t)
        }
        maxK = splines.maxBy { it.curvature.absoluteValue }?.curvature?.absoluteValue ?: 1.0
        maxAngular = trajectory.map { Math.abs(it.velocity * it.state.curvature) }.max() ?: 1.0
        maxAngularAcc = trajectory.map { Math.abs(it.acceleration * it.state.curvature) }.max() ?: 1.0
        redrawScreen()
    }

    fun redrawBackground() {
        background(0f, 0f, 0f)
        image(bg, 17f, 0f)
        noFill()
        strokeWeight(1f)
        stroke(192f, 192f, 192f)
        line(17f, 0f, 17f, 492f)
        strokeWeight(2f)
        line(512f, 0f, 512f, 492f)
        line(17f, 492f, 512f, 492f)
        strokeWeight(1f)
        stroke(192f, 192f, 192f)
        rect(529f, 17f, 235f, 208f)
        line(529f, 121f, 764f, 121f)
        rect(772f, 17f, 235f, 208f)
        line(772f, 121f, 1007f, 121f)
        rect(529f, 233f, 478f, 259f)
    }

    fun redrawControlPoints() {
        controlPoints.forEachIndexed { index, controlPoint ->
            if (selectedIndex == index) {
                stroke(90f, 138f, 222f)
                strokeWeight(2f)
                noFill()
            } else {
                stroke(255f, 255f, 0f)
                strokeWeight(2f)
                noFill()
            }
            drawArrow(controlPoint)
        }
    }

    fun v2T(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(531 + (t / trajectoryTime) * 474, y - (v / max) * 50)

    fun h2TL(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(531 + (t / trajectoryTime) * 231, y - (v / max) * 104)

    fun h2TR(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(774 + (t / trajectoryTime) * 231, y - (v / max) * 104)

    fun List<Translation2D>.connect() {
        beginShape()
        forEach { vertex(it) }
        endShape()
    }

    fun drawGraph(i: Int) {
        if (i == 0) return
        trajectory.subList(0, i + 1).apply {
            strokeWeight(1f)
            stroke(255f, 128f, 0f)
            forEachIndexed { index, point ->
                val x = (531 + (point.t / trajectoryTime) * 474).toFloat()
                if (index % 2 == 0) line(x, 352.5f, x, 367.5f)
                else line(x, 357.5f, x, 372.5f)
            }
            strokeWeight(2f)
            stroke(0f, 128f, 192f)
            map { v2T(it.acceleration, it.t, model.maxAcceleration, 434) }.connect()
            stroke(0f, 192f, 128f)
            map { v2T((it.state.curvature * it.acceleration), it.t, maxAngularAcc, 434) }.connect()
            stroke(255f, 255f, 128f)
            map { v2T(it.state.curvature * it.velocity, it.t, maxAngular, 290) }.connect()
            stroke(128f, 128f, 255f)
            map { v2T(it.velocity, it.t, model.maxVelocity, 290) }.connect()
        }
        dynamics.subList(0, i + 1).apply {
            stroke(255f, 255f, 128f)
            strokeWeight(2f)
            map { h2TL(it.first.left, it.third, 12.0, 121) }.connect()
            map { h2TR(it.first.right, it.third, 12.0, 121) }.connect()
            stroke(128f, 255f, 255f)
            map { h2TL(it.second.voltage.left, it.third, 12.0, 121) }.connect()
            map { h2TR(it.second.voltage.right, it.third, 12.0, 121) }.connect()
        }
    }

    fun redrawScreen() {
        redrawBackground()

        // draw the start of the curve
        val s0 = splines.first()
        val t0 = s0.state.translation
        var normal = (s0.state.rotation.normal * wheelBaseRadius).translation
        var left = (t0 - normal).newXY
        var right = (t0 + normal).newXY

        strokeWeight(2f)
        stroke(0f, 255f, 0f)
        val a0 = t0.newXY - Translation2D(robotLength, wheelBaseRadius).rotate(s0.state.rotation).newXYNoOffset
        val b0 = t0.newXY + Translation2D(-robotLength, wheelBaseRadius).rotate(s0.state.rotation).newXYNoOffset
        lineTo(a0, b0)
        lineTo(left, a0)
        lineTo(right, b0)

        // draw the curve
        for (i in 1 until splines.size) {
            val s = splines[i]
            val t = s.state.translation
            normal = (s.state.rotation.normal * wheelBaseRadius).translation
            val newLeft = (t - normal).newXY
            val newRight = (t + normal).newXY
            val kx = s.curvature.absoluteValue / maxK
            val r = interpolate(0.0, 192.0, kx).toFloat() + 64
            val g = 255 - interpolate(0.0, 192.0, kx).toFloat()
            stroke(r, g, 0f)
            lineTo(left, newLeft)
            lineTo(right, newRight)
            left = newLeft
            right = newRight
        }

        // draw the end of the curve

        val sf = splines.last()
        stroke(0f, 255f, 0f)
        val tf = sf.state.translation.newXY
        val af = tf - Translation2D(-robotLength, wheelBaseRadius).rotate(sf.state.rotation).newXYNoOffset
        val bf = tf + Translation2D(robotLength, wheelBaseRadius).rotate(sf.state.rotation).newXYNoOffset
        lineTo(af, bf)
        lineTo(left, af)
        lineTo(right, bf)
        val msg = "K=${maxK.f}  " +
                "ΣΔk2=${curvatureSum.f}  " +
                "ΣΔd=${(kMetersToFeet * arcLength).f}ft  " +
                "ΣΔt=${trajectory.last().t.f}s  " +
                "V=${(maxVRatio * 100).toInt()}%  " +
                "a=${(maxARatio * 100).toInt()}%  " +
                "O=$optimizing  "
        drawText(msg)

        if (!simulating) {
            redrawControlPoints()
            drawGraph(trajectory.size - 1)
        }
    }

    fun processConstructing() {
        when (key) {
            '_' -> {
                maxVRatio = (maxVRatio - 0.1).coerceIn(0.3, 1.0)
                regenerate()
            }
            '+' -> {
                maxVRatio = (maxVRatio + 0.1).coerceIn(0.3, 1.0)
                regenerate()
            }
            '{' -> {
                maxARatio = (maxARatio - 0.1).coerceIn(0.3, 1.0)
                regenerate()
            }
            '}' -> {
                maxARatio = (maxARatio + 0.1).coerceIn(0.3, 1.0)
                regenerate()
            }
            'r' -> {
                val newWaypoints = waypoints.reversedArray()
                for (i in 0 until newWaypoints.size) {
                    newWaypoints[i] = newWaypoints[i].run {
                        Pose2D(translation, rotation.rotate(reversedRotation))
                    }
                }
                waypoints = newWaypoints
                if (selectedIndex != -1) {
                    selectedIndex = waypoints.size - 1 - selectedIndex
                }
                regenerate()
            }
            'f' -> {
                for (i in 0 until waypoints.size) {
                    waypoints[i] = waypoints[i].run {
                        Pose2D(Translation2D(translation.x, -translation.y), Rotation2D(rotation.cos, -rotation.sin))
                    }
                }
                regenerate()
            }
            'o' -> {
                optimizing = !optimizing
                regenerate()
            }
        }
        if (selectedIndex == -1) processDeselected()
        else processSelected()
    }

    fun processDeselected() {
        if (key.toInt() == CODED) when (keyCode) {
            PConstants.UP -> translateAll(Translation2D(step, 0.0))
            PConstants.DOWN -> translateAll(Translation2D(-step, 0.0))
            PConstants.LEFT -> translateAll(Translation2D(0.0, step))
            PConstants.RIGHT -> translateAll(Translation2D(0.0, -step))
        }
    }

    fun processSelected() {
        when {
            key.toInt() == CODED -> when (keyCode) {
                PConstants.UP -> translateSelected(Translation2D(step, 0.0))
                PConstants.DOWN -> translateSelected(Translation2D(-step, 0.0))
                PConstants.LEFT -> translateSelected(Translation2D(0.0, step))
                PConstants.RIGHT -> translateSelected(Translation2D(0.0, -step))
            }
            key == 'q' -> rotateSelected(pAng)
            key == 'w' -> rotateSelected(nAng)
            key == 'n' -> {
                val newWaypoints = arrayOfNulls<Pose2D>(waypoints.size + 1)
                for (i in 0..selectedIndex) newWaypoints[i] = waypoints[i]
                for (i in selectedIndex + 2..waypoints.size) newWaypoints[i] = waypoints[i - 1]
                val newPoint = waypoints[selectedIndex].run {
                    Pose2D(translation + rotation.norm.scaled(0.75).translation, rotation)
                }
                selectedIndex++
                newWaypoints[selectedIndex] = newPoint
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

    fun processSimulatePaused() {
        when (key) {
            '-' -> {
                simElapsed = (simElapsed - 0.2).coerceAtLeast(0.0)
                simIndex = 0
                simElapsedChanged = true
            }
            '=' -> {
                simElapsed = (simElapsed + 0.2).coerceAtMost(trajectoryTime)
                simElapsedChanged = true
            }
            '[' -> {
                simElapsed = (simElapsed - 0.02).coerceAtLeast(0.0)
                simIndex = 0
                simElapsedChanged = true
            }
            ']' -> {
                simElapsed = (simElapsed + 0.02).coerceAtMost(trajectoryTime)
                simElapsedChanged = true
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
            draggedControlPoint = drawArrow(ControlPoint(mouse, heading, dir))
        }
        if ((controlPoint.heading - mouse).mag < 10 && !draggingPoint) draggingAngle = true
        if (draggingAngle) {
            redrawScreen()
            val dir = (mouse - controlPoint.pos).oldXYNoOffset.norm
            val heading = controlPoint.pos + (mouse - controlPoint.pos).norm.scaled(0.5 * kPixelsPerMeter)
            stroke(255f, 128f, 255f)
            strokeWeight(2f)
            draggedControlPoint = drawArrow(ControlPoint(controlPoint.pos, heading, dir))
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
        when (key) {
            ' ' -> if (simulating) {
                simPaused = !simPaused
            } else {
                simulating = true
                simIndex = 0
                simElapsed = 0.0
                simPaused = false
                lastTime = System.currentTimeMillis() / 1000.0
                redrawScreen()
            }
            's' -> showForCopy(waypoints.joinToString(",\n") {
                "Pose2D(${(kMetersToFeet * it.translation.x).f}.feet, " +
                        "${(kMetersToFeet * it.translation.y).f}.feet, " +
                        "${it.rotation.degrees.f}.degrees)"
            })
            '0' -> {
                simulating = false
                simPaused = false
                simIndex = 0
                simElapsed = 0.0
                redrawScreen()
            }
        }
        if (simulating) {
            if (simPaused) processSimulatePaused()
        } else processConstructing()
    }

    override fun draw() {
        if (simulating) {
            val nt = System.currentTimeMillis() / 1000.0
            dt = nt - lastTime
            lastTime = nt
            if (simPaused) {
                if (!simElapsedChanged) return
                simElapsedChanged = false
            } else simElapsed += dt
            val t = simElapsed
            while (simIndex < trajectory.size - 2 && trajectory[simIndex + 1].t < t) simIndex++
            if (t > trajectoryTime || simIndex >= trajectory.size) {
                simulating = false
                simPaused = false
                redrawScreen()
                return
            }
            val thisMoment = trajectory[simIndex]
            val nextMoment = trajectory[simIndex + 1]
            val tx = (t - thisMoment.t) / (nextMoment.t - thisMoment.t)
            val pos = thisMoment.state.state.translation.interpolate(nextMoment.state.state.translation, tx).newXY
            redrawScreen()
            val heading = thisMoment.state.state.rotation.interpolate(nextMoment.state.state.rotation, tx)
            drawRobot(pos, heading)
            stroke(255f, 255f, 255f)
            noFill()
            val headingXY = pos + heading.translation.scaled(0.5).newXYNoOffset
            val dir = heading.norm.translation
            drawArrow(ControlPoint(pos, headingXY, dir))
            drawGraph(simIndex)
            stroke(255f, 0f, 0f)
            val x1 = (531 + (t / trajectoryTime) * 474).toFloat()
            line(x1, 233f, x1, 492f)
            val x2 = (531 + (t / trajectoryTime) * 231).toFloat()
            line(x2, 17f, x2, 225f)
            val x3 = (774 + (t / trajectoryTime) * 231).toFloat()
            line(x3, 17f, x3, 225f)
        } else if (selectionChanged) {
            redrawScreen()
            selectionChanged = false
        }
    }
}