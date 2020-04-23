package org.firstinspires.ftc.teamcode.teamcode


import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime


@TeleOp(name = "Two Motors Synchronized", group = "Concept")
//@Disabled
class TwoMotorsSynchronized : OpMode() {

    lateinit var vent: RoboVent2Motor
    val motorSynchronizer = PidController(
            setPoint = 0.0,
            initialOutput = 0.2,
            kp = 0.01,
            ki = 0.0,
            kd = 0.0
    )
    var priorPosition = 0
    val loopTimer = ElapsedTime()

    override fun init() {
        vent = RoboVent2Motor(hardwareMap)
        vent.rightVentMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        vent.leftVentMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    override fun loop() {
        val loopTime = loopTimer.seconds()
        loopTimer.reset()
        val ticksMoved = vent.rightVentMotor.currentPosition - priorPosition
        priorPosition = vent.rightVentMotor.currentPosition
        val speed = (ticksMoved) / loopTime
        vent.rightVentMotor.power = 0.2
        val motorDiscrepancy = vent.rightVentMotor.currentPosition - vent.leftVentMotor.currentPosition
        val pidOutput = motorSynchronizer.run(motorDiscrepancy.toDouble())
        vent.leftVentMotor.power = pidOutput
//        telemetry.addData("Discrepancy", motorDiscrepancy)
//        telemetry.addData("PID Output", pidOutput)
        telemetry.addData("Ticks Moved", ticksMoved)
        telemetry.addData("Speed", speed)
        telemetry.update()
        Thread.sleep(10)
    }
}
