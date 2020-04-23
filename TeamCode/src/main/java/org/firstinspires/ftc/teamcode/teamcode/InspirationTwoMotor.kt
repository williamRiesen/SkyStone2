package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.util.ElapsedTime

class InspirationTwoMotor : BreathCycleStepTwoMotor {

    private val loopTimer = ElapsedTime()
    private var priorPosition = 0
    private val inspirationSpeedController = PidController(
            setPoint = 750.0,
            initialOutput = 0.3,
            kp = 0.0001, // 0.00001,
            ki = 0.0, //0.000001, // 0.0035
            kd = 0.0
    )

    override fun whatBreathCycleStepNow(vent: RoboVent2Motor): BreathCycleStepTwoMotor {
        var updatedBreathCycleStep: BreathCycleStepTwoMotor = this
        val endInspiratoryPosition = (vent.tidalVolumeSetting * TIDAL_VOLUME_CALIBRATION).toInt()
        if (vent.rightVentMotor.currentPosition > endInspiratoryPosition) {
            updatedBreathCycleStep = vent.postInspiratoryPause
            inspirationSpeedController.reset()
        }
        return this // updatedBreathCycleStep
    }

    override fun runVentMotor(vent: RoboVent2Motor): Double {
        val loopTime = loopTimer.seconds()
        loopTimer.reset()
        val currentPosition = vent.rightVentMotor.currentPosition
        val currentSpeed = (currentPosition - priorPosition) / loopTime
        priorPosition = currentPosition
        val power = inspirationSpeedController.run(currentSpeed)
        if (power > vent.peakPower) vent.peakPower = power
        vent.setPowerBothMotors(power)
        return power
    }

    override fun toString(): String {
        return "Inspiration"
    }
}