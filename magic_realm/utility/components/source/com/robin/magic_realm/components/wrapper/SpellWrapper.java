package com.robin.magic_realm.components.wrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.robin.game.objects.*;
import com.robin.game.server.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.effect.ISpellEffect;
import com.robin.magic_realm.components.effect.NullifyEffect;
import com.robin.magic_realm.components.effect.SpellEffectContext;
import com.robin.magic_realm.components.effect.SpellEffectFactory;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.utility.*;

/*
 * SpellWrapper will wrap GameObjects representing Instance Spells owned by a character.  A Spell can be uncast, alive/inert,
 * or alive/energized.  A spell can have any number of targets, but ALWAYS at least one.  The first target in the list (if
 * multiple) is the PRIMARY target.  All spells have an incantationObject, which is often just a MAGIC chit, but can be an
 * artifact or a book.
 */
public class SpellWrapper extends GameObjectWrapper implements BattleChit {
	public static final JFrame dummyFrame = new JFrame(); // this is so affectSpells can work on Battle emulator
	
	public static final String SPELL_BLOCK_NAME = "_s_Block";
	
	public static final String SPELL_ALIVE = "alive";		// Any spell that is alive - could be inert or nullified, but it is alive
	private static final String SPELL_INERT = "inert";		// Inert means that the spell is alive, but not functioning due to lack of color
	private static final String SPELL_AFFECTED = "affected";	// indicates the spell has affected targets
	private static final String SPELL_NULLIFIED = "nullified";	// A spell is nullified when the target is melted into mist, but will be restored when that condition ends
	public static final String NULLIFIED_SPELLS = "nullified_spells"; // Other spells which have been nullified by this spell
	private static final String SPELL_VIRTUAL = "virtual";		// A virtual spell is an instance of a real spell when cast using Enhanced Magic rules (i.e., the spell isn't tied up)
	public static final String INCANTATION_TIE = "incantation_tie";
	public static final String NO_INCANTATION_TIE = "no_incantation_tie";
	public static final String COLOR_CHIT = "color_chit";
	public static final String CASTER_ID = "caster_id";
	
	private static final String TARGET_IDS = "target_ids";
	private static final String TARGET_EXTRA_IDENTIFIER = "target_ex_id";
	public static final String SECONDARY_TARGET = "secondary_target";
	private static final String RED_DIE_LOCK = "red_die_lock";

	private static final String ALWAYS_ACTIVE = "always_active";
	public static final String SPELL_EXPIRES_AT_ROUND_END = "expires_at_round_end";
	
