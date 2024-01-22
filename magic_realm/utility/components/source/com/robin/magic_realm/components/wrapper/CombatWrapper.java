package com.robin.magic_realm.components.wrapper;

import java.util.*;

import com.robin.game.objects.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.Harm;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Spoils;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

/**
 * A generic wrapper for handling all things combat
 */
public class CombatWrapper extends GameObjectWrapper {
	
	public static final String GROUP_RED = "R";
	public static final String GROUP_CIRCLE = "C";
	public static final String GROUP_SQUARE = "S";
	
	public static final String COMBAT_BLOCK = "CMB_BLK_";
	
	// Combat Information
	private static final String COMBAT_HIT_RESULT_LIST = "CMB_RES_LIST";
	private static final String COMBAT_ROUND_RESULT_LIST = "CMB_RND_LIST";
	private static final String COMBAT_KILL_RESULT_LIST = "CMB_KILL_LIST";
	private static final String COMBAT_SPOILS_RESULT_LIST = "CMB_SPLS_LIST";
	private static final String COMBAT_SHEET_OWNER = "CMB_SH_OWN";
	private static final String SHEET_OWNER_ID = "CMB_SH_OWN_ID";
	private static final String PEACE = "PEACE";
	private static final String PEACE_CLEARING = "PEACE_CLEARING";
	private static final String SLEEP_CLEARING = "SLEEP_CLEARING";
	private static final String WATCHFUL = "WATCHFUL";
	private static final String LOCK_NEXT = "LOCK_NEXT";
	private static final String CASTED_RAISE_THE_DEAD = "CAST_RAISE_THE_DEAD";
	private static final String RAISED_UNDEADS_IN_COMBAT = "RAISED_UNDEADS";
	
	// Round-only Information
	private static final String COMBAT_BOX = "CMB_BOX";
	private static final String PLACED_AS_MOVE = "PAM"; // this is only important for the Unleash Power MOVE/FIGHT chits
	private static final String PLACED_AS_FIGHT = "PAF"; // this is only important for the Unleash Power MOVE/FIGHT chits
	private static final String PLACED_AS_PARRY = "PAP";
	private static final String PLACED_AS_PARRY_SHIELD = "PAPS";
	private static final String HAS_CHARGED = "CHRG";
	private static final String CHARGED_BY_IDS = "CHRG_IDS"; // List of game object ids of the MOVE chits used (
	private static final String USED_IDS = "USED_IDS"; // List of chit ids used this round
	private static final String ATTACKER_IDS = "ATK_IDS"; // List of game object ids targeting this participant
	private static final String RANDOMIZE_PREFICES = "RANDOM_P";
	private static final String REPOSITION_RESULT = "REP_RST";
	private static final String CHANGE_TACTICS_RESULT = "CHTC_BX";
	private static final String HIT_RESULT = "HIT_RSLT";
	private static final String HIT_BY_IDS = "HIT_BY_IDS"; // A list of all ids of killers, in the order they hit
	private static final String PARRIED = "PARRIED";
	private static final String KILLED_BY_ID = "KILLED_BY_ID"; // The fastest or longest killer (sounds silly, but I know what I mean here!)
	private static final String KILLED_LENGTH = "KILLED_LENGTH";
	private static final String KILLED_SPEED = "KILLED_SPEED";
	private static final String NEW_WOUNDS = "NEW_WOUNDS";
	private static final String MISSILE_ROLLS = "MISSILE_ROLLS";
	private static final String MISSILE_ROLL_SUBTITLES = "M_ROLL_SS";
	private static final String MISSILE_ROLL_TARGETIDS = "M_ROLL_TI";
	private static final String MAGIC_HIT_LIST = "MAGIC_HITS";
	private static final String SKIP_COMBAT = "SKIP_COMBAT";
	private static final String FUMBLE_ROLLS = "FUMBLE_ROLLS";
	private static final String FUMBLE_ROLL_SUBTITLES = "F_ROLL_SS";
	private static final String FUMBLE_ROLL_TARGETIDS = "F_ROLL_TI";
	private static final String CAST_SPELL = "CAST_SPELL"; // indicates the player cast a spell this combat round
	private static final String BURNED_COLOR = "BURNED_COLOR"; // indicates the player burned a color chit this combat round
	private static final String SERIOUS_WOUND_ROLLS = "S_W_ROLLS";
	private static final String PLAYED_ATTACK = "PLAYED_ATTACK";
	private static final String PLAYED_SPELL = "PLAYED_SPELL";
	private static final String PLAYED_BONUS_PARRY = "PLAYED_BONUS_PARRY";
	private static final String THROWN = "THROWN";
	private static final String TARGETING_RIDER = "TARGETING_RIDER";
	private static final String GALLOPED = "GALLOPED";
	private static final String RAISE_THE_DEAD = "RAISE_THE_DEAD";
	private static final String SPELL_CANCELED = "SPELL_CANCELED";
	private static final String ATTACKED_AFTER_CASTING = "ATTACKED_AFTER_CASTING";
	private static final String FREEZED = "FREEZED";
	private static final String PACIFIED = "PACIFIED";
	private static final String HORSE_CANNOT_MANEUVER = "HORSE_CANNOT_MANEUVER";
	
