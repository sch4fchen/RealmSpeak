package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardRegenerateDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_drx";
	public static final String DENIZEN_AMOUNT = "_damnt";
	public static final String CHARACTERS_CLEARING = "_ch_cl";
	public static final String CHARACTERS_TILE = "_ch_tile";
	public static final String REGENERATE_HIRELINGS = "_reg_hirelings";
	
	public QuestRewardRegenerateDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		int regeneratedDenizens = 0;
		if (numberOfDenizens()!=0) {
			Collections.shuffle(denizens);
		}
		for (GameObject denizen : denizens) {
			if (denizen != null && denizen.hasThisAttribute("denizen") && !denizen.hasThisAttribute(Constants.CLONED) && !denizen.hasThisAttribute(Constants.COMPANION) && !denizen.hasThisAttribute(Constants.SUMMONED)) {				
				RealmComponent denizenRc = RealmComponent.getRealmComponent(denizen);
				if (denizenRc.getOwner()!=null && !regenerateHirelings()) continue;
				if (charactersClearingOnly()) {
					if(denizenRc.getCurrentLocation() == null || character.getCurrentLocation() == null || denizenRc.getCurrentLocation().tile != character.getCurrentLocation().tile || denizenRc.getCurrentLocation().clearing != character.getCurrentLocation().clearing) {
						continue;
					}
				}
				if (charactersTileOnly()) {
					if(denizenRc.getCurrentLocation() == null || character.getCurrentLocation() == null || denizenRc.getCurrentLocation().tile != character.getCurrentLocation().tile) {
						continue;
					}
				}
				SetupCardUtility.resetDenizen(denizen);
				regeneratedDenizens++;
				if (numberOfDenizens() != 0 && regeneratedDenizens>=numberOfDenizens()) return;
			}
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDenizenNameRegex() +" are regenerated");
		if (numberOfDenizens()!=0) {
			sb.append(" (max. "+numberOfDenizens()+")");
		}
		if (charactersClearingOnly()) {
			sb.append(" in the characters clearing");
		}
		if (charactersTileOnly()) {
			sb.append(" in the characters tile");
		}
		sb.append(".");
		return sb.toString();
	}
	
	private String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}
	private int numberOfDenizens() {
		return getInt(DENIZEN_AMOUNT);
	}
	private Boolean charactersClearingOnly() {
		return getBoolean(CHARACTERS_CLEARING);
	}
	private Boolean charactersTileOnly() {
		return getBoolean(CHARACTERS_CLEARING);
	}
	private Boolean regenerateHirelings() {
		return getBoolean(REGENERATE_HIRELINGS);
	}
	
	public RewardType getRewardType() {
		return RewardType.RegenerateDenizen;
	}
}