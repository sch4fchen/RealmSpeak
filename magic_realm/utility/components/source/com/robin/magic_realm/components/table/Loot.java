package com.robin.magic_realm.components.table;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.general.swing.DieRoller;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.SearchResultType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.RealmComponentDisplayDialog;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class Loot extends RealmTable {
	protected GameObject treasureLocation;
	protected TileLocation tileLocation; // null if not searching a clearing
	protected ActionPrerequisite searchPr;
	protected ActionPrerequisite drawPr;
	protected CharacterWrapper character;
	
	public Loot(JFrame frame,ChangeListener listener) {
		super(frame,listener);
		this.treasureLocation = null;
		this.tileLocation = null;
		this.character = null;
	}
	public Loot(JFrame frame,CharacterWrapper character,GameObject treasureLocation,ChangeListener listener) {
		super(frame,listener);
		this.character = character;
		this.treasureLocation = treasureLocation;
		this.tileLocation = null;
		searchPr = ActionPrerequisite.getActionPrerequisite(treasureLocation,treasureLocation.getThisAttribute(Constants.SEARCH),Constants.SEARCH);
		drawPr = ActionPrerequisite.getActionPrerequisite(treasureLocation,treasureLocation.getThisAttribute("draw"),"draw from");
	}
	public Loot(JFrame frame,CharacterWrapper character,TileLocation tl,ChangeListener listener) {
		this(frame,character,tl.tile.getGameObject(),listener);
		this.tileLocation = tl;
	}

	public String getTableName(boolean longDescription) {
		if (tileLocation!=null) {
			int n = ClearingUtility.getAbandonedItemCount(tileLocation);
			return "Loot the clearing ("+n+" item"+(n==1?"":"s")+")";
		}
		return "Loot the " + treasureLocation.getName() 
				+ (longDescription?("\n(" + TreasureUtility.getTreasureCount(treasureLocation,character) + " left)"):"");
	}
	public String getTableKey() {
		return "Loot";
	}
	public String apply(CharacterWrapper character, DieRoller inRoller) {
		// Other characters in the clearing discover the treasureLocation when being looted (if found hidden enemies)
		if (tileLocation==null) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
			if (!hostPrefs.hasPref(Constants.SR_NO_SPYING)) {
				ClearingDetail current = character.getCurrentLocation().clearing;
				for (RealmComponent rc : current.getClearingComponents()) {
					if (rc.canSpy() && !rc.getGameObject().equals(character.getGameObject())) {
						CharacterWrapper spy = new CharacterWrapper(rc.getGameObject());
						if (!character.isHidden() || spy.foundHiddenEnemy(character.getGameObject())) {
							if (!spy.hasTreasureLocationDiscovery(treasureLocation.getName())) {
								spy.addTreasureLocationDiscovery(treasureLocation.getName());
							}
							// Observing a character looting a Site card leads to the discovery of the site card AND the original Site chit!!!
							String siteChitName = treasureLocation.getThisAttribute("siteChitName");
							if (siteChitName!=null && !spy.hasTreasureLocationDiscovery(siteChitName)) {
								spy.addTreasureLocationDiscovery(siteChitName);
							}
						}
					}
				}
			}
			
			// Site chits are revealed immediately when looted
			RealmComponent tlRc = RealmComponent.getRealmComponent(treasureLocation);
			if (tlRc.isTreasureLocation()) {
				TreasureLocationChitComponent chit = (TreasureLocationChitComponent)tlRc;
				if (chit.isFaceDown()) {
					chit.setFaceUp();
					
					// Make sure chit isn't hidden inside a Red-Special chit
					TileLocation tl = character.getCurrentLocation();
					ClearingUtility.moveToLocation(chit.getGameObject(),tl);
				}
			}
		}
		
		return super.apply(character,inRoller);
	}
	
	public boolean fulfilledPrerequisite(JFrame frame,CharacterWrapper character) {
		if (searchPr!=null) {
			if (!searchPr.fullfilled(frame,character,getListener())) {
				JOptionPane.showMessageDialog(frame,searchPr.getFailReason(),"Search Action Cancelled",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if (drawPr!=null) {
			if (!drawPr.canFullfill(frame,character,getListener())) {
				JOptionPane.showMessageDialog(frame,drawPr.getFailReason(),"Search Action Cancelled",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	public String applyOne(CharacterWrapper character) {
		return doLoot(character, 1);
	}

	public String applyTwo(CharacterWrapper character) {
		return doLoot(character, 2);
	}

	public String applyThree(CharacterWrapper character) {
		return doLoot(character, 3);
	}

	public String applyFour(CharacterWrapper character) {
		return doLoot(character, 4);
	}

	public String applyFive(CharacterWrapper character) {
		return doLoot(character, 5);
	}

	public String applySix(CharacterWrapper character) {
		return doLoot(character, 6);
	}

	protected String doLoot(CharacterWrapper character, int treasureNumber) {
		Collection<GameObject> treasures;
		
		if (treasureLocation.hasThisAttribute(Constants.PIT)) {
			if (character.getGameObject().hasThisAttribute(Constants.SEARCHED_PIT)) {
				character.getGameObject().removeThisAttribute(Constants.SEARCHED_PIT);
			}
			else {
				character.getGameObject().setThisAttribute(Constants.SEARCHED_PIT);
				return "Blindly Reaching into the Pit";
			}
		}
		
		if (tileLocation==null) {
			if (treasureLocation.hasThisAttribute(RealmComponent.CACHE_CHIT)) {
				// This is a special case, where the person is looting someone's CACHE.  The first item in the
				// "pile" is gold, if there is any
				CharacterWrapper cache = new CharacterWrapper(treasureLocation);
				int gold = (int)cache.getGold();
				if (gold>0) {
					treasureNumber--;
					if (treasureNumber==0) {
						// The looter scores the gold!
						character.addGold(gold);
						cache.setGold(0.0);
						CacheChitComponent ccc = (CacheChitComponent)RealmComponent.getRealmComponent(treasureLocation);
						ccc.testEmpty();
						return "Found "+gold+" gold!";
					}
				}
			}
			treasures = TreasureUtility.getTreasures(treasureLocation,null);
		}
		else {
			treasures = ClearingUtility.getAbandonedItems(tileLocation);
		}
		String ret = "Nothing";
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = getTableKey();
		qp.actionType = CharacterActionType.SearchTable;
		qp.targetOfSearch = treasureLocation;
		qp.searchType = SearchResultType.getLootSearchResultType(treasureNumber);
		for (GameObject thing : treasures) {
			treasureNumber--;
			if (treasureNumber == 0) {
				ret = characterFindsItem(character, thing);
				if (treasureLocation.hasThisAttribute(RealmComponent.CACHE_CHIT)) {
					CacheChitComponent ccc = (CacheChitComponent)RealmComponent.getRealmComponent(treasureLocation);
					ccc.testEmpty();
				}
				qp.objectList.add(thing);
				qp.searchHadAnEffect = true;
			}
		}
		character.testQuestRequirements(getParentFrame(),qp);
		return ret;
	}

	protected String takeWeapon(CharacterWrapper character) {
		if (tileLocation!=null) return null;
		for (GameObject thing : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isWeapon()) {
				return characterFindsItem(character, thing);
			}
		}
		return "Nothing";
	}

	protected String takeArmor(CharacterWrapper character) {
		if (tileLocation!=null) return null;
		for (GameObject thing : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isArmor()) {
				return characterFindsItem(character, thing);
			}
		}
		return "Nothing";
	}

	protected String takeHorse(CharacterWrapper character) {
		if (tileLocation!=null) return null;
		for (GameObject thing : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isHorse()) {
				return characterFindsItem(character, thing);
			}
		}
		return "Nothing";
	}
	
	protected String takeWeaponType(CharacterWrapper character,String type) {
		if (tileLocation!=null) return null;
		for (GameObject thing : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isWeapon() && rc.getGameObject().hasThisAttribute(type)) {
				return characterFindsItem(character, thing);
			}
		}
		return "Nothing";
	}
	
	protected String takeArmorType(CharacterWrapper character,String type) {
		if (tileLocation!=null) return null;
		for (GameObject thing : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isArmor() && rc.getGameObject().hasThisAttribute(type)) {
				return characterFindsItem(character, thing);
			}
		}
		return "Nothing";
	}

	protected String takeTreasure(CharacterWrapper character) {
		if (tileLocation!=null) return null;
		for (GameObject thing : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isTreasure()) {
				return characterFindsItem(character, thing);
			}
		}
		return "Nothing";
	}
	
	public String learnSpell(CharacterWrapper character) {
		if (tileLocation!=null) return null;
		for (GameObject spell : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(spell);
			if (rc.isSpell()) {
				dumpGoldSpecialsToClearing();
				if (character.canLearn(spell)) {
					character.recordNewSpell(getParentFrame(),spell);
					return "Learned "+SpellUtility.getSpellName(spell);
				}
			}
		}
		return "Nothing";
	}

	public String characterFindsItem(CharacterWrapper character, GameObject thing) {
		if (!thing.hasThisAttribute(Constants.TREASURE_SEEN)) {
			thing.setThisAttribute(Constants.TREASURE_SEEN);
		}
		
		// This is it!  The character found the treasure...
		RealmComponent rc = RealmComponent.getRealmComponent(thing);
		if (rc.isTreasure()) {
			CardComponent card = (CardComponent)rc;
			if (!card.isFaceUp()) {
				card.setFaceUp();
			}
		}
		
		// Handle TLs with "draw=fatigue" here (only pool, I think)
		if (drawPr!=null && !thing.hasThisAttribute("discovery")) { // Site cards don't count as "items"
			// Handle fatigue_t and key here
			while(!drawPr.fullfilled(getParentFrame(),character,getListener())) {
				JOptionPane.showMessageDialog(getParentFrame(),"You MUST fatigue a chit!","",JOptionPane.WARNING_MESSAGE);
			}
		}
		
		/*
		 * Mouldy Skeleton - Roll for CURSE (this.curse).  armor is added to top of loot pile (this.add_to_pile),
		 * with gold on very top (position 1), silver next, and jade last (armor.this.pile_position)
		 * 
		 * Thief Remains - Roll for CURSE (this.curse).  Gain 20 gold (this.gold_reward).  Gain both treasures (this.no_loot)
		 */

		if (!thing.hasThisAttribute(Constants.NEEDS_OPEN)) {
			// If doesn't need to be opened, or is already opened, then handle special attributes before adding
			handleSpecial(character,thing,true,true);
		}
		else {
			// If the treasure is not "open", then just add the treasure directly
			addItemToCharacter(getParentFrame(),getListener(),character,thing,HostPrefWrapper.findHostPrefs(thing.getGameData()));
		}
		
		/*
		 *  Rule 7.5.5.e: When an individual draws an Enchanted card, a Site card,
		 *  the Mouldy Skeleton or the Remains of Thief, he must reveal it
		 *  instantly (see the List of Treasures). When he draws any other
		 *  card he keeps it secret until he activates it.
		 *  2nd Edition Rule # (optional) 	
		 */
		
		GameObject source = thing.getHeldBy();
		if (source!=null && source.hasThisAttribute(Constants.MIN_LARGE_T) && !source.hasThisAttribute(Constants.DESTROYED)) {
			int minLarge = source.getThisInt(Constants.MIN_LARGE_T);
			int totalLarge = 0;
			for (GameObject go : source.getHold()) {
				String treasureSize = go.getThisAttribute(RealmComponent.TREASURE);
				if (treasureSize!=null && "large".equals(treasureSize) && !go.hasThisAttribute(Constants.TREASURE_SEEN)) {
					totalLarge++;
				}
			}
			if (totalLarge<minLarge) {
				TreasureUtility.destroyGenerator(character,source);
			}
		}
		
		dumpGoldSpecialsToClearing();
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(thing.getGameData());
		if (hostPrefs.hasPref(Constants.HOUSE1_NO_SECRETS) || thing.hasThisAttribute(Constants.NO_SECRET)) {
			return "Found " + thing.getName();
		}
		return "Found ##"+StringUtilities.capitalize(rc.getName())+"|"+thing.getName()+"##";
	}
	private void dumpGoldSpecialsToClearing() {
		if (tileLocation!=null) {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			ArrayList<GameObject> boxes = pool.find("summon_t="+treasureLocation.getName().toLowerCase());
			for (GameObject box : boxes) {
				ClearingUtility.dumpGoldSpecialsToTile(tileLocation.tile.getGameObject(),box,tileLocation.clearing.getNum());
			}
		}
	}
	public static void addItemToCharacter(JFrame frame,ChangeListener listener,CharacterWrapper character,GameObject thing) {
		addItemToCharacter(frame,listener,character,thing,HostPrefWrapper.findHostPrefs(thing.getGameData()));
	}
	public static void addItemToCharacter(JFrame frame,ChangeListener listener,CharacterWrapper character,GameObject thing,HostPrefWrapper hostPrefs) {
		boolean drop = false;
		boolean abandon = false;
		if (!thing.hasThisAttribute(Constants.TREASURE_SEEN)) {
			thing.setThisAttribute(Constants.TREASURE_SEEN);
		}
		RealmComponent rc = RealmComponent.getRealmComponent(thing);
		if (thing.hasThisAttribute("color_source")) {
			// Found a color emitting object - better make sure that they actually want to take it!
			if (rc.isTreasure()) {
				TreasureCardComponent treasure = (TreasureCardComponent)rc;
				treasure.setFaceUp();
			}
			ButtonOptionDialog dialog = new ButtonOptionDialog(
					frame,
					rc.getIcon(),
					"This object emits color magic, and can energize spells.  What do you want to do?",
					"Found "+thing.getName(),
					false);
			dialog.addSelectionObject("Take the "+thing.getName());
			if (hostPrefs.hasPref(Constants.ADV_DROPPING)) {
				dialog.addSelectionObject("Drop the "+thing.getName());
			}
			dialog.addSelectionObject("Abandon the "+thing.getName());
			dialog.setVisible(true);
			String choice = (String)dialog.getSelectedObject();
			
			if (choice.startsWith("Drop")) {
				drop = true;
			}
			else if (choice.startsWith("Abandon")) {
				abandon = true;
			}
		}
		else {
			if (rc.isTreasure() || rc.isWeapon() || rc.isArmor()) {
				Strength weight = rc.getWeight();
				
				// Make sure item isn't heavier than the character can handle
				Strength moveStrength = character.getMoveStrength(true,true);
				if (!moveStrength.strongerOrEqualTo(weight)) {
					if (rc.isTreasure()) {
						TreasureCardComponent treasure = (TreasureCardComponent)rc;
						treasure.setFaceUp();
					}
					ButtonOptionDialog dialog = new ButtonOptionDialog(
							frame,
							rc.getIcon(),
							"The "+thing.getName()+" is too heavy for your character to manage right now.\n"
							+"This will prevent your character from being able to run or move.  What do you want to do?",
							"Found "+thing.getName(),
							false);
					dialog.addSelectionObject("Take the "+thing.getName());
					if (hostPrefs.hasPref(Constants.ADV_DROPPING)) {
						dialog.addSelectionObject("Drop the "+thing.getName());
					}
					dialog.addSelectionObject("Abandon the "+thing.getName());
					dialog.setVisible(true);
					String choice = (String)dialog.getSelectedObject();
					
					if (choice.startsWith("Drop")) {
						drop = true;
					}
					else if (choice.startsWith("Abandon")) {
						abandon = true;
					}
				}
			}
		}
		if (drop || abandon) {
			TreasureUtility.doDrop(character,thing,listener,drop);
		}
		else {
			if (rc.isTreasure()) {
				TreasureCardComponent treasure = (TreasureCardComponent)rc;
				treasure.setFaceUp();
			}
			rc.setCharacterTimestamp(character);
			thing.setThisAttribute(Constants.TREASURE_NEW);
			character.getGameObject().add(thing);
			character.checkInventoryStatus(frame,thing,listener);
		}
	}
	/**
	 * This should be called upon receiving an item, or opening an item (like the chest) for the first time.
	 */
	public void handleSpecial(CharacterWrapper character, GameObject thing,boolean addByDefault) {
		handleSpecial(character,thing,addByDefault,false);
	}
	public void handleSpecial(CharacterWrapper character, GameObject thing,boolean addByDefault,boolean testQuestRequirementsForLooting) {
		if (thing.hasThisAttribute("curse")) {
			setNewTable(new Curse(getParentFrame(), character.getGameObject()));
		}
		if (thing.hasThisAttribute("mesmerize")) {
			setNewTable(new Mesmerize(getParentFrame(), character.getGameObject()));
		}
		if (thing.hasThisAttribute("add_to_pile") && treasureLocation != null) {
			// Add everything to pile
			ArrayList<GameObject> list = new ArrayList<GameObject>(thing.getHold());
			Collections.sort(list,new Comparator<GameObject>() { // sort by pile position (if any)
				public int compare(GameObject go1,GameObject go2) {				
					int pos1 = go1.getThisInt("pile_position");
					int pos2 = go2.getThisInt("pile_position");
					
					return pos1-pos2;
				}
			});
			
			int listCount = list.size();
			
			ArrayList<GameObject> pile = new ArrayList<GameObject>(treasureLocation.getHold());
			pile.addAll(0,list);
			for (GameObject go : pile) {
				treasureLocation.add(go);
			}
			JOptionPane.showMessageDialog(getParentFrame(),listCount+" treasure"+(listCount==1?"":"s")+" added to the "+treasureLocation.getName()+" pile.","New Treasures",JOptionPane.INFORMATION_MESSAGE);
		}
		if (thing.hasThisAttribute("gold_reward")) {
			int gold = thing.getThisInt("gold_reward");
			character.addGold(gold);
			JOptionPane.showMessageDialog(getParentFrame(),"Received "+gold+" gold.","Found Gold",JOptionPane.INFORMATION_MESSAGE);
		}
		if (thing.hasThisAttribute("gold_penalty")) {
			int gold = thing.getThisInt("gold_penalty");
			character.addGold(-gold);
			JOptionPane.showMessageDialog(getParentFrame(),"Lost "+gold+" gold.","Lost Gold",JOptionPane.INFORMATION_MESSAGE);
		}
		if (thing.hasThisAttribute("enchant_tile")) {
			TileLocation loc = character.getCurrentLocation();
			if (loc!=null && loc.tile!=null) {
				loc.tile.setDarkSideUp();
				JOptionPane.showMessageDialog(getParentFrame(),"Current location was enchanted.","Tile enchanted",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (thing.hasThisAttribute(Constants.NO_LOOT)) {
			// Gain all treasures immediately
			RealmComponentDisplayDialog dialog = new RealmComponentDisplayDialog(getParentFrame(),"Found "+thing.getName(),"The "+thing.getName()+" contained the following items:");
			ArrayList<GameObject> inside = new ArrayList<GameObject>(thing.getHold());
			ArrayList<GameObject> gain = new ArrayList<>();
			for (GameObject go : inside) {
				RealmComponent goRc = RealmComponent.getRealmComponent(go);
				dialog.addRealmComponent(goRc);
				if (goRc.isCard()) {
					CardComponent card = (CardComponent)goRc;
					card.setFaceUp();
				}
				gain.add(go);
			}
			dialog.setVisible(true);
			
			// Add these AFTER the player has seen the reason for the gain
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(thing.getGameData());
			for (GameObject go:gain) {
				addItemToCharacter(getParentFrame(),getListener(),character,go,hostPrefs);
				if (testQuestRequirementsForLooting) {
					QuestRequirementParams qp = new QuestRequirementParams();
					qp.actionName = "Loot";
					qp.actionType = CharacterActionType.SearchTable;
					qp.searchHadAnEffect = true;
					qp.objectList.add(go);
					character.testQuestRequirements(getParentFrame(),qp);
				}
			}
		}
		if (thing.hasThisAttribute(Constants.CANNOT_MOVE) && treasureLocation != null) {
			// Discover this site card immediately (not found by normal searching!!)
			character.addTreasureLocationDiscovery(thing.getName());
			
			// Add back to the bottom of the treasure pile, but face up, so others can find it!
			TreasureCardComponent card = (TreasureCardComponent)RealmComponent.getRealmComponent(thing);
			card.setFaceUp();
			treasureLocation.add(thing);
			
			// Save the siteChitName, so it can be added to spies discovery list when someone
			// loots this Site card (see apply(...) method above)
			thing.setThisAttribute("siteChitName",treasureLocation.getName());
		}
		else if (thing.hasThisAttribute("remove_from_play")) {
			// Discard from play
			thing.detach();
		}
		else if (addByDefault) {
			addItemToCharacter(getParentFrame(),getListener(),character,thing,HostPrefWrapper.findHostPrefs(thing.getGameData()));
		}
	}
	@Override
	protected ArrayList<ImageIcon> getHintIcons(CharacterWrapper character) {
		ArrayList<ImageIcon> list = new ArrayList<>();
		list.add(getIconForSearch(RealmComponent.getRealmComponent(treasureLocation)));
		return list;
	}
}