package com.robin.magic_realm.components.store;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TravelerChitComponent;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.*;

public class SpellCast extends Store {
	
	private CharacterWrapper character;
	
	public SpellCast(TravelerChitComponent traveler,CharacterWrapper character) {
		super(traveler);
		this.character = character;
		setupStore();
	}
	private void setupStore() {
		if (character.hasCurse(Constants.ASHES)) {
			reasonStoreNotAvailable = "The "+getTraderName()+" does not like your ASHES curse!";
			return;
		}
	}
	
	public String doService(JFrame frame) {
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Which Service?",true);
		SpellWrapper spell = new SpellWrapper(trader.getHold().iterator().next());
		Hashtable<String,String[]> optionKeyToPrice = new Hashtable<>();
		ArrayList<String> prices = trader.getGameObject().getThisAttributeList("prices");
		for (String price:prices) {
			String[] keyVal = price.split("=");
			int cost = Integer.parseInt(keyVal[0]);
			int spellMod = Integer.parseInt(keyVal[1]);
			String modName = spellMod==0?"":(" ("+spellMod+")");
			String optionKey = chooser.generateOption("Cast "+spell.getName()+modName+" for "+cost+" gold.");
			chooser.addGameObjectToOption(optionKey,spell.getGameObject());
			optionKeyToPrice.put(optionKey,keyVal);
		}
		chooser.setVisible(true);
		String selOptionKey = chooser.getSelectedOptionKey();
		if (selOptionKey!=null) {
			RealmComponent rc = chooser.getFirstSelectedComponent();
			String[] keyVal = optionKeyToPrice.get(selOptionKey);
			int cost = Integer.parseInt(keyVal[0]);
			double gold = character.getGold();
			if (cost>gold) {
				JOptionPane.showMessageDialog(frame,"You cannot afford the spell casting.","Too expensive!",JOptionPane.PLAIN_MESSAGE,rc.getIcon());
			}
			else {
				character.addGold(-cost);
				int spellMod = Integer.parseInt(keyVal[1]);
				
				GameObject virtualSpellGo = spell.getGameObject().copy();
				spell.getGameObject().add(virtualSpellGo);
				SpellWrapper virtualSpell = new SpellWrapper(virtualSpellGo);
				virtualSpell.makeVirtual();
				virtualSpell.getGameObject().setThisAttribute(Constants.SPELL_MOD,spellMod);
				virtualSpell.castSpellNoEnhancedMagic(trader.getGameObject());
				virtualSpell.addTarget(HostPrefWrapper.findHostPrefs(character.getGameData()),character.getGameObject());
				virtualSpell.affectTargets(frame,GameWrapper.findGame(character.getGameData()),false,null);
				
				return getTraderName()+" cast "+spell.getGameObject().getName();
			}
		}
		return null;
	}
}