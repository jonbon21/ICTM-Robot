package shaperecognition;

import lejos.hardware.port.MotorPort;

public class ZSensorHeight {
	Motor motorZ = new Motor(MotorPort.C);
	
	public void home() {
		while (motorcurrent is low) {
		System.out.println("Homing Z-axis");
		motorZ.setSpeed(500);
		motorZ.backward();
		}
		motorZ.resetTachoCount();
		
	}
}
