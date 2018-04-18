package shaperecognition;

import lejos.utility.Delay;
import shaperecognition.library.ColorSensor;
import lejos.hardware.port.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class XYMap {
	Boolean[][] xyMap;
	public int maxPointsX;
	public int maxPointsY;
	//maximum x and y-length of physical matrix [in mm]
	public double maxLengthX = 200.0; 
	public double maxLengthY = 120.0;

	public XYMap(int X, int Y) {
		maxPointsX=X;
		maxPointsY=Y;
		xyMap = new Boolean[maxPointsY][maxPointsX];
	}


	public void scan(Motor motorX, Motor motorY, ColorSensor sensor) {
		sensor.setColorIdMode();
		sensor.setFloodLight(false);
		int homingColor = sensor.getColorID();
		int DegreesPerStepX = (int)((maxLengthX/maxPointsX)*motorX.getConversion()); 
		int DegreesPerStepY = (int)((maxLengthY/maxPointsY)*motorY.getConversion()); 
		for(int i=0;i<maxPointsY;i++) {
			for(int j=0;j<maxPointsX; j++) {
				Delay.msDelay(100);
				if(sensor.getColorID()== homingColor) {
					xyMap[i][j]=false;
					System.out.print("0");
				} 
				else {
					xyMap[i][j]=true;
					System.out.print("X");
				}
				Delay.msDelay(100);
				motorY.setSpeed(90);
				double k = Math.pow((-1), (i));
				int x = (int) k;
				motorX.rotate(x*DegreesPerStepX);
				motorX.stop();
    		
			}
			System.out.println();
			motorY.rotate(DegreesPerStepY);
			motorY.stop();      	
		}

		// free up motor resources. 
		motorX.close(); 
		motorY.close();
		Button.waitForAnyPress();
		Sound.beepSequence(); // we are done.
  
	} 
	
public void moveTo(int mapPosX, int mapPosY, Motor x, Motor y) {
		int DegreesPerStepX = (int)((maxLengthX/maxPointsX)*x.getConversion()); 
		int DegreesPerStepY = (int)((maxLengthY/maxPointsY)*y.getConversion());
		System.out.println("Y Conversion constant:" + DegreesPerStepY);
		x.rotateTo(DegreesPerStepX*mapPosX);
		System.out.println("X move done");
		y.rotateTo(DegreesPerStepY*mapPosY);
		System.out.println("Y move done");
	}
}
