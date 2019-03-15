package ca.warp7.frc2019.test.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import kotlin.math.absoluteValue

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LiftPID : TimedRobot() {

    private val master = Lift.master

    private val tab = Shuffleboard.getTab("Lift PID")

    private val xboxController = XboxController(0)

    val p: NetworkTableEntry = tab.add("P", 0).withWidget(BuiltInWidgets.kTextView).entry
    val i: NetworkTableEntry = tab.add("I", 0).withWidget(BuiltInWidgets.kTextView).entry
    val d: NetworkTableEntry = tab.add("D", 0).withWidget(BuiltInWidgets.kTextView).entry
    val t: NetworkTableEntry = tab.add("Target", 0.5).withWidget(BuiltInWidgets.kTextView).entry
    val pos: NetworkTableEntry = tab.add("pos", 0.5).withWidget(BuiltInWidgets.kGraph).entry

    var lastP = 0.0
    var lastI = 0.0
    var lastD = 0.0
    var lastTime = Timer.getFPGATimestamp()

    override fun robotPeriodic() {
        val newP = p.getDouble(0.0)
        if (!newP.epsilonEquals(lastP, 1E-9)) {
            lastP = newP
            master.config_kP(0, newP, 0)
        }
        val newI = i.getDouble(0.0)
        if (!newI.epsilonEquals(lastI, 1E-9)) {
            lastI = newI
            master.config_kI(0, newI, 0)
        }
        val newD = d.getDouble(0.0)
        if (!newD.epsilonEquals(lastD, 1E-9)) {
            lastD = newD
            master.config_kD(0, newD, 0)
        }
        val nt = Timer.getFPGATimestamp()
        Lift.onMeasure(nt - lastTime)
        lastTime = nt
        Lift.onPostUpdate()
    }

    override fun teleopInit() {
        master.selectedSensorPosition = 0
    }

    val maxHeight = 42726.0
    override fun teleopPeriodic() {
        if (xboxController.getBumper(GenericHID.Hand.kLeft)) {
            master.selectedSensorPosition = 0
        }
        when {
            xboxController.xButton -> {
                val setpoint = t.getDouble(0.0)
                val scaledLiftLocation = Lift.actualPositionTicks.absoluteValue / maxHeight
                if (scaledLiftLocation > 0.1) master.set(ControlMode.Position, setpoint * -maxHeight)
                else if (!Lift.hallEffectTriggered) when {
                    setpoint <= 0.1 -> master.set(ControlMode.PercentOutput, 0.06)
                    else -> master.set(ControlMode.Position, setpoint * -maxHeight)
                }
                else master.selectedSensorPosition = 0
                pos.setDouble(scaledLiftLocation)
            }
            else -> {
                val speed = xboxController.getY(GenericHID.Hand.kLeft)
                when {
                    speed.absoluteValue > 0.2 -> master.set(ControlMode.PercentOutput, (speed - 0.2) / 0.8)
                    else -> master.set(ControlMode.PercentOutput, -0.12)
                }
            }
        }
    }

    override fun disabledInit() {
        master.neutralOutput()
    }
}