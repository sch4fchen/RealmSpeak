package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardVisitor extends QuestReward {
	
	public static final String VISITOR_REGEX = "_vrx";
	public static final String ACQUISITION_TYPE = "_goc";
	
	public QuestRewardVisitor(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame, CharacterWrapper character) {
		String actionDescription;
		ChitAcquisitionType at = getAcquisitionType();
		ArrayList<GameObject> objects;
		if (at == ChitAcquisitionType.Lose) {
			actionDescription = ": Select ONE visitor to lose.";
			objects = character.getInactiveInventory();
		}
		else {
			actionDescription = ": Select ONE visitor to join you.";
			objects = getGameData().getGameObjects();
		}
		ArrayList<GameObject> selectionObjects = getObjectList(objects, at, getVisitorRegex());
		if (selectionObjects.size() == 0)
			return; // no real reward!
		GameObject selected = null;
		if (selectionObjects.size() == 1) {
			selected = selectionObjects.get(0);
		}
		else {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame, getTitleForDialog() + actionDescription, false);
			chooser.addGameObjects(selectionObjects, true);
			chooser.setVisible(true);
			selected = chooser.getFirstSelectedComponent().getGameObject();
		}

		if (at == ChitAcquisitionType.Lose) {
			if (selected.hasThisAttribute(Constants.CLONED)) {
				selected.detach();
				getGameData().removeObject(selected);
			}
			else {
				TileLocation loc = character.getCurrentLocation();
				if (loc.clearing!=null) {
					character.getCurrentLocation().clearing.add(selected,character);
				}
				else {
					loc.tile.getClearing(RandomNumber.getRandom(loc.tile.getClearingCount())).add(selected,character);
				}
			}
		}
		else {
			if (at == ChitAcquisitionType.Clone) {
				GameObject go = getGameData().createNewObject();
				go.copyAttributesFrom(selected);
				go.setThisAttribute(Constants.CLONED); // tag as cloned, so that the remove method will expunge the clone
				selected = go;
			}
			character.getGameObject().add(selected);
		}
	}

	private static ArrayList<GameObject> getObjectList(ArrayList<GameObject> sourceObjects, ChitAcquisitionType at, String regEx) {
		Pattern pattern = (regEx == null || regEx.length() == 0) ? null : Pattern.compile(regEx);
		GamePool pool = new GamePool(sourceObjects);
		ArrayList<GameObject> objects = new ArrayList<GameObject>();
		for (GameObject go : pool.find("visitor")) {
			if (pattern == null || pattern.matcher(go.getName()).find()) {
				if (at == ChitAcquisitionType.Available) {
					// Make sure these are not already hired
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if (rc.getOwnerId() != null)
						continue;
				}
				objects.add(go);
			}
		}
		return objects;
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getVisitorRegex());
		ChitAcquisitionType at = getAcquisitionType();
		if (at == ChitAcquisitionType.Lose) {
			sb.append(" leaves the character.");
		}
		else {
			sb.append(" joins the character.");
		}
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.Visitor;
	}
	
	public ChitAcquisitionType getAcquisitionType() {
		return ChitAcquisitionType.valueOf(getString(ACQUISITION_TYPE));
	}
	
	public String getVisitorRegex() {
		return getString(VISITOR_REGEX);
	}
}