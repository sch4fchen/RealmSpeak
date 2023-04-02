package com.robin.magic_realm.components;

import java.awt.*;
import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.StarShape;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class MonsterChitComponent extends SquareChitComponent implements BattleChit {
	protected int chitSize;
	
	private boolean alteredMoveSpeed = false;
	
	private MonsterFightChitComponent fightChit = null; // only used when a monster is absorbed!!
	private MonsterMoveChitComponent moveChit = null; // only used when a monster is absorbed!!
	
	public MonsterFightChitComponent getFightChit() {
		if (fightChit==null) {
			fightChit = new MonsterFightChitComponent(getGameObject());
		}
		return fightChit;
	}

	public MonsterMoveChitComponent getMoveChit() {
		if (moveChit==null) {
			moveChit = new MonsterMoveChitComponent(getGameObject());
		}
		return moveChit;
	}

	protected MonsterChitComponent(GameObject obj) {
		super(obj);
	}
	public void updateChit() {
		int oldSize = chitSize;
		if (isDisplayStyleFrenzel()) {
			chitSize = H_CHIT_SIZE;
		}
		else {
			String vul = getVulnerability().getChar();
			if (vul.equals("X")) {
				chitSize = X_CHIT_SIZE;
			}
			else if (vul.equals("T") || vul.equals("!")) {
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
		if (oldSize!=chitSize) {
			updateSize();
		}

		try {
			if (isDisplayStyleFrenzel()) {
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

	public boolean isBlocked() {
		return getGameObject().hasThisAttribute("blocked");
	}

	public void setBlocked(boolean val) {
		boolean current = getGameObject().hasThisAttribute("blocked");
		if (current != val) {
			if (val) {
				getGameObject().setThisAttribute("blocked");
			}
			else {
				getGameObject().removeThisAttribute("blocked");
			}
		}
	}

	public MonsterPartChitComponent getWeapon() {
		if (!getGameObject().hasThisAttribute("animal") && !getGameObject().hasThisAttribute("statue")) { // as long as the monster isn't transformed!
			ArrayList<GameObject> list = getGameObject().getHold();
			if (list != null && list.size() > 0) {
				for (GameObject weapon : list) {
					RealmComponent rc = RealmComponent.getRealmComponent(weapon);
					if (rc.isMonsterPart()) { // Might be a Hurricane Winds FLY chit
						MonsterPartChitComponent monsterPart = (MonsterPartChitComponent) RealmComponent.getRealmComponent(weapon);
						if (!monsterPart.isDestroyed()) {
							return monsterPart;
						}
					}
				}
			}
		}
		return null;
	}

	public boolean isTremendous() {
		return getVulnerability().isTremendous();
	}
	
	public boolean isSmall() {
		return getGameObject().hasThisAttribute(Constants.SMALL);
	}
	
	public boolean canPinOpponent() {
		return getGameObject().hasAttribute("dark","pins");
	}
	
	public boolean cannotChangeTactics() {
		return getGameObject().hasThisAttribute("no_change_tactics");
	}

	public void changeTactics() {
		flip();
	}

	public String getName() {
		return MONSTER;
	}

	public String getLightSideStat() {
		return "light";
	}

	public String getDarkSideStat() {
		return "dark";
	}

	public int getChitSize() {
		updateChit();
		return chitSize;
	}
	
	public boolean isPinningOpponent() {
		return hasFaceAttribute("pins");
	}
	
	public boolean isRedSideUp() {
		return ("RED".equals(getFaceAttributeString("strength")));
	}

	public void paintAttackValues(Graphics2D g,int ox,int oy,Color attackBack) {
		int cs = getChitSize();
		TextType tt;
		String attack_speed = getAttackSpeed().getSpeedString();
		String strength = getStrength().getChitString();
		String magic_type = getFaceAttributeString("magic_type");
		int sharpness = getSharpness();
		
		boolean pins = isPinningOpponent();
		boolean red = strength.equals("!");
		String statColor = "STAT_BLACK";
		g.setColor(Color.black);
		if (isDisplayStyleFrenzel()) {
			if (!pins) {
				if (isLightSideUp()) {
					statColor = "STAT_ORANGE";
				}
				else {
					statColor = "STAT_BRIGHT_ORANGE";
				}
				
				String length = getFaceAttributeString("length");
				if (length.trim().length()==0) {
					length="0";
				}
				tt = new TextType("("+length+")", cs,statColor);
				int offset = 0;
				if (this.isAbsorbed()) {
					offset = 12;
				}
				tt.draw(g,(cs>>1)-offset,oy - tt.getHeight(g),Alignment.Left);
			}
		}
		
		int x;
		int y;
		if (red) {
			tt = new TextType(attack_speed, cs,statColor);
			x = ox + 8;
			y = oy - 2 - tt.getHeight(g);
			int rad = Math.max(tt.getWidth(g), tt.getHeight(g)) + 4;
			g.fillOval(x - 6, y + 1, rad, rad);
			g.setColor(attackBack);
			g.fillOval(x - 4, y + 3, rad - 4, rad - 4);
		}
		else {
			String string = (strength + magic_type + attack_speed);
			tt = new TextType(string, cs,statColor);
			x = ox + 6;
			y = oy - tt.getHeight(g);
		}
		tt.draw(g, x, y, Alignment.Left);
		x += tt.getWidth(g) + 4;
		y += tt.getHeight(g) - 6;
		for (int i = 0; i < sharpness; i++) {
			StarShape star = new StarShape(x, y, 5, 7);
			g.fill(star);
			x += 10;
		}
	}
	public void paintMoveValues(Graphics2D g,int ox,int oy) {
		int cs = getChitSize();
		alteredMoveSpeed = false;
		Speed move_speed = getMoveSpeed();
		if (move_speed!=null) {
			String string = String.valueOf(move_speed.getNum())+(alteredMoveSpeed?"!":"");
			if (isDisplayStyleFrenzel()) {
				int x,y;
				TextType tt = new TextType(string, cs, "STAT_WHITE");
				x = ox - tt.getWidth(g)-4;
				y = oy - tt.getHeight(g)-2;
				int rad = Math.max(tt.getWidth(g), tt.getHeight(g)) + 2;
				g.setColor(Color.blue);
				g.fillOval(x - 5, y + 2, rad, rad);
				tt.draw(g, x, y, Alignment.Left);
			}
			else {
				int x,y;
				TextType tt = new TextType(string, cs, "STAT_BLACK");
				x = ox - tt.getWidth(g);
				y = oy - tt.getHeight(g);
				tt.draw(g, x, y, Alignment.Left);
			}
		}
	}
	protected void paintFrenzelValues(Graphics2D g) {
		int cs = getChitSize();
		
		TextType tt = new TextType(getGameObject().getName(),(cs>>1)+20, "ITALIC");
		tt.draw(g,5,5,Alignment.Left);
		
		int not = getGameObject().getThisInt("notoriety");
		int fam = getGameObject().getThisInt("fame");
		if (not>0 || fam>0) {
			int midy = cs>>1;
			tt = new TextType("N:"+not, cs, "NORMAL");
			tt.draw(g,cs-25,midy-21,Alignment.Left);
			tt = new TextType("F:"+fam, cs, "NORMAL");
			tt.draw(g,cs-24,midy-11,Alignment.Left);
		}
		
		boolean armored = getGameObject().hasThisAttribute(Constants.ARMORED);
		int x = cs - 18;
		int y = 5;
		String vul = getVulnerability().getChar();//getGameObject().getThisAttribute("vulnerability");
		if (vul!=null) {
			tt = new TextType(vul, cs, "STAT_BLACK");
			int rad = Math.max(tt.getWidth(g), tt.getHeight(g)) + 4;
			g.setColor(armored?Color.lightGray:Color.yellow);
			g.fillOval(x - 4, y + 1, rad, rad);
			tt.draw(g, x, y, Alignment.Left);
		}
		
		String tileReq = getGameObject().getThisAttribute(Constants.SETUP_START_TILE_REQ); // Pruitt's monsters
		if (tileReq!=null) {
			tt = new TextType(tileReq, cs, "STAT_BLACK");
			x = cs - tt.getWidth(g)-6;
			y = cs>>1;
			
			g.setColor(MagicRealmColor.LIGHTGREEN);
			g.fillRect(x-2,y+1,tt.getWidth(g)+4,tt.getHeight(g)+4);
			
			tt.draw(g,x,y,Alignment.Left);
		}
		
		if (flies()) {
			tt = new TextType("flies",cs-4, "ITALIC");
			tt.draw(g,0,cs-35,Alignment.Right);
		}
	}
	protected String getIconFolder() {
		String iconDir = getGameObject().getThisAttribute(Constants.ICON_FOLDER);
		if (useColorIcons() && !gameObject.hasThisAttribute("super_realm")) {
			iconDir = iconDir+"_c";
		}
		return iconDir;
	}
	public Dimension getSize() {
		updateChit();
		return super.getSize();
	}
	public void paintComponent(Graphics g1) {
		if (this.getGameObject().hasThisAttribute(Constants.OUT_OF_GAME)) return;
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		int cs = getChitSize();
		
		Color attackBack = isLightSideUp()?lightColor:darkColor;

		// Draw image
		String icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
		if (icon_type != null) {
			if (isDisplayStyleFrenzel()) {
				double size = 0.6;
				int yOffset = 1;
				if (gameObject.hasThisAttribute(Constants.ICON_SIZE)) {
					size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE));
				}
				if (gameObject.hasThisAttribute(Constants.ICON_Y_OFFSET)) {
					yOffset = getThisInt(Constants.ICON_Y_OFFSET);
				}
				drawIcon(g, getIconFolder(), icon_type, size,-5,yOffset,null);
			}
			else {
				if (chitSize == T_CHIT_SIZE) {
					drawIcon(g, getIconFolder(), icon_type, 0.9);
				}
				else if (chitSize == S_CHIT_SIZE) {
					drawIcon(g, getIconFolder(), icon_type, 0.5);
				}
				else {
					drawIcon(g, getIconFolder(), icon_type, 0.7);
				}
			}
		}
		
		if (GameObject.showNumbers && gameObject.hasThisAttribute(Constants.NUMBER)) {
			g.setColor(Color.black);
			g.setFont(new Font("Dialog",Font.BOLD,11));
			int x = 5;
			int y = cs>>1;
			if (isDisplayStyleFrenzel()) {
				y += 10;
			}
			g.drawString(gameObject.getThisAttribute(Constants.NUMBER),x,y);
		}
		
		
		if (isDisplayStyleFrenzel() && isDarkSideUp()) {
			attackBack = isPinningOpponent()?Color.red:Color.black;
			g.setColor(attackBack);
			g.fillRect(4,cs-20,cs-8,18);
		}

		// Draw Stats
		paintAttackValues(g,0,cs - 5,attackBack);
		paintMoveValues(g,cs - 5,cs - 5);
		
		if (isDisplayStyleFrenzel()) {
			paintFrenzelValues(g);
		}
		
		if (RealmComponent.displayArmor && getGameObject().hasThisAttribute(Constants.ARMORED)) {
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.black);
			g.drawRect(1,1,cs-1,cs-1);
			g.setStroke(stroke);
		}

		drawEmployer(g);
		drawHiddenStatus(g);
		drawAttentionMarkers(g);
		drawDamageAssessment(g);
	}

	// BattleChit Interface
	public boolean targets(BattleChit chit) {
		RealmComponent rc = getTarget();
		RealmComponent rc2 = get2ndTarget();
		return ((rc != null && rc.equals(chit)) || (rc2 != null && rc2.equals(chit)));
	}

	public Integer getLength() {
		Integer length = getFaceAttributeInteger("length");
		if (length == null) {
			length = Integer.valueOf(0); // tooth and claw
		}
		return length;
	}

	public Speed getMoveSpeed() {
		int otherSpeed = getGameObject().getThisInt("move_speed_change");
		if (otherSpeed>0) {
			alteredMoveSpeed = true;
			return new Speed(otherSpeed,speedModifier());
		}
		return new Speed(getFaceAttributeInteger("move_speed"),speedModifier());
	}
	public boolean flies() {
		return getGameObject().hasThisAttribute("flying");
	}
	public Speed getFlySpeed() {
		if (flies()) {
			return getMoveSpeed();
		}
		return null;
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
	
	protected int speedModifier() {
		int mod = 0;
		if (getGameObject().hasThisAttribute(Constants.SLOWED)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.SHRINK)) {
			mod--;
		}
		return mod;
	}
	protected int sizeModifier() {
		int mod = 0;
		if (getGameObject().hasThisAttribute(Constants.SHRINK)) {
			mod--;
		}
		return mod;
	}

	public Speed getAttackSpeed() {
		return new Speed(getFaceAttributeInteger("attack_speed"),speedModifier());
	}

	public Strength getStrength() {
		Strength strength = new Strength(getFaceAttributeString("strength"));
		int mod = sizeModifier();
		if (mod<0) {
			strength.moveRedToMaximum();
		}
		strength.modify(mod);
		if (strength.getChar()!="T" && getGameObject().hasThisAttribute(Constants.STRONG_MF)) {
			strength.modify(1);
		}
		return strength;
	}

	public Harm getHarm() {
		return new Harm(getStrength(), getSharpness());
	}

	public int getSharpness() {
		int sharpness = getFaceAttributeInt("sharpness");
		sharpness += getGameObject().getThisInt(Constants.ADD_SHARPNESS);
		if (sharpness>0) {
			TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
			if (tl!=null && tl.isInClearing() && tl.clearing.hasSpellEffect(Constants.BLUNTED)) {
				sharpness--;
			}
		}
		return sharpness;
	}

	public String getMagicType() {
		return getFaceAttributeString("magic_type");
	}

	public int getManeuverCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBox();
	}
	
	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBox();
	}
	
	public boolean hasAnAttack() {
		return (getFaceAttributeString("strength")!=null && getFaceAttributeString("strength").length()>0)
				|| (getMagicType()!=null && getMagicType().length()>0);
	}

	public Strength getVulnerability() {
		Strength vul =  new Strength(getAttribute("this", "vulnerability"));
		vul.modify(sizeModifier());
		return vul;
	}
	
	public boolean isArmored() {
		return getGameObject().hasThisAttribute(Constants.ARMORED);
	}
	
	public boolean hasActiveShield() {
		ArrayList<GameObject> hold = getGameObject().getHold();
		for (GameObject item : hold) {
			if (item.hasThisAttribute(Constants.SHIELD) && !item.hasThisAttribute(Constants.DESTROYED)) {
				return true;
			}
		}
		return false;
	}
	
	public MonsterPartChitComponent getShield() {
		ArrayList<GameObject> hold = getGameObject().getHold();
		for (GameObject item : hold) {
			if (item.hasThisAttribute(Constants.SHIELD)) {
				return (MonsterPartChitComponent) RealmComponent.getRealmComponent(item);
			}
		}
		return null;
	}

	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs, BattleChit attacker, int box, Harm attackerHarm,int attackOrderPos) {
		Harm harm = new Harm(attackerHarm);
		Strength vulnerability = getVulnerability();
		
		if (getGameObject().hasThisAttribute(Constants.MAGIC_IMMUNITY)) {
			if (attacker.isCharacter()) {
				WeaponChitComponent weapon = ((CharacterChitComponent)attacker).getAttackingWeapon();
				if (weapon!=null && weapon.getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE) && weapon.getFaceAttributeInt(Constants.MAGIC_COLOR_BONUS_SHARPNESS)!=0) {
					String immunity = getGameObject().getThisAttribute(Constants.MAGIC_IMMUNITY);
					ColorMagic monsterImmunityColor = ColorMagic.makeColorMagic(immunity,true);
					ColorMagic weaponMagicColorBonus = ColorMagic.makeColorMagic(weapon.getGameObject().getThisAttribute(Constants.MAGIC_COLOR_BONUS),true);
					if (immunity.matches("prism") || monsterImmunityColor.sameColorAs(weaponMagicColorBonus)) {	
						int weaponBonus = weapon.getFaceAttributeInt(Constants.MAGIC_COLOR_BONUS_SHARPNESS);
						for (int i = 0; i<weaponBonus ; i++) {
							harm.dampenSharpness();
						}
						RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Monster has magic immunity and additional sharpness is ignored: "+harm.toString());
					}
				}
			}
		}
		
		if (!harm.getIgnoresArmor() && isArmored()) {
			harm.dampenSharpness();
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits armor, and reduces sharpness: "+harm.toString());
		}
		if (!harm.getIgnoresArmor() && hasActiveShield()) {
			MonsterPartChitComponent shield = getShield();
			CombatWrapper shieldCombat = new CombatWrapper(getShield().getGameObject());
			if (shieldCombat.getCombatBox() == box) {
				harm.dampenSharpness();
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits shield, thus monster is not killed, and reduces sharpness: "+harm.toString());
				if (harm.getAppliedStrength().strongerThan(shield.getStrength())) {
					shield.setDestroyed(true);
					shieldCombat.setKilledBy(attacker.getGameObject());
					shieldCombat.setKilledLength(attacker.getLength());
					shieldCombat.setKilledSpeed(attacker.getAttackSpeed());
					shieldCombat.setHitByOrderNumber(attackOrderPos);
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Destroys "+this.getName()+"'s shield.");
				}
				return false; // Any attack hitting the shield, does not harm the monster.
			}
		}
	
		Strength applied = harm.getAppliedStrength();
		if (applied.strongerOrEqualTo(vulnerability)) {
			// Dead monster!
			CombatWrapper combat = new CombatWrapper(getGameObject());
			combat.setKilledBy(attacker.getGameObject());
			combat.setKilledLength(attacker.getLength());
			combat.setKilledSpeed(attacker.getAttackSpeed());
			return true;
		}
		return false;
	}

	public boolean isMissile() {
		return gameObject.hasThisAttribute("missile");
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
	public void setHidden(boolean val) {
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
	
	public boolean isMistLike() {
		return getGameObject().hasThisAttribute(Constants.MIST_LIKE);
	}
	public boolean isAbsorbed() {
		GameObject heldBy = getGameObject().getHeldBy();
		return heldBy!=null && heldBy.hasThisAttribute("spell");
	}
}