package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.CenteredMapView;
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
		ClearingDetail targetClearing = null;
		
		if (!activeParticipant.isCharacter()) return true;
		CharacterWrapper character = new CharacterWrapper(activeParticipant.getGameObject());
		TileLocation current = character.getCurrentLocation();
		if (current==null||current.tile==null) return true;
		
		CenteredMapView map = CenteredMapView.getSingleton();
		if (map.getTiles().size()==1) return true; //BattleBuilder

		if (character.getPeerAny()) {
			TileLocation tl = PeerClearingChooser.chooseAnyClearing(combatFrame, character);
			targetClearing = tl.clearing;
		}
		else {
			RealmCalendar cal = RealmCalendar.getCalendar(character.getGameData());
			boolean canPeer = character.canPeer() && !cal.isPeerDisabled(character.getCurrentMonth());
			if (canPeer && current.clearing.isMountain()) {
				TileLocation tl = PeerClearingChooser.chooseClearingFromMountain(combatFrame, character);
				targetClearing = tl.clearing;
			}
		}
		
		//choose 2nd clearing -> roadway between the two clearings
		
		return true;
	}

	public boolean assign(HostPrefWrapper hostPrefs, CharacterWrapper activeCharacter) {
		return true;
	}

	public boolean hasTargets() {
		return !gameObjects.isEmpty();
	}
}