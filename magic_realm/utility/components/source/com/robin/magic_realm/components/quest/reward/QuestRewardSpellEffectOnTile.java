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
package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.SpellCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffectOnTile extends QuestReward {
	
	public static final String SPELL = "_spell";
	public static final String REMOVE = "_remove";
	
	public enum EffectOnTile {
		Fog,
		ViolentStorm,
	}
	
	public QuestRewardSpellEffectOnTile(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String spell;
		switch (getSpell()) {
		case Fog:
			spell = "fog";
			break;
		case ViolentStorm:
			spell = "violent storm";
			break;
		default:
			return;
		}
		
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		SpellWrapper spellWrapper = SpellCreator.CreateSpellWrapper(spell, character);
		
		TileLocation charactersLocation = character.getCurrentLocation();
		if (charactersLocation != null && charactersLocation.tile != null) {
			spellWrapper.addTarget(hostPrefs, charactersLocation.tile.getGameObject());
		}
		
		if (remove()) {
			spellWrapper.unaffectTargets();
			return;
		}
		spellWrapper.affectTargets(frame, gameWrapper, false, null);
	}
	
	private EffectOnTile getSpell() {
		return EffectOnTile.valueOf(getString(SPELL));
	}
	
	private Boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		if (remove()) {
			return("Remove the spell "+getSpell()+" from the characters tile.");
		}
		return "Cast the spell "+getSpell()+" on the characters tile";
	}
	public RewardType getRewardType() {
		return RewardType.SpellEffectOnTile;
	}
}