	private static final String HEALING = "HEALING";
	
	private static final String HARM_APPLIED = "HA_";
	private static final String HARM_APPLIED_ID = "HAID_";
	private static final String HIT_TYPE = "HT_";
	private static final String HIT_TYPE_ID = "HTID_";
	private static final String WEAPON_ID = "WID_";
	private static final String HIT_WEAPON_ID = "HIT_WID_";
	
	private static final String HIT_BY_ORDER_NUMBER = "HIT_BY_ORDER_NUMBER";
	
	private static final String WAS_FATIGUE = "WAS_FATIGUE";
	
	public CombatWrapper(GameObject obj) {
		super(obj);
	}
	
	public CombatWrapper(Object obj){
		super((GameObject)obj);
	}
	
	public String toString() {
		return getGameObject().getName();
	}
	public String getBlockName() {
		return COMBAT_BLOCK;
	}
	public void setSheetOwner(boolean val) {
		setBoolean(COMBAT_SHEET_OWNER,val);
	}
	public void setSheetOwnerId(RealmComponent owner) {
		setString(SHEET_OWNER_ID,owner.getGameObject().getStringId());
	}
	public String getSheetOwnerId() {
		return getString(SHEET_OWNER_ID);
	}
	public boolean isSheetOwner() {
		return getBoolean(COMBAT_SHEET_OWNER);
	}
	public void setWatchful(boolean val) {
		setBoolean(WATCHFUL,val);
	}
	public boolean isWatchful() {
		return getBoolean(WATCHFUL);
	}
	public void setBurnedColor(boolean val) {
		setBoolean(BURNED_COLOR,val);
	}
	public boolean isBurnedColor() {
		return getBoolean(BURNED_COLOR);
	}
	public void setLockNext(boolean val) {
		setBoolean(LOCK_NEXT,val);
	}
	public boolean isLockNext() {
		return getBoolean(LOCK_NEXT);
	}
	/**
	 * Sets the most recent cast spell
	 */
	public void setCastSpell(GameObject spell) {
		setString(CAST_SPELL,String.valueOf(spell.getId()));
	}
	/**
	 * @return		The spell that was cast THIS round (or null if none)
	 */
	public GameObject getCastSpell() {
		String id = getString(CAST_SPELL);
		if (id!=null) {
			return getGameObject().getGameData().getGameObject(Long.valueOf(id));
		}
		return null;
	}
	public boolean hasCastSpell() {
		String id = getString(CAST_SPELL);
		return id!=null && !id.isEmpty();
	}
	public void clearCastSpell() {
		GameObject go = getCastSpell();
		if (go!=null) {
			SpellWrapper spell = new SpellWrapper(go);
			spell.expireSpell();
			setBoolean(CAST_SPELL,false);
		}
	}
	public void setCancelSpell() {
		setBoolean(SPELL_CANCELED, true);
	}
	public boolean getCancelSpell() {
		return getBoolean(SPELL_CANCELED);
	}
	public void setAttackedAfterCasting() {
		setBoolean(ATTACKED_AFTER_CASTING, true);
	}
	public boolean getAttackedAfterCasting() {
		return getBoolean(ATTACKED_AFTER_CASTING);
	}
	public void setRaiseTheDead() {
		setBoolean(RAISE_THE_DEAD, true);
	}
	public boolean getRaiseTheDead() {
		return getBoolean(RAISE_THE_DEAD);
	}
	public void setRaisedDead() {
		setBoolean(CASTED_RAISE_THE_DEAD, true);
	}
	public boolean getRaisedDead() {
		return getBoolean(CASTED_RAISE_THE_DEAD);
	}
	public void freeze() {
		setBoolean(FREEZED, true);
	}
	public boolean isFreezed() {
		return getBoolean(FREEZED);
	}
	public void targetsRemoveAttackers() {
		RealmComponent rc = RealmComponent.getRealmComponent(this.getGameObject());
		if (rc.getTarget()!=null) {
			(new CombatWrapper(rc.getTarget().getGameObject())).removeAttacker(this.getGameObject());
		}
		if (rc.get2ndTarget()!=null) {
			(new CombatWrapper(rc.get2ndTarget().getGameObject())).removeAttacker(this.getGameObject());
		}
		rc.clearTargets();
	}
	public void pacify() {
		setBoolean(PACIFIED, true);
		targetsRemoveAttackers();
	}
	public void removePacified() {
		setBoolean(PACIFIED, false);
	}
	public boolean isPacified() {
		return getBoolean(PACIFIED);
	}
	public void setSkipCombat(boolean val) {
		setBoolean(SKIP_COMBAT,val);
	}
	public boolean getSkipCombat() {
		return getBoolean(SKIP_COMBAT);
	}
	public void setCombatBox(int val) {
		setInt(COMBAT_BOX,val);
	}
	public int getCombatBox() {
		return getInt(COMBAT_BOX);
	}
	public void setPeace(boolean peace) {
		setBoolean(PEACE,peace);
	}
	public boolean isPeaceful() {
		return getBoolean(PEACE);
	}
	public void addPeaceClearing(int clearing) {
		addListItem(PEACE_CLEARING,String.valueOf(clearing));
	}
	public ArrayList<String> getPeaceClearings() {
		return getList(PEACE_CLEARING);
	}
	public boolean isPeaceClearing(int clearing) {
		if (isPeaceful()) {
			ArrayList<String> list = getPeaceClearings();
			return (list!=null && list.contains(String.valueOf(clearing)));
		}
		return false;
	}
	public void addSleepClearing(int clearing) {
		addListItem(SLEEP_CLEARING,String.valueOf(clearing));
	}
	public ArrayList<String> getSleepClearings() {
		return getList(SLEEP_CLEARING);
	}
	public boolean isSleepClearing(int clearing) {
		ArrayList<String> list = getSleepClearings();
		return (list!=null && list.contains(String.valueOf(clearing)));
	}
	private void addRandomizePrefix(String prefix) {
		ArrayList<String> list = getRandomPrefices();
		if (list==null || !list.contains(prefix)) {
			addListItem(RANDOMIZE_PREFICES,prefix);
		}
	}
	private ArrayList<String> getRandomPrefices() {
		return getList(RANDOMIZE_PREFICES);
	}
	public void setRepositionResult(String prefix,int val) {
		addRandomizePrefix(prefix);
		setInt(prefix+REPOSITION_RESULT,val);
	}
	public int getRepositionResult(String prefix) {
		return getInt(prefix+REPOSITION_RESULT);
	}
	public void setChangeTacticsResult(String prefix,int box,String val) {
		addRandomizePrefix(prefix);
		setString(prefix+CHANGE_TACTICS_RESULT+box,val);
	}
	public String getChangeTacticsResult(String prefix,int box) {
		return getString(prefix+CHANGE_TACTICS_RESULT+box);
	}
	public void setHitResult(String val) {
		setString(HIT_RESULT,val);
	}
	public String getHitResult() {
		return getString(HIT_RESULT);
	}
	public void setWasParried(String val) {
		addListItem(PARRIED,val);
	}
	public boolean wasParried() {
		return getBoolean(PARRIED);
	}
	public ArrayList<String> getParriedBy() {
		return getList(PARRIED);
	}
	public void setHitByOrderNumber(int val) {
		setInt(HIT_BY_ORDER_NUMBER,val);
	}
	public int getHitByOrderNumber() {
		return getInt(HIT_BY_ORDER_NUMBER);
	}
	public void addSeriousWoundRoll(String val) {
		addListItem(SERIOUS_WOUND_ROLLS,val);
	}
	public ArrayList<String> getSeriousWoundRolls() {
		return getList(SERIOUS_WOUND_ROLLS);
	}
	public void addMissileRoll(String val) {
		addListItem(MISSILE_ROLLS,val);
	}
	public ArrayList<String> getMissileRolls() {
		return getList(MISSILE_ROLLS);
	}
	public void addMissileRollSubtitle(String val) {
		addListItem(MISSILE_ROLL_SUBTITLES,val);
	}
	public ArrayList<String> getMissileRollSubtitles() {
		return getList(MISSILE_ROLL_SUBTITLES);
	}
	public void addMissileRollTargetId(String val) {
		addListItem(MISSILE_ROLL_TARGETIDS,val);
	}
	public ArrayList<String> getMissileRollTargetIds() {
		return getList(MISSILE_ROLL_TARGETIDS);
	}
	public void addFumbleRoll(String val) {
		addListItem(FUMBLE_ROLLS,val);
	}
	public ArrayList<String> getFumbleRolls() {
		return getList(FUMBLE_ROLLS);
	}
	public void addFumbleRollSubtitle(String val) {
		addListItem(FUMBLE_ROLL_SUBTITLES,val);
	}
	public ArrayList<String> getFumbleRollSubtitles() {
		return getList(FUMBLE_ROLL_SUBTITLES);
	}
	public void addFumbleRollTargetId(String val) {
		addListItem(FUMBLE_ROLL_TARGETIDS,val);
	}
	public ArrayList<String> getFumbleRollTargetIds() {
		return getList(FUMBLE_ROLL_TARGETIDS);
	}
	public void addHitType(int hitType,GameObject target) {
		addListItem(HIT_TYPE,String.valueOf(hitType));
		addListItem(HIT_TYPE_ID,target.getStringId());
	}
	public Integer getHitType(GameObject target) {
		ArrayList<String> types = getList(HIT_TYPE);
		ArrayList<String> targetids = getList(HIT_TYPE_ID);
		if (types!=null) {
			String id = target.getStringId();
			Iterator<String> h = types.iterator();
			Iterator<String> hi = targetids.iterator();
			while(h.hasNext()) {
				String type = h.next();
				String tid = hi.next();
				if (id.equals(tid)) {
					return Integer.valueOf(type);
				}
			}
		}
		return null;
	}
	public void addHarmApplied(Harm harm,GameObject target) {
		addListItem(HARM_APPLIED,harm.toKey());
		addListItem(HARM_APPLIED_ID,target.getStringId());
	}
	public String getHarmApplied(GameObject target) {
		ArrayList<String> harms = getList(HARM_APPLIED);
		ArrayList<String> harmids = getList(HARM_APPLIED_ID);
		if (harms!=null) {
			String id = target.getStringId();
			Iterator<String> h = harms.iterator();
			Iterator<String> hi = harmids.iterator();
			while(h.hasNext()) {
				String harm = h.next();
				String tid = hi.next();
				if (id.equals(tid)) {
					return harm;
				}
			}
		}
		return null;
	}
	public void setPlayedAttack(boolean val) {
		setBoolean(PLAYED_ATTACK,val);
	}
	public boolean getPlayedAttack() {
		return getBoolean(PLAYED_ATTACK);
	}
	public void setPlayedSpell(boolean val) {
		setBoolean(PLAYED_SPELL,val);
	}
	public boolean getPlayedSpell() {
		return getBoolean(PLAYED_SPELL);
	}
	public void setPlayedBonusParry(boolean val) {
		setBoolean(PLAYED_BONUS_PARRY,val);
	}
	public boolean getPlayedBonusParry() {
		return getBoolean(PLAYED_BONUS_PARRY);
	}
	public void setThrown(boolean val) {
		setBoolean(THROWN,val);
	}
	public boolean wasThrown() {
		return getBoolean(THROWN);
	}
	public void setTargetingRider(GameObject attacker) {
		addListItem(TARGETING_RIDER,attacker.getStringId());
	}
	public boolean isTargetingRider(GameObject attacker) {
		return hasListItem(TARGETING_RIDER,attacker.getStringId());
	}
	public void setGalloped(boolean val) {
		setBoolean(GALLOPED,val);
	}
	public boolean hasGalloped() {
		return getBoolean(GALLOPED);
	}
	public void setHorseCannotManeuver(boolean val) {
		setBoolean(HORSE_CANNOT_MANEUVER,val);
	}
	public boolean getHorseCannotManeuver() {
		return getBoolean(HORSE_CANNOT_MANEUVER);
	}
	public void setKilledBy(GameObject killer) {
		setString(KILLED_BY_ID,String.valueOf(killer.getId()));
	}
	public void setKilledLength(Integer length) {
		setInt(KILLED_LENGTH,length.intValue());
	}
	public void setKilledSpeed(Speed speed) {
		setInt(KILLED_SPEED,speed.getNum());
	}
	public GameObject getKilledBy() {
		String id = getString(KILLED_BY_ID);
		if (id!=null) {
			GameData data = getGameObject().getGameData();
			return data.getGameObject(Long.valueOf(id));
		}
		return null;
	}
	public int getKilledLength() {
		return getInt(KILLED_LENGTH);
	}
	public int getKilledSpeed() {
		return getInt(KILLED_SPEED);
	}
	public boolean isDead() {
		return getString(KILLED_BY_ID)!=null;
	}
	public void addHitBy(GameObject attacker) {
		addListItem(HIT_BY_IDS,String.valueOf(attacker.getId()));
	}
	public boolean wasHitBy(GameObject attacker) {
		ArrayList<String> list = getList(HIT_BY_IDS);
		if (list!=null && !list.isEmpty()) {
			String aid = attacker.getStringId();
			for (String id : list) {
				if (aid.equals(id)) {
					return true;
				}
			}
		}
		return false;
	}
	public ArrayList<GameObject> getHitByList() {
		ArrayList<String> list = getList(HIT_BY_IDS);
		if (list!=null && !list.isEmpty()) {
			GameData data = getGameObject().getGameData();
			ArrayList<GameObject> ret = new ArrayList<>();
			for (String id : list) {
				ret.add(data.getGameObject(Long.valueOf(id)));
			}
			return ret;
		}
		return null;
	}
//	public void addMagicHit(String magicType) { // V or VIII
//		addListItem(MAGIC_HIT_LIST,magicType);
//	}
//	public ArrayList getMagicHitList() {
//		return getList(MAGIC_HIT_LIST);
//	}
	public void addNewWounds(int val) {
		int current = getInt(NEW_WOUNDS);
		setInt(NEW_WOUNDS,val+current);
	}
	public int getNewWounds() {
		return getInt(NEW_WOUNDS);
	}
	public void addHealing(int val) {
		int current = getInt(HEALING);
		setInt(HEALING,val+current);
	}
	public int getHealing() {
		return getInt(HEALING);
	}
	public void setWasFatigue(boolean val) {
		setBoolean(WAS_FATIGUE,val); // only used by tile to track fatigue by characters running away this round
	}
	public boolean getWasFatigue() {
		return getBoolean(WAS_FATIGUE);
	}
	public void addHitResult() {
		addListItem(COMBAT_HIT_RESULT_LIST,"H"); // only used by tile to record total combat hits
	}
	public void addMissResult() {
		addListItem(COMBAT_HIT_RESULT_LIST,"M"); // only used by tile to record total combat misses
	}
	public void addKillResult() {
		addListItem(COMBAT_HIT_RESULT_LIST,"K"); // only used by battle participants to record kills
	}
	public void addKill(GameObject kill) {
		addListItem(COMBAT_KILL_RESULT_LIST,kill.getStringId());; // only used by battle location to record kills
	}
	public void addSpoilsInfo(int round,GameObject kill,Spoils spoils) {
		addListItem(COMBAT_ROUND_RESULT_LIST,String.valueOf(round));
		addListItem(COMBAT_KILL_RESULT_LIST,kill.getStringId());
		addListItem(COMBAT_SPOILS_RESULT_LIST,spoils.asKey());
	}
	public ArrayList<Integer> getAllRounds() {
		ArrayList<Integer> rounds = null;
		if (getBoolean(COMBAT_ROUND_RESULT_LIST)) {
			rounds = new ArrayList<>();
			for (String i : getList(COMBAT_ROUND_RESULT_LIST)) {
				rounds.add(Integer.valueOf(i));
			}
		}
		return rounds;
	}
	public ArrayList<GameObject> getAllKills() {
		GameData gameData = getGameObject().getGameData();
		ArrayList<GameObject> kills = new ArrayList<>();
		if (getBoolean(COMBAT_KILL_RESULT_LIST)) {
			for (String i : getList(COMBAT_KILL_RESULT_LIST)) {
				kills.add(gameData.getGameObject(Long.valueOf(i)));
			}
		}
		return kills;
	}
	public ArrayList<Spoils> getAllSpoils() {
		ArrayList<Spoils> spoils = null;
		if (getBoolean(COMBAT_SPOILS_RESULT_LIST)) {
			spoils = new ArrayList<>();
			for (String i : getList(COMBAT_SPOILS_RESULT_LIST)) {
				spoils.add(new Spoils(i));
			}
		}
		return spoils;
	}
	public void clearHitResults() {
		setBoolean(COMBAT_HIT_RESULT_LIST,false);
	}
	public void clearKillResults() {
		setBoolean(COMBAT_KILL_RESULT_LIST,false);
	}
	public int getHitResultCount() {
		ArrayList<String> results = getList(COMBAT_HIT_RESULT_LIST);
		if (results!=null) {
			return results.size();
		}
		return 0;
	}
	public int getRoundsOfMissing() {
		ArrayList<String> list = getList(COMBAT_HIT_RESULT_LIST);
		if (list!=null) {
			ArrayList<String> results = new ArrayList<>(list);
			int count = 0;
			for (int i=results.size()-1;i>=0;i--) {
				String result = results.get(i);
				if ("M".equals(result)) {
					count++;
				}
				else {
					break;
				}
			}
			return count;
		}
		return 0;
	}
	public boolean lastTwoAreMisses() {
		ArrayList<String> list = getList(COMBAT_HIT_RESULT_LIST);
		if (list!=null) {
			ArrayList<String> results = new ArrayList<>(list);
			if (results.size()>=2) {
				String val1 = results.remove(results.size()-1);
				String val2 = results.remove(results.size()-1);
				return ("M".equals(val1) && "M".equals(val2));
			}
		}
		return false;
	}
	
