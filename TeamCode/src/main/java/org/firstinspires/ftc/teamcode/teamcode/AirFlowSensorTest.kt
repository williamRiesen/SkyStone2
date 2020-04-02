package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple
import com.qualcomm.robotcore.util.ElapsedTime


@TeleOp(name = "AirFlowSensorTest", group = "Concept")
//@Disabled
class AirFlowSensorTest : LinearOpMode() {
    private val runtime = ElapsedTime()


    override fun runOpMode() {
        val airflowSensor = hardwareMap.get (I2cDeviceSynchSimple::class.java, "airflow_sensor")
    }



}