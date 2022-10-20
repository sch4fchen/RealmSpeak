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
package com.robin.magic_realm.components.table;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.CenteredMapView;
import com.robin.magic_realm.components.swing.TileLocationChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class PeerClearingChooser {
	
	private static final String[] TYPES = {"woods","normal","mountain","water"};
	
	public PeerClearingChooser() {
	}
		
	public static TileLocation chooseAnyClearing(JFrame frame, CharacterWrapper character) {
		TileLocation planned = character.getPlannedLocation();
		CenteredMapView.getSingleton().setMarkClearingAlertText("Peer into which clearing?");
		CenteredMapView.getSingleton().markAllClearings(true);
		TileLocationChooser chooser = new TileLocationChooser(frame,CenteredMapView.getSingleton(),planned);
		chooser.setVisible(true);
		CenteredMapView.getSingleton().markAllClearings(false);
		return chooser.getSelectedLocation();
	}
	
	public static TileLocation chooseClearingFromMountain(JFrame frame, CharacterWrapper character) {
		TileLocation planned = character.getCurrentLocation();
		CenteredMapView.getSingleton().setMarkClearingAlertText("Peer into which clearing?");
		ArrayList<ClearingDetail> clearingsMarked = CenteredMapView.getSingleton().markClearingsInTile(planned.tile,Arrays.asList(TYPES),true);
		for(ClearingDetail clearing:clearingsMarked) {
			if (clearing.getParent().getGameObject().hasThisAttribute(Constants.SP_NO_PEER)) {
				clearing.setMarked(false);
			}
		}
		TileLocationChooser chooser = new TileLocationChooser(frame,CenteredMapView.getSingleton(),planned);
		chooser.setVisible(true);
		CenteredMapView.getSingleton().markAllClearings(false);
		return chooser.getSelectedLocation();
	}
}