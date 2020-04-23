package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp(name = "A/C Ventilation2", group = "RoboVent")
class AcVentilationUsingInterface: OpMode() {
    private lateinit var vent: RoboVent
    private var currentBreathCycleStep: BreathCycleStep = PostExpiratoryPause()

    override fun init(){
        vent = RoboVent(hardwareMap)
        vent.motorMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    override fun init_loop() {
        telemetry.addData("Resp Rate Setting: ", vent.respiratoryRateSetting)
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
        telemetry.update()
    }

    override fun loop() {
        currentBreathCycleStep = currentBreathCycleStep.whatBreathCycleStepNow(vent)
        currentBreathCycleStep.runVentMotor(vent)
        vent.updateAlarmConditions()
        vent.updateAlarmBell()
        updateDisplay()
        Thread.sleep(10)
    }

    private fun updateDisplay(){
        var alarmString = ""
        if (vent.apneaAlarm) alarmString += "APNEA  "
        if (vent.noReturnFlowAlarm)alarmString += "NO RETURN FLOW  "
        if (vent.tachypneaAlarm) alarmString += "TACHYPNEA   "
        if (vent.highPressureAlarm) alarmString += "HIGH PRESSURE  "
        if (alarmString == "") alarmString = "OK"
        telemetry.addData("", alarmString)
//        telemetry.addData("Step", currentBreathCycleStep.toString())
//        telemetry.addData("Resp Rate Setting: ", vent.respiratoryRateSetting)
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
//        telemetry.addData("Power", vent.motorPower)
        telemetry.addData("SpeedDelta", vent.speedDelta)
        telemetry.addData("Peak Power:", vent.peakPower)
        telemetry.update()
    }
}