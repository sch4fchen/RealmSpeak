/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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
