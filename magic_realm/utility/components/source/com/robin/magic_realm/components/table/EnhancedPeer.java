package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class EnhancedPeer extends Peer {
	public EnhancedPeer(JFrame parent,ClearingDetail clearing) {
		super(parent,clearing);
	}
	public String getTableName(boolean longDescription) {
		return targetClearing.getDescription();
	}
	public String apply(CharacterWrapper character, DieRoller inRoller) {
		if (targetClearing.getParent().getGameObject().hasThisAttribute(Constants.SP_NO_PEER) || targetClearing.getParent().getGameObject().hasThisAttribute(Constants.EVENT_FOG)) {
			return "Cannot Peer Here: FOG";
		}
		return super.apply(character,inRoller);
	}
	@Override
	protected ArrayList<ImageIcon> getHintIcons(CharacterWrapper character) {
		return new ArrayList<>();
	}
}