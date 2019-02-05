package ca.warp7.frc2019.constants

object ElectricalConstants {
    const val kCompressorModule = 0
    const val kPDPModule = 0

    /*
    =====================================================
    The following motor controllers must have the same id
    as the the port they are plugged in to the PDP. Left/
    Right pairs should be made symmetrical on the PDP.
    =====================================================
     */

    const val kLiftFollowerVictorId = 0
    const val kLiftMasterTalonId = 15

    const val kDriveLeftMasterTalonId = 1
    const val kDriveRightMasterTalonId = 14

    const val kDriveLeftFollowerAVictorId = 2
    const val kDriveRightFollowerAVictorId = 13

    const val kDriveLeftFollowerBVictorId = 3
    const val kDriveRightFollowerBVictorId = 12

    const val kConveyorLeftVictorId = 4
    const val kConveyorRightVictorId = 11

    const val kIntakeVictorId = 10
    const val kOuttakeLeftVictorId = 5

    // 6 is not connected to anything
    const val kOuttakeRightVictorId = 9

    /*
    ===============================================
    The following solenoids must have the same port
    as the the port they are plugged in to the PCM
    ===============================================
    */

    const val kIntakeSolenoidPort = 0 // TODO
    const val kOuttakeSolenoidPort = 1 // TODO


    /*
    ===============================================
    The following Digital I/O ports are plugged
    into the RIO
    ===============================================
     */

    const val kLiftHallEffectSensorDIO = 0 // TODO
}