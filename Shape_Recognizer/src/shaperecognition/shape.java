package shaperecognition;

import java.util.ArrayList;

public class shape extends zone{
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
	public String determineShape() {
		double internalAngleSum = 0; int nConcave = 0; String res = "shape";
		//int idMax = 0;int boundId = 0; double Cx = 0; double Cy; int[] shapeRec = new int[4]; double maxL = 0; 
		//0: driehoek
		//1: vierkant
		//2: kruis
		//3: halve cirkel
		for (int i=0;i<cornerInternal.size();i++) {
			internalAngleSum += cornerInternal.get(i);
		}
		for (int i=0;i<cornerInternal.size();i++) {
			if(cornerInternal.get(i)>(180*(2*Math.PI/360))) {
				nConcave++;
			}
		}
		/*for (int i=1;i<lineLastPointId.size()+1;i++) {
			if( boundary.get(lineLastPointId.get(i-1)+1).getDist(boundary.get(lineLastPointId.get(i%lineLastPointId.size()))) > maxL){
				maxL = corner.get(i-1).getDist(corner.get(i%corner.size()));
				boundId = lineLastPointId.get(i-1)+1;}
		}
		//som  interne hoeken
		if ((135*(2*Math.PI/360) <= internalAngleSum) && (internalAngleSum <= 225*(2*Math.PI/360))) {
			shapeRec[1]++; 
		}
		if ((315*(2*Math.PI/360) <= internalAngleSum) && (internalAngleSum <= 405*(2*Math.PI/360))) {
			shapeRec[2]++; 
		}
		if ((1710*(2*Math.PI/360) <= internalAngleSum) && (internalAngleSum <= 1890*(2*Math.PI/360))) {
			shapeRec[3]++; 
		}
		//aantal hoeken
		if(cornerInternal.size()==3) {
			shapeRec[1]++;
		}
		if(cornerInternal.size()==4) {
			shapeRec[2]++;
		}
		if(cornerInternal.size()>=11) {
			shapeRec[3]++;
		}
		for(int i=0;i<shapeRec.length;i++) {
			if(shapeRec[i]>shapeRec[idMax]) {idMax = i;}
		}
		switch(idMax) {
		case 0: System.out.println("triangle");break;
		case 1: System.out.println("square");break;
		case 3: System.out.println("cross");break;
		case 4: System.out.println("semi-circle");break;
		default: System.out.println("error");
		}*/
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
		        		System.out.print("square");
		        		res = "square";
		        		keepGoing = false;
		        	}
		        	break;
		        case 4: 
		        	System.out.print("semi-circle");
		        	res = "semi-circle";
		        	keepGoing = false;
		        	break;
		     }
		 k++;
		 }   
		return res;
	}
	public void calcLineRot() {
		//ArrayList<Double> lineRot = new ArrayList<Double>();
		double rSum = 0; int idEnd = 0; int nv = 0;
		double rotC = 0; int lineTol = 2; double rotCI = 0; double tol = 0.00001;
		for(int i = 0; i<lines.length+nv; i++) {
			if( Math.abs(lines[(i+1)%lines.length]-lines[i]) > 0.5 ){
				lineLastPointId.add(i); 
				if( rotCI == 0 ) {nv = i; rotCI = 1;} //geval waarbij lijn 1 start op index 0
				//System.out.println("id: " + i);
				corner.add(new Coordinates(boundary.get(i).getX(),boundary.get(i).getY()));
			}
		}
		for (int i=1; i < lineLastPointId.size()+1; i++) {
					rotCI = 0;
					if (Math.abs(rotation[lineLastPointId.get((i-1))+1]) < tol) {rotC = 0;}
					else {
						for(int k = 1;k <= lineTol;k++) {
							rotCI = rotCI + (rotCI + rotation[lineLastPointId.get(i-1)+1+k]);
						}
						rotCI = rotCI/(lineTol+1);
						rotC = - Math.signum(rotCI) * Math.round(Math.abs(rotCI)/(Math.PI/4)) * (Math.PI/4);
					}
			rSum = rotC;
			//System.out.println("rotC: " +rotC);
			//System.out.println("id: " +lineLastPointId.get(i));
			if(i+1 < lineLastPointId.size()) {idEnd = lineLastPointId.get(i+1)-1;}
			else {idEnd = lineLastPointId.get((i+1)%lineLastPointId.size())-1 + rotation.length;}
			for(int j=( lineLastPointId.get(i-1)+2 ); j <= idEnd; j++) {
				//System.out.println("rotation: " +rotation[j%rotation.length]);
				rSum += rotation[j%rotation.length];
			}
			cornerRotation.add(rSum);
			System.out.println("rotation corner: " +rSum);
			//lineRot.add(rSum);
		}
		//Double[] rtn= lineRot.toArray(new Double[lineRot.size()]);
		//System.out.println(boundary.get(31).getX());
		//System.out.println(boundary.get(31).getY());
		//return rtn;
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
		int lineTol = 2; double rotCI = 0; double tol = 0.00001;
		for (int i = 0; i < 2*lines.length+1 ; i++) {
			id = i % lines.length;
			if (newLine == 1) {
				rotCI = 0;
				if (Math.abs(rotation[id]) < tol) {rotC = 0;}
				else {
					for(int k = 1;k <= lineTol;k++) {
						rotCI = rotCI + (rotCI + rotation[(id+k)%lines.length]);
					}
					rotCI = rotCI/(lineTol+1);
					rotC = - Math.signum(rotCI) * Math.round(Math.abs(rotCI)/(Math.PI/4)) * (Math.PI/4);
				}
				//System.out.println("rotC: " +rotC);
				idLine++;
				lines[id] = idLine;
				newLine = 0;
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
		int stop = 0; int found = 0;
		int k = 0; int count = 0; 
		int[][] W = this.getArrayZone(dimX,dimY);
		while (stop == 0) {
			k = 0; found = 0; 
			val_new = -1; val_old = -1;
			while (found == 0) {
				val_new = W[xc_new+xn[k]][yc_new+yn[k]];
				//System.out.println("x: "+(xc_new+xn[k]) +" y: " +(yc_new+yn[k]));
				//System.out.println("val_new: " + val_new + " val_old: " +val_old);
				if(val_new == 0 && val_old == 1) {
					if ((xc_new+xn[k-1] != xc_old || yc_new+yn[k-1] != yc_old) && (xc_new+xn[k-1] != xc_old2 || yc_new+yn[k-1] != yc_old2)) {
						xc_old2 = xc_old; yc_old2 = yc_old;
						xc_old = xc_new; yc_old = yc_new;
						xc_new = xc_new+xn[k-1];
						yc_new = yc_new+yn[k-1];
						boundary.add(new Coordinates(xc_new,yc_new));
						found = 1;
					}
				}
				if(val_old == 0 && val_new == 1) {
					if ((xc_new+xn[k] != xc_old || yc_new+yn[k] != yc_old) && (xc_new+xn[k] != xc_old2 || yc_new+yn[k] != yc_old2)) {
						xc_old2 = xc_old; yc_old2 = yc_old;
						xc_old = xc_new; yc_old = yc_new;
						xc_new = xc_new+xn[k];
						yc_new = yc_new+yn[k];
						boundary.add(new Coordinates(xc_new,yc_new));
						found = 1;
					}
				}
				//System.out.println("ycold: " +yc_old+ " xcold: "+xc_old);
				//System.out.println("ycold2: " +yc_old2+ " xcold2: "+xc_old2);
				val_old = val_new;
				k++;
				if (k >= xn.length){
					System.out.println("no boundary point or open boundary");
					stop = 1; break;
				}
			}
			if (xc_new == start.getX() && yc_new == start.getY()) {
				stop = 1;
				boundary.remove(boundary.size()-1);
			}
			if (count > 500) {
				System.out.println("Boundary: total number of scanpoinnts exceeded");
				break;
				}
			count++;
		}
	}
}	