	public SpellWrapper(GameObject go) {
		super(go);
	}
	public SpellWrapper makeCopy() {
		// Clone the spell
		GameObject copy = getGameObject().getGameData().createNewObject(getGameObject());
		
		// Mark the spell as instance
		copy.setThisAttribute(Constants.SPELL_INSTANCE); // prevents it from appearing in spell chooser for other characters
		
		return new SpellWrapper(copy);
	}
	public String toString() {
		return getGameObject().getName();
	}
	public String getBlockName() {
		return SPELL_BLOCK_NAME;
	}
	public SpellWrapper getSpell() {
		return this;
	}
	public ColorMagic getRequiredColorMagic() {
		return ColorMagic.makeColorMagic(getGameObject().getThisAttribute("magic_color"),true);
	}
	public boolean canConflict() {
		return getGameObject().hasThisAttribute("spell_strength");
	}
	public int getConflictStrength() {
		return getGameObject().getThisInt("spell_strength");
	}
	public boolean isTransform() {
		return getName().toLowerCase().matches("transform");
	}
	public boolean isStoneGaze() {
		return getName().toLowerCase().matches("stone gaze");
	}
	public boolean isAbsorbEssence() {
		return getName().toLowerCase().matches("absorb essence");
	}
	public boolean isBenevolent() {
		return getGameObject().hasThisAttribute(Constants.BENEVOLENT);
	}
	public boolean isBenevolentForHirelings() {
		return getGameObject().hasThisAttribute(Constants.BENEVOLENT_HIRED);
	}
	public boolean isDenizenSpell() {
		return getGameObject().hasThisAttribute(Constants.SPELL_DENIZEN);
	}
	public boolean hasAffectedTargets() {
		return getBoolean(SPELL_AFFECTED);
	}
	/**
	 * Based on the location of its target, this method will return the current locations of the spell.  This will only
	 * matter to permanent spells, as all others expire before energizing is required.
	 */
	public TileLocation getCurrentLocation() {
		// The first target should be sufficient
		RealmComponent rc = getFirstTarget();
		if (rc!=null) {
			if (!rc.isTreasure() || rc.getGameObject().hasThisAttribute(Constants.TREASURE_SEEN)) {
				return ClearingUtility.getTileLocation(rc);
			}
		}
		return null;
	}
	public boolean targetsCharacterOrDenizen() {
		if(targetsClearing()) return false;
		return getTargets().stream().anyMatch(t -> t.isCharacter() || t.isMonster() || t.isNative());
	}
	/**
	 * This method is here to help differentiate spells that target individuals versus those that
	 * target a clearing
	 */
	public boolean targetsClearing() {
		String att = getGameObject().getThisAttribute("target");
		return "clearing".equals(att);
	}
	/**
	 * Causes the spell to become alive.  Assumes the proper color was provided.
	 */
	public SpellWrapper castSpell(GameObject incantationObject) {
		return castSpell(incantationObject,false);
	}
	public SpellWrapper castSpellNoEnhancedMagic(GameObject incantationObject) {
		return castSpell(incantationObject,true);
	}
	public void addColorChit(MagicChit chit) {
		setString(COLOR_CHIT,chit.getGameObject().getStringId());
	}
	/**
	 * Finds the spellcaster for a spell
	 */
	private CharacterWrapper findSpellCasterToCastSpell() {
		ArrayList<String> list = new ArrayList<>();
		GameObject caster = getGameObject().getHeldBy();
		GameObject lastNonNull = caster; 
		while (caster!=null && !caster.hasThisAttribute("character") && !Constants.STORE_SPELLCAST.equals(caster.getThisAttribute(Constants.STORE))) {
			list.add(caster.toString());
			if (list.size()>20) { // That should be big enough to indicate an infinite loop
				System.err.println("Hit an infinite loop condition in findSpellCasterToCastSpell:");
				for (String val:list) {
					System.err.println("    "+val);
				}
				return null;
			}
			lastNonNull = caster;
			caster = caster.getHeldBy();
		}
		GameObject go = caster==null?lastNonNull:caster;
		return go==null?null:new CharacterWrapper(go);
	}
	private SpellWrapper castSpell(GameObject incantationObject,boolean ignoreEnhancedMagic) {
		if (!isVirtual() && isPersistentSpell() && isUsingEnhancedMagic() && !ignoreEnhancedMagic) {
			GameObject virtualSpellGo = getGameObject().copy();
			getGameObject().add(virtualSpellGo);
			SpellWrapper virtualSpell = new SpellWrapper(virtualSpellGo);
			virtualSpell.makeVirtual();
			return virtualSpell.castSpell(incantationObject);
		}
		
		setBoolean(SPELL_AFFECTED,false); // make sure this is cleared out
		setBoolean(SPELL_ALIVE,true);
		if (!getGameObject().hasThisAttribute(NO_INCANTATION_TIE)) {
			setString(INCANTATION_TIE,incantationObject.getStringId());
			incantationObject.addThisAttributeListItem(INCANTATION_TIE,getCastMagicType());
		}
		
		RealmComponent rc = RealmComponent.getRealmComponent(incantationObject);
		if (rc.isActionChit()) {
			// MAGIC chits get "tied-up" with the spell
			getGameObject().add(incantationObject);
		}
		
		// Add the spell to the spell master (only affects day, combat, and permanent spells)
		SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
		sm.addSpell(this);
		
		CharacterWrapper caster = findSpellCasterToCastSpell();
		if (caster!=null) {
			if (caster.getGameObject().hasThisAttribute("character")) {
				// Only log this, if a character is actually casting the spell...
				RealmLogging.logMessage(caster.getGameObject().getName(),"Casts "+getGameObject().getName());
			}
			setString(CASTER_ID, String.valueOf(caster.getGameObject().getId()));
		}

		return this;
	}
	public boolean selectTargetForDenizen(HostPrefWrapper hostPrefs, TileLocation battleLocation, BattleChit denizen, RealmComponent target) {
		if (denizen.getGameObject().hasThisAttribute(Constants.SPELL_TARGETS_SELF)|| ((denizen.isNative() || denizen.isMonster()) && ((ChitComponent)denizen).hasFaceAttribute(Constants.SPELL_TARGETS_SELF))) {
			addTarget(hostPrefs, denizen.getGameObject());
			return true;
		}
		if (this.getGameObject().getThisAttribute("target").matches("clearing")) {
			addTarget(hostPrefs, battleLocation.tile.getGameObject(),true);
			setExtraIdentifier(String.valueOf(battleLocation.clearing.getNum()));
			return true;
		}
		if (target!=null) {
			addTarget(hostPrefs, target.getGameObject());
			return true;
		}
		return false;
	}
	public SpellWrapper castSpellByDenizen(GameObject denizen) {
		setBoolean(SPELL_AFFECTED,false); // make sure this is cleared out
		setBoolean(SPELL_ALIVE,true);
		setString(CASTER_ID, String.valueOf(denizen.getId()));
		(new CombatWrapper(denizen)).setCastSpell(getGameObject());
		return this;
	}
	public SpellWrapper recognizeCastedSpellByDenizen() {
		SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
		sm.addSpell(this);
		return this;
	}
	public CharacterWrapper getCaster() {
		String id = getString(CASTER_ID);
		if (id!=null) {
			GameObject c = getGameObject().getGameData().getGameObject(Long.valueOf(id));
			return new CharacterWrapper(c);
		}
		return null;
	}
	
	public GameObject getIncantationObject() {
		if (isAlive()) {
			String id = getString(INCANTATION_TIE);
			if (id!=null) { // Might be null if using Enhanced Magic
				GameObject go = getGameObject().getGameData().getGameObject(Long.valueOf(id));
				return go;
			}
		}
		return null;
	}
	public GameObject getColorChitObject() {
		String id = getString(COLOR_CHIT);
		if (id!=null) { // Might be null if using other source
			GameObject go = getGameObject().getGameData().getGameObject(Long.valueOf(id));
			return go;
		}
		return null;
	}
	public ColorMagic getColorChitMagicColor() {
		GameObject chit = getColorChitObject();
		if (chit==null) return null;
		RealmComponent chitRc = RealmComponent.getRealmComponent(chit);
		if (chitRc instanceof CharacterActionChitComponent) {
			return ((CharacterActionChitComponent)chitRc).getEnchantedColorMagic();
		}
		return null;
	}
	public void makeInert() {
		setBoolean(SPELL_INERT,true);
	}
	public boolean isInert() {
		return getBoolean(SPELL_INERT);
	}
	public void makeVirtual() {
		setBoolean(SPELL_VIRTUAL,true);
	}
	public boolean isVirtual() {
		return getBoolean(SPELL_VIRTUAL);
	}
	public void energize() {
		setBoolean(SPELL_INERT,false);
	}
	public boolean canCast(String clearingCode,int clearingCount) {
		// Only "non-already-cast" spells that are finished (notready indicates the coding isn't in place yet)
		if (!getGameObject().hasThisAttribute("notready") && !isAlive()) {
			String clearingRequirement = getGameObject().getThisAttribute("clearing_req");
			if (clearingRequirement==null || clearingRequirement.equals(clearingCode)) {
				int tileRequirement = getGameObject().getThisInt("tile_req"); // whistle for monsters
				return (tileRequirement==0 || tileRequirement==clearingCount);
			}
		}
		return false;
	}
	public boolean canExpire() {
		return !getGameObject().hasThisAttribute("no_expire");				// The Flying Carpet cannot expire
	}
	
