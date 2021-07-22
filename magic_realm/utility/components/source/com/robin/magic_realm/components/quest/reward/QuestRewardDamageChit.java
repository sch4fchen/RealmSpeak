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

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.quest.DamageType;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDamageChit extends QuestReward {	
	public static final String DAMAGE_TYPE = "_dt";
	public static final String TYPE = "_type";
	public static final String STRENGTH = "_strength";
	public static final String SPEED = "_speed";
	public static final String MAGIC_COLOR = "_magic_color";
	public static final String MAGIC_TYPE = "_magic_type";
	public static final String ONLY_ACTIVE = "_only_active";
	
	public enum ChitType {
		Any,
		Move,
		Fight,
		Magic,
		Fly
	}
		
	public QuestRewardDamageChit(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		ArrayList<CharacterActionChitComponent> chitsToCheck = character.getAllChits();
		ArrayList<CharacterActionChitComponent> chits = new ArrayList<>();
		switch(getType()) {
		case Move:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isMove()) chits.add(chit);
			}
			break;
		case Fight:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isFight()) chits.add(chit);
			}
			break;
		case Magic:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isMagic() && chit.getMagicNumber()==getMagicType() && (getMagicColor().matches("Any") || chit.getColorMagic().getColorName().matches(getMagicColor()))) {
					chits.add(chit);
				}
			}
			break;
		case Fly:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isFly()) chits.add(chit);
			}
			break;
		case Any:
		default:
			chits.addAll(chitsToCheck);
			break;
		}
		chitsToCheck.clear();
		chitsToCheck.addAll(chits);
		chits.clear();
		for (CharacterActionChitComponent chit : chitsToCheck) {
				if (getStrength() != VulnerabilityType.Any && chit.getStrength().weakerTo(new Strength(getStrength().toString()))) continue;
				if (getSpeed() != 0 && chit.getSpeed().getNum()>getSpeed()) continue;
				if (onlyActive() && !chit.isActive()) continue;
				if (getDamageType() == DamageType.Wounds && chit.isWounded()) continue;
				chits.add(chit);
		}
		
		String dmgType = "";
		switch (getDamageType()) {
			case WeatherFatigue:
				dmgType = "fatigue";
				break;
			case Wounds:
				dmgType = "wound";
				break;
		}
		if (chits.isEmpty()) return;
		
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose chit to "+dmgType+":",false);
		chooser.addRealmComponents(chits,false);
		chooser.setVisible(true);
		CharacterActionChitComponent chit = (CharacterActionChitComponent)chooser.getFirstSelectedComponent();
		switch (getDamageType()) {
			case WeatherFatigue:
				chit.makeFatigued();
				break;
			case Wounds:
				chit.makeWounded();
				break;
		}
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must damage a");
		if (getType() != ChitType.Any) {
			sb.append(getType()+" ");
		}
		sb.append("chit.");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.DamageChit;
	}
	private DamageType getDamageType() {
		return DamageType.valueOf(getString(DAMAGE_TYPE));
	}
	public ChitType getType() {
		return ChitType.valueOf(getString(TYPE));
	}
	private VulnerabilityType getStrength() {
		return VulnerabilityType.valueOf(getString(STRENGTH));
	}
	private int getSpeed() {
		return getInt(SPEED);
	}
	private String getMagicColor() {
		return getString(MAGIC_COLOR);
	}
	private int getMagicType() {
		return getInt(MAGIC_TYPE);
	}
	private boolean onlyActive() {
		return getBoolean(ONLY_ACTIVE);
	}
}