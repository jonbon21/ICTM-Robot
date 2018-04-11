package shaperecognition;   //hallo maarten

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.*;

public class ShapeRecognition {
	public static void main(String[] args) {
	
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Press any key to start");
		
		Button.waitForAnyPress();
		
		double xAxis_mmToDegreesConversion;
		double yAxis_mmToDegreesConversion;
		double zAxis_mmToDegreesConversion;
	
		double standardMaxMotorCurrent = 0;
		int standardHomingSpeed =  0; //in mm/s
	
	
		Motor motorX = new Motor(MotorPort.A);
		Motor motorY = new Motor(MotorPort.B);
		EV3TouchSensor eindeloopX = new EV3TouchSensor(SensorPort.S2);
		EV3TouchSensor eindeloopY = new EV3TouchSensor(SensorPort.S3);
		int maxX = 50; 					//set manually
		int maxY = 50;						//set manually
		Coordinates mapLimits = new Coordinates(maxX, maxY);
		XYMap map = new XYMap(mapLimits);
			motorX.home(eindeloopX);
			motorY.home(eindeloopY);
			
			System.out.println("Press any key to start mapping");
			Button.waitForAnyPress();
			
			
	}
	
}