	public void setPlacedAsMove(boolean val) {
		setBoolean(PLACED_AS_MOVE,val);
	}
	public void setPlacedAsFight(boolean val) {
		setBoolean(PLACED_AS_FIGHT,val);
	}
	public void setPlacedAsParry(boolean val) {
		setBoolean(PLACED_AS_PARRY,val);
	}
	public void setPlacedAsParryShield(boolean val) {
		setBoolean(PLACED_AS_PARRY_SHIELD,val);
	}
	public void setWeaponId(RealmComponent weapon) {
		setString(WEAPON_ID,weapon.getGameObject().getStringId());
	}
	public void setWeaponHit(String weaponId) {
		addListItem(HIT_WEAPON_ID,weaponId);
	}
	public boolean getPlacedAsMove() {
		return getBoolean(PLACED_AS_MOVE);
	}
	public boolean getPlacedAsFight() {
		return getBoolean(PLACED_AS_FIGHT);
	}
	public boolean getPlacedAsParry() {
		return getBoolean(PLACED_AS_PARRY);
	}
	public boolean getPlacedAsParryShield() {
		return getBoolean(PLACED_AS_PARRY_SHIELD);
	}
	public boolean getPlacedAsFightOrParryOrParryShield() {
		return getPlacedAsFight() || getPlacedAsParry() || getPlacedAsParryShield();
	}
	public String getWeaponId() {
		return getString(WEAPON_ID);
	}
	public ArrayList<String> getWeaponsHit() {
		return getList(HIT_WEAPON_ID);
	}
	public boolean weaponHasHit(String weaponId) {
		return hasListItem(HIT_WEAPON_ID, weaponId);
	}
	
