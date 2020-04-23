package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.ElapsedTime

const val ALARM_RESET_POSITION = 0.0
const val ALARM_STRIKE_POSITION = 0.12

class RoboVent2Motor(hardwareMap: HardwareMap) {

    val rightVentMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "vent_motor2")
    val leftVentMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "vent_motor")
    //    val button: DigitalChannel = hardwareMap.get<DigitalChannel>(DigitalChannel::class.java, "sensor_digital")
    private val airflowSensor = hardwareMap.get(I2cDeviceSynch::class.java, "airflow_sensor")!!
    private val rateControl = hardwareMap.get(AnalogInput::class.java, "rate_control")
    private val volumeControl = hardwareMap.get(AnalogInput::class.java, "volume_control")
    private val alarmBell: Servo = hardwareMap.get(Servo::class.java, "alarm_bell")

    var apneaAlarm = false
    var noReturnFlowAlarm = false
    var tachypneaAlarm = false
    var highPressureAlarm = false
    var alarmsSilenced = false
    var breathCount = 0
    var peakPower = 0.0

    private val apneaTimer = ElapsedTime()
    private val returnFlowTimer = ElapsedTime()
    private val breathCountTimer = ElapsedTime()
    private val bellTimer = ElapsedTime()
    private val silenceTimer = ElapsedTime()
    val cycleTimer = ElapsedTime()


    val respiratoryRateSetting
        get() = rateControl.voltage * 40.0

    val tidalVolumeSetting
        get() = volumeControl.voltage * 800

    init {
        rightVentMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        leftVentMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        leftVentMotor.direction = DcMotorSimple.Direction.REVERSE
//        button.mode = DigitalChannel.Mode.INPUT
        airflowSensor.engage()
        val manufacturerAddress = I2cAddr.create7bit(0x49)
        airflowSensor.i2cAddress = manufacturerAddress
    }


    fun readAirflow(): Int {
        val reading = airflowSensor.read(0, 2)
        return reading[0] * 64 + reading[1]
    }

    fun updateAlarmConditions(){
        highPressureAlarm = peakPower > HIGH_PRESSURE_ALARM_THRESHOLD
        val airflow = readAirflow()
        if (airflow > 1000) apneaTimer.reset()
        apneaAlarm = apneaTimer.seconds() > 10.0
        if (airflow < 250) returnFlowTimer.reset()
        noReturnFlowAlarm = returnFlowTimer.seconds() > 10.0
        if (breathCountTimer.seconds() > 60) {
            breathCountTimer.reset()
            breathCount = 0
        }
        tachypneaAlarm = breathCount > TACHYPNEA_THRESHOLD
    }

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

    private fun strikeAlarmBell() {
        alarmBell.position = ALARM_STRIKE_POSITION
    }

    private fun resetAlarmStriker() {
        alarmBell.position = ALARM_RESET_POSITION
    }

    fun setPowerBothMotors(power: Double){
        rightVentMotor.power = power
        synchronizeMotors()
    }

    private val motorSynchronizer = PidController(
            setPoint = 0.0,
            initialOutput = 0.2,
            kp = 0.001,
            ki = 0.0,
            kd = 0.0
    )

    private fun synchronizeMotors(){
        val motorDiscrepancy = (rightVentMotor.currentPosition - leftVentMotor.currentPosition).toDouble()
        leftVentMotor.power = motorSynchronizer.run(motorDiscrepancy)
    }

    val inspiration: BreathCycleStepTwoMotor = InspirationTwoMotor()
    val postExpiratoryPause: BreathCycleStepTwoMotor = PostExpiratoryPauseTwoMotor()
    val postInspiratoryPause: BreathCycleStepTwoMotor = PostInspiratoryPauseTwoMotor((tidalVolumeSetting * TIDAL_VOLUME_CALIBRATION).toInt())
    val expiration: BreathCycleStepTwoMotor = ExpirationTwoMotor()
}