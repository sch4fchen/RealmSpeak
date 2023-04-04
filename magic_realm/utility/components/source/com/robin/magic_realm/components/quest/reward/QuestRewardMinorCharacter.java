package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMinorCharacter extends QuestReward {
	public static final String MINOR_CHARACTER = "_mc";
	public static final String GAIN_TYPE = "_gt";

	public QuestRewardMinorCharacter(GameObject go) {
		super(go);
		autoRepair(); // to fix any quest with an id instead of a name
	}
	private void autoRepair() {
		String id = getString(MINOR_CHARACTER);
		if (id==null) return;
		try {
			long sid = Long.valueOf(id);
			GameObject go = getGameData().getGameObject(sid);
			if (go!=null) {
				setString(MINOR_CHARACTER,go.getName());
			}
		}
		catch(NumberFormatException ex) {
			// ignore
		}
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		QuestMinorCharacter minorCharacter = getQuestMinorCharacter();
		if (minorCharacter==null) return;
		if (getGainType()==GainType.Gain) {
			minorCharacter.getGameObject().setThisAttribute(Constants.ACTIVATED);
			minorCharacter.setupAbilities();
			minorCharacter.setupBonusChit();
			character.getGameObject().add(minorCharacter.getGameObject());
		}
		else {
			character.getGameObject().remove(minorCharacter.getGameObject());
		}
		character.setNeedsActionPanelUpdate(true);
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getString(MINOR_CHARACTER));
		sb.append(getGainType()==GainType.Gain?" joins ":" leaves ");
		sb.append("the character.");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.MinorCharacter;
	}

	public QuestMinorCharacter getQuestMinorCharacter() {
		String id = getString(MINOR_CHARACTER);
		if (id==null) return null;

		CharacterWrapper character = getParentQuest().getOwner();
		if (character==null) return null; // what to do here?  shouldn't ever happen - the reward shouldn't be given while this quest is still a template!
		
		ArrayList<String> query = new ArrayList<>();
		query.add(Quest.QUEST_MINOR_CHARS);
		query.add("name="+id);
		
		ArrayList<GameObject> mcs = new ArrayList<>();
		// Try the quest FIRST
		GamePool pool = new GamePool(getParentQuest().getGameObject().getHold());
		mcs.addAll(pool.find(query));
		if (mcs.isEmpty()) {
			// Try the character inventory
			pool = new GamePool(character.getInventory());
			mcs.addAll(pool.find(query));
		}
		if (mcs.isEmpty()) {
			// Try the entire game library
			pool = new GamePool(getParentQuest().getGameData().getGameObjects());
			mcs.addAll(pool.find(query));
		}
		if (!mcs.isEmpty()) {
			//setString(MINOR_CHARACTER,mc.getStringId()); // save for future reference?
			return new QuestMinorCharacter(mcs.get(RandomNumber.getRandom(mcs.size())));
		}
		return null;
	}	
	
	public GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}
}