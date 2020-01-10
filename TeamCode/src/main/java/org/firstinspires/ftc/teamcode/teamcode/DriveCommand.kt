package org.firstinspires.ftc.teamcode

import kotlin.math.*

class DriveCommand(var xSpeed: Double, var ySpeed: Double, var rotationSpeed: Double) {

    fun rotate(radians: Float) {
        val x = xSpeed * cos(radians) - ySpeed * sin(radians)
        val y = xSpeed * sin(radians) + ySpeed * cos(radians)
        xSpeed = x
        ySpeed = y
    }

    fun rotated(radians: Double): DriveCommand {

        return DriveCommand(
                xSpeed * cos(radians) - ySpeed * sin(radians),
                xSpeed * sin(radians) + ySpeed * cos(radians),
                rotationSpeed)
    }

    override fun toString()= "Go north by $xSpeed); Go west by $ySpeed"

    val length
        get() = sqrt(xSpeed * xSpeed + ySpeed * ySpeed)
}