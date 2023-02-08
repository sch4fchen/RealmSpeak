package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementNoDenizens extends QuestRequirement {

	private static Logger logger = Logger.getLogger(QuestRequirementNoDenizens.class.getName());

	public static final String NO_MONSTERS = "_mon";
	public static final String NO_NATIVES = "_nat";
	public static final String TILE_WIDE = "_tw";

	public QuestRequirementNoDenizens(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation tl = character.getCurrentLocation();
		if (tl==null || !tl.isInClearing()) {
			logger.fine(character.getName() + " is not in a clearing.");
			return false;
		}
		boolean monsters = getBoolean(NO_MONSTERS);
		boolean natives = getBoolean(NO_NATIVES);
		boolean tilewide = getBoolean(TILE_WIDE);
		String word = tilewide ? "tile" : "clearing";

		ArrayList<RealmComponent> components = tilewide ? tl.tile.getAllClearingComponents() : tl.clearing.getClearingComponents();
		for (RealmComponent rc : components) {
			if (monsters && rc.isMonster()) {
				logger.fine(character.getName() + " is in a " + word + " with monsters.");
				return false;
			}
			if (natives && rc.isNative()) {
				logger.fine(character.getName() + " is in a " + word + " with natives.");
				return false;
			}
		}

		return true;
	}

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.NoDenizens;
	}

	@Override
	protected String buildDescription() {
		boolean monsters = getBoolean(NO_MONSTERS);
		boolean natives = getBoolean(NO_NATIVES);
		boolean tilewide = getBoolean(TILE_WIDE);

		if (!monsters && !natives) {
			return "Error - Select denizens!";
		}

		StringBuffer sb = new StringBuffer();
		if (monsters)
			sb.append("No monsters");
		if (natives) {
			if (monsters) {
				sb.append(" or natives");
			}
			else {
				sb.append("No natives");
			}
		}
		sb.append(" in ");
		sb.append(tilewide ? "tile" : "clearing");
		return sb.toString();
	}
}