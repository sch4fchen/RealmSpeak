package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.NativeChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardRelationshipSet extends QuestReward {
	public static final String NATIVE_GROUP = "_ng";
	public static final String RELATIONSHIP_SET = "_rs";
	
	public QuestRewardRelationshipSet(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> representativeNativesToChange = getRepresentativeNatives(character);
		if (representativeNativesToChange==null) return;
		
		String name = getRelationshipName();
		int targetRel = RelationshipType.getIntFor(name);
		for(GameObject denizen:representativeNativesToChange) {
			int current = character.getRelationship(denizen);
			int diff = targetRel - current;
			character.changeRelationship(denizen,diff);
		}
	}
	
	public ArrayList<GameObject> getRepresentativeNatives(CharacterWrapper character) {
		TileLocation tl = character.getCurrentLocation();
		if (isAllNatives()) {
			if (!tl.isInClearing()) return null;
			return fetchNativesFromClearing(tl, character);
		}
		
		// Fetch the group leader - if multiple boards, then match the warning chit board
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		ArrayList<String> queryNatives = new ArrayList<String>();
		ArrayList<String> queryVisitors = new ArrayList<String>();
		queryNatives.add("rank=HQ");
		queryNatives.add("native="+getNativeGroup());
		queryVisitors.add("visitor="+getNativeGroup().toLowerCase());
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
		if (hostPrefs.getMultiBoardEnabled()) {
			for(GameObject go : tl.tile.getGameObject().getHold()) {
				if (go.hasThisAttribute("warning") && go.hasThisAttribute("chit")) {
					String board = go.getThisAttribute(Constants.BOARD_NUMBER);
					if (board!=null) {
						queryNatives.add(Constants.BOARD_NUMBER+"="+board);
						queryVisitors.add(Constants.BOARD_NUMBER+"="+board);
					}
				}
			}
		}
		
		ArrayList<GameObject> representativeNatives = pool.find(queryNatives);
		representativeNatives.addAll(pool.find(queryVisitors));
		return representativeNatives;
	}
	
	public boolean isAllNatives() {
		String group = getNativeGroup();
		return group.equals("Clearing");
	}
	
	private static ArrayList<GameObject> fetchNativesFromClearing(TileLocation tl, CharacterWrapper character) {
		
		ArrayList<String> groupsToChange = new ArrayList<String>();
		ArrayList<GameObject> representativeNativesToChange = new ArrayList<GameObject>();
		for(RealmComponent rc:tl.clearing.getClearingComponents()) {
			if (!rc.isNative()) continue;
			
			NativeChitComponent nat = (NativeChitComponent)rc;
			String groupName = nat.getGameObject().getThisAttribute("native");
			if (!groupsToChange.contains(groupName)) {
				groupsToChange.add(groupName);
				representativeNativesToChange.add(nat.getGameObject());
			}
		}
		return representativeNativesToChange;
	}
	
	public String getDescription() {
		return "Set relationship with "+getNativeGroup()+" to "+getRelationshipName();
	}
	
	public RewardType getRewardType() {
		return RewardType.RelationshipSet;
	}
	public String getNativeGroup() {
		return getString(NATIVE_GROUP);
	}
	public String getRelationshipName() {
		return getString(RELATIONSHIP_SET);
	}
}