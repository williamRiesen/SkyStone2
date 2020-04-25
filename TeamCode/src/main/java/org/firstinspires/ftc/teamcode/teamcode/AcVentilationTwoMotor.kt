package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp(name = "A/C Ventilation Two Motor", group = "RoboVent")
class AcVentilationTwoMotor: OpMode() {
    private lateinit var vent: RoboVent2Motor
    private lateinit var currentBreathCycleStep: BreathCycleStepTwoMotor
    private var proposedPower = 0.0
    private var currentSpeed = 0.0

    override fun init(){
        vent = RoboVent2Motor(hardwareMap)
        currentBreathCycleStep =  vent.inspiration
    }

    override fun init_loop() {
        telemetry.addData("Resp Rate Setting: ", vent.respiratoryRateSetting)
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
        telemetry.update()
        vent.respiratoryRateCounter.respiratoryRateTimer.reset()
    }

    override fun loop() {
        currentBreathCycleStep = currentBreathCycleStep.whatBreathCycleStepNow(vent)
        proposedPower = currentBreathCycleStep.runVentMotor(vent)
        vent.updateAlarmConditions()
        vent.updateAlarmBell()
        updateDisplay()
        Thread.sleep(2)
    }

    private fun updateDisplay(){
        var alarmString = ""
        if (vent.apneaAlarm) alarmString += "APNEA  "
        if (vent.noReturnFlowAlarm)alarmString += "NO RETURN FLOW  "
        if (vent.tachypneaAlarm) alarmString += "TACHYPNEA   "
        if (vent.highPressureAlarm) alarmString += "HIGH PRESSURE  "
        if (alarmString == "") alarmString = "OK"
        telemetry.addData("", alarmString)
        telemetry.addData("Step", currentBreathCycleStep.toString())
        telemetry.addData("Resp Rate Setting: ", vent.respiratoryRateSetting)
        telemetry.addData("Resp Rate Actual", vent.respiratoryRateCounter.readRate())
        telemetry.addData("Tidal Volume Setting: ", vent.tidalVolumeSetting)
//        telemetry.addData("Peak Power", vent.peakPower)
        telemetry.addData("Proposed Power", proposedPower )
        telemetry.update()
    }
}