	public void breakIncantation(boolean markIncantationChitsAsUsed) {
		// Break incantation (if any)
		GameObject io = getIncantationObject();
		if (io!=null) {
			ArrayList<String> list = io.getThisAttributeList(INCANTATION_TIE);
			list.remove(getCastMagicType());
			if (list.isEmpty()) {
				io.removeThisAttribute(INCANTATION_TIE);
			}
			else {
				io.removeThisAttribute(INCANTATION_TIE);
				io.setThisAttributeList(INCANTATION_TIE,list);
			}
			setBoolean(INCANTATION_TIE,false);
			
			// Fatigue MAGIC chit (if any)
			RealmComponent rc = RealmComponent.getRealmComponent(io);
			if (rc.isActionChit()) {
				// Return chit to caster
				getCaster().getGameObject().add(io);
				
				// Mark chit as fatigued
				CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
				chit.makeFatigued();
				RealmUtility.reportChitFatigue(getCaster(),chit,"Fatigued chit: ");
				
				if (markIncantationChitsAsUsed) {
					// Mark chit as used
					CombatWrapper combat = new CombatWrapper(getCaster().getGameObject());
					combat.addUsedChit(io);
				}
			}
			
			removeAttribute(COLOR_CHIT);
		}
	}
	/**
	 * Causes this spell to expire, and everything that goes with it
	 */
	public void expireSpell() {
		clearRedDieLock();
		if (isAlive() && canExpire()) {
			// Undo any duration effects
			if (!isInert()) {
				unaffectTargets();
			}
			
			breakIncantation(true);
			
			// Restore any absorbed monsters
			TileLocation loc = getCaster().getCurrentLocation(); // might be null if character is dead!
			boolean casterIsDead = (new CombatWrapper(getCaster().getGameObject())).getKilledBy()!=null;
				
			ArrayList<GameObject> hold = new ArrayList<>();
			hold.addAll(getGameObject().getHold()); 
			for (GameObject go : hold) {
				restoreAbsorbedMonster(go, loc, casterIsDead);
			}
				
			// Remove all targets
			setBoolean(TARGET_IDS,false);
			setBoolean(TARGET_EXTRA_IDENTIFIER,false);
			setBoolean(SECONDARY_TARGET,false);
			
			// Spell dies
			//setBoolean(CASTER_ID,false); // I don't think there is any harm leaving the caster... It's needed for disengagement 5/29/2007
			setBoolean(SPELL_INERT,false);
			setBoolean(SPELL_ALIVE,false);
			setBoolean(SPELL_AFFECTED,false); // Probably redundant
			setBoolean(SPELL_EXPIRES_AT_ROUND_END,false);
			
			// Remove it from the spell master, just in case
			SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
			sm.removeSpell(this);
		}
		restoreNullifiedSpells();
	}
	
	public void restoreNullifiedSpells() {
		ArrayList<String> spellsToRestore = getList(NULLIFIED_SPELLS);
		if (spellsToRestore != null) {
			GameData data = this.getGameData();
			for (String spellId : spellsToRestore) {
				SpellWrapper spell = new SpellWrapper(data.getGameObject(Long.valueOf(spellId)));
				spell.restoreSpell();
			}
		}
		removeAttribute(NULLIFIED_SPELLS);
	}
	
	public void cancelSpell() {
		clearRedDieLock();
		if (isAlive() && canExpire()) {
			breakIncantation(true);
			
			// Restore any absorbed monsters
			TileLocation loc = getCaster().getCurrentLocation(); // might be null if character is dead!
			boolean casterIsDead = (new CombatWrapper(getCaster().getGameObject())).getKilledBy()!=null;
				
			ArrayList<GameObject> hold = new ArrayList<>();
			hold.addAll(getGameObject().getHold());
			for (GameObject go : hold) {
				restoreAbsorbedMonster(go, loc, casterIsDead);
			}
				
			// Remove all targets
			setBoolean(TARGET_IDS,false);
			setBoolean(TARGET_EXTRA_IDENTIFIER,false);
			setBoolean(SECONDARY_TARGET,false);
			
			// Spell dies
			//setBoolean(CASTER_ID,false); // I don't think there is any harm leaving the caster... It's needed for disengagement 5/29/2007
			setBoolean(SPELL_INERT,false);
			setBoolean(SPELL_ALIVE,false);
			setBoolean(SPELL_AFFECTED,false); // Probably redundant
			
			// Remove it from the spell master, just in case
			SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
			sm.removeSpell(this);
		}
	}
	
	public void clearSpellAttributes() {
		clear(SPELL_INERT);
		clear(SPELL_ALIVE);
		clear(SPELL_AFFECTED);
		clear(SPELL_NULLIFIED);
		clear(NULLIFIED_SPELLS);
		clear(SPELL_VIRTUAL);
		clear(INCANTATION_TIE);
		clear(COLOR_CHIT);
		clear(TARGET_IDS);
		clear(TARGET_EXTRA_IDENTIFIER);
		clear(SECONDARY_TARGET);
		clear(CASTER_ID);
		clear(SPELL_EXPIRES_AT_ROUND_END);
		clearRedDieLock();
	}
	
	private void restoreAbsorbedMonster(GameObject go, TileLocation loc, boolean casterIsDead){
		RealmComponent rc = RealmComponent.getRealmComponent(go);
		if(rc.isMonster() && !go.hasThisAttribute("animal") && !go.hasThisAttribute("statue")){
			if(affectsCaster() && casterIsDead){
				RealmUtility.makeDead(rc);
			} else {
				ClearingUtility.moveToLocation(go, loc);
			}
		} else{
			ClearingUtility.moveToLocation(go, null);
		}
	}
	
	public boolean isActive() {
		if(isAlwaysActive()){return true;}
		
		return isAlive() && !isInert() && !isNullified();
	}
	
