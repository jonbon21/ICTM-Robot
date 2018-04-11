package shaperecognition;

import lejos.utility.Delay;
import shaperecognition.library.ColorSensor;
import lejos.hardware.port.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class XYMap {
	Boolean[][] xyMap;
	public int maxX;
	public int maxY;


	public XYMap(int X, int Y) {
		maxX=X;
		maxY=Y;
		xyMap = new Boolean[maxX][maxY];
	}


	public void scan(Motor motorX, Motor motorY, ColorSensor sensor) {
		sensor.setColorIdMode();
		sensor.setFloodLight(false);
		int homingColor = sensor.getColorID();
    
		for(int i=1;i<maxY+1;i++) {
			for(int j=1;j<maxX+1; j++) {
				Delay.msDelay(250);
				if(sensor.getColorID()== homingColor) {
					xyMap[i-1][j-1]=false;
				} 
				else {
					xyMap[i-1][j-1]=true;
    			
				}
				Delay.msDelay(250);
				motorY.setSpeed(90);
				double k = Math.pow((-1), (i-1));
				int x = (int) k;
				motorX.rotate(x*120);
				motorX.stop();
    		
			}
			motorY.rotate(60);
			motorY.stop();      	
		}
		for(int i=0; i<maxX;i++) {
			System.out.println();
			for(int j=0; j<maxY;j++) {
				System.out.print("[" + xyMap [i][j] + "]");
			}
		}
		// free up motor resources. 
		motorX.close(); 
		motorY.close();
		Button.waitForAnyPress();
		Sound.beepSequence(); // we are done.
  
	} 
}
