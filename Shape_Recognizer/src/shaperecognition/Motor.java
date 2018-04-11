package shaperecognition;

import lejos.hardware.Power;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

import java.lang.Math.*;

/* to do: if USsensor is used -> make second constructor with this sensor as parameter, and add extra condition in homing routine
 * double check or only the sensor? only the sensor is probably best*/

public class Motor extends EV3LargeRegulatedMotor {
	Boolean isHomed = false;
	Boolean homingBusy = false;
	String motorName = "unknownMotor";
	int homingSpeed = 60;
	//double mmToDegreesConversion = 1;
	
	public Motor(Port inpPort, double mmToDegreesConversion) {   //double inpNormalMotorCurrent, String inpMotorName,
		super(inpPort);
		//normalMotorCurrent = inpNormalMotorCurrent;
		//maxMotorCurrentForHoming = overcurrentLimitForHoming*normalMotorCurrent + normalMotorCurrent;
		//motorName = inpMotorName;
		//mmToDegreesConversion = inpMMToDegreesConversion;
		//homingSpeed = (int) (inpHomingSpeed*mmToDegreesConversion); //  degrees/s
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
	        	 isHomed=true;
	        	 System.out.println("Homing " + motorName + " DONE");
	         }
		}
	}

}
