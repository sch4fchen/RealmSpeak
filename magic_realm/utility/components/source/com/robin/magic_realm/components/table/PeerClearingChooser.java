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
		return chooseClearingFromMountain(frame,character,"Peer into which clearing?");
	}
	public static TileLocation chooseClearingFromMountain(JFrame frame, CharacterWrapper character, String text) {
		TileLocation planned = character.getCurrentLocation();
		CenteredMapView.getSingleton().setMarkClearingAlertText(text);
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