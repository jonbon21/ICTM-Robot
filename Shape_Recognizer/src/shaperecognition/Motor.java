package shaperecognition;

import lejos.hardware.Power;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

import java.lang.Math.*;


public class Motor extends EV3LargeRegulatedMotor {
	Boolean isHomed = false;
	Boolean homingBusy = false;
	String motorName;
	int homingSpeed = 60;		//in degrees/s
	double degreesPerPixel;
	
	
	public Motor(Port inpPort, double inpDegreesPerPixel, String motorName) {   //double inpNormalMotorCurrent, String inpMotorName,
		super(inpPort);
		//normalMotorCurrent = inpNormalMotorCurrent;
		//maxMotorCurrentForHoming = overcurrentLimitForHoming*normalMotorCurrent + normalMotorCurrent;
		//motorName = inpMotorName;
		degreesPerPixel = inpDegreesPerPixel;
		//homingSpeed = (int) (inpHomingSpeed*mmToDegreesConversion); //  degrees/s
	}
	
	
	public double getConversion() {
		return degreesPerPixel;
	}

<<<<<<< HEAD
	public void home() {
		while (isHomed =! true) {
=======
	
	public void home(EV3TouchSensor eindeloop) {
		SampleProvider sp = eindeloop.getTouchMode();
		float[] sample = new float[sp.sampleSize()];
		System.out.println("Homing " + motorName + " in progress");
		isHomed=false;
		
		while (!isHomed) {
		
>>>>>>> Homing
			homingBusy = true;
		
		    sp.fetchSample(sample, 0);
      	  	this.setSpeed(homingSpeed);
      	  	this.backward();
      	  	
	        if (sample[0] >0) {
	        	 this.stop();
	        	 this.resetTachoCount();
	        	 isHomed=true;
	        	 System.out.println("Homing " + motorName + " DONE");
	         }
		}
	}
	
	
	public void rotateTo(int inpPositionInPixels) {
		this.rotateTo((int) (inpPositionInPixels*degreesPerPixel));
	}

	
}