	public boolean isAlwaysActive() {
		return getGameObject().hasThisAttribute(ALWAYS_ACTIVE);
	}
	public boolean expiresAtRoundEnd() {
		return getBoolean(SPELL_EXPIRES_AT_ROUND_END);
	}
	public void nullifySpell(boolean includeNullifyEffects) {
		unaffectTargets(includeNullifyEffects);
		getGameObject().setThisAttribute(SPELL_NULLIFIED);
	}
	public boolean isNullified() {
		return getGameObject().hasThisAttribute(SPELL_NULLIFIED);
	}
	public void restoreSpell() {
		if (isNullified()) {
			if (!isInert()) {
				GameWrapper game = GameWrapper.findGame(getGameObject().getGameData());
				affectTargets(null,game,false,false,null);
			}
			getGameObject().removeThisAttribute(SPELL_NULLIFIED);
		}
	}
	public void setRedDieLock(int val) {
		setInt(RED_DIE_LOCK,val);
	}
	public int getRedDieLock() {
		return getInt(RED_DIE_LOCK);
	}
	public void clearRedDieLock() {
		clear(RED_DIE_LOCK);
	}
	public void setExtraIdentifier(String val) {
		setString(TARGET_EXTRA_IDENTIFIER,val);
	}
	public String getExtraIdentifier() {
		return getString(TARGET_EXTRA_IDENTIFIER);
	}
	public void setSecondaryTarget(GameObject val) {
		setString(SECONDARY_TARGET,val.getStringId());
	}
	public GameObject getSecondaryTarget() {
		String id = getString(SECONDARY_TARGET);
		if (id==null) return null;
		GameObject go = getGameObject().getGameData().getGameObject(Long.valueOf(id));
		return go;
	}
	public boolean removeTarget(GameObject target) {
		if (isAlive() && !isInert()) {
			// If the spell is alive and non-inert, then we'd better disable it's affect on the target (if any)
			ISpellEffect[] effects = SpellEffectFactory.create(getName().toLowerCase(),getAlternativeSpellEffect());
			unaffect(effects, GameWrapper.findGame(getCaster().getGameData()), RealmComponent.getRealmComponent(target));
		}
		
		String removeId = target.getStringId();
		ArrayList<String> targetids = getList(TARGET_IDS);
		if (targetids.contains(removeId)) {
			targetids.remove(removeId);
			return true;
		}
		else if (affectsCaster() && target.equals(getCaster().getGameObject())) {
			// Even though no targets are removed, removing the caster is automatic grounds for expiring the spell!
			expireSpell();
		}
		return false;
	}
	/**
	 * Adds a target of the spell
	 */
	public void addTarget(HostPrefWrapper hostPrefs,GameObject target) {
		addTarget(hostPrefs,target,false);
	}
	/**
	 * @param ignoreBattling		If true, then ignore battling results, because the natives don't know who is targeting them! (Roof Collapses)
	 */
	public void addTarget(HostPrefWrapper hostPrefs,GameObject target,boolean ignoreBattling) {
		addListItem(TARGET_IDS,target.getStringId());
		
		// Be sure to tag the target
		CombatWrapper combat = new CombatWrapper(target);
		CharacterWrapper casterCharacterWrapper = getCaster();
		if (casterCharacterWrapper!=null && hostPrefs!=null) { // caster might be null if the spell is cast by a treasure (Flying Carpet)
			GameObject caster = casterCharacterWrapper.getGameObject();
			combat.addAttacker(caster);
			
			CharacterWrapper character = new CharacterWrapper(caster);
			RealmComponent rc = RealmComponent.getRealmComponent(target);
			if (rc.ownedBy(RealmComponent.getRealmComponent(caster))) {
				if (!hostPrefs.hasPref(Constants.TE_BENEVOLENT_SPELLS) || !isBenevolent()) {
					if ((!getGameObject().hasThisAttribute(Constants.BENEVOLENT_FOR_LEADERS) || !rc.isNativeLeader())
					&& (!getGameObject().hasThisAttribute(Constants.BENEVOLENT_FOR_MONSTERS) || !rc.isMonster())) {
						BattleUtility.processTreachery(character,rc);
					}
				}
			}
			
			// if target is an unassigned denizen, move them to their own sheet (sucker punch)
			if (rc.getOwnerId()==null && rc.getTarget()==null && rc.get2ndTarget()==null) {
				if (!hostPrefs.hasPref(Constants.TE_WATCHFUL_NATIVES)) {
					combat.setSheetOwner(true);
				}
			}
			
			if (combat.isPacified()) {
				combat.removePacified();
			}
			
			// Make sure we aren't ignoring battling...
			if (!ignoreBattling) {
				// non-battling unhired natives will begin battling the character immediately if attacked
				if (rc.isNative() && !character.isBattling(target) && !this.getGameObject().hasThisAttribute("no_battle")) {
					character.addBattlingNative(target);
				}
				if (rc.isPacifiedBy(character)) {
					// Targeting a pacified monster or native will break the spell
					SpellWrapper spell = rc.getPacificationSpell(character);
					spell.expireSpell();
				}
			}
		}
	}
	public RealmComponent getFirstTarget() {
		ArrayList<String> targetids = getList(TARGET_IDS);
		if(targetids == null)return null;
		
		Optional<String> first = targetids.stream().findFirst();		
		GameObject target = getGameObject().getGameData().getGameObject(first.get());
		return RealmComponent.getRealmComponent(target);
	}
	
	public String getTargetsName() {
		RealmComponent rc = getFirstTarget();
		if (rc!=null) {
			return rc.getGameObject().getName();
		}
		return "None";
	}
	
	public boolean noTargeting() {
		String att = getGameObject().getThisAttribute("target");
		return "none".matches(att);
	}
	
	public ArrayList<RealmComponent> getTargets() {
		ArrayList<?> targetids = getList(TARGET_IDS);
		
		return targetids != null
				? targetids.stream()
						.mapToLong(id -> Long.valueOf((String)id))
						.mapToObj(id -> getGameObject().getGameData().getGameObject(id))
						.map(go -> RealmComponent.getRealmComponent(go))
						.collect(Collectors.toCollection(ArrayList::new))
				: new ArrayList<>();
	}
	/**
	 * This returns the number of actual targets.  If a single target is listed more than once (i.e., Stones Fly), it still is only
	 * counted once.
	 */
	public int getTargetCount() {
		ArrayList<String> targetids = getList(TARGET_IDS);
		
		return targetids != null
				? (int) targetids.stream().distinct().count()
				: 0;
	}
	
