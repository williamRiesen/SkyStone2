package org.firstinspires.ftc.teamcode.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DigitalChannel.Mode
import com.qualcomm.robotcore.util.ElapsedTime

const val END_INSPIRATORY_POSITION = 325
const val INSPIRATION_SPEED = 0.2
const val EXPIRATION_SPEED = 0.2
const val RESPIRATORY_RATE = 12.0
const val INSPIRATORY_HOLD_TIME_SEC = 2.0.toLong()

@TeleOp(name = "A/C Ventilation", group = "RoboVent")
class AcVentilation : OpMode() {
    private lateinit var vent: RoboVent
    private val timer = ElapsedTime()
    private val cycleLength = 60.0 / RESPIRATORY_RATE

    override fun init() {
        vent = RoboVent(hardwareMap)
        // set the digital channel to input.


        // wait for the start button to be pressed.

    }

    override fun loop() {
        vent.updateState()
        when (vent.state) {
            "Waiting at End Inspiration" -> {
                if (timer.seconds() > INSPIRATORY_HOLD_TIME_SEC) {
                    timer.reset()
                    vent.allowExpiration()
                }
            }
            "Waiting at End Expiration" -> {
                if (timer.seconds() > cycleLength || !vent.trigger.state) {
                    timer.reset()
                    vent.deliverInspiration()
                }
            }
        }
        telemetry.addData("State", vent.state)
        telemetry.addData("Timer", timer.seconds())
        telemetry.addData("Trigger", !vent.trigger.state)
        telemetry.update()
    }
}

