package com.robin.magic_realm.components.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.DieRoller;
import com.robin.general.util.HashLists;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.MagicChit;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.SpellSet;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class ActionPrerequisite {
	private GameObject source;
	private String messageText;
	
	private boolean fatigueAsterisk = false;
	private boolean fatigueT = false;
	private boolean needKey = false;
	
	private StringBuffer failReason = new StringBuffer();

	private ActionPrerequisite(GameObject source,String input,String messageText) {
		this.source = source;
		this.messageText = messageText;
		fatigueAsterisk = input.indexOf(Constants.FATIGUE_ASTERISK)>=0;
		fatigueT = input.indexOf(Constants.FATIGUE_TREMENDOUS)>=0;
		needKey = input.indexOf(Constants.KEY)>=0;
	}
	public String toString() {
		return "fa("+fatigueAsterisk+"),ft("+fatigueT+"),nk("+needKey+")";
	}
	
	public String getFailReason() {
		return failReason.toString();
	}
	private boolean hasLostKeys(CharacterWrapper character) {
		if (character.affectedByKey(Constants.PICKS_LOCKS)) {
			// this new custom ability can unlock anything
			return true;
		}
		
		// Otherwise, look for the lost keys
		GamePool pool = new GamePool();
		pool.addAll(character.getActivatedTreasureObjects());
		
		ArrayList<String> query = new ArrayList<>();
		query.add("key");
		if (source.hasThisAttribute(Constants.BOARD_NUMBER)) {
			query.add(Constants.BOARD_NUMBER+"="+source.getThisAttribute(Constants.BOARD_NUMBER));
		}
		else {
			query.add("!"+Constants.BOARD_NUMBER);
		}
		
		return (!pool.find(query).isEmpty());
	}
	public boolean canFullfill(JFrame frame,CharacterWrapper character,ChangeListener listener) {
		return fullfilled(frame,character,listener,false);
	}
	public boolean fullfilled(JFrame frame,CharacterWrapper character,ChangeListener listener) {
		return fullfilled(frame,character,listener,true);
	}
	private boolean fullfilled(JFrame frame,CharacterWrapper character,ChangeListener listener,boolean performAction) {
		boolean success = false;
		failReason = new StringBuffer();
		if (character.hasActiveInventoryThisKey(Constants.LOCKPICK)) {
			fatigueAsterisk = true;
		}
		if (needKey) {
			success = testKey(character);
		}
		if (!success && fatigueT) { // only check if no success yet
			success = testFatigueTremendous(frame,character,listener,performAction);
		}
		if (!success && fatigueAsterisk) {
			success = testFatigueAsterisk(frame,character,performAction);
		}
		return success;
	}
	private boolean testKey(CharacterWrapper character) {
		boolean success = false;
		// First, see if character has Lost Keys treasure
		if (hasLostKeys(character)) {
			success = true;
		}
		else {
			String boardNum = "";
			if (source.hasThisAttribute(Constants.BOARD_NUMBER)) {
				boardNum = " "+source.getThisAttribute(Constants.BOARD_NUMBER);
			}
			failReason.append("You don't have the Lost Keys"+boardNum+" activated");
		}
		// else Perform and Non-perform success!
		return success;
	}
	private boolean testFatigueTremendous(JFrame frame,CharacterWrapper character,ChangeListener listener,boolean performAction) {
		boolean success = false;
		// Try fatiguing T
		Strength tStrength = new Strength("T");
		Strength wishStrength = character.getWishStrength();
		if (tStrength.equalTo(character.getMoveStrength(false,true)) || tStrength.equalTo(character.getFightStrength(false,true))) {
			// Having strength from horse,boots is enough to satisfy the requirement (see rule 9.3/3b)
			success = true;
		}
		if (!success) {
			boolean optionalOpeningTreasureLocations = HostPrefWrapper.findHostPrefs(source.getGameData()).hasPref(Constants.SR_OPENING_TREASURE_LOCATIONS);
			// Instead, you need to fatigue a T chit
			ArrayList<CharacterActionChitComponent> tremendousChits = new ArrayList<>();
			Collection<CharacterActionChitComponent> active = character.getActiveChits();
			for (CharacterActionChitComponent chit : active) {
				if ("T".equals(chit.getStrength().toString()) || ("X".equals(chit.getStrength().toString()) && !chit.isFly())) {
					if (!optionalOpeningTreasureLocations || !chit.isMove() || !character.isMistLike()) {
						tremendousChits.add(chit);
					}
				}
			}
			ArrayList<RealmComponent> spells = new ArrayList<>();
			HashLists<String, SpellSet> spellSetHashlists = new HashLists<>();
			ArrayList<RealmComponent> items  = new ArrayList<>();
			if (optionalOpeningTreasureLocations && !source.hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE)) {
				for (SpellSet spellSet : character.getCastableSpellSets()) {
					if (spellSet.getSpell().hasThisAttribute(Constants.OPENS_TREASURE_LOCATION) && spellSet.canBeCast()) {
						spells.add(RealmComponent.getRealmComponent(spellSet.getSpell()));
						spellSetHashlists.put(spellSet.getSpell().getName(),spellSet);
					}
				}
				for (GameObject item : character.getInventory()) {
					RealmComponent rc = RealmComponent.getRealmComponent(item);
					if (rc.isTreasure() && item.hasThisAttribute("attack") && item.hasThisAttribute(Constants.POTION)) {
						items.add(rc);
					}
				}
			}
			//hurricaneWinds Spell and Lightning Bolt and Alchemists Mixture or Holy Handgrenade
			
			ArrayList<RealmComponent> allOptions  = new ArrayList<>();
			allOptions.addAll(tremendousChits);
			allOptions.addAll(spells);
			allOptions.addAll(items);
			boolean hasTWishStrength = wishStrength!=null && wishStrength.strongerOrEqualTo(tStrength);
			if (!allOptions.isEmpty() || hasTWishStrength) {
				if (performAction) {
					RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Select a chit to fatigue:",true);
					chooser.addRealmComponents(allOptions,false);
					if (hasTWishStrength) {
						chooser.addOption("WISH_","WISH Strength");
					}
					chooser.setVisible(true);
					String selText = chooser.getSelectedText();
					if (selText!=null) {
						if ("WISH Strength".equals(selText)) {
							success = true;
							character.clearWishStrength();
						}
						else {
							RealmComponent rc = chooser.getFirstSelectedComponent();
							if (rc.isSpell() || rc.isTreasure()) {
								int sharpness = rc.getGameObject().getThisInt("sharpness");
								DieRoller roller = null;
								if (rc.isSpell() && rc.getGameObject().hasThisAttribute("missile")) {
									SpellWrapper spellWrapper = new SpellWrapper(rc.getGameObject());
									roller = DieRollBuilder.getDieRollBuilder(frame,character,spellWrapper.getRedDieLock()).createRoller("magicmissil");
								}
								else if (rc.isTreasure() && rc.getGameObject().hasThisAttribute("missile")) {
									roller = DieRollBuilder.getDieRollBuilder(frame,character).createRoller("missile");
									TreasureUtility.doActivate(frame,character,rc.getGameObject(),listener,false);
								}
								int mod = 0;
								if (roller!=null) {
									int result = roller.getHighDieResult();
									mod = RealmUtility.revisedMissileTable(result);
									RealmLogging.logMessage(character.getName(),"Missile table result (using revised): "+result+" = "+RealmUtility.getLevelChangeString(mod));
								}
								String strengthString = rc.getGameObject().hasThisAttribute(Constants.STRENGTH)?rc.getGameObject().getThisAttribute(Constants.STRENGTH):rc.getGameObject().getThisAttribute(Constants.OPENS_TREASURE_LOCATION);
								Strength strength = new Strength(strengthString,mod+sharpness);
								RealmLogging.logMessage(character.getName(),"Final strength for opening "+source.getNameWithNumber()+" with "+rc.getGameObject().getNameWithNumber()+": "+strength.fullString());
								
								if (rc.isSpell()) {
									SpellWrapper spellWrapper = new SpellWrapper(rc.getGameObject());
									ArrayList<SpellSet> list = spellSetHashlists.getList(spellWrapper.getGameObject().getName());
									RealmComponentOptionChooser spellChooser = new RealmComponentOptionChooser(frame,"Choose Casting Options for "+spellWrapper.getName()+":",true);
									// Then choose a set
									Hashtable<String, SpellSet> setHash = new Hashtable<>();
									int keyN = 0;
									for (SpellSet set : list) { // by definition, the set is castable
										for (GameObject type : set.getValidTypeObjects()) {
											if (set.getInfiniteSource()!=null) {
												String key = "P"+(keyN++);
												spellChooser.addOption(key,"");
												spellChooser.addRealmComponentToOption(key,RealmComponent.getRealmComponent(type));
												setHash.put(key, set);
											}
											if (set.getInfiniteSource()==null || set.getColorMagic()==null) {
												for (MagicChit chit:set.getValidColorChits()) {
													String key = "P"+(keyN++);
													spellChooser.addOption(key,"");
													spellChooser.addRealmComponentToOption(key,RealmComponent.getRealmComponent(type));
													spellChooser.addRealmComponentToOption(key,(RealmComponent)chit);
													setHash.put(key, set);
												}
											}
										}
									}
									spellChooser.setVisible(true);
									if (spellChooser.getSelectedText()!=null) {
										String key = chooser.getSelectedOptionKey();
										SpellSet set = setHash.get(key);
										Collection<RealmComponent> c = spellChooser.getSelectedComponents();
										Iterator<RealmComponent> i=c.iterator();
										RealmComponent incantationComponent = i.next();
										if (!incantationComponent.isActionChit()) {
											String dayKey = character.getCurrentDayKey();
											String usedSpell = incantationComponent.getGameObject().getThisAttribute(Constants.USED_SPELL);
											if (usedSpell!=null && !usedSpell.equals(dayKey)) {
												incantationComponent.getGameObject().removeThisAttribute(Constants.USED_MAGIC_TYPE_LIST);
											}
											incantationComponent.getGameObject().setThisAttribute(Constants.USED_SPELL,dayKey);
											incantationComponent.getGameObject().addThisAttributeListItem(Constants.USED_MAGIC_TYPE_LIST,set.getCastMagicType());
										}
										if (incantationComponent.isActionChit()) {
											CharacterActionChitComponent chit = (CharacterActionChitComponent)incantationComponent;
											chit.makeFatigued();
											RealmUtility.reportChitFatigue(character,chit,"Fatigued chit: ");
										}										
										if (i.hasNext()) {
											MagicChit colorChit = (MagicChit)i.next();
											colorChit.makeFatigued();
											RealmUtility.reportChitFatigue(character,colorChit,"Fatigued color chit: ");
										}
									}
								}
								
								if (strength.strongerOrEqualTo(new Strength("T"))) {
									success = true;
								}				
								else {
									if (failReason.length()>0) {
										failReason.append(" and");
									}
									failReason.append(" your attack wasn't strong enough (Strength: "+strength.toString()+")");
								}
								if (rc.getGameObject().hasThisAttribute(Constants.POTION)) {
									character.expirePotion(rc.getGameObject());
								}
								listener.stateChanged(new ChangeEvent(this));
							}
							else {
								GameObject toFatigue = chooser.getFirstSelectedComponent().getGameObject();
								if (toFatigue!=null) {
									CharacterActionChitComponent chit = (CharacterActionChitComponent)RealmComponent.getRealmComponent(toFatigue);
									chit.makeFatigued();
									RealmUtility.reportChitFatigue(character,chit,"Fatigued chit: ");
									if (chit.isFight()) {
										character.clearWishStrength(); // in case that was used
									}
									listener.stateChanged(new ChangeEvent(this));
									success = true;
								}
							}
						}
					}
					else {
						if (failReason.length()>0) {
							failReason.append(" and");
						}
						failReason.append(" you didn't fatigue any T chits");
					}
				}
				else {
					success = true;
				}
			}
			else {
				// no T chits available
				if (failReason.length()>0) {
					failReason.append(" and");
				}
				failReason.append(" you don't have any active Tremendous items, chits, or hirelings to use");
			}
		}
		return success;
	}
	private boolean testFatigueAsterisk(JFrame frame,CharacterWrapper character,boolean performAction) {
		boolean success = false;
		if (character.isHiredLeader()
				|| character.isControlledMonster()
				|| character.getTransmorph()!=null
				|| character.hasActiveInventoryThisKey(Constants.NO_FATIGUE)) {
			// Leaders, monsters, and transmorphed ALWAYS fatigue for free
			// If you have the NO_FATIGUE option active, that's good too!
			success = true;
		}
		else {
			if (performAction) {
				if (selectAndFatigueChit(frame,character)) {
					success = true;
				}
				else {
					if (failReason.length()>0) {
						failReason.append(" and");
					}
					failReason.append(" you didn't fatigue any effort chits.");
				}
			}
			else {
				if (character.getActiveEffortChits().isEmpty()) {
					if (failReason.length()>0) {
						failReason.append(" and");
					}
					failReason.append(" you don't have any active effort asterisks to fatigue");
				}
				else {
					success = true;
				}
			}
		}
		return success;
	}
	private boolean selectAndFatigueChit(JFrame frame,CharacterWrapper character) {
		Collection<CharacterActionChitComponent> active = character.getActiveEffortChits();
		if (active.isEmpty()) {
			JOptionPane.showMessageDialog(frame,"You don't have any active chits to fatigue!","No Chits to Fatigue",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"You must fatigue a chit to "+messageText+" this site:",false);
		int keyN = 0;
		for (CharacterActionChitComponent chit : active) {
			String key = "N"+(keyN++);
			chooser.addOption(key,"Fatigue");
			chooser.addRealmComponentToOption(key,chit);
		}
		chooser.setVisible(true);
		String text = chooser.getSelectedText();
		if (text!=null) {
			CharacterActionChitComponent chit = (CharacterActionChitComponent)chooser.getFirstSelectedComponent();
			
			if (chit.getEffortAsterisks()==2) {
				// Need to make change
				Collection<CharacterActionChitComponent> fatigued = character.getFatiguedChits(); // In case you need to make change
				ArrayList<CharacterActionChitComponent> singleAsteriskFatiguedChits = new ArrayList<>();
				for (CharacterActionChitComponent fatiguedChit : fatigued) {
					if (fatiguedChit.getEffortAsterisks()==1) {
						singleAsteriskFatiguedChits.add(fatiguedChit);
					}
				}
				if (singleAsteriskFatiguedChits.isEmpty()) {
					int ret = JOptionPane.showConfirmDialog(frame,"There are no single asterisk chits to make change.  Fatigue anyway?","Fatiguing Two Asterisk Chit Warning",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
					if (ret==JOptionPane.NO_OPTION) {
						return false;
					}
				}
				else {
					chooser = new RealmComponentOptionChooser(frame,"Select a fatigued chit for which to make change:",true);
					chooser.addRealmComponents(singleAsteriskFatiguedChits,false);
					chooser.setVisible(true);
					if (chooser.getSelectedText()!=null) {
						CharacterActionChitComponent fatiguedChit = (CharacterActionChitComponent)chooser.getFirstSelectedComponent();
						fatiguedChit.makeActive();
					}
					else {
						// Cancelled
						return false;
					}
				}
			}
			
			chit.makeFatigued();
			RealmUtility.reportChitFatigue(character,chit,"Fatigued chit: ");
			return true;
		}
		return false;
	}
	public static ActionPrerequisite getActionPrerequisite(GameObject source,String input,String messageText) {
		if (input != null) {
			return new ActionPrerequisite(source,input,messageText);
		}
		return null;
	}
}