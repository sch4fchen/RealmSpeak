package com.robin.magic_realm.components.utility;

import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.robin.game.objects.*;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.general.swing.DieRoller;
import com.robin.general.swing.IconGroup;
import com.robin.general.util.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.swing.RealmObjectChooser;
import com.robin.magic_realm.components.table.*;
import com.robin.magic_realm.components.wrapper.*;

public class TreasureUtility {
	public enum ArmorType {
		None,
		Shield,
		Helmet,
		Breastplate,
		Armor,
		Charge,
		Dodge,
		Duck,
		Special,
	}

	/**
	 * @return		true on success
	 */
	public static boolean doActivate(JFrame parentFrame,CharacterWrapper character,GameObject thing,ChangeListener listener,boolean fromCombat) {
		if (thing!=null) {
			if (thing.hasThisAttribute(Constants.NO_ACTIVATE)) {
				JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" is broken, and can no longer be activated.");
				return false;
			}
			if (thing.hasThisAttribute(Constants.COMBAT_ONLY) && !fromCombat) {
				JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" can only be activated during combat.");
				return false;
			}
			if ((RealmComponent.getRealmComponent(thing)).getWeight().isMaximum()) {
				JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" has maximum weight, and can not be activated.");
				return false;
			}
			
			/* Activating items in inventory:  Need to do a few checks and balances here.
			 * Can't have multiple horses activated, for example.  What are the rules on this?
			 * 
			 * - Boots must have getThisAttribute("strength") equal to or higher than
			 * 		character getThisAttribute("vulnerability") and EVERY item in
			 * 		inventory getThisAttribute(Constants.WEIGHT) to be active
			 * - Only ONE Boots card active at a time:  hasThisAttribute("boots")
			 * - Only ONE Horse card active at a time:  hasThisAttribute("horse")
			 * - Horse must have getAttribute("trot","strength") equal to or higher than
			 * 		character getThisAttribute("vulnerability") and EVERY item in
			 * 		inventory getThisAttribute(Constants.WEIGHT) to be active
			 * - Only ONE Gloves card active at a time:  hasThisAttribute("gloves")
			 * - Only ONE Helmet
			 * - Only ONE Breastplate
			 * - Only ONE Shield
			 * - Only ONE Suit of Armor
			 * - No limitation to armor cards
			 * - Only ONE Weapon counter OR card active at a time (are there ANY weapon cards?  Didn't see any...)
			 */
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			
			// Before anything else, check for item restrictions (custom characters)
			ArrayList<String> itemRestrictions = character.getGameObject().getThisAttributeList(Constants.ITEM_RESTRICTIONS);
			if (itemRestrictions!=null && !itemRestrictions.isEmpty()) {
				// Check first by name
				for (String restriction : itemRestrictions) {
					ArrayList<String> thingWords = StringUtilities.stringToCollection(thing.getName()," ");
					if (restriction.equals(thing.getName()) || thingWords.contains(restriction)) {
						JOptionPane.showMessageDialog(parentFrame,"Your character is not allowed to activate the "+thing.getName()+" (see advantages).");
						return false;
					}
				}
				
				// Check specials
				if (itemRestrictions.contains("Boots") && thing.hasThisAttribute("boots")) {
					JOptionPane.showMessageDialog(parentFrame,"Your character is not allowed to wear boots (see advantages).");
					return false;
				}
				if (itemRestrictions.contains("Gloves") && thing.hasThisAttribute("gloves")) {
					JOptionPane.showMessageDialog(parentFrame,"Your character is not allowed to wear gloves (see advantages).");
					return false;
				}
				if (itemRestrictions.contains("Books") && thing.hasThisAttribute("book")) {
					JOptionPane.showMessageDialog(parentFrame,"Your character is not allowed to use books (see advantages).");
					return false;
				}
			}
			
