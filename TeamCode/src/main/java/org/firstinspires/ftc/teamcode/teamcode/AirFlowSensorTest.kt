package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple
import com.qualcomm.robotcore.util.ElapsedTime


@TeleOp(name = "AirFlowSensorTest", group = "Concept")
//@Disabled
class AirFlowSensorTest : LinearOpMode() {
    private val runtime = ElapsedTime()



    override fun runOpMode() {
//        val airflowSensor = hardwareMap.get (I2cDeviceSynchSimple::class.java, "airflow_sensor")
        val airflowSensor = hardwareMap.get (I2cDeviceSynch::class.java, "airflow_sensor")
        Thread.sleep(100)
        airflowSensor.engage()
        val manufacturerAddress = I2cAddr.create7bit(0x49)
        airflowSensor.i2cAddress = manufacturerAddress
        telemetry.addData("isEngaged", airflowSensor.isEngaged)
        telemetry.addData("isArmed", airflowSensor.isArmed)
        telemetry.addData("7-bit address:  ", airflowSensor.i2cAddress)
        telemetry.update()
        waitForStart()
        while (opModeIsActive()) {

            telemetry.addData("reading", airflowSensor.read8(1))
            telemetry.update()
        }
        Thread.sleep(5000)

    }



}