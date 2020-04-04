package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.util.ElapsedTime


@TeleOp(name = "AirFlowSensorTest", group = "Concept")
//@Disabled
class AirFlowSensorTest : LinearOpMode() {
    private val runtime = ElapsedTime()


    override fun runOpMode() {
//        val airflowSensor = hardwareMap.get (I2cDeviceSynchSimple::class.java, "airflow_sensor")

        val airflowSensor = hardwareMap.get(I2cDeviceSynch::class.java, "airflow_sensor")
        airflowSensor.engage()
        val manufacturerAddress = I2cAddr.create7bit(0x49)
        airflowSensor.i2cAddress = manufacturerAddress
        waitForStart()
        while (opModeIsActive()) {
            val reading = airflowSensor.read(0, 3)
            val byteOne = String.format("%02X", reading[0])
            val byteTwo = String.format("%02X", reading[1])
            val byteThree = String.format("%02X", reading[2])
            telemetry.addData("byteOne", byteOne)
            telemetry.addData("byteTwo", byteTwo)
            telemetry.addData("byteThree", byteThree)
            telemetry.update()
            Thread.sleep(20)
        }
    }


}
