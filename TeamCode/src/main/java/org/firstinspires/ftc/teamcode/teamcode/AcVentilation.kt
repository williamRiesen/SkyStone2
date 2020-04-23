package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
const val SECONDS_IN_A_MINUTE = 60
const val INSPIRATION_SPEED = 0.2
const val EXPIRATION_SPEED = 0.2
const val TICKS_PER_ML = 1
const val TRIGGER_THRESHOLD = 20
const val BASELINE_AIRFLOW_SENSOR_OUTPUT = 490
const val I_TO_E_RATIO = 0.33
const val TIDAL_VOLUME_CALIBRATION = 1.0
const val HIGH_PRESSURE_ALARM_THRESHOLD = 0.75
const val TACHYPNEA_THRESHOLD = 24
const val TARGET_EXPIRATORY_SPEED = -300.0
const val TARGET_INSPIRATORY_SPEED = 300.0

@TeleOp(name = "A/C Ventilation", group = "RoboVent")
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
    private var state = VentState.WAITING_AFTER_EXPIRATION
    private var positionPrior = 0
    private val loadRamp = 0.0005
    private var applyRamp = 1.0
    private var peakPower = 0.0
    private var airflow = 0
    private val apneaTimer = ElapsedTime()
    private val returnFlowTimer = ElapsedTime()
    private var breathCount = 0
    private val breathCountTimer = ElapsedTime()
    private var trigger = false

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
        apneaTimer.reset()
        returnFlowTimer.reset()
        breathCountTimer.reset()
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
        } else if (state == VentState.WAITING_AFTER_EXPIRATION && cycleTimer.seconds() > cycleTime) {
            switchToInspiration()
        }

        val iterationTime = iterationTimer.seconds()
        iterationTimer.reset()
        val currentPosition = vent.currentPosition
        val currentSpeed = (currentPosition - positionPrior) / iterationTime
        val delta = targetSpeed - currentSpeed
        val integral = integralPrior + delta * iterationTime
        val derivative = (delta - deltaPrior) / iterationTime
        val ramp = currentPosition * loadRamp * applyRamp
        val output = kp * delta + ki * integral + kd * derivative + bias + ramp
        deltaPrior = delta
        integralPrior = integral
        positionPrior = currentPosition
        if (output > peakPower) peakPower = output

        vent.motorPower = output

        vent.highPressureAlarm = peakPower > HIGH_PRESSURE_ALARM_THRESHOLD
        airflow = vent.readAirflow()
        trigger = airflow > BASELINE_AIRFLOW_SENSOR_OUTPUT + TRIGGER_THRESHOLD
        if (trigger && state == VentState.WAITING_AFTER_EXPIRATION)switchToInspiration()
        if (airflow > 1000) apneaTimer.reset()
        vent.apneaAlarm = apneaTimer.seconds() > 10.0
        if (airflow < 250) returnFlowTimer.reset()
        vent.noReturnFlowAlarm = returnFlowTimer.seconds() > 10.0
        if (breathCountTimer.seconds() > 60) {
            breathCountTimer.reset()
            breathCount = 0
        }
        vent.tachypneaAlarm = breathCount > TACHYPNEA_THRESHOLD
        vent.updateAlarmBell()
        telemetry.addData("Resp Rate: ", vent.respiratoryRateSetting)
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
        telemetry.addData("Flow rate: ", airflow)
        telemetry.addData("Peak Power: ", peakPower)
        telemetry.update()
        Thread.sleep(10)
    }

    private fun switchToWaitingAfterInspiration() {
        state = VentState.WAITING_AFTER_INSPIRATION
        targetSpeed = 0.0
        bias = 0.0
        applyRamp = 0.0
        resetPID()
    }

    private fun switchToExpiration() {
        state = VentState.ALLOWING_EXPIRATION
        targetSpeed = targetExpiratorySpeed
        bias = -0.15
        applyRamp = 0.0
        resetPID()
    }

    private fun switchToWaitingAfterExpiration() {
        state = VentState.WAITING_AFTER_EXPIRATION
        targetSpeed = 0.0
        bias = 0.0
        applyRamp = 0.0
        resetPID()
    }

    private fun switchToInspiration() {
        state = VentState.DELIVERING_INSPIRATION
        cycleTimer.reset()
        breathCount += 1
        targetSpeed = targetInspiratorySpeed
        bias = 0.15
        applyRamp = 1.0
        peakPower = 0.0
        resetPID()
    }

    private fun resetPID() {
        deltaPrior = 0.0
        integralPrior = 0.0
    }
}

