package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime

@TeleOp(name = "PID Control", group = "RoboVent")
class PidControl : OpMode() {
    private lateinit var vent: RoboVent
    private var deltaPrior = 0.0
    private var integralPrior = 0.0
    private val kp = 0.0005
    private var ki = 0.0035
    private val kd = 0
    private var bias = 0.0
    private val iterationTimer = ElapsedTime()
    private val cycleTimer = ElapsedTime()
    private var endInspiratoryPosition = 300
    private val endExpiratoryPosition = 0
    private val targetExpiratorySpeed = -300.0
    private val targetInspiratorySpeed = 300.0
    private var targetSpeed = 0.0
    private var targetPosition = endInspiratoryPosition
    private var state = VentState.WAITNG_AFTER_EXPIRATION
    private var positionPrior = 0
    private val loadRamp = 0.0005
    private var applyRamp = 1.0
    private var peakPower = 0.0

    override fun init() {
        vent = RoboVent(hardwareMap)
        vent.motorMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        positionPrior = vent.currentPosition
    }

    override fun init_loop() {
        telemetry.addData("Resp Rate Setting: ", vent.respiratoryRateSetting)
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
        telemetry.update()
        cycleTimer.reset()
    }

    override fun loop() {
        val cycleTime = SECONDS_IN_A_MINUTE / vent.respiratoryRateSetting
        val startExpirationTime = cycleTime * I_TO_E_RATIO
        endInspiratoryPosition = (vent.tidalVolumeSetting * TIDAL_VOLUME_CALIBRATION).toInt()
        if (state == VentState.DELIVERING_INSPIRATION && vent.currentPosition > endInspiratoryPosition) {
            switchToWaitingAfterInspiration()
        } else if (state == VentState.WAITING_AFTER_INSPIRATION && cycleTimer.seconds() > startExpirationTime) {
            switchToExpiration()
        } else if (state == VentState.ALLOWING_EXPIRATION && vent.currentPosition < endExpiratoryPosition) {
            switchToWaitingAfterExpiration()
        } else if (state == VentState.WAITNG_AFTER_EXPIRATION && cycleTimer.seconds() > cycleTime) {
            switchToInspiration()
        }

        val iterationTime = iterationTimer.seconds()
        iterationTimer.reset()
        val currentPosition = vent.currentPosition
        var currentSpeed = (currentPosition - positionPrior) / iterationTime
        val delta = targetSpeed - currentSpeed
        val integral = integralPrior + delta * iterationTime
        val derivative = (delta - deltaPrior) / iterationTime
        val ramp = currentPosition * loadRamp * applyRamp
        ki
        val output = kp * delta + ki * integral + kd * derivative + bias + ramp
        deltaPrior = delta
        integralPrior = integral
        positionPrior = currentPosition
        if (output > peakPower) peakPower = output

        vent.motorPower = output
//        telemetry.addData("Speed: ", currentSpeed)
//        telemetry.addData("Delta: ", delta)
        telemetry.addData("Integral: ", integral)
//        telemetry.addData("Load Ramp:", loadRamp)
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
        telemetry.addData("Peak Power: ", peakPower)
        telemetry.update()
        Thread.sleep(10)
    }

    private fun switchToWaitingAfterInspiration() {
        state = VentState.WAITING_AFTER_INSPIRATION
        targetSpeed = 0.0
        bias = 0.0
        applyRamp = 0.0
//        targetPosition = endExpiratoryPosition
        resetPID()
    }

    private fun switchToExpiration() {
        state = VentState.ALLOWING_EXPIRATION
        targetSpeed = targetExpiratorySpeed
        bias = -0.15
        applyRamp = 0.0
//        targetPosition = endExpiratoryPosition
        resetPID()
    }

    private fun switchToWaitingAfterExpiration() {
        state = VentState.WAITNG_AFTER_EXPIRATION
        targetSpeed = 0.0
        bias = 0.0
        applyRamp = 0.0
//        targetPosition = endExpiratoryPosition
        resetPID()
    }

    private fun switchToInspiration() {
        state = VentState.DELIVERING_INSPIRATION
        cycleTimer.reset()
        targetSpeed = targetInspiratorySpeed
        bias = 0.15
        applyRamp = 1.0
//        targetPosition = endInspiratoryPosition
        peakPower = 0.0
        resetPID()
    }

    private fun resetPID() {
        deltaPrior = 0.0
        integralPrior = 0.0
    }
}

