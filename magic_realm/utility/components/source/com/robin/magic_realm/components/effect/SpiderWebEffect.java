package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
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
					attackBoxes.add(weapon.getAttackCombatBox());
					defenseBoxes.add(weapon.getManeuverCombatBox());
				}
				BattleHorse horse = monster.getHorse();
				if (horse!=null) {
					attackBoxes.add(horse.getAttackCombatBox());
					defenseBoxes.add(horse.getManeuverCombatBox());
				}
			}
			else if (target.isNative()) {
				NativeChitComponent nativeDenizen = ((NativeChitComponent)target);
				BattleHorse horse = nativeDenizen.getHorse();
				if (horse!=null) {
					attackBoxes.add(horse.getAttackCombatBox());
					defenseBoxes.add(horse.getManeuverCombatBox());
				}
			}
		}
		else if (target.isCharacter()) {
			CharacterWrapper character = new CharacterWrapper(target.getGameObject());
			for (CharacterActionChitComponent chit : character.getActiveChits()) {
				CombatWrapper cwChit = new CombatWrapper(chit.getGameObject());
				attackBoxes.add(cwChit.getCombatBoxAttack());
				defenseBoxes.add(cwChit.getCombatBoxDefense());
			}
		}
		for (Integer box : attackBoxes) {
			if (box!=0) {
				target.getGameObject().addThisAttributeListItem(Constants.SPIDER_WEB_ATTACK_BOXES,box.toString());
			}
		}
		for (Integer box : defenseBoxes) {
			if (box!=0) {
				target.getGameObject().addThisAttributeListItem(Constants.SPIDER_WEB_DEFENSE_BOXES,box.toString());
			}
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		GameObject target = context.Target.getGameObject();
		target.removeThisAttribute(Constants.SPIDER_WEB_ATTACK_BOXES);
		target.removeThisAttribute(Constants.SPIDER_WEB_DEFENSE_BOXES);
	}

}
