package org.firstinspires.ftc.teamcode.teamcode

class PostInspiratoryPause : BreathCycleStep() {
    override val targetSpeed = 0.0
    override val bias = 0.0
    override val applyRamp = false

    override fun whatBreathCycleStepNow(vent: RoboVent): BreathCycleStep {
        var updatedBreathCycleStep: BreathCycleStep = this
        val cycleTime = SECONDS_IN_A_MINUTE / vent.respiratoryRateSetting
        val startExpirationTime = cycleTime * I_TO_E_RATIO
        if (vent.cycleTimer.seconds() > startExpirationTime) {
            updatedBreathCycleStep = Expiration()
            resetPID(vent)
        }
        return updatedBreathCycleStep
    }

    override fun runVentMotor(vent: RoboVent){
        vent.motorPower = 0.0
    }
}