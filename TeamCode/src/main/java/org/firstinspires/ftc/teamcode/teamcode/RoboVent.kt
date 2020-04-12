package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.ElapsedTime

const val ALARM_RESET_POSITION = 0.0
const val ALARM_STRIKE_POSITION = 0.12

class RoboVent(hardwareMap: HardwareMap) {

    private val ventMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "vent_motor")
    //    val button: DigitalChannel = hardwareMap.get<DigitalChannel>(DigitalChannel::class.java, "sensor_digital")
    private val airflowSensor = hardwareMap.get(I2cDeviceSynch::class.java, "airflow_sensor")!!
    private val rateControl = hardwareMap.get(AnalogInput::class.java, "rate_control")
    private val volumeControl = hardwareMap.get(AnalogInput::class.java, "volume_control")
    private val alarmBell: Servo = hardwareMap.get(Servo::class.java, "alarm_bell")
    private val bellTimer = ElapsedTime()
    private val silenceTimer = ElapsedTime()

    init {
        ventMotor.direction = DcMotorSimple.Direction.REVERSE
        ventMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        ventMotor.targetPosition = 0
        ventMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        ventMotor.power = 0.0
//        button.mode = DigitalChannel.Mode.INPUT
        airflowSensor.engage()
        val manufacturerAddress = I2cAddr.create7bit(0x49)
        airflowSensor.i2cAddress = manufacturerAddress
    }

    var state = "Waiting at End Expiration"

    fun updateState() {
        if (state == "Delivering Inspiration" && !ventMotor.isBusy) {
            state = "Waiting at End Inspiration"
        }
        if (state == "Allowing Expiration" && !ventMotor.isBusy) {
            state = "Waiting at End Expiration"
        }
    }


    fun deliverInspiration() {
        state = "Delivering Inspiration"
        val endExpiratoryPosition = tidalVolumeSetting.toInt() * TICKS_PER_ML
        ventMotor.targetPosition = endExpiratoryPosition
        ventMotor.power = INSPIRATION_SPEED
    }

    fun allowExpiration() {
        state = "Allowing Expiration"
        ventMotor.targetPosition = 0
        ventMotor.power = EXPIRATION_SPEED
    }

    fun readAirflow(): Int {
        val reading = airflowSensor.read(0, 2)
        return reading[0] * 64 + reading[1]
    }

    private fun strikeAlarmBell() {
        alarmBell.position = ALARM_STRIKE_POSITION
    }

    private fun resetAlarmStriker() {
        alarmBell.position = ALARM_RESET_POSITION
    }

    var apneaAlarm = false
    var noReturnFlowAlarm = false
    var tachypneaAlarm = false
    var highPressureAlarm = false
    var alarmsSilenced = false


    fun updateAlarmBell() {
        if (silenceTimer.seconds() > 60) alarmsSilenced = false
        if (bellTimer.seconds() > 2.5) bellTimer.reset()
        val alarmBellCount = when {
            alarmsSilenced -> 0
            apneaAlarm -> 4
            noReturnFlowAlarm -> 3
            tachypneaAlarm -> 2
            highPressureAlarm -> 1
            else -> 0
        }

        when (bellTimer.seconds()) {
            in (0.0..0.2) -> {
                if (alarmBellCount > 0) strikeAlarmBell()
            }
            in (0.2..0.5) -> resetAlarmStriker()
            in (0.5..0.7) -> {
                if (alarmBellCount > 1) strikeAlarmBell()
            }
            in (0.7..1.0) -> resetAlarmStriker()
            in (1.0..1.2) -> {
                if (alarmBellCount > 2) strikeAlarmBell()
            }
            in (1.2..1.5) -> resetAlarmStriker()
            in (1.5..1.7) -> {
                if (alarmBellCount > 3) strikeAlarmBell()
            }
            in (1.7..2.0) -> resetAlarmStriker()
            in (2.0..2.5) -> {
            }
            else -> bellTimer.reset()

        }
    }

    var respiratoryRateSetting = rateControl.voltage * 40.0
        get() = rateControl.voltage * 40.0

    var tidalVolumeSetting = volumeControl.voltage * 800
        get() = volumeControl.voltage * 800

    var motorMode: DcMotor.RunMode
        get() = ventMotor.mode
        set(value) {
            ventMotor.mode = value
        }

    val currentPosition
        get() = ventMotor.currentPosition

    var motorPower
        get() = ventMotor.power
        set(value) {
            ventMotor.power = value
        }



}