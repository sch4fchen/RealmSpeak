package com.robin.magic_realm.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.*;

public class SteedChitComponent extends RoundChitComponent implements BattleHorse {
	public static final String TROT_SIDE_UP = LIGHT_SIDE_UP;
	public static final String GALLOP_SIDE_UP = DARK_SIDE_UP;

	private boolean alteredMoveSpeed = false;
	
	public SteedChitComponent(GameObject obj) {
		super(obj);
		try {
			lightColor = MagicRealmColor.getColor(getAttribute("trot","chit_color"));
			darkColor = MagicRealmColor.getColor(getAttribute("gallop","chit_color"));
		}
		catch(Exception ex) {
			System.out.println("problem with "+obj.getName()+": "+ex);
		}
	}

	public String getLightSideStat() {
		return "trot";
	}
	public String getDarkSideStat() {
		return "gallop";
	}
	public boolean isTrotting() {
		return isLightSideUp();
	}
	public boolean isGalloping() {
		return isDarkSideUp();
	}
	public String getName() {
	    return HORSE;
	}
	public boolean doublesMove() {
		String val = getGameObject().getThisAttribute("move_bonus");
		return "double".equals(val);
	}
	public boolean extraMove() {
		String val = getGameObject().getThisAttribute("move_bonus");
		return "extra".equals(val);
	}
	
	public int getChitSize() {
		return T_CHIT_SIZE;
	}

