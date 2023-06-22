package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ListSelectionModel;

import com.robin.general.swing.ListChooser;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingCharacter extends SpellTargetingSingle {
	
	private boolean lightOnly;

	public SpellTargetingCharacter(CombatFrame combatFrame, SpellWrapper spell,boolean lightOnly) {
		super(combatFrame, spell);
		this.lightOnly = lightOnly;
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allCharacters = combatFrame.findCanBeSeen(battleModel.getAllParticipatingCharactersAsRc(),true);
		for (RealmComponent rc : allCharacters) {
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			if (!character.hasMagicProtection() && (!lightOnly || !character.getVulnerability().strongerThan(new Strength("L")))) {
				gameObjects.add(rc.getGameObject());
			}
		}
		return true;
	}
	
	public boolean assign(HostPrefWrapper hostPrefs, CharacterWrapper activeCharacter) {
		boolean assign = super.assign(hostPrefs,activeCharacter);
		
		if (!spell.getGameObject().hasThisAttribute(Constants.RESERVE)) return assign;
		
		TileLocation loc = activeCharacter.getCurrentLocation();
		if (loc == null || loc.clearing == null || loc.clearing.getClearingColorMagic().isEmpty()) {
			spell.expireSpell();
			return false;
		}
		
		ArrayList<ColorMagic> colors = loc.clearing.getAllSourcesOfColor(true);
		ArrayList<String> colorNames = new ArrayList<>();
		for (ColorMagic color : colors) {
			colorNames.add(color.getColorName());
		}
		ListChooser chooser = new ListChooser(combatFrame, "Select magic color", colorNames);
		chooser.setDoubleClickEnabled(true);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setLocationRelativeTo(combatFrame);
		chooser.setVisible(true);
		Vector<String> v = chooser.getSelectedItems();
		if (v == null || v.isEmpty()) {
			spell.expireSpell();
			RealmLogging.logMessage(RealmLogging.BATTLE,"Spell cancelled - no magic color chosen or none available.");
			return false;
		}
		
		spell.setExtraIdentifier(v.get(0));		
		return true;
	}
}