	// Charging
	public void setHasCharged(boolean val) {
		setBoolean(HAS_CHARGED,val);
	}
	public boolean getHasCharged() {
		return getBoolean(HAS_CHARGED);
	}
	public void addChargeChit(GameObject chit) {
		addListItem(CHARGED_BY_IDS,String.valueOf(chit.getId()));
	}
	public Collection<GameObject> getChargeChits() {
		GameData data = getGameObject().getGameData();
		ArrayList<GameObject> list = new ArrayList<>();
		ArrayList<String> ids = getList(CHARGED_BY_IDS);
		if (ids!=null) {
			for (String id : ids) {
				GameObject go = data.getGameObject(Long.valueOf(id));
				list.add(go);
			}
		}
		return list;
	}
	public int getChargeChitCount() {
		ArrayList<String> ids = getList(CHARGED_BY_IDS);
		if (ids!=null) {
			return ids.size();
		}
		return 0;
	}
	// Using
	public void addUsedChit(GameObject chit) {
		addListItem(USED_IDS,String.valueOf(chit.getId()));
	}
	public ArrayList<GameObject> getUsedChits() {
		GameData data = getGameObject().getGameData();
		ArrayList<GameObject> list = new ArrayList<>();
		ArrayList<String> ids = getList(USED_IDS);
		if (ids!=null) {
			for (String id : ids) {
				GameObject go = data.getGameObject(Long.valueOf(id));
				list.add(go);
			}
		}
		return list;
	}
	public int getUsedChitCount() {
		ArrayList<String> ids = getList(USED_IDS);
		if (ids!=null) {
			return ids.size();
		}
		return 0;
	}
	
