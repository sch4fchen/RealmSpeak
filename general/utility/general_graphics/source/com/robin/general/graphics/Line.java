package com.robin.general.graphics;

import java.awt.Point;

public class Line {
	public Point p1;
	public Point p2;
	public Line(Point p1,Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	public boolean isStraightLine() {
		return p1.x==p2.x || p1.y==p2.y;
	}
	public boolean isEndpoint(Point p) {
		return p.equals(p1) || p.equals(p2);
	}
	public boolean contains(Point p) {
		if (isStraightLine()) {
			if ((p1.x==p2.x && p.x==p2.x && between(p.y,p1.y,p2.y)) ||
					(p1.y==p2.y && p.y==p2.y && between(p.x,p1.x,p2.x))) {
				return true;
			}
		}
		else {
			// TODO Finish this code someday ...
		}
		return false;
	}
	private static boolean between(int n,int n1,int n2) {
		return (n1<n2?(n>=n1 && n<=n2):(n>=n2 && n<=n1));
	}
}