package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.PI

lateinit var robot: TurtleDozerAutoBot3
var startLocation = convertTilesToInches(0.0, 0.0)
var startHeading = PI / 2.0
var alignWithFoundation = convertTilesToInches(0.5, -1.0)
var bumpSideWall = convertTilesToInches(2.0, 0.0)
var reboundToAlignWithFoundation = AutonomousStep(-16.0, 0.0, 0.5)
var backUpToLatchFoundation = convertTilesToInches(0.0, -0.35, 0.1)
var dragFoundation = convertTilesToInches(0.0, 2.0)
var shiftTowardAudience = convertTilesToInches(-1.25, 0.0)
var backUpToGoAroundFoundation = convertTilesToInches(1.9, 0.0, 0.75, 0.0)
var advanceAwayFromAudience = convertTilesToInches(0.0, 1.3, 0.75, 0.0)
var pushFoundationHome = convertTilesToInches(-3.0, 0.0,0.75, 0.0)
var parkUnderBridge = convertTilesToInches(0.0, -2.0, 0.75, 0.0)
val steps = listOf(
        startLocation,
        alignWithFoundation,
        bumpSideWall,
        reboundToAlignWithFoundation,
        backUpToLatchFoundation,
        dragFoundation,
        shiftTowardAudience,
        backUpToGoAroundFoundation,
        advanceAwayFromAudience,
        pushFoundationHome,
        parkUnderBridge)


fun initialize(hardwareMap: HardwareMap, lightPattern: RevBlinkinLedDriver.BlinkinPattern) {
    robot = TurtleDozerAutoBot3(hardwareMap)
    with(robot) {
        xPosition = startLocation.x
        yPosition = startLocation.y
        kennethClawLeft.direction = Servo.Direction.REVERSE
        blinkyLights.setPattern(lightPattern)
    }
}

fun checkList(
        telemetry: Telemetry,
        gamepad1: Gamepad,
        gamepad2: Gamepad,
        lightPattern: RevBlinkinLedDriver.BlinkinPattern) {
    robot.blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_GOLD)
    telemetry.addData("WAIT!", "Is tail hook servo vertical?")
    telemetry.addData("Press yellow if yes", "")
    telemetry.update()
    while (!gamepad1.y && !gamepad2.y) {
        Thread.sleep(10)
    }
    telemetry.addData("WAIT!", "Are eyes of robot facing you?")
    telemetry.addData("Press green if yes", "")
    telemetry.update()
    while (!gamepad1.a && !gamepad2.a) {
        Thread.sleep(10)
    }
    robot.blinkyLights.setPattern(lightPattern)
    telemetry.addData("Status", "READY!")
    telemetry.update()
}

fun go(telemetry: Telemetry) {
    with(robot) {
        blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK)
        drive(alignWithFoundation,telemetry)
        bumpDrive(bumpSideWall)
        drive(reboundToAlignWithFoundation,telemetry)
        deployHook()
        drive(backUpToLatchFoundation,telemetry)
        drive(dragFoundation,telemetry)
        unlatchHook()
        drive(shiftTowardAudience,telemetry)
        drive(backUpToGoAroundFoundation,telemetry)
        drive(advanceAwayFromAudience,telemetry)
        drive(pushFoundationHome,telemetry)
        drive(parkUnderBridge,telemetry)
        kennethClawLeft.position = clawRestPosition
        kennethClawRight.position = clawRestPosition
        blinkyLights.setPattern(RevBlinkinLedDriver.BlinkinPattern.TWINKLES_PARTY_PALETTE)
    }
    Thread.sleep(1000)
}
fun convertTilesToInches(
        xTile: Double,
        yTile: Double,
        speed: Double = 0.75,
        desiredHeading: Double = PI / 2.0): AutonomousStep {
    return AutonomousStep(xTile * 24.0, yTile * 24.0, speed, desiredHeading)
}
