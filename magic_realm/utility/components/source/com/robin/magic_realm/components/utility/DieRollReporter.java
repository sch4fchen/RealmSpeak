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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.robin.general.swing.DieRoller;

public class DieRollReporter {

//	Not sure yet where I will need this -- CJM
//	public static void sendMessage(DieRoller roller, GameData data,String clientName,String title,String message) {
//		ArrayList strings = new ArrayList();
//		strings.add(title);
//		strings.add(message);
//		strings.add(roller==null?"":roller.getStringResult());
//		RealmDirectInfoHolder holder = new RealmDirectInfoHolder(data);
//		holder.setCommand(RealmDirectInfoHolder.POPUP_MESSAGE);
//		holder.setStrings(strings);
//		if (GameClient.GetMostRecentClient()!=null && !GameClient.GetMostRecentClient().getClientName().equals(clientName)) {
//			GameClient.GetMostRecentClient().sendInfoDirect(clientName, holder.getInfo(),true);
//		}
//		else {
//			// Running in Battle Simulator or getting a curse during the day
//			RealmUtility.popupMessage(null,holder); // DO NOT CHANGE THE --NULL-- to parentFrame.  This breaks the Curse logic
//		}
//	}
	
	public static void showMessageDialog(DieRoller roller, JFrame frame, String title, String message, int msgType){
		JOptionPane.showMessageDialog(frame, message,  title, msgType, getRollerImage(roller));
	}
	
	private static ImageIcon getRollerImage(DieRoller roller) {
		if (roller!=null) {
			return roller.getIcon();
		}
		return null;
	}
}
