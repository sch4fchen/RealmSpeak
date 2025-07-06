package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.BattleHorse;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.NativeChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class SpiderWebEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		RealmComponent target = context.Target;
		CombatWrapper cw = new CombatWrapper(target.getGameObject());
		ArrayList<Integer> attackBoxes = new ArrayList<>();
		ArrayList<Integer> defenseBoxes = new ArrayList<>();
		attackBoxes.add(cw.getCombatBoxAttack());
		defenseBoxes.add(cw.getCombatBoxDefense());
		if (target.isDenizen()) {
			if (target.isMonster()) {
				MonsterChitComponent monster = ((MonsterChitComponent)target);
				MonsterPartChitComponent weapon = monster.getWeapon();
				if (weapon!=null) {
					CombatWrapper cwWeapon = new CombatWrapper(weapon.getGameObject());
					if (!attackBoxes.contains(cwWeapon.getCombatBoxAttack())) {
						attackBoxes.add(cwWeapon.getCombatBoxAttack());
					}
					if (!defenseBoxes.contains(cwWeapon.getCombatBoxDefense())) {
						defenseBoxes.add(cwWeapon.getCombatBoxDefense());
					}
				}
				BattleHorse horse = monster.getHorse();
				if (horse!=null) {
					if (!attackBoxes.contains(horse.getAttackCombatBox())) {
						attackBoxes.add(horse.getAttackCombatBox());
					}
					if (!defenseBoxes.contains(horse.getManeuverCombatBox())) {
						defenseBoxes.add(horse.getManeuverCombatBox());
					}
				}
			}
			else if (target.isNative()) {
				NativeChitComponent nativeDenizen = ((NativeChitComponent)target);
				BattleHorse horse = nativeDenizen.getHorse();
				if (horse!=null) {
					if (!attackBoxes.contains(horse.getAttackCombatBox())) {
						attackBoxes.add(horse.getAttackCombatBox());
					}
					if (!defenseBoxes.contains(horse.getManeuverCombatBox())) {
						defenseBoxes.add(horse.getManeuverCombatBox());
					}
				}
			}
		}
		else if (target.isCharacter()) {
			CharacterWrapper character = new CharacterWrapper(target.getGameObject());
			for (CharacterActionChitComponent chit : character.getActiveChits()) {
				CombatWrapper cwChit = new CombatWrapper(chit.getGameObject());
				if (!attackBoxes.contains(cwChit.getCombatBoxAttack())) {
					attackBoxes.add(cwChit.getCombatBoxAttack());
				}
				if (!defenseBoxes.contains(cwChit.getCombatBoxDefense())) {
					defenseBoxes.add(cwChit.getCombatBoxDefense());
				}
			}
			for (GameObject item :character.getActiveInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(item);
				if (rc.isHorse() || rc.isNativeHorse()) {
					CombatWrapper itemChit = new CombatWrapper(item);
					if (!attackBoxes.contains(itemChit.getCombatBoxAttack())) {
						attackBoxes.add(itemChit.getCombatBoxAttack());
					}
					if (!defenseBoxes.contains(itemChit.getCombatBoxDefense())) {
						defenseBoxes.add(itemChit.getCombatBoxDefense());
					}
				}
			}
		}
		if (attackBoxes.isEmpty() || (attackBoxes.size()==1 && attackBoxes.get(0)==0)) {
			int randomBox = RandomNumber.getRandom(3)+1;
			target.getGameObject().addThisAttributeListItem(Constants.SPIDER_WEB_BOXES_ATTACK,Integer.toString(randomBox));
		}
		else {
			for (Integer box : attackBoxes) {
				if (box!=0) {
					target.getGameObject().addThisAttributeListItem(Constants.SPIDER_WEB_BOXES_ATTACK,box.toString());
				}
			}
		}
		if (defenseBoxes.isEmpty() || (defenseBoxes.size()==1 && defenseBoxes.get(0)==0)) {
			int randomBox = RandomNumber.getRandom(3)+1;
			target.getGameObject().addThisAttributeListItem(Constants.SPIDER_WEB_BOXES_DEFENSE,Integer.toString(randomBox));
		}
		else {
			for (Integer box : defenseBoxes) {
				if (box!=0) {
					target.getGameObject().addThisAttributeListItem(Constants.SPIDER_WEB_BOXES_DEFENSE,box.toString());
				}
			}
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		GameObject target = context.Target.getGameObject();
		target.removeThisAttribute(Constants.SPIDER_WEB_BOXES_ATTACK);
		target.removeThisAttribute(Constants.SPIDER_WEB_BOXES_DEFENSE);
	}

}
