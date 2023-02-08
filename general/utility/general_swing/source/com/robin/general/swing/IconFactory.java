package com.robin.general.swing;

import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;

public class IconFactory {
	private static final ClassLoader sysLoader=ClassLoader.getSystemClassLoader();
	public static ImageIcon findIcon(String path) {
		return findIcon(null,path);
	}
	public static ImageIcon findIcon(Class c, String path) {
		ImageIcon i=null;
		File f = new File(path);
		if (f.exists()) {
			i=new ImageIcon(path);
		}
		else {
			URL u=null;
			if (c==null) {
				u=sysLoader.getResource(path);
			}
			else {
				u=c.getResource(path);
			}
			if (u!=null) {
				i=new ImageIcon(u);
			}
		}
//		if (i==null) { // Sometimes I want a null return without an error, so don't report this!
//			System.err.println("Icon not found for "+path);
//		}
		return i;
	}
}