/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.ArmorChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.ChitItemType;
import com.robin.magic_realm.components.quest.ItemGainType;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.table.Loot;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardItem extends QuestReward {
	
	public static final String GAIN_TYPE = "_g"; // gain or lose
	public static final String DAMAGED_ITEM = "_d";
	public static final String ITEM_DESC = "_idsc";
	public static final String ITEM_CHITTYPES = "_ict";
	public static final String ITEM_REGEX = "_irx";
	
	public QuestRewardItem(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String actionDescription;
		ArrayList<GameObject> objects;
		if (isGain()) {
			actionDescription = ": Select ONE item to gain.";
			objects = getObjectList(getGameData().getGameObjects(),getChitTypes(),getItemRegex());
		}
		else {
			actionDescription = ": Select ONE item from your inventory to lose.";
			objects = getObjectList(character.getInventory(),getChitTypes(),getItemRegex());
		}
		GameObject selected = null;
		if (objects.size()==1) {
			selected = objects.get(0);
		}
		else {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,getTitleForDialog()+actionDescription,false);
			chooser.addGameObjects(objects,true);
			chooser.setVisible(true);
			selected = chooser.getFirstSelectedComponent().getGameObject();
		}
		
		if (getGainType()==ItemGainType.GainCloned) {
			GameObject newItem = selected.copy();
			newItem.setThisAttribute(Constants.CLONED);
			selected = newItem;
		}
		
		if (isGain()) {
			if (damagedItem() && selected.hasThisAttribute("armor")) {
				ArmorChitComponent selectedItem = (ArmorChitComponent) RealmComponent.getRealmComponent(selected);
				selectedItem.setLightSideUp();
			}
			Loot.addItemToCharacter(frame,null,character,selected);
		}
		else {
			if (TreasureUtility.doDeactivate(null,character,selected)) { // null JFrame so that character isn't hit with any popups
				switch (getGainType()) {
				case RemoveFromGame:
					lostItemToDefault(selected);
					character.getGameData().removeObject(selected);
					break;
				case LoseToClearing:
					TileLocation location = character.getCurrentLocation();
					if (location.clearing == null) {
						location.setRandomClearing();
					}
					ClearingUtility.moveToLocation(selected,location);
					break;
				case LoseToLocation:
					lostItem(selected);
					break;
				default:
				case LoseToChartOfAppearance:
					lostItemToDefault(selected);
					break;
				}
			}
			else {
				JOptionPane.showMessageDialog(frame,"The "+selected.getName()+" could not be removed from your inventory.","Quest Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private boolean isGain() {
		return getGainType()==ItemGainType.Gain || getGainType()==ItemGainType.GainCloned;
	}
	
	private boolean damagedItem() {
		return getBoolean(DAMAGED_ITEM);
	}
	
	public String getDescription() {
		return (isGain()?"Gains":"Loses")+" "+getItemDescription();
	}

	public RewardType getRewardType() {
		return RewardType.Item;
	}
	
	public ItemGainType getGainType() {
		String gainType = getString(GAIN_TYPE);
		if (gainType.matches("Lose")) { // compatibility for old quests
			return ItemGainType.LoseToLocation;
		}
		return ItemGainType.valueOf(getString(GAIN_TYPE));
	}
	
	public String getItemDescription() {
		return getString(ITEM_DESC);
	}
	
	public String getItemRegex() {
		return getString(ITEM_REGEX);
	}
	
	public ArrayList<ChitItemType> getChitTypes() {
		return ChitItemType.listToTypes(getList(ITEM_CHITTYPES));
	}
	
	public static ArrayList<GameObject> getObjectList(ArrayList<GameObject> sourceObjects,ArrayList<ChitItemType> chitItemTypes,String regEx) {
		Pattern pattern = (regEx==null || regEx.length()==0)?null:Pattern.compile(regEx);
		ArrayList<GameObject> objects = new ArrayList<GameObject>();
		GamePool pool = new GamePool(sourceObjects);
		for(ChitItemType cit:chitItemTypes) {
			for(GameObject obj:pool.extract(Arrays.asList(cit.getKeyVals()))) {
				if (pattern==null || pattern.matcher(obj.getName()).find()) {
					objects.add(obj);
				}
			}
		}
		return objects;
	}
}