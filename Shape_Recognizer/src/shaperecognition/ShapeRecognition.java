package shaperecognition;  

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.*;
import lejos.hardware.motor.*;
import shaperecognition.library.ColorSensor;

public class ShapeRecognition {
	public static void main(String[] args) {
	
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Initializing");
		
		
		double xAxis_DegreesPermm = 360.0/81.0; 	//81 is the circumference of the driving wheel
		double yAxis_DegreesPermm = 360.0/134.0;	//134 is the circumference of the driving wheel
		double zAxis_DegreesPermm= 360.0/22.0; 		//25 is the circumference of the driving wheel
		//set resolution manually:
		int maxPointsX = 6; 					
		int maxPointsY = 3;	
		
	
	
		Motor motorX = new Motor(MotorPort.A, xAxis_DegreesPermm, "motorX");
		Motor motorY = new Motor(MotorPort.B, yAxis_DegreesPermm, "motorY");
		Motor motorZ = new Motor(MotorPort.C, zAxis_DegreesPermm, "motorZ");
		ColorSensor    sensor1 = new ColorSensor(SensorPort.S1);
		EV3TouchSensor eindeloopX = new EV3TouchSensor(SensorPort.S2);
		EV3TouchSensor eindeloopY = new EV3TouchSensor(SensorPort.S3);
		XYMap map = new XYMap(maxPointsX, maxPointsY);
			
		motorX.home(eindeloopX);
		motorY.home(eindeloopY);
		

		
		System.out.println("Press any key to start mapping");
		Button.waitForAnyPress();
		
			
		map.scan(motorX, motorY, sensor1);    
		
		String shape= "triangle";
		System.out.println("The shape is a " +shape);
		
		System.out.println("Press any key to start SORTING");
		Button.waitForAnyPress();
		
				
		switch (shape) {       //triangle = -x direction, semicircle = +x, square = -y, plus-sign = y)
			case "triangle" :
				System.out.println(motorX.getTachoCount());
				System.out.println(motorY.getTachoCount());
				map.moveTo(0,0, motorX, motorY);
				
				System.out.println("Press any key to start Z AXIS MOVE");
				Button.waitForAnyPress();
				
				motorZ.rotate((int) (-20*zAxis_DegreesPermm));
				motorZ.stop();
		}
	}
	
}
