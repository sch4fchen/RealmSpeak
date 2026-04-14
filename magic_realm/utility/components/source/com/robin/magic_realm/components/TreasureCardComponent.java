package com.robin.magic_realm.components;

import java.awt.*;
import java.util.*;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.*;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class TreasureCardComponent extends CardComponent implements MagicChit {
	protected static final int SPOT_DIAM = 20;
	private static final Color COMMITTED_BACKING = new Color(255,200,200,220);

	public TreasureCardComponent(GameObject obj) {
		super(obj);
	}
	public String getName() {
	    return TREASURE;
	}
	public Color getBackingColor() {
		return new Color(0,0,128);
	}
	public String getCardTypeName() {
		return "Treasure";
	}
	public void drawSpot(Graphics g,int x,int y,Color c) {
		int rad = SPOT_DIAM>>1;
		Color alias = GraphicsUtil.convertColor(c,Color.white,50);
		g.setColor(alias);
		g.fillOval(x-rad,y-rad,SPOT_DIAM,SPOT_DIAM);
		
		g.setColor(c);
		g.fillOval(x-rad+1,y-rad+1,SPOT_DIAM-2,SPOT_DIAM-2);
	}
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		if (isFaceUp()) { // only need to draw if face up!
			int pos;
			TextType tt;
			
			// Draw spots first (text can overwrite without harm)
			
			String tSize = gameObject.getThisAttribute("treasure");
			boolean large = tSize.equals("large");
			boolean great = gameObject.hasKey("great");
			
			pos = (CARD_HEIGHT>>1) + (CARD_HEIGHT>>3); // 3/4 down
			if (!isColor()) {
				if (large && great) {
					drawSpot(g,(CARD_WIDTH>>1)-SPOT_DIAM,pos,MagicRealmColor.RED);
					drawSpot(g,(CARD_WIDTH>>1)+SPOT_DIAM,pos,MagicRealmColor.GOLD);
				}
				else if (large || great) {
					if (large) {
						drawSpot(g,CARD_WIDTH>>1,pos,MagicRealmColor.GOLD);
					}
					else {
						drawSpot(g,CARD_WIDTH>>1,pos,MagicRealmColor.RED);
					}
				}
			}
			
			// Add the NOT READY flag, if needed
			if (gameObject.hasThisAttribute("notready")) {
				tt = new TextType("UNFINISHED",PRINT_WIDTH,"NOTREADY");
				tt.setRotate(25);
				tt.draw(g,PRINT_MARGIN,CARD_HEIGHT>>1,Alignment.Center);
			}
			
			pos = 0;
			
			// Save a line position for the word "Potion"
			tt = new TextType("Potion",PRINT_WIDTH,"ITALIC");
			if (gameObject.hasKey("potion")) {
				// Draw the word "Potion" if the treasure is a potion
				tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
				pos += tt.getHeight(g); // no, dont save the line!
			}
			
			// Draw the title
			tt = new TextType(gameObject.getName(),PRINT_WIDTH,"TITLE");
			tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
			pos += tt.getHeight(g);
			
			if (isColor()) {
				ImageIcon icon = getColorMagic().getIcon();
				int ix = (CARD_WIDTH - icon.getIconWidth())>>1;
				g.drawImage(icon.getImage(),ix,50,null);
			}
			else {			
				// Draw the description
				String desc = gameObject.getThisAttribute("text");
				if (desc!=null) {
					tt = new TextType(desc,PRINT_WIDTH,"NORMAL");
					tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
					pos += tt.getHeight(g);
				}
				
				// Draw CURSED
				if (gameObject.hasThisAttribute(Constants.CURSED)) {
					tt = new TextType("CURSED",PRINT_WIDTH,"TITLE_RED");
					tt.draw(g,PRINT_MARGIN+1,pos+1,Alignment.Center);
					tt = new TextType("CURSED",PRINT_WIDTH,"BOLD");
					tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
					pos += tt.getHeight(g);
				}
				if (gameObject.hasThisAttribute(Constants.DESTROYED)) {
					tt = new TextType("DESTROYED",PRINT_WIDTH,"DESTROYED_FONT");
					tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
					pos += tt.getHeight(g);
				}
				
				// Draw TWT designation
				String twt = gameObject.getThisAttribute(TREASURE_WITHIN_TREASURE);
				if (twt!=null) {
					tt = new TextType("P"+twt,PRINT_WIDTH,"TITLE_RED");
					tt.draw(g,PRINT_MARGIN,CARD_HEIGHT - 50,Alignment.Center);
					pos += tt.getHeight(g);
				}
				
				// Draw magic
				String magic = gameObject.getThisAttribute("magic");
				if (magic!=null) {
					ArrayList<String> tiedMagic = readTiedMagicTypes(getGameObject());
					ArrayList<String> enchantedMagic = gameObject.getThisAttributeList(Constants.ARTIFACT_ENHANCED_MAGIC);
					ArrayList<String> finalList = new ArrayList<>();
					finalList.add(magic);
					if (enchantedMagic!=null) {
						TreeSet<String> unique = new TreeSet<>();
						unique.addAll(enchantedMagic);
						finalList.addAll(unique);
					}
					StringBuffer sb = new StringBuffer();
					for (String mt:finalList) {
						if (sb.length()>0) sb.append(",");
						boolean tied = tiedMagic.remove(mt);
						if (tied) sb.append("(");
						sb.append(mt);
						if (tied) sb.append(")");
					}
					magic = sb.toString();
					
					tt = new TextType(magic,PRINT_WIDTH,"TITLE_RED");
					tt.draw(g,PRINT_MARGIN,CARD_HEIGHT - 66,Alignment.Center);
					pos += tt.getHeight(g);
				}
				
				// Draw color
				String color = SpellUtility.getColorSourceName(this);
				if (color!=null) {
					tt = new TextType(ColorMagic.getColorName(color.toUpperCase()),PRINT_WIDTH,"TITLE_RED");
					tt.draw(g,PRINT_MARGIN,CARD_HEIGHT - 66,Alignment.Center);
					pos += tt.getHeight(g);
				}
				
				// Draw the stats
				pos = CARD_HEIGHT - 20;
				
				if (gameObject.hasKey(Constants.CANNOT_MOVE)) {
					// Special CANNOT_MOVE text
					tt = new TextType("CANNOT MOVE",PRINT_WIDTH,"BOLD");
					tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
				}
				else {
					// Weight
					Strength weight = TreasureUtility.getWeightForTreasure(gameObject);
					String textTpye = "BOLD";
					if (RealmComponent.displayColoredStats) {
						Strength defaultWeight = new Strength();
						 if(getGameObject().hasThisAttribute(Constants.WEIGHT)) {
							defaultWeight = new Strength(getGameObject().getThisAttribute(Constants.WEIGHT));
						 }
						 if (weight.strongerThan(defaultWeight)) {
							 textTpye = "TITLE_RED";
						 } else if (defaultWeight.strongerThan(weight)) {
							 textTpye = "BOLD_BLUE";
						 }
					}
					tt = new TextType(weight.getChar(),PRINT_WIDTH,textTpye);
					tt.draw(g,PRINT_MARGIN+5,pos,Alignment.Left);
					
					// Gold
					String gold = gameObject.getThisAttribute("base_price");
					gold = gold==null?"0":gold;
					tt = new TextType(gold,PRINT_WIDTH,"BOLD");
					tt.draw(g,CARD_WIDTH-PRINT_MARGIN-5-tt.getWidth(g),pos,Alignment.Left);
					
					// Notoriety
					String notoriety = gameObject.getThisAttribute("notoriety");
					notoriety = notoriety==null?"":("N: "+notoriety);
					tt = new TextType(notoriety,PRINT_WIDTH,"NORMAL");
					tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
					
					// Fame
					String fame = gameObject.getThisAttribute("fame");
					String nativeCode = gameObject.getThisAttribute("native");
					if (nativeCode==null) {
						fame = fame==null?"":("FAME: "+fame);
					}
					else {
						fame = fame==null?"":(nativeCode+": "+fame+"F");
					}
					tt = new TextType(fame,PRINT_WIDTH,"NORMAL");
					pos -= tt.getHeight(g);
					tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
					
					// Show awakened/not awakened
					if ((magic!=null && magic.length()>0 && !getGameObject().hasThisAttribute("action"))
							|| getGameObject().hasThisAttribute("book")) {
						int total = SpellUtility.getSpellCount(getGameObject(),null,false);
						int awakened = SpellUtility.getSpellCount(getGameObject(),Boolean.TRUE,false);
						String awake = "Awakened "+awakened+"/"+total;
						tt = new TextType(awake,PRINT_WIDTH,"INFO_GREEN");
						pos -= tt.getHeight(g);
						tt.draw(g,PRINT_MARGIN,pos,Alignment.Center);
					}
				}
				
				// If attack card, show damage
				if (getGameObject().hasThisAttribute("attack")) {
					Strength strength = TreasureUtility.getStrengthForTreasure(gameObject);
					String strengthString = strength.toShortString();
					String speedString = "";
					Speed speed = TreasureUtility.getAttackSpeedForTreasure(gameObject);
					if (speed!=null) speedString=speed.getSpeedString();
					String textTpye = "CLOSED_RED";
					String textTpye2 = "CLOSED_RED";
					if (RealmComponent.displayColoredStats) {
						Strength defaultStrength = new Strength();
						if (gameObject.hasThisAttribute("strength")) {
							defaultStrength = new Strength(gameObject.getThisAttribute("strength"));
						}
						if (strength.strongerThan(defaultStrength)) {
							textTpye = "CLOSED_BLUE";
						} else if(defaultStrength.strongerThan(strength)) {
							textTpye = "CLOSED_ORANGE";
						}
						if (!speedString.isEmpty()) {
							String defaultSpeed = gameObject.getThisAttribute("attack_speed");
							if ((defaultSpeed==null || defaultSpeed.isEmpty()) && speed!=null) {
								textTpye2 = "CLOSED_BLUE";
							} else if(speed!=null && defaultSpeed!=null && !defaultSpeed.isEmpty() && speed.getNum()<Integer.parseInt(defaultSpeed)) {
								textTpye2 = "CLOSED_BLUE";
							} else if(speed!=null && defaultSpeed!=null && !defaultSpeed.isEmpty() && speed.getNum()>Integer.parseInt(defaultSpeed)) {
								textTpye2 = "CLOSED_ORANGE";
							}
						}
					}
					tt = new TextType(strengthString,PRINT_WIDTH,textTpye);
					TextType tt2 = new TextType(speedString,PRINT_WIDTH,textTpye2);
					pos -= tt.getHeight(g);
					int sharpness = TreasureUtility.getSharpnessForTreasure(gameObject);
					int deafaultSharpness = gameObject.getThisInt("sharpness");
					int x = PRINT_MARGIN+20;
					x += (3-sharpness)*5;
					tt.draw(g,x,pos,Alignment.Left);
					tt2.draw(g,x+tt.getWidth(g),pos,Alignment.Left);
					x += tt.getWidth(g)+tt2.getWidth(g)+5;
					int y = pos+tt.getHeight(g)-8;
					g.setColor(Color.red);
					if (RealmComponent.displayColoredStats && deafaultSharpness>sharpness) {
						g.setColor(Color.orange);
					}
					for (int i=0;i<sharpness;i++) {
						if (RealmComponent.displayColoredStats && i==deafaultSharpness) {
							g.setColor(Color.blue);
						}
						StarShape star = new StarShape(x,y,5,7);
						g.fill(star);
						x += 10;
					}
					g.setColor(Color.red);					
					if (gameObject.hasThisAttribute("length")) {
						int length = TreasureUtility.getLengthForTreasure(gameObject);
						String textType = "MINI_RED";
						if (RealmComponent.displayColoredStats) {
							int defaultLength = gameObject.getThisInt("length");
							if (length>defaultLength) {
								textType = "MINI_BLUE";
							} else if(defaultLength>length) {
								textType = "MINI_ORANGE";
							}
						}
						tt = new TextType(String.valueOf(length),PRINT_WIDTH,textType);
						tt.draw(g,x-4,pos+6,Alignment.Left);
					}
				}
				
				if (gameObject.hasThisAttribute(Constants.NEEDS_OPEN)) {
					tt = new TextType("CLOSED",PRINT_WIDTH,"CLOSED_RED");
					tt.setRotate(25);
					tt.draw(g,PRINT_MARGIN,CARD_HEIGHT>>2,Alignment.Center);
				}
				
				if (gameObject.hasThisAttribute(Constants.CAST_SPELL_ON_INIT)) {
					for (GameObject sgo : gameObject.getHold()) {
						if (sgo.hasThisAttribute("spell")) {
							SpellWrapper spell = new SpellWrapper(sgo);
							if (spell.isInert()) {
								tt = new TextType("INERT",PRINT_WIDTH,"CLOSED_RED");
								int x = PRINT_MARGIN;
								int y = CARD_HEIGHT>>1;
								tt.draw(g,x,y,Alignment.Center);
							}
						}
					}
				}
			}
			if (getGameObject().hasThisAttribute(SpellWrapper.INCANTATION_TIE) && isCommittable()) {
				g.setColor(COMMITTED_BACKING);
				int x = PRINT_MARGIN;
				int y = (CARD_HEIGHT>>2);
				g.fillRect(x,y+2,PRINT_WIDTH,12);
				
				tt = new TextType("Committed",PRINT_WIDTH,"BOLD");
				tt.draw(g,x,y,Alignment.Center);
			}
			
			drawDamageAssessment(g);
		}
	}
	public String getAdditionalInfo() {
		String tSize = gameObject.getThisAttribute("treasure");
		return tSize.equals("large")?"Large":"Small";
	}
	public boolean isCommittable() {
		return !getAllMagicNumbers(8).isEmpty();
	}
	// MagicChit Interface
	public boolean isEnchantable() {
		return !isColor() && !getEnchantableNumbers().isEmpty();
	}
	public boolean isColor() {
		return getGameObject().hasThisAttribute(Constants.ENCHANTED_COLOR);
	}
	public boolean compatibleWith(ColorMagic cm) {
		ArrayList<String> types = readAvailableMagicTypes(null,getGameObject());
		for(String type:types) {
			int mn = CharacterActionChitComponent.getMagicNumber(type);
			if (cm.getColorNumber()==mn) {
				return true;
			}
		}
		return false;
	}
	public ColorMagic getColorMagic() {
		if (isColor()) {
			int color = getGameObject().getThisInt(Constants.ENCHANTED_COLOR);
			return new ColorMagic(color,false);
		}
		return null;
	}
	public ArrayList<Integer> getEnchantableNumbers() {
		// only 1-5 can be enchanted colors!
		return getAllMagicNumbers(5);
	}
	public ArrayList<Integer> getAllMagicNumbers(int maximum) {
		ArrayList<Integer> list = new ArrayList<>();
		
		ArrayList<String> types = readAvailableMagicTypes(null,getGameObject());
		for(String type:types) {
			int mn = CharacterActionChitComponent.getMagicNumber(type);
			if (mn>0 && mn<=maximum) {
				list.add(Integer.valueOf(mn));
			}
		}
			
		return list;
	}
	public void enchant(int magicNumber) {
		getGameObject().setThisAttribute(Constants.ENCHANTED_COLOR,magicNumber);
	}
	public void makeFatigued() {
		// artifacts don't really "fatigue" in the same sense as a chit!
		getGameObject().removeThisAttribute(Constants.ENCHANTED_COLOR);
	}
	public boolean sameChitAttributes(MagicChit chit) {
		return false; // ALWAYS false, because every treasure is different!
	}
	public String asKey() {
		return toString();
	}
	
	public void validateColor() {
		if (isColor()) {
			int color = getGameObject().getThisInt(Constants.ENCHANTED_COLOR);
			if (!getEnchantableNumbers().contains(Integer.valueOf(color))) {
				makeFatigued();
			}
		}
	}
	
	private static ArrayList<String> readTiedMagicTypes(GameObject treasure) {
		ArrayList<String> magicTypes = new ArrayList<>();
		
		// for backward compatibility
		Object test = treasure.getThisAttributeBlock().get(SpellWrapper.INCANTATION_TIE); 
		if (test instanceof String) {
			SpellWrapper spell = new SpellWrapper(treasure.getGameObjectFromThisAttribute(SpellWrapper.INCANTATION_TIE));
			ArrayList<String> newList = new ArrayList<>();
			newList.add(spell.getCastMagicType());
			treasure.removeThisAttribute(SpellWrapper.INCANTATION_TIE);
			treasure.setThisAttributeList(SpellWrapper.INCANTATION_TIE,newList);
		}
		
		// Ok, back to normal now
		ArrayList<String> list = treasure.getThisAttributeList(SpellWrapper.INCANTATION_TIE);
		if (list!=null) {
			magicTypes.addAll(list);
		}
		return magicTypes;
	}

	public static ArrayList<String> readAvailableMagicTypes(String dayKey,GameObject treasure) {
		ArrayList<String> possMagicTypes = new ArrayList<>();
		
		String magic = treasure.getThisAttribute("magic");
		if (magic!=null) {
			possMagicTypes.add(magic);
		}
		String magic2 = treasure.getThisAttribute("magic2");
		if (magic2!=null) {
			possMagicTypes.add(magic2);
		}
		
		// Include any enchantments
		ArrayList<String> enchants = treasure.getThisAttributeList(Constants.ARTIFACT_ENHANCED_MAGIC);
		if (enchants!=null) {
			for (String enchant : enchants) {
				if (!possMagicTypes.contains(enchant)) {
					possMagicTypes.add(enchant);
				}
			}
		}
		
		// Figure out which magic types have already been used on this artifact today and remove them from possibilities
		String usedKey = treasure.getThisAttribute(Constants.USED_SPELL);
		if (usedKey!=null && usedKey.equals(dayKey)) {
			ArrayList<String> list = treasure.getThisAttributeList(Constants.USED_MAGIC_TYPE_LIST);
			if (list!=null) {
				for (String chitType : list) {
					possMagicTypes.remove(chitType);
				}
			}
		}
		
		// Finally, remove any tied magic types
		ArrayList<String> tiedMagicTypes = readTiedMagicTypes(treasure);
		for (String tiedMagicType : readTiedMagicTypes(treasure)) {
			if (possMagicTypes.contains(tiedMagicType)) {
				possMagicTypes.remove(tiedMagicType);
			}
		}

		return possMagicTypes;
	}

}