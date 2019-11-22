/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

import java.text.SimpleDateFormat;

/**
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 * The code is structured as a LinearOpMode
 *
 * This particular OpMode executes a POV Game style Teleop for a PushBot
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Pushbot: Strafe_1", group="Strafebot")
@Disabled
public class Strafe_1 extends OpMode {

    /* Declare OpMode members. */
    HardwareStrafe robot           = new HardwareStrafe();   // Use a Pushbot's hardware
    STATE incrementup   = STATE.OFF;
    STATE incrementdown = STATE.OFF;
    STATE clawstatus = STATE.OPEN;
    private double Gear = 0.25;
    private static final Double GearChange = .05;
    private double offset = 0;
    private double drive;
    private double strafe;
    private double turn;
    private double leftR;
    private double rightR;
    private double leftF;
    private double rightF;
    //set up all variables

    @Override
    public void init() {
        robot.init(hardwareMap);
        Gear = 0.5;
        offset = 0.0;

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Ready");
    }

    @Override
    public void loop() {
      //activate variables
       //grab values from cotroller
        drive = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;

        if(gamepad1.dpad_up && incrementup == STATE.OFF)
        {
            incrementup = STATE.INPROGRESS;
        }
        else if (!gamepad1.dpad_up && incrementup == STATE.INPROGRESS)
        {
            Gear += GearChange;
            incrementup = STATE.OFF;
        }

        if(gamepad1.dpad_down && incrementdown == STATE.OFF)
        {
            incrementdown = STATE.INPROGRESS;
        }
        else if (!gamepad1.dpad_down && incrementdown == STATE.INPROGRESS)
        {
            Gear -= GearChange;
            incrementdown = STATE.OFF;
        }
        Gear = Range.clip(Gear, 0.25, 1);

        //combine drive and turn for blended motion
        leftR = ((-strafe + drive) + turn) * Gear;
        rightR = ((strafe + drive) - turn) * Gear;
        leftF = ((strafe + drive) + turn) * Gear;
        rightF = ((-strafe + drive) - turn) * Gear;

        //Normalize values so neither exceed +/- 1.0
        leftR = Range.clip(leftR, -1, 1);
        rightR = Range.clip(rightR, -1, 1);
        leftF = Range.clip(leftF, -1, 1);
        rightF = Range.clip(rightF, -1, 1);

        robot.Drive0.setPower(leftF);
        robot.Drive1.setPower(rightF);
        robot.Drive2.setPower(leftR);
        robot.Drive3.setPower(rightR);

        if (gamepad1.right_bumper)
        {
            offset = .0;
            clawstatus = STATE.OPEN;
        }
        else if (gamepad1.right_trigger>0)
        {
            offset = .26 ;
            clawstatus = STATE.CLOSED;
        }
        robot.clawServo.setPosition(robot.MID_SERVO + offset);

        telemetry.addData("leftF",  "%.2f", leftF);
        telemetry.addData("leftR",  "%.2f", leftR);
        telemetry.addData("rightF",  "%.2f", rightF);
        telemetry.addData("rightR",  "%.2f", rightR);
        telemetry.addData("Gear","%.2f", Gear);
        telemetry.addData("ClawStatus", clawstatus);

    }
}
