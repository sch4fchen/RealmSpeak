package com.robin.magic_realm.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.*;

public class MonsterMoveChitComponent extends MonsterActionChitComponent {
	public MonsterMoveChitComponent(GameObject obj) {
		super(obj);
	}

	public String getName() {
		return "monster_move_chit";
	}

	public Integer getLength() {
		return null; // Not a FIGHT chit!
	}

	public Strength getMoveStrength() {
		return monster.getVulnerability();
	}
	
	public Speed getMoveSpeed() {
		return monster.getMoveSpeed();
	}
	
	public Speed getFlySpeed() {
		return monster.getFlySpeed();
	}

	public boolean hasAnAttack() {
		return false;
	}
	
	public Speed getAttackSpeed() {
		return null; // Not a FIGHT chit!
	}

	public Harm getHarm() {
		return null; // Not a FIGHT chit!
	}

	public String getMagicType() {
		return null; // Not a FIGHT chit!
	}
	
	public String getAttackSpell() {
		return null; // Not a FIGHT chit!
	}

	public int getManeuverCombatBox() {
		return monster.getManeuverCombatBox(); // oops.... can't separate these, can I!!
	}

	public int getAttackCombatBox() {
		return 0; // Not a FIGHT chit!
	}
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		int n = (getChitSize()>>1)+5;
		monster.paintMoveValues(g,n,n+5);
		
		TextType tt = new TextType("MOVE",S_CHIT_SIZE,"BOLD");
		tt.draw(g1,0,5,Alignment.Center);
	}
}