package com.robin.general.graphics;

import java.awt.*;
import java.util.*;

/**
 * A AveragePoint is the center point of all the points in its
 * collection.
 */
public class AveragePoint extends Point {
	protected ArrayList<Point> points;
	public AveragePoint(int x,int y) {
		super(x,y);
		points = new ArrayList<>();
		addPoint(x,y);
	}
	public AveragePoint(Point p) {
		super(p);
		points = new ArrayList<>();
		addPoint(p);
	}
	public void addPoint(int valX,int valY) {
		addPoint(new Point(valX,valY));
	}
	public void addPoint(Point p) {
		points.add(p);
		refresh();
	}
	public Polygon getPolygon() {
		Polygon poly = new Polygon();
		for (Point p : points) {
			poly.addPoint(p.x,p.y);
		}
		return poly;
	}
	/**
	 * This will update the x and y values to reflect an average
	 * of all the points (equals the center point)
	 */
	private void refresh() {
		int tx = 0;
		int ty = 0;
		int n = 0;
		for (Point p : points) {
			tx += p.x;
			ty += p.y;
			n++;
		}
		this.x = tx/n;
		this.y = ty/n;
	}
	public boolean equals(AveragePoint p) {
		return (p.x==x && p.y==y);
	}
}