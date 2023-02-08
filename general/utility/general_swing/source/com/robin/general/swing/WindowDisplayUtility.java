package com.robin.general.swing;

import java.awt.Window;

import javax.swing.SwingUtilities;

/**
 * This class will guarantee that a window is displayed on the GUI thread, where it belongs.
 */
public class WindowDisplayUtility implements Runnable {
	private Window window;
	private WindowDisplayUtility(Window window) {
		this.window = window;
	}
	public void run() {
		window.setVisible(true);
	}
	public static void displayWindow(Window window) {
		WindowDisplayUtility util = new WindowDisplayUtility(window);
		SwingUtilities.invokeLater(util);
		window.setVisible(true);
	}
}