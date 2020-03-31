package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry

const val MAX_POWER = 0.75
const val TICKS_PER_INCH = 128
const val ONE_OVER_SQRT2 = 0.70710678118
const val INCHES_PER_SEC_PER_POWER_UNIT = 20.5
const val clawRestPosition = 0.20
val STOP = DriveCommand(0.0, 0.0, 0.0)

class TurtleDozerAutoBot3(hardwareMap: HardwareMap) {

    val launcher: CRServo = hardwareMap.get(CRServo::class.java, "launcher")
    val kennethClawRight: Servo = hardwareMap.get(Servo::class.java, "kennethClawRight")
    val kennethClawLeft: Servo = hardwareMap.get(Servo::class.java, "kennethClawLeft")
    val blinkyLights: RevBlinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver::class.java, "blinkyLights")
    var xPosition = startLocation.x
    var yPosition = startLocation.y
    private val inertialMotionUnit: InertialMotionUnit = InertialMotionUnit(hardwareMap)
    private val tailHook: Servo? = hardwareMap.get(Servo::class.java, "tailhook")
    private val rightFrontDrive: DcMotor = hardwareMap.get(DcMotor::class.java, "rightFrontDrive")
    private val leftFrontDrive: DcMotor = hardwareMap.get(DcMotor::class.java, "leftFrontDrive")
    private val rightRearDrive: DcMotor = hardwareMap.get(DcMotor::class.java, "rightRearDrive")
    private val leftRearDrive: DcMotor = hardwareMap.get(DcMotor::class.java, "leftRearDrive")
    private val allMotors = listOf(rightFrontDrive, leftFrontDrive, rightRearDrive, leftRearDrive)
    private var xAccel = 0.0
    private var yAccel = 0.0
    private var driveCommand = DriveCommand(0.0, 0.0, 0.0)
    private val heading
        get() = inertialMotionUnit.getHeading().toDouble() + startHeading

    fun bumpDrive(autonomousStep: AutonomousStep) {
        for (motor in allMotors) {
            motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
        if (autonomousStep.length != 0.0) {
            val runDuration = autonomousStep.length / (autonomousStep.speed * INCHES_PER_SEC_PER_POWER_UNIT)
            val runTimer = ElapsedTime()
            xAccel = inertialMotionUnit.xAccel
            yAccel = inertialMotionUnit.yAccel
            driveCommand = DriveCommand(
                    xSpeed = autonomousStep.x * autonomousStep.speed / autonomousStep.length,
                    ySpeed = autonomousStep.y * autonomousStep.speed / autonomousStep.length,
                    rotationSpeed = 0.0)
            while (runTimer.seconds() < 0.5 || (runTimer.seconds() < runDuration && xAccel > -0.75)) {
                driveCommand.rotationSpeed = (autonomousStep.desiredHeading - robot.heading)
                setDriveMotion(driveCommand)
            }
        }
        setDriveMotion(STOP)
    }

    private fun updateAccelLights() {
        xAccel = inertialMotionUnit.xAccel
        yAccel = inertialMotionUnit.yAccel
        if (xAccel > 0.25) blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED)
        if (xAccel < -0.25) blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE)
        if (yAccel > 0.25) blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW)
        if (yAccel < -0.25) blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN)
        Thread.sleep(50)
        blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK)
    }

    private val motorsBusy: Boolean
        get() {
            var checkMotor = false
            for (motor in allMotors) {
                if (motor.isBusy) checkMotor = true
            }
            return checkMotor
        }

    fun driveByEncoder(autonomousStep: AutonomousStep) {

        val x = autonomousStep.x * TICKS_PER_INCH
        val y = autonomousStep.y * TICKS_PER_INCH
        val speed = autonomousStep.speed
        val length = autonomousStep.length * TICKS_PER_INCH
        val rightRear = (x + y) * ONE_OVER_SQRT2
        val leftFront = -rightRear
        val leftRear = (x - y) * ONE_OVER_SQRT2
        val rightFront = -leftRear
        val timer = ElapsedTime()

        rightFrontDrive.targetPosition = rightFront.toInt()
        leftFrontDrive.targetPosition = leftFront.toInt()
        rightRearDrive.targetPosition = rightRear.toInt()
        leftRearDrive.targetPosition = leftRear.toInt()

        for (motor in allMotors) {
            motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            motor.mode = DcMotor.RunMode.RUN_TO_POSITION
        }

        timer.reset()

        while (motorsBusy) {
            rightFrontDrive.power = speed * MAX_POWER * rightFront / length
            leftFrontDrive.power = speed * MAX_POWER * leftFront / length
            rightRearDrive.power = speed * MAX_POWER * rightRear / length
            leftRearDrive.power = speed * MAX_POWER * leftRear / length
        }
        for (motor in allMotors) {
            motor.power = 0.0
        }
    }

    private fun setDriveMotion(command: DriveCommand) {
        driveCommand = command
        for (motor in allMotors) {
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
        val xSpeedScaled = driveCommand.xSpeed * ONE_OVER_SQRT2
        val ySpeedScaled = driveCommand.ySpeed * ONE_OVER_SQRT2

        rightFrontDrive.power = -xSpeedScaled + ySpeedScaled + driveCommand.rotationSpeed
        leftFrontDrive.power = -xSpeedScaled - ySpeedScaled + driveCommand.rotationSpeed
        rightRearDrive.power = xSpeedScaled + ySpeedScaled + driveCommand.rotationSpeed
        leftRearDrive.power = xSpeedScaled - ySpeedScaled + driveCommand.rotationSpeed

    }

    fun deployHook() {
        if (tailHook != null) {
            tailHook.position = 0.0
        }
    }

    fun unlatchHook() {
        if (tailHook != null) {
            tailHook.position = 0.5
        }
    }

    fun drive(autonomousStep: AutonomousStep,telemetry: Telemetry) {
        for (motor in allMotors) {
            motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
        if (autonomousStep.length != 0.0) {
            val runDuration = autonomousStep.length / (autonomousStep.speed * INCHES_PER_SEC_PER_POWER_UNIT)
            val runTimer = ElapsedTime()
            driveCommand = DriveCommand(
                    xSpeed = autonomousStep.x * autonomousStep.speed / autonomousStep.length,
                    ySpeed = autonomousStep.y * autonomousStep.speed / autonomousStep.length,
                    rotationSpeed = 0.0)

            while (runTimer.seconds() < runDuration) {
                driveCommand.rotationSpeed = (autonomousStep.desiredHeading - robot.heading)
                telemetry.addData("Desired Heading",autonomousStep.desiredHeading)
                telemetry.addData("Heading", robot.heading)
                telemetry.update()
                setDriveMotion(driveCommand)
                updateAccelLights()

            }
        }

        setDriveMotion(STOP)
    }
}
