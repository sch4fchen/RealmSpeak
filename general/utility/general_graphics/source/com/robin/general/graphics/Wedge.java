package com.robin.general.graphics;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Wedge {

	public Wedge(Point center, int radius) {
		this(center, radius, 0, 0);
	}

	public Wedge(Point center, int radius, int start, int finish) {
		this.center = new AveragePoint(center);
		this.radius = radius;
		this.start = start;
		this.finish = finish;
	}

	public int getRadius() {
		return radius;
	}

	public AveragePoint getPlotPosition() {
		if (start == finish)
			return center;
		Point p1 = (new Polar(center.x, center.y, radius, start)).getRect();
		Point p2 = (new Polar(center.x, center.y, radius, finish)).getRect();
		for (; start > finish; start -= 360)
			;
		int mid = start + (finish - start >> 1);
		Point p3 = (new Polar(center.x, center.y, radius, mid)).getRect();
		AveragePoint pos = new AveragePoint(center);
		pos.addPoint(p1);
		pos.addPoint(p3);
		pos.addPoint(p2);
		pos.addPoint(center);
		return pos;
	}

	public ArrayList<Wedge> makeWedges(int divisions) {
		return makeWedges(divisions, false);
	}

	public ArrayList<Wedge> makeWedges(int divisions, boolean moveCenter) {
		if (divisions <= 0)
			throw new IllegalArgumentException("Wedge divisions must be greater than zero.");
		ArrayList<Wedge> wedges = new ArrayList<>();
		Point newCenter = center;
		double st = start;
		double fin = finish;
		int r = radius;
		if (moveCenter) {
			newCenter = getPlotPosition();
			r = radius - (int) Point2D.distance(newCenter.x, newCenter.y, center.x, center.y);
			Point s = (new Polar(center.x, center.y, radius, start)).getRect();
			Point f = (new Polar(center.x, center.y, radius, finish)).getRect();
			Polar ps = new Polar(newCenter, s);
			Polar pf = new Polar(newCenter, f);
			st = ps.getAngle();
			fin = pf.getAngle();
		}
		for (; st > fin; st -= 360D)
			;
		if ((int) st == (int) fin) {
			fin += 360D;
			if (divisions < 3)
				divisions++;
		}
		double range = fin - st;
		double d = range / divisions;
		for (int i = 0; i < divisions; i++) {
			wedges.add(new Wedge(newCenter, r, (int) st, (int) (st + d)));
			st += d;
		}

		return wedges;
	}

	public Polygon getPolygon() {
		return getPlotPosition().getPolygon();
	}

	public String toString() {
		return "Wedge@(" + center.x + ',' + center.y + ") from " + start + " to " + finish + ", radius=" + radius;
	}

	public boolean isLike(Wedge wedge) {
		if (wedge != null) {
			return wedge.center.x == center.x && wedge.center.y == center.y && wedge.radius == radius && wedge.start == start && wedge.finish == finish;
		}
		return false;
	}

	private AveragePoint center;
	private int radius;
	private int start;
	private int finish;
}