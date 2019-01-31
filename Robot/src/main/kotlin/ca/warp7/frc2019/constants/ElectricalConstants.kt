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
    // 5 is not connected to anything

    const val kOuttakeLeftVictorId = 6
    const val kOuttakeRightVictorId = 9

    /*
    ===============================================
    The following solenoids must have the same id
    as the the port they are plugged in to the PCM
    ===============================================
    */

    const val kIntakeSolenoidId = 0
    const val kOuttakeSolenoidId = 1
}