	private RealmComponent getRider() {
		RealmComponent rider = RealmComponent.getRealmComponent(getGameObject().getHeldBy());
		if (rider.isCharacter() || rider.isNative() ||  rider.isMonster()) {
			return rider;
		}
		return null;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Draw image
		if (isDisplayStyleAlternative() && gameObject.hasThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE) && gameObject.hasThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE)) {
			double size = 0.5;
			int yOffset = 0;
			String folder = gameObject.getThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE);
			String icon = gameObject.getThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE);
			if (gameObject.hasThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE)) {
				size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE));
			}
			if (gameObject.hasThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE)) {
				yOffset = getThisInt(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE);
			}
			drawIcon(g,folder,icon,size,0,yOffset,null);
		}
		else if (gameObject.hasThisAttribute(Constants.SUPER_REALM)) {
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
			String horse_type = gameObject.getThisAttribute("horse");
			if (horse_type!=null) {
				drawIcon(g,"steed"+(useColorIcons()?"_c":""),horse_type,0.5);
			}
		}
		
		// Draw Stats
		alteredMoveSpeed = false;
		String statSide = isTrotting()?"trot":"gallop";
		String asterisk = isTrotting()?"":"*";
		
		Speed speed = isTrotting()?getTrotSpeed():getGallopSpeed();
		String strength = gameObject.getAttribute(statSide,"strength");
		
		TextType tt = new TextType(strength,getChitSize(),"BIG_BOLD");
		tt.draw(g,10,(getChitSize()>>1)-tt.getHeight(g),Alignment.Left);
		
		tt = new TextType(speed.getSpeedString()+asterisk,getChitSize(),alteredMoveSpeed?"BIG_BOLD_BLUE":"BIG_BOLD");
		tt.draw(g,getChitSize()>>1,getChitSize()-(getChitSize()>>2)-(getChitSize()>>3),Alignment.Left);
		
		if (RealmComponent.displayArmor && isArmored()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.black);
			g2.setStroke(new BasicStroke(2));
			g2.draw(this.getShape(0,0,getChitSize()));
		}
		
		drawDamageAssessment(g);
	}
	private Strength getStrength() {
		int mod = 0;
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) {
			mod++;
		}
		return new Strength(getFaceAttributeString("strength"),mod);
	}
	// BattleChit Interface
	public boolean targets(BattleChit chit) {
		return false;
	}
	public Integer getLength() {
	    return null; // horses don't have length
	}
	public Speed getMoveSpeed() {
		int mod = 0;
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) {
			mod++;
		}
		if (hasFaceAttribute(Constants.MOVE_SPEED_BONUS_COMBAT_BOX1) && getManeuverCombatBox()==1) {
			mod=mod-getFaceAttributeInteger(Constants.MOVE_SPEED_BONUS_COMBAT_BOX1);
		}
		if (hasFaceAttribute(Constants.MOVE_SPEED_BONUS_COMBAT_BOX2) && getManeuverCombatBox()==2) {
			mod=mod-getFaceAttributeInteger(Constants.MOVE_SPEED_BONUS_COMBAT_BOX2);
		}
		if (hasFaceAttribute(Constants.MOVE_SPEED_BONUS_COMBAT_BOX3) && getManeuverCombatBox()==3) {
			mod=mod-getFaceAttributeInteger(Constants.MOVE_SPEED_BONUS_COMBAT_BOX3);
		}
		RealmComponent owner = getHeldBy();
		if (getGameObject().hasThisAttribute("horse") && this.isActivated() && getStrength().strongerOrEqualTo(new Strength("M")) && owner!=null) {
			CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
			if (character.affectedByKey(Constants.HORSE_ARMOR)) {
				mod++;
			}
		}
		Speed speed = new Speed(getFaceAttributeInteger("move_speed"),mod);
		if (getGameObject().hasThisAttribute(Constants.GROW_WINGS) && (new Speed(Constants.GROW_WINGS_SPEED)).fasterThan(speed)) {
			mod--;
			speed = new Speed(getFaceAttributeInteger("move_speed"),mod);
		}
		if (this.isActivated() && !speed.fasterThanOrEqual(new Speed(2))) {
			if (owner!=null) {
				CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
				if (character.affectedByKey(Constants.SPURS)) {
					mod--;
					speed = new Speed(getFaceAttributeInteger("move_speed"),mod);
				}
			}
		}
		return speed;
	}
	public boolean flies() {
		return getGameObject().hasThisAttribute(Constants.FLYING) || getGameObject().hasThisAttribute(Constants.GROW_WINGS);
	}
	public Speed getFlySpeed() {
		if (flies()) {
			return getMoveSpeed();
		}
		return null;
	}
	private int getMoveModifier() {
		int mod = 0;
		GameObject go = getGameObject().getHeldBy();
		if (go!=null && go.hasThisAttribute(CHARACTER)) {
			CharacterWrapper character = new CharacterWrapper(go);
			ArrayList<GameObject> list = character.getAllActiveInventoryThisKeyAndValue(Constants.HORSE_MOD,null);
			for (GameObject item:list) {
				mod += item.getThisInt(Constants.HORSE_MOD); // cumulative... though there's really only the Horse Trainer (Traveler) at this time
			}
		}
		if (getGameObject().hasThisAttribute(Constants.SLOWED)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.SHRINK)) {
			mod--;
		}
		if (new CombatWrapper(getGameObject()).isFreezed()) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) {
			mod++;
		}
		RealmComponent owner = getHeldBy();
		if (getGameObject().hasThisAttribute("horse") && this.isActivated() && getStrength().strongerOrEqualTo(new Strength("M")) && owner!=null) {
			CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
			if (character.affectedByKey(Constants.HORSE_ARMOR)) {
				mod++;
			}
		}
		alteredMoveSpeed = mod!=0;
		return mod;
	}
	public Speed getTrotSpeed() {
		int mod = getMoveModifier();
		Speed speed = new Speed(getAttributeInt("trot","move_speed"),mod);
		if (getGameObject().hasThisAttribute(Constants.GROW_WINGS) && (new Speed(Constants.GROW_WINGS_SPEED)).fasterThan(speed)) {
			mod--;;
			speed = new Speed(getAttributeInt("trot","move_speed"),mod);
		}
		if (this.isActivated() && !speed.fasterThanOrEqual(new Speed(2))) {
			RealmComponent owner = getHeldBy();
			if (owner!=null) {
				CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
				if (character.affectedByKey(Constants.SPURS)) {
					mod--;
					speed = new Speed(getAttributeInt("trot","move_speed"),mod);
				}
			}
		}
		return speed;
	}
	public Speed getGallopSpeed() {
		int mod = getMoveModifier();
		Speed speed =  new Speed(getAttributeInt("gallop","move_speed"),mod);
		if (getGameObject().hasThisAttribute(Constants.GROW_WINGS) && (new Speed(Constants.GROW_WINGS_SPEED)).fasterThan(speed)) {
			mod--;;
			speed = new Speed(getAttributeInt("gallop","move_speed"),mod);
		}
		if (this.isActivated() && !speed.fasterThanOrEqual(new Speed(2))) {
			RealmComponent owner = getHeldBy();
			if (owner!=null) {
				CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
				if (character.affectedByKey(Constants.SPURS)) {
					mod--;
					speed = new Speed(getAttributeInt("gallop","move_speed"),mod);
				}
			}
		}
		return speed;
	}
	public Strength getTrotStrength() {
		return new Strength(getAttribute("trot","strength"));
	}
	public Strength getGallopStrength() {
		return new Strength(getAttribute("gallop","strength"));
	}
	public boolean hasAnAttack() {
		return false;
	}
	public Speed getAttackSpeed() {
	    return null; // horses don't attack
	}
	public Harm getHarm() {
		return null;
	}
	public Integer getSharpness() {
	    return null; // horses don't have sharpness
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
	public int getManeuverCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBoxDefense();
	}
	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBoxAttack();
	}
	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs,BattleChit attacker,int box,Harm attackerHarm,int attackOrderPos) {
		RealmComponent rider = getRider();
		if (rider!=null) {
			CombatWrapper combatRider = new CombatWrapper(rider.getGameObject());
			ArrayList<SpellWrapper> holyShieldsRider = SpellUtility.getBewitchingSpellsWithKey(rider.getGameObject(),Constants.HOLY_SHIELD);
			if ((holyShieldsRider!=null&&!holyShieldsRider.isEmpty()) || rider.affectedByKey(Constants.HOLY_SHIELD) || combatRider.hasHolyShield(attacker.getAttackSpeed(),attacker.getLength())) {
				for (SpellWrapper spell : holyShieldsRider) {
					spell.expireSpell();
				}
				combatRider.setHolyShield(attacker.getAttackSpeed(), attacker.getLength());
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits Holy Shield and the attack is blocked.");
				return false;
			}
		}
		
		Harm harm = new Harm(attackerHarm);
		Strength vulnerability = new Strength(getThisAttribute("vulnerability"));
		
		if (getGameObject().hasThisAttribute(Constants.POISON_IMMUNITY)) {
			if (attacker.isCharacter()) {
				WeaponChitComponent weapon = ((CharacterChitComponent)attacker).getAttackingWeapon();
				if (weapon!=null && weapon.getGameObject().hasThisAttribute(Constants.POISON)) {
					harm.dampenSharpness();
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Steed has poison immunity and additional sharpness is ignored: "+harm.toString());
				}
			}
			if (attacker.getGameObject().hasThisAttribute(Constants.POISON)) {
				harm.dampenSharpness();
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Steed has poison immunity and additional sharpness is ignored: "+harm.toString());
			}
		}
		
		if (!harm.getIgnoresArmor() && isArmored()) {
			harm.dampenSharpness();
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits armor, and reduces sharpness: "+harm.toString());
		}
		else if (!isArmored() && hasBarkskin()) {
			ColorMagic attackerImmunityColor = ColorMagic.makeColorMagic(attacker.getGameObject().getThisAttribute(Constants.MAGIC_IMMUNITY),true);
			if (attackerImmunityColor!=null && (attackerImmunityColor.isPrismColor()||attackerImmunityColor.getColorNumber()==ColorMagic.GRAY)) {
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Barkskin is ignored.");
			}
			else {
				harm.dampenSharpness();
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits barkskin, and reduces sharpness: "+harm.toString());
			}
		}
		
		Strength applied = harm.getAppliedStrength();
		if (hostPrefs.hasPref(Constants.HOUSE2_DENIZENS_WOUNDS) && applied.equalTo(vulnerability)) {
			addWound();
			RealmLogging.logMessage(getGameObject().getNameWithNumber(),"Wounded.");
		}	
		else if (applied.strongerOrEqualTo(vulnerability)) {
			// Dead horse!
			CombatWrapper combat = new CombatWrapper(getGameObject());
			combat.setKilledBy(attacker.getGameObject());
			combat.setKilledLength(attacker.getLength());
			combat.setKilledSpeed(attacker.getAttackSpeed());
			combat.setHitByOrderNumber(attackOrderPos);
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Kills the "+getGameObject().getName());
			if (!hostPrefs.hasPref(Constants.SR_ENDING_COMBAT)) return true;
		}
		return false;
	}
	public boolean isMissile() {
		return false;
	}
	public String getMissileType() {
		return null;
	}
	public void changeWeaponState(HostPrefWrapper hostPrefs) {
		// Do nothing
	}
	public boolean hitsOnTie() {
		return false; // default
	}
	public SpellWrapper getSpell() {
		return null;
	}
	public Strength getVulnerability() {
		Strength strength = new Strength(getThisAttribute("vulnerability"));
		int mod = 0;
		if (getGameObject().hasThisAttribute(Constants.WEAKENED_VULNERABILITY)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.STRENGTHENED_VULNERABILITY)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_VULNERABILITY)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_VULNERABILITY)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.WOUNDS)) {
			mod = mod - getWounds();
		}
		strength.modify(mod);
		return strength;
	}
	public boolean isArmored() {
		if (getGameObject().hasThisAttribute(Constants.ARMORED)) {
			return true;
		}
		RealmComponent owner = getHeldBy();
		if (getGameObject().hasThisAttribute("horse") && this.isActivated() && getStrength().strongerOrEqualTo(new Strength("M")) && owner!=null) {
			CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
			if (character.affectedByKey(Constants.HORSE_ARMOR)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasBarkskin() {
		return getGameObject().hasThisAttribute(Constants.BARKSKIN);
	}
	public boolean isDead() {
		return getGameObject().hasThisAttribute(Constants.DEAD);
	}
	public void addWound() {
		addWounds(1);
	}
	public void addWounds(int i) {
		getGameObject().setThisAttribute(Constants.WOUNDS,getWounds()+i); 
	}
	public int getWounds() {
		return getGameObject().getThisInt(Constants.WOUNDS);
	}
}