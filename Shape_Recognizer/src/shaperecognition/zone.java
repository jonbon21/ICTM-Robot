package shaperecognition;

import java.util.ArrayList;

public class zone {
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
	public void fillHoles(int dimX, int dimY) {
		int counter = 0;
		int[][] M = this.getArrayZone(dimX, dimY);
		int[][] bX = new int[dimY][2]; 
		int[][] bY = new int[dimX][2];
		for (int i = 0; i < dimX ; i++) {
			bY[i][0] = dimY;
		}
		for (int j = 0; j < dimY ; j++) {
			bX[j][0] = dimX;
		}
		for (int i = 1; i < dimX-1 ; i++) {
			for (int j = 1; j < dimY-1 ; j++) {
				if(M[i][j] == 1) {
					if(i < bX[j][0]) {bX[j][0] = i;} 
					if(i > bX[j][1]) {bX[j][1] = i;} 
					if(j < bY[i][0]) {bY[i][0] = j;} 
					if(j > bY[i][1]) {bY[i][1] = j;} 
				}
			}
		}
		for (int i = 1; i < dimX-1 ; i++) {
			for (int j = 1; j < dimY-1 ; j++) {
				if(M[i][j] == 0) {
					if(((i > bX[j][0]) && (i < bX[j][1])) && ((j > bY[i][0]) && (j < bY[i][1]))) {
						this.addEl(new Coordinates(i,j));
						counter++;
					}
				}
			}
		}
		if(counter >= 3) {System.out.println("object with holes");}
	}
} 

