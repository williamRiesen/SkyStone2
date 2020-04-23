package org.firstinspires.ftc.teamcode.teamcode

class Expiration : BreathCycleStep() {
    override val targetSpeed = -300.0 // TARGET_EXPIRATORY_SPEED
    override val bias = 0.0
    override val applyRamp = false

    override fun whatBreathCycleStepNow(vent: RoboVent): BreathCycleStep {
        val endExpiratoryPosition = 0
        var updatedBreathCycleStep: BreathCycleStep = this
         if (vent.currentPosition < endExpiratoryPosition) {
             updatedBreathCycleStep = PostExpiratoryPause()
             resetPID(vent)
         }
        return updatedBreathCycleStep

    }

    override fun runVentMotor(vent: RoboVent) {
        vent.motorPower = -0.2
    }

}