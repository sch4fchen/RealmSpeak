package com.robin.magic_realm.components;

import java.util.*;
import java.awt.*;

import com.robin.game.objects.*;
import com.robin.general.util.*;

public class TileEditComponent extends TileComponent {
	
	private boolean changed = false;
	
	public TileEditComponent(GameObject obj) {
		super(obj);
		imageTransparency = 50;
		setAlwaysPaint(true);
	}
	
	public Collection<ClearingDetail> getClearingDetail() {
		return clearings[getFacingIndex()];
	}
	public void setClearingDetail(Collection<ClearingDetail> c) {
		clearings[getFacingIndex()] = new ArrayList<ClearingDetail>(c);
		changed = true;
		repaint();
	}
	public Collection<PathDetail> getPathDetail() {
		return paths[getFacingIndex()];
	}
	public void setPathDetail(Collection<PathDetail> c) {
		paths[getFacingIndex()] = new ArrayList<PathDetail>(c);
		changed = true;
		repaint();
	}
	public Point getOffroadPos() {
		return offroadPos[getFacingIndex()];
	}
	public void setOffroadPos(Point pos) {
		offroadPos[getFacingIndex()] = pos;
		changed = true;
	}
	/**
	 * Translates paths and clearings detail back into the gameObject
	 */
	public void applyChanges() {
		String blockName = isEnchanted()?"enchanted":"normal";
		
		// First, rip out all clearing/path keys from the side
		OrderedHashtable hash = gameObject.getAttributeBlock(blockName);
		ArrayList<String> keysToRemove = new ArrayList<>();
		for (Enumeration e=hash.keys();e.hasMoreElements();) {
			String key = (String)e.nextElement();
			if (key.startsWith("path") || key.startsWith("clearing")) {
				keysToRemove.add(key);
			}
		}
		for (String key : keysToRemove) {
			hash.remove(key);
		}
		
		// Now add them back
		for (ClearingDetail detail : clearings[getFacingIndex()]) {
			String baseKey = detail.toString();
			gameObject.setAttribute(blockName,baseKey+"_type",detail.getType());
			gameObject.setAttribute(blockName,baseKey+"_xy",encodePoint(detail.getPosition()));
			
			StringBuffer magic = new StringBuffer();
			for (int m=ClearingDetail.MAGIC_WHITE;m<=ClearingDetail.MAGIC_VARIED;m++) {
				if (detail.getMagic(m)) {
					magic.append(ClearingDetail.MAGIC_CHAR[m]);
				}
			}
			if (magic.length()>0) {
				gameObject.setAttribute(blockName,baseKey+"_magic",magic.toString());
			}
		}
		
		int n=1;
		for (PathDetail detail : paths[getFacingIndex()]) {
			String baseKey = "path_"+n;
			gameObject.setAttribute(blockName,baseKey+"_from",detail.getFrom().toString());
			gameObject.setAttribute(blockName,baseKey+"_to",detail.getTo().toString());
			gameObject.setAttribute(blockName,baseKey+"_type",detail.getType());
			Point arc = detail.getArcPoint();
			if (arc!=null) {
				gameObject.setAttribute(blockName,baseKey+"_arc",encodePoint(arc));
			}
			n++;
		}
		
		// Don't forget the offroadPos detail
		gameObject.setAttribute(blockName,"offroad_xy",encodePoint(offroadPos[getFacingIndex()]));
		
		changed = false;
	}
	
	protected String encodePoint(Point p) {
		String px = Double.valueOf((p.x*100.0)/TILE_WIDTH).toString()+". ";
		px = px.substring(0,px.indexOf(".")+2);
		String py = Double.valueOf((p.y*100.0)/TILE_HEIGHT).toString()+". ";
		py = py.substring(0,py.indexOf(".")+2);
		return px+","+py;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw offroad pos
		g.setColor(Color.black);
		g.drawRect(offroadPos[getFacingIndex()].x-25,offroadPos[getFacingIndex()].y-25,50,50);
	}
	public void didChange() {
		changed = true;
	}
	public boolean isChanged() {
		return changed;
	}
}