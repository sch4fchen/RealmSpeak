package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.GamePhaseType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGamePhase extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementGamePhase.class.getName());
	
	public static final String ATTRIBUTE_TYPE = "_at";
	public static final String GAME_PHASE_TYPE = "_gpt";

	public QuestRequirementGamePhase(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		GamePhaseType expecting = getGamePhaseType();
		boolean ret = reqParams!=null && reqParams.timeOfCall==expecting;
		
		if (!ret) {
			String phase = (reqParams==null || reqParams.timeOfCall==null) ? "Unknown" : reqParams.timeOfCall.toString();
			logger.fine("Incorrect game phase "+phase+".  Expecting: "+expecting);
		}
		
		return ret;
	}

	protected String buildDescription() {
		return "Only at "+getGamePhaseType();
	}

	public RequirementType getRequirementType() {
		return RequirementType.GamePhase;
	}
	
	public GamePhaseType getGamePhaseType() {
		return GamePhaseType.valueOf(getString(GAME_PHASE_TYPE));
	}
}