package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.util.ElapsedTime

class PidController(var setPoint: Double,
                    private val initialOutput: Double,
                    private val kp: Double,
                    private val ki: Double,
                    private val kd: Double) {

    private val pidTimer = ElapsedTime()
    private var integralPrior = 0.0
    private var deltaPrior = 0.0

    fun run(currentValue: Double): Double {
        val timeInterval = pidTimer.seconds()
        pidTimer.reset()
        val delta = currentValue - setPoint
        val integral =  integralPrior + delta * timeInterval
        val derivative = (delta - deltaPrior) / timeInterval
        val output = kp * delta + ki * integral + kd * derivative + initialOutput
        deltaPrior = delta
        integralPrior = integral
        return output
    }

    fun reset(){
        integralPrior = 0.0
        deltaPrior = 0.0
    }
}