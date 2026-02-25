package com.robin.magic_realm.components.table;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StealAttempt extends RealmTable {
	
	public static final String KEY = "Stealing";
	private static final String[] RESULT = {
		"Success - Take desired item",
		"Success - Roll for item (susbstract 1)",
		"Success - Roll for item",
		"Success - Roll for item",
		"Suspect - Lose one level of friendliness",
		"Caught/Block - Natives become Enemy",
	};
	private RealmComponent victim;
		
	public StealAttempt(JFrame frame,RealmComponent victim) {
		super(frame,null);
		this.victim = victim;
	}
	public String getTableName(boolean longDescription) {
		return "Steal Attempt";
	}
	public String getTableKey() {
		return KEY;
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		return super.apply(character,roller);
	}
	public String applyOne(CharacterWrapper character) {
		GameObject holder = SetupCardUtility.getDenizenHolder(victim.getGameObject());
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(getParentFrame(),"Select item to steal:",false);
		for(GameObject item:holder.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if (rc.isItem() || rc.isTreasure()) {
				if (rc.isTreasure()) {
					TreasureCardComponent treasure = (TreasureCardComponent)rc;
					treasure.setFaceUp();
					item.setThisAttribute(Constants.TREASURE_SEEN);
				}
				chooser.addRealmComponent(RealmComponent.getRealmComponent(item),item.getName());
			}
		}
		if (chooser.getComponentCount()==0) {
			JOptionPane.showMessageDialog(getParentFrame(),"Nothing to steal from "+victim.getGameObject().getNameWithNumber(),"Steal Attempt",JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			chooser.setVisible(true);
			RealmComponent selectedItem = chooser.getFirstSelectedComponent();
			character.getGameObject().add(selectedItem.getGameObject());
		}
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim,-1));
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		character.changeRelationship(victim.getGameObject(), -1);
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		character.changeRelationshipTo(victim.getGameObject(), RelationshipType.ENEMY);
		character.setBlocked(true);
		character.setHidden(false);
		return RESULT[5];
	}
}