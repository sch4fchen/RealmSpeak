package com.robin.magic_realm.components.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class DuelEffect implements ISpellEffect {

	private static String DUELLING = "duelling";
	
	@Override
	public void apply(SpellEffectContext context) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(context.getGameData());
		
		ArrayList<RealmComponent> targets = context.Spell.getTargets();
		if (targets.size() != 2 || context.Spell.getExtraIdentifier() == DUELLING) return;
		
		targets.get(0).clearTargets();
		targets.get(1).clearTargets();
		targets.get(0).setTarget(targets.get(1));
		targets.get(1).setTarget(targets.get(0));
		
		CombatWrapper combat0 = new CombatWrapper(targets.get(0).getGameObject());
		combat0.setSheetOwner(true);
		CombatWrapper combat1 = new CombatWrapper(targets.get(1).getGameObject());
		combat1.setSheetOwnerId(targets.get(0));
		if (hostPrefs.hasPref(Constants.SR_COMBAT)) {
			if (combat0.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_ATTACK)) {
				ArrayList<String> boxes = combat0.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_ATTACK);
				String box = boxes.get(RandomNumber.getRandom(boxes.size()));
				combat0.setCombatBoxAttack(Integer.parseInt(box));
			}
			else {
				combat0.setCombatBoxAttack(RandomNumber.getRandom(3)+1);
			}
			if (combat0.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_DEFENSE)) {
				ArrayList<String> boxes = combat0.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_DEFENSE);
				String box = boxes.get(RandomNumber.getRandom(boxes.size()));
				combat0.setCombatBoxAttack(Integer.parseInt(box));
			} else {
				combat0.setCombatBoxDefense(RandomNumber.getRandom(3)+1);
			}
			if (combat1.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_ATTACK)) {
				ArrayList<String> boxes = combat1.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_ATTACK);
				String box = boxes.get(RandomNumber.getRandom(boxes.size()));
				combat1.setCombatBoxAttack(Integer.parseInt(box));
			}
			else {
				combat1.setCombatBoxAttack(RandomNumber.getRandom(3)+1);
			}
			if (combat1.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_DEFENSE)) {
				ArrayList<String> boxes = combat1.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_DEFENSE);
				String box = boxes.get(RandomNumber.getRandom(boxes.size()));
				combat1.setCombatBoxAttack(Integer.parseInt(box));
			} else {
				combat1.setCombatBoxDefense(RandomNumber.getRandom(3)+1);
			}
		}
		else {
			if (combat0.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_ATTACK) && combat0.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_DEFENSE)) {
				ArrayList<String> boxesA = combat0.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_ATTACK);
				ArrayList<String> boxesD = combat0.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_ATTACK);
				List<String> result = boxesA.stream()
						  .distinct()
						  .filter(boxesD::contains)
						  .collect(Collectors.toList());
				String box = result.get(RandomNumber.getRandom(result.size()));
				combat0.setCombatBoxAttack(Integer.parseInt(box));
				combat0.setCombatBoxDefense(Integer.parseInt(box));
			}
			else {
				int random1 = RandomNumber.getRandom(3)+1;
				combat0.setCombatBoxAttack(random1);
				combat0.setCombatBoxDefense(random1);
			}
			if (combat1.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_ATTACK) && combat1.getGameObject().hasThisAttribute(Constants.SPIDER_WEB_BOXES_DEFENSE)) {
				ArrayList<String> boxesA = combat1.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_ATTACK);
				ArrayList<String> boxesD = combat1.getGameObject().getThisAttributeList(Constants.SPIDER_WEB_BOXES_ATTACK);
				List<String> result = boxesA.stream()
						  .distinct()
						  .filter(boxesD::contains)
						  .collect(Collectors.toList());
				String box = result.get(RandomNumber.getRandom(result.size()));
				combat1.setCombatBoxAttack(Integer.parseInt(box));
				combat1.setCombatBoxDefense(Integer.parseInt(box));
			} else {
				int random2 = RandomNumber.getRandom(3)+1;
				combat1.setCombatBoxAttack(random2);
				combat1.setCombatBoxDefense(random2);
			}
		}
		
		for (RealmComponent target : targets) {
			if (target instanceof MonsterChitComponent) {
				MonsterPartChitComponent weapon = ((MonsterChitComponent) target).getWeapon();
				if (weapon != null) {
					CombatWrapper combat = new CombatWrapper(target.getGameObject());
					CombatWrapper combatWeapon = new CombatWrapper(weapon.getGameObject());
					combatWeapon.setCombatBoxAttack(RandomNumber.getRandom(3)+1);
					while (combat.getCombatBoxAttack() == combatWeapon.getCombatBoxAttack()) {
						combatWeapon.setCombatBoxAttack(RandomNumber.getRandom(3)+1);
					}
				}
			}
		}
		context.Spell.setExtraIdentifier(DUELLING);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
