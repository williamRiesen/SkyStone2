package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.util.ElapsedTime

class ExpirationTwoMotor : BreathCycleStepTwoMotor {

    private val loopTimer = ElapsedTime()
    private var priorPosition = 0
    private val expirationSpeedController =  PidController(
            setPoint = 300.0,
            initialOutput = -0.2,
            kp = 0.0, //0.00001,
            ki = 0.0,//0.000001, // 0.0035
            kd = 0.0
    )

    override fun whatBreathCycleStepNow(vent: RoboVent2Motor): BreathCycleStepTwoMotor {
        val endExpiratoryPosition = 0
        var updatedBreathCycleStep: BreathCycleStepTwoMotor = this
         if (vent.rightVentMotor.currentPosition < endExpiratoryPosition) {
             updatedBreathCycleStep = vent.postExpiratoryPause
             expirationSpeedController.reset()
         }
        return updatedBreathCycleStep
    }

    override fun runVentMotor(vent: RoboVent2Motor): Double {
        val loopTime = loopTimer.seconds()
        loopTimer.reset()
        val currentPosition = vent.rightVentMotor.currentPosition
        val currentSpeed = (currentPosition - priorPosition) / loopTime
        priorPosition = currentPosition
        val power = expirationSpeedController.run(currentSpeed)
        if (power > vent.peakPower) vent.peakPower = power
//        vent.setPowerBothMotors(power)
        vent.setPowerBothMotors(-0.2)
        return power
    }
    override fun toString(): String {
        return "Expiration"
    }
}