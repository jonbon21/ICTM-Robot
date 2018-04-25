package shaperecognition;

import lejos.hardware.Power;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import java.lang.Math.*;


public class Motor extends EV3LargeRegulatedMotor {
	Boolean isHomed = false;
	Boolean homingBusy = false;
	String motorName;
	int homingSpeed = 80;		//in degrees/s
	double degreesPerActUnit;
	

	public Motor(Port inpPort, double inpDegreesPerActUnit, String motorName) {   //double inpNormalMotorCurrent, String inpMotorName,
		super(inpPort);
		//normalMotorCurrent = inpNormalMotorCurrent;
		//maxMotorCurrentForHoming = overcurrentLimitForHoming*normalMotorCurrent + normalMotorCurrent;
		//motorName = inpMotorName;
		degreesPerActUnit = inpDegreesPerActUnit;
		//homingSpeed = (int) (inpHomingSpeed*mmToDegreesConversion); //  degrees/s
	}
	
	
	public double getConversion() {
		return degreesPerActUnit;
	}

	public void home() {
		System.out.println("Homing " + motorName + " in progress");
		isHomed=false;
		
		while (!isHomed) {
			homingBusy = true;
	        this.resetTachoCount();
	        isHomed=true;
	        System.out.println("Homing " + motorName + " DONE");
		}
	}
	
	public void home(EV3TouchSensor eindeloop) {
		SampleProvider sp = eindeloop.getTouchMode();
		float[] sample = new float[sp.sampleSize()];
		System.out.println("Homing " + motorName + " in progress");
		isHomed=false;
		
		while (!isHomed) {
		
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
	

	
	

	
	
	public void goTo(int inpPositionInActUnits) {
		int absolutePosInDegrees = (int) (inpPositionInActUnits*degreesPerActUnit);
		this.setSpeed(120);
		Delay.msDelay(100);
		super.rotateTo(absolutePosInDegrees);
	}

	
}
