package ca.warp7.frc2019.test.lift

import ca.warp7.frc.config
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive.leftMaster
import ca.warp7.frc2019.subsystems.Drive.rightMaster
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Lift.master
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LiftPID : TimedRobot() {

    private val master = Lift.master
    private val target = -25000.0

    private val tab = Shuffleboard.getTab("Lift PID")

    val p: NetworkTableEntry = tab.add("P", 0).withWidget(BuiltInWidgets.kNumberSlider).entry
    val i: NetworkTableEntry = tab.add("I", 0).withWidget(BuiltInWidgets.kNumberSlider).entry
    val d: NetworkTableEntry = tab.add("D", 0).withWidget(BuiltInWidgets.kNumberSlider).entry

    var lastP = 0.0
    var lastI = 0.0
    var lastD = 0.0

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
        Lift.onPostUpdate()
    }

    override fun autonomousInit() {
        master.selectedSensorPosition = 0
    }

    override fun autonomousPeriodic() {
        master.set(ControlMode.Position, target)
    }

    override fun disabledInit() {
        master.neutralOutput()
    }
}