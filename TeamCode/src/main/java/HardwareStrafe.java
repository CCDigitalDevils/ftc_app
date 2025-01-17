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

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class HardwareStrafe
{
    /* Public OpMode members. */
    public DcMotor  Drive0 = null;
    public DcMotor  Drive1 = null;
    public DcMotor  Drive2 = null;
    public DcMotor  Drive3 = null;
    public DcMotor  Drive4 = null;
    public Servo clawServo = null;
    public Servo armServo = null;
    public Servo dragServo = null;
    public DigitalChannel bottomedSensor = null;
    public DigitalChannel maxxedSensor = null;
    public BNO055IMU imu = null;
    public ColorSensor colorSensor = null;


    public static final double MID_SERVO = 0 ;
    public static final double SERVO_CLOSED = .35;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareStrafe(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        Drive0  = hwMap.get(DcMotor.class, "drive0");
        Drive1 = hwMap.get(DcMotor.class, "drive1");
        Drive2  = hwMap.get(DcMotor.class, "drive2");
        Drive3 = hwMap.get(DcMotor.class, "drive3");
        Drive4 = hwMap.get(DcMotor.class, "drive4");
        bottomedSensor = hwMap.get(DigitalChannel.class, "touch0-1");
        maxxedSensor = hwMap.get(DigitalChannel.class, "touch2-3");
        colorSensor = hwMap.get(ColorSensor.class, "colorsensor");
        Drive0.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        Drive1.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        Drive2.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        Drive3.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        Drive4.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        bottomedSensor.setMode(DigitalChannel.Mode.INPUT);
        maxxedSensor.setMode(DigitalChannel.Mode.INPUT);
        // Set all motors to zero power

        imu = hwMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters= new BNO055IMU.Parameters();
        parameters.mode  =  BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu.initialize(parameters);

        Drive4.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Drive0.setPower(0);
        Drive1.setPower(0);
        Drive2.setPower(0);
        Drive3.setPower(0);
        Drive4.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        Drive0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Drive1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Drive2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Drive3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Drive4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

         //Define and initialize ALL installed servos.
        clawServo = hwMap.get(Servo.class, "servo1");
        armServo = hwMap.get(Servo.class, "servo0");
        dragServo = hwMap.get(Servo.class, "servo2");
        clawServo.setPosition(SERVO_CLOSED);
        armServo.setPosition(.65);
        dragServo.setPosition(.025);
    }
 }

