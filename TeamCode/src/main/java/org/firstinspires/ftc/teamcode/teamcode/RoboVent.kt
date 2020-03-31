package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.HardwareMap


class RoboVent(hardwareMap: HardwareMap) {

    private val ventMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "ventMotor")
    val trigger: DigitalChannel = hardwareMap.get<DigitalChannel>(DigitalChannel::class.java, "sensor_digital")



    init {
        ventMotor.direction = DcMotorSimple.Direction.REVERSE
        ventMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        ventMotor.targetPosition = 0
        ventMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        ventMotor.power = 0.0
        trigger.mode = DigitalChannel.Mode.INPUT
    }





    var state = "Waiting at End Expiration"

    fun updateState(){
        if (state == "Delivering Inspiration" && !ventMotor.isBusy) {
            state = "Waiting at End Inspiration"
        }
        if (state == "Allowing Expiration"  && ! ventMotor.isBusy){
            state = "Waiting at End Expiration"
        }
    }


    fun deliverInspiration() {
        state = "Delivering Inspiration"
        ventMotor.targetPosition = END_INSPIRATORY_POSITION
        ventMotor.power = INSPIRATION_SPEED
    }

    fun allowExpiration() {
        state = "Allowing Expiration"
        ventMotor.targetPosition = 0
        ventMotor.power = EXPIRATION_SPEED
    }
}