	// Attacker
	public void addAttacker(GameObject attacker) {
		if (isPeaceful()) { // PEACE ends if attacked
			setPeace(false);
			RealmLogging.logMessage(
					getGameObject().getName(),
					"No longer affected by PEACE, because of attack by "+attacker.getName());
		}
		if (isPacified()) {
			removePacified();
		}
		ArrayList<String> ids = getList(ATTACKER_IDS);
		if (ids==null || !ids.contains(attacker.getStringId())) {
			addListItem(ATTACKER_IDS,attacker.getStringId());
		}
	}
	public void removeAttacker(GameObject attacker) {
		removeListItem(ATTACKER_IDS,attacker.getStringId());
	}
	public void removeAllAttackers() {
		setBoolean(ATTACKER_IDS,false);
	}
	public ArrayList<GameObject> getAttackers() {
		GameData data = getGameObject().getGameData();
		ArrayList<GameObject> list = new ArrayList<>();
		ArrayList<String> ids = getList(ATTACKER_IDS);
		if (ids!=null) {
			for (String id : ids) {
				GameObject go = data.getGameObject(Long.valueOf(id));
				list.add(go);
			}
		}
		return list;
	}
	public ArrayList<RealmComponent> getAttackersAsComponents() {
		ArrayList<RealmComponent> ret = new ArrayList<>();
		for (GameObject go:getAttackers()) {
			ret.add(RealmComponent.getRealmComponent(go));
		}
		return ret;
	}
	public int getAttackerCount() {
		ArrayList<String> ids = getList(ATTACKER_IDS);
		if (ids!=null) {
			return ids.size();
		}
		return 0;
	}
	
