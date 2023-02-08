package com.robin.general.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class GameOption {
	public static final Color ACTIVE_COLOR = new Color(210,255,210);
	public static final Color INACTIVE_COLOR = Color.lightGray;
	
	private String key;
	private String description;
	private JTextArea area;
	private JCheckBox activeCB;
	private JPanel panel;
	
	private ArrayList<String> overrideKeys;
	private ArrayList<String> includeKeys;
	private ArrayList<String> cantHaveKeys;
	
	private GameOptionPane gameOptionPane;
	
	private ActionListener listener = null;
	
	public GameOption(String key,String description,boolean active) {
		this(key,description,active,null,null);
	}
	public GameOption(String inKey,String description,boolean active,String[] overrides,String[] includes) {
		this(inKey,description,active,overrides,includes,null);
	}
	public GameOption(String inKey,String description,boolean active,String[] overrides,String[] includes,String[] cantHaves) {
		this.key = inKey;
		this.description = description;
		overrideKeys = new ArrayList<>();
		if (overrides!=null) {
			overrideKeys.addAll(Arrays.asList(overrides));
		}
		includeKeys = new ArrayList<>();
		if (includes!=null) {
			includeKeys.addAll(Arrays.asList(includes));
		}
		cantHaveKeys = new ArrayList<>();
		if (cantHaves!=null) {
			cantHaveKeys.addAll(Arrays.asList(cantHaves));
		}
		panel = new JPanel(new BorderLayout());
			area = new JTextArea(description);
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
			area.setEditable(false);
			area.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent ev) {
					if (activeCB.isEnabled()) {
						setActive(!activeCB.isSelected());
//						activeCB.setSelected(!activeCB.isSelected());
//						updateOthers();
//						updateColor();
					}
				}
			});
			area.setBorder(BorderFactory.createEtchedBorder());
		panel.add(area,"Center");
			Box box = Box.createVerticalBox();
				activeCB = new JCheckBox();
				activeCB.setSelected(active);
				activeCB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						setActive(activeCB.isSelected());
						//updateOthers();
						//updateColor();
					}
				});
			box.add(activeCB);
			box.add(Box.createVerticalGlue());
		panel.add(box,"West");
		panel.add(Box.createVerticalStrut(30),"East");
		panel.setMaximumSize(new Dimension(2000,50));
		updateColor();
	}
	public void setActionListener(ActionListener listener) {
		this.listener = listener;
	}
	public void setEnabled(boolean val) {
		activeCB.setEnabled(val);
	}
	private void updateOthers() {
		if (activeCB.isSelected()) {
			// turn off overrides
			for (String overrideKey : getOverrideKeys()) {
				if (!overrideKey.equals(key)) {
					gameOptionPane.setOption(overrideKey,false);
				}
			}
			
			// turn on includes
			for (String includeKey : getIncludeKeys()) {
				if (!includeKey.equals(key)) {
					gameOptionPane.setOption(includeKey,true);
				}
			}
		}
		else {
			// turn off cant haves
			for (String cantHaveKey : getCantHaveKeys()) {
				if (!cantHaveKey.equals(key)) {
					gameOptionPane.setOption(cantHaveKey,false);
				}
			}
		}
	}
	public Collection<String> getOverrideKeys() {
		return overrideKeys;
	}
	public Collection<String> getIncludeKeys() {
		return includeKeys;
	}
	public Collection<String> getCantHaveKeys() {
		return cantHaveKeys;
	}
	private void updateColor() {
		area.setBackground(activeCB.isSelected()?ACTIVE_COLOR:INACTIVE_COLOR);
	}
	public String getKey() {
		return key;
	}
	public String getDescription() {
		return description;
	}
	public void setActive(boolean val) {
		activeCB.setSelected(val);
		updateOthers();
		updateColor();
		if (listener!=null) {
			listener.actionPerformed(new ActionEvent(GameOption.this,0,""));
		}
	}
	public boolean isActive() {
		return activeCB.isSelected();
	}
	public JPanel getPanel() {
		return panel;
	}
	public void setGameOptionPane(GameOptionPane gameOptionPane) {
		this.gameOptionPane = gameOptionPane;
	}
}