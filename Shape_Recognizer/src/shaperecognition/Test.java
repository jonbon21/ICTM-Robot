package shaperecognition;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.utility.Delay;
import lejos.hardware.Power;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import java.lang.Math.*;


public class Test
{
    public static void main(String[] args){
        System.out.println("Press any key to start");

        Button.LEDPattern(4);     // flash green led and
        Sound.beepSequenceUp();   // make sound when ready.

        Button.waitForAnyPress();

        // create two motor objects to control the motors.
        EV3LargeRegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);

        
        int matrixA = 5;
        int matrixB = 5;
        for(int i=1;i<matrixB;i++) {
        	motorB.rotate(30);
        	motorB.stop();
        	for(int j=1;j<matrixA; j++) {
        		double k = Math.pow((-1), (i-1));
        		int x = (int) k;
        		motorA.setSpeed(20);
        		motorA.rotate(x*60);
        		motorA.stop();
        }
}
        // free up motor resources. 
        motorA.close(); 
        motorB.close();
        Sound.beepSequence(); // we are done.
}
}