	public void addRaisedUndead(GameObject undead) {
		addListItem(RAISED_UNDEADS_IN_COMBAT,undead.getStringId());
	}
	
	public ArrayList<GameObject> getRaisedUndeads() {
		GameData data = getGameObject().getGameData();
		ArrayList<GameObject> list = new ArrayList<>();
		ArrayList<String> ids = getList(RAISED_UNDEADS_IN_COMBAT);
		if (ids!=null) {
			for (String id : ids) {
				GameObject go = data.getGameObject(Long.valueOf(id));
				list.add(go);
			}
		}
		return list;
	}
	
	/**
	 * Strips all round related information - leaves other combat info (like number of consecutive rounds without hits)
	 */
	public static void clearRoundCombatInfo(GameObject go) {
		if (hasCombatInfo(go)) {
			go.removeAttribute(COMBAT_BLOCK,HAS_CHARGED);
			go.removeAttribute(COMBAT_BLOCK,CHARGED_BY_IDS);
			go.removeAttribute(COMBAT_BLOCK,USED_IDS);
			go.removeAttribute(COMBAT_BLOCK,ATTACKER_IDS);
			go.removeAttribute(COMBAT_BLOCK,COMBAT_BOX);
			go.removeAttribute(COMBAT_BLOCK,PLACED_AS_FIGHT);
			go.removeAttribute(COMBAT_BLOCK,PLACED_AS_PARRY);
			go.removeAttribute(COMBAT_BLOCK,PLACED_AS_PARRY_SHIELD);
			go.removeAttribute(COMBAT_BLOCK,PLACED_AS_MOVE);
			go.removeAttribute(COMBAT_BLOCK,HIT_RESULT);
			go.removeAttribute(COMBAT_BLOCK,PARRIED);
			go.removeAttribute(COMBAT_BLOCK,KILLED_BY_ID);
			go.removeAttribute(COMBAT_BLOCK,KILLED_LENGTH);
			go.removeAttribute(COMBAT_BLOCK,KILLED_SPEED);
			go.removeAttribute(COMBAT_BLOCK,NEW_WOUNDS);
			go.removeAttribute(COMBAT_BLOCK,HEALING);
			go.removeAttribute(COMBAT_BLOCK,MISSILE_ROLLS);
			go.removeAttribute(COMBAT_BLOCK,MISSILE_ROLL_SUBTITLES);
			go.removeAttribute(COMBAT_BLOCK,MISSILE_ROLL_TARGETIDS);
			go.removeAttribute(COMBAT_BLOCK,MAGIC_HIT_LIST);
			go.removeAttribute(COMBAT_BLOCK,SKIP_COMBAT);
			go.removeAttribute(COMBAT_BLOCK,FUMBLE_ROLLS);
			go.removeAttribute(COMBAT_BLOCK,FUMBLE_ROLL_SUBTITLES);
			go.removeAttribute(COMBAT_BLOCK,FUMBLE_ROLL_TARGETIDS);
			go.removeAttribute(COMBAT_BLOCK,BURNED_COLOR);
			go.removeAttribute(COMBAT_BLOCK,CAST_SPELL);
			go.removeAttribute(COMBAT_BLOCK,SERIOUS_WOUND_ROLLS);
			go.removeAttribute(COMBAT_BLOCK,HIT_BY_ORDER_NUMBER);
			go.removeAttribute(COMBAT_BLOCK,WAS_FATIGUE);
			go.removeAttribute(COMBAT_BLOCK,PLAYED_ATTACK);
			go.removeAttribute(COMBAT_BLOCK,PLAYED_SPELL);
			go.removeAttribute(COMBAT_BLOCK,PLAYED_BONUS_PARRY);
			go.removeAttribute(COMBAT_BLOCK,THROWN);
			go.removeAttribute(COMBAT_BLOCK,TARGETING_RIDER);
			go.removeAttribute(COMBAT_BLOCK,GALLOPED);
			go.removeAttribute(COMBAT_BLOCK,HORSE_CANNOT_MANEUVER);
			go.removeAttribute(COMBAT_BLOCK,HARM_APPLIED);
			go.removeAttribute(COMBAT_BLOCK,HARM_APPLIED_ID);
			go.removeAttribute(COMBAT_BLOCK,HIT_TYPE);
			go.removeAttribute(COMBAT_BLOCK,HIT_TYPE_ID);
			go.removeAttribute(COMBAT_BLOCK,WEAPON_ID);
			go.removeAttribute(COMBAT_BLOCK,HIT_WEAPON_ID);
			go.removeAttribute(COMBAT_BLOCK,SHEET_OWNER_ID);
			go.removeAttribute(COMBAT_BLOCK,RAISE_THE_DEAD);
			go.removeAttribute(COMBAT_BLOCK,SPELL_CANCELED);
			go.removeAttribute(COMBAT_BLOCK,ATTACKED_AFTER_CASTING);
			go.removeAttribute(COMBAT_BLOCK,FREEZED);
			go.removeAttribute(COMBAT_BLOCK,Constants.HOLY_SHIELD);
			
			ArrayList<String> list = go.getAttributeList(COMBAT_BLOCK,RANDOMIZE_PREFICES);
			if (list!=null) {
				for (String prefix : list) {
					go.removeAttribute(COMBAT_BLOCK,prefix+REPOSITION_RESULT);
					for (int n=1;n<=3;n++) {
						go.removeAttribute(COMBAT_BLOCK,prefix+CHANGE_TACTICS_RESULT+n);
					}
				}
			}
		}
	}
	/**
	 * Strips all combat related information from the game object and anything held within (like dead native horses!!)
	 */
	public static void clearAllCombatInfo(GameObject go) {
		if (hasCombatInfo(go)) {
			go.removeAttributeBlock(COMBAT_BLOCK);
			if (go.getHoldCount()>0) {
				for (GameObject held : go.getHold()) {
					clearAllCombatInfo(held);
					if (held.hasThisAttribute(Quest.QUEST_MINOR_CHARS)) {
						for (GameObject bonusChit : held.getHold()) {
							clearAllCombatInfo(bonusChit);
						}
					}
				}
			}
		}
	}
	public static boolean hasCombatInfo(GameObject test) {
		return test.hasAttributeBlock(COMBAT_BLOCK);
	}
	
	public void setHolyShield(Speed attackSpeed, int length) {
		getGameObject().addAttributeListItem(COMBAT_BLOCK,Constants.HOLY_SHIELD,"s_"+String.valueOf(attackSpeed.getNum())+"l_"+String.valueOf(length));
	}
	public boolean hasHolyShield(Speed attackSpeed, int length) {
		ArrayList<String> holyShields = getGameObject().getAttributeList(COMBAT_BLOCK,Constants.HOLY_SHIELD);
		if (holyShields==null||holyShields.isEmpty()) return false;
		for (String shield : holyShields) {
			if (shield.matches("s_"+String.valueOf(attackSpeed.getNum())+"l_"+String.valueOf(length))) return true;
		}
		return false;
	}
}