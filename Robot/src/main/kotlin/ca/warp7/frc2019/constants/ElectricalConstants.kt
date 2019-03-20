package ca.warp7.frc2019.constants

internal object ElectricalConstants {
    const val kCompressorModule = 0
    const val kPDPModule = 0

    /*
    =====================================================
    The following motor controllers must have the same id
    as the the port they are plugged in to the PDP. Left/
    Right pairs should be made symmetrical on the PDP.
    =====================================================
     */

    const val kLiftMasterTalonId = 0
    const val kLiftFollowerVictorId = 15 // 11

    const val kDriveLeftMasterTalonId = 1
    const val kDriveRightMasterTalonId = 14

    const val kDriveLeftFollowerAVictorId = 2
    const val kDriveRightFollowerAVictorId = 13

    const val kDriveLeftFollowerBVictorId = 3
    const val kDriveRightFollowerBVictorId = 12

    const val kOuttakeLeftVictorId = 4
    const val kOuttakeRightVictorId = 11 // 15

    const val kConveyorLeftVictorId = 5
    const val kConveyorRightVictorId = 10

    const val kIntakeVictorId = 9

    /*
    ===============================================
    Solenoid ports[
    ===============================================
    */

    const val kIntakeSolenoidPort = 7
    const val kPusherSolenoid = 2
    const val kGrabberSolenoid = 1 


    /*
    ===============================================
    The following Digital I/O ports are plugged
    into the RIO
    ===============================================
     */

    const val kLiftHallEffectSensorDIO = 9
}