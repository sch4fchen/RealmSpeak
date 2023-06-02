package com.robin.magic_realm.components.store;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Merchant extends Store {

	private CharacterWrapper character;
	private ArrayList<GameObject> itemsToSell;
	
	public Merchant(TravelerChitComponent traveler,CharacterWrapper character) {
		super(traveler);
		this.character = character;
		setupStore();
	}
	private void setupStore() {
		itemsToSell = new ArrayList<GameObject>();
		for (GameObject go:character.getSellableInventory()) {
			itemsToSell.add(go);
		}
		if (itemsToSell.isEmpty()) {
			reasonStoreNotAvailable = "You have no items to sell.";
		}
	}

	public String doService(JFrame frame) {
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Sell which?",true);
		for (GameObject go:itemsToSell) {
			int basePrice = TreasureUtility.getBasePrice(null,RealmComponent.getRealmComponent(go));
			chooser.addGameObject(go,"Sell for "+basePrice+" gold");
		}
		chooser.setVisible(true);
		if (chooser.getSelectedText()!=null) {
			RealmComponent rc = chooser.getFirstSelectedComponent();
			GameObject thing = rc.getGameObject();
			if (thing.hasThisAttribute(Constants.ACTIVATED)) {
				if (!TreasureUtility.doDeactivate(frame,character,thing)) {
					return "You were unable to deactivate the "+thing.getName(); // This should never happen.
				}
			}
			
			int basePrice = TreasureUtility.getBasePrice(null,rc);
			character.addGold(basePrice);
			
			if (rc.isArmor()) {
				// Make sure armor is no longer damaged
				ArmorChitComponent armor = (ArmorChitComponent)rc;
				armor.setActivated(false);
				armor.setIntact(true);
			}
			
			GameObject dwelling = getRandomDwelling();
			thing.removeThisAttribute(Constants.DEAD);
			dwelling.add(thing);
			RealmLogging.logMessage(
					character.getGameObject().getName(),
					"Sold "+thing.getName()+" regenerates at the "+dwelling.getName()+".");
			return "Sold the "+thing.getName()+" to the "+getTraderName();
		}
		return null;
	}
	private GameObject getRandomDwelling() {
		RealmObjectMaster rom = RealmObjectMaster.getRealmObjectMaster(character.getGameData());
		ArrayList<GameObject> dwellings = new ArrayList<GameObject>(); 
		for(GameObject go:rom.getDwellingObjects()) {
			if (!go.hasThisAttribute(Constants.GENERAL_DWELLING)) { // exclude s_fire, l_fire, and hut!
				dwellings.add(go);
			}
		}
		int r = RandomNumber.getRandom(dwellings.size());
		return dwellings.get(r);
	}
}