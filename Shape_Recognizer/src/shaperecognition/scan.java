package shaperecognition;

import java.util.ArrayList;

class scan {
	//This class processes the scanned matrix.
	//main methods:
	//1)prepare: This method sets the first and last column/row equal to zero. This is necessary for the method 'findZones' to work properly.
	//2)findzones: This method searches all zones (zones are objects of the class zone) in the scanned matrix and stores these zones in a list.
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
	for (int j = 0; j < map[0].length; j++) {
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
		ArrayList<Integer> updateOldZoneNr = new ArrayList<Integer> (); 
		ArrayList<Integer> updateNewZoneNr = new ArrayList<Integer> ();
		int[][] track = new int[map.length][map[0].length]; int matchNewZoneId = 0; int matchNewZoneNr = 0;
		track = elwiseAdd(track,-1);
		int zoneNr = -1; int ignore = 0; boolean matchNewZone = false; int zoneId = -1;
		for (int j = 1; j < map[0].length; j++) {
			for (int i = 1; i < map.length; i++) {
				if (map[i][j]!=0) {
					Coordinates P = new Coordinates(i,j);						
					if(map[i-1][j] == 0) {
						zoneNr++; zoneId++;
						zone z = new zone(P);
						zoneNew.add(z);
						zoneNew.get( zoneId ).setZoneNr(zoneNr);
						ignore = 0;
						track[i][j] = zoneNr;
					}
					else {
						((zone) zoneNew.get( zoneId )).addEl(P);
						track[i][j] = zoneNr;
					}
					if (map[i][j-1]!=0 && ignore == 0) {
						 matchNewZone = false;
						 for (int k = 0; k < updateOldZoneNr.size(); k++) {
							 if(track[i][j-1] == updateOldZoneNr.get(k)) {
								 matchNewZone = true;
								 matchNewZoneNr = updateNewZoneNr.get(k);
								 for (int m = 0; m < zoneOld.size(); m++) {
								 if(zoneNew.get(m).getZoneNr() == matchNewZoneNr) {matchNewZoneId = m;}
								 }
							 }
						 }
						 ignore = 1;
							 if((matchNewZone == true)&&(matchNewZoneNr != zoneNr)) {
								 ((zone) zoneNew.get(zoneId)).merge( (zone) zoneNew.get(matchNewZoneId));
								 zoneNew.remove(matchNewZoneId);
								 zoneId--;
								 for (int k = 0; k < track.length; k++) {
									 if(track[k][j] == matchNewZoneNr) {track[k][j] = zoneNr;} 
								 } 
							 //System.out.println("mergeNew x: "  + i + " y: " + j);
							 }
							 else if (matchNewZone == false) {
								 for(int k=0; k<zoneOld.size();k++) {
									 if(zoneOld.get(k).getZoneNr()==track[i][j-1]) {
										 ((zone) zoneNew.get(zoneId)).merge( (zone) zoneOld.get(k) );
									 }
								 }
							//System.out.println("mergeOld x: "  + i + " y: " + j);
							updateOldZoneNr.add(track[i][j-1]);
							updateNewZoneNr.add(track[i][j]);
							}		
							//System.out.println("AFTER j: " + j + " zonenNr: " + zoneNew.get( zoneNr ).getZoneNr());
					}
					if(map[i][j-1] == 0) {
						ignore = 0;
					}
				}
			}
			zoneNr = -1; zoneId = -1;	
			ignore = 0;
			for (int k = 0; k < updateOldZoneNr.size() ; k++) {
			for	(int m = zoneOld.size()-1; m >= 0; m--) {
				if(zoneOld.get(m).getZoneNr() == updateOldZoneNr.get(k)) {
					zoneOld.remove(m);
				}
				}
			}
			zoneList.addAll(zoneOld);
			zoneOld.clear();
			for (int k = 0; k < zoneNew.size(); k++) {
				zoneOld.add(new zone(zoneNew.get(k)));		
			}
			zoneNew.clear();
			updateOldZoneNr.clear();
			updateNewZoneNr.clear();
		}
	}
	public static void present(int[][] M){
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[i].length; j++) {
			System.out.print(M[i][j] + " ");
		}
		System.out.println();
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
