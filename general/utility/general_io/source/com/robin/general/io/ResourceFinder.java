package com.robin.general.io;

import java.io.*;
import java.net.URL;

public class ResourceFinder {
	private static final ClassLoader sysLoader=ClassLoader.getSystemClassLoader();

	public static InputStream getInputStream(String path) {
		InputStream stream = null;
		
		File f=new File(path);
		if (f.exists()) {
			try {
				return new FileInputStream(f);
			}
			catch(IOException ex) {
				// ignore
			}
		}
		
		URL u=sysLoader.getResource(path);
		if (u!=null) {
			try {
				stream = u.openStream();
			}
			catch(IOException ex) { }
		}
		return stream;
	}
	public static boolean exists(String path) {
		File f=new File(path);
		if (f.exists()) {
			return true;
		}
		URL u=sysLoader.getResource(path);
		if (u!=null) {
			return true;
		}
		return false;
	}
	public static String getString(String path) {
		try {
			InputStream stream = getInputStream(path);
			if (stream!=null) {
				StringBuffer sb = new StringBuffer();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String line;
				while((line=reader.readLine())!=null) {
					sb.append(line);
					sb.append("\n");
				}
				return sb.toString();
			}
		}
		catch(IOException ex) {
			// Do nothing
		}
		return null;
	}
}