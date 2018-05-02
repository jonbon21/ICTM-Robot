package shaperecognition;

import lejos.utility.Delay;
import shaperecognition.library.ColorSensor;
import lejos.hardware.port.*;
import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import java.lang.Math.*;


public class XYMap {
	int[][] xyMap;
	public int xResolution;
	public int yResolution;

	public XYMap(int inpXRes, int inpYRes) {
		xResolution=inpXRes;
		yResolution=inpYRes;
		xyMap = new int[yResolution][xResolution];
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
			motorX.goTo(0, 4);
			motorY.goTo(i, 4);
			Delay.msDelay(2000);
			System.out.println();
			
			for(int j=0;j<xResolution; j++) {
								
				//double k = Math.pow((-1), (i));
					Delay.msDelay(100);
					motorX.goTo(j, 4);
					
				
				/*else {
					motorX.setSpeed(90);
					Delay.msDelay(100);
					
					motorX.goTo(xResolution-1-j);
					Delay.msDelay(2000);
				}*/
				//motorX.stop();
				
				Delay.msDelay(100);
				if(sensor.getColorID()== homingColor) {
					xyMap[i][j]=0;
					System.out.print("0");
				} 
				
				else {
					xyMap[i][j]=1;
					System.out.print("X");
				}
				
			} //for j
		} //for i
		
				
		//END SCANNING
		motorX.close();			//free up motor resources 
		motorY.close();
		Sound.beepSequence(); 
  
	} 
	
	
	
	
	
	public void trackTriangle(final Motor motorX, final Motor motorY, ArrayList <Coordinates> corners){
		final Coordinates a,b,c;
		final int speedInPixelsPerS = 4;
		
		double motionTimeBA = 0;
		double motionTimeCB = 0;
		double motionTimeAC = 0;

		a = corners.get(0);
		b = corners.get(1);
		c = corners.get(2);
		
		double distanceXBA = Math.abs(b.getX() - a.getX());
		double distanceYBA = Math.abs(b.getY() - a.getY());
		
		double distanceXCB = Math.abs(c.getX() - b.getX());
		double distanceYCB = Math.abs(c.getY() - b.getY());
		
		double distanceXAC = Math.abs(a.getX() - c.getX());
		double distanceYAC = Math.abs(a.getY() - c.getY());
		
		
		double distanceBetweenPointsBA = Math.sqrt(Math.pow(distanceXBA, 2) + Math.pow(distanceYBA, 2));
		double distanceBetweenPointsCB = Math.sqrt(Math.pow(distanceXCB, 2) + Math.pow(distanceYCB, 2));
		double distanceBetweenPointsAC = Math.sqrt(Math.pow(distanceXAC, 2) + Math.pow(distanceYAC, 2));
		
		
		motionTimeBA = distanceBetweenPointsBA/speedInPixelsPerS;   //s
		motionTimeCB = distanceBetweenPointsCB/speedInPixelsPerS;
		motionTimeAC = distanceBetweenPointsAC/speedInPixelsPerS;
		
				
		final int speedXBA = (int) Math.abs((distanceXBA/motionTimeBA));  
		final int speedYBA = (int) Math.abs((distanceYBA/motionTimeBA));
		
		final int speedXCB = (int) Math.abs((distanceXBA/motionTimeCB));  
		final int speedYCB = (int) Math.abs((distanceYBA/motionTimeCB));
		
		final int speedXAC = (int) Math.abs((distanceXAC/motionTimeAC));  
		final int speedYAC = (int) Math.abs((distanceYAC/motionTimeAC));
		
		
		
		System.out.println("Tracking Triangle");

		
		
	// go to first point 	
		
		Thread trackTriangleXPointA = new Thread() {
			public void run() {
					motorX.goTo(a.getX(), 4);		
			}
		};
		
		
		Thread trackTriangleYPointA = new Thread() {
			public void run() {
				motorY.goTo(a.getY(), 4);
			}
		};
		
		trackTriangleXPointA.start();
		trackTriangleYPointA.start();
		
		
				//wait for this movement to finish
					try {
						trackTriangleXPointA.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						trackTriangleYPointA.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
		System.out.println("Sensor in point A");
		
		
	
		
	// track first edge
		
		Thread trackTriangleXPointB = new Thread() {
			public void run() {
					motorX.goTo(b.getX(), speedXBA);	
			}
		};
		
		
		Thread trackTriangleYPointB = new Thread() {
			public void run() {
				motorY.goTo(b.getY(), speedYBA);
			}
		};
		
		trackTriangleXPointB.start();
		trackTriangleYPointB.start();
		
		
		
				//wait for this movement to finish
					try {
						trackTriangleXPointB.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						trackTriangleYPointB.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
		
					System.out.println("Sensor in point B");
					
					

 // track second edge
		
		Thread trackTriangleXPointC = new Thread() {
			public void run() {
					motorX.goTo(b.getX(), speedXCB);	
			}
		};
		
		
		Thread trackTriangleYPointC = new Thread() {
			public void run() {
				motorY.goTo(b.getY(), speedYCB);
			}
		};
		
		trackTriangleXPointC.start();
		trackTriangleYPointC.start();
		
		
		
				//wait for this movement to finish
					try {
						trackTriangleXPointC.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						trackTriangleYPointC.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
		
					System.out.println("Sensor in point C");


					
					
					
					
		// track third edge (go back to start point) 	
					
		Thread trackTriangleXPointAReturn = new Thread() {
			public void run() {
					motorX.goTo(a.getX(), speedXAC);		
			}
		};
		
		
		Thread trackTriangleYPointAReturn = new Thread() {
			public void run() {
				motorY.goTo(a.getY(), speedYAC);
			}
		};
		
		trackTriangleXPointAReturn.start();
		trackTriangleYPointAReturn.start();
		
		
				//wait for this movement to finish
					try {
						trackTriangleXPointAReturn.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						trackTriangleYPointAReturn.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
		System.out.println("Sensor back in point A");
		
		
				
								

}
	
	
}
