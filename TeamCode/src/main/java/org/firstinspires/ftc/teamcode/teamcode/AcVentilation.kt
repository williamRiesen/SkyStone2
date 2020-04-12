package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime

//const val END_INSPIRATORY_POSITION = 325
const val SECONDS_IN_A_MINUTE = 60
const val INSPIRATION_SPEED = 0.2
const val EXPIRATION_SPEED = 0.2
const val TICKS_PER_ML = 1
//const val RESPIRATORY_RATE = 12.0
const val INSPIRATORY_HOLD_TIME_SEC = 2.0.toLong()
const val TRIGGER_THRESHOLD = 20
const val BASELINE_AIRFLOW_SENSOR_OUTPUT = 490
const val I_TO_E_RATIO = 0.33
const val TIDAL_VOLUME_CALIBRATION = 1.0

@TeleOp(name = "A/C Ventilation", group = "RoboVent")
class AcVentilation : OpMode() {
    private lateinit var vent: RoboVent
    private val timer = ElapsedTime()
    private var cycleLength = 5.0
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
        cycleLength = 60.0 / vent.respiratoryRateSetting
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
//        testButtonPressed = !vent.button.state
        if (apnea || testButtonPressed) vent.raiseAlarm()
        else vent.resetAlarm()

        vent.updateAlarmBell()
        telemetry.addData("State", vent.state)
        telemetry.addData("Flow rate: ", airflow)
        telemetry.addData("Rate: ", vent.respiratoryRateSetting)
        telemetry.addData("Tidal Vol: ", vent.tidalVolumeSetting)
        telemetry.update()
    }
}

