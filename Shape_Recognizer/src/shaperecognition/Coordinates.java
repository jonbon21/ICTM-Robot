package shaperecognition;

class Coordinates {
	//This class defines a point by its coordinates
	//This class also includes a method to calculate the distance (getDist) and the angle (getAngle) between 2 points
    private int X;
    private int Y;
    
    public Coordinates() { 	
    }
    public Coordinates(int first, int second) {
        this.X = first;
        this.Y = second;
    }
    public Coordinates(Coordinates A) {
    	X = A.X;
    	Y = A.Y;
    }
    public int getX() {
        return X;
    }
    public int getY() {
        return Y;
    }
    public double getDist(Coordinates P2) {
    	return Math.sqrt(Math.pow(P2.Y - Y, 2) + Math.pow(P2.X - X, 2));
    }
    public double getAngle(Coordinates P2) {
    	double res = 0;
    	res = Math.acos((P2.Y-Y)/(this.getDist(P2)));
    	if ((P2.X-X)>0) {res = 2*Math.PI - res;} 
    	return res;    	
    }
}

