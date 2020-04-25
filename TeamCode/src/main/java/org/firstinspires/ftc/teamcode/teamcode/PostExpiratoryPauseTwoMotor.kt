package org.firstinspires.ftc.teamcode.teamcode

class PostExpiratoryPauseTwoMotor : BreathCycleStepTwoMotor {

    private val endExpirationHoldPositionController = PidController(
            setPoint =  0.0,
            initialOutput = 0.0,
            kp = 0.01,
            ki = 0.0,
            kd = 0.0
    )
    override fun whatBreathCycleStepNow(vent: RoboVent2Motor): BreathCycleStepTwoMotor {
        var updatedBreathCycleStep: BreathCycleStepTwoMotor = this
        val cycleTime = SECONDS_IN_A_MINUTE / vent.respiratoryRateSetting
        val patientTriggers = vent.readAirflow() > BASELINE_AIRFLOW_SENSOR_OUTPUT + TRIGGER_THRESHOLD
        if (vent.cycleTimer.seconds() > cycleTime || patientTriggers ) {
            updatedBreathCycleStep = vent.inspiration
            vent.cycleTimer.reset()
            vent.respiratoryRateCounter.recordBreathGiven()
            endExpirationHoldPositionController.reset()
        }
        return updatedBreathCycleStep
        }


    override fun runVentMotor(vent: RoboVent2Motor): Double {
//        vent.setPowerBothMotors(endExpirationHoldPositionController.run(vent.rightVentMotor.currentPosition.toDouble()) )
        vent.setPowerBothMotors(0.0)
        return 0.0
    }

    override fun toString(): String {
        return "End-Expiratory Pause"
    }
}