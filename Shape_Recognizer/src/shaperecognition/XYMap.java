package shaperecognition;

import lejos.utility.Delay;
import shaperecognition.library.ColorSensor;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3TouchSensor;

import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import java.lang.Math.*;


// This class contains the matrix that will be filled with the scanned data
// It contains methods used for scanning the matrix, edge tracking of the shape and the sorting routines

public class XYMap {
	int[][] xyMap;
	public int xResolution;
	public int yResolution;

	public XYMap(int inpXRes, int inpYRes) {
		xResolution=inpXRes;
		yResolution=inpYRes;
		xyMap = new int[xResolution][yResolution];
	}
	public int[][] getMap() {
		return xyMap;
	}
	
	
	public void scanMultithread (final Motor motorX, final Motor motorY, final ColorSensor sensor, EV3TouchSensor eindeloop) {

		ArrayList<ArrayList<Integer>> scan = new ArrayList<ArrayList<Integer>>();

		System.out.println("xResolution " + xResolution);
		System.out.println("yResolution " + yResolution);
		Button.waitForAnyPress();
		
		//SENSOR SETUP
		sensor.setColorIdMode();
		final int ObjectColor = sensor.getColorID();
		//int ObjectColor = 0 ;
		Delay.msDelay(2000);
		System.out.println("ObjectColor = " + ObjectColor);
		
		Button.waitForAnyPress();
		//SCANNING
		
		
		for(int i=0;i<yResolution;i++) {	
			//System.out.print("counter =" + counter);
			final ArrayList <Integer> scanline = new ArrayList<Integer>();
			if(Button.ENTER.isDown()) {
				for(int k=i; k<yResolution; k++) { 
					ArrayList <Integer> scanline2 = new ArrayList<Integer>();

					for(int j=0; j<100; j++) {
						scanline2.add(0);
					}
					scan.add(scanline2);
				}
				break;
			}
			Thread contScanning = new Thread() {
				public void run() {
					while(motorX.getTachoCount()<=700){ //set tachoCount
						try {
							Thread.sleep(25); //set delay between two consecutive measurements in ms!
							//Delay.msDelay(25);
						} catch (InterruptedException ex) {
							return;	//end execution
						}
						if(sensor.getColorID()== ObjectColor) {
						scanline.add(1);
						System.out.print(1);
						}
						else {
						scanline.add(0);
						System.out.print(0);
						}	
					}
			}	
			};
			Thread contMoving = new Thread() {
				public void run() {
					motorX.goTo(xResolution,6);
					//System.out.println("Tachocount = " + motorX.getTachoCount());
					}
			};
			Delay.msDelay(100);
			motorX.home(eindeloop,30);
			System.out.println();
			motorY.goTo(i, 4);
			Delay.msDelay(100);
			System.out.println();
			
			contMoving.start();
			contScanning.start();				
				try {
					contMoving.join(); // wait until contMoving is done
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			contScanning.interrupt(); //interrupt contScanning
			scan.add(scanline);
			//System.out.println("test: " +scan.get(i).get(2));
		}						
		
		//END SCANNING
		System.out.println();
		Sound.beepSequence(); 
		
		//compress the scanned data into the matrix XYmap. The matrix XYmap serves as input for the class scan 
		double nSample = 0; double avgRes = 0;
		for(int i=0; i<yResolution; i++) {
			nSample = (double) scan.get(i).size()/xResolution;
			for(int j=0;j<xResolution;j++) {
				for(int k=(int) Math.floor(j*nSample);k < (int) Math.ceil((j+1)*nSample-0.1);k++) {
					if(k == (int) Math.floor(j*nSample)) {
					avgRes += scan.get(i).get(k) * (Math.ceil(j*nSample+0.001)-j*nSample);} 
					else if(k == (int) Math.ceil((j+1)*nSample) -1) {
					avgRes += scan.get(i).get(k) * ((j+1)*nSample - Math.floor((j+1)*nSample-0.001));
					}
					else {avgRes += scan.get(i).get(k);}
				}
			avgRes = avgRes / nSample;
			xyMap[j][i] = (int) Math.round(avgRes);
			avgRes = 0;
			}	
		}
		for(int i =0; i<yResolution;i++) {
			System.out.println();
			for(int j =0; j<xResolution;j++) {
				System.out.print(xyMap[j][i]);
			}
		}
	}
		
	/*public void scan(Motor motorX, Motor motorY, ColorSensor sensor, EV3TouchSensor eindeloop) {
		
		Old method, using stepwise scanning. Precise but slow.
		
		System.out.println("xResolution " + xResolution);
		System.out.println("yResolution " + yResolution);
		Button.waitForAnyPress();
		
		//SENSOR SETUP
		sensor.setColorIdMode();
		int ObjectColor = sensor.getColorID();
		//int ObjectColor = 0 ;
		Delay.msDelay(2000);
		System.out.println("ObjectColor =" + ObjectColor);
		Button.waitForAnyPress();

		//SCANNING
		for(int i=0;i<yResolution;i++) {
			
			Delay.msDelay(100);
			motorX.home(eindeloop,30);
			motorY.goTo(i, 4);
			Delay.msDelay(100);
			System.out.println();
			
			for(int j=0;j<xResolution; j++) {
								
				//double k = Math.pow((-1), (i));
					Delay.msDelay(100);
					motorX.goTo(j, 4);
					
				
				//motorX.stop();
				
				Delay.msDelay(100);
				if(sensor.getColorID()== ObjectColor) {
					xyMap[j][i]=1;
					System.out.print("X");
				} 
				
				else {
					xyMap[j][i]=0;
					System.out.print("0");
				}
				
			} //for j
		} //for i
		
				
		//END SCANNING
		System.out.println();
		Sound.beepSequence(); 
  
	} */

	public void trackEdge (final Motor motorX, final Motor motorY, ArrayList <Coordinates> corners){
		final Coordinates a,d;
		final int speedInPixelsPerS = 15;
	
		double motionTimeAD = 0;
		
		a = corners.get(0);
		d = corners.get(corners.size()-1);
		
		double distanceXAD = Math.abs(a.getX() - d.getX());
		double distanceYAD = Math.abs(a.getY() - d.getY());
		
		double distanceBetweenPointsAD = Math.sqrt(Math.pow(distanceXAD, 2) + Math.pow(distanceYAD, 2));
		
		motionTimeAD = distanceBetweenPointsAD/speedInPixelsPerS;
		
		final int speedXAD = (int) Math.round(Math.abs((distanceXAD/motionTimeAD)));  
		final int speedYAD = (int) Math.round(Math.abs((distanceYAD/motionTimeAD)));
		
		System.out.println("Tracking");

	// go to first point 	
		System.out.println("Press to go to first point");
		Button.waitForAnyPress();
		Thread trackXPointA = new Thread() {
			public void run() {
					motorX.goTo(a.getX(), 4);		
			}
		};
		
		
		Thread trackYPointA = new Thread() {
			public void run() {
				motorY.goTo(a.getY(), 4);
			}
		};
		
		trackXPointA.start();
		trackYPointA.start();
		
		
				//wait for this movement to finish
					try {
						trackXPointA.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						trackYPointA.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		Delay.msDelay(2000);
	// cycle through the other points
		for(int i=0;i<(corners.size()-1); i++) {			
			final Coordinates k,l;
			double motionTime = 0;
			
			k = corners.get(i);
			l = corners.get(i+1);
			
			double distanceX = Math.abs(l.getX() - k.getX());
			double distanceY = Math.abs(l.getY() - k.getY());
			
			double distanceBetweenPoints = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
			
			motionTime = distanceBetweenPoints/speedInPixelsPerS;
			
			final int speedX = (int) Math.round(Math.abs((distanceX/motionTime)));  
			final int speedY = (int) Math.round(Math.abs((distanceY/motionTime)));
			
			Thread trackXPoint = new Thread() {
				public void run() {
						motorX.goTo(l.getX(), speedX);	
				}
			};
			
			
			Thread trackYPoint = new Thread() {
				public void run() {
					motorY.goTo(l.getY(), speedY);
				}
			};
			
			trackXPoint.start();
			trackYPoint.start();
			
			
			
					//wait for this movement to finish
						try {
							trackXPoint.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							trackYPoint.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			
			
			
		}		
	
	
		// track fourth edge (go back to start point)		
		Thread trackXPointAReturn = new Thread() {
			public void run() {
					motorX.goTo(a.getX(), speedXAD);		
			}
		};
		
		
		Thread trackYPointAReturn = new Thread() {
			public void run() {
				motorY.goTo(a.getY(), speedYAD);
			}
		};
		
		trackXPointAReturn.start();
		trackYPointAReturn.start();
		
		
				//wait for this movement to finish
					try {
						trackXPointAReturn.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						trackYPointAReturn.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
		
}
	
	public void sortTriangle(final Motor motorX, final Motor motorY, final Motor motorZ, ArrayList <Coordinates> corners) {
		
		int sumX=0;
		int sumY=0;		
		
			for(int i=0 ; i<(corners.size()); i++) {
				final Coordinates z;
				
				z=corners.get(i);
				
				sumX = sumX +z.getX();
				sumY = sumY +z.getY();
			}
			
		final int centreOfGravityX = sumX/corners.size(); //zwaartepuntX = (x1+x2+x3)/3
		final int centreOfGravityY = sumY/corners.size(); //zwaartepuntY = (y1+y2+y3)/3
		
		Thread gotoCentreOfGravityX = new Thread() {
			public void run() {
				motorX.goTo(centreOfGravityX, 4);		
			}
		};
		
		
		Thread gotoCentreOfGravityY = new Thread() {
			public void run() {
				motorY.goTo(centreOfGravityY, 4);
			}
		};
		
		gotoCentreOfGravityX.start();
		gotoCentreOfGravityY.start();
		
		
				//wait for this movement to finish
					try {
						gotoCentreOfGravityX.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						gotoCentreOfGravityY.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		motorX.goTo(xResolution, 4); //motor helemaal naar het einde laten gaan
		motorZ.rotateTo(-500);//z-as laten zakken. Heb zelf een waarde ingevuld, aangezien ik denk dat de 'goTo()' methode niet werkt voor de z-motor?
		motorX.goTo(0, 4);
		motorZ.rotateTo(0);
	}
	
	public void sortSquare(final Motor motorX, final Motor motorY, final Motor motorZ, ArrayList <Coordinates> corners) {
			
			int sumX=0;
			int sumY=0;		
			
				for(int i=0 ; i<(corners.size()); i++) {
					final Coordinates z;
					
					z=corners.get(i);
					
					sumX = sumX +z.getX();
					sumY = sumY +z.getY();
				}
				
			final int centreOfGravityX = sumX/corners.size(); 
			final int centreOfGravityY = sumY/corners.size(); 
			
			Thread gotoCentreOfGravityX = new Thread() {
				public void run() {
					motorX.goTo(centreOfGravityX, 4);		
				}
			};
			
			
			Thread gotoCentreOfGravityY = new Thread() {
				public void run() {
					motorY.goTo(centreOfGravityY, 4);
				}
			};
			
			gotoCentreOfGravityX.start();
			gotoCentreOfGravityY.start();
			
			
					//wait for this movement to finish
						try {
							gotoCentreOfGravityX.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							gotoCentreOfGravityY.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
			motorX.goTo(0, 4); 
			motorZ.rotateTo(-500);
			motorX.goTo(xResolution, 4);
			motorZ.rotateTo(0);
		}
	
	public void sortCross(final Motor motorX, final Motor motorY, final Motor motorZ, ArrayList <Coordinates> corners) {
		
		int sumX=0;
		int sumY=0;		
		
			for(int i=0 ; i<(corners.size()); i++) {
				final Coordinates z;
				
				z=corners.get(i);
				
				sumX = sumX +z.getX();
				sumY = sumY +z.getY();
			}
			
		final int centreOfGravityX = sumX/corners.size(); 
		final int centreOfGravityY = sumY/corners.size(); 
		
		Thread gotoCentreOfGravityX = new Thread() {
			public void run() {
				motorX.goTo(centreOfGravityX, 4);		
			}
		};
		
		
		Thread gotoCentreOfGravityY = new Thread() {
			public void run() {
				motorY.goTo(centreOfGravityY, 4);
			}
		};
		
		gotoCentreOfGravityX.start();
		gotoCentreOfGravityY.start();
		
		
				//wait for this movement to finish
					try {
						gotoCentreOfGravityX.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						gotoCentreOfGravityY.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		motorY.goTo(yResolution, 4); 
		motorZ.rotateTo(-500);
		motorY.goTo(0, 4);
		motorZ.rotateTo(0);
	}
	
	public void sortSemicircle(final Motor motorX, final Motor motorY, final Motor motorZ, ArrayList <Coordinates> corners) {
			
			int sumX=0;
			int sumY=0;		
			
				for(int i=0 ; i<(corners.size()); i++) {
					final Coordinates z;
					
					z=corners.get(i);
					
					sumX = sumX +z.getX();
					sumY = sumY +z.getY();
				}
				
			final int centreOfGravityX = sumX/corners.size(); 
			final int centreOfGravityY = sumY/corners.size(); 
			
			Thread gotoCentreOfGravityX = new Thread() {
				public void run() {
					motorX.goTo(centreOfGravityX, 4);		
				}
			};
			
			
			Thread gotoCentreOfGravityY = new Thread() {
				public void run() {
					motorY.goTo(centreOfGravityY, 4);
				}
			};
			
			gotoCentreOfGravityX.start();
			gotoCentreOfGravityY.start();
			
			
					//wait for this movement to finish
						try {
							gotoCentreOfGravityX.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							gotoCentreOfGravityY.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
			motorY.goTo(0, 4); 
			motorZ.rotateTo(-500);
			motorY.goTo(yResolution, 4);
			motorZ.rotateTo(0);
		}

}
