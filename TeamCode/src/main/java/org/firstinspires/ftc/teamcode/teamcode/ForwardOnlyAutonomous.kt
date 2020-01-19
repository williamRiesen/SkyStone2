package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name = "Forward Only Autonomous (Blue)", group = "Holobot")

class ForwardOnlyAutonomous : LinearOpMode() {

    override fun runOpMode() {

        initialize(hardwareMap, Alliance.BLUE)

        waitForStart()
        val moveForward = AutonomousStep(0.0, 12.0, 0.5)
        robot.driveByEncoder(moveForward)
    }


}