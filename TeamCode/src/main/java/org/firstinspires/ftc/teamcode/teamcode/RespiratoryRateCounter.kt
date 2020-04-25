package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.util.ElapsedTime

class RespiratoryRateCounter {
    val respiratoryRateTimer = ElapsedTime()
    val breathLog = mutableListOf<Double>()

    fun recordBreathGiven(){
        breathLog.add(respiratoryRateTimer.seconds())
    }



    fun readRate(): Int {
        while (breathLog.first() < respiratoryRateTimer.seconds() - SECONDS_IN_A_MINUTE){
            breathLog.removeAt(0)
        }
        return breathLog.size
    }
}

