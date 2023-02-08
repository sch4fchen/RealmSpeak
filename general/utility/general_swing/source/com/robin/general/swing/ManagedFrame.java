package com.robin.general.swing;

public interface ManagedFrame {
	public void dispose();
	public void setVisible(boolean val);
	public String getKey();
	public void toFront();
	public void cleanup();
}