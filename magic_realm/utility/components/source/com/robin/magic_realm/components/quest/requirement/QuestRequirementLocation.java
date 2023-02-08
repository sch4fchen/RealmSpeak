package com.robin.magic_realm.components.quest.requirement;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementLocation extends QuestRequirement {
	
	private static Logger logger = Logger.getLogger(QuestRequirementLocation.class.getName());
	
	public static final String LOCATION = "_l";
	public static final String NON_CLEARING = "_c";
	
	public QuestRequirementLocation(GameObject go) {
		super(go);
	}
	
	protected boolean testFulfillsRequirement(JFrame frame,CharacterWrapper character,QuestRequirementParams reqParams) {
		QuestLocation location = getQuestLocation();
		if (!nonClearingAllowed() && character.getCurrentLocation() != null && !character.getCurrentLocation().isInClearing()) {
			logger.fine(character.getName()+" is not in a clearing.");
			return false;
		}		
		if (!location.locationMatchAddress(frame,character)) {
			logger.fine(character.getName()+" is not at specified location: "+location.getName());
			return false;
		}
		return true;
	}
	
	private boolean nonClearingAllowed() {
		return getBoolean(NON_CLEARING);
	}
	
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
	}
	
	public RequirementType getRequirementType() {
		return RequirementType.OccupyLocation;
	}
	
	protected String buildDescription() {
		QuestLocation questLocation = getQuestLocation();
		if (questLocation==null) return "ERROR - No location found!";
		return "Occupy "+getQuestLocation().getName();
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