package shaperecognition;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class AM{
//test 
	public static void main(String [] args) {
		//vb sensor output
		int  [][] scanmap = new int[7][7];
		scanmap[2][5] = 1;	scanmap[5][3] = 1;
		scanmap[3][4] = 1;	scanmap[5][4] = 1;
		scanmap[3][5] = 1;	scanmap[5][5] = 1;
		scanmap[4][3] = 1;	scanmap[4][4] = 1;	
		scanmap[4][5] = 1;	scanmap[5][2] = 1;   
		scanmap [1][1] = 1; scanmap[2][3] = 1;
		
		printInt(scanmap);
		System.out.println();
		scan scanObj = new scan(scanmap);
		int dimX = scanObj.getDimX(); int dimY = scanObj.getDimY();
		scanObj.prepare();
		scanObj.findZones(); //zone wordt gedefinieerd als aaneengesloten geheel waarbij elk element verbonden is langs boven,onder,links of rechts
		ArrayList<zone> zones = scanObj.getZoneList();	
		Collections.sort(zones, new zoneSort()); //zones sorteren volgens 'aantal elementen'
		
		for (int k = 0; k < zones.size(); k++) {
			System.out.println("aantal elementen zone " +(k+1)+ " : "  +zones.get(k).getZoneEl()); //print aantal elementen
		 	printInt(zones.get(k).getArrayZone(dimX,dimY)); //print zones	
		}
		shape shapeObj = new shape(zones.get(zones.size()-1));
		//rand wordt bepaald vanaf startpunt op de omtrek (moet nog geautimatiseerd worden)
		Coordinates start = new Coordinates(5,5);
		shapeObj.findBound(dimX,dimY,start);
		System.out.println("rand grootste zone");
		printInt(shapeObj.getArrayBound(dimX,dimY));
		shapeObj.direction();
		System.out.println("orientatie vectoren");
		printDouble(shapeObj.getArrayAngle(shapeObj.getDirection(),dimX,dimY));
		shapeObj.rotation();
		System.out.println("rotatie vectoren");
		printDouble(shapeObj.getArrayAngle(shapeObj.getRotation(),dimX,dimY));
		shapeObj.internalAngle();
		System.out.println("inwendige hoeken");
		printDouble(shapeObj.getArrayAngle(shapeObj.getAngle(),dimX,dimY));
		//Coordinates P1 = new Coordinates(0,0);
		//Coordinates P2 = new Coordinates(0,1);
		//System.out.println(P1.getAngle(P2));
		//System.out.println(shapeObj.getBoundEl());
		
	}

		public static void printInt(int[][] M){
			for (int i = 0; i < M.length; i++) {
				for (int j = 0; j < M[i].length; j++) {
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
		}
		public static void printDouble(double[][] M){
			for (int i = 0; i < M.length; i++) {
				for (int j = 0; j < M[i].length; j++) {    
				//time = Double.valueOf(df.format(time));
				System.out.print(String.format("%.2f", M[i][j]) + "\t");
			}
			System.out.println();
		}
		}	
}

class scan {
	private int[][] map = new int[0][0];
	private int dimX = 0;
	private int dimY = 0;
	private ArrayList<zone> zoneList = new ArrayList<zone>();
	scan(){
	}
	scan(int[][] newMap){
		map = newMap;
		dimX = newMap.length;
		dimY = newMap[0].length;
	}
	public int getDimX() {
		return dimX;
	}
	public int getDimY() {
		return dimY;
	}
	public void prepare() {
	// set first row and first column equal to 0
	for (int j = 0; j < map.length; j++) {
		map[0][j] = 0;
		map[map.length-1][j] = 0;
	}
	for (int i = 0; i < map.length; i++) {
		map[i][0] = 0;
		map[i][map[0].length-1] = 0;
	}
	}
	public int[][] getMap(){
		return map;
	}
	public ArrayList<zone> getZoneList(){
		return zoneList;
	}
	public void findZones() {
		ArrayList<zone> zoneOld = new ArrayList<zone>();
		ArrayList<zone> zoneNew = new ArrayList<zone>();
		//ArrayList<zone> zoneFinal = new ArrayList<zone>();
		ArrayList<Integer> update = new ArrayList<Integer> ();
		int[][] track = new int[map.length][map[0].length];
		track = elwiseAdd(track,-1);
		int zoneNr = -1;
		int ignore = 0;
		//int column = 2;
		for (int j = 1; j < map[0].length; j++) {
			//System.out.println("column: " +column);
			//column++;
			for (int i = 1; i < map.length; i++) {
				if (map[i][j]!=0) {
					Coordinates P = new Coordinates(i,j);		
					
					if(map[i-1][j] == 0) {
						zoneNr++;
						zone z = new zone(P);
						zoneNew.add(z);
						ignore = 0;
						track[i][j] = zoneNr;
					}
					else {
						((zone) zoneNew.get( zoneNr )).addEl(P);
						track[i][j] = zoneNr;
					}
					if (map[i][j-1]!=0 && ignore == 0) {
						 ((zone) zoneNew.get(zoneNr)).merge( (zone) zoneOld.get(track[i][j-1]) );
						 //System.out.println("merger id " + track[i][j-1]);
						 //System.out.println("zoneOld size: " +zoneOld.size());
						 //System.out.println("mergeobj: ");
						 //present(((zone) zoneOld.get(track[i][j-1])).getArray(7));
						 update.add(track[i][j-1]);
						 ignore = 1;
					}
					if(map[i][j-1] == 0) {
						ignore = 0;
					}
				}
			}
			zoneNr = -1;	
			ignore = 0;
			for (int k = update.size()-1; k == 0; k--) {
				zoneOld.remove((int)update.get(k));		
			}
			zoneList.addAll(zoneOld);
			zoneOld.clear();
			for (int k = 0; k < zoneNew.size(); k++) {
				zoneOld.add(new zone(zoneNew.get(k)));		
			}
			//zoneOld = (ArrayList<zone>) zoneNew.clone();
			zoneNew.clear();
			update.clear();
			//System.out.println();
			//present(track);
		}
	}
	public int[][] elwiseAdd(int[][] M, int a){
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[i].length; j++) {
				M[i][j] +=  a ;
			}
		}
		return M;
	}
	
}
class shape extends zone{
	private ArrayList<Coordinates> boundary = new ArrayList<Coordinates>();
	double [] direction = new double[0]; 
	double [] rotation = new double[0];
	double [] angle = new double[0];
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
	public void findBound(int dimX,int dimY, Coordinates start) {
		boundary.add(start);
		int[] xn = { 0, 1, 1, 1, 0, -1, -1, -1, 0};
		int[] yn = {-1,-1, 0, 1, 1,  1,  0, -1, -1};
		int xc_new = start.getX(); int xc_old = -1;
		int yc_new = start.getY(); int yc_old = -1;
		int val_new = -1; int val_old = -1;
		int stop = 0; int found = 0;
		int k = 0; //int count = 0; 
		int[][] W = this.getArrayZone(dimX,dimY);
		while (stop == 0) {
			k = 0; found = 0; 
			val_new = -1; val_old = -1;
			while (found == 0) {
				val_new = W[xc_new+xn[k]][yc_new+yn[k]];
				//System.out.println("x: "+(xc_new+xn[k]) +" y: " +yc_new+yn[k]);
				//System.out.println("val_new: " + val_new + " val_old: " +val_old);
				if(val_new == 0 && val_old == 1) {
					if (xc_new+xn[k-1] != xc_old || yc_new+yn[k-1] != yc_old) {
						xc_old = xc_new; yc_old = yc_new;
						xc_new = xc_new+xn[k-1];
						yc_new = yc_new+yn[k-1];
						boundary.add(new Coordinates(xc_new,yc_new));
						found = 1;
					}
				}
				if(val_old == 0 && val_new == 1) {
					if (xc_new+xn[k] != xc_old || yc_new+yn[k] != yc_old) {
						xc_old = xc_new; yc_old = yc_new;
						xc_new = xc_new+xn[k];
						yc_new = yc_new+yn[k];
						boundary.add(new Coordinates(xc_new,yc_new));
						found = 1;
					}
				}
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
			//if (count > 30) {break;}
			//count++;
		}
	}
}	
		
class zone {
	protected int zoneNr = 0;
	protected ArrayList<Coordinates> coordZone = new ArrayList<Coordinates>();
	public zone() {
	}
	/*public int compare(zone a, zone b)
	    {
	        return a.getZoneEl() - b.getZoneEl();
	    }*/
	public zone(Coordinates P) {
		coordZone.add(P);
	}
	public zone(zone A) {
		zoneNr   = A.zoneNr;
		coordZone = A.coordZone;
	}
	public void addEl(Coordinates P){
		coordZone.add(P);
	}
	public void setZoneNr(int newZoneNr) {
		zoneNr = newZoneNr;
	}
	public ArrayList<Coordinates> getCoordZone(){
		return coordZone;
	}
	public int getZoneNr() {
		return zoneNr;
	}
	public int getZoneEl() {
		return coordZone.size();
	} 
	public int[][] getArrayZone(int dimX, int dimY) {
		int[][] m = new int[dimX][dimY];
		int x;
		int y;
		for(int i = 0; i < coordZone.size(); i++) {
			x = ((Coordinates) coordZone.get(i)).getX();
			y = ((Coordinates) coordZone.get(i)).getY();
			m[x][y] = 1;
		}
		return m;
	}
	public void merge(zone A) {
		coordZone.addAll(A.getCoordZone());
	}
	public void filter() {
		//aanvullen
	}
} 
class zoneSort implements Comparator<zone>{
    public int compare(zone a, zone b) {
                return a.getZoneEl() - b.getZoneEl();
    	    }
}




