package com.robin.magic_realm.components.utility;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GameFileFilters {
	public GameFileFilters() {
	}
	
	public static FileFilter createGameDataFileFilter() {
		return new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || (f.isFile() && f.getPath().endsWith("xml"));
			}

			public String getDescription() {
				return "RealmSpeak Game Files (*.xml)";
			}
		};
	}
	
	public static FileFilter createSaveGameFileFilter() {
		return new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || (f.isFile() && f.getPath().endsWith("rsgame"));
			}
	
			public String getDescription() {
				return "RealmSpeak Save Files (*.rsgame)";
			}
		};
	}
}
