package shaperecognition;

import java.util.ArrayList;
class shape extends zone{
	//This class defines the shape of a given zone (therefore it extends the class zone).
	//main methods:
	//1)findBoundPoint: searches the first point of the boundary
	//2)findBound: determines the boundary of the zone, the boundary points are stored in the list boundary
	//3)direction: calculates the orientation of the boundary vectors; a boundary vector is defined based on two consecutive points. The result is stored in direction.
	//4)rotation: calculates the rotation between two consecutive boundary vectors. The result is stored in rotation.
	//5)calcLines: assigns a line number to each point in the boundary, based on a line tolerance. The result is stored in lines
	//6)linearRegression: fits a straight line to all points assigned to a given line number. this method is applied in the method calcLineRot.
	//7)calcCornerRotReg: calculates the angle between two consecutive regression lines. The result is stored in cornerRotation. This method is applied in the method calcLineRot.
	//8)calcCornerReg: calculates the Coordinates of the intersection between two consecutive lines. The result is stored in corner. This method is applied in the method calcLineRot.
	//9)calcLineRot: This method method implements the methods linearRegression, calcCornerReg and calcCornerReg. This method optionally merges lines that fall within certain criteria.
	//10)internalAngleCorner: this method calculates the interior angles for the given shape
	//11)determineShape: this method determines the shape of the object based on certain boundary characteristics. In case of a semi-circle we calculate a certain number of equally spaced points on the curved line. 
	private ArrayList<Coordinates> boundary = new ArrayList<Coordinates>();
	private double [] direction = new double[0]; 
	private double [] rotation = new double[0];
	private double [] angle = new double[0];
	private int [] lines = new int[0];
	private ArrayList<Integer> lineLastPointId = new ArrayList<Integer>();
	private ArrayList<Coordinates> corner = new ArrayList<Coordinates> ();
	private ArrayList<Double> cornerRotation = new ArrayList<Double> ();
	private ArrayList<Double> cornerInternal = new ArrayList<Double> ();

