import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutonomousUtilities {
    private HardwareStrafe robot;
    private LinearOpMode linearOpMode;
    private ElapsedTime runtime;
    public AutonomousUtilities(){};
    public AutonomousUtilities (HardwareStrafe robot, LinearOpMode linearOpMode, ElapsedTime runtime){
        this.robot = robot;
        this.linearOpMode = linearOpMode;
        this.runtime = runtime;
    }
    private double liftpower;
    public void pause (){
        try {
            Thread.sleep(200);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void strafe (double speed, double angle) {
        angle = angle + 45;
        double lFrR = Math.sin(Math.toRadians(angle)) * speed;
        double rFlR = Math.cos(Math.toRadians(angle)) * speed;
        lFrR = Range.clip(lFrR, -1, 1);
        rFlR = Range.clip(rFlR, -1, 1);
        if (linearOpMode.opModeIsActive()) {
            robot.Drive0.setPower(lFrR);
            robot.Drive1.setPower(rFlR);
            robot.Drive2.setPower(rFlR);
            robot.Drive3.setPower(lFrR);
        }
    }

        public void stopMotors () {
            robot.Drive0.setPower(0);
            robot.Drive1.setPower(0);
            robot.Drive2.setPower(0);
            robot.Drive3.setPower(0);
            pause();
    }

        public void strafeTime ( double speed, double angle, double time){
            runtime.reset();
            while (linearOpMode.opModeIsActive() && (runtime.seconds() < time)) {
                    strafe(speed, angle);
                linearOpMode.telemetry.addData("Path", "Leg: %2.5f S Elapsed", runtime.seconds());
                linearOpMode.telemetry.update();
            }
            stopMotors();
        }
    public void strafeTime ( double speed, double angle, double time, STATE state){
        if(state == STATE.OPEN){
            clawOpen();
        }
        else if(state == STATE.CLOSED){
            clawClosed();
        }
        strafeTime(speed, angle, time);
    }
        public void clawOpen (){
        robot.clawServo.setPosition(robot.MID_SERVO);
        }
        public void clawClosed(){
        robot.clawServo.setPosition(robot.SERVO_CLOSED);
        }

        public void rotate( double speed, STATE state, double time){
            runtime.reset();
            while (linearOpMode.opModeIsActive() && (runtime.seconds() < time)) {
                if (state == STATE.LEFT){
                    robot.Drive0.setPower(-1 * speed);
                    robot.Drive1.setPower(1 * speed);
                    robot.Drive2.setPower(-1 * speed);
                    robot.Drive3.setPower(1 * speed);
                }
                else if (state == STATE.RIGHT){
                    robot.Drive0.setPower(1 * speed);
                    robot.Drive1.setPower(-1 * speed);
                    robot.Drive2.setPower(1 * speed);
                    robot.Drive3.setPower(-1 * speed);
                }
            }
        }
        public void strafeTime(double speed, double angle, double time, STATE state, double liftTime){
            liftpower = (liftTime / time) * .75;
            runtime.reset();
            while (linearOpMode.opModeIsActive() && (runtime.seconds() < time)) {
                strafe(speed, angle);
                if (state == STATE.UP){
                    robot.Drive4.setPower(liftpower);
                }
                else if (state == STATE.DOWN){
                    robot.Drive4.setPower(-liftpower);
                }
                linearOpMode.telemetry.addData("Path", "Leg: %.2f S Elapsed", runtime.seconds());
                linearOpMode.telemetry.update();
            }
            stopMotors();
        }
        public void liftTime(double speed, STATE state, double time){
            runtime.reset();
            while (linearOpMode.opModeIsActive() && (runtime.seconds() < time)) {
                if (state == STATE.UP){
                    robot.Drive4.setPower(speed);
                }
                else if (state == STATE.DOWN){
                    robot.Drive4.setPower(-speed);
                }
            }
            stopMotors();
        }
    }