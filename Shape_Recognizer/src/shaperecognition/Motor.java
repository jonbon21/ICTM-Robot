package shaperecognition;

import lejos.hardware.Power;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import java.lang.Math.*;

/* to do: if USsensor is used -> make second constructor with this sensor as parameter, and add extra condition in homing routine
 * double check or only the sensor? only the sensor is probably best*/

public class Motor extends EV3LargeRegulatedMotor implements Power {
	Boolean isHomed = false;
	Boolean homingBusy = false;
	String motorName = "unknownMotor";
	int homingSpeed = 10;
	double normalMotorCurrent = 0;
	double maxMotorCurrentForHoming = 0;
	double overcurrentLimitForHoming = 0.10;
	double mmToDegreesConversion = 1;
	
	public Motor(Port inpPort, int inpHomingSpeed, double inpMMToDegreesConversion) {   //double inpNormalMotorCurrent, String inpMotorName,
		super(inpPort);
		//normalMotorCurrent = inpNormalMotorCurrent;
		maxMotorCurrentForHoming = overcurrentLimitForHoming*normalMotorCurrent + normalMotorCurrent;
		//motorName = inpMotorName;
		mmToDegreesConversion = inpMMToDegreesConversion;
		homingSpeed = (int) (inpHomingSpeed*mmToDegreesConversion); //  degrees/s
	}

	public void home() {
		while (!isHomed) {
			homingBusy = true;
			System.out.println("Homing " + motorName + " IN PROGRESS");
			this.setSpeed(homingSpeed);
		    this.backward();
		    
		    if (this.getMotorCurrent() > maxMotorCurrentForHoming) {
		    	this.stop();
		    	System.out.println("Homing " + motorName + " DONE");
		    	isHomed = true;
	    		homingBusy = false;
	    		this.resetTachoCount();
		    }
		}	
	}
		
	
	@Override
	public int getVoltageMilliVolt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getBatteryCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMotorCurrent() {
		return this.getMotorCurrent();
	}

}