	public boolean targetsGameObject(GameObject go) {
		boolean ret = false;
		ArrayList<String> targetids = getList(TARGET_IDS);
		if (targetids!=null) {
			ret = targetids.contains(go.getStringId());
		}
		if (ret==false) {
			RealmComponent at = getAffectedTarget();
			ret = at!=null && at.getGameObject().equals(go);
		}
		return ret;
	}
	/**
	 * @return		true if any one component is targeted
	 */
	public boolean targetsRealmComponents(Collection<?> components) {
		ArrayList<String> targetids = getList(TARGET_IDS);
		if(targetids == null) return false;
		
		return components.stream()
			.map(c -> (RealmComponent)c)
			.anyMatch(rc -> targetids.contains(rc.getGameObject().getStringId()));
	}
	
	public ArrayList<RealmComponent> getTargetedRealmComponents(Collection<?> components) {
		ArrayList<String> targetids = getList(TARGET_IDS);
		
		return targetids != null
				? components.stream()
						.map(c -> (RealmComponent)c)
						.filter(rc -> targetids.contains(rc.getGameObject().getStringId()))
						.collect(Collectors.toCollection(ArrayList::new))
				: new ArrayList<>();
	}
	
	/**
	 * @return		true if the spell is "alive".  Any spell that is cast is alive for the spell's duration, or until
	 * 				it is cancelled.
	 */
	public boolean isAlive() {
		return getBoolean(SPELL_ALIVE);
	}
	public boolean isPersistentSpell() { // Needed for Enhanced Magic
		return !isAttackSpell() && !isInstantSpell();
	}
	/**
	 * @return		true if this spell is the kind that does an attack during a round of combat
	 */
	public boolean isAttackSpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("attack".equals(duration));
	}
	/**
	 * @return		true if this spell is a combat spell
	 */
	public boolean isCombatSpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("combat".equals(duration));
	}
	/**
	 * @return		true if this spell is a day spell
	 */
	public boolean isDaySpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("day".equals(duration));
	}
	/**
	 * @return		true if this spell is an instant spell
	 */
	public boolean isInstantSpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("instant".equals(duration));
	}
	/**
	 * @return		true if this spell is a move spell
	 */
	public boolean isMoveSpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("move".equals(duration));
	}
	/**
	 * @return		true if this spell is a permanent spell
	 */
	public boolean isPermanentSpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("permanent".equals(duration));
	}
	/**
	 * @return		true if this spell uneffects targets at midnight
	 */
	public boolean uneffectAtMidnight() {
		return getGameObject().hasThisAttribute(Constants.UNEFFECT_AT_MIDNIGHT);
	}
	/**
	 * @return		true if this spell is a permanent spell
	 */
	public boolean isPhaseSpell() {
		String duration = getGameObject().getThisAttribute("duration");
		return ("phase".equals(duration));
	}
	public boolean hasPhaseChit() {
		return getGameObject().hasThisAttribute(Constants.PHASE_CHIT_ID);
	}
	/**
	 * @return		true if this spell is a fly chit type of spell
	 */
	public boolean isFlySpell() {
		return getGameObject().hasAttributeBlock(RealmComponent.FLY_CHIT);
	}
	/**
	 * @return	true if this is a "no cancel" spell, like the Flying Carpet spell
	 */
	public boolean isNoCancelSpell() {
		return getGameObject().hasThisAttribute("no_cancel");
	}
	
	// Battle Chit Interface
	public RealmComponent getTarget() {
		throw new RuntimeException("getTarget() is not functional in SpellWrapper!!  Use getTargets()");
	}
	public void changeWeaponState(HostPrefWrapper hostPrefs) {
		// nothing
	}
	public void flip() {
		// nothing
	}
	public void setFacing(String val) {
		// nothing
	}
	public String getName() {
		return getGameObject().getName();
	}
	public boolean isDenizen() {
		return false;
	}
	public boolean isCharacter() {
		return false;
	}
	public Integer getLength() {
		if (getGameObject().hasThisAttribute("length")) {
			int len = getGameObject().getThisInt("length");
			return Integer.valueOf(len);
		}
		return null;
	}
	public Speed getMoveSpeed() {
		return null;
	}
	public Speed getFlySpeed() {
		return null;
	}
	public boolean hasAnAttack() {
		return getAttackCombatBox()>0;
	}
	public Speed getAttackSpeed() {
		RealmComponent rc = RealmComponent.getRealmComponent(getIncantationObject());
		Speed speed = BattleUtility.getMagicSpeed(rc);
		// Check to see if speed is overridden by spell (like Roof Collapses)
		if (getGameObject().hasThisAttribute("attack_speed")) {
			speed = new Speed(getGameObject().getThisInt("attack_speed"));
		}
		return speed;
	}
	public Harm getHarm() {
		Strength strength = new Strength(getGameObject().getThisAttribute("strength"));
		int sharpness = getGameObject().getThisInt("sharpness");
		
		CharacterWrapper caster = getCaster();
		if (caster.hasActiveInventoryThisKey(Constants.ENHANCE_SPELL_SHARPNESS)) {
			sharpness++;
		}
		
		if (sharpness>0 && HostPrefWrapper.findHostPrefs(getGameObject().getGameData()).hasPref(Constants.REV_DAMPEN_FAST_SPELLS)) {
			GameObject go = getIncantationObject();
			if (go!=null) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isActionChit()) {
					CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
					if (chit.getMagicSpeed().getNum()==0) {
						sharpness--;
					}
				}
			}
		}
		return new Harm(strength,sharpness);
	}
	public String getMagicType() {
		String attackSpell = getAttackSpell();
		if (attackSpell!=null) return attackSpell;
		return getGameObject().getThisAttribute("magic_type");
	}
	public String getAttackSpell() {
		if (getGameObject().hasThisAttribute(Constants.POWER_OF_THE_PIT)) return Constants.POWER_OF_THE_PIT;
		if (getGameObject().hasThisAttribute(Constants.WALL_OF_FORCE)) return Constants.WALL_OF_FORCE;
		if (getGameObject().hasThisAttribute(Constants.FEAR)) return Constants.FEAR;
		if (getGameObject().hasThisAttribute(Constants.MESMERIZE)) return Constants.MESMERIZE;
		return null;
	}
	public String getCastMagicType() {
		return getGameObject().getThisAttribute("spell");
	}
	public int getManeuverCombatBox() {
		CombatWrapper combat = new CombatWrapper(getIncantationObject());
		return combat.getCombatBoxDefense();
	}
	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getIncantationObject());
		return combat.getCombatBoxAttack();
	}
	public void setCombatBox(int val) {
		CombatWrapper combat = new CombatWrapper(getIncantationObject());
		combat.setCombatBoxAttack(val);
		combat.setCombatBoxDefense(val);
	}
	public boolean isMissile() {
		return getGameObject().hasThisAttribute("missile");
	}
	public String getMissileType() {
		return getGameObject().getThisAttribute("missile");
	}
	public boolean hitsOnTie() {
		return false;
	}
	public boolean isMonster() {
		return false;
	}
	public boolean isHorse() {
		return false;
	}
	public boolean isNativeHorse() {
		return false;
	}
	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs, BattleChit attacker, int box, Harm attackerHarm,int attackOrderPos) {
		// Spells never take hits
		return false;
	}
	private InfoObject buildAnInfoObject(String destClientName,GameData data,String command) {
//		String destClientName = getCaster().getPlayerName();
		RealmDirectInfoHolder info = new RealmDirectInfoHolder(data);
		info.setCommand(command);
		info.addGameObject(getGameObject());
		InfoObject io = new InfoObject(destClientName,info.getInfo());
		return io;
	}
	public ArrayList<String> affectTargets(JFrame parent,GameWrapper theGame,boolean expireImmediately, ArrayList<SpellWrapper> simultaneousSpells) {
		return affectTargets(parent,theGame,expireImmediately,true,simultaneousSpells);
	}
	private ArrayList<String> affectTargets(JFrame parent,GameWrapper theGame,boolean expireImmediately, boolean includeNullifyEffects, ArrayList<SpellWrapper> simultaneousSpells) {
		if (getBoolean(SPELL_AFFECTED)) {
			// Don't affect twice in a row!!
			return null;
		}
		if (parent==null) {
			parent = dummyFrame;
		}
		if (!getGameObject().hasThisAttribute("host_okay")) {
			String destClientName = getCaster().getPlayerName();
			if (getGameObject().hasThisAttribute("target_client")) {
				
				CharacterWrapper character = new CharacterWrapper(getFirstTarget().getGameObject());
				String name = character.getPlayerName();
				if (name!=null) {
					destClientName = name;
				}
			}
			String command = expireImmediately?RealmDirectInfoHolder.SPELL_AFFECT_TARGETS_EXPIRE_IMMEDIATE:RealmDirectInfoHolder.SPELL_AFFECT_TARGETS;
			GameData data = getGameObject().getGameData();
			if (GameHost.DATA_NAME.equals(data.getDataName()) && !this.getGameObject().hasThisAttribute(Constants.SPELL_DENIZEN)) {
				// Should never "affectTargets" from the host.  Do it on the caster's client.
				if (GameHost.mostRecentHost!=null) {
					GameHost.mostRecentHost.distributeInfo(buildAnInfoObject(destClientName,data,command));
					return null;
				}
				throw new IllegalStateException("mostRecentHost is null?");
			}
			else if (GameClient.DATA_NAME.equals(data.getDataName())) {
				// With clients, make sure to affectTargets from the caster's client
				if (GameClient.GetMostRecentClient()!=null) {
					CharacterWrapper caster = getCaster();
					if (caster!=null) {
						// destClientName will be null for the spell that is active on the flying carpet!!
						if (destClientName!=null && !destClientName.equals(GameClient.GetMostRecentClient().getClientName())) {
							GameClient.GetMostRecentClient().sendInfoDirect(
									destClientName,
									buildAnInfoObject(destClientName,data,command).getInfo());
							return null;
						}
					}
				}
				else {
					throw new IllegalStateException("mostRecentClient is null?");
				}
			}
		}
		
		if (parent==null) {
			throw new IllegalStateException("Parent should NOT be null here!!");
		}
		
		AffectThread at = new AffectThread(parent,theGame,expireImmediately,includeNullifyEffects);
		if (SwingUtilities.isEventDispatchThread() || getGameObject().hasThisAttribute(Constants.SPELL_DENIZEN)) {
//System.out.println("Already EDT");
			// NON threaded
			return at.doAffect(simultaneousSpells);
		}
//System.out.println("Non EDT - invoke and wait");
			// Run on event dispatch thread!
			//SwingUtilities.invokeLater(at); // THIS is NOT the solution:  this breaks other things.
		try {
			SwingUtilities.invokeAndWait(at); // FIXME This causes a deadlock when WISH/CONTROLBATS are cast at the same time!!!
		}
		catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	private class AffectThread implements Runnable {
		private JFrame parent;
		private GameWrapper theGame;
		private boolean expireImmediately;
		private boolean includeNullifyEffects;
		
		public AffectThread(JFrame parent,GameWrapper theGame,boolean expireImmediately,boolean includeNullifyEffects) {
			this.parent = parent;
			this.theGame = theGame;
			this.expireImmediately = expireImmediately;
			this.includeNullifyEffects = includeNullifyEffects;
		}
		
		public void run() {
			doAffect(null);
		}
		
		public ArrayList<String> doAffect(ArrayList<SpellWrapper> simultaneousSpells) {
			// If we get here, then it's okay to proceed
			energize();
			
			ArrayList<String> logs = new ArrayList<>();
			ISpellEffect[] effects = SpellEffectFactory.create(getName().toLowerCase(),getAlternativeSpellEffect());
			int ignoredTargets = 0;
			
			if (!includeNullifyEffects && effects!=null) {
				ArrayList<ISpellEffect> effectsFiltered = new ArrayList<>();
				for (ISpellEffect effect : effects) {
					if (!(effect instanceof NullifyEffect)) {
						effectsFiltered.add(effect);
					}
				}
				effects = new ISpellEffect[effectsFiltered.size()];
				effects = effectsFiltered.toArray(effects);
			}
			for (RealmComponent target : getTargets()) {
				boolean affectTarget = true;
				ArrayList<SpellWrapper> bewichtedSpells = SpellUtility.getBewitchingSpells(target.getGameObject());
				if (bewichtedSpells.contains(getSpell())) {
					bewichtedSpells.remove(getSpell());
				}
				if (simultaneousSpells!=null) {
					for (SpellWrapper simultaneousSpell : simultaneousSpells) {
						if (bewichtedSpells.contains(simultaneousSpell)) {
							bewichtedSpells.remove(simultaneousSpell);
						}
					}
				}
				
				if (isCombatSpell() || isDaySpell() || isPermanentSpell() && simultaneousSpells!=null) {
					for (SpellWrapper spell : bewichtedSpells) {
						if (spell.isActive() && spell.hasAffectedTargets() && spell.getName().toLowerCase().matches(getName().toLowerCase())) {
							affectTarget = false;
							ignoredTargets = ignoredTargets + 1;
							logs.add(getName() + " effect (cast by "+getCaster().getName()+") on " + target + " canceled, as target already affected by " + getName()+".");
						}
					}
				}
				if (canConflict() && !isInstantSpell() && !isAttackSpell() && !isMoveSpell() && !isPhaseSpell()) {
					int spellStrength = getConflictStrength();
					for (SpellWrapper spell : bewichtedSpells) {
						if (spell.canConflict() && spell.hasAffectedTargets() && spell.isActive()) {
							if (spell.getConflictStrength() < spellStrength) {
								spell.nullifySpell(true);
								addListItem(NULLIFIED_SPELLS, spell.getGameObject().getStringId());
								logs.add(spell.getName() + " (cast by "+spell.getCaster().getName()+") was nullified, as stronger spell ("+getName()+", cast by "+getCaster().getName()+") hit the " + target + ".");
							}
							if (spell.getConflictStrength() == spellStrength) {
								affectTarget = false;
								ignoredTargets = ignoredTargets + 1;
								logs.add(getName() + " effect (cast by "+getCaster().getName()+") on " + target + " canceled, as target already affected by a spell of same strength: " + spell.getName()+" (cast by "+spell.getCaster().getName()+").");
							}
							if (spell.getConflictStrength() > spellStrength) {
								affectTarget = false;
								ignoredTargets = ignoredTargets + 1;
								logs.add(getName() + " effect (cast by "+getCaster().getName()+") on " + target + " canceled, as target already affected by a stronger spell: " + spell.getName()+" (cast by "+spell.getCaster().getName()+").");
							}
						}
					}
				}
				if (affectTarget) {
					affect(effects, parent, theGame, target);
				}
			}
			
			if (ignoredTargets > 0 && getTargets().size() == ignoredTargets) {
				cancelSpell();
				logs.add(getName() + " spell (cast by "+getCaster().getName()+") cancelled, as all targets already affected by " + getName()+" or a stronger spell.");
				return logs;
			}
			
			if (!(isPhaseSpell() && hasPhaseChit())) { // ignore phase spells that still have a phase chit active!!
				setBoolean(SPELL_AFFECTED,true);
			}
			if (expireImmediately) {
				expireSpell();
			}
			
			if (getCaster()!=null) { //null for if cast by treasure
				getCaster().addCastedSpell(getGameObject());
				QuestRequirementParams reqParams = new QuestRequirementParams();
				reqParams.actionType = CharacterActionType.CastSpell;
				reqParams.objectList.add(getGameObject());
				getCaster().testQuestRequirements(parent, reqParams);
			}
			
			return logs;
		}
	}
	
	private void affect(ISpellEffect[] effects, JFrame parent,GameWrapper theGame,RealmComponent target) {
		if (!isAlive()) {
			// If spell is not alive, it has NO effect
			return;
		}
		
		GameObject caster = getCaster().getGameObject();
		CombatWrapper combat = new CombatWrapper(target.getGameObject());
		SpellEffectContext context = new SpellEffectContext(parent, theGame, target, this, caster);

		if(effects != null){
			for(ISpellEffect effect:effects){
				effect.apply(context);
			}
		}

		// Once the spell affects its target, the marker chit should be removed!
		if (caster!=null) {
			combat.removeAttacker(caster);
		}
	}
	
	public ClearingDetail getTargetAsClearing(RealmComponent target) {
		TileComponent tile = (TileComponent)target;
		return tile.getClearing(Integer.parseInt(getExtraIdentifier()));
	}
	public void unaffectTargets() {
		unaffectTargets(true);
	}
	public void unaffectTargets(boolean includeNullifyEffects) {
		ISpellEffect[] effects = SpellEffectFactory.create(getName().toLowerCase(),getAlternativeSpellEffect());
		
		GameWrapper theGame = GameWrapper.findGame(getCaster().getGameData());
		if (!includeNullifyEffects) {
			ArrayList<ISpellEffect> effectsFiltered = new ArrayList<>();
			if (effects != null) {
				for (ISpellEffect effect : effects) {
					if (!(effect instanceof NullifyEffect)) {
						effectsFiltered.add(effect);
					}
				}
			}
			ISpellEffect[] effects2 = new ISpellEffect[effectsFiltered.size()];
			effects2 = effectsFiltered.toArray(effects2);
			for (RealmComponent target : getTargets()) {
				unaffect(effects2, theGame, target);
			}
		}
		else {
			getTargets().stream().forEach(t -> unaffect(effects, theGame, t));
		}
		setBoolean(SPELL_AFFECTED,false);
	}
	
	private void unaffect(ISpellEffect[] effects, GameWrapper theGame, RealmComponent target) {	
		SpellEffectContext context = new SpellEffectContext(null, theGame, target, this, getCaster().getGameObject());
		
		if(effects != null){
			for(ISpellEffect effect:effects){
				effect.unapply(context);
			}
			return;
		}
	}

	
	public boolean affectsCaster() {
		return getGameObject().hasThisAttribute(Constants.AFFECTS_CASTER);
	}
	/**
	 * All spells except for "Absorb Essence" affect the spell target.  With the Aborb Essence spell, the target is absorbed, but
	 * the caster is affected!
	 */
	public RealmComponent getAffectedTarget() {
		if (affectsCaster()) {
			return RealmComponent.getRealmComponent(getCaster().getGameObject());
		}
		return getFirstTarget(); // Not real fond of this, but it will work in all cases where it matters
	}
	public GameObject getTransformAnimalOrStatue() {
		Optional<GameObject> animal = getGameObject().getHold().stream()
										.filter(t -> t.hasThisAttribute("animal") || t.hasThisAttribute("statue"))
										.findFirst();						
		return animal.isPresent() ? animal.get() : null;
	}
	
	public boolean isImmuneTo(RealmComponent rc) {
		// This is meaningless in this context, so just return false.
		return false;
	}
	private boolean isUsingEnhancedMagic() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		CharacterWrapper caster = findSpellCasterToCastSpell();
		return hostPrefs.hasPref(Constants.OPT_ENHANCED_MAGIC) || hostPrefs.hasPref(Constants.HOUSE2_REVISED_ENHANCED_MAGIC) || (caster != null && caster.affectedByKey(Constants.ENHANCED_MAGIC));
	}
	public static void copyTransformToObject(GameObject source,String blockName,GameObject dest) {
		String animalName = source.getAttribute(blockName,"name");
		dest.setName(animalName);
		if (blockName.matches("statue")) {
			dest.setThisAttribute("statue");
		}
		else {
			dest.setThisAttribute("animal");
		}
					
		copyMonsterAttributesToObject(source,blockName,dest);
	}
	public static void copyMonsterAttributesToObject(GameObject source,String blockName,GameObject dest) {
		// Ignore these attributes
		String[] ignorVars = {"light_color","dark_color"};
		ArrayList<String> ignoreTest = new ArrayList<>(Arrays.asList(ignorVars));
		
		// Earmark some attributes for the "this" block
		String[] thisVars = {"vulnerability",Constants.ICON_FOLDER,Constants.ICON_TYPE,Constants.FLYING,Constants.WALK_WOODS,Constants.ARMORED,"name",Constants.MIST_LIKE,Constants.SMALL,"animal",
				Constants.ICON_FOLDER+Constants.ALTERNATIVE,Constants.ICON_TYPE+Constants.ALTERNATIVE,Constants.ICON_FOLDER+Constants.ICON_CHARACTER,Constants.ICON_TYPE+Constants.ICON_CHARACTER,
				Constants.ICON_SIZE,Constants.ICON_FOLDER+Constants.ALTERNATIVE,Constants.ICON_Y_OFFSET,Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE,
				Constants.DRAGON,Constants.DRAKE,Constants.WYRM,Constants.DEMON,Constants.DEVIL,Constants.IMP,Constants.ELEMENTAL,Constants.ANOMALY,Constants.GOLEM,
				Constants.ORC,Constants.GOBLIN,Constants.OGRE,Constants.TROLL,Constants.GIANT,Constants.FROST_GIANT,Constants.SPIDER,Constants.OCTOPUS,Constants.SCORPION,Constants.BAT,Constants.WOLF,Constants.BEAST,
				Constants.VAMPIRE,Constants.SUCCUBUS,Constants.GHOST,Constants.GHOUL,Constants.ZOMBIE,Constants.SKELETON,Constants.WRAITH,Constants.COLOSSUS,Constants.TITAN,Constants.MINOTAUR,Constants.GARGOYLE,Constants.VIPER,Constants.SERPENT,
				Constants.UNDEAD,Constants.UNDEAD_SUMMONED,
				Constants.TRANSMORPH_IMMUNITY,Constants.TRANSMORPH_IMMUNITY_SELF,Constants.MAGIC_IMMUNITY,
				Constants.NO_CHANGE_TACTICS,Constants.KILLS_HORSE,Constants.DESTROYS_ARMOR,Constants.CHANGE_TACTICS_AFTER_CASTING,Constants.ATTACK_AFTER_CASTING,Constants.SPELL_TARGETS_SELF,Constants.FAST_CASTER,Constants.SPELL_PRE_BATTLE,
				Constants.SUPER_REALM
			};
		ArrayList<String> thisTest = new ArrayList<>(Arrays.asList(thisVars));
		Hashtable<String,Object> hash = source.getAttributeBlock(blockName);
		for (String key : hash.keySet()) {
			if (!ignoreTest.contains(key)) {
				String val = (String)hash.get(key);
				if (thisTest.contains(key)) {
					dest.setThisAttribute(key,val);
				}
				else {
					// same attributes on either side
					dest.setAttribute("light",key,val);
					dest.setAttribute("dark",key,val);
				}
			}
		}
		// Set the colors separately
		if (source.hasAttribute(blockName,"light_color")) {
			dest.setAttribute("light","chit_color",source.getAttribute(blockName,"light_color"));
		}
		else {
			dest.setAttribute("light","chit_color",source.getAttribute("light","chit_color"));
		}
		if (source.hasAttribute(blockName,"light_color")) {
			dest.setAttribute("dark","chit_color",source.getAttribute(blockName,"dark_color"));
		}
		else {
			dest.setAttribute("dark","chit_color",source.getAttribute("dark","chit_color"));
		}
	}

	public boolean isNative() {
		return false;
	}
	
	private String getAlternativeSpellEffect() {
		return getGameObject().getThisAttribute(Constants.ALTERNATIVE_SPELL_EFFECT);
	}
	
	public boolean freezingTarget() {
		return getGameObject().hasThisAttribute(Constants.FREEZING);
	}
	
	public boolean isSpiderWeb() {
		return getGameObject().hasThisAttribute(Constants.SPIDER_WEB);
	}
	
	public boolean isControlHorseSpell() {
		return getGameObject().hasThisAttribute(Constants.CONTROL_HORSE);
	}
}