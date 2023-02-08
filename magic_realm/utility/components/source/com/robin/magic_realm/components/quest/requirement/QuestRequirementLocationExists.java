package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementLocationExists extends QuestRequirement {
	public static final String LOCATION = "_l";
	
	public QuestRequirementLocationExists(GameObject go) {
		super(go);
	}
	
	protected boolean testFulfillsRequirement(JFrame frame,CharacterWrapper character,QuestRequirementParams reqParams) {
		QuestLocation location = getQuestLocation();
		ArrayList<TileLocation> allLocations = location.fetchAllLocations(frame, character, character.getGameData());
		return allLocations!=null && !allLocations.isEmpty();
	}
		
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
	}
	
	public RequirementType getRequirementType() {
		return RequirementType.LocationExists;
	}
	
	protected String buildDescription() {
		QuestLocation questLocation = getQuestLocation();
		if (questLocation==null) return "ERROR - No location found!";
		return getQuestLocation().getName()+" must exist.";
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