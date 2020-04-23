package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.util.ElapsedTime


@TeleOp(name = "Align Motors", group = "Concept")
//@Disabled
class AlignMotors : OpMode() {

    lateinit var vent: RoboVent2Motor

    override fun init() {
        vent = RoboVent2Motor(hardwareMap)
    }

    override fun loop() {
        vent.rightVentMotor.power = 0.1
    }
}
