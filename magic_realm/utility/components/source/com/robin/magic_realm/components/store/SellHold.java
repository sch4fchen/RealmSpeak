package com.robin.magic_realm.components.store;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser.DisplayOption;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class SellHold extends Store {

	private CharacterWrapper character;
	
	public SellHold(TravelerChitComponent traveler,CharacterWrapper character) {
		super(traveler);
		this.character = character;
		setupStore();
	}
	private void setupStore() {
		if (character.hasCurse(Constants.ASHES)) {
			reasonStoreNotAvailable = "The "+getTraderName()+" does not like your ASHES curse!";
			return;
		}
		if (trader.getHold().isEmpty()) {
			reasonStoreNotAvailable = "The "+getTraderName()+" has nothing left to sell.";
			return;
		}
	}
	
	public String doService(JFrame frame) {
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Buy which?",true);
		for(GameObject go:new ArrayList<>(trader.getHold())) {
			int basePrice = go.getThisInt("base_price");
			chooser.addGameObject(go,basePrice+" gold",DisplayOption.FaceUp);
		}
		chooser.setVisible(true);
		if (chooser.getSelectedText()!=null) {
			double gold = character.getGold();
			RealmComponent rc = chooser.getFirstSelectedComponent();
			int basePrice = rc.getGameObject().getThisInt("base_price");
			if (basePrice>gold) {
				JOptionPane.showMessageDialog(frame,"You cannot afford the "+rc.getGameObject().getName()+".","Too expensive!",JOptionPane.PLAIN_MESSAGE,rc.getFaceUpIcon());
			}
			else {
				if (rc.isCard()) {
					CardComponent card = (CardComponent)rc;
					card.setFaceUp();
				}
				rc.getGameObject().setThisAttribute(Constants.TREASURE_NEW);
				character.addGold(-basePrice);
				if (rc.isCompanion()) {
					ClearingUtility.moveToLocation(rc.getGameObject(),character.getCurrentLocation());
					character.addHireling(rc.getGameObject());
				}
				else {
					rc.setCharacterTimestamp(character);
					character.getGameObject().add(rc.getGameObject());
				}
				return "Bought the "+rc.getGameObject().getName();
			}
		}
		return null;
	}
}