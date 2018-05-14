package shaperecognition;  

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;


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
		
		int pixelDimensionInMM = 7;					//defines a square pixel of x by x
		double maxDistanceXInMM = 150.0; 			//maximum x and y-length of physical matrix [in mm]
		double maxDistanceYInMM = 100.0;
		double maxDistanceZInMM = 35.0;
		
		int xResolution = (int) maxDistanceXInMM/pixelDimensionInMM ; 						//set resolution manually:
		int yResolution = (int) maxDistanceYInMM/pixelDimensionInMM;	
		
		XYMap map = new XYMap(xResolution, yResolution);

		int scanningSensorHeight = 10;
		
	//***MOTORS INIT
	
		int xAxisCircumferenceDrivingWheel = 80;  //mm
		int yAxisCircumferenceDrivingWheel = 134; //mm
		
		double xAxis_DegreesPerMM = 360.0/xAxisCircumferenceDrivingWheel;
		double yAxis_DegreesPerMM = 360.0/yAxisCircumferenceDrivingWheel;
		double zAxis_DegreesPerMM= 100.0/7;	//experimentally measured
		
		double xAxis_DegreesPerPixel = xAxis_DegreesPerMM*pixelDimensionInMM;
		double yAxis_DegreesPerPixel = yAxis_DegreesPerMM*pixelDimensionInMM;
		
		
		
		final Motor motorX = new Motor(MotorPort.A, xAxis_DegreesPerPixel, "MotorX");
		final Motor motorY = new Motor(MotorPort.B, yAxis_DegreesPerPixel, "MotorY");
		final Motor motorZ = new Motor(MotorPort.C, zAxis_DegreesPerMM, "MotorZ");
		
		
	//***SENSORS INIT
		
		ColorSensor    sensor1 = new ColorSensor(SensorPort.S1);
		final EV3TouchSensor eindeloopX = new EV3TouchSensor(SensorPort.S2);
		final EV3TouchSensor eindeloopY = new EV3TouchSensor(SensorPort.S3);
	
		
	//***HOMING Routine
		System.out.println("Press any key to start HOMING");
		Button.waitForAnyPress();
		
		System.out.println("Put the Z-axis in the fully upward position." + '\n' + "Press any button to continue homing Z axis.");
		Button.waitForAnyPress();
		motorZ.home();
		
			Thread homeX = new Thread() {
				public void run() {
						motorX.home(eindeloopX, 30);
					
				}
			};
				
			Thread homeY = new Thread() {
				public void run() {
						motorY.home(eindeloopY, 0);
				}
			};
			
			homeX.start();
			homeY.start();	
			try {
				homeX.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				homeY.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	//***MAPPING Routine
	
		lcd.clear();
		lcd.refresh();
		System.out.println("Press any key to start MAPPING");
		Button.waitForAnyPress();
		lcd.clear();
		lcd.refresh();
		System.out.println("MAPPING in progress");
		
		int zScanHeight = (int) (-(maxDistanceZInMM-scanningSensorHeight));
		motorZ.goTo(zScanHeight, 20);
		
		System.out.println("Scanheight is SET");
		
		map.scan(motorX, motorY, sensor1, eindeloopX);   
		motorZ.rotateTo(0); 
		
		
	//***SHAPE IDENTIFICATION Routine
		lcd.clear();
		lcd.refresh();
		System.out.println("Press any key to start identification");
		Button.waitForAnyPress();		
		
		
		printInt(map.getMap());
		System.out.println();
		scan scanObj = new scan(map.getMap());
		int dimX = scanObj.getDimX(); int dimY = scanObj.getDimY();
		scanObj.prepare();
		scanObj.findZones(); //zone wordt gedefinieerd als aaneengesloten geheel waarbij elk element verbonden is langs boven,onder,links of rechts
		ArrayList<zone> zones = scanObj.getZoneList();	
		Collections.sort(zones, new zoneSort()); //zones sorteren volgens 'aantal elementen'
		
		for (int k = 0; k < zones.size(); k++) {
			System.out.println("aantal elementen zone " +(k+1)+ " : "  +zones.get(k).getZoneEl()); //print aantal elementen
		 	///printInt(zones.get(k).getArrayZone(dimX,dimY)); //print zones	
		}
		zone zoneLargest = new zone(zones.get(zones.size()-1));
		///System.out.println("fill holes");
		zoneLargest.fillHoles(dimX, dimY);
		///printInt(zoneLargest.getArrayZone(dimX, dimY));
		shape shapeObj = new shape(zoneLargest);
		Coordinates start = new Coordinates(shapeObj.findBoundPoint());
		shapeObj.findBound(dimX,dimY,start);
		System.out.println("edge largest zone");
		printInt(shapeObj.getArrayBound(dimX,dimY));
		shapeObj.direction();
		///System.out.println("orientatie vectoren");
		///printDouble(shapeObj.getArrayAngle(shapeObj.getDirection(),dimX,dimY));
		shapeObj.rotation();
		///System.out.println("rotatie vectoren");
		///printDouble(shapeObj.getArrayAngle(shapeObj.getRotation(),dimX,dimY));
		shapeObj.internalAngle();
		///System.out.println("inwendige hoeken");
		///printDouble(shapeObj.getArrayAngle(shapeObj.getAngle(),dimX,dimY));
		System.out.println();
		System.out.println("circumference: " +shapeObj.circumference());
		System.out.println("surface: " +shapeObj.surface());
		//System.out.println("circumferenceCheck: " +(3+3+Math.sqrt(18)));
		System.out.println();
		//double[] sumRotation = shapeObj.rotSum(0);
		//System.out.println("sum rotation vectors");
		//printDouble(shapeObj.getArrayAngle(sumRotation,dimX,dimY));
		//double[] sumRotationW = shapeObj.weight(sumRotation, 0);
		//System.out.println();
		//System.out.println("sum rotation: ");
		//printDouble(shapeObj.getArrayAngle(sumRotationW,dimX,dimY));
		//double[] internalAngleW = shapeObj.internalAngleNew(sumRotationW);
		//System.out.println();
		//System.out.println("weighted internal angles");
		//printDouble(shapeObj.getArrayAngle(internalAngleW,dimX,dimY));
		//shapeObj.determineAngles(internalAngleW);
		//System.out.println("surface - circumference ratio: " + (shapeObj.surface()/shapeObj.circumference()));
		//Coordinates P1 = new Coordinates(0,0);
		//Coordinates P2 = new Coordinates(0,1);
		//System.out.println(P1.getAngle(P2));
		//System.out.println(shapeObj.getBoundEl());
		shapeObj.calcLines();
		shapeObj.calcLineRot(true,dimX,dimY); //set mergeLines on or off
		System.out.println("lines");
		printInt(shapeObj.getArrayInt(shapeObj.getLines(),dimX,dimY));
		System.out.println();
		System.out.println("corners");
		printInt(shapeObj.getArrayCorner(dimX, dimY));
		shapeObj.internalAngleCorner();
		String shape = shapeObj.determineShape(dimX,dimY);
		System.out.println("shape = " + shape);
		System.out.println();
		if("semicircle".equals(shape)) {
			System.out.println("final corners");
			printInt(shapeObj.getArrayCorner(dimX, dimY));
		}
		ArrayList<Coordinates> corners = shapeObj.getCorner();

	//***EDGE TRACKING + SORTING Routine
		lcd.clear();
		lcd.refresh();
		System.out.println("Press any key to start EDGE TRACKING");
		Button.waitForAnyPress();
		
		
		sensor1.setFloodLight(true);
		map.track(motorX, motorY, corners);
		sensor1.setFloodLight(false);
		
		switch (shape) {       //triangle = -x direction, square = +x, plus-sign = y)
			case "triangle" :
				System.out.println("Press to start SORTING");
				Button.waitForAnyPress();
				map.sortTriangle(motorX, motorY, motorZ, corners);
				break;
				
			case "square" : 
				System.out.println("Press to start SORTING");
				Button.waitForAnyPress();
				map.sortSquare(motorX, motorY, motorZ, corners);
				break;
				
			case "cross" : 
				System.out.println("Press to start SORTING");
				Button.waitForAnyPress();
				map.sortCross(motorX, motorY, motorZ, corners);
				break;
				
			case "semicircle" : 
				System.out.println("Press to start SORTING");
				Button.waitForAnyPress();
				map.sortSemicircle(motorX, motorY, motorZ, corners);
				break;
		}
		
		
	//***END OF PROGRAM	
		lcd.clear();
		lcd.refresh();
		System.out.println("All routines have been executed.");
		System.out.println("Press to QUIT PROGRAM");
		Button.waitForAnyPress();
	}
	public static void printInt(int[][] M){
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[i].length; j++) {
			System.out.print(M[i][j] + " "); //+ "\t"
		}
		System.out.println();
	}
	}
	public static void printDouble(double[][] M){
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[i].length; j++) {    
			//time = Double.valueOf(df.format(time));
			System.out.print(String.format("%.2f", M[i][j]) + "\t");
		}
		System.out.println();
	}
	}	
}
