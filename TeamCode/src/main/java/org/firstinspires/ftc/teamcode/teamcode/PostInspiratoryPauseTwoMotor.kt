package org.firstinspires.ftc.teamcode.teamcode

class PostInspiratoryPauseTwoMotor(initialTargetPosition: Int) : BreathCycleStepTwoMotor {

    private val endInspirationHoldPositionController = PidController(
            setPoint = initialTargetPosition.toDouble(),
            initialOutput = 0.0,
            kp = 0.01,
            ki = 0.0,
            kd = 0.0
    )

    override fun whatBreathCycleStepNow(vent: RoboVent2Motor): BreathCycleStepTwoMotor {
        var updatedBreathCycleStep: BreathCycleStepTwoMotor = this
        val cycleTime = SECONDS_IN_A_MINUTE / vent.respiratoryRateSetting
        val startExpirationTime = cycleTime * I_TO_E_RATIO
        if (vent.cycleTimer.seconds() > startExpirationTime) {
            updatedBreathCycleStep = vent.inspiration
            endInspirationHoldPositionController.reset()
        }
        return updatedBreathCycleStep
    }

    override fun runVentMotor(vent: RoboVent2Motor): Double {
        endInspirationHoldPositionController.setPoint = vent.tidalVolumeSetting * TIDAL_VOLUME_CALIBRATION
//        vent.setPowerBothMotors(endInspirationHoldPositionController.run(vent.rightVentMotor.currentPosition.toDouble()) )
        vent.setPowerBothMotors(0.0)
        return 0.0
    }


    override fun toString(): String {
        return "End-Inspiratory Pause"
    }


}