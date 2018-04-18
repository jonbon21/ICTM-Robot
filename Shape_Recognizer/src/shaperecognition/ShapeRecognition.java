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
	
		
	//***INITIALISATION
		
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("INITIALISATION in progress");

		
	//***MAP INIT	
		
		int pixelDimensionInMM = 5;
		double maxDistanceXInMM = 200.0; 			//maximum x and y-length of physical matrix [in mm]
		double maxDistanceYInMM = 120.0;
		double maxDistanceZInMM = 35.0;
		
		int xResolution = (int) maxDistanceXInMM/pixelDimensionInMM ; 						//set resolution manually:
		int yResolution = (int) maxDistanceYInMM/pixelDimensionInMM;	
		
		XYMap map = new XYMap(xResolution, yResolution);		

		int scanningSensorHeight = 10;
		
	//***MOTORS INIT
	
		int xAxisCircumferenceDrivingWheel = 81;  //mm
		int yAxisCircumferenceDrivingWheel = 134; //mm
		int zAxisCircumferenceDrivingWheel = 22;  //mm
		double xAxis_DegreesPerPixel = 360.0/xAxisCircumferenceDrivingWheel; 	//81 is the circumference of the driving wheel
		double yAxis_DegreesPerPixel = 360.0/yAxisCircumferenceDrivingWheel;	//134 is the circumference of the driving wheel
		double zAxis_DegreesPerMM= 360.0/zAxisCircumferenceDrivingWheel; 	//25 is the circumference of the driving wheel
		
		
		Motor motorX = new Motor(MotorPort.A, xAxis_DegreesPerPixel, "MotorX");
		Motor motorY = new Motor(MotorPort.B, yAxis_DegreesPerPixel, "MotorY");
		Motor motorZ = new Motor(MotorPort.C, zAxis_DegreesPerMM, "MotorZ");
		
		
	//***SENSORS INIT
		
		ColorSensor    sensor1 = new ColorSensor(SensorPort.S1);
		EV3TouchSensor eindeloopX = new EV3TouchSensor(SensorPort.S2);
		EV3TouchSensor eindeloopY = new EV3TouchSensor(SensorPort.S3);
	
		
	//***HOMING Routine
		
		motorX.home(eindeloopX);
		motorY.home(eindeloopY);
		
		System.out.println("Put the Z-axis in the fully upward position." + '\n' + "Press any button to continue homing Z axis.");
		Button.waitForAnyPress();
		motorY.home();
		
	//***MAPPING Routine
		
		System.out.println("Press any key to start mapping");
		Button.waitForAnyPress();
		System.out.println("MAPPING in progress");
		motorZ.rotateTo((int)(maxDistanceZInMM-scanningSensorHeight));
		
		map.scan(motorX, motorY, sensor1);    
		
		
	//***SHAPE IDENTIFICATION Routine
		String shape= "triangle";
		System.out.println("The shape is a " +shape);
		
		
	//***SORTING Routine
		System.out.println("Press any key to start SORTING");
		Button.waitForAnyPress();
		System.out.println("SORTING in progress");
		
		switch (shape) {       //triangle = -x direction, semicircle = +x, square = -y, plus-sign = y)
			case "triangle" :
				map.moveTo(0,0, motorX, motorY);
				
				System.out.println("Press any key to start Z AXIS MOVE");
				Button.waitForAnyPress();
				
				motorZ.rotate((int) (-20*zAxis_DegreesPerMM));
				motorZ.stop();
		}
		
		
	//***END OF PROGRAM	
		System.out.println("All routines have been executed.");
		System.out.println("Press any key to start QUIT PROGRAM");
		Button.waitForAnyPress();
	}
	
}
