package com.robin.magic_realm.RealmBattle;

import java.util.ArrayList;
import java.util.Iterator;

import com.robin.game.objects.*;
import com.robin.magic_realm.components.BattleChit;
import com.robin.magic_realm.components.CharacterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class BattleSummaryWrapper extends GameObjectWrapper {
	
	private static final String ATTACKERS = "as";
	private static final String TARGETS = "ts";
	
	public BattleSummaryWrapper(GameObject go) {
		super(go);
	}
	public String getBlockName() {
		return "_BSUM_";
	}
	public void initFromBattleChits(ArrayList<BattleChit> battleChits) {
		clearBattleSummary();
		
		ArrayList<GameObject> battleChitsAdded = new ArrayList<>();
		for (BattleChit bp : battleChits) {
			if (battleChitsAdded.contains(bp.getGameObject())) continue;
			if (bp instanceof SpellWrapper) {
				SpellWrapper spell = (SpellWrapper)bp;
				for (RealmComponent rc : spell.getTargets()) {
					BattleChit target = (BattleChit)rc;
					addBattleSummaryKill(bp.getGameObject(),target.getGameObject());
				}
			}
			else {
				BattleChit target = (BattleChit)bp.getTarget();
				if (target==null) {
					// this happens with the monster weapons
					RealmComponent monster = RealmComponent.getRealmComponent(bp.getGameObject().getHeldBy());
					target = (BattleChit)monster.getTarget();
				}
				if (target!=null) {
					addBattleSummaryKill(bp.getGameObject(),target.getGameObject());
				}
				if (bp instanceof CharacterChitComponent) {
					BattleChit target2 = (BattleChit) ((CharacterChitComponent)bp).get2ndTarget();
					if (target2!=null) {
						addBattleSummaryKill(bp.getGameObject(),target2.getGameObject());
					}
				}	
			}
			battleChitsAdded.add(bp.getGameObject());
		}
	}
	public BattleSummary getBattleSummary() {
		BattleSummary bs = new BattleSummary();
		ArrayList<String> attackers = getList(ATTACKERS);
		ArrayList<String> targets = getList(TARGETS);
		GameData data = getGameObject().getGameData();
		if (attackers!=null && attackers.size()>0) {
			Iterator<String> k = attackers.iterator();
			Iterator<String> d = targets.iterator();
			while(k.hasNext()) {
				String kid = k.next();
				String did = d.next();
				GameObject kGo = data.getGameObject(Long.valueOf(kid));
				GameObject dGo = data.getGameObject(Long.valueOf(did));
				bs.addAttackerTarget(kGo,dGo);
			}
		}
		return bs;
	}
	private void clearBattleSummary() {
		getGameObject().removeAttributeBlock(getBlockName());
	}
	private void addBattleSummaryKill(GameObject attacker,GameObject target) {
		addListItem(ATTACKERS,attacker.getStringId());
		addListItem(TARGETS,target.getStringId());
	}
}