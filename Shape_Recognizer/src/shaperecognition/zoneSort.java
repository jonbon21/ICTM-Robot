package shaperecognition;

import java.util.Comparator;

public class zoneSort implements Comparator<zone>{
    public int compare(zone a, zone b) {
                return a.getZoneEl() - b.getZoneEl();
    	    }
}
