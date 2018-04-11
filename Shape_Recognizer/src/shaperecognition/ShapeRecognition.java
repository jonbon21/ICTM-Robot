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
		
		double xAxis_mmPerDegree = 0.225;
		double yAxis_mmPerDegree = 0.372222;
		double zAxis_mmPerDegree= 0.06944444;
		int maxX = 5; 					//set manually
		int maxY = 5;					//set manually
		
	
		Motor motorX = new Motor(MotorPort.A, xAxis_mmPerDegree);
		Motor motorY = new Motor(MotorPort.B, yAxis_mmPerDegree);
		ColorSensor    sensor1 = new ColorSensor(SensorPort.S1);
		EV3TouchSensor eindeloopX = new EV3TouchSensor(SensorPort.S2);
		EV3TouchSensor eindeloopY = new EV3TouchSensor(SensorPort.S3);
		XYMap map = new XYMap(maxX, maxY);
			
		motorX.home(eindeloopX);
		motorY.home(eindeloopY);
		
		System.out.println("Press any key to start mapping");
		Button.waitForAnyPress();
			
		map.scan(motorX, motorY, sensor1);
	}
	
}
