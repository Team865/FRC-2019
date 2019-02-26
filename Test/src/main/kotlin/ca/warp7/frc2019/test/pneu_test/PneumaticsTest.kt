package ca.warp7.frc2019.test.pneu_test

import ca.warp7.frc.ControllerState
import ca.warp7.frc2019.constants.HatchConstants
import ca.warp7.frc2019.constants.InfrastructureConstants
import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

class PneumaticsTest : TimedRobot() {
    private val compressor = Compressor(InfrastructureConstants.kCompressorModule)
    private val grabber = Solenoid(HatchConstants.kGrabberSolenoid)
    private val pusher = Solenoid(HatchConstants.kPusherSolenoid)
    lateinit var controller: XboxController

    override fun robotInit() {controller= XboxController(0)}

    override fun disabledInit() {
        compressor.stop()
    }

    override fun teleopPeriodic() {
        when {!compressor.enabled() -> compressor.start()}
        grabber.set(controller.aButton)
        pusher.set(controller.bButton)
    }
}