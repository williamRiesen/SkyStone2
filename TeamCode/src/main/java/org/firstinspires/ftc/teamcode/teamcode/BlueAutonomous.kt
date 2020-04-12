package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.lang.Math.PI

@Autonomous(name = "Blue Autonomous", group = "Holobot")
@Disabled
class BlueAutonomous : LinearOpMode() {

    override fun runOpMode() {
        startHeading = PI/2.0
        initialize(hardwareMap, Alliance.BLUE)
        checkList(telemetry,gamepad1,gamepad2, RevBlinkinLedDriver.BlinkinPattern.BLUE)
        waitForStart()
        go(telemetry)
    }


}