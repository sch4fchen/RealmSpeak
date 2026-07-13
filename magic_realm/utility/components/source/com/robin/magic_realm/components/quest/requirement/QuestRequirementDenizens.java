package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
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
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementDenizens extends QuestRequirement {
	
	public static final String DENIZEN_REGEX = "_regex";
	public static final String AMOUNT = "_amount";
	public static final String SAME_TILE = "_same_tile";
	public static final String SAME_CLEARING = "_same_clearing";
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
	public static final String REQ_MARK = "_requires_mark";
	public static final String NATIVES_ONLY = "_natives_only";
	public static final String MONSTERS_ONLY = "_monsters_only";
	public static final String IGNORE_HIRELINGS = "_ignore_hirelings";
	public static final String IGNORE_CONTROLLED_DENIZENS = "_ignore_controlled";
	public static final String IGNORE_COMPANIONS = "_ignore_companions";
	public static final String IGNORE_SUMMONED = "_ignore_summoned";

	public QuestRequirementDenizens(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		ArrayList<RealmComponent> denizens = new ArrayList<>();
		int amount = 0;
		Pattern pattern = Pattern.compile(getRegExFilter());
		if (sameClearing()) {
			TileLocation loc = character.getCurrentLocation();
			if (loc==null || !loc.hasClearing()) return false;
			for (RealmComponent component : loc.clearing.getClearingComponents()) {
				if (component.isMonster() || component.isNative()) {
					denizens.add(component);
				}
			}
		} else if (sameTile()) {
			TileComponent tile = character.getCurrentLocation().tile;
			for (ClearingDetail cl : tile.getClearings()) {
				for (RealmComponent component : cl.getClearingComponents()) {
					if (component.isMonster() || component.isNative()) {
						denizens.add(component);
					}
				}
			}
		} else {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			for (GameObject go : pool.find("denizen")) {
				denizens.add(RealmComponent.getRealmComponent(go));
			}
		}
		
		String questId = getParentQuest().getGameObject().getStringId();
		for (RealmComponent denizen : denizens) {
			if (ignoreHirelings() && denizen.isHireling()) continue;
			if (ignoreControlledDenizens() && (denizen.isControlledNative() || denizen.isControlledMonster())) continue;
			if (ignoreCompanions() && denizen.isCompanion()) continue;
			if (ignoreSummoned() && denizen.isSummoned()) continue;
			if (nativesOnly() && !denizen.isNative()) continue;
			if (monstersOnly() && !denizen.isMonster()) continue;
			if (requiresMark() && !Quest.GameObjectHasQuestMark(denizen.getGameObject(),questId)) continue;
			if (getRegExFilter().isEmpty() || pattern.matcher(denizen.getGameObject().getName()).find()) {
				if (checkStats()) {
					Strength vul = new Strength();
					Strength str = new Strength();
					int sharp = 0;
					Boolean armored = false;
					BattleChit battleChit = null;
					if (denizen.isNative()) {
						NativeChitComponent nativeChit = (NativeChitComponent) denizen;
						battleChit = (NativeChitComponent) denizen;
						vul = nativeChit.getVulnerability();
						str = nativeChit.getStrength();
						sharp = nativeChit.getSharpness();
						armored = nativeChit.isArmored();
						if (checkBothSides()) {
							denizen.flip();
							Strength vul2 = nativeChit.getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = nativeChit.getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, nativeChit.getSharpness());
							armored = armored ? armored : nativeChit.isArmored();
							denizen.flip();
						}
					}
					else if (denizen.isMonster()) {
						MonsterChitComponent monsterChit = (MonsterChitComponent) denizen;
						battleChit = (MonsterChitComponent) denizen;
						vul = monsterChit.getVulnerability();
						str = monsterChit.getStrength();
						sharp = monsterChit.getSharpness();
						armored = monsterChit.isArmored();
						if (checkBothSides()) {
							denizen.flip();
							Strength vul2 = monsterChit.getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = monsterChit.getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, monsterChit.getSharpness());
							armored = armored ? armored : monsterChit.isArmored();
							denizen.flip();
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
						denizen.flip();
						Speed attackSpeed2 = battleChit.getAttackSpeed();
						attackSpeed = attackSpeed.fasterThanOrEqual(attackSpeed2) ? attackSpeed : attackSpeed2;
						Speed moveSpeed2 = battleChit.getMoveSpeed();
						length = Math.max(length, battleChit.getLength());
						isMissile = isMissile ? isMissile : battleChit.isMissile();
						moveSpeed = moveSpeed.fasterThanOrEqual(moveSpeed2) ? moveSpeed : moveSpeed2;
						Speed flySpeed2 = battleChit.getFlySpeed();
						flySpeed = (flySpeed != null && flySpeed.fasterThanOrEqual(flySpeed2)) ? flySpeed : flySpeed2;
						denizen.flip();
					}
					
					if (denizen.isMonster() && includeWeapons()) {
						MonsterPartChitComponent weapon = ((MonsterChitComponent) denizen).getWeapon();
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
				amount++;
			}
		}
		return amount >= getAmount();
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("There must be "+getAmount()+" ");
		if (requiresMark()) {
			sb.append("marked ");
		}
		sb.append("denizens");
		if (sameClearing()) {
			sb.append(" in characters clearing");
		} else if (sameTile()) {
			sb.append(" in characters tile");
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Denizens;
	}
	
	private String getRegExFilter() {
		return getString(DENIZEN_REGEX).trim();
	}
	private Boolean sameTile() {
		return getBoolean(SAME_TILE);
	}
	private Boolean sameClearing() {
		return getBoolean(SAME_CLEARING);
	}
	private int getAmount() {
		return getInt(AMOUNT);
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
	private Boolean requiresMark() {
		return getBoolean(REQ_MARK);
	}
	private Boolean nativesOnly() {
		return getBoolean(NATIVES_ONLY);
	}
	private Boolean monstersOnly() {
		return getBoolean(MONSTERS_ONLY);
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