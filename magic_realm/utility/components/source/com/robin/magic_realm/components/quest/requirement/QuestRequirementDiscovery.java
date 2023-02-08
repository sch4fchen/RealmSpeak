package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementDiscovery extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementDiscovery.class.getName());
	
	public static final String DISCOVERY_KEY = "_drk";
	
	public QuestRequirementDiscovery(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		ArrayList<String> discoveryKeys = character.getAllDiscoveryKeys();
		String keyToFind = getDiscoveryKey();
		for (String val:discoveryKeys) {
			if (val.startsWith(keyToFind)) { // Using startsWith, to ignore board number (for now)
				return true;
			}
		}
		logger.fine("Discovery \""+keyToFind+"\" was not found.");
		return false;
	}

	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must have discovery for \"");
		sb.append(getDiscoveryKey());
		sb.append("\".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Discovery;
	}
	
	public String getDiscoveryKey() {
		return getString(DISCOVERY_KEY);
	}
}