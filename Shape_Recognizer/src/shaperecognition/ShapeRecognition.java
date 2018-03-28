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
	double standardHomingSpeed =   //in mm/s
	
	
	Motor xAxis = new Motor(Port A, standardMaxMotorCurrent, )
	int maxX = 1000; 					//set manually
	int maxY = 500;						//set manually
	Coordinates mapLimits = new Coordinates(maxX, maxY);
	XYMap map = new XYMap(mapLimits);
	
	while (Button.ESCAPE.isUp()) {
    
		public static void main() {
			Button.waitForAnyPress();
		
			if not homed
			
			map.home();
			
			if homed
			
			if sensorpos != encoderposition = not homed
		}
}
}
