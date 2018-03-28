package shaperecognition;

import lejos.utility.Delay;
import lejos.hardware.port.*;

public class XYMap {
	Boolean[][] xyMap;

	public XYMap(Coordinates inpLimits) {
		xyMap = new Boolean[inpLimits.Coord_X][inpLimits.Coord_Y];
	}
	
	public Coordinates scan(Motor inpMotorX, Motor inpMotorY) {
		Coordinates object_maxX, object_minX, object_maxY, object_minY;
		return new Coordinates(, );
	}
}
