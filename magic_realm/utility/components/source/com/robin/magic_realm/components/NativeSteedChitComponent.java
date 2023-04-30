package com.robin.magic_realm.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.*;

public class NativeSteedChitComponent extends SquareChitComponent implements BattleHorse {

	public static final String TROT_SIDE_UP = LIGHT_SIDE_UP;
	public static final String GALLOP_SIDE_UP = DARK_SIDE_UP;
	
	protected NativeSteedChitComponent(GameObject obj) {
		super(obj);
		try {
			lightColor = MagicRealmColor.getColor(getAttribute("trot","chit_color"));
			darkColor = MagicRealmColor.getColor(getAttribute("gallop","chit_color"));
			if (gameObject.hasThisAttribute(Constants.SUPER_REALM)) {
				lightColor = Color.white;
				darkColor = Color.white;
			}
		}
		catch(Exception ex) {
			System.out.println("problem with "+obj.getName()+": "+ex);
		}
	}
	public String getName() {
	    return NATIVE_HORSE;
	}
	
	public int getChitSize() {
		if (getRider()!=null && getRider().isShrunk()) {
			return M_CHIT_SIZE;
		}
		return H_CHIT_SIZE;
	}
	private NativeChitComponent getRider() {
		RealmComponent rider = RealmComponent.getRealmComponent(getGameObject().getHeldBy());
		if (rider instanceof NativeChitComponent) {
			return (NativeChitComponent) rider;
		}
		return null;
	}
	protected int sizeModifier() {
		if (getRider()!=null ) {
			getRider().sizeModifier();
		}
		return 0;
	}
	protected int speedModifier() {
		if (getRider()!=null ) {
			getRider().speedModifier();
		}
		int mod = 0;
		if (getGameObject().hasThisAttribute(Constants.SLOWED)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.SHRINK)) {
			mod--;
		}
		if (new CombatWrapper(getGameObject()).isFreezed()) {
			mod++;
		}
		return mod;
	}
	public String getLightSideStat() {
		return "trot";
	}
	public String getDarkSideStat() {
		return "gallop";
	}
	public String[] getFolderAndType() {
		String[] ret = new String[3];
		if (gameObject.hasThisAttribute(Constants.SUPER_REALM)) {
			ret[0] = gameObject.getThisAttribute(Constants.ICON_FOLDER);
			ret[1] = gameObject.getThisAttribute(Constants.ICON_TYPE);
			ret[2] = gameObject.getThisAttribute(Constants.ICON_SIZE);
			return ret;
		}
		String horse_type = gameObject.getThisAttribute("horse");
		if (horse_type!=null) {
			String folder = useColorIcons()?"steed_c":"steed";
			String letterCode = gameObject.getName().substring(0,1).toUpperCase();
			horse_type = horse_type + (useColorIcons()?("_"+letterCode.toLowerCase()):"");
			
			ret[0] = folder;
			ret[1] = horse_type;
			return ret;
		}
		return null;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		String name = gameObject.getName();
		String letterCode = name.substring(0,1).toUpperCase();
		
		// Draw image
		if (gameObject.hasThisAttribute(Constants.SUPER_REALM)) {
			double size = 0.5;
			int yOffset = 0;
			String folder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
			String icon = gameObject.getThisAttribute(Constants.ICON_TYPE);
			if (gameObject.hasThisAttribute(Constants.ICON_SIZE)) {
				size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE));
			}
			if (gameObject.hasThisAttribute(Constants.ICON_Y_OFFSET)) {
				yOffset = getThisInt(Constants.ICON_Y_OFFSET);
			}
			drawIcon(g,folder,icon,size,0,yOffset,null);
		}
		else {
			String horse_type = gameObject.getAttribute("this","horse");
			if (horse_type!=null) {
				String folder = useColorIcons()?"steed_c":"steed";
				horse_type = horse_type + (useColorIcons()?("_"+letterCode.toLowerCase()):"");
				drawIcon(g,folder,horse_type,0.5);
			}
		}
		
		TextType tt;
		
		// Draw Owner
		if (!getGameObject().hasThisAttribute("companion") && !getGameObject().hasThisAttribute("monster_steed")) {
			String id;
			if (getGameObject().hasThisAttribute(Constants.BOARD_NUMBER)) {
				id = name.substring(name.length() - 4,name.length()-2).trim();
			}
			else {
				id = name.substring(name.length() - 2).trim();
			}
			tt = new TextType(letterCode+id,getChitSize(),"WHITE_NOTE");
		}
		else {
			tt = new TextType(name,getChitSize(),"WHITE_NOTE");
		}
		tt.draw(g,getChitSize()-10-tt.getWidth(g),7,Alignment.Left);
		if (RealmComponent.displaySubline && getGameObject().hasThisAttribute("native")) {
			String text = getGameObject().getThisAttribute("native");
			if (getGameObject().hasThisAttribute("rank")) {
				text = text + " " + getGameObject().getThisAttribute("rank");
			}
			tt = new TextType(text,(getChitSize()>>1)+20, "ITALIC");
			if (getGameObject().hasThisAttribute("clan")) {
				Color color = g.getColor();
				Color clanColor = MagicRealmColor.getClanColor(getGameObject().getThisAttribute("clan"));
				tt.draw(g,5,13,Alignment.Left,clanColor);
				g.setColor(color);
			} else {
				tt.draw(g,5,13,Alignment.Left);
			}
		}
		
		// Draw Stats
		String asterisk = isTrotting()?"":"*";
		
		String speed = getMoveSpeed().getSpeedString();
		String strength = getStrength().getChitString();
		
		tt = new TextType(strength,getChitSize(),"BIG_BOLD");
		tt.draw(g,10,(getChitSize()>>1)-tt.getHeight(g),Alignment.Left);
		
		tt = new TextType(speed+asterisk,getChitSize(),"BIG_BOLD");
		tt.draw(g,getChitSize()>>1,getChitSize()-(getChitSize()>>2)-(getChitSize()>>3),Alignment.Left);
		
		if (RealmComponent.displayArmor && getGameObject().hasThisAttribute(Constants.ARMORED)) {
			g.setColor(Color.black);
			g.drawRect(0,0,getChitSize(),getChitSize());
			g.drawRect(1,1,getChitSize()-2,getChitSize()-2);
		}
		
		drawDamageAssessment(g);
	}
	// BattleChit Interface
	public boolean targets(BattleChit chit) {
		return false;
	}
	public Integer getLength() {
	    return null; // horses don't have length
	}
	public Speed getMoveSpeed() {
	    return new Speed(getFaceAttributeInteger("move_speed"),speedModifier());
	}
	public Speed getFlySpeed() {
		return null;
	}
	public boolean hasAnAttack() {
		return false;
	}
	public Speed getAttackSpeed() {
	    return null; // horses don't attack
	}
	public Strength getStrength() {
		Strength strength = new Strength(getFaceAttributeString("strength"));
		strength.modify(sizeModifier());
		return strength;
	}
	public Harm getHarm() {
		return null; // horses don't cause harm
	}
	public int getSharpness() {
	    return 0;
	}
	public String getMagicType() {
	    return null; // horses don't have magic
	}
	public String getAttackSpell() {
		return null; // horses don't have magic
	}
	// BattleHorse Interface
	public void setGallop() {
	    setFacing(GALLOP_SIDE_UP);
	}
	public void setWalk() {
	    setFacing(TROT_SIDE_UP);
	}
	public boolean isTrotting() {
		return isLightSideUp();
	}
	public int getManeuverCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBox();
	}
	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBox();
	}
	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs,BattleChit attacker,int box,Harm attackerHarm,int attackOrderPos) {
		RealmComponent rider = getRider();
		if (rider!=null) {
			CombatWrapper combatRider = new CombatWrapper(rider.getGameObject());
			ArrayList<SpellWrapper> holyShieldsRider = SpellUtility.getBewitchingSpellsWithKey(rider.getGameObject(),Constants.HOLY_SHIELD);
			if ((holyShieldsRider!=null&&!holyShieldsRider.isEmpty()) || (holyShieldsRider!=null&&!holyShieldsRider.isEmpty())) {
				for (SpellWrapper spell : holyShieldsRider) {
					spell.expireSpell();
				}
				combatRider.setHolyShield(attacker.getAttackSpeed(), attacker.getLength());
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits Holy Shield and attack is blocked.");
				return false;
			}
		}
		
		Harm harm = new Harm(attackerHarm);
		Strength vulnerability = new Strength(getAttribute("this","vulnerability"));
		if (!harm.getIgnoresArmor() && getGameObject().hasThisAttribute(Constants.ARMORED)) {
			harm.dampenSharpness();
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits armor, and reduces sharpness: "+harm.toString());
		}
		Strength applied = harm.getAppliedStrength();
		if (applied.strongerOrEqualTo(vulnerability)) {
			// Dead horse!
			CombatWrapper combat = new CombatWrapper(getGameObject());
			combat.setKilledBy(attacker.getGameObject());
			combat.setKilledLength(attacker.getLength());
			combat.setKilledSpeed(attacker.getAttackSpeed());
			combat.setHitByOrderNumber(attackOrderPos);
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Kills the "+getGameObject().getNameWithNumber());			
			return true;
		}
		return false;
	}
	public boolean isMissile() {
		return false;
	}
	public String getMissileType() {
		return null;
	}
	public void changeWeaponState() {
		// Do nothing
	}
	public boolean hitsOnTie() {
		return false; // default
	}
	public SpellWrapper getSpell() {
		return null;
	}
	public Strength getVulnerability() {
		Strength strength = new Strength(getAttribute("this", "vulnerability"));
		strength.modify(sizeModifier());
		return strength;
	}
	public boolean isArmored() {
		return getGameObject().hasThisAttribute(Constants.ARMORED);
	}
	public String getAttackString() {
		Strength str = getStrength();
		if (!str.isNegligible()) {
			return str.toString();
		}
		return "";
	}
	public boolean doublesMove() {
		String val = getGameObject().getThisAttribute("move_bonus");
		return "double".equals(val);
	}
	public boolean extraMove() {
		String val = getGameObject().getThisAttribute("move_bonus");
		return "extra".equals(val);
	}
	public boolean isDead() {
		return getGameObject().hasThisAttribute(Constants.DEAD);
	}
}