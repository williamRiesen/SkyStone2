package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime

const val END_INSPIRATORY_POSITION = 325
const val INSPIRATION_SPEED = 0.2
const val EXPIRATION_SPEED = 0.2
const val RESPIRATORY_RATE = 12.0
const val INSPIRATORY_HOLD_TIME_SEC = 2.0.toLong()
const val TRIGGER_THRESHOLD = 20
const val BASELINE_AIRFLOW_SENSOR_OUTPUT = 490

@TeleOp(name = "A/C Ventilation", group = "RoboVent")
class AcVentilation : OpMode() {
    private lateinit var vent: RoboVent
    private val timer = ElapsedTime()
    private val cycleLength = 60.0 / RESPIRATORY_RATE
    private var trigger = false
    private var testButtonPressed = false
    private var apnea = false
    private val apneaTimer = ElapsedTime()
    private var airflow = 0

    override fun init() {
        vent = RoboVent(hardwareMap)
        apneaTimer.reset()
    }

    override fun loop() {
        vent.updateState()
        when (vent.state) {
            "Waiting at End Inspiration" -> {
                if (timer.seconds() > INSPIRATORY_HOLD_TIME_SEC) {
                    timer.reset()
                    vent.allowExpiration()
                }
            }
            "Waiting at End Expiration" -> {
                val airFlow = vent.readAirflow()
                trigger = airFlow > BASELINE_AIRFLOW_SENSOR_OUTPUT + TRIGGER_THRESHOLD
                if (timer.seconds() > cycleLength || trigger) {
                    timer.reset()
                    vent.deliverInspiration()
                }
            }
        }
        airflow = vent.readAirflow()
        if (airflow > 1000) {
            apneaTimer.reset()
            apnea = false
        }
        if (apneaTimer.seconds() > 10) apnea = true
        testButtonPressed = !vent.button.state
        if (apnea || testButtonPressed) vent.raiseAlarm()
        else vent.resetAlarm()

        vent.updateAlarmBell()
        telemetry.addData("State", vent.state)
        telemetry.addData("Flow rate: ", airflow)
        telemetry.addData("Trigger", trigger)
        telemetry.update()
    }
}

