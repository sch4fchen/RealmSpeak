package com.robin.magic_realm.components.quest.requirement;

import java.util.Hashtable;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestCounter;
import com.robin.magic_realm.components.quest.QuestStepState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCounter extends QuestRequirement {
	
	public static final String COUNTER = "_c";
	public static final String TARGET_VALUE = "_tv";
	public static final String EXCEED_TARGET_VALUE = "_ev";
	public static final String SUBCEED_TARGET_VALUE = "_sv";
	
	public QuestRequirementCounter(GameObject go) {
		super(go);
	}
	
	protected boolean testFulfillsRequirement(JFrame frame,CharacterWrapper character,QuestRequirementParams reqParams) {
		boolean success = false;
		QuestCounter counter = getQuestCounter();
		if(counter.getCount() == getTargetValue()) {
			success = true;
		}
		else if (exceedAllowed() && counter.getCount() >= getTargetValue()) {
			success = true;
		}
		else if (subceedAllowed() && counter.getCount() <= getTargetValue()) {
			success = true;
		}
		
		boolean autoJournal = isAutoJournal();
		if (autoJournal) {
			if (success) {
				getParentQuest().addJournalEntry("REQ" + getGameObject().getStringId(), QuestStepState.Finished, buildDescriptionText() + "  Done!");
			}
			else {
				StringBuilder sb = new StringBuilder(buildDescriptionText());
				sb.append("  Still need ");
				sb.append(getTargetValue() - counter.getCount());
				sb.append(".");
				getParentQuest().addJournalEntry("REQ" + getGameObject().getStringId(), QuestStepState.Pending, sb.toString());
			}
		}
		
		return success;
	}
	
	public RequirementType getRequirementType() {
		return RequirementType.Counter;
	}
	protected String buildDescription() {
		String desc = buildDescriptionText();
		if (isAutoJournal()) {
			desc += " (Auto Journal ON)";
		}
		return desc;
	}
	private String buildDescriptionText() {
		QuestCounter questCounter = getQuestCounter();
		if (questCounter==null) return "ERROR - No counter found!";
		return getQuestCounter().getName() +" must reach " +getTargetValue() +".";
	}
	public QuestCounter getQuestCounter() {
		String id = getString(COUNTER);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestCounter(go, go.getThisInt("count"));
			}
		}
		return null;
	}
	private int getTargetValue() {
		return getInt(TARGET_VALUE);
	}
	private boolean exceedAllowed() {
		return getBoolean(EXCEED_TARGET_VALUE);
	}
	private boolean subceedAllowed() {
		return getBoolean(SUBCEED_TARGET_VALUE);
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,COUNTER);
	}
	public boolean usesAutoJournal() {
		return true;
	}
}