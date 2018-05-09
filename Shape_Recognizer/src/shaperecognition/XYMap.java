package shaperecognition;

import lejos.utility.Delay;
import shaperecognition.library.ColorSensor;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3TouchSensor;

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
		xyMap = new int[xResolution][yResolution];
	}
	public int[][] getMap() {
		return xyMap;
	}
	
	
	public void scan2 (final Motor motorX, final Motor motorY, final ColorSensor sensor) {

		final int varX = (int) motorX.getConversion() ;
		final int varY =0 ;
		
		//SENSOR SETUP
		sensor.setColorIdMode();
		final int homingColor = sensor.getColorID();
		
		//matrix 	
		final Thread matrix = new Thread() {
			public void run() {
			for(int i=0;i<xResolution;i++) {	
				motorX.goTo(xResolution, 2);	
						/*Delay.msDelay(100);
						motorX.goTo(0, 4);
						motorY.goTo(i, 4);
						Delay.msDelay(2000);
						motorX.goTo(xResolution, 1);*/
			}
		
			Sound.beepSequence(); 	
			}
		};
		
		
		//color detection 
		Thread color = new Thread() {
			public void run() {
			int i = 0;
			int j =0 ;
			while(i<xResolution && j<yResolution) {
				if(motorX.getTachoCount()==(varX*i)) {
					if(sensor.getColorID()== homingColor) {
						xyMap[i][j]=0;
						System.out.print("0");
						i++;
					} 
					if(sensor.getColorID()!= homingColor) {
						xyMap[i][j]=1;
						System.out.print("X");
						i++;
					}
					
					
				}
				
			}
			}
			
		};
		
		
		
		color.start();
		matrix.start();
		
				//wait for this movement to finish
					try {
						color.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						matrix.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
		
		}
	public void scan(Motor motorX, Motor motorY, ColorSensor sensor, EV3TouchSensor eindeloop) {
		
		System.out.println("xResolution " + xResolution);
		System.out.println("yResolution " + yResolution);
		Button.waitForAnyPress();
		
		//SENSOR SETUP
		sensor.setColorIdMode();
		int homingColor = sensor.getColorID();

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
					
				
				/*else {
					motorX.setSpeed(90);
					Delay.msDelay(100);
					
					motorX.goTo(xResolution-1-j);
					Delay.msDelay(2000);
				}*/
				//motorX.stop();
				
				Delay.msDelay(100);
				if(sensor.getColorID()== homingColor) {
					xyMap[j][i]=0;
					System.out.print("0");
				} 
				
				else {
					xyMap[j][i]=1;
					System.out.print("X");
				}
				
			} //for j
		} //for i
		
				
		//END SCANNING
		System.out.println();
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
		
		final int speedXCB = (int) Math.abs((distanceXCB/motionTimeCB));  
		final int speedYCB = (int) Math.abs((distanceYCB/motionTimeCB));
		
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
					
		Delay.msDelay(2000);
		
	// cycle through the other points
		for(int i=0;i<2; i++) {			
			final Coordinates k,l;
			double motionTime = 0;
			
			k = corners.get(i);
			l = corners.get(i+1);
			
			double distanceX = Math.abs(l.getX() - k.getX());
			double distanceY = Math.abs(l.getY() - k.getY());
			
			double distanceBetweenPoints = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
			
			motionTime = distanceBetweenPoints/speedInPixelsPerS;
			
			final int speedX = (int) Math.abs((distanceX/motionTime));  
			final int speedY = (int) Math.abs((distanceY/motionTime));
			
			Thread trackTriangleXPoint = new Thread() {
				public void run() {
						motorX.goTo(l.getX(), speedX);	
				}
			};
			
			
			Thread trackTriangleYPoint = new Thread() {
				public void run() {
					motorY.goTo(l.getY(), speedY);
				}
			};
			
			trackTriangleXPoint.start();
			trackTriangleYPoint.start();
			
			
			
					//wait for this movement to finish
						try {
							trackTriangleXPoint.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							trackTriangleYPoint.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			
			
			
		}		
	
	/*	
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
					motorX.goTo(c.getX(), speedXCB);	
			}
		};
		
		
		Thread trackTriangleYPointC = new Thread() {
			public void run() {
				motorY.goTo(c.getY(), speedYCB);
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


					
					
					
		*/			
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
		
		
}
	
	public void trackSquare(final Motor motorX, final Motor motorY, ArrayList <Coordinates> corners){
		final Coordinates a,b,c,d;
		final int speedInPixelsPerS = 4;
	
		double motionTimeAD = 0;
		
		a = corners.get(0);
		b = corners.get(1);
		c = corners.get(2);
		d = corners.get(3);
		
		double distanceXAD = Math.abs(a.getX() - d.getX());
		double distanceYAD = Math.abs(a.getY() - d.getY());
		
		double distanceBetweenPointsAD = Math.sqrt(Math.pow(distanceXAD, 2) + Math.pow(distanceYAD, 2));
		
		motionTimeAD = distanceBetweenPointsAD/speedInPixelsPerS;
		
		final int speedXAD = (int) Math.abs((distanceXAD/motionTimeAD));  
		final int speedYAD = (int) Math.abs((distanceYAD/motionTimeAD));
		
		System.out.println("Tracking Square");

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
		for(int i=0;i<3; i++) {			
			final Coordinates k,l;
			double motionTime = 0;
			
			k = corners.get(i);
			l = corners.get(i+1);
			
			double distanceX = Math.abs(l.getX() - k.getX());
			double distanceY = Math.abs(l.getY() - k.getY());
			
			double distanceBetweenPoints = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
			
			motionTime = distanceBetweenPoints/speedInPixelsPerS;
			
			final int speedX = (int) Math.abs((distanceX/motionTime));  
			final int speedY = (int) Math.abs((distanceY/motionTime));
			
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
	
	public void trackCross(final Motor motorX, final Motor motorY, ArrayList <Coordinates> corners){
		final Coordinates a,d;
		final int speedInPixelsPerS = 4;
	
		double motionTimeAD = 0;
		
		a = corners.get(0);
		d = corners.get(11);
		
		double distanceXAD = Math.abs(a.getX() - d.getX());
		double distanceYAD = Math.abs(a.getY() - d.getY());
		
		double distanceBetweenPointsAD = Math.sqrt(Math.pow(distanceXAD, 2) + Math.pow(distanceYAD, 2));
		
		motionTimeAD = distanceBetweenPointsAD/speedInPixelsPerS;
		
		final int speedXAD = (int) Math.abs((distanceXAD/motionTimeAD));  
		final int speedYAD = (int) Math.abs((distanceYAD/motionTimeAD));
		
		System.out.println("Tracking Cross");

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
		for(int i=0;i<11; i++) {			
			final Coordinates k,l;
			double motionTime = 0;
			
			k = corners.get(i);
			l = corners.get(i+1);
			
			double distanceX = Math.abs(l.getX() - k.getX());
			double distanceY = Math.abs(l.getY() - k.getY());
			
			double distanceBetweenPoints = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
			
			motionTime = distanceBetweenPoints/speedInPixelsPerS;
			
			final int speedX = (int) Math.abs((distanceX/motionTime));  
			final int speedY = (int) Math.abs((distanceY/motionTime));
			
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
	
	public void trackSemicircle(final Motor motorX, final Motor motorY, ArrayList <Coordinates> corners){
		final Coordinates a,d;
		final int speedInPixelsPerS = 4;
	
		double motionTimeAD = 0;
		
		a = corners.get(0);
		d = corners.get(4);
		
		double distanceXAD = Math.abs(a.getX() - d.getX());
		double distanceYAD = Math.abs(a.getY() - d.getY());
		
		double distanceBetweenPointsAD = Math.sqrt(Math.pow(distanceXAD, 2) + Math.pow(distanceYAD, 2));
		
		motionTimeAD = distanceBetweenPointsAD/speedInPixelsPerS;
		
		final int speedXAD = (int) Math.abs((distanceXAD/motionTimeAD));  
		final int speedYAD = (int) Math.abs((distanceYAD/motionTimeAD));
		
		System.out.println("Tracking Circle");

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
		for(int i=0;i<4; i++) {			
			final Coordinates k,l;
			double motionTime = 0;
			
			k = corners.get(i);
			l = corners.get(i+1);
			
			double distanceX = Math.abs(l.getX() - k.getX());
			double distanceY = Math.abs(l.getY() - k.getY());
			
			double distanceBetweenPoints = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
			
			motionTime = distanceBetweenPoints/speedInPixelsPerS;
			
			final int speedX = (int) Math.abs((distanceX/motionTime));  
			final int speedY = (int) Math.abs((distanceY/motionTime));
			
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
		
			for(int i=0 ; i<3; i++) {
				final Coordinates z;
				
				z=corners.get(i);
				
				sumX = sumX +z.getX();
				sumY = sumY +z.getY();
			}
			
		final int centreOfGravityX = sumX/3; //zwaartepuntX = (x1+x2+x3)/3
		final int centreOfGravityY = sumY/3; //zwaartepuntY = (y1+y2+y3)/3
		
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
			
				for(int i=0 ; i<4; i++) {
					final Coordinates z;
					
					z=corners.get(i);
					
					sumX = sumX +z.getX();
					sumY = sumY +z.getY();
				}
				
			final int centreOfGravityX = sumX/4; 
			final int centreOfGravityY = sumY/4; 
			
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
		
			for(int i=0 ; i<12; i++) {
				final Coordinates z;
				
				z=corners.get(i);
				
				sumX = sumX +z.getX();
				sumY = sumY +z.getY();
			}
			
		final int centreOfGravityX = sumX/12; 
		final int centreOfGravityY = sumY/12; 
		
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
			
				for(int i=0 ; i<5; i++) {
					final Coordinates z;
					
					z=corners.get(i);
					
					sumX = sumX +z.getX();
					sumY = sumY +z.getY();
				}
				
			final int centreOfGravityX = sumX/5; 
			final int centreOfGravityY = sumY/5; 
			
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
