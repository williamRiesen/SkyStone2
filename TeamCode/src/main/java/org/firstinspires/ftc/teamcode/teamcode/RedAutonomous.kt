package org.firstinspires.ftc.teamcode


import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.teamcode.*
import java.lang.Math.PI

@Autonomous(name = "Red Autonomous", group = "Holobot")

class RedAutonomous : LinearOpMode() {

    override fun runOpMode() {


        initialize(hardwareMap, Alliance.RED)
        checkList(telemetry,gamepad1,gamepad2, RevBlinkinLedDriver.BlinkinPattern.RED)
        waitForStart()
        go(telemetry)
    }
}