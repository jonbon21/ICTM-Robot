package shaperecognition;

import lejos.utility.Delay;
import shaperecognition.library.ColorSensor;
import lejos.hardware.port.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class XYMap {
	Boolean[][] xyMap;
	public int xResolution;
	public int yResolution;

	public XYMap(int inpXRes, int inpYRes) {
		xResolution=inpXRes;
		yResolution=inpYRes;
		xyMap = new Boolean[yResolution][xResolution];
	}


	public void scan(Motor motorX, Motor motorY, ColorSensor sensor) {
		
		System.out.println("xResolution " + xResolution);
		System.out.println("yResolution " + yResolution);
		Button.waitForAnyPress();
		
		//SENSOR SETUP
		sensor.setColorIdMode();
		sensor.setFloodLight(false);
		int homingColor = sensor.getColorID();

		//SCANNING
		for(int i=0;i<yResolution;i++) {
			
			Delay.msDelay(100);
			motorX.goTo(0);
			motorY.goTo(i);
			Delay.msDelay(2000);
			System.out.println();
			
			for(int j=0;j<xResolution; j++) {
								
				//double k = Math.pow((-1), (i));
					Delay.msDelay(100);
					motorX.goTo(j);
					
				
				/*else {
					motorX.setSpeed(90);
					Delay.msDelay(100);
					
					motorX.goTo(xResolution-1-j);
					Delay.msDelay(2000);
				}*/
				//motorX.stop();
				
				Delay.msDelay(100);
				if(sensor.getColorID()== homingColor) {
					xyMap[i][j]=false;
					System.out.print("0");
				} 
				
				else {
					xyMap[i][j]=true;
					System.out.print("X");
				}
				
			} //for j
		} //for i

		
		//END SCANNING
		motorX.close();			//free up motor resources 
		motorY.close();
		Sound.beepSequence(); 
  
	} 
	
		public void moveTo(int mapPosX, int mapPosY, Motor x, Motor y) {
			
			x.rotateTo(mapPosX);
			System.out.println("X move done");
			
			y.rotateTo(mapPosY);
			System.out.println("Y move done");
	}
}
