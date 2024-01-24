package com.robin.magic_realm.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.StarShape;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.swing.ImageCache;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class NativeChitComponent extends SquareChitComponent implements BattleChit,Horsebackable {
	protected int chitSize;
	private boolean alteredMoveSpeed = false;
	protected NativeChitComponent(GameObject obj) {
		super(obj);
	}
	public void updateChit() {
		int oldSize = chitSize;
		chitSize = isShrunk()?S_CHIT_SIZE:M_CHIT_SIZE;
		if (isDisplayStyleFrenzel() || isDisplayStyleAlternative()) {
			chitSize = H_CHIT_SIZE;
		}
		else {
			if (getGameObject().hasThisAttribute("animal") || getGameObject().hasThisAttribute("statue")) {
				String vul = getThisAttribute( "vulnerability");
				if (vul.equals("X")) {
					chitSize = X_CHIT_SIZE;
				}
				else if (vul.equals("T")) {
					chitSize = T_CHIT_SIZE;
				}
				else if (vul.equals("H")) {
					chitSize = H_CHIT_SIZE;
				}
				else if (vul.equals("M")) {
					chitSize = M_CHIT_SIZE;
				}
				else {
					chitSize = S_CHIT_SIZE;
				}
			}
		}
		if (oldSize!=chitSize) {
			updateSize();
		}
		try {
			if (isDisplayStyleFrenzel() || isDisplayStyleAlternative()) {
				lightColor = Color.white;
				darkColor = Color.white;
			}
			else {
				lightColor = MagicRealmColor.getColor(getAttribute("light", "chit_color"));
				darkColor = MagicRealmColor.getColor(getAttribute("dark", "chit_color"));
			}
		}
		catch (Exception ex) {
			System.out.println("problem with " + getGameObject().getName() + ": " + ex);
		}
	}
	public Dimension getSize() {
		updateChit();
		return super.getSize();
	}
	public boolean cannotChangeTactics() {
		return getGameObject().hasThisAttribute(Constants.NO_CHANGE_TACTICS) || hasFaceAttribute(Constants.NO_CHANGE_TACTICS) || getWeight().isMaximum();
	}
	public boolean changeTacticsAfterCasting() {
		return getGameObject().hasThisAttribute(Constants.CHANGE_TACTICS_AFTER_CASTING) || hasFaceAttribute(Constants.CHANGE_TACTICS_AFTER_CASTING);
	}
	public void changeTactics() {
		flip();
	}

	public String getName() {
		return NATIVE;
	}

	public int getChitSize() {
		updateChit();
		return chitSize;
	}

	public String getLightSideStat() {
		return "light";
	}

	public String getDarkSideStat() {
		return "dark";
	}

	protected void paintFrenzelValues(Graphics2D g) {
		int cs = getChitSize();
		
		TextType tt = new TextType(getGameObject().getName(),(cs>>1)+20, "ITALIC");
		tt.draw(g,5,5,Alignment.Left);
		if (getGameObject().hasThisAttribute(Constants.NATIVE_NAME) && RealmComponent.displaySubline) {
			String text = getGameObject().getThisAttribute(Constants.NATIVE_NAME);
			tt = new TextType(text,(cs>>1)+20, "ITALIC");
			if (getGameObject().hasThisAttribute("clan")) {
				Color color = g.getColor();
				Color clanColor = MagicRealmColor.getClanColor(getGameObject().getThisAttribute("clan"));
				tt.draw(g,5,13,Alignment.Left,clanColor);
				g.setColor(color);
			} else {
				tt.draw(g,5,13,Alignment.Left);
			}
		}
		
		int not = getGameObject().getThisInt("notoriety");
		int hire = getGameObject().getThisInt("base_price");
		
		int midy = cs>>1;
		tt = new TextType("N:"+not, cs, "NORMAL");
		tt.draw(g,cs-28,midy-21,Alignment.Left);
		tt = new TextType("G:"+hire, cs, "NORMAL");
		tt.draw(g,cs-29,midy-11,Alignment.Left);
		
		boolean armored = isArmored();
		int x = cs - 18;
		int y = 5;
		String vul = getVulnerability().getChar();
		if (vul!=null) {
			tt = new TextType(vul, cs, "STAT_BLACK");
			int rad = Math.max(tt.getWidth(g), tt.getHeight(g)) + 4;
			g.setColor(armored?Color.lightGray:Color.yellow);
			g.fillOval(x - 4, y + 1, rad, rad);
			tt.draw(g, x, y, Alignment.Left);
		}
		
		String commerce = getGameObject().getThisAttribute("commerce");
		if (commerce!=null) {
			if (commerce.indexOf('N')>=0) {
				tt = new TextType("+N",cs,"INFO_GREEN");
				tt.draw(g,5,midy-2,Alignment.Left);
			}
			if (commerce.indexOf('F')>=0) {
				tt = new TextType("+F",cs,"INFO_GREEN");
				tt.draw(g,5,midy+8,Alignment.Left);
			}
		}
		
		if (flies()) {
			tt = new TextType("flies",cs-4, "ITALIC");
			tt.draw(g,0,cs-35,Alignment.Right);
		}
	}
	public void paintComponent(Graphics g1) {
		if (this.getGameObject().hasThisAttribute(Constants.OUT_OF_GAME)) return;
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		
		int cs = getChitSize();

		String name = gameObject.getName();
		String group = gameObject.getThisAttribute("native");
		String letterCode = group.substring(0, 1).toUpperCase();
		
		// Draw image
		String icon_type = null;
		if (isDisplayStyleAlternative() && gameObject.hasThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE)) {
			icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE);
		}
		else {
			icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
		}
		if (icon_type != null) {
			NativeSteedChitComponent horse = (NativeSteedChitComponent)getHorse(false);
			if (horse!=null) {
				String icon_rider = null;
				if (isDisplayStyleAlternative() && gameObject.hasThisAttribute(Constants.ICON_TYPE_RIDER+Constants.ALTERNATIVE)) {
					icon_rider = gameObject.getThisAttribute(Constants.ICON_TYPE_RIDER+Constants.ALTERNATIVE);
				}
				else {
					icon_rider = gameObject.getThisAttribute(Constants.ICON_TYPE_RIDER);
				}
				if (icon_rider!=null) {
					icon_type = icon_rider;
				}
			}
			String iconDir = null;
			if (isDisplayStyleAlternative() && gameObject.hasThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE)) {
				iconDir = gameObject.getThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE);
			}
			else {
				iconDir = gameObject.getThisAttribute(Constants.ICON_FOLDER);
			}
			if (iconDir==null) {
				iconDir = "natives";
			}
			if ((isDisplayStyleColor() || isDisplayStyleFrenzel()) && !isDisplayStyleAlternative() && !gameObject.hasThisAttribute(Constants.SUPER_REALM)) {
				if (iconDir.startsWith("natives")) {
					icon_type = icon_type+"_"+letterCode.toLowerCase();
				}
				iconDir = iconDir+"_c";
			}

			double size = 0;
			int yOffset = 0;
			if (gameObject.hasThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE)) {
				size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE));
			}
			else if (gameObject.hasThisAttribute(Constants.ICON_SIZE)) {
				size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE));
			}
			if (gameObject.hasThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE)) {
				yOffset = getThisInt(gameObject.getThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE));
			}
			else if (gameObject.hasThisAttribute(Constants.ICON_Y_OFFSET)) {
				yOffset = getThisInt(Constants.ICON_Y_OFFSET);
			}
			
			if (chitSize == T_CHIT_SIZE) {
				if (size==0) size = 0.9;
				drawIcon(g, iconDir, icon_type, size, 0, yOffset, null);
			}
			else if (chitSize == S_CHIT_SIZE) {
				if (size==0) size = 0.5;
				drawIcon(g, iconDir, icon_type, size, 0, yOffset, null);
			}
			else {
				if (size==0) size = 0.7;
				drawIcon(g, iconDir, icon_type, size, 0, yOffset, null);
			}
		}

		TextType tt;

		// Draw Name
		if (!getGameObject().hasThisAttribute(Constants.COMPANION) && !getGameObject().hasThisAttribute(Constants.SUMMONED)) {
			String id;
			if (getGameObject().hasThisAttribute(Constants.BOARD_NUMBER)) {
				id = name.substring(name.length() - 4,name.length()-2).trim();
			}
			else {
				id = name.substring(name.length() - 2).trim();
			}
			tt = new TextType(letterCode + id, getChitSize(), "WHITE_NOTE");
			tt.draw(g, getChitSize() - 10 - tt.getWidth(g), 7, Alignment.Left);
		}
		
		if ((isDisplayStyleFrenzel() || isDisplayStyleAlternative())&& isDarkSideUp()) {
			g.setColor(Color.black);
			g.fillRect(4,cs-19,cs-8,17);
		}

		// Draw Stats
		alteredMoveSpeed = false;
		String move_speed = String.valueOf(getMoveSpeed(false).getNum());
		String attack_speed = getAttackSpeed().getSpeedString();
		String strength = getStrength().getChitString();
		String magic_type = getFaceAttributeString("magic_type");
		int sharpness = getSharpness();
		int x;
		int y;
		
		String statColor = "BOLD";
		if (isDisplayStyleFrenzel() || isDisplayStyleAlternative()) {
			statColor = isLightSideUp()?"STAT_ORANGE":"STAT_BRIGHT_ORANGE";
			
			String length = getFaceAttributeString("length");
			if (length.trim().length()==0) {
				length="0";
			}
			tt = new TextType("("+length+")", cs,statColor);
			tt.draw(g,cs>>1,cs - tt.getHeight(g) - 5,Alignment.Left);
		}
		
		tt = new TextType(strength + magic_type + attack_speed, getChitSize(), statColor);
		x = 5;
		y = cs - 5 - tt.getHeight(g);
		tt.draw(g, x, y, Alignment.Left);
		x += tt.getWidth(g) + 4;
		y += tt.getHeight(g) - 6;
		for (int i = 0; i < sharpness; i++) {
			StarShape star = new StarShape(x, y, 5, 7);
			g.fill(star);
			x += 10;
		}

		String moveString = move_speed+(alteredMoveSpeed?"!":"");
		if (isDisplayStyleFrenzel() || isDisplayStyleAlternative()) {
			tt = new TextType(moveString, cs, "STAT_WHITE");
			x = cs - tt.getWidth(g) - 9;
			y = cs - tt.getHeight(g) - 6;
			int rad = Math.max(tt.getWidth(g), tt.getHeight(g)) + 2;
			g.setColor(Color.blue);
			g.fillOval(x - 5, y + 2, rad, rad);
			tt.draw(g, x, y, Alignment.Left);
		}
		else {
			tt = new TextType(moveString, getChitSize(), statColor);
			x = cs - 5 - tt.getWidth(g);
			y = cs - 5 - tt.getHeight(g);
			tt.draw(g, x, y, Alignment.Left);
		}
		
		if (isDisplayStyleFrenzel() || isDisplayStyleAlternative()) {
			paintFrenzelValues(g);
		}
		
		if (RealmComponent.displayArmor && isArmored()) {
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.black);
			g.drawRect(1,1,cs-1,cs-1);
			g.setStroke(stroke);
		}
		
		drawHorse(g);
		
		drawEmployer(g);
		drawHiddenStatus(g);
		drawAttentionMarkers(g);
		drawDamageAssessment(g);
	}
	private void drawHorse(Graphics g) {
		int size = getChitSize();
		NativeSteedChitComponent horse = (NativeSteedChitComponent)getHorse(false);
		if (horse!=null) {
			String[] ret = horse.getFolderAndType();
			int horseSize = ret[2]==null?20:40*Integer.valueOf(ret[2]);
			ImageIcon icon = ImageCache.getIcon(ret[0]+"/"+ret[1],horseSize);
			g.drawImage(icon.getImage(),size-icon.getIconWidth()-2,(size>>1),null);
		}
	}

	// BattleChit Interface
	public boolean targets(BattleChit chit) {
		RealmComponent rc = getTarget();
		RealmComponent rc2 = get2ndTarget();
		return ((rc != null && rc.equals(chit)) || (rc2 !=null && rc2.equals(chit)));
	}

	public Integer getLength() {
		if (getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_LENGTH)) {
			return Integer.valueOf(getGameObject().getThisAttribute(Constants.ENCHANTED_WEAPON_LENGTH));
		}
		Integer length = getFaceAttributeInteger("length");
		if (length == null) {
			length = Integer.valueOf(0);
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) {
			length--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) {
			length++;
		}
		return length<0?0:length;
	}

	public Speed getMoveSpeed() {
		return getMoveSpeed(true);
	}
	public Speed getMoveSpeed(boolean includeHorse) {
		if (includeHorse) {
			BattleHorse horse = getHorse();
			if (horse!=null) {
				return horse.getMoveSpeed();
			}
		}
		int otherSpeed = getGameObject().getThisInt("move_speed_change");
		if (otherSpeed>0) {
			alteredMoveSpeed = true;
			Speed speed = new Speed(otherSpeed,speedModifier());
			if (getGameObject().hasThisAttribute(Constants.GROW_WINGS) && (new Speed(Constants.GROW_WINGS_SPEED)).fasterThan(speed)) {
				speed = new Speed(otherSpeed,speedModifier()-1);
			}
			return speed;
		}
		Speed speed = new Speed(getFaceAttributeInteger("move_speed"),speedModifier());
		if (getGameObject().hasThisAttribute(Constants.GROW_WINGS) && (new Speed(Constants.GROW_WINGS_SPEED)).fasterThan(speed)) {
			speed = new Speed(getFaceAttributeInteger("move_speed"),speedModifier()-1);
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
	
	public boolean hasAnAttack() {
		return (getFaceAttributeString("strength")!=null && getFaceAttributeString("strength").length()>0)
				|| (getMagicType()!=null && getMagicType().length()>0);
	}
	
	public Speed getAttackSpeed() {
		if (getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_SPEED)) {
			return new Speed(getGameObject().getThisAttribute(Constants.ENCHANTED_WEAPON_SPEED),0);
		}
		return new Speed(getFaceAttributeInteger("attack_speed"),speedModifier());
	}

	public Strength getStrength() {
		if (getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_STRENGTH)) {
			return new Strength(getGameObject().getThisAttribute(Constants.ENCHANTED_WEAPON_STRENGTH));
		}
		Strength strength = new Strength(getFaceAttributeString("strength"));
		strength.modify(sizeModifier());
		if (strength.getChar()!="T" && SpellUtility.affectedByBewitchingSpellKey(getGameObject(),Constants.STRONG_MF)) {
			strength.modify(1);
		}
		return strength;
	}

	public Harm getHarm() {
		if (getWeight().isNegligible()) return new Harm(new Strength("N"),0);
		return new Harm(getStrength(), getSharpness());
	}

	public int getSharpness() {
		if (getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_SHARPNESS)) {
			return Integer.valueOf(getGameObject().getThisAttribute(Constants.ENCHANTED_WEAPON_SHARPNESS));
		}
		int sharpness = getFaceAttributeInt("sharpness");
		sharpness += getGameObject().getThisInt(Constants.ADD_SHARPNESS);
		if (sharpness>0) {
			TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
			if (tl!=null && tl.isInClearing() && tl.clearing.hasSpellEffect(Constants.BLUNTED)) {
				sharpness--;
			}
			if (sharpness>0 && getGameObject().hasThisAttribute(Constants.BLUNT)
					&& !getGameObject().hasThisAttribute(Constants.BLUNT_IMMUNITY)
					&& !getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
				sharpness--;
			}
		}
		return sharpness;
	}

	public String getMagicType() {
		return getFaceAttributeString("magic_type");
	}
	public String getAttackSpell() {
		return getFaceAttributeString("attack_spell");
	}
	
	public int getManeuverCombatBox() {
		return getManeuverCombatBox(true);
	}
	public int getManeuverCombatBox(boolean includeHorse) {
		if (includeHorse) {
			BattleHorse horse = getHorse();
			if (horse!=null) {
				return horse.getManeuverCombatBox();
			}
		}
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBox();
	}

	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBox();
	}

	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs, BattleChit attacker, int box, Harm attackerHarm,int attackOrderPos) {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		
		BattleHorse horse = null;
		if (!combat.isTargetingRider(attacker.getGameObject())) {
			horse = getHorse(attackOrderPos);
		}
		boolean horseHarmed = false;
		if (horse!=null) {
			CombatWrapper horseCombat = new CombatWrapper(horse.getGameObject());
			if (horseCombat.getKilledBy()==null || horseCombat.getHitByOrderNumber()==attackOrderPos) {
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits the "
						+getGameObject().getNameWithNumber()+"'s "
						+horse.getGameObject().getNameWithNumber());
						
				horseHarmed = horse.applyHit(game,hostPrefs,attacker,box,attackerHarm,attackOrderPos);
				if (!attackerHarm.getStrength().isRed()) {
					// If harm is NOT RED, then only the horse is hit, otherwise, the RED continues to the native!
					return horseHarmed;
				}
			}
		}
		
		ArrayList<SpellWrapper> holyShields = SpellUtility.getBewitchingSpellsWithKey(getGameObject(),Constants.HOLY_SHIELD);
		if ((holyShields!=null&&!holyShields.isEmpty()) || affectedByKey(Constants.HOLY_SHIELD) || combat.hasHolyShield(attacker.getAttackSpeed(),attacker.getLength())) {
			for (SpellWrapper spell : holyShields) {
				spell.expireSpell();
			}
			combat.setHolyShield(attacker.getAttackSpeed(), attacker.getLength());
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits Holy Shield and the attack is blocked.");
			return false;
		}
		
		Harm harm = new Harm(attackerHarm);
		Strength vulnerability = getVulnerability();
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
		if (applied.strongerOrEqualTo(vulnerability)) {
			// Dead native!
			combat.setKilledBy(attacker.getGameObject());
			combat.setKilledLength(attacker.getLength());
			combat.setKilledSpeed(attacker.getAttackSpeed());
			if (!hostPrefs.hasPref(Constants.OPT_SR_ENDING_COMBAT)) return true;
		}
		return horseHarmed;
	}

	public boolean isMissile() {
		return gameObject.hasThisAttribute("missile") && !gameObject.hasThisAttribute(Constants.ENCHANTED_WEAPON);
	}
	public String getMissileType() {
		return gameObject.getThisAttribute("missile");
	}

	public void changeWeaponState() {
		// Do nothing
	}

	public boolean hitsOnTie() {
		return false; // default
	}
	public void setHidden(boolean val) { // I don't like using sides to determine hideness for natives...
		if (isHidden() != val) {
			if (val) {
				getGameObject().setThisAttribute(Constants.HIDDEN);
			}
			else {
				getGameObject().removeThisAttribute(Constants.HIDDEN);
			}
		}
	}
	public boolean isHidden() {
		return getGameObject().hasThisAttribute(Constants.HIDDEN);
	}
	public Strength getVulnerability() {
		Strength vul =  new Strength(getThisAttribute( "vulnerability"));
		vul.modify(sizeModifier());
		if (getGameObject().hasThisAttribute(Constants.WEAKENED_VULNERABILITY)) {
			vul.modify(-1);
		}
		if (getGameObject().hasThisAttribute(Constants.STRENGTHENED_VULNERABILITY)) {
			vul.modify(+1);
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_VULNERABILITY)) {
			vul.modify(-1);
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_VULNERABILITY)) {
			vul.modify(+1);
		}
		return vul;
	}
	public boolean isArmored() {
		return getGameObject().hasThisAttribute(Constants.ARMORED);
	}
	public boolean hasBarkskin() {
		return getGameObject().hasThisAttribute(Constants.BARKSKIN);
	}
	public String getAttackString() {
		String magicType = getMagicType();
		if (magicType!=null && magicType.trim().length()>0) {
			return magicType+getAttackSpeed().getNum();
		}
		Strength str = getStrength();
		if (!str.isNegligible()) {
			StringBuffer sb = new StringBuffer(str.toString());
			sb.append(getAttackSpeed().getNum());
			for (int i=0;i<getSharpness();i++) {
				sb.append("*");
			}
			return sb.toString();
		}
		return "";
	}
	public boolean isMistLike() {
		return getGameObject().hasThisAttribute(Constants.MIST_LIKE);
	}
	protected int speedModifier() {
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
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) {
			mod++;
		}
		if (flies() && getCurrentLocation()!=null && getCurrentLocation().clearing!=null && getCurrentLocation().clearing.isAffectedByViolentWinds()) {
			mod++;
		}
		return mod;
	}
	protected boolean isShrunk() {
		return getGameObject().hasThisAttribute(Constants.SHRINK);
	}
	protected int sizeModifier() {
		int mod = 0;
		if (isShrunk()) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) {
			mod++;
		}
		return mod;
	}
}