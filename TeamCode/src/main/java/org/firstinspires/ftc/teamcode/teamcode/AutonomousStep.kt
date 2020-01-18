package org.firstinspires.ftc.teamcode.teamcode

import kotlin.math.PI
import kotlin.math.sqrt

class AutonomousStep(
        var x: Double,
        var y: Double,
        val speed: Double = 0.75,
        var desiredHeading: Double = PI / 2.0) {

    override fun toString(): String {
        return "($x,$y) at speed $speed"
    }

    fun reflectOverXAxis() {
        if (desiredHeading == PI / 2.0) {
            x = -x
        } else if (desiredHeading == 0.0) {
            desiredHeading = PI
            x = -x
        }
    }

    val length
        get() = sqrt(x * x + y * y)
}
