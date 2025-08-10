package com.robin.magic_realm.RealmBattle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.graphics.StarShape;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.swing.DieRoller;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.Harm;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class BattleSummaryRow implements Comparable<BattleSummaryRow> {
	public static final int WIDTH = 580;
	public static final int HEIGHT = 110;
	
	private static final Font HARM_FONT = new Font("Dialog",Font.BOLD,24);
	private static final Font ROLLTYPE_FONT = new Font("Dialog",Font.BOLD,14);
	private static final Font SUBTITLE_FONT = new Font("Dialog",Font.BOLD,12);
	
	private static final Color TITLE_BACK_COLOR = new Color(255,0,0,30);
	
	private static final int RESOLUTION_MISS = 0;
	private static final int RESOLUTION_HIT = 1;
	private static final int RESOLUTION_KILL = 2;
	private static final int RESOLUTION_NOATTACK = 3;
	private static final int RESOLUTION_NOPARRY = 4;
	private static final int RESOLUTION_PARRY = 5;
	
	private int hitOrder;
	private GameObject attacker;
	private GameObject target;
	
	private int resolution;
	
	private String hitType;
	private String rollType;
	private DieRoller roller;
	private String subtitle;
	private Harm harmApplied;
	
	public BattleSummaryRow(GameObject attacker,GameObject target,int hitOrder) {
		this.attacker = attacker;
		this.target = target;
		this.hitOrder = hitOrder;
		
		CombatWrapper attackerCombat = new CombatWrapper(attacker);
		CombatWrapper combat = attackerCombat;
		String targetId = target.getStringId();
		CombatWrapper tCombat = new CombatWrapper(target);
		GameObject killedBy = tCombat.getKilledBy();
		BattleHorse horse = null;
		if (RealmComponent.getRealmComponent(target).isNative()) {
			horse = RealmComponent.getRealmComponent(target).getHorseIncludeDead();
		}
		
		if (attacker.hasThisAttribute("spell") && !attacker.hasThisAttribute(Constants.EVENT)) {
			SpellWrapper spell = new SpellWrapper(attacker);
			combat = new CombatWrapper(spell.getCaster().getGameObject());
		}
		String has = combat.getHarmApplied(target);
		if (has!=null) {
			harmApplied = Harm.getHarmFromKey(has);
		}
		
		Integer hitTypeId = attackerCombat.getHitType(target);
		if (hitTypeId==null) hitTypeId = BattleModel.NO_ATTACK;
		switch(hitTypeId) {
			case BattleModel.ATTACK_CANCELLED:
				resolution = RESOLUTION_NOATTACK;
				hitType = "'s attack was cancelled";
				break;
			case BattleModel.NO_ATTACK: // this happens when a denizen with no FIGHT value (like an archer) attacks
				resolution = RESOLUTION_NOATTACK;
				hitType = " didn't attack";
				break;
			case BattleModel.MISS:
				resolution = RESOLUTION_MISS;
				hitType = " missed";
				break;
			case BattleModel.INTERCEPT:
				resolution = RESOLUTION_HIT;
				hitType = " intercepted";
				break;
			case BattleModel.UNDERCUT:
				resolution = RESOLUTION_HIT;
				hitType = " undercut";
				break;
			case BattleModel.PARRY_CANCELLED:
				resolution = RESOLUTION_NOPARRY;
				hitType = "'s parry was cancelled";
				break;
			case BattleModel.INTERCEPT_PARRY:
				resolution = RESOLUTION_PARRY;
				hitType = " parried (intercepted)";
				break;
			case BattleModel.UNDERCUT_PARRY:
				resolution = RESOLUTION_PARRY;
				hitType = " parried (undercut)";
				break;
			case BattleModel.CANNOT_PARRY:
				resolution = RESOLUTION_NOPARRY;
				hitType = " could not parry";
				break;
		}
		
		if (horse != null) {
			CombatWrapper hCombat = new CombatWrapper(horse.getGameObject());
			GameObject horseKilledBy = hCombat.getKilledBy();
			if ((horseKilledBy!=null && horseKilledBy.equals(attacker))) {
				hitType += ", killed "+target.getName()+"'s horse";
			}
		}
		
		//ArrayList<GameObject> kills = combat.getAllKills(); // This isn't the right solution.  See #1606 and #1609
		if ((killedBy!=null && killedBy.equals(attacker))) {
				//|| (kills!=null && kills.size()>0 && kills.contains(target))) {
			resolution = RESOLUTION_KILL;
			hitType += " and killed";
		}
		else {
			if (resolution==RESOLUTION_HIT) {
				hitType += ", but did not kill";
			}
		}
		
		rollType = null;
		
		// Check fumble rolls
		ArrayList<String> fumbleRolls = combat.getFumbleRolls();
		if (fumbleRolls!=null && fumbleRolls.size()>0) {
			Iterator<String> r = fumbleRolls.iterator();
			Iterator<String> s = combat.getFumbleRollSubtitles().iterator();
			Iterator<String> t = combat.getFumbleRollTargetIds().iterator();
			rollType = "fumble";
			readLists(targetId,r,s,t);
		}
		
		if (rollType==null) {
			// Check missile rolls
			ArrayList<String> missileRolls = combat.getMissileRolls();
			if (missileRolls!=null && missileRolls.size()>0) {
				Iterator<String> r = missileRolls.iterator();
				Iterator<String> s = combat.getMissileRollSubtitles().iterator();
				Iterator<String> t = combat.getMissileRollTargetIds().iterator();
				rollType = "missile";
				readLists(targetId,r,s,t);
			}
		}
		
	}
	public String toString() {
		StringBuffer title = new StringBuffer();
		if (attacker.hasThisAttribute(RealmComponent.CHARACTER)) {
			title.append("The ");
		}
		title.append(attacker.getName());
		title.append(getResolutionString());
		if (resolution!=RESOLUTION_NOATTACK && resolution!=RESOLUTION_NOPARRY) {
			title.append(" ");
			if (target.hasThisAttribute(RealmComponent.CHARACTER)) {
				title.append("the ");
			}
			title.append(target.getName());
		}
		return title.toString();
	}
	public GameObject getKiller() {
		return attacker;	
	}
	public GameObject getKilled() {
		return target;
	}
	public int getKillOrder() {
		return hitOrder;
	}
	public DieRoller getRoller() {
		return roller;
	}
	public String getSubtitle() {
		return subtitle;
	}
	private void readLists(String id,Iterator<String> r,Iterator<String> s,Iterator<String> t) {
		while(t.hasNext()) {
			String rollString = r.next();
			String subtitleString = s.next();
			String targetId = t.next();
			if (id.equals(targetId)) {
				roller = new DieRoller(rollString,25,6);
				subtitle = subtitleString;
			}
		}
	}
	public int compareTo(BattleSummaryRow row) {
		return row.hitOrder;
	}
	
	private int draw(Graphics2D g,RealmComponent rc,int x,int y,boolean attacker) {
		ChitComponent chit = null;
		if ((resolution==RESOLUTION_HIT || resolution==RESOLUTION_PARRY || resolution==RESOLUTION_MISS) && rc.isChit()) {
			chit = (ChitComponent)rc;
			chit.setIgnoreDamage(true);
		}
		
		ImageIcon icon = rc.getIcon();
		int h = icon.getIconHeight();
		int yoff = (ChitComponent.T_CHIT_SIZE - h)/2;
		g.drawImage(icon.getImage(),x+40,y+yoff,null);
		if (chit!=null) {
			chit.setIgnoreDamage(false);
		}
		
		// Draw character attacks/maneuvers here
		if (rc.isCharacter()) {
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			// If transformed, show monster values
			if (character.isTransmorphed()) {
				GameObject go = character.getTransmorph();
				RealmComponent tm = RealmComponent.getRealmComponent(go);
				if (tm.isMonster()) {
					MonsterChitComponent monster = (MonsterChitComponent)tm;
					if (attacker) {
						g.drawImage(monster.getFightChit().getImage(),x,y+yoff,null);
					}
					else {
						g.drawImage(monster.getMoveChit().getImage(),x,y+yoff,null);
					}
				}
			}
			else {
				CharacterChitComponent charChit = (CharacterChitComponent)rc;
				if (attacker) {
					RealmComponent weapon = null;
					ArrayList<GameObject> wgos = charChit.getActiveWeaponsObjects();
					if (wgos!=null) {
						for (GameObject wgo : wgos) {
							weapon = RealmComponent.getRealmComponent(wgo);
							ImageIcon wicon = weapon.getIcon();
							yoff = ChitComponent.T_CHIT_SIZE - wicon.getIconHeight();
							g.drawImage(wicon.getImage(),x,y+yoff,null);
						}
					}
					
					RealmComponent attackChit = charChit.getAttackChit();
					if (attackChit == null) {
						GameObject transmorph = character.getTransmorph();
						if (transmorph == null) {
							ArrayList<RealmComponent> attackChits = BattleUtility.findFightComponentsWithCombatBox(character.getFightSpeedOptions(new Speed(), true));
							if (attackChits != null && attackChits.size() == 1) {
								attackChit = attackChits.get(0);
							}
						}
					}
					
					if (attackChit!=null) {
						if (attackChit.isCard()) {
							g.drawImage(attackChit.getMediumImage(),x,y,null);
						}
						else {
							g.drawImage(attackChit.getImage(),x,y,null);
						}
					}
				}
				else {
					RealmComponent moveChit = charChit.getManeuverChit();
					if (moveChit!=null) {
						if (moveChit.isCard()) {
							g.drawImage(moveChit.getMediumImage(),x,y,null);
						}
						else {
							g.drawImage(moveChit.getImage(),x,y,null);
						}
						RealmComponent maneuverChit = charChit.getManeuverChit(false);
						if (maneuverChit!=null && !maneuverChit.equals(moveChit)) {
							if (maneuverChit.isCard()) {
								g.drawImage(maneuverChit.getMediumImage(),x,y,null);
							}
							else {
								g.drawImage(maneuverChit.getImage(),x,y,null);
							}
						}
					}
				}
			}
		}
		
		return h;
	}
	private String getResolutionString() {
		return hitType;
	}

	/*
	 * Kill is a solid RED arrow
	 * Hit (no kill) is a red arrow outline
	 * Miss is a blue arrow outline
	 */
	public void draw(Graphics2D g,int x,int y,Color background) {
		g.setColor(background);
		g.fillRect(x,y,WIDTH,HEIGHT);
		
		x+=5;
		y+=5;
		
		// Draw the title
		g.setColor(TITLE_BACK_COLOR);
		g.fillRect(x-5,y-5,170,35);
		
		TextType tt = new TextType(toString(),170,"TITLE");
		tt.draw(g,x-5,y-5,Alignment.Left);
		
		// Draw Die Roll
		if (roller!=null) {
			g.setColor(Color.black);
			g.setFont(ROLLTYPE_FONT);
			g.drawString(StringUtilities.capitalize(rollType)+" Roll",x,y+45);
			g.drawImage(roller.getImage(),x,y+45,null);
			g.setFont(SUBTITLE_FONT);
			g.drawString(subtitle,x+10,y+95);
		}
		
		x += 170;
		
		// Draw Attacker
		RealmComponent attackerRc = RealmComponent.getRealmComponent(attacker);
		draw(g,attackerRc,x,y,true);
		x += 150;
		
		// Draw Resolution with Harm
		Polygon poly = new Polygon();
		poly.addPoint(x,y+30);
		poly.addPoint(x+50,y+30);
		poly.addPoint(x+50,y);
		poly.addPoint(x+100,y+50);
		poly.addPoint(x+50,y+100);
		poly.addPoint(x+50,y+70);
		poly.addPoint(x,y+70);
		poly.addPoint(x,y+30);
		
		switch(resolution) {
			case RESOLUTION_MISS:
				g.setColor(Color.blue);
				g.setStroke(Constants.THICK_STROKE);
				g.draw(poly);
				g.setFont(HARM_FONT);
				g.drawString("MISS",x+10,y+58);
				break;
			case RESOLUTION_HIT:
				g.setColor(Color.red);
				g.setStroke(Constants.THICK_STROKE);
				g.draw(poly);
				break;
			case RESOLUTION_PARRY:
				g.setColor(Color.orange);
				g.setStroke(Constants.THICK_STROKE);
				g.draw(poly);
				g.setFont(HARM_FONT);
				g.drawString("PARRY",x+10,y+58);
				break;
			case RESOLUTION_KILL:
				g.setColor(Color.red);
				g.fill(poly);
				break;
			case RESOLUTION_NOATTACK:
			case RESOLUTION_NOPARRY:
				break;
		}
		
		if (harmApplied!=null) {
			g.setColor(Color.black);
			g.setFont(HARM_FONT);
			int s = x+40;
			s -= harmApplied.getSharpness()*7;
			g.drawString(harmApplied.getStrength().getChar(),s,y+58);
			s += 22;
			if ("M".equals(harmApplied.getStrength().toString())) {
				s+=3;
			}
			for (int i = 0; i < harmApplied.getSharpness(); i++) {
				StarShape star = new StarShape(s, y+50, 5, 7);
				g.fill(star);
				s += 10;
			}
		}
		x += 110;
		
		// Draw Target
		RealmComponent targetRc = RealmComponent.getRealmComponent(target);
		draw(g,targetRc,x,y,false);
		x += 150;
	}
	
	public static void main(String[] args) {
		RealmUtility.setupTextType();
		RealmLoader loader = new RealmLoader();
		GameData data = loader.getData();
		GameObject attacker = data.getGameObjectByName("Captain");
		GameObject target = data.getGameObjectByName("Rogue 1");
		GameObject anotherAttacker = data.getGameObjectByName("Lancer 1");
		CombatWrapper combat = new CombatWrapper(target);
		combat.setKilledBy(attacker);
		BattleSummaryRow row;
		BattleSummaryIcon icon = new BattleSummaryIcon();
		
		row = new BattleSummaryRow(anotherAttacker,target,0);
		row.resolution = RESOLUTION_HIT;
		row.roller = new DieRoller("Red|3:White|2&1",25,6);
		row.subtitle = "Boom";
		row.rollType = "Fumble";
		row.harmApplied = Harm.getHarmFromKey("neg+2");
		icon.addRow(row);
		
		row = new BattleSummaryRow(attacker,target,0);
		row.resolution = RESOLUTION_KILL;
		row.roller = new DieRoller("Red|3:White|2&1",25,6);
		row.subtitle = "Boom";
		row.rollType = "Fumble";
		row.harmApplied = Harm.getHarmFromKey("neg+2");
		icon.addRow(row);
		
		row = new BattleSummaryRow(target,attacker,0);
		row.resolution = RESOLUTION_NOATTACK;
		row.roller = new DieRoller("Red|3:White|2&1",25,6);
		row.subtitle = "Boom";
		row.rollType = "Fumble";
		row.harmApplied = null;//Harm.getHarmFromKey("neg+2");
		icon.addRow(row);
		
		JOptionPane.showMessageDialog(new JFrame(),new JLabel(icon));
		System.exit(0);
	}
}