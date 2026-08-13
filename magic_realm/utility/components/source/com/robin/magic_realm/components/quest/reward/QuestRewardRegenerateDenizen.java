package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.BattleChit;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.NativeChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardRegenerateDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_drx";
	public static final String DENIZEN_AMOUNT = "_damnt";
	public static final String CHARACTERS_CLEARING = "_ch_cl";
	public static final String CHARACTERS_TILE = "_ch_tile";
	public static final String REGENERATE_HIRELINGS = "_reg_hirelings";
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
	public static final String REQ_MARK = "_req_mark";
	public static final String REMOVE_MARKS = "_remove_marks";
	
	public QuestRewardRegenerateDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		int regeneratedDenizens = 0;
		if (numberOfDenizens()!=0) {
			Collections.shuffle(denizens);
		}
		String questId = getParentQuest().getGameObject().getStringId();
		for (GameObject denizen : denizens) {
			if (denizen != null && denizen.hasThisAttribute("denizen") && !denizen.hasThisAttribute(Constants.CLONED) && !denizen.hasThisAttribute(Constants.COMPANION) && !denizen.hasThisAttribute(Constants.SUMMONED)) {				
				RealmComponent denizenRc = RealmComponent.getRealmComponent(denizen);
				if (nativesOnly() && !denizenRc.isNative()) continue;
				if (monstersOnly() && !denizenRc.isMonster()) continue;
				if (guardiansOnly() && !RealmUtility.denizenIsGuardian(denizenRc,character.getGameData())) continue;
				if (requiresMark() && !Quest.GameObjectHasQuestMark(denizen, questId)) {
					continue;
				}
				if (denizenRc.getOwner()!=null && !regenerateHirelings()) continue;
				if (charactersClearingOnly()) {
					if(denizenRc.getCurrentLocation() == null || character.getCurrentLocation() == null || denizenRc.getCurrentLocation().tile != character.getCurrentLocation().tile || denizenRc.getCurrentLocation().clearing != character.getCurrentLocation().clearing) {
						continue;
					}
				}
				if (charactersTileOnly()) {
					if(denizenRc.getCurrentLocation() == null || character.getCurrentLocation() == null || denizenRc.getCurrentLocation().tile != character.getCurrentLocation().tile) {
						continue;
					}
				}
				
				if (checkStats()) {
					Strength vul = new Strength();
					Strength str = new Strength();
					int sharp = 0;
					Boolean armored = false;
					BattleChit battleChit = null;
					if (denizenRc.isNative()) {
						NativeChitComponent nativeChit = (NativeChitComponent) denizenRc;
						battleChit = (NativeChitComponent) denizenRc;
						vul = nativeChit.getVulnerability();
						str = nativeChit.getStrength();
						sharp = nativeChit.getSharpness();
						armored = nativeChit.isArmored();
						if (checkBothSides()) {
							denizenRc.flip();
							Strength vul2 = nativeChit.getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = nativeChit.getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, nativeChit.getSharpness());
							armored = armored ? armored : nativeChit.isArmored();
							denizenRc.flip();
						}
					}
					else if (denizenRc.isMonster()) {
						MonsterChitComponent monsterChit = (MonsterChitComponent) denizenRc;
						battleChit = (MonsterChitComponent) denizenRc;
						vul = monsterChit.getVulnerability();
						str = monsterChit.getStrength();
						sharp = monsterChit.getSharpness();
						armored = monsterChit.isArmored();
						if (checkBothSides()) {
							denizenRc.flip();
							Strength vul2 = monsterChit.getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = monsterChit.getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, monsterChit.getSharpness());
							armored = armored ? armored : monsterChit.isArmored();
							denizenRc.flip();
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
						denizenRc.flip();
						Speed attackSpeed2 = battleChit.getAttackSpeed();
						attackSpeed = attackSpeed.fasterThanOrEqual(attackSpeed2) ? attackSpeed : attackSpeed2;
						Speed moveSpeed2 = battleChit.getMoveSpeed();
						length = Math.max(length, battleChit.getLength());
						isMissile = isMissile ? isMissile : battleChit.isMissile();
						moveSpeed = moveSpeed.fasterThanOrEqual(moveSpeed2) ? moveSpeed : moveSpeed2;
						Speed flySpeed2 = battleChit.getFlySpeed();
						flySpeed = (flySpeed != null && flySpeed.fasterThanOrEqual(flySpeed2)) ? flySpeed : flySpeed2;
						denizenRc.flip();
					}
					
					if (denizenRc.isMonster() && includeWeapons()) {
						MonsterPartChitComponent weapon = ((MonsterChitComponent) denizenRc).getWeapon();
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
				
				SetupCardUtility.resetDenizen(denizen);
				if (removeMarks() ) {
					Quest.GameObjectRemoveQuestMark(denizen, questId);
				}
				regeneratedDenizens++;
				if (numberOfDenizens() != 0 && regeneratedDenizens>=numberOfDenizens()) return;
			}
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		if (requiresMark()) {
			sb.append("Marked ");
		}
		if (getDenizenNameRegex()!=null && !getDenizenNameRegex().isEmpty()) {
			sb.append(getDenizenNameRegex());
		} else {
			sb.append("Denizens");
		}
		sb.append(" are regenerated");
		if (numberOfDenizens()!=0) {
			sb.append(" (max. "+numberOfDenizens()+")");
		}
		if (charactersClearingOnly()) {
			sb.append(" in the characters clearing");
		}
		if (charactersTileOnly()) {
			sb.append(" in the characters tile");
		}
		sb.append(".");
		return sb.toString();
	}
	
	private String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}
	private int numberOfDenizens() {
		return getInt(DENIZEN_AMOUNT);
	}
	private Boolean charactersClearingOnly() {
		return getBoolean(CHARACTERS_CLEARING);
	}
	private Boolean charactersTileOnly() {
		return getBoolean(CHARACTERS_TILE);
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
	private Boolean regenerateHirelings() {
		return getBoolean(REGENERATE_HIRELINGS);
	}
	public boolean requiresMark() {
		return getBoolean(REQ_MARK);
	}
	public boolean removeMarks() {
		return getBoolean(REMOVE_MARKS);
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
	
	public RewardType getRewardType() {
		return RewardType.RegenerateDenizen;
	}
}