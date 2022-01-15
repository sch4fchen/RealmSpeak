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

public class CustomUiUtility {
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
			return Color.WHITE;
		case BACKGROUND_COLOR_2:
			return Color.LIGHT_GRAY;
		case BACKGROUND_COLOR_3:
			return Color.GRAY;
		}
	}
	
	public static void initColors() {
		Color color = getBackgroundColor();
		UIManager.put("Desktop.background", color);
		UIManager.put("InternalFrame.background", color);
		UIManager.put("Panel.background", color);
		
		UIManager.put("Button.background", color);
		UIManager.put("CheckBox.background",color);
		UIManager.put("CheckBoxMenuItem.background",color);
		UIManager.put("ComboBox.background",color);
		UIManager.put("EditorPane.background", color);
		UIManager.put("List.background",color);
		UIManager.put("OptionPane.background", color);
		UIManager.put("RadioButton.background",color);
		UIManager.put("RadioButtonMenuItem.background",color);
		UIManager.put("ScrollPane.background", color);
		UIManager.put("Slider.background",color);
		UIManager.put("TabbedPane.background",color);
		UIManager.put("Table.background",color);
		UIManager.put("TableHeader.background",color);
		UIManager.put("ToggleButton.background",color);
		
		/*Font font = UIManager.getFont("Button.font");
		font = UIManager.getFont("Checkbox.font");
		font = UIManager.getFont("CheckBoxMenuItem.font");
		font = UIManager.getFont("ComboBox.font");
		font = UIManager.getFont("EditorPane.font");
		font = UIManager.getFont("FormattedTextField.font");
		font = UIManager.getFont("IconButton.font");
		font = UIManager.getFont("InternalFrame.optionDialogTitleFont");
		font = UIManager.getFont("InternalFrame.paletteTitleFont");
		font = UIManager.getFont("InternalFrame.titleFont");
		font = UIManager.getFont("Label.font");
		font = UIManager.getFont("List.font");
		font = UIManager.getFont("Menu.font");
		font = UIManager.getFont("Menubar.font");
		font = UIManager.getFont("MenuItem.font");
		font = UIManager.getFont("OptionPane.buttonFont");
		font = UIManager.getFont("OptionPane.font");
		font = UIManager.getFont("OptionPane.messageFont");
		font = UIManager.getFont("Panel.font");
		font = UIManager.getFont("PasswordField.font");
		font = UIManager.getFont("PopupMenu.font");
		font = UIManager.getFont("ProgressBar.font");
		font = UIManager.getFont("RadioButton.font");
		font = UIManager.getFont("RadioButtonMenuItem.font");
		font = UIManager.getFont("ScrollPane.font");
		font = UIManager.getFont("Slider.font");
		font = UIManager.getFont("Spinner.font");
		font = UIManager.getFont("TabbedPane.font");
		font = UIManager.getFont("TabbedPane.smallFont");
		font = UIManager.getFont("Table.font");
		font = UIManager.getFont("TableHeader.font");
		font = UIManager.getFont("TextArea.font");
		font = UIManager.getFont("TextField.font");
		font = UIManager.getFont("TextPane.font");
		font = UIManager.getFont("TitledBorder.font");
		font = UIManager.getFont("ToggleButton.font");
		font = UIManager.getFont("ToolBar.font");
		font = UIManager.getFont("ToolTip.font");
		font = UIManager.getFont("Tree.font");
		font = UIManager.getFont("Viewport.font");*/
	}
}