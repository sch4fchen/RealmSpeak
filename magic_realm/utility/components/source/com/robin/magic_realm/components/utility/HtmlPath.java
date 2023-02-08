package com.robin.magic_realm.components.utility;

import java.io.File;

public class HtmlPath {
	private String currentDirectory;

	public HtmlPath(String currentDirectory) {
		this.currentDirectory = currentDirectory;
		File dir = new File(currentDirectory);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
	public String toString() {
		return currentDirectory;
	}

	public HtmlPath newDirectory(String dirName) {
		return new HtmlPath(currentDirectory + File.separator + dirName);
	}
	
	public HtmlPath upOneDirectory() {
		File path = new File(currentDirectory);
		return new HtmlPath(path.getParent());
	}
	
	public String path() {
		return currentDirectory;
	}
	
	public String path(String filename) {
		return currentDirectory + File.separator + filename;
	}
	
	public HtmlPath relativePathTo(HtmlPath path) {
		if (path.currentDirectory.startsWith(currentDirectory)) {
			String ret = path.currentDirectory.substring(currentDirectory.length());
			while(ret.startsWith(File.separator)) ret = ret.substring(1);
			return new HtmlPath(ret);
		}
		return null;
	}
}