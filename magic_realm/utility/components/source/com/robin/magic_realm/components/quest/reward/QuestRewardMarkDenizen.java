package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.BattleChit;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.NativeChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_regex";
	public static final String DENIZEN_AMOUNT = "_amount";
	public static final String TILE = "_tile";
	public static final String MAP = "_map";
	public static final String VULNERARBILITY = "_vulnerability";
	public static final String ATTACK_STRENGTH = "_strength";
	public static final String ATTACK_SPEED = "_attack_speed";
	public static final String ATTACK_LENGTH = "_attack_length";
	public static final String SHARPNESS = "_sharpness";
	public static final String MISSILE = "_missile";
	public static final String MOVE_SPEED = "_move_speed";
	public static final String FLY_SPEED = "_fly_speed";
	public static final String ARMORED = "_armored";
	public static final String CHECK_BOTH_SIDES = "_both_sides";
	public static final String INCLUDE_WEAPONS = "_include_weapons";
	public static final String NATIVES_ONLY = "_natives_only";
	public static final String MONSTERS_ONLY = "_monsters_only";
	public static final String GUARDIANS_ONLY = "_guardians_only";
	public static final String REMOVE_MARK = "_remove_mark";
	public static final String REMOVE_UNCONTROLLED_HIRELINGS = "_remove_uncontrolled_hirelings";
	public static final String IGNORE_HIRELINGS = "_ignore_hirelings";
	public static final String IGNORE_CONTROLLED_DENIZENS = "_ignore_controlled";
	public static final String IGNORE_COMPANIONS = "_ignore_companions";
	public static final String IGNORE_SUMMONED = "_ignore_summoned";

	public QuestRewardMarkDenizen(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		TileLocation current = character.getCurrentLocation();
		String regex = getDenizenRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		int markedDenizen = 0;
		ArrayList<RealmComponent> denizens = new ArrayList<>();
		if (allOnMap()) {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			for (GameObject go : pool.find("denizen")) {
				denizens.add(RealmComponent.getRealmComponent(go));
			}
		} else {
			if (allInTile()) {
				for (ClearingDetail cl : current.tile.getClearings()) {
					denizens.addAll(cl.getClearingComponents());
				}
			} else {
				if (!current.isInClearing()) return;
				denizens.addAll(current.clearing.getClearingComponents());
			}
		}
		
		if (getDenizenAmount()!=0) {
			Collections.shuffle(denizens);
		}
		
		for (RealmComponent rc:denizens) {
			if (ignoreHirelings() && rc.isHireling()) continue;
			if (ignoreControlledDenizens() && (rc.isControlledNative() || rc.isControlledMonster())) continue;
			if (ignoreCompanions() && rc.isCompanion()) continue;
			if (ignoreSummoned() && rc.isSummoned()) continue;
			if (nativesOnly() && !rc.isNative()) continue;
			if (monstersOnly() && !rc.isMonster()) continue;
			if (guardiansOnly() && !RealmUtility.denizenIsGuardian(rc,character.getGameData())) continue;
			if (pattern==null || pattern.matcher(rc.getGameObject().getName()).find()) {
				if (checkStats()) {
					Strength vul = new Strength();
					Strength str = new Strength();
					int sharp = 0;
					Boolean armored = false;
					BattleChit battleChit = null;
					if (rc.isNative()) {
						NativeChitComponent nativeChit = (NativeChitComponent) rc;
						battleChit = (NativeChitComponent) rc;
						vul = nativeChit.getVulnerability();
						str = nativeChit.getStrength();
						sharp = nativeChit.getSharpness();
						armored = nativeChit.isArmored();
						if (checkBothSides()) {
							rc.flip();
							Strength vul2 = nativeChit.getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = nativeChit.getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, nativeChit.getSharpness());
							armored = armored ? armored : nativeChit.isArmored();
							rc.flip();
						}
					}
					else if (rc.isMonster()) {
						MonsterChitComponent monsterChit = (MonsterChitComponent) rc;
						battleChit = (MonsterChitComponent) rc;
						vul = monsterChit.getVulnerability();
						str = monsterChit.getStrength();
						sharp = monsterChit.getSharpness();
						armored = monsterChit.isArmored();
						if (checkBothSides()) {
							rc.flip();
							Strength vul2 = monsterChit.getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = monsterChit.getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, monsterChit.getSharpness());
							armored = armored ? armored : monsterChit.isArmored();
							rc.flip();
						}
					}
					else {
						continue;
					}
					Speed attackSpeed = battleChit.getAttackSpeed();
					Integer length = battleChit.getLength();
					boolean isMissile = battleChit.isMissile();
					Speed moveSpeed = battleChit.getMoveSpeed();
					Speed flySpeed = battleChit.getFlySpeed();
					if (checkBothSides()) {
						rc.flip();
						Speed attackSpeed2 = battleChit.getAttackSpeed();
						attackSpeed = attackSpeed.fasterThanOrEqual(attackSpeed2) ? attackSpeed : attackSpeed2;
						Speed moveSpeed2 = battleChit.getMoveSpeed();
						length = Math.max(length, battleChit.getLength());
						isMissile = isMissile ? isMissile : battleChit.isMissile();
						moveSpeed = moveSpeed.fasterThanOrEqual(moveSpeed2) ? moveSpeed : moveSpeed2;
						Speed flySpeed2 = battleChit.getFlySpeed();
						flySpeed = (flySpeed != null && flySpeed.fasterThanOrEqual(flySpeed2)) ? flySpeed : flySpeed2;
						rc.flip();
					}
					
					if (rc.isMonster() && includeWeapons()) {
						MonsterPartChitComponent weapon = ((MonsterChitComponent) rc).getWeapon();
						if (weapon != null && !weapon.isDestroyed()) {
							Strength weaponStrength = weapon.getStrength();
							str = str.strongerOrEqualTo(weaponStrength) ? str : weaponStrength;
							Speed weaponSpeed = weapon.getAttackSpeed();
							attackSpeed = attackSpeed.fasterThanOrEqual(weaponSpeed) ? attackSpeed : weaponSpeed;
							Integer weaponLength = weapon.getLength();
							length = Math.max(length, weaponLength);
						}
					}
					
					if (getVulnerability() != VulnerabilityType.Any && vul.weakerTo(new Strength(getVulnerability().toString()))) continue;
					if (getAttackStrength() != VulnerabilityType.Any && str.weakerTo(new Strength(getAttackStrength().toString()))) continue;
					if (getAttackSpeed() != 0 && attackSpeed.getNum()>getAttackSpeed()) continue;
					if (getAttackLength() != 0 && length<getAttackLength()) continue;
					if (getSharpness() != 0 && sharp<getSharpness()) continue;
					if (getMissile() && !isMissile) continue;
					if (getMoveSpeed() != 0 && moveSpeed.getNum()>getMoveSpeed()) continue;
					if (getFlySpeed() != 0 && (flySpeed == null || flySpeed.getNum()>getFlySpeed())) continue;
					if (getArmored() && !armored) continue;
				}
				
				if (removeMark()) {
					Quest.GameObjectRemoveQuestMark(rc.getGameObject(),getParentQuest().getGameObject().getStringId());
					if (removeUncontrolledHirelings()) {
						validateControlForHireling(rc,character,getParentQuest().getGameObject().getStringId());
					}
				} else {
					Quest.GameObjectAddQuestMark(rc.getGameObject(),getParentQuest().getGameObject().getStringId());
				}
				markedDenizen++;
				if (getDenizenAmount()!=0 && markedDenizen>=getDenizenAmount()) return;
			}
		}
	}

	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		int number = getDenizenAmount();
		if (number != 0) {
			sb.append("Mark up to "+number+" denizens");
		} else {
			sb.append("Mark all denizens");
		}
		if (allOnMap()) {
			sb.append(" on the map");
		} else {
			sb.append(" in characters");
			if (allInTile()) {
				sb.append(" tile");
			} else {
				sb.append(" clearing");
			}
		}
		if (getDenizenRegEx()!=null && !getDenizenRegEx().isEmpty()) {
			sb.append(" matching the name: "+getDenizenRegEx());
		}
		sb.append(".");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.MarkDenizen;
	}

	public String getDenizenRegEx() {
		return getString(DENIZEN_REGEX);
	}
	
	public int getDenizenAmount() {
		return getInt(DENIZEN_AMOUNT);
	}
	
	public boolean allInTile() {
		return getBoolean(TILE);
	}
	public boolean allOnMap() {
		return getBoolean(MAP);
	}
	private boolean checkStats() {
		return getVulnerability() != VulnerabilityType.Any || getAttackStrength() != VulnerabilityType.Any
				|| getAttackSpeed() != 0 || getAttackLength() != 0 || getSharpness() != 0
				|| getMissile() || getMoveSpeed() != 0 || getFlySpeed() != 0 || getArmored();
	}
	private VulnerabilityType getVulnerability() {
		if (getString(VULNERARBILITY) == null) {
			return VulnerabilityType.Any;
		}
		return VulnerabilityType.valueOf(getString(VULNERARBILITY));
	}
	private VulnerabilityType getAttackStrength() {
		if (getString(ATTACK_STRENGTH) == null) {
			return VulnerabilityType.Any;
		}
		return VulnerabilityType.valueOf(getString(ATTACK_STRENGTH));
	}
	private int getAttackSpeed() {
		return getInt(ATTACK_SPEED);
	}
	private int getAttackLength() {
		return getInt(ATTACK_LENGTH);
	}
	private int getSharpness() {
		return getInt(SHARPNESS);
	}
	private Boolean getMissile() {
		return getBoolean(MISSILE);
	}
	private int getMoveSpeed() {
		return getInt(MOVE_SPEED);
	}
	private int getFlySpeed() {
		return getInt(FLY_SPEED);
	}
	private Boolean getArmored() {
		return getBoolean(ARMORED);
	}
	private Boolean checkBothSides() {
		return getBoolean(CHECK_BOTH_SIDES);
	}
	private Boolean includeWeapons() {
		return getBoolean(INCLUDE_WEAPONS);
	}
	private Boolean nativesOnly() {
		return getBoolean(NATIVES_ONLY);
	}
	private Boolean monstersOnly() {
		return getBoolean(MONSTERS_ONLY);
	}
	private Boolean guardiansOnly() {
		return getBoolean(GUARDIANS_ONLY);
	}
	private Boolean removeMark() {
		return getBoolean(REMOVE_MARK);
	}
	public boolean removeUncontrolledHirelings() {
		return getBoolean(REMOVE_UNCONTROLLED_HIRELINGS);
	}
	private Boolean ignoreHirelings() {
		return getBoolean(IGNORE_HIRELINGS);
	}
	private Boolean ignoreControlledDenizens() {
		return getBoolean(IGNORE_CONTROLLED_DENIZENS);
	}
	private Boolean ignoreCompanions() {
		return getBoolean(IGNORE_COMPANIONS);
	}
	private Boolean ignoreSummoned() {
		return getBoolean(IGNORE_SUMMONED);
	}
}