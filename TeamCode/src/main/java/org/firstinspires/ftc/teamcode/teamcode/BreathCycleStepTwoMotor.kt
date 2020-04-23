package org.firstinspires.ftc.teamcode.teamcode

interface BreathCycleStepTwoMotor {

    fun whatBreathCycleStepNow(vent: RoboVent2Motor): BreathCycleStepTwoMotor

    fun runVentMotor(vent: RoboVent2Motor): Double
}