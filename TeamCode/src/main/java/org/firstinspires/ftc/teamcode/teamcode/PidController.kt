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
    private val ki = 0.00005
    private val kd = 0
    private val bias = 0.2
    private val iterationTimer = ElapsedTime()
    private val cycleTimer = ElapsedTime()
    private var endInspiratoryPosition = 250
    private val endExpiratoryPosition = 0
    private val targetExpiratorySpeed = -380.0
    private val targetInspiratorySpeed =380.0
    private var targetSpeed = targetInspiratorySpeed
    private var targetPosition = endInspiratoryPosition
    private var state = VentState.DELIVERING_INSPIRATION
    private var positionPrior = 0

    override fun init() {
        vent = RoboVent(hardwareMap)
        vent.motorMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        positionPrior = vent.currentPosition
    }

    override fun loop() {
        val cycleTime = SECONDS_IN_A_MINUTE / vent.respiratoryRateSetting
//        val startExpirationTime = cycleTime * I_TO_E_RATIO
//        if (state == VentState.ALLOWING_EXPIRATION && cycleTimer.seconds() > cycleTime) {
//            switchToInspiration()
//        } else if (state == VentState.DELIVERING_INSPIRATION && cycleTimer.seconds() > startExpirationTime) {
//            switchToExpiration()
//        }
        val iterationTime = iterationTimer.seconds()
        iterationTimer.reset()
        var currentSpeed = (vent.currentPosition - positionPrior) / iterationTime
        val delta = targetSpeed - currentSpeed
        val integral = integralPrior + delta * iterationTime
        val derivative = (delta - deltaPrior) / iterationTime
        val output = kp * delta + ki * integral + kd * derivative + bias
        deltaPrior = delta
        integralPrior = integral
        positionPrior = vent.currentPosition

        vent.motorPower = output
        telemetry.addData("Speed: ", currentSpeed)
        telemetry.addData("Delta: ", delta)
        telemetry.addData("Integral: ", integral)
        telemetry.addData("Motor Power: ", vent.motorPower)
        telemetry.update()
        Thread.sleep(50)
    }

    private fun switchToExpiration() {
        state = VentState.ALLOWING_EXPIRATION
        targetSpeed = targetExpiratorySpeed
//        targetPosition = endExpiratoryPosition
        resetPID()
    }

    private fun switchToInspiration() {
        state = VentState.DELIVERING_INSPIRATION
        cycleTimer.reset()
        targetSpeed = targetInspiratorySpeed
//        targetPosition = endInspiratoryPosition
        resetPID()
    }

    private fun resetPID() {
        deltaPrior = 0.0
        integralPrior = 0.0
    }
}

