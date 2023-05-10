package com.robin.magic_realm.RealmBattle.targeting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.CenteredMapView;
import com.robin.magic_realm.components.swing.TileLocationChooser;
import com.robin.magic_realm.components.table.PeerClearingChooser;
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingRoadway extends SpellTargeting {

	public SpellTargetingRoadway(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame,spell);
	}
	
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		return true;
	}

	public boolean assign(HostPrefWrapper hostPrefs, CharacterWrapper character) {
		ClearingDetail targetClearing = null;
		TileLocation current = character.getCurrentLocation();
		if (current==null||current.tile==null) return true;
		
		CenteredMapView map = CenteredMapView.getSingleton();
		if (map.getTiles().size()==1) return true; //BattleBuilder

		TileLocation targetedTileLocation = null;
		if (character.getPeerAny()) {
			targetedTileLocation = PeerClearingChooser.chooseAnyClearing(combatFrame, character);
			targetClearing = targetedTileLocation.clearing;
		}
		else {
			RealmCalendar cal = RealmCalendar.getCalendar(character.getGameData());
			boolean canPeer = character.canPeer() && !cal.isPeerDisabled(character.getCurrentMonth());
			if (canPeer && current.clearing.isMountain()) {
				targetedTileLocation = PeerClearingChooser.chooseClearingFromMountain(combatFrame, character, "Choose a roadway between two clearings. Select first clearing.");
				targetClearing = targetedTileLocation.clearing;
			}
		}
		
		if (targetedTileLocation==null || targetClearing==null) return true;
		TileLocation targetedTileLocation2 = null;
		while (targetedTileLocation2==null||targetedTileLocation2.tile==null||targetedTileLocation2.clearing==null||targetedTileLocation2.equals(targetedTileLocation)) {
			TileLocationChooser clearingChooser = new TileLocationChooser(new JFrame(),CenteredMapView.getSingleton(),targetedTileLocation,"Select a second clearing. It must be a different clearing.");
			CenteredMapView.getSingleton().markClearings(new ArrayList<>(Arrays.asList(targetClearing)),true,Color.red);
			CenteredMapView.getSingleton().markClearingConnections(targetClearing,true);
			clearingChooser.setVisible(true);
			targetedTileLocation2 = clearingChooser.getSelectedLocation();
			CenteredMapView.getSingleton().markAllClearings(false);
		}
		
		gameObjects.add(targetedTileLocation.tile.getGameObject());
		gameObjects.add(targetedTileLocation2.tile.getGameObject());
		spell.setExtraIdentifier(targetedTileLocation.clearing.getNum()+"_"+targetedTileLocation2.clearing.getNum());
		spell.addTarget(hostPrefs,targetedTileLocation.tile.getGameObject());
		spell.addTarget(hostPrefs,targetedTileLocation2.tile.getGameObject());
		
		return true;
	}

	public boolean hasTargets() {
		CharacterWrapper character = new CharacterWrapper(spell.getCaster().getGameObject());
		TileLocation current = character.getCurrentLocation();
		if (current==null||current.tile==null) return false;
		
		CenteredMapView map = CenteredMapView.getSingleton();
		if (map.getTiles().size()==1) return false; //BattleBuilder

		RealmCalendar cal = RealmCalendar.getCalendar(character.getGameData());
		boolean canPeer = character.canPeer() && !cal.isPeerDisabled(character.getCurrentMonth());
		if (character.getPeerAny() || (canPeer && current.clearing.isMountain())) {
			return true;
		}

		return false;
	}
}