//			Strength characterVulnerability = new Strength(character.getGameObject().getThisAttribute("vulnerability"));
			Strength characterCarryWeight = character.getNeededSupportWeight();
			ArrayList<GameObject> activeInventory = character.getActiveInventory();
			ArmorType armorType = getArmorType(thing);
			if (rc.isHorse()) {
				// Horse
				
				// Horses cannot be activated in caves!
				TileLocation tl = character.getCurrentLocation();
				if (tl!=null && tl.isInClearing() && tl.clearing.isCave() && !rc.getGameObject().hasThisAttribute(Constants.STEED_IN_CAVES_AND_WATER)) {
					JOptionPane.showMessageDialog(parentFrame,"You cannot activate a horse in a cave.");
					return false;
				}
				if (tl!=null && tl.isInClearing() && tl.clearing.isWater() && !rc.getGameObject().hasThisAttribute(Constants.STEED_IN_CAVES_AND_WATER)) {
					JOptionPane.showMessageDialog(parentFrame,"You cannot activate a horse in a river.");
					return false;
				}
				
				// Check to see that horse is strong enough to carry character
				Strength horseStrength = new Strength(thing.getAttribute("trot","strength"));
				if (horseStrength.strongerOrEqualTo(characterCarryWeight)) {
					// If good, then inactivate any existing horses
					for (GameObject otherThing : activeInventory) {
						if (otherThing.hasThisAttribute("horse")) {
							otherThing.removeThisAttribute(Constants.ACTIVATED);
							break; // no need to keep searching as long as this code is in place
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(parentFrame,"That horse is not strong enough to carry your character.");
					return false;
				}
			}
			else if (thing.hasThisAttribute("boots")) {
				// Boots card
				
				// Check to see that boots are strong enough to carry character
				Strength bootStrength = RealmUtility.getBootsStrength(thing);
				if (bootStrength.strongerOrEqualTo(characterCarryWeight)) {
					// If good, then inactivate any existing boots
					for (GameObject otherThing : activeInventory) {
						if (otherThing.hasThisAttribute("boots")) {
							otherThing.removeThisAttribute(Constants.ACTIVATED);
							break; // no need to keep searching as long as this code is in place
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(parentFrame,"Those boots are not strong enough to support your character.");
					return false;
				}
			}
			else if (thing.hasThisAttribute("gloves")) {
				// Gloves
				// Inactivate any existing gloves
				for (GameObject otherThing : activeInventory) {
					if (otherThing.hasThisAttribute("gloves")) {
						otherThing.removeThisAttribute(Constants.ACTIVATED);
						break; // no need to keep searching as long as this code is in place
					}
				}
			}
			else if (armorType!=ArmorType.None && armorType!=ArmorType.Special) {
				// Inactivate any existing armor of the same ArmorType
				for (GameObject otherThing : activeInventory) {
					ArmorType otherArmor = getArmorType(otherThing);
					if (otherArmor==armorType) {
						if (doDeactivate(parentFrame,character,otherThing)) {
							break; // no need to keep searching as long as this code is in place
						}
						return false;
					}
				}
				
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(thing.getGameData());
				boolean twoHandedWeaponResctriction = hostPrefs.hasPref(Constants.OPT_TWO_HANDED_WEAPONS) && !character.affectedByKey(Constants.STRONG);
				boolean dualWielding = hostPrefs.hasPref(Constants.OPT_DUAL_WIELDING) || character.affectedByKey(Constants.DUAL_WIELDING);
				if (thing.hasThisAttribute(Constants.SHIELD) && (twoHandedWeaponResctriction || dualWielding)) {
					boolean secondWeaponActive = false;
					for (GameObject otherThing : activeInventory) {
						if (otherThing.hasThisAttribute("weapon") || (otherThing.hasThisAttribute(Constants.POTION) && otherThing.hasThisAttribute("attack"))) {
							if (twoHandedWeaponResctriction) {
								boolean twoHandedMissleWeaponWithFumbleRule = !hostPrefs.hasPref(Constants.OPT_FUMBLE) && otherThing.hasThisAttribute(Constants.TWO_HANDED);
								boolean towHandedWeaponWithoutFumbleRule = hostPrefs.hasPref(Constants.OPT_FUMBLE) && otherThing.hasThisAttribute(Constants.TWO_HANDED) && otherThing.hasThisAttribute("missile");
								if (twoHandedMissleWeaponWithFumbleRule || towHandedWeaponWithoutFumbleRule) {
									JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" can only be activated with a one-handed weapon at the same time.");
									return false;
								}
							}
							if (dualWielding && secondWeaponActive == true) {
									otherThing.removeThisAttribute(Constants.ACTIVATED);
							}
							secondWeaponActive = true;
						}
					}
				}
			}
			else if (thing.hasThisAttribute("weapon")) {
				// Inactivate any existing weapon
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(thing.getGameData());
				for (GameObject otherThing : activeInventory) {
					if (otherThing.hasThisAttribute("weapon") || (otherThing.hasThisAttribute(Constants.POTION) && otherThing.hasThisAttribute("attack"))) {
						if ((hostPrefs.hasPref(Constants.OPT_DUAL_WIELDING) || character.affectedByKey(Constants.DUAL_WIELDING))
							&& (((character.affectedByKey(Constants.STRONG) || character.affectedByKey(Constants.DUAL_WIELDING_TWO_HANDED) || hostPrefs.hasPref(Constants.OPT_DUAL_WIELDING_STRONG) || hostPrefs.hasPref(Constants.OPT_DUAL_WIELDING_HEAVY)) && (!thing.hasThisAttribute(Constants.TWO_HANDED) || !thing.hasThisAttribute("missile")) && (!otherThing.hasThisAttribute(Constants.TWO_HANDED) || !otherThing.hasThisAttribute("missile")))
							||
							(!thing.hasThisAttribute(Constants.TWO_HANDED) && !otherThing.hasThisAttribute(Constants.TWO_HANDED)
							&&
							((hostPrefs.hasPref(Constants.OPT_DUAL_WIELDING_HEAVY) || character.affectedByKey(Constants.DUAL_WIELDING_HEAVY)) || (character.getVulnerability().strongerThan((RealmComponent.getRealmComponent(thing)).getWeight()) && character.getVulnerability().strongerThan((RealmComponent.getRealmComponent(otherThing)).getWeight())))
							))) {
							boolean secondWeaponActive = false;
							for (GameObject activeItem : activeInventory) {
								if (activeItem.hasThisAttribute(Constants.SHIELD)) {
									activeItem.removeThisAttribute(Constants.ACTIVATED);
								}
								if (activeItem.hasThisAttribute("weapon") || (otherThing.hasThisAttribute(Constants.POTION) && otherThing.hasThisAttribute("attack"))) {
									if (secondWeaponActive) {
										activeItem.removeThisAttribute(Constants.ACTIVATED);
									}
									secondWeaponActive = true;
								}
							}
						}
						else {
							if (otherThing.hasThisAttribute("weapon")) {
								WeaponChitComponent weapon = (WeaponChitComponent)RealmComponent.getRealmComponent(otherThing);
								if (weapon.isAlerted() && !weapon.getGameObject().hasThisAttribute(Constants.ALERTED_WEAPON) && !weapon.getGameObject().hasThisAttribute(Constants.ENCHANTED_ALERTED_WEAPON)) {
									CharacterChitComponent chit = (CharacterChitComponent)RealmComponent.getRealmComponent(character.getGameObject());
									if (!chit.activeWeaponStaysAlerted(weapon)) {
										int ret = JOptionPane.showConfirmDialog(parentFrame,"You are about to deactivate an alerted weapon.  Are you sure?","",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
										if (ret==JOptionPane.NO_OPTION) {
											return false;
										}
										weapon.setAlerted(false);
									}
								}	
							}
							otherThing.removeThisAttribute(Constants.ACTIVATED);
						}
					}
					
					if (hostPrefs.hasPref(Constants.OPT_TWO_HANDED_WEAPONS) && otherThing.hasThisAttribute(Constants.SHIELD) && !character.affectedByKey(Constants.STRONG)) {
						boolean twoHandedMissleWeaponWithFumbleRule = !hostPrefs.hasPref(Constants.OPT_FUMBLE) && thing.hasThisAttribute(Constants.TWO_HANDED);
						boolean towHandedWeaponWithoutFumbleRule = hostPrefs.hasPref(Constants.OPT_FUMBLE) && thing.hasThisAttribute(Constants.TWO_HANDED) && thing.hasThisAttribute("missile");
						if (twoHandedMissleWeaponWithFumbleRule || towHandedWeaponWithoutFumbleRule) {
							otherThing.removeThisAttribute(Constants.ACTIVATED);
						}
					}
				}
			}
			else if (thing.hasThisAttribute(Constants.NEEDS_OPEN)) {
				Collection<GameObject> openable = new ArrayList<>();
				openable.add(thing);
				
				boolean success = TreasureUtility.openOneObject(parentFrame,character,openable,listener,false)!=null;
				if (success) {
					QuestRequirementParams qp = new QuestRequirementParams();
					qp.actionType = CharacterActionType.ActivatingItem;
					qp.objectList = new ArrayList<>();
					qp.objectList.add(thing);
					character.testQuestRequirements(parentFrame,qp);
				}
				return success;
			}
			if (thing.hasThisAttribute(Constants.BEAST_AWAY)) {
				TileLocation current = character.getCurrentLocation();
				if (current.isInClearing()) {
					for (RealmComponent cc:current.clearing.getClearingComponents(false)) {
						if (cc.isMonster()) {
							SetupCardUtility.resetDenizen(cc.getGameObject());
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" can only be activated in a clearing.",thing.getName(),JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
			if (thing.hasThisAttribute(Constants.GENERATOR_FLAMED)) {
				TileLocation current = character.getCurrentLocation();
				if (current.isInClearing()) {
					StringBufferedList sb = new StringBufferedList();
					for (RealmComponent cc:current.clearing.getClearingComponents(false)) {
						if (cc.isTreasureLocation()
								&& cc.getGameObject().hasThisAttribute(Constants.GENERATOR)
								&& !cc.getGameObject().hasThisAttribute(Constants.DESTROYED)
								&& Constants.GENERATOR_FLAMED.equals(cc.getGameObject().getThisAttribute(Constants.SUSCEPTIBLETO))) {

							TreasureUtility.destroyGenerator(character,cc.getGameObject());
							sb.append(cc.getGameObject().getName());
						}
					}
					if (sb.size()==0) {
						JOptionPane.showMessageDialog(parentFrame,"Nothing in the clearing was affected by the "+thing.getName(),thing.getName(),JOptionPane.WARNING_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" destroyed the "+sb.toString(),thing.getName(),JOptionPane.WARNING_MESSAGE);
					}
				}
				else {
					JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" can only be activated in a clearing.",thing.getName(),JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
			if (thing.hasThisAttribute(Constants.COMBINE_COUNT)) {
				int count = thing.getThisInt(Constants.COMBINE_COUNT);
				String target = thing.getThisAttribute(Constants.COMBINE);
				ArrayList<GameObject> list = character.getAllActiveInventoryThisKeyAndValue(Constants.COMBINE,target);
				list.add(thing);
				
				if (count==list.size()) {
					// Combine achieved!  Create object
					GameObject result = thing.getGameData().getGameObjectByName(target);
					character.getGameObject().add(result);
					for(GameObject go:list) {
						go.removeThisAttribute(Constants.ACTIVATED);
						character.getGameObject().remove(go);
					}
				}
			}
			if (thing.hasThisAttribute(Constants.ADD_CHIT)) {
				character.getGameObject().addAll(thing.getHold());
			}
			if (thing.hasThisAttribute(Constants.MAJOR_WOUND)) {
				character.setExtraWounds(4);
			}
			if (thing.hasThisAttribute(Constants.NOPIN)) {
				CombatWrapper combat = new CombatWrapper(character.getGameObject());
				for (RealmComponent attacker:combat.getAttackersAsComponents()) {
					if (attacker.isMonster()) {
						MonsterChitComponent monster = (MonsterChitComponent)attacker;
						if (monster.isPinningOpponent()) {
							monster.flip();
						}
					}
				}
			}
			if (thing.hasThisAttribute(Constants.CONVERT_CHITS)) {
				if (!doConvertChitsToNew(character,thing)) {
					return false;
				}
			}
			if (thing.hasThisAttribute(Constants.COLOR_CAPTURE)) {
				if (!doColorCapture(parentFrame,character,thing)) {
					return false;
				}
			}
			if (thing.hasThisAttribute(Constants.SUMMON_COMPANION) && !thing.hasThisAttribute(Constants.POTION)) {
				GameObject companion = getCompanionFromItem(thing);
				character.addHireling(companion,Constants.TEN_YEARS);
				CombatWrapper combat = new CombatWrapper(companion);
				combat.setSheetOwner(true); // in case you are in combat!
				if (character.getCurrentLocation().clearing!=null) {
					character.getCurrentLocation().clearing.add(companion,null);
				}
				thing.setThisAttribute(Constants.SUMMON_COMPANION_ID,companion.getStringId());
				companion.setThisAttribute(Constants.DESTROY_TREASURE_WHEN_KILLED,thing.getStringId());
			}
			if (thing.hasThisAttribute(Constants.COMPANION_FROM_HOLD)) {
				ArrayList<GameObject> companions = new ArrayList<>(thing.getHold());
				StringBufferedList list = new StringBufferedList();
				IconGroup group = new IconGroup(rc.getIcon(),IconGroup.VERTICAL,5);
				for(GameObject companion : companions) {
					character.getGameObject().add(companion);
					character.addHireling(companion);
					list.append(companion.getName());
					RealmComponent crc = RealmComponent.getRealmComponent(companion);
					group.addIcon(crc.getIcon());
					CombatWrapper combat = new CombatWrapper(companion);
					combat.setSheetOwner(true); // in case you are in combat!
					if (thing.hasThisAttribute(Constants.COMPANION_FROM_HOLD_RETURNS)) {
						thing.addThisAttributeListItem(Constants.COMPANION_FROM_HOLD_RETURNS,companion.getStringId());
						companion.addThisAttributeListItem(Constants.DESTROY_TREASURE_WHEN_KILLED,thing.getStringId());
					}
				}
				if (!thing.hasThisAttribute(Constants.COMPANION_FROM_HOLD_RETURNS)) {
					JOptionPane.showMessageDialog(parentFrame,"The "+thing.getName()+" vanishes, and a "+list.toString()+" appears in its place!",thing.getName(),JOptionPane.PLAIN_MESSAGE,group);
					character.getGameObject().remove(thing);
				}
				else {
					JOptionPane.showMessageDialog(parentFrame,"A "+list.toString()+" appears in its place!",thing.getName(),JOptionPane.PLAIN_MESSAGE,group);
				}
			}
			if (thing.hasThisAttribute(Constants.SPECIAL_ACTION)) {
				character.setNeedsActionPanelUpdate(true);
			}
			
			// thing might have gotten removed from inventory (ie., the Chest when it is opened), so check
			if (thing.getHeldBy()!=null && thing.getHeldBy().equals(character.getGameObject())) {
				if (thing.hasThisAttribute(Constants.PHASE_CHIT)) {
					String id = thing.getThisAttribute(Constants.SPELL_ID);
					GameObject go = character.getGameObject().getGameData().getGameObject(Long.valueOf(id));
					SpellWrapper spell = new SpellWrapper(go);
					spell.unaffectTargets();
					SpellMasterWrapper.getSpellMaster(go.getGameData()).addSpell(spell);

					character.applyPhaseChit(parentFrame, thing, spell);
				}
				else if (thing.hasThisAttribute(Constants.POTION)) {
					if (!TreasureUtility.handlePotionEffects(parentFrame,character,thing,listener)) {
						return false;
					}
				}
				thing.setThisAttribute(Constants.ACTIVATED);
				
				// treasures (including potions) that affect chits are applied here
				// Since we cannot know whether something else is still in effect, check all activated treasures/spells
				character.updateChitEffects();
			}
		}
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionType = CharacterActionType.ActivatingItem;
		qp.objectList = new ArrayList<>();
		qp.objectList.add(thing);
		character.testQuestRequirements(parentFrame,qp);
		return true;
	}
	
	private static boolean doColorCapture(JFrame parentFrame,CharacterWrapper character,GameObject thing) {
		ArrayList<ColorMagic> permColor = character.getInfiniteColorSources();
		ArrayList<String> permColorNames = new ArrayList<>();
		for (ColorMagic cm:permColor) {
			if (!permColorNames.contains(cm.getColorName())) {
				permColorNames.add(cm.getColorName());
			}
		}
		
		String colorName = null;
		if (permColorNames.size()==1) {
			colorName = permColorNames.get(0);
		}
		else if (permColorNames.size()>1) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parentFrame,"Choose a color to capture:",true);
			chooser.addStrings(permColorNames);
			chooser.setVisible(true);
			colorName = chooser.getSelectedText();
			if (colorName==null) return false;
		}
		
		if (colorName!=null) {
			ColorMagic cm = ColorMagic.makeColorMagic(colorName,true);
			int magicNumber = cm.getColorNumber();
			thing.setThisAttribute(Constants.ENCHANTED_COLOR,magicNumber);
			JOptionPane.showMessageDialog(parentFrame,cm.getColorName()+" magic was captured!  See chit panel...",thing.getName(),JOptionPane.INFORMATION_MESSAGE,cm.getIcon());
		}
		
		return true;
	}
	
	private static boolean doConvertChitsToNew(CharacterWrapper character,GameObject thing){
		ArrayList<String> chitBlocks = thing.getThisAttributeList(Constants.CONVERT_CHITS);
		int count = chitBlocks.size();
		RealmObjectChooser chooser = new RealmObjectChooser("Select "+count+" chit"+(count==1?"":"s")+" to convert:",thing.getGameData(),false,false);
		chooser.setValidCount(count);
		for (CharacterActionChitComponent chit:character.getAllChits()) {
			if (!chit.isTreasureChit()) {
				chooser.addComponentToChoose(chit);
			}
		}
		chooser.setVisible(true);
		
		ArrayList<GameObject> chosen = chooser.getChosenObjects();
		if (chosen==null) return false;
		
		for (int i=0;i<count;i++) {
			GameObject chit = chosen.get(i);
			chit.renameAttributeBlock("this","this_convert");
			String block = chitBlocks.get(i);
			chit.copyAttributeBlockFrom(thing,block);
			chit.renameAttributeBlock(block,"this");
			chit.setThisAttribute("icon_folder",character.getGameObject().getThisAttribute("icon_folder"));
			chit.setThisAttribute("icon_type",character.getGameObject().getThisAttribute("icon_type"));
			chit.setThisAttribute("convertedby",thing.getStringId());
		}
		character.updateChitEffects();
		return true;
	}
	private static void doRestoreConvertedChits(CharacterWrapper character,GameObject thing) {
		for (RealmComponent rc : character.getAllChits()) {
			GameObject chit = rc.getGameObject();
			if (chit.hasThisAttribute("convertedby")) {
				String thingId = chit.getThisAttribute("convertedby");
				if (thingId.equals(thing.getStringId())) {
					chit.removeAttributeBlock("this");
					chit.renameAttributeBlock("this_convert","this");
				}
			}
		}
	}
	private static boolean handlePotionEffects(JFrame parentFrame,CharacterWrapper character,GameObject thing,ChangeListener listener) {
		// some potion and phase chit effects are immediate
		if (thing.hasThisAttribute("attack")) {
			ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
			if (weapons!=null && !weapons.isEmpty()) {
				int ret = JOptionPane.showConfirmDialog(parentFrame,"You are about to deactivate your primary weapon(s).  Are you sure?","",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
				if (ret==JOptionPane.NO_OPTION) {
					return false;
				}
				for (WeaponChitComponent weapon : weapons) {
					if (weapon.isAlerted()) {
						weapon.setAlerted(false);
					}
					weapon.getGameObject().removeThisAttribute(Constants.ACTIVATED);
				}
			}
		}
		if (thing.hasThisAttribute(Constants.REPAIR_ONE)) {
			ArrayList<ArmorChitComponent> list = new ArrayList<>();
			for(GameObject go:character.getInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isArmor()) {
					ArmorChitComponent armor = (ArmorChitComponent)rc;
					if (armor.isDamaged()) {
						list.add(armor);
					}
				}
			}
			if (list.size()>0) {
				RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parentFrame,"Repair which?",false);
				chooser.addRealmComponents(list,false);
				chooser.setVisible(true);
				if (chooser.getSelectedText()!=null) {
					ArmorChitComponent armor = (ArmorChitComponent)chooser.getFirstSelectedComponent();
					armor.setIntact(true);
					RealmLogging.logMessage(character.getGameObject().getName(),"Repaired "+armor.getGameObject().getName()+" with "+thing.getName());
				}
			}
		}
		if (thing.hasThisAttribute(Constants.RANDOM_TL)) {
			// Discover Random Treasure Location
			GamePool pool = new GamePool(thing.getGameData().getGameObjects());
			ArrayList<GameObject> tls = pool.find("treasure_location,!cannot_move,!treasure_within_treasure");
			int r = RandomNumber.getRandom(tls.size());
			GameObject go = tls.get(r);
			String tlName = go.getName();
			if (!character.hasTreasureLocationDiscovery(tlName)) {
				character.addTreasureLocationDiscovery(tlName);
				JOptionPane.showMessageDialog(parentFrame,"Reveals the location of the "+tlName+".");
			}
			else {
				JOptionPane.showMessageDialog(parentFrame,"Reveals the location of the "+tlName+", which you already know.");
			}
		}
		if (thing.hasThisAttribute(Constants.WOUNDS_TO_FATIGUE)) {
			character.doHealWoundsToFatigue();
		}
		if (thing.hasThisAttribute(Constants.WISH_AND_CURSE)) {
			// Roll wish and curse (same die roll for both tables)
			Wish wish = new Wish(parentFrame);
			Curse curse = new Curse(parentFrame, character.getGameObject());
			DieRoller roller = DieRollBuilder.getDieRollBuilder(parentFrame,character).createRoller(wish);
			roller.rollDice("Wish/Curse");
			wish.apply(character,roller);
			curse.apply(character,roller);
		}
		if (thing.hasThisAttribute(Constants.WISH_AND_MESMERIZE)) {
			// Roll wish and mesmerize (same die roll for both tables)
			Wish wish = new Wish(parentFrame);
			Mesmerize mesmerize = new Mesmerize(parentFrame, character.getGameObject());
			DieRoller roller = DieRollBuilder.getDieRollBuilder(parentFrame,character).createRoller(wish);
			roller.rollDice("Wish/Mesmerize");
			wish.apply(character,roller);
			mesmerize.apply(character,roller);
		}
		if (thing.hasThisAttribute(Constants.CANCEL_SPELL) || thing.hasThisAttribute(Constants.REMOVE_CURSE)) {
			String title = thing.hasThisAttribute(Constants.CANCEL_SPELL) ? "Select spell or curse to break:" : "Select curse to remove:";
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parentFrame,title,false);
			if (thing.hasThisAttribute(Constants.CANCEL_SPELL)) {
				// Select bewitching spells to cancel (spellcaster or target must be in clearing)
				TileLocation current = character.getCurrentLocation();
				SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(thing.getGameData());
				Collection<SpellWrapper> spells = sm.getAllSpellsInClearing(current,true);
				if (!spells.isEmpty()) {
					for (SpellWrapper spell : spells) {
						RealmComponent src = RealmComponent.getRealmComponent(spell.getGameObject());
						chooser.addRealmComponent(src);
					}
				}
			}
			
			// Select curse to cancel
			TileLocation loc = character.getCurrentLocation();
			if (loc != null) {
				for (RealmComponent rc : loc.clearing.getClearingComponents(false)) {
					if (rc.isCharacter()) {
						CharacterWrapper rcChar = new CharacterWrapper(rc.getGameObject());
						ArrayList<String> rcCurses = rcChar.getAllCurses();
						for (String rcCurse : rcCurses) {
							chooser.addRealmComponent(rc, rcCurse);
						}
					}
				}
			}
			else {
				Collection<String> curses = character.getAllCurses();
				for (String curse : curses) {
					chooser.addRealmComponent(RealmComponent.getRealmComponent(character.getGameData()), curse);
				}
			}
			
			if (chooser.hasOptions()) {
				chooser.setVisible(true);
				
				RealmComponent src = chooser.getFirstSelectedComponent();
				if (src != null) {
					if (src.isCharacter()) {
						CharacterWrapper rcChar = new CharacterWrapper(src.getGameObject());
						String curseToCancel = chooser.getSelectedText();
						rcChar.removeCurse(curseToCancel);
						JOptionPane.showMessageDialog(parentFrame,curseToCancel+" was removed from "+rcChar.getName()+".");
					}
					else {
						// Selected a spell
						SpellWrapper spell = new SpellWrapper(src.getGameObject());
						spell.expireSpell();
						JOptionPane.showMessageDialog(parentFrame,src.getGameObject().getName()+" was broken.");
					}
				}
			}
		}
		if (thing.hasThisAttribute(Constants.ENCHANT)) {
			enchantTile(parentFrame,character,listener);
			enchantChit(parentFrame,character);
		}
		if (thing.hasThisAttribute(Constants.DISENCHANT)) {
			thing.setThisAttribute(Constants.DISENCHANT_POTION_AFFECTED_CHARACTER,character.getGameObject().getStringId());
			character.getGameObject().setThisAttribute(Constants.DISENCHANT_POTION);
			character.nullifyCurses();
			SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(thing.getGameData());
			for (SpellWrapper spell:sm.getAffectingSpells(character.getGameObject())) {
				if (spell.isNullified()) continue;
				if (spell.isActive() && spell.hasAffectedTargets()) {
					spell.nullifySpell(false);
					RealmLogging.logMessage(character.getName(),"Spell effect nullified: "+spell.getName());
				}
			}
		}
		if (thing.hasThisAttribute(Constants.MAGIC_PATH)) {
			thing.setThisAttribute(Constants.MAGIC_PATH_AFFECTED_CHARACTER,character.getGameObject().getStringId());
			character.getGameObject().setThisAttribute(Constants.MAGIC_PATH_EFFECT);
		}
		if (thing.hasThisAttribute(Constants.CURDLE_OF_BONE)) {
			thing.setThisAttribute(Constants.CURDLE_OF_BONE_AFFECTED_CHARACTER,character.getGameObject().getStringId());
			character.getGameObject().setThisAttribute(Constants.CURDLE_OF_BONE);
		}
		if (thing.hasThisAttribute(Constants.HOLY_WATER)) {
			thing.setThisAttribute(Constants.HOLY_WATER_AFFECTED_CHARACTER,character.getGameObject().getStringId());
			character.getGameObject().setThisAttribute(Constants.HOLY_WATER);
			CombatWrapper characterCw = new CombatWrapper(character.getGameObject());
			ArrayList<RealmComponent> attackers = characterCw.getAttackersAsComponents();
			for (RealmComponent attacker : attackers) {
				if (attacker.getGameObject().hasThisAttribute(Constants.DEMON)
				|| attacker.getGameObject().hasThisAttribute(Constants.IMP)
				|| attacker.getGameObject().hasThisAttribute(Constants.SUCCUBUS)
				|| attacker.getGameObject().hasThisAttribute(Constants.VAMPIRE)
				|| attacker.getGameObject().hasThisAttribute(Constants.DEVIL)
				|| attacker.getGameObject().hasThisAttribute(Constants.UNDEAD)) {
					if (attacker.getTarget()!=null && attacker.getTarget().getGameObject() == character.getGameObject()) {
						attacker.clearTarget();
						characterCw.removeAttacker(attacker.getGameObject());
					}
					if (attacker.get2ndTarget()!=null && attacker.get2ndTarget().getGameObject() == character.getGameObject()) {
						attacker.clear2ndTarget();
						characterCw.removeAttacker(attacker.getGameObject());
					}
				}
			}
		}
		if (thing.hasThisAttribute(Constants.SUMMON_COMPANION)) {
			GameObject companion = getCompanionFromItem(thing);
			character.addHireling(companion,Constants.TEN_YEARS);
			CombatWrapper combat = new CombatWrapper(companion);
			combat.setSheetOwner(true); // in case you are in combat!
			if (character.getCurrentLocation().clearing!=null) {
				character.getCurrentLocation().clearing.add(companion,null);
			}
		}
		if (thing.hasThisAttribute(Constants.FLASH_BOMB)) {
			character.setHidden(true);
			CombatWrapper characterCw = new CombatWrapper(character.getGameObject());
			ArrayList<RealmComponent> attackers = characterCw.getAttackersAsComponents();
			for (RealmComponent attacker : attackers) {
				if (attacker.getTarget()!=null && attacker.getTarget().getGameObject() == character.getGameObject()) {
					attacker.clearTarget();
				}
				if (attacker.get2ndTarget()!=null && attacker.get2ndTarget().getGameObject() == character.getGameObject()) {
					attacker.clear2ndTarget();
				}
			}
			characterCw.removeAllAttackers();
		}
		
		if (thing.hasThisAttribute(Constants.PACIFY_MONSTERS)) {
			TileLocation loc = character.getCurrentLocation();
			if (loc != null) {
				for (RealmComponent rc : loc.clearing.getClearingComponents(false)) {
					if (rc.isMonster()) {
						CombatWrapper cw = new CombatWrapper(rc.getGameObject());
						cw.pacify();						
					}
				}
			}
		}
		
		if (thing.hasThisAttribute(Constants.STICKS_TO_ARMOR)) {
			ArrayList<ArmorChitComponent> list = new ArrayList<>();
			for(GameObject go:character.getInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isArmor()) {
					list.add((ArmorChitComponent)rc);
				}
			}
			if (list.size()>0) {
				RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parentFrame,"Apply to which armor?",false);
				chooser.addRealmComponents(list,false);
				chooser.setVisible(true);
				if (chooser.getSelectedText()!=null) {
					ArmorChitComponent armor = (ArmorChitComponent)chooser.getFirstSelectedComponent();
					if (thing.hasThisAttribute(Constants.OINTMENT_OF_STONE)) {
						armor.getGameObject().setThisAttribute(Constants.OINTMENT_OF_STONE);
						RealmLogging.logMessage(character.getGameObject().getName(),"Applies Ointment of Stone to "+armor.getGameObject().getName());
						thing.setThisAttribute(Constants.OINTMENT_OF_STONE_AFFECTED_ARMOR,armor.getGameObject().getStringId());
					}
				}
			}
		}
		
		WeaponChitComponent weapon = character.getActivePrimaryWeapon();
		if (thing.hasThisAttribute(Constants.ALERTED_WEAPON)) {
			if (weapon!=null) {
				if (!weapon.isAlerted()) {
					weapon.setAlerted(true);
				}
			}
		}
		GameObject weaponObject = null;
		if (weapon!=null) {
			weaponObject = weapon.getGameObject();
		}
		else {
			// Check for Treasure Weapons (Alchemists Mixture)
			for (GameObject item : character.getActiveInventory()) {
				if (item.hasThisAttribute("attack")) { // ONLY the Alchemists Mixture has this, for now!
					weaponObject = item;
					break;
				}
			}
		}
		if (weaponObject==null) {
			// applies to dagger, which is effectively the character himself
			weaponObject = character.getGameObject();
		}
		
		if (thing.hasThisAttribute(Constants.STICKS_TO_WEAPON)) {
			if (weaponObject!=null) {
				thing.setThisAttribute(Constants.AFFECTED_WEAPON_ID,weaponObject.getStringId());
			}
		}
		if (thing.hasThisAttribute(Constants.ADD_SHARPNESS)) {
			if (weaponObject!=null) {
				int val = weaponObject.getThisInt(Constants.ADD_SHARPNESS) + 1;
				weaponObject.setThisAttribute(Constants.ADD_SHARPNESS,val);
			}
		}
		if (thing.hasThisAttribute(Constants.IGNORE_ARMOR)) {
			if (weaponObject!=null) {
				int val = weaponObject.getThisInt(Constants.IGNORE_ARMOR) + 1; // stacks
				weaponObject.setThisAttribute(Constants.IGNORE_ARMOR,val);
			}
		}
		if (thing.hasThisAttribute(Constants.HIT_TIE)) {
			if (weaponObject!=null) {
				int val = weaponObject.getThisInt(Constants.HIT_TIE) + 1; // stacks
				weaponObject.setThisAttribute(Constants.HIT_TIE,val);
			}
		}
		return true;
	}

	public static void enchantTile(JFrame frame, CharacterWrapper character,ChangeListener listener) {
		TileLocation loc = character.getCurrentLocation();
		if (loc!=null && loc.tile!=null) {
			boolean enchanted = loc.tile.isEnchanted();
			String text = "";
			if (enchanted) {
				text = "Do you want to disenchant your current tile?";
			}
			else {
				text = "Do you want to enchant your current tile?";
			}
			int ret = JOptionPane.showConfirmDialog(frame, text, "Enchantment Potion", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION) {
				loc.tile.flip();
				if (listener!=null) {
					listener.stateChanged(new ChangeEvent(character));
				}
			}
		}
	}
	public static void enchantChit(JFrame frame, CharacterWrapper character) {
		ArrayList<MagicChit> enchantable = new ArrayList<>();
		ArrayList<CharacterActionChitComponent> enchantableChits = character.getEnchantableChits();
		Collections.sort(enchantableChits);
		enchantable.addAll(enchantableChits);
		
		RealmComponentOptionChooser compChooser = new RealmComponentOptionChooser(frame,"Enchant which?",true);
		int keyN = 0;
		for (MagicChit magicChit : enchantable) {
			RealmComponent chit = (RealmComponent)magicChit;
			String key = "k"+(keyN++);
			if (chit.isActionChit()) {
				compChooser.addOption(key,"MAGIC Chit");
			}
			else {
				compChooser.addOption(key,"Artifact/Book");
			}
			compChooser.addRealmComponentToOption(key,chit);
		}
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
		if (hostPrefs.hasPref(Constants.OPT_ENHANCED_ARTIFACTS) || character.affectedByKey(Constants.ENHANCED_ARTIFACTS)) {
			// Enchantable Artifacts and Books
			for(GameObject item:character.getActiveInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(item);
				if (rc.isMagicChit()) {
					MagicChit mc = (MagicChit)rc;
					if (mc.isEnchantable()) {
						enchantable.add(mc);
					}
				}
			}
		}
		
		if (compChooser.hasOptions()) {
			compChooser.setVisible(true);
			String text = compChooser.getSelectedText();
			if (text!=null) {
				MagicChit chit = (MagicChit)compChooser.getFirstSelectedComponent();
				if (chit!=null) {
					int enchantNumber;
					ArrayList<Integer> list = chit.getEnchantableNumbers();
					if (list.size()>1) {
						ButtonOptionDialog colorChooser = new ButtonOptionDialog(frame,chit.getIcon(),"What color?","Enchant "+chit.getGameObject().getName(),false);
						for(int mn:list) {
							ColorMagic cm = new ColorMagic(mn,false);
							colorChooser.addSelectionObject(cm.getColorName());
						}
						colorChooser.setVisible(true);
						String colorName = (String)colorChooser.getSelectedObject();
						enchantNumber = ColorMagic.makeColorMagic(colorName,false).getColorNumber();
					}
				else {
					enchantNumber = list.get(0);
				}
					
				chit.enchant(enchantNumber);
				
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Enchant;
				params.actionName = "chit";
				character.testQuestRequirements(frame,params);
				}
			}
		}
	}
	
	public static boolean doDeactivate(JFrame frame,CharacterWrapper character,GameObject thing) {
		return doDeactivate(frame, character, thing, false);
	}
	public static boolean doDeactivate(JFrame frame,CharacterWrapper character,GameObject thing, boolean forceDeactivation) {
		if (!thing.hasThisAttribute(Constants.ACTIVATED)) {
			return true; // already deactivated, so automatically successful.
		}
		if (!forceDeactivation) {
			if (thing.hasThisAttribute(Constants.CONTROLLED_HORSE)) {
				if (frame!=null) {
					JOptionPane.showMessageDialog(frame,"You cannot deactivate the steed.");
				}
				return false;
			}
			// Potions cannot be deactivated - they expire at midnight
			if (thing.hasThisAttribute(Constants.POTION)) {
				if (frame!=null) {
					JOptionPane.showMessageDialog(frame,"Potions cannot be deactivated.  They expire at midnight.");
				}
				return false;
			}
			if (thing.hasThisAttribute(Constants.CURSED)) {
				if (frame!=null) {
					JOptionPane.showMessageDialog(frame,"The "+thing.getName()+" is CURSED, and can only be deactivated (and hence destroyed) at the Chapel at midnight.");
				}
				return false;
			}
			if (thing.hasThisAttribute("phase_chit")) {
				if (frame!=null) {
					JOptionPane.showMessageDialog(frame,"Phase chits cannot be deactivated.  They expire at the end of the phase.");
				}
				return false;
			}
			if (thing.hasThisAttribute("weapon")) {
				WeaponChitComponent weapon = (WeaponChitComponent)RealmComponent.getRealmComponent(thing);
				if (weapon.isAlerted()) {
					if (frame!=null) {
						int ret = JOptionPane.showConfirmDialog(frame,"You are about to deactivate an alerted weapon.  Are you sure?","",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
						if (ret==JOptionPane.NO_OPTION) {
							return false;
						}
					}
					weapon.setAlerted(false);
				}
			}
		}
		else if(thing.hasThisAttribute("weapon")) {
			WeaponChitComponent weapon = (WeaponChitComponent)RealmComponent.getRealmComponent(thing);
			weapon.setAlerted(false);
		}
		
		if (thing.hasThisAttribute(Constants.SUMMON_COMPANION) && !thing.hasThisAttribute(Constants.POTION)) {
			String id = thing.getThisAttribute(Constants.SUMMON_COMPANION_ID);
			if (id!=null) {
				GameData data = thing.getGameData();
				GameObject companion = data.getGameObject(Long.valueOf(id));
				if (companion != null) {
					CombatWrapper combat = new CombatWrapper(companion);
					RealmComponent companionRc = RealmComponent.getRealmComponent(companion);
					if (forceDeactivation || (combat.getAttackerCount()==0 && !companionRc.hasTarget())) {
						RealmComponent owner = companionRc.getOwner();
						if (owner!=null) {
							(new CharacterWrapper(owner.getGameObject())).removeHireling(companion);
						}
						combat.targetsRemoveAttackers();
						CombatWrapper.clearAllCombatInfo(companion);
						companion.detach();
						data.removeObject(companion);
						thing.removeThisAttribute(Constants.SUMMON_COMPANION_ID);
					}
					else {
						return false;
					}
				}
			}
		}
		if (thing.hasThisAttribute(Constants.COMPANION_FROM_HOLD_RETURNS)) {
			ArrayList<String> ids = thing.getThisAttributeList(Constants.COMPANION_FROM_HOLD_RETURNS);
			if (ids!=null && !ids.isEmpty()) {
				GameData data = thing.getGameData();
				boolean success = true;
				for (String id : ids) {
					GameObject companion = data.getGameObject(Long.valueOf(id));
					if (companion != null) {
						CombatWrapper combat = new CombatWrapper(companion);
						RealmComponent companionRc = RealmComponent.getRealmComponent(companion);
						if (!forceDeactivation && (combat.getAttackerCount()!=0 || companionRc.hasTarget())) {
							success = false;
							continue;
						}
						
						RealmComponent owner = companionRc.getOwner();
						if (owner!=null) {
							(new CharacterWrapper(owner.getGameObject())).removeHireling(companion);
						}
						combat.targetsRemoveAttackers();
						CombatWrapper.clearAllCombatInfo(companion);
						thing.add(companion);
					}
				}
				if (success == false) {
					return false;
				}
			}
			thing.setThisAttributeList(Constants.COMPANION_FROM_HOLD_RETURNS,new ArrayList<String>());
		}
		if (thing.hasThisAttribute(Constants.ADD_CHIT)) {
			GamePool pool = new GamePool(character.getGameObject().getHold());
			thing.addAll(pool.find(Constants.ADD_CHIT+"="+thing.getName()));
		}
		if (thing.hasThisAttribute(Constants.CONVERT_CHITS)) {
			doRestoreConvertedChits(character,thing);
		}
		if (thing.hasThisAttribute(Constants.COLOR_CAPTURE)) {
			thing.removeThisAttribute(Constants.ENCHANTED_COLOR);
		}
		if (thing.hasThisAttribute(Constants.SPECIAL_ACTION)) {
			character.setNeedsActionPanelUpdate(true);
		}
		thing.removeThisAttribute(Constants.ACTIVATED);
		if (character!=null) {
			character.updateChitEffects();
		}
		
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionType = CharacterActionType.DeactivatingItem;
		qp.objectList = new ArrayList<>();
		qp.objectList.add(thing);
		character.testQuestRequirements(frame,qp);
		
		return true;
	}

	public static void doDrop(CharacterWrapper character,GameObject thing,ChangeListener listener,boolean plainSight) {
		if (thing!=null) {
			if (thing.hasThisAttribute(Constants.TREASURE_NEW)) {
				thing.removeThisAttribute(Constants.TREASURE_NEW);
			}
			
			if (!plainSight) {
				RealmComponent rc = RealmComponent.getRealmComponent(thing);
				if (rc.isTreasure() && !rc.getGameObject().hasThisAttribute("color_source")) {
					// abandoned treasures (no color source) should be face down
					TreasureCardComponent tc = (TreasureCardComponent)rc;
					tc.setFaceDown();
				}
			}
			
			TileLocation tl = character.getCurrentLocation();
            if(tl.isInClearing() && tl.clearing.isEdge())
            {
                PathDetail path = tl.clearing.getConnectedMapEdges().get(0);
                tl = new TileLocation(path.getEdgeClearing());
            }
            
			if (tl.hasClearing()) {
				tl.clearing.add(thing,plainSight?character:null);
			}
			else {
				// If character is flying, item lands in a random clearing
				while(!tl.hasClearing()) { // keep rolling until a valid clearing is found
					int r = RandomNumber.getDieRoll();
					tl.clearing = tl.tile.getClearing(r);
				}
				tl.clearing.add(thing,null); // Things dropped when flying are always lost
			}
			if (listener!=null) {
				listener.stateChanged(new ChangeEvent(character));
			}
		}
	}
	
	public static int getBasePrice(RealmComponent trader,RealmComponent merchandise) {
		int basePrice = merchandise.getGameObject().getThisInt("base_price");
		if (merchandise.isArmor()) {
			// basePrice calculated differently for armor
			ArmorChitComponent armor = (ArmorChitComponent)merchandise;
			/*
			 * Actually, I don't need to handle destroyed armor here - the rules say that you recover the destroyed
			 * cost automatically as gold at the time of the loss.  You will only ever sell damaged or intact armor.
			 */
			String blockName = armor.isDamaged()?"damaged":"intact";
			basePrice = merchandise.getGameObject().getAttributeInt(blockName,"base_price");
		}
		else if (merchandise.isSpell()) {
			basePrice = 10;
		}
		else if (trader!=null) {
			// Check to see if trader has a special price
			String visitor = trader.getGameObject().getThisAttribute(Constants.VISITOR);
			if (visitor!=null) {
				String priceKey = visitor.toLowerCase()+"_price";
				int specialPrice = merchandise.getGameObject().getThisInt(priceKey);
				if (specialPrice!=0) {
					basePrice = specialPrice;
				}
			}
		}
		
		if (trader!=null) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(trader.getGameObject().getGameData());
			if (hostPrefs.hasPref(Constants.OPT_COMMERCE)) {
				basePrice += TreasureUtility.getCommerceBonusPrice(trader,merchandise);
			}
		}
		
		return basePrice;
	}

	public static int getCommerceBonusPrice(RealmComponent trader,RealmComponent merchandise) {
		int commerceBonus = 0;
		
		int fame = merchandise.getGameObject().getThisInt("fame");
		if (merchandise.getGameObject().hasThisAttribute("native")) {
			// In this case, the fame is native-related, so you only get it when dealing with a particular native group
			fame = TreasureUtility.getFamePrice(merchandise.getGameObject(),trader.getGameObject());
		}
		
		// The first bonus is based on the group
		if (trader.isGuild()) {
			commerceBonus += fame;
		}
		else if (!trader.isVisitor()) {
			int notoriety = merchandise.getGameObject().getThisInt("notoriety");
			String val = trader.getGameObject().getThisAttribute("commerce");
			if ("F".equals(val)) {
				commerceBonus += fame;
			}
			else if ("N".equals(val)) {
				commerceBonus += notoriety;
			}
			else { // both F & N
				commerceBonus += fame;
				commerceBonus += notoriety;
			}
		}
		
		return commerceBonus;
	}

	/**
	 * @param doApply			true to apply, false to remove
	 */
	public static void applyChitEffects(Collection<CharacterActionChitComponent> allChits,GameObject thing) {
		if (thing.hasThisAttribute(Constants.CHIT_STRENGTH)) {
			String val = thing.getThisAttribute(Constants.CHIT_STRENGTH);
			ArrayList<String> changes = new ArrayList<>(StringUtilities.stringToCollection(val,","));
			if (changes.size()==3) { // there MUST be three to indicate 0,1,2 asterisks
				for (CharacterActionChitComponent chit : allChits) {
					if (!chit.isMagic() && !chit.isTreasureChit()) {
						int effort = chit.getEffortAsterisks();
						if (effort<changes.size()) { 
							Strength strength = new Strength(changes.get(effort));
							chit.setAlternateStrength(strength);
						}
					}
				}
			}
			else {
				throw new IllegalStateException(thing.getName()+" has CHIT_STRENGTH with something other than 3 entries!");
			}
		}
		if (thing.hasThisAttribute(Constants.CHIT_SPEED)) {
			String val = thing.getThisAttribute(Constants.CHIT_SPEED);
			ArrayList<?> changes = new ArrayList<>(StringUtilities.stringToCollection(val,","));
			if (changes.size()==3) { // there MUST be three to indicate 0,1,2 asterisks
				for (CharacterActionChitComponent chit : allChits) {
					if (!chit.isMoveFight() && !chit.isTreasureChit()) { // MOVE/FIGHT and treasure-based chits are not affected
						int effort = chit.getEffortAsterisks();
						if (effort<changes.size()) {
							Speed speed = new Speed((String)changes.get(effort));
							chit.setAlternateSpeed(speed);
						}
					}
				}
			}
			else {
				throw new IllegalStateException(thing.getName()+" has CHIT_SPEED with something other than 3 entries!");
			}
		}
		if (thing.hasThisAttribute(Constants.CHIT_SPEED_INC)) {
			int increase = thing.getThisInt(Constants.CHIT_SPEED_INC);
			for (CharacterActionChitComponent chit : allChits) {
				if (!chit.isTreasureChit()) { // treasure-based chits are not affected
					Speed speed = chit.getSpeed();
					chit.setAlternateSpeed(new Speed(speed.getNum()-increase));
				}
			}
		}
	}

	/**
	 * @return		Return the fame price for the given item with the specified trader.  This is the amount that would be
	 * 				added to the character's fame when sold to the trader, and the amount subtracted from the character's fame
	 * 				when purchased from the trader.
	 */
	public static int getFamePrice(GameObject merchandise,GameObject trader) {
		// Some treasures have special value to native groups...
		int fame = 0;
		String special = merchandise.getThisAttribute("native");
		if (special!=null) {
			// Make sure the board matches (when playing multiple boards)
			String merchNum = merchandise.getThisAttribute(Constants.BOARD_NUMBER);
			String traderNum = trader.getThisAttribute(Constants.BOARD_NUMBER);
			if (merchNum==null?traderNum==null:merchNum.equals(traderNum)) {
				// Make sure the group matches
				String groupName = RealmUtility.getRelationshipGroupName(trader);
				if (groupName!=null && groupName.toLowerCase().equals(special.toLowerCase())) {
					fame = merchandise.getThisInt("fame");
				}
			}
		}
		return fame;
	}
	
	public static ArrayList<GameObject> getTreasures(GameObject treasureLocation,CharacterWrapper character) {
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject obj : treasureLocation.getHold()) {
			if (character!=null && hasSeen(character,obj)) continue;
			RealmComponent rc = RealmComponent.getRealmComponent(obj);
			if (!rc.isMonster() && !rc.isSpell() && !rc.isNative()) {
				list.add(obj);
			}
		}
		return list;
	}
	private static boolean hasSeen(CharacterWrapper character,GameObject go) {
		if (go.hasThisAttribute(Constants.TREASURE_SEEN)) {
			return !go.hasThisAttribute("discovery") || character.hasTreasureLocationDiscovery(go.getName());
		}
		return false;
	}
	public static int getTreasureCount(GameObject treasureLocation,CharacterWrapper character) {
		return getTreasures(treasureLocation,character).size();
	}
	
	public static int getTreasureCardCount(GameObject treasureLocation) {
		return TreasureUtility.getTreasureCards(treasureLocation).size();
	}

	public static Collection<GameObject> getTreasureCards(GameObject treasureLocation) {
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject obj : treasureLocation.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(obj);
			if (rc.isTreasure()) {
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * @return		true on success
	 */
	public static GameObject openOneObject(JFrame frame,CharacterWrapper character,Collection<GameObject> openable,ChangeListener listener,boolean ignorePrerequisites) {
		GameObject toOpen = null;
		
		if (openable.size()==1) {
			toOpen = openable.iterator().next();
		}
		else {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Select object to open:",true);
			chooser.addGameObjects(openable,false);
			chooser.setVisible(true);
			
			String selText = chooser.getSelectedText();
			if (selText!=null) {
				toOpen = chooser.getFirstSelectedComponent().getGameObject();
			}
		}
		
		if (toOpen!=null) {
			// Since we are here, we can assume that toOpen has the NEEDS_OPEN attribute
			ActionPrerequisite openRequirements = ActionPrerequisite.getActionPrerequisite(toOpen,toOpen.getThisAttribute(Constants.NEEDS_OPEN),"open");
			if (ignorePrerequisites || openRequirements.fullfilled(frame,character,listener)) {
				// Open it
				toOpen.removeThisAttribute(Constants.NEEDS_OPEN);
				JOptionPane.showMessageDialog(frame,"You opened the "+toOpen.getName()+".","Opened "+toOpen.getName(),JOptionPane.INFORMATION_MESSAGE);
				
				Loot loot = new Loot(frame,listener);
				loot.handleSpecial(character,toOpen,false);
				
				QuestRequirementParams qr = new QuestRequirementParams();
				qr.actionType = CharacterActionType.Open;
				qr.targetOfSearch = toOpen;
				character.testQuestRequirements(frame, qr);
				
				return toOpen;
			}
			JOptionPane.showMessageDialog(frame,openRequirements.getFailReason()+".","Unable to open "+toOpen.getName(),JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static GameObject handleExpiredPotion(GameObject potion) {
		GameObject discardTarget = null;
		if (potion.hasThisAttribute(Constants.POTION)) {
			// discard item
			String discard = potion.getThisAttribute("discard"); // never null
			if (discard==null) {
				throw new IllegalStateException("discard is null for "+potion.getName());
			}
			discardTarget = potion.getGameData().getGameObjectByNameIgnoreCase(discard);
			if (discardTarget!=null) {
				discardTarget.add(potion);
			}
			else {
				// Simply detach the potion (it will go nowhere)
				potion.detach();
			}
			potion.removeThisAttribute(Constants.ACTIVATED);
			decrementKey(potion,Constants.ADD_SHARPNESS);
			decrementKey(potion,Constants.IGNORE_ARMOR);
			decrementKey(potion,Constants.HIT_TIE);
			potion.removeThisAttribute(Constants.AFFECTED_WEAPON_ID); // just in case
			potion.removeThisAttribute(Constants.AFFECTED_ARMOR_ID); // just in case
			
			if (potion.hasThisAttribute(Constants.DISENCHANT)) {
				String id = potion.getThisAttribute(Constants.DISENCHANT_POTION_AFFECTED_CHARACTER);
				if (id!=null) {
					GameObject charGo = potion.getGameData().getGameObject(Long.valueOf(id));
					if (charGo != null) {
						charGo.removeThisAttribute(Constants.DISENCHANT_POTION);
						potion.removeThisAttribute(Constants.DISENCHANT_POTION_AFFECTED_CHARACTER);
					}
					CharacterWrapper character = new CharacterWrapper(charGo);
					character.restoreCurses();
					SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(potion.getGameData());
					sm.restoreBewitchingNullifiedSpells(charGo,null);
				}
			}
			
			removeEffectOnGameObject(potion,Constants.CURDLE_OF_BONE,Constants.CURDLE_OF_BONE_AFFECTED_CHARACTER);
			removeEffectOnGameObject(potion,Constants.HOLY_WATER,Constants.HOLY_WATER_AFFECTED_CHARACTER);
			removeEffectOnGameObject(potion,Constants.MAGIC_PATH,Constants.MAGIC_PATH_AFFECTED_CHARACTER);
			removeEffectOnGameObject(potion,Constants.OINTMENT_OF_STONE,Constants.OINTMENT_OF_STONE_AFFECTED_ARMOR);
		}
		return discardTarget;
	}

	private static void decrementKey(GameObject potion,String key) {
		if (potion.hasThisAttribute(key)) { // Only decrement keys that the potion supports
			String id = potion.getThisAttribute(Constants.AFFECTED_WEAPON_ID);
			if (id!=null) { // This shouldn't ever happen, but it did once, so I'll protect against NPE here.
				GameObject go = potion.getGameData().getGameObject(Long.valueOf(id));
				if (go!=null) {
					int val = go.getThisInt(key) - 1;
					if (val==0) {
						go.removeThisAttribute(key);
					}
					else {
						go.setThisAttribute(key,val);
					}
				}
			}
		}
	}
	
	private static void removeEffectOnGameObject(GameObject potion, String effect, String itemId) {
		if (potion.hasThisAttribute(effect)) {
			String id = potion.getThisAttribute(itemId);
			if (id!=null) {
				GameObject charGo = potion.getGameData().getGameObject(Long.valueOf(id));
				if (charGo != null) {
					charGo.removeThisAttribute(effect);
					potion.removeThisAttribute(itemId);
				}
			}
		}
	}

	public static void handleDestroyedItem(CharacterWrapper character,GameObject thing) {
		if (thing.hasThisAttribute(Constants.CURSED)) {
			// Restore item
			character.getGameObject().add(thing);
			thing.setThisAttribute(Constants.DESTROYED);
			thing.removeThisAttribute("vulnerability");
			return;
		}
		GameData gameData = thing.getGameData();
		RealmComponent trc = RealmComponent.getRealmComponent(thing);
		if (trc.isArmor()) {
			// Make sure armor is no longer damaged, in case it regenerates at a dwelling
			ArmorChitComponent armor = (ArmorChitComponent)trc;
			armor.setActivated(false);
			armor.setIntact(true);
		}
		
		// Destroyed armor chits (not cards or magic armor) should relocate to a dwelling
		String returnDwelling = thing.getThisAttribute(Constants.ARMOR_RETURN_DWELLING);
		if (returnDwelling!=null) {
			RealmObjectMaster rom = RealmObjectMaster.getRealmObjectMaster(gameData);
			GamePool pool = new GamePool(rom.getDwellingObjects());
			returnDwelling = RealmUtility.updateNameToBoard(thing,returnDwelling);
			GameObject dwelling = pool.findFirst("name="+returnDwelling);
			
			if (dwelling==null) { // this can happen when someone loads an older game, I think
				RealmLogging.logMessage(
					character.getGameObject().getName(),
					thing.getName()+" has an invalid return dwelling \"" + returnDwelling +
							"\", so it will regenerate at the Inn.");
				dwelling = pool.findFirst("name=Inn");
				returnDwelling = "Inn";
			}
			
			thing.removeThisAttribute(Constants.DEAD);
			dwelling.add(thing);
			RealmLogging.logMessage(
					character.getGameObject().getName(),
					"Destroyed "+thing.getName()+" regenerates at the "+dwelling.getName()+".");
		}
	}
	public static ArmorType getArmorType(GameObject thing) {
		if (thing.hasThisAttribute("armor")) { //  && !thing.hasThisAttribute(Constants.DESTROYED) // Even destroyed ghost armor should prevent new armor!!
			if (thing.getThisAttribute("armor").length()==0) {
				if (thing.getThisInt("armor_row")==1) {
					return ArmorType.Shield;
				}
				else if (thing.getThisInt("armor_row")==2 && thing.hasThisAttribute("armor_smash")) {
					return ArmorType.Helmet;
				}
				else if (thing.getThisInt("armor_row")==2 && !thing.hasThisAttribute("armor_smash")) {
					return ArmorType.Breastplate;
				}
				else {
					return ArmorType.Armor;
				}
			}
			return ArmorType.Special;
		}
		return ArmorType.None;
	}
	public static void removeCursedItem(CharacterWrapper character,GameObject thing) {
		thing.removeThisAttribute(Constants.CURSED);
		TreasureUtility.doDeactivate(null,character,thing);
		thing.setThisAttribute(Constants.NO_ACTIVATE);
		thing.setThisAttribute("text","Broken - can't be activated");
		thing.removeThisAttribute("fame");
		thing.removeThisAttribute("notoriety");
		thing.removeThisAttribute("great");
		int basePrice = thing.getThisInt("base_price");
		if (basePrice>0) {
			thing.setThisAttribute("base_price",basePrice>>1);// half the value!
		}
	}
	
	public static GameObject getSleepObject(TileLocation current) {
		ArrayList<RealmComponent> seen = new ArrayList<>();
		if (current.isInClearing()) {
			for (RealmComponent rc:current.clearing.getClearingComponents()) {
				seen.addAll(ClearingUtility.dissolveIntoSeenStuff(rc));
			}
			for (RealmComponent rc:seen) {
				GameObject item = rc.getGameObject();
				if (item.hasThisAttribute(Constants.SLEEP)) {
					return item;
				}
			}
		}
		return null;
	}
	
	public static ArrayList<GameObject> getDamagedArmor(ArrayList<GameObject> stuff) {
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject go:stuff) {
			RealmComponent inv = RealmComponent.getRealmComponent(go);
			if (inv.isArmor()) {
				ArmorChitComponent armor = (ArmorChitComponent)inv;
				if (armor.isDamaged()) {
					list.add(armor.getGameObject());
				}
			}
		}
		return list;
	}
	
	public static int getBaseRepairPrice(ArmorChitComponent armor) {
		int price = 0;
		if (armor.isDamaged()) {
			price = armor.getGameObject().getAttributeInt("intact","base_price");
			price -= armor.getGameObject().getAttributeInt("damaged","base_price");
		}
		return price;
	}
	public static void destroyGenerator(CharacterWrapper character,GameObject generator) {
		// Mark generator destroyed
		generator.setThisAttribute(Constants.DESTROYED);
		RealmLogging.logMessage(character.getGameObject().getName(),"Destroyed the "+generator.getName()+"!");
		
		// Destroy ALL monster pods
		GamePool pool = new GamePool(generator.getGameData().getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add(Constants.GENERATED);
		query.add(Constants.GENERATOR_ID+"="+generator.getStringId());
		query.add("!"+Constants.DEAD);
		ArrayList<GameObject> list = pool.find(query);
		if (!list.isEmpty()) {
			RealmLogging.logMessage(character.getGameObject().getName(),list.size()+" generated monster"+(list.size()==1?"":"s")+" leave the realm.");
			for (GameObject go:list) {
				RealmUtility.makeDead(RealmComponent.getRealmComponent(go));
			}
		}
		
		// Reward character
		character.addFame(20);
		character.addNotoriety(20);
	}
	
	private static GameObject getCompanionFromItem(GameObject item) {
		GameObject companion = item.getGameData().createNewObject();
		companion.setName(item.getThisAttribute(Constants.SUMMON_COMPANION));
		companion.copyAttributeBlockFrom(item,Constants.COMPANION);
		companion.renameAttributeBlock(Constants.COMPANION,"this");
		companion.copyAttributeBlockFrom(item,Constants.COMPANION+"_light");
		companion.renameAttributeBlock(Constants.COMPANION+"_light","light");
		companion.copyAttributeBlockFrom(item,Constants.COMPANION+"_dark");
		companion.renameAttributeBlock(Constants.COMPANION+"_dark","dark");
		return companion;
	}
}