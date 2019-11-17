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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

/**
 * This file provides basic Telop driving for a Pushbot robot.
 * The code is structured as an Iterative OpMode
 *
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 *
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Teleop_1", group="Pushbot")
@Disabled
public class Teleop_Iterative extends OpMode{

    /* Declare OpMode members. */
    Hardware robot      = new Hardware(); // use the class created to define a Pushbot's hardware
    STATE incrementup   = STATE.OFF;
    STATE incrementdown = STATE.OFF;
    STATE clawstatus = STATE.OPEN;
    private double Gear = 0.25;
    private static final Double GearChange = .05;
    private double drive;
    private double turn;
    private double right;
    private double left;
    private double offset = 0;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        Gear = 0.25;
        offset = 0.0;

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Ready");    //
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //define drive & turn
        drive = -gamepad1.left_stick_y;
        turn  =  gamepad1.right_stick_x;

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
        Gear = Range.clip(Gear, 0.15, 0.5);

        if (gamepad1.dpad_left)
        {
            turn = turn - .15 / Gear;
        }
        else if (gamepad1.dpad_right)
        {
            turn = turn + .15 / Gear;
        }

        // Combine drive and turn for blended motion.
        left  = (drive + turn)*Gear;
        right = (drive - turn)*Gear;
        left = Range.clip(left, -1, 1);
        right = Range.clip(right, -1,1);

        robot.Drive0.setPower(left);
        robot.Drive1.setPower(right);

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

        robot.armServo.setPosition(robot.MID_SERVO + offset);

        telemetry.addData("Gear","%.2f", Gear);
        telemetry.addData("left",  "%.2f", left);
        telemetry.addData("right", "%.2f", right);
        telemetry.addData("ClawStatus", clawstatus);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
