package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.TouchSensor;


@TeleOp(name = "Student Training OpMode", group = "Training")
public class DCMotorTutor extends LinearOpMode {

    public static final double MOTOR_POWER = 0.1; // Speed from 0.0 to 1.0

    public static final String MOTOR_NAME = "testMotor";
    public static final String TOUCH_NAME = "testTouch";

    /* -------------------------------------------------------------------------
     * HARDWARE & STATE VARIABLES
     * ------------------------------------------------------------------------- */
    private DcMotorEx trainingMotor;
    private TouchSensor trainingTouch;
    
    @Override
    public void runOpMode() {

        /* ---------------------------------------------------------------------
         * INITIALIZATION
         * --------------------------------------------------------------------- */
        trainingMotor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        trainingTouch = hardwareMap.get(TouchSensor.class, TOUCH_NAME);

        // TEACHING NOTE: Resetting the encoder is like "zeroing" a scale.
        // It makes the current physical position of the motor equal to 0.
        trainingMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        trainingMotor.setPower(0.0);


        // TEACHING NOTE: Advanced Hardware Setting
        // You can reverse the direction of a motor if it's mounted "backwards".
        // trainingMotor.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Motor Stopped", "Hit start to continue.");
        telemetry.update();

        waitForStart();

        /* ---------------------------------------------------------------------
         * MAIN LOOP
         * --------------------------------------------------------------------- */
        while (opModeIsActive()) {

            if(trainingTouch.isPressed())
            {
                trainingMotor.setPower(0.5);
            }
            else {
                trainingMotor.setPower(0.0);
            }
            telemetry.addData("Button State", trainingTouch.isPressed());
            telemetry.addData("Motor Power",  trainingMotor.getPower());

            telemetry.update();
        }
    }

}
