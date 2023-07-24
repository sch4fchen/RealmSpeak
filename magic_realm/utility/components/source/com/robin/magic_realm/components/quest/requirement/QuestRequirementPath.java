package com.robin.magic_realm.components.quest.requirement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.TargetValueType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementPath extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementPath.class.getName());

	public static final String PATH = "_path";
	public static final String TIME_RESTRICTION = "_tr";
	public static final String CHECK_REVERSE = "_cr";
	public static final String ALLOW_TRANSPORT = "_at";

	public QuestRequirementPath(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		String path = getPathString();
		if (path==null || path.trim().length()==0) {
			logger.fine("QUEST ERROR:  Invalid path defined in Quest file.");
			return false;
		}
		path = path.trim();
		
		ArrayList<String> history = character.getMoveHistory();
		if (history==null || history.size()==0) {
			logger.fine("Character hasn't gone anywhere yet.");
			return false;
		}
		ArrayList<String> historyDays = character.getMoveHistoryDayKeys();
		if (history.size()!=historyDays.size()) {
			logger.fine("QUEST ERROR:  history is different size than historyDays.");
			return false;
		}
		
		DayKey startKey = null;
		switch(getTimeRestriction()) {
			case Quest:
				startKey = getParentStep().getQuestStartTime();
				break;
			case Step:
				startKey = getParentStep().getQuestStepStartTime();
				break;
			case Game:
				startKey = new DayKey(1,1);
				break;
			case Day:
			default:
				startKey = new DayKey(character.getCurrentDayKey());
				break;
		}
		
		boolean ignoreJumps = isAllowTransport();
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<history.size();i++) {
			if (startKey!=null) {
				DayKey dayKey = new DayKey(historyDays.get(i));
				if (dayKey.before(startKey)) continue;
			}
			String location = history.get(i);
			if (CharacterWrapper.MOVE_HISTORY_DAY.equals(location)) continue; // always ignore the days
			if (ignoreJumps && CharacterWrapper.MOVE_HISTORY_JUMP.equals(location)) continue; // ignore the jumps only if transport is allowed
			if (sb.length()>0) sb.append(" ");
			sb.append(location);
		}
		
		String charPath = sb.toString();
		
		boolean matchForward = testPath(charPath,path);
		boolean matchReverse = !matchForward && isCheckReverse() && testPath(charPath,getReversePath());
		
		if (!matchForward && !matchReverse) {
			logger.fine("Character path ("+charPath+") doesn't contain specified path ("+path+")");
			return false;
		}
		return true;
	}
	
	public static boolean testPath(String charPath,String testPath) {
		
		charPath = " "+charPath;
		String[] each = testPath.split(" "); // "1 2 3 4 5"
		String[] pathSections = new String[each.length - 1]; // "1 2" "2 3" "3 4" "4 5"
		if (each.length == 1) {
			String clearing = (String) Array.get(each, 0);
			return charPath.contains(" "+clearing) == true;
		}
		
		for (int i=0;i<pathSections.length;i++) {
			StringBuilder sb = new StringBuilder(each[i]);
			sb.append(' ');
			sb.append(each[i+1]);
			pathSections[i] = sb.toString();
		}
		
		int lastIndex = -1;
		for(String section:pathSections) {
			int index = charPath.lastIndexOf(" "+section);
			if (index<0 || index<=lastIndex) return false;
			lastIndex = index;
		}
		return true;
	}

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.Path;
	}

	@Override
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must follow path");
		switch(getTimeRestriction()) {
			case Quest:
				sb.append(" during the quest");
				break;
			case Step:
				sb.append(" during the step");
				break;
			case Day:
			default:
				sb.append(" during the current day");
				break;
		}
		if (!isAllowTransport()) {
			sb.append(" without teleporting");
		}
		if (isCheckReverse()) {
			sb.append(" in either direction");
		}
		sb.append(": ");
		sb.append(getPathString());
		return sb.toString();
	}
	
	public String getPathString() {
		return getString(PATH);
	}
	
	public String getReversePath() {
		String path = getPathString();
		if (path==null) return null;
		String[] ret = path.split(" ");
		StringBuilder sb = new StringBuilder();
		for(int i=ret.length-1;i>=0;i--) {
			sb.append(ret[i]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}
	
	public TargetValueType getTimeRestriction() {
		return TargetValueType.valueOf(getString(TIME_RESTRICTION));
	}
	
	public boolean isCheckReverse() {
		return getBoolean(CHECK_REVERSE);
	}
	
	public boolean isAllowTransport() {
		return getBoolean(ALLOW_TRANSPORT);
	}
}