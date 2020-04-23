package org.firstinspires.ftc.teamcode.teamcode

class Inspiration : BreathCycleStep() {

    override val targetSpeed = 10000.0 // TARGET_INSPIRATORY_SPEED
    override val bias = 0.15
    override val applyRamp = true

    override fun whatBreathCycleStepNow(vent: RoboVent): BreathCycleStep {
        var updatedBreathCycleStep: BreathCycleStep = this
        val endInspiratoryPosition = (vent.tidalVolumeSetting * TIDAL_VOLUME_CALIBRATION).toInt()
        if (vent.currentPosition > endInspiratoryPosition) {
            updatedBreathCycleStep = PostInspiratoryPause()
            resetPID(vent)
        }
        return updatedBreathCycleStep
    }

//    override fun runVentMotor(vent: RoboVent) {
//        val iterationTime = vent.iterationTimer.seconds()
//        vent.iterationTimer.reset()
//        val currentPosition = vent.currentPosition
//        val currentSpeed = (currentPosition - priorPosition) / iterationTime
//        val delta = targetSpeed - currentSpeed
//        vent.speedDelta = delta
//        val integral = integralPrior + delta * iterationTime
//        val derivative = (delta - deltaPrior) / iterationTime
//        var output = bias + kp * delta // + ki * integral + kd * derivative +
//        if (applyRamp) output += currentPosition * loadRamp
//        deltaPrior = delta
//        integralPrior = integral
//        priorPosition = currentPosition
//
//        if (output > vent.peakPower) vent.peakPower = output
//
//        vent.motorPower = output
//    }
}