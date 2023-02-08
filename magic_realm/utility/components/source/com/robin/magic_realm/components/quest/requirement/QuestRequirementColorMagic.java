package com.robin.magic_realm.components.quest.requirement;

import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementColorMagic extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementActive.class.getName());

	public static final String COLOR_KEY = "_clrk";
	
	public QuestRequirementColorMagic(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		ColorMagic test = getColorMagic();
		if (reqParams.burnedColor!=null && reqParams.burnedColor.sameColorAs(test)) return true;
		Collection<ColorMagic> colors = character.getInfiniteColorSources();
		
		boolean found = false;
		for(ColorMagic cm:colors) {
			if (cm.sameColorAs(test)) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			logger.fine(getColorName()+" magic is not present.");
			return false;
		}
		
		return true;
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Only when in the presence of ");
		sb.append(getColorName());
		sb.append(" magic.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.ColorMagic;
	}
	
	public String getColorName() {
		return getString(COLOR_KEY);
	}
	
	public ColorMagic getColorMagic() {
		return ColorMagic.makeColorMagic(getColorName(),true);
	}
}