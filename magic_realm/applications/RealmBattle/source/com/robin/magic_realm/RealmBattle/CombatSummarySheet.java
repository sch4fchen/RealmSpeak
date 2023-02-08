package com.robin.magic_realm.RealmBattle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.BattleHorse;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class CombatSummarySheet extends JPanel {
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
	private CombatFrame combatFrame;
	
	public CombatSummarySheet(CombatFrame combatFrame) {
		super();
		this.battleModel = combatFrame.getBattleModel();
		this.combatFrame = combatFrame;
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
		this.setLayout(null);
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
		g.drawString("DEFENDER",x+90,y);
		g.drawString("ATTACKERS",x+200,y);
		y -= 45;
		int row=0;
		for (RealmComponent battleParticipant : combatFrame.getAllParticipants()) {
			CombatWrapper cr = new CombatWrapper(battleParticipant.getGameObject());
			row+=1;
			y += 90;
			g.drawImage(battleParticipant.getImage(),x+80,y-40,80,80,null);		
			JButton chartButton = new JButton("Sheet");
			final int rcRow = row;
			chartButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					combatFrame.participantTable.setRowSelectionInterval(rcRow,rcRow);
				}
			});
			chartButton.setBounds(x,y-10,65,20);
			add(chartButton);
			
			RealmComponent owner = battleParticipant.getOwner();
			boolean isOwnedByActive = (owner!=null && owner.equals(combatFrame.getActiveParticipant()));
			if (combatFrame.getActionState() == Constants.COMBAT_LURE && CombatFrame.isInteractiveFrame()) {
				if (combatFrame.areDenizensToLure() && (combatFrame.getActiveParticipant() ==  battleParticipant || isOwnedByActive) && !battleParticipant.isMistLike() ) {
					JButton lureButton = new JButton("Lure");
					lureButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							if (battleParticipant.isCharacter()) {
								combatFrame.lureDenizens(battleParticipant,0,true);
							} else {
								DenizenCombatSheet denizenSheet = new DenizenCombatSheet(combatFrame,combatFrame.getBattleModel(),battleParticipant,false,null);
								if (denizenSheet.canLureMoreDenizens()) {
									DenizenCombatSheet.lureDenizens(combatFrame,battleParticipant);
								}
								else {
									DenizenCombatSheet.showDialogOnlySingleDenizenCanBeLured(combatFrame);
								}
							}
						}
					});
					lureButton.setBounds(x,y-35,65,20);
					add(lureButton);
				}
				if (!battleParticipant.isCharacter() && DenizenCombatSheet.denizenCanFlip(battleParticipant)) {
					JButton flip = new JButton("Flip");
					flip.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							battleParticipant.flip();
							combatFrame.repaintCombatSheetPanel();
						}
					});
					flip.setBounds(x,y+12,65,12);
					add(flip);	
				}
				if (!battleParticipant.isCharacter() && battleParticipant.hasHorse()) {
					JButton flipHorse = new JButton("FlipSteed");
					flipHorse.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							BattleHorse horse = battleParticipant.getHorse();
							horse.flip();
						}
					});
					flipHorse.setBounds(x-5,y+25,76,12);
					add(flipHorse);	
				}
			}
			
			int xAttacker = x+110;
			int attackerCount = 0;
			for (GameObject attacker : cr.getAttackers()) {
				if (attackerCount != 0 && attackerCount % 4 == 0) {
					y += 90;
					xAttacker = x+110;
				}
				xAttacker += 90;
				RealmComponent attackerRc = RealmComponent.getRealmComponent(attacker);
				g.drawImage(attackerRc.getImage(),xAttacker,y-40,80,80,null);
				attackerCount += 1;
			}
		}
		y += 80;
		
		/*
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
			if (!cr.isSheetOwner() && cr.getAttackerCount() == 0 && !battleParticipant.hasTarget()) {
				g.drawImage(battleParticipant.getImage(),xUnassigned,y-40,80,80,null);
				xUnassigned += 90;
				unassignedCount +=1;
			}
		}
		if (unassignedCount != 0) {
			g.drawString("UNASSIGNED",x+10,yUnassignedHeadline);
		}
		*/
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