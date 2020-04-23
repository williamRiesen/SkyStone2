package org.firstinspires.ftc.teamcode.teamcode

class PostExpiratoryPause : BreathCycleStep() {
    override val targetSpeed = 0.0
    override val bias = 0.0
    override val applyRamp = false

    override fun whatBreathCycleStepNow(vent: RoboVent): BreathCycleStep {
        var updatedBreathCycleStep: BreathCycleStep = this
        val cycleTime = SECONDS_IN_A_MINUTE / vent.respiratoryRateSetting
        val patientTriggers = vent.readAirflow() > BASELINE_AIRFLOW_SENSOR_OUTPUT + TRIGGER_THRESHOLD
        if (vent.cycleTimer.seconds() > cycleTime || patientTriggers ) {
            updatedBreathCycleStep = Inspiration()
            vent.cycleTimer.reset()
            vent.breathCount += 1
            resetPID(vent)
        }
        return updatedBreathCycleStep
        }

    override fun runVentMotor(vent: RoboVent) {
        vent.motorPower = 0.0
    }
}