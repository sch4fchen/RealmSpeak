package com.robin.magic_realm.components.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.robin.general.swing.AggressiveDialog;
import com.robin.magic_realm.components.attribute.TileLocation;

public class TileLocationChooser extends AggressiveDialog {
	private static Rectangle lastDisplayArea = null;
	
	private CenteredMapView viewer;
	private TileLocation selectedLocation;
	
	private MouseAdapter mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent ev) {
			TileLocation tl = viewer.getTileLocationAtPoint(ev.getPoint());
			if (tl!=null) {
				if (tl.tile.isMarked()) {
					tl.clearing = null; // clicking a marked tile nullifies any clearing result
					selectedLocation = tl;
					cleanClose();
				}
				if (tl.hasClearing() && tl.clearing.isMarked()) {
					selectedLocation = tl;
					cleanClose();
				}
			}
		}
	};

	public TileLocationChooser(JFrame parent,CenteredMapView map,TileLocation center) { // May want to include a cancel option here...
		this(parent,map,center,"Clearing Chooser");
	}
	
	public TileLocationChooser(JFrame parent,CenteredMapView map,TileLocation center,String title) { // May want to include a cancel option here...
		super(parent,title,true);
		this.viewer = map;
		selectedLocation = null;
		initComponents();
		if (lastDisplayArea == null) {
			setSize(800,600);
			setLocationRelativeTo(parent);
		}
		else {
			setSize(lastDisplayArea.width,lastDisplayArea.height);
			setLocation(lastDisplayArea.x,lastDisplayArea.y);
		}
		initMapSize();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		if (center!=null) {
			viewer.centerOn(center);
		}
		else {
			viewer.centerMap();
		}
	}
	private void initMapSize() {
		Dimension s = getSize();
		Insets i = getInsets();
		int w = s.width-i.left-i.right;
		int h = s.height-i.top-i.bottom;
		viewer.setSize(w,h);
	}
	public TileLocation getSelectedLocation() {
		return selectedLocation;
	}
	private void initComponents() {
		getContentPane().setLayout(new BorderLayout());
		viewer.addMouseListener(mouseListener);
		getContentPane().add(viewer,"Center");
 	}
	private void cleanClose() {
		viewer.removeMouseListener(mouseListener);
		lastDisplayArea = new Rectangle(getLocation().x,getLocation().y,getSize().width,getSize().height);
		setVisible(false);
		dispose();
	}
}