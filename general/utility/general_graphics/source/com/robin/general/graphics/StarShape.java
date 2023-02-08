package com.robin.general.graphics;

import java.awt.*;

public class StarShape extends Polygon {
	public StarShape(int centerx,int centery,int points,int radius) {
		super();
		init(centerx,centery,points,radius);
	}
	public void init(int centerx,int centery,int points,int radius) {
		Point center = new Point(centerx,centery);
		double angle = 270; // always start first point on top
		Polar p = new Polar(centerx,centery,radius,(int)angle);
		double angleSize = 360.0/points;
		Point[] pointPoints = new Point[points];
		for (int i=0;i<points;i++) {
			p.setAngle((int)angle);
			pointPoints[i] = p.getRect();
			angle += angleSize;
		}
		for (int i=0;i<pointPoints.length;i++) {
			addPoint(pointPoints[i].x,pointPoints[i].y);
			AveragePoint ap = new AveragePoint(center);
			ap.addPoint(center);
			ap.addPoint(pointPoints[i]);
			ap.addPoint(pointPoints[(i+1)%pointPoints.length]);
			addPoint(ap.x,ap.y);
		}
	}
}