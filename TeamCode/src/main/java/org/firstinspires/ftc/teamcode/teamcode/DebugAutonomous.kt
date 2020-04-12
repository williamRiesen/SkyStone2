package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.lang.Math.PI

@Autonomous(name = "Debug Autonomous", group = "Holobot")
@Disabled
class DebugAutonomous : LinearOpMode() {

    override fun runOpMode() {
        startHeading = 0.0
        initialize(hardwareMap, Alliance.BLUE)
//        checkList(telemetry,gamepad1,gamepad2, RevBlinkinLedDriver.BlinkinPattern.BLUE)
        waitForStart()
        robot.drive(AutonomousStep(0.0,12.0,0.5,PI/4.0),telemetry)
    }
}