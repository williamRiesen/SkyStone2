package org.firstinspires.ftc.teamcode.teamcode

abstract class BreathCycleStep {
    abstract val targetSpeed: Double
    abstract val bias: Double
    abstract val applyRamp: Boolean

    open val loadRamp = 0.0005
    open val kp = 0.00001
    open var ki = 0.000001 // 0.0035
    open val kd = 0

    abstract fun whatBreathCycleStepNow(vent: RoboVent): BreathCycleStep

    open fun runVentMotor(vent: RoboVent) {
        val iterationTime = vent.iterationTimer.seconds()
        vent.iterationTimer.reset()
        val currentPosition = vent.currentPosition
        val currentSpeed = (currentPosition - vent.positionPrior) / iterationTime
        val delta = targetSpeed - currentSpeed
        vent.speedDelta = delta
        val integral = vent.integralPrior + delta * iterationTime
//        val derivative = (delta - vent.deltaPrior) / iterationTime
        var output = bias  + kp * delta   + ki * integral // + kd * derivative
//        if (applyRamp) output += currentPosition * loadRamp
        vent.deltaPrior = delta
        vent.integralPrior = integral
        vent.positionPrior = currentPosition
//        if (output > vent.peakPower) vent.peakPower = output
//
        vent.motorPower = output

    }

    fun resetPID(vent: RoboVent) {
        vent.deltaPrior = 0.0
        vent.integralPrior = 0.0
    }

}