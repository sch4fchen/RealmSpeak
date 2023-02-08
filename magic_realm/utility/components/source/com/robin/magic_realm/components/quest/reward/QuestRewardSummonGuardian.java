package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSummonGuardian extends QuestReward {
	
	public static final String LOCATION = "_l";
	
	public QuestRewardSummonGuardian(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		QuestLocation loc = getQuestLocation();
		RealmComponent[] pieces = loc.allPiecesForLocationClearings(frame,character);
		GamePool pool = new GamePool(getGameData().getGameObjects());
		for(RealmComponent piece:pieces) {
			if (!piece.isTreasureLocation()) continue;
			
			String locationName = piece.getGameObject().getThisAttribute(RealmComponent.TREASURE_LOCATION);
			ArrayList<String> query = new ArrayList<>();
			query.add("setup_start="+StringUtilities.capitalize(locationName));
			String boardNumber = piece.getGameObject().getThisAttribute(Constants.BOARD_NUMBER);
			if (boardNumber!=null) {
				query.add(Constants.BOARD_NUMBER+"="+boardNumber);
			}
			TileLocation tl = piece.getCurrentLocation();
			for(GameObject denizen:pool.find(query)) { // I don't think there is ever more than one...
				SetupCardUtility.resetDenizen(denizen); // make sure denizen is not dead, or elsewhere on the board
				tl.clearing.add(denizen,null);
			}
		}
	}
	
	public String getDescription() {
		return "Summon guardian at "+getQuestLocation().getName();
	}

	public RewardType getRewardType() {
		return RewardType.SummonGuardian;
	}
	
	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
	}
	
	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
}