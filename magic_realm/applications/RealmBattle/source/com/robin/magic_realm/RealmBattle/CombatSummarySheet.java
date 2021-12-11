/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmBattle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class CombatSummarySheet extends JLabel {
	private static final String[] COMBAT_STAGES  = {
		"Prebattle",
		"Luring",
		"Random Assignment",
		"Deploy/Charge",
		"Encounter",
		"Assign Targets",
		"Positioning",
		"Change Tactics (rare)",
		"Resolving",
		"Fatigue",
		"Disengage",
	};
	
	private ArrayList<CharacterWrapper> characters;
	private BattleModel battleModel;
	
	public CombatSummarySheet(BattleModel battleModel) {
		super("");
		this.battleModel = battleModel;
		ArrayList<CharacterWrapper> characters = new ArrayList<>();
		for (RealmComponent rc : battleModel.getAllParticipatingCharacters()) {
			characters.add(new CharacterWrapper(rc.getGameObject()));
		}
		this.characters = characters;
		Collections.sort(characters,new Comparator<CharacterWrapper>() {
			public int compare(CharacterWrapper c1,CharacterWrapper c2) {
				int ret = 0;
				ret = c1.getCombatPlayOrder()-c2.getCombatPlayOrder();
				return ret;
			}
		});
		int numberOfCharacters = characters.size();
		int numberOfParticipants = battleModel.getAllBattleParticipants(true).size();
		int totalHeight = 250 + numberOfCharacters*150 + (numberOfParticipants-numberOfCharacters)*50;
		int width = 590;
		BufferedImage bi = new BufferedImage(width,totalHeight,BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0,0,width,totalHeight);
		setIcon(new ImageIcon(bi));
	}
	private Font STAGE_FONT = new Font("Dialog",Font.BOLD,12);
	private Color STAGE_SECTION_COLOR = new Color(200,255,200,150);
	private Color NAME_SECTION_COLOR = new Color(200,200,255,150);
	private Color NUMBER_BOX_COLOR = new Color(0,0,0,100);
	private Stroke MARK_STROKE = new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		g1.setFont(STAGE_FONT);
		Graphics2D g = (Graphics2D)g1;
		AffineTransform normal = g.getTransform();
		
		int x,y,s;
		
		// Draw name sections
		g.setColor(NAME_SECTION_COLOR);
		x = 5;
		y = 182;
		s = 145 + (COMBAT_STAGES.length*30) - 20;
		for (int i=0;i<characters.size();i++) {
			g.fillRect(x,y-20,s,25);
			y += 30;
		}
		
		// Draw names
		g.setColor(Color.black);
		x = 10;
		y = 180;
		for (CharacterWrapper character : characters) {
			String name = character.getGameObject().getName();
			g.drawString(name,x,y);
			y += 30;
		}
		int listBottom = y;
		
		// Draw stage sections
		g.setColor(STAGE_SECTION_COLOR);
		x = 150;
		y = 5;
		s = 150 + (characters.size()*30);
		for (int i=0;i<COMBAT_STAGES.length;i++) {
			g.fillRect(x-18,y,25,s);
			x+=30;
		}
		
		// Draw Combat Stage Titles
		AffineTransform rotated = new AffineTransform(normal);
		rotated.rotate(Math.toRadians(-90),150,150);
		g.setTransform(rotated);
		g.setColor(Color.black);
		x = 150;
		y = 150;
		for (int i=0;i<COMBAT_STAGES.length;i++) {
			g.drawString(COMBAT_STAGES[i],x,y);
			y += 30;
		}
		g.setTransform(normal);
		Stroke normalStroke = g.getStroke();
		
		for (int r=0;r<characters.size();r++) {
			CharacterWrapper character = characters.get(r);
			boolean active = true;
			int stage = character.getCombatStatus();
			if (stage>Constants.COMBAT_WAIT) {
				stage -= Constants.COMBAT_WAIT;
				active = false;
			}
			for (int c=0;c<COMBAT_STAGES.length;c++) {
				int n = ((c*characters.size())+r)+1;
				int stageCompare = c+1;
				Rectangle rect = getRectangleForPosition(r,c);
				
				g.setColor(NUMBER_BOX_COLOR);
				GraphicsUtil.drawCenteredString(g,rect.x,rect.y,rect.width,rect.height,String.valueOf(n));
				
				g.setColor(Color.black);
				g.draw(rect);
				
				g.setStroke(MARK_STROKE);				
				if (stage==stageCompare && active) {
					g.setColor(Color.red);
					rect.x += 2;
					rect.y += 2;
					rect.width -= 4;
					rect.height -= 4;
					g.draw(rect);
				}
				else if (stage>stageCompare) {
					rect.x += 2;
					rect.y += 2;
					rect.width -= 4;
					rect.height -= 4;
					
					g.drawLine(rect.x,rect.y,rect.x+rect.width,rect.y+rect.height);
				}
				g.setStroke(normalStroke);
			}
		}
		
		// List battling natives for each character
		x = 5;
		y = listBottom;
		g.setColor(Color.black);
		for (CharacterWrapper character : characters) {
			for (String groupName : character.getBattlingNativeGroups()) {
				StringBuffer sb = new StringBuffer();
				sb.append("The ");
				sb.append(StringUtilities.capitalize(groupName));
				sb.append(" are battling the ");
				sb.append(character.getGameObject().getName());
				sb.append(".");
				
				g.drawString(sb.toString(),x,y+20);
				y += 20;
			}
		}
		y += 40;
		
		// Battle overview
		g.drawString("DEFENDER",x+10,y);
		g.drawString("ATTACKERS",x+120,y);
		y -= 45;
		for (RealmComponent battleParticipant : battleModel.getAllBattleParticipants(true)) {
			CombatWrapper cr = new CombatWrapper(battleParticipant.getGameObject());
			if (!battleParticipant.isCharacter() && !battleParticipant.isHiredOrControlled() && cr.getAttackers().size() == 0) continue;
			
			y += 90;
			g.drawImage(battleParticipant.getImage(),x,y-40,80,80,null);
			int xAttacker = x+30;
			int attackerCount = 0;
			for (GameObject attacker : cr.getAttackers()) {
				if (attackerCount != 0 && attackerCount % 5 == 0) {
					y += 90;
					xAttacker = x+30;
				}
				xAttacker += 90;
				RealmComponent attackerRc = RealmComponent.getRealmComponent(attacker);
				g.drawImage(attackerRc.getImage(),xAttacker,y-40,80,80,null);
				attackerCount += 1;
			}
		}
		y += 80;
		
		int yUnassignedHeadline = y;
		y += 50;
		int xUnassigned = x;
		int unassignedCount = 0;
		for (RealmComponent battleParticipant : battleModel.getAllBattleParticipants(true)) {
			if (unassignedCount != 0 && unassignedCount % 6 == 0) {
				y += 90;
				xUnassigned = x;
			}
			CombatWrapper cr = new CombatWrapper(battleParticipant.getGameObject());
			if (!battleParticipant.isCharacter() && !battleParticipant.isHiredOrControlled() && cr.getAttackerCount() == 0 && !battleParticipant.hasTarget()) {
				g.drawImage(battleParticipant.getImage(),xUnassigned,y-40,80,80,null);
				xUnassigned += 90;
				unassignedCount +=1;
			}
		}
		if (unassignedCount != 0) {
			g.drawString("UNASSIGNED",x+10,yUnassignedHeadline);
		}
	}
	private static Rectangle getRectangleForPosition(int row,int col) {
		int x = (col * 30) + 132;
		int y = (row * 30) + 162;
		return new Rectangle(x,y,24,24);
	}
	
//	public static void main(String[] args) {
//		ArrayList list = new ArrayList();
//		list.add("White Knight");
//		list.add("Captain");
//		list.add("Swordsman");
//		JOptionPane.showMessageDialog(null,new CombatSummarySheet(list));
//	}
}