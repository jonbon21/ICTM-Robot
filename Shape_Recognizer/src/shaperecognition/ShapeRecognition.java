package shaperecognition;  

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;
import lejos.hardware.port.*;
import lejos.hardware.motor.*;
import shaperecognition.library.ColorSensor;

public class ShapeRecognition {
	public static void main(String[] args) {
	
		
	//***INITIALISATION
		
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		Brick		brick = BrickFinder.getLocal();
		TextLCD		lcd = brick.getTextLCD(Font.getFont(0, 0, Font.SIZE_MEDIUM));
		System.out.println("INITIALISATION in progress");

		
	//***MAP INIT	
		
		int pixelDimensionInMM = 5;					//defines a square pixel of x by x
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
		int zAxisCircumferenceDrivingWheel = 20;  //mm
		
		double xAxis_DegreesPerMM = 360.0/xAxisCircumferenceDrivingWheel;
		double yAxis_DegreesPerMM = 360.0/yAxisCircumferenceDrivingWheel;
		double zAxis_DegreesPerMM= 360.0/zAxisCircumferenceDrivingWheel;
		
		double xAxis_DegreesPerPixel = xAxis_DegreesPerMM/pixelDimensionInMM;
		double yAxis_DegreesPerPixel = yAxis_DegreesPerMM/pixelDimensionInMM;
		
		
		
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
		lcd.clear();
		lcd.refresh();
		System.out.println("MAPPING in progress");
		
		int zScanHeight = (int) (-(maxDistanceZInMM-scanningSensorHeight));
		System.out.println("Scanheight = " + '\n' + zScanHeight);
		
		Delay.msDelay(5000);
		Button.waitForAnyPress();
		
		motorZ.rotateTo(zScanHeight);
		
		System.out.println("Scanheight is SET");
		
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
