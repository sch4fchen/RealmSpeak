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

import java.awt.*;

import javax.swing.UIManager;

import com.robin.general.io.PreferenceManager;

public class CustomColorUtility {
	private final static Color WHITE = Color.WHITE;
	private final static Color LIGHT = Color.LIGHT_GRAY;
	private final static Color DARK = Color.DARK_GRAY;
	
	public static final String BACKGROUND_COLOR = "backgroundColor";
	public static final int BACKGROUND_COLOR_0 = 0;
	public static final int BACKGROUND_COLOR_1 = 1;
	public static final int BACKGROUND_COLOR_2 = 2;
	public static final int BACKGROUND_COLOR_3 = 3;
	
	public static Color getBackgroundColor() {
		PreferenceManager gamePrefMan = RealmUtility.getRealmSpeakPrefs();
		if (gamePrefMan.canLoad()) {
			gamePrefMan.loadPreferences();
		}
		
		return getBackgroundColorFromSelection(gamePrefMan.getInt(BACKGROUND_COLOR));
	}
	
	public static Color getBackgroundColorFromSelection(int selection) {
		switch(selection) {
		default:
		case BACKGROUND_COLOR_0:
			return null;
		case BACKGROUND_COLOR_1:
			return WHITE;
		case BACKGROUND_COLOR_2:
			return LIGHT;
		case BACKGROUND_COLOR_3:
			return DARK;
		}
	}
	
	public static void initColors() {
		Color color = getBackgroundColor();
		UIManager.put("Panel.background", color);
		UIManager.put("ScrollPane.background", color);
		UIManager.put("OptionPane.background", color);
		UIManager.put("Button.background", color);
		UIManager.put("CheckBox.background",color);
		UIManager.put("CheckBoxMenuItem.background",color);
		UIManager.put("ComboBox.background",color);
		UIManager.put("RadioButton.background",color);
		UIManager.put("RadioButtonMenuItem.background",color);
		UIManager.put("Slider.background",color);
		//UIManager.put("Table.foreground",color);
		//UIManager.put("Table.background",color);
		//UIManager.put("TableHeader.foreground",color);
		//UIManager.put("TableHeader.background",color);
	}
}