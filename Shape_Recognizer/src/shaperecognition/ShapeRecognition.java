package shaperecognition;  

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.*;
import shaperecognition.library.ColorSensor;

public class ShapeRecognition {
	public static void main(String[] args) {
	
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Press any key to start");
		
		Button.waitForAnyPress();
		
		double xAxis_DegreesPermm = 360.0/81.0;
		double yAxis_DegreesPermm = 360.0/134.0;
		double zAxis_DegreesPermm= 360.0/25.0;
		//set resolution manually:
		int maxPointsX = 6; 					
		int maxPointsY = 3;					
		
	
		Motor motorX = new Motor(MotorPort.A, xAxis_DegreesPermm, "motorX");
		Motor motorY = new Motor(MotorPort.B, yAxis_DegreesPermm, "motorY");
		ColorSensor    sensor1 = new ColorSensor(SensorPort.S1);
		EV3TouchSensor eindeloopX = new EV3TouchSensor(SensorPort.S2);
		EV3TouchSensor eindeloopY = new EV3TouchSensor(SensorPort.S3);
		XYMap map = new XYMap(maxPointsX, maxPointsY);
			
		motorX.home(eindeloopX);
		motorY.home(eindeloopY);
		
		System.out.println("Press any key to start mapping");
		Button.waitForAnyPress();
			
		map.scan(motorX, motorY, sensor1);
	}
	
}
