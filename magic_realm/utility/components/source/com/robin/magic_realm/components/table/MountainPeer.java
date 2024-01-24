package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MountainPeer extends Peer {
	
	public MountainPeer(JFrame frame) {
		super(frame);
	}
	public String getTableName(boolean longDescription) {
		return "Peer Neighboring Clearing";
	}
	public String apply(CharacterWrapper character, DieRoller inRoller) {
		TileLocation loc = character.getCurrentLocation();
		if (loc!=null && loc.clearing!=null) {
			for (RealmComponent rc : loc.clearing.getDeepClearingComponents()) {
				if (rc.getGameObject().hasThisAttribute(Constants.MIST_CRYSTAL)) {
					return loc.toString()+": Affected by Mist Crystal, Peering not possible";
				}
			}
		}
		TileLocation tl = PeerClearingChooser.chooseClearingFromMountain(getParentFrame(), character);
		targetClearing = tl.clearing;
		return tl.toString()+": "+super.apply(character,inRoller);
	}
	@Override
	protected ArrayList<ImageIcon> getHintIcons(CharacterWrapper character) {
		return new ArrayList<>();
	}
}