	public shape(zone newZone){
		super(newZone);
	}
	public ArrayList<Coordinates> getListBound(){
		return boundary;
	}
	public int getBoundEl() {
		return boundary.size();
	}
	public double[] getDirection() {
		return direction;
	}
	public double[] getRotation() {
		return rotation;
	}
	public double[] getAngle() {
		return angle;
	}
	public int[] getLines() {
		return lines;
	}
	public ArrayList<Coordinates> getCorner(){
		return corner;
	}
	public int[][] getArrayBound(int dimX, int dimY) {
		int[][] m = new int[dimX][dimY];
		int x;
		int y;
		for(int i = 0; i < boundary.size(); i++) {
			x = ((Coordinates) boundary.get(i)).getX();
			y = ((Coordinates) boundary.get(i)).getY();
			m[x][y] = 1;
		}
		return m;
	}
	public int[][] getArrayCorner(int dimX, int dimY) {
		int[][] m = new int[dimX][dimY];
		int x;
		int y;
		for(int i = 0; i < corner.size(); i++) {
			x = ((Coordinates) corner.get(i)).getX();
			y = ((Coordinates) corner.get(i)).getY();
			m[x][y] = 1;
		}
		return m;
	}
	public double[][] getArrayAngle(double[] A, int dimX, int dimY) {
		double[][] m = new double[dimX][dimY];
		int x; int y;
		for(int i = 0; i < boundary.size(); i++) {
			x = ((Coordinates) boundary.get(i)).getX();
			y = ((Coordinates) boundary.get(i)).getY();
			m[x][y] = A[i] *360/(2*Math.PI);
		}
		return m;
	}
	public int[][] getArrayInt(int[] A, int dimX, int dimY) {
		int[][] m = new int[dimX][dimY];
		int x; int y;
		for(int i = 0; i < boundary.size(); i++) {
			x = ((Coordinates) boundary.get(i)).getX();
			y = ((Coordinates) boundary.get(i)).getY();
			m[x][y] = A[i];
		}
		return m;
	}
	public double circumference() {
		double res = 0; int id_n = 0;
		for (int i=0;i<boundary.size();i++) {
			id_n = (i+1)%(direction.length); 
			res += boundary.get(i).getDist(boundary.get(id_n));
		}
		return res;
	}
	public double surface() {
		double scale = 1; double surf = 0; double surplus = 0;
		surf = this.getZoneEl()*scale;
		for (int i=0;i<boundary.size();i++) { 
		surplus += (2 * Math.PI - angle[i])/(2 * Math.PI) ;
		}
		return (surf - surplus);
	}
	public void direction() {
		direction = new double[this.getBoundEl()]; 
		int id_n = 0; 
		for (int i=0;i<direction.length;i++) {
			id_n = (i+1)%(direction.length); 
			//System.out.println("x: " +boundary.get(i).getX() + " y: " + +boundary.get(i).getY() );
			//System.out.println("x: " +boundary.get(id_n).getX() + " y: " + +boundary.get(id_n).getY() );
			direction[i] = boundary.get(i).getAngle(boundary.get(id_n)); 
			//System.out.println("rotation: "+direction[i]);
		}
	}
	public void internalAngle() {
		angle = new double[this.getBoundEl()]; 
		double sum = 0; int sign = 1;
		for (int i=0;i<rotation.length;i++) {
		    sum += rotation[i];
		}
		if (sum > 0) {sign = -1;}
		for  (int i=0;i<angle.length;i++) {
			angle[i] = Math.PI + sign * rotation[i];
		} 
	}
	public double[] internalAngleNew(double[] rot) {
		double[] angleN = new double[this.getBoundEl()]; 
		double sum = 0; int sign = 1;
		for (int i=0;i<rotation.length;i++) {
		    sum += rotation[i];
		}
		if (sum > 0) {sign = -1;}
		for  (int i=0;i<angleN.length;i++) {
			angleN[i] = Math.PI + sign * rot[i];
		} 
		return angleN;
	}
	public double[] internalAngleCorner() {
		double[] intAngleCorner = new double[cornerRotation.size()]; 
		double sum = 0; int sign = 1;
		for (int i=0;i<intAngleCorner.length;i++) {
		    sum += cornerRotation.get(i);
		}
		if (sum > 0) {sign = -1;}
		for  (int i=0;i<intAngleCorner.length;i++) {
			intAngleCorner[i] = Math.PI + sign * cornerRotation.get(i);
			cornerInternal.add(Math.PI + sign * cornerRotation.get(i));
			System.out.println("internal angle corner: " +intAngleCorner[i]/(2*Math.PI)*360);
		} 
		return intAngleCorner;
	}
	public void rotation() {
		rotation = new double[this.getBoundEl()];
		double val = 0;	int id_n = 0; 
		for (int i=0;i<rotation.length;i++) {
			id_n = i-1;
			if (id_n<0) {id_n = id_n + boundary.size();} 
			val = direction[i] - direction[id_n];
			if (val < - Math.PI) { val += 2*Math.PI;}
			if (val >   Math.PI) { val -= 2*Math.PI;}
			rotation[i] = val;
		}
	}
	public String determineShape(int dimX, int dimY) {
		double internalAngleSum = 0; int nConcave = 0; String res = "shape";
		for (int i=0;i<cornerInternal.size();i++) {
			internalAngleSum += cornerInternal.get(i);
		}
		for (int i=0;i<cornerInternal.size();i++) {
			if(cornerInternal.get(i)>(180*(2*Math.PI/360))) {
				nConcave++;
			}
		}
		double[] distance = new double[corner.size()];
		for (int i=1;i<corner.size()+1;i++) {
			distance[i-1] = corner.get(i-1).getDist(corner.get(i%corner.size()));
		}
		boolean equalLength = true;
		for (int i=0;i<distance.length;i++) {
			if(Math.abs(distance[0]-distance[i])>0.3*distance[0]) {equalLength = false;}
		}
		boolean keepGoing = true;
	    int k = 1;
		while(keepGoing)
		{
		switch(k)
		    {
		        case 1: 
		            if (nConcave >= 3) {
		            	System.out.print("cross");
		            	res = "cross";
		            	keepGoing = false;
		            }
		            break;
		        case 2: 
		        	if( (cornerInternal.size()==3) && (135*(2*Math.PI/360) <= internalAngleSum) && (internalAngleSum <= 225*(2*Math.PI/360)) ){
		        		System.out.print("triangle");
		        		res = "triangle";
		        		keepGoing = false;
		        	}
		            break;
		        case 3: 
		        	if( (cornerInternal.size()==4) && (315*(2*Math.PI/360) <= internalAngleSum) && (internalAngleSum <= 405*(2*Math.PI/360)) ){
		        		if(equalLength == true) {
		        		System.out.print("square");
		        		res = "square";
		        		keepGoing = false;
		        		}
		        	}
		        	break;
		        case 4: 
		        	int idMax = 0, idQ = 0, nCirPoints = 5; double xMid = 0, yMid = 0, radius = 0, pQOrientation = 0; 
		        	Coordinates cornerA, cornerB; double cornerBNewX,cornerBNewY, pQnewX, pQnewY;
		        	for (int i=0;i<distance.length;i++) {
		    			if((distance[i] - distance[idMax])>0) {idMax = i;}
		    		}
		        	cornerA = corner.get(idMax);
		        	cornerB = corner.get((idMax+1)%corner.size());

		        	xMid = (cornerA.getX() + cornerB.getX())/2;
		        	yMid = (cornerA.getY() + cornerB.getY())/2;
		        	radius = Math.sqrt(Math.pow(yMid - cornerB.getY(), 2) + Math.pow(xMid - cornerB.getX(), 2));
		        	for (int i=0;i<corner.size();i++) {
		    			if((i != idMax)&&(i != (idMax+1)%corner.size())) {
		    				idQ = i;
		    				break;
		    			}
		    		}
		        	cornerBNewX = cornerB.getX()-xMid; 	cornerBNewY = cornerB.getY()-yMid;    	
		        	pQnewX = corner.get(idQ).getX()-xMid; pQnewY = corner.get(idQ).getY()-yMid;
		        	pQOrientation = cornerBNewX*pQnewY - cornerBNewY*pQnewX;
		        	corner.clear();
		        	corner.add(cornerA); corner.add(cornerB);
		        	double vectorRot = Math.PI/(nCirPoints+1), ss = pQOrientation/Math.abs(pQOrientation);
		        	for(int i=1; i<nCirPoints+1;i++) {
		        		int rrx,rry;
		        		rrx = (int) Math.round((Math.cos(ss*i*vectorRot)*cornerBNewX - Math.sin(ss*i*vectorRot)*cornerBNewY) + xMid);
		        	    rry = (int) Math.round((Math.sin(ss*i*vectorRot)*cornerBNewX + Math.cos(ss*i*vectorRot)*cornerBNewY) + yMid);
		        	    if(rrx <0) {rrx = 0;}
		    			if(rry <0) {rry = 0;}
		    			if(rrx >= dimX) {rrx = dimX-1;}
		    			if(rry >= dimY) {rry = dimY-1;} 
		        	    //System.out.println("xcircle:" +rrx+ " ycircle: " +rry);
		        	    corner.add(new Coordinates(rrx,rry));
		        	}
		        	System.out.print("semicircle");
		        	res = "semicircle";
		        	keepGoing = false;
		        	break;
		     }
		 k++;
		 } 
	return res;
	}
	public void calcLineRot(boolean optMerge, int dimX, int dimY) {
		boolean mergeLines = true; int mergeId = 0; 
		ArrayList <double[]> regLines = new ArrayList<double[]>(); 
		while(mergeLines == true) {
			for(int i = 0; i<lines.length; i++) {
				if( Math.abs(lines[(i+1)%lines.length]-lines[i%lines.length]) > 0.5 ){
					lineLastPointId.add(i); 
				}
			}
			for (int i=1; i < lineLastPointId.size()+1; i++) {
			int firstPointLine = (lineLastPointId.get(i-1)+1)%this.getBoundEl();	
			int lastPointLine = lineLastPointId.get((i)%lineLastPointId.size());
			double[] newRegLine = linearRegression(firstPointLine,lastPointLine);
			regLines.add(newRegLine);
			//System.out.println("line: " +newRegLine[3]);
			//System.out.println("intercept: " +newRegLine[0]);
			//System.out.println("slope: " +newRegLine[1]);
			//System.out.println("quadrant: " +newRegLine[2]);
			}
			for(int i=0;i<regLines.size();i++) {
			cornerRotation.add(calcCornerRotReg(regLines.get(i),regLines.get((i+1)%regLines.size())));
			corner.add(calcCornerReg(regLines.get(i),regLines.get((i+1)%regLines.size()),dimX,dimY));
			System.out.println("rotation regression: "+cornerRotation.get(i));
			}		
			mergeLines = false; mergeId = 0; 
			if(optMerge == true) {
			for(int i = 0; i<regLines.size(); i++) {
				if((int)regLines.get(i)[4]<= 2) {
					mergeLines = true;
					if(regLines.get(i)[4]< regLines.get(mergeId)[4]) {
						mergeId = i;
					}
				}
			}
			}
			if((mergeLines == false) && (optMerge == true)) {
				for(int i = 0; i<cornerRotation.size(); i++) {
					if(Math.abs(cornerRotation.get(i)) <= 30*(2*Math.PI/360)) {
						mergeLines = true;
						if(Math.abs(cornerRotation.get(i))< Math.abs(cornerRotation.get(mergeId))) {
							mergeId = i;
						}
					}
				}
			}
			if((mergeLines == true)&& (optMerge == true)) {
				for(int j = 0; j<lines.length; j++) {
					if(lines[j] == (int)regLines.get(mergeId)[3]) {
					lines[j] = 	(int)regLines.get((mergeId+1)%regLines.size())[3];
					//System.out.println("mergLine: "+(int)regLines.get((mergeId)%regLines.size())[3]);
					//System.out.println("mergLine: "+(int)regLines.get((mergeId+1)%regLines.size())[3]);
					}	
				}
				lineLastPointId.clear();
				cornerRotation.clear();
				corner.clear();
				regLines.clear();
				System.out.println("linesMerged");
			}
		}
	}
	public Coordinates findBoundPoint() {
		int id_xMax = 0; int xMax = 0;
		for (int i = 1; i < coordZone.size() ; i++) {
			if (coordZone.get(i).getX() > xMax) {
				id_xMax = i;
				xMax = coordZone.get(i).getX();
			}		
		}
		return coordZone.get(id_xMax);
	}
	public void calcLines() {
		lines = new int[this.getBoundEl()];
		int idLine = 0; int newLine = 1; int id = 0; double rotC = 0; double rotCF = 0;//int nv = 0; 
		int lineTol = 2; double rotCI = 0; double tol = 0.00001; int nI = 1;
		for (int i = 0; i < 2*lines.length+nI ; i++) {
			id = i % lines.length;
			if (newLine == 1) {
				if (Math.abs(rotation[id]) < tol) {rotC = 0;}
				else {
					rotCI = 0;
					for(int j = 1;j <= lineTol;j++) {
						for(int k = 1;k <= j;k++) { //UPDATE
						rotCI = rotCI +  rotation[(id+k)%lines.length]; //rotCI
						}
					}
					rotCI = rotCI/(lineTol+1);
					rotC = - Math.signum(rotCI) * Math.round(Math.abs(rotCI)/(Math.PI/4)) * (Math.PI/4);
				}
				//System.out.println("rotC: " +rotC);
				idLine++;
				lines[id] = idLine;
				newLine = 0;
				if(idLine == 3 ) {nI = id;};
			}
			else if ( (newLine == 0) && (Math.abs(rotC + rotation[id]) <  tol) ) {
				lines[id] = idLine;
				rotC = 0;
			}
			else if ((newLine == 0) && ((rotC + rotation[id]) != 0)) {
				rotC = rotC + rotation[id];
				rotCF = rotC;
				for (int j = 1; j <= lineTol ; j++) {
					rotCF  = rotCF + rotation[(id+j)%lines.length];
					if ((( (rotCF) <=0) && (rotC>0)) ||
						(( (rotCF) >=0) && (rotC<0))) {
						lines[id] = idLine;
						break;
					}
					else if(j == lineTol){
						newLine = 1;
						lines[id] = idLine;
					}
				}
			}
			//System.out.println(i);
			//System.out.println("X: " + boundary.get(id).getX() + " Y: " + boundary.get(id).getY());
			//System.out.println("lineLastPointId: " + idLine + " rotC: " + rotC + " rotatie: " + rotation[id]);
		}
	} 
	public void findBound(int dimX,int dimY, Coordinates start) {
		boundary.add(start);
		int[] xn = {  1, 1, 1, 0, -1, -1, -1,  0,  1, 1}; //-1,  0, 1, 0, -1,  0, 
		int[] yn = { -1, 0, 1, 1,  1,  0, -1, -1, -1, 0}; //0,  -1, 0, 1,  0, -1,
		int xc_new = start.getX(); int xc_old = -1; int xc_old2 = -1;
		int yc_new = start.getY(); int yc_old = -1; int yc_old2 = -1;
		int val_new = -1; int val_old = -1; 
		int stop = 0; boolean found = false;
		int count = 0; boolean newBP = true;
		ArrayList<Coordinates> bPoints = new ArrayList<Coordinates>();
		int[][] W = this.getArrayZone(dimX,dimY);
		while (stop == 0) {
			val_new = -1; val_old = -1;
			bPoints.clear();
			for(int k = 0;k<xn.length;k++) {
			val_new = W[xc_new+xn[k]][yc_new+yn[k]];
				if(val_new == 0 && val_old == 1) {
					//if ((xc_new+xn[k-1] != xc_old || yc_new+yn[k-1] != yc_old) && (xc_new+xn[k-1] != xc_old2 || yc_new+yn[k-1] != yc_old2)) {
					//	xc_old2 = xc_old; yc_old2 = yc_old;
					//	xc_old = xc_new; yc_old = yc_new;
					//	xc_new = xc_new+xn[k-1];
					//	yc_new = yc_new+yn[k-1];
					//	boundary.add(new Coordinates(xc_new,yc_new));
					bPoints.add(new Coordinates(xc_new+xn[k-1],yc_new+yn[k-1]));
				}
				if(val_old == 0 && val_new == 1) {
					//if ((xc_new+xn[k] != xc_old || yc_new+yn[k] != yc_old) && (xc_new+xn[k] != xc_old2 || yc_new+yn[k] != yc_old2)) {
					//	xc_old2 = xc_old; yc_old2 = yc_old;
					//	xc_old = xc_new; yc_old = yc_new;
					//	xc_new = xc_new+xn[k];
					//	yc_new = yc_new+yn[k];
					//	boundary.add(new Coordinates(xc_new,yc_new));
					bPoints.add(new Coordinates(xc_new+xn[k],yc_new+yn[k]));	
				}
			val_old = val_new;
			}
			if (count > 5) {
				for(int i=0;i<bPoints.size();i++) {
					if (bPoints.get(i).getX() == start.getX() && bPoints.get(i).getY() == start.getY()) {
					stop = 1;
					}
				}
			}
			//System.out.println("bpointsSize: "+bPoints.size());
			if(stop == 0) {
				for(int i=0;i<bPoints.size();i++) {
					newBP = true;
					//System.out.println("check" + Math.min(15, boundary.size()-1));
					for(int j= boundary.size()-1;j>=Math.max(0, boundary.size()-15) ;j--) {
						if(bPoints.get(i).getX() == boundary.get(j).getX() && bPoints.get(i).getY() == boundary.get(j).getY()) {
							newBP = false;
						}
					}
				if(newBP ==  true) {
				boundary.add(new Coordinates(bPoints.get(i).getX(),bPoints.get(i).getY()));
				xc_new = bPoints.get(i).getX();
				yc_new = bPoints.get(i).getY();
				//System.out.println("xnew: " +xc_new+ "ynew: "+yc_new);
				break;
				}
				}
			}
			if(newBP == false && stop == 0) {
				int oldestId =0, bPointsId = 0;
				for(int i=0;i<bPoints.size();i++) {
					for(int j= boundary.size()-1;j>=Math.max(0, boundary.size()-15) ;j--) {
						if(bPoints.get(i).getX() == boundary.get(j).getX() && bPoints.get(i).getY() == boundary.get(j).getY()) {
							if(j>oldestId) {
								oldestId = j;
								bPointsId = i;
							}
						}
					}
				}
				boundary.add(new Coordinates(bPoints.get(bPointsId).getX(),bPoints.get(bPointsId).getY()));
				xc_new = bPoints.get(bPointsId).getX();
				yc_new = bPoints.get(bPointsId).getY();
			}
			if (count > 1000) {
			System.out.println("Boundary: total number of scanpoinnts exceeded");
			break;
			}
		count++;
		}
	}
	public double[] linearRegression(int lineIdFirst, int lineIdLast) {
		//DEMING regression (smallest distance)
	    	double intercept, slope; double[] res = new double[5]; 
	    	int[] x,y; int nPoints = 0; //double meanDir = 0;
	    	res[3] = lines[lineIdLast];
	    	if ((lineIdLast + this.getBoundEl() - lineIdFirst) >= this.getBoundEl()) { //Update
	    		nPoints = lineIdLast - lineIdFirst + 1;
	    	}
	    	else
	    	{
	    		nPoints = this.getBoundEl() + lineIdLast - lineIdFirst + 1;
	    	}
	    	//System.out.println("number of points " + nPoints+ " lineId: "+res[3]);
	    	//System.out.println("lineIdFirst " + lineIdFirst+ " lineIdLast: "+lineIdLast);
	    	x = new int[nPoints]; y = new int[nPoints];
	    	for(int i=0;i<nPoints;i++) {
	    		x[i]= boundary.get((lineIdFirst+i)%this.getBoundEl()).getX();
	    	    y[i]= boundary.get((lineIdFirst+i)%this.getBoundEl()).getY();
	    	}
	    	//for(int i=0;i<nPoints-1;i++) {
	    	//	res[2] += direction[(lineIdFirst+i)%this.getBoundEl()]/(nPoints-1);
	    	//}
	    	res[4] = nPoints;
	        if (x.length != y.length) {
	            throw new IllegalArgumentException("array lengths are not equal");
	        }
	        
	        int n = x.length;
	        // first pass
	        double sumx = 0.0, sumy = 0.0; //, sumx2 = 0.0;
	        for (int i = 0; i < n; i++) {
	            sumx  += x[i];
	            //sumx2 += x[i]*x[i];
	            sumy  += y[i];
	        }
	        double xbar = sumx / n;
	        double ybar = sumy / n;
	        double Sxx = 0,Syy = 0,Sxy = 0, nomS = 0, denomS = 0;
	        for (int i = 0; i < n; i++) {
	        Sxx += (x[i]-xbar)*(x[i]-xbar);
	        Syy += (y[i]-ybar)*(y[i]-ybar);
	        Sxy += (x[i]-xbar)*(y[i]-ybar);
	        }
	        Sxx = Sxx / (n-1);
	        Syy = Syy / (n-1);
	        Sxy = Sxy / (n-1);
	        nomS = Syy - Sxx + Math.sqrt((Syy-Sxx)*(Syy-Sxx) + 4*Sxy*Sxy); 
	        denomS = 2*Sxy;
	        double ss = 1;
	        if (Math.abs(denomS) < Math.pow(10, -5)) {
		        if (Math.abs(denomS) > 0) {
		        	ss = denomS/Math.abs(denomS);
		        }
	        denomS = ss * Math.pow(10, -5);
	        }
	        //System.out.println("check denomS: " +denomS+ "check nomS: " +nomS);
	        slope  = nomS/denomS;
	        intercept = ybar - slope * xbar;
	        if(slope >= 1) {
	        	if((boundary.get(lineIdLast).getY()-boundary.get(lineIdFirst).getY()) >= 0) {res[2] = 1;} 
	        	else {res[2] = 3;}
	        }
	        if( (slope < 1)&&(slope >= 0) ) {
		        if((boundary.get(lineIdLast).getX()-boundary.get(lineIdFirst).getX()) >= 0) {res[2] = 1;} 
		        else {res[2] = 3;}
	        }
	        if( (slope < 0)&&(slope >= -1) ) {
	        	if((boundary.get(lineIdLast).getX()-boundary.get(lineIdFirst).getX()) >= 0) {res[2] = 4;} 
	        	else {res[2] = 2;}
            }
	        if(slope < -1){
	        	if((boundary.get(lineIdLast).getY()-boundary.get(lineIdFirst).getY()) >= 0) {res[2] = 2;} 
	        	else {res[2] = 4;}
            }
	        res[0] = intercept; res[1] = slope;
	        return res;
	    }
	public double calcCornerRotReg(double[] lineA, double[] lineB) {
			double res = 0, angleA = 0, angleB = 0;
			if(lineA[1] >= 0) {angleA = Math.atan(lineA[1])+(lineA[2]-1)*Math.PI/2;}
			else {angleA = Math.atan(lineA[1]) + Math.PI/2 +(lineA[2]-1)*Math.PI/2;}
			if(lineB[1] >= 0) {angleB = Math.atan(lineB[1])+(lineB[2]-1)*Math.PI/2;}
			else {angleB = Math.atan(lineB[1]) + Math.PI/2 +(lineB[2]-1)*Math.PI/2;}
			res = angleB - angleA;
			if (res < - Math.PI) { res += 2*Math.PI;}
			if (res >   Math.PI) { res -= 2*Math.PI;}
			return res;
		}
	public Coordinates calcCornerReg(double[] lineA, double[] lineB, int dimX, int dimY) {
			int xCoord = 0, yCoord = 0; double xSec =0, ySec = 0;
			xSec = (lineB[0]-lineA[0])/(lineA[1]-lineB[1]);
			ySec = lineA[0] + lineA[1]*xSec;
			
			xCoord = (int) Math.round(xSec);
			yCoord = (int) Math.round(ySec);
			//System.out.println("X: " +xCoord+ " Y: " +yCoord);
			if(xCoord <0) {xCoord = 0;}
			if(yCoord <0) {yCoord = 0;}
			if(xCoord >= dimX) {xCoord = dimX-1;}
			if(yCoord >= dimY) {yCoord = dimY-1;}
			return new Coordinates(xCoord,yCoord);
		}
}	
