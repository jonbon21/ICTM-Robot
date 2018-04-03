package shaperecognition;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.*;

public class ShapeRecognition {
	
	double xAxis_mmToDegreesConversion;
	double yAxis_mmToDegreesConversion;
	double zAxis_mmToDegreesConversion;
	
	double standardMaxMotorCurrent = 0;
	int standardHomingSpeed =  0; //in mm/s
	
	
	Motor motorAxisX = new Motor(MotorPort.A, standardHomingSpeed, xAxis_mmToDegreesConversion);
	int maxX = 1000; 					//set manually
	int maxY = 500;						//set manually
	Coordinates mapLimits = new Coordinates(maxX, maxY);
	XYMap map = new XYMap(mapLimits);
	
	
	
	
	
/*	
	while (Button.ESCAPE.isUp()) {   //algemene while, programma blijft lopen zolang niet op escape wordt geduwd
    
		public static void main() {
			Button.waitForAnyPress();   //soort startknop
		
			if not homed   //routin om alle axes the homen INDIEN nog niet gehomed 
			
			motorAxisX.home(); //homing routine van elke motor aanroepen
			
			if homed  //
			
			if sensorpos != encoderposition = not homed
		}
} */
}
