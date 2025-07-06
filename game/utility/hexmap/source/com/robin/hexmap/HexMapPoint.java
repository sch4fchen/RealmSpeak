package com.robin.hexmap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class HexMapPoint {
	private int x;
	private int y;
	public HexMapPoint(int x,int y) {
		this.x = x;
		this.y = y;
	}
	public String getKey() {
		return x+","+y;
	}
	public Point getPoint() {
		return new Point(x,y);
	}
	/**
	 * Initialize, based on toString representation
	 */
/*	public HexMapPoint(String s) {
		if (s.indexOf("HexMapPoint(")==0) {
			if (s.indexOf(")")==s.length()-1) {
				int comma = s.indexOf(",");
				try {
					Integer xi = Integer.valueOf(s.substring(12,comma));
					Integer yi = Integer.valueOf(s.substring(comma+1,s.length()-1));
					
					x = xi.intValue();
					y = yi.intValue();
					
					return;
				}
				catch(NumberFormatException ex) {
					// intentionally empty here
				}
			}
		}
		throw new IllegalArgumentException("Invalid initializer: "+s);
	}*/
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public HexMapPoint getPositionFromPlacement(Placement p) {
		return new HexMapPoint(x+p.getOffsetX(),y+p.getOffsetY());
	}
	public HexMapPoint getAdjacentPoint(int direction) {
		switch(direction) {
			case 0:	return new HexMapPoint(x,y-1);
			case 1:	return new HexMapPoint(x+1,y-1);
			case 2:	return new HexMapPoint(x+1,y);
			case 3:	return new HexMapPoint(x,y+1);
			case 4:	return new HexMapPoint(x-1,y+1);
			case 5:	return new HexMapPoint(x-1,y);
		}
		throw new IllegalArgumentException("Invalid direction: "+direction);
	}
	public HexMapPoint[] getAdjacentPoints() {
		HexMapPoint[] adjacentPoints = new HexMapPoint[6];
		adjacentPoints[0] = new HexMapPoint(x,y-1);
		adjacentPoints[1] = new HexMapPoint(x+1,y-1);
		adjacentPoints[2] = new HexMapPoint(x+1,y);
		adjacentPoints[3] = new HexMapPoint(x,y+1);
		adjacentPoints[4] = new HexMapPoint(x-1,y+1);
		adjacentPoints[5] = new HexMapPoint(x-1,y);
		return adjacentPoints;
	}
	public boolean equals(Object obj) {
		if (obj!=null && obj.getClass()==HexMapPoint.class) {
			HexMapPoint pos = (HexMapPoint)obj;
			return (x==pos.x && y==pos.y);
		}
		return false;
	}
	public int hashCode() {
		return x+y;
	}
	/**
	 * Returns the adjacent direction - if not adjacent, then -1
	 */
	public int directionOf(HexMapPoint pos) {
		if (x-1 == pos.x && y   == pos.y) {
			return 5;
		}
		else if (x+1 == pos.x && y   == pos.y) {
			return 2;
		}
		else if (x   == pos.x && y-1 == pos.y) {
			return 0;
		}
		else if (x   == pos.x && y+1 == pos.y) {
			return 3;
		}
		else if (x+1 == pos.x && y-1 == pos.y) {
			return 1;
		}
		else if (x-1 == pos.x && y+1 == pos.y) {
			return 4;
		}
		return -1;
	}
	
	/**
	 * Returns the distance (in hexes) from the desiginated pos
	 */
	public int getDistanceFrom(HexMapPoint pos) {
		int distance = -1;
	
		int dx = x - pos.x;
		int dy = y - pos.y;
		
		if ((dx<0 && dy>0) || (dx>0 && dy<0)) {
			// if opposite signs, then just return the absolute value of the largest
			int pdx = Math.abs(dx);
			int pdy = Math.abs(dy);
			
			distance = (pdx>pdy)?pdx:pdy;
		}
		else {
			distance = Math.abs(dx+dy);
		}
		
		return distance;
	}
	public boolean isAdjacentTo(HexMapPoint pos) {
		return directionOf(pos)>=0;
	}
	public String toString() {
		return "HexMapPoint("+x+","+y+")";
	}
	
	public static ArrayList<String> getKeyCollection(Collection<HexMapPoint> hexMapPoints) {
		ArrayList<String> keys = new ArrayList<>();
		for (HexMapPoint pos : hexMapPoints) {
			keys.add(pos.getKey());
		}
		return keys;
	}
	public static Collection<HexMapPoint> getHexMapPoints(Collection<String> keys) {
		ArrayList<HexMapPoint> hexMapPoints = new ArrayList<>();
		for (String key : keys) {
			hexMapPoints.add(readKey(key));
		}
		return hexMapPoints;
	}
	public static HexMapPoint readKey(String key) {
		StringTokenizer st = new StringTokenizer(key,",");
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		return new HexMapPoint(x,y);
	}
}