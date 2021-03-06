package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.PI

const val ROTATION_SPEED_ADJUST = 0.5

@TeleOp(name = "TeleOp", group = "TurtleDozer.")
@Disabled
class TeleOp : OpMode() {
    private lateinit var robot: TurtleDozerTeleBot
    private val timer = ElapsedTime()
    private var timeStamp = timer.milliseconds()
    private var allianceIsBlue = false
    private var elevatorPosition = 0.0
    private var elevatorSpeed = 0.0
    private var elevatorIsCalibrated = false
    private var elevatorIsAtTopLimit = false
    private val elevatorTopLimit = 8800.0
    private val clawReductionFactor = 0.5
    private val startHeading = (PI / 2.0).toFloat()
    private val waggleInPosition = clawRestPosition
    private val waggleOutPosition = clawRestPosition + 0.4

    override fun init() {
        robot = TurtleDozerTeleBot(hardwareMap)
        timer.reset()
        when (alliance) {
            Alliance.BLUE -> robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE)
            Alliance.RED -> robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED)
        }
    }

//    override fun init_loop() {
//        if (gamepad2.b) {
//            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED)
//            allianceIsBlue = false
//        }
//        if (gamepad2.x) {
//            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE)
//            allianceIsBlue = true
//        }
//        if (gamepad2.y) {
//            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW)
//            allianceIsBlue = false
//        }
//        if (gamepad2.a) {
//            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN)
//            allianceIsBlue = false
//        }
//    }

    private fun updateElevatorPosition() {
        val timePassed = timer.milliseconds() - timeStamp
        timeStamp = timer.milliseconds()
        val distanceTravelled = timePassed * elevatorSpeed
        elevatorPosition += distanceTravelled
        elevatorIsAtTopLimit = elevatorPosition > elevatorTopLimit
    }

    private fun waggleRight() {
        robot.kennethClawRight.position = waggleOutPosition
        robot.kennethClawLeft.position = waggleInPosition
    }

    private fun waggleLeft() {
        robot.kennethClawRight.position = waggleInPosition
        robot.kennethClawLeft.position = waggleOutPosition
    }

    override fun loop() {
        updateElevatorPosition()
        val gamepadInput = -gamepad2.left_stick_y.toDouble()
        if (robot.elevatorIsAtBottomLimit) {
            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE)
            elevatorPosition = 0.0
            elevatorIsCalibrated = true
            elevatorSpeed = gamepadInput.coerceAtLeast(0.0)
        } else if (!elevatorIsCalibrated) {
            elevatorSpeed = gamepadInput.coerceAtMost(0.0)
            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW)
        } else if (elevatorIsAtTopLimit) {
            elevatorSpeed = gamepadInput.coerceAtMost(0.0)
            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE)
        } else {
            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN)
            elevatorSpeed = gamepadInput
        }
        robot.kennethElevator.power = elevatorSpeed

        if (gamepad2.dpad_down) {
            robot.deployHook()
        }

        if (gamepad2.dpad_up) {
            robot.unlatchHook()
        }

        if (gamepad2.x) {
            robot.launcher.power = 0.2
        }
        if (gamepad2.b) {
            robot.launcher.power = -0.2
        }

        if (!gamepad2.x  && !gamepad2.b){
            robot.launcher.power = 0.0
        }







//        if (gamepad2.b) {
//            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED)
//            allianceIsBlue = false
//        }
//        if (gamepad2.x) {
//            robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE)
//            allianceIsBlue = true
//        }
        if (timer.seconds() > 90) {
            when (alliance) {
                Alliance.BLUE -> robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_BLUE)
                Alliance.RED -> robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_RED)
            }
        }

        when {
            gamepad2.right_bumper -> {
                waggleRight()
                robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET)
            }
            gamepad2.left_bumper -> waggleLeft()
            else -> {
                val clawPosition = clawRestPosition + gamepad2.right_trigger.toDouble() * clawReductionFactor
                robot.kennethClawRight.position = clawPosition
                robot.kennethClawLeft.position = clawPosition
            }
        }

        val rotation = gamepad1.left_stick_x.toDouble()
        val xScooch = gamepad1.right_stick_x.toDouble()
        val yScooch = -gamepad1.right_stick_y.toDouble()


        val driveCommand = DriveCommand(xScooch, yScooch, rotation * ROTATION_SPEED_ADJUST)

        telemetry.addData("heading", robot.heading)
        telemetry.update()
        driveCommand.rotate(robot.heading)


        robot.setDriveMotion(driveCommand)


    }
}

