package com.robin.magic_realm.components;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.graphics.*;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.swing.DieRoller;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class TravelerChitComponent extends StateChitComponent implements BattleChit {
	
	private boolean alteredAttackSpeed = false;
	private boolean alteredMoveSpeed = false;
	private static Hashtable<Integer,ImageIcon> dieIconHash;
	
	protected TravelerChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.FORESTGREEN;
	}
	public String getName() {
	    return TRAVELER;
	}
	public void flip() {
		setFaceUp();
	}
	protected void explode() {
		GameWrapper game = GameWrapper.findGame(getGameObject().getGameData());
		game.addTravelerKnowledge(getGameObject());
		getGameObject().setThisAttribute(Constants.SPAWNED);
	}
	public void setLightSideUp() {
		GameWrapper game = GameWrapper.findGame(getGameObject().getGameData());
		if (game.hasTravelerKnowledge(getGameObject())) return;
		if (isDarkSideUp()) {
			super.setLightSideUp();
			updateSize();
		}
	}
	public void setDarkSideUp() {
		if (isLightSideUp()) {
			super.setDarkSideUp();
			assignTravelerTemplate(); // Needed for RealmViewer!
			updateSize();
		}
	}
	public int getChitSize() {
		return isFaceUp()?M_CHIT_SIZE:super.getChitSize();
	}
	public void assignTravelerTemplate() {
		if (getGameObject().hasThisAttribute(Constants.TEMPLATE_ASSIGNED)) return;
		GamePool pool = new GamePool(getGameObject().getGameData().getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add(Constants.TRAVELER_TEMPLATE);
		query.add("!"+Constants.USED);
		query.add("!notready");
		query.add("test");
		ArrayList<GameObject> list = pool.find(query);
		if (list.isEmpty()) { // if there are no more "test" templates
			query.remove("test");
			list = pool.find(query);
			if (list.isEmpty()) { // if there are no more "finished" templates, then choose from the rest
				query.remove("!notready");
				list = pool.find(query);
			}
		}
		
		 // assume there are ALWAYS enough templates
		int r = RandomNumber.getRandom(list.size());
		GameObject template = list.get(r);
		assignTravelerTemplate(template);
	}
	private void assignTravelerTemplate(GameObject template) {
		getGameObject().setName(template.getName());
		getGameObject().copyAttributeBlockFrom(template,"this");
		getGameObject().removeThisAttribute(Constants.TRAVELER_TEMPLATE);
		getGameObject().setThisAttribute(Constants.TEMPLATE_ASSIGNED);
		getGameObject().addAll(template.getHold());
		template.setThisAttribute(Constants.USED);
	}
	private static ImageIcon getDieIcon(int val){
		if (dieIconHash==null) {
			dieIconHash = new Hashtable<>();
			for (int i=1;i<=6;i++) {
				DieRoller dr = new DieRoller(String.valueOf(i),16,4);
				dr.setAllRed();
				dieIconHash.put(Integer.valueOf(i),dr.getIcon());
			}
		}
		return dieIconHash.get(Integer.valueOf(val));
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			updateSize();
			String icon_type = "";
			String iconDir = "";
			double size = 0.7;
			int yOffset = 0;
			if (isDisplayStyleAlternative() && gameObject.hasThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE) && gameObject.hasThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE)) {
				icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE);
				iconDir = gameObject.getThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE);
				if (gameObject.hasThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE)) {
					size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE));
				}
				if (gameObject.hasThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE)) {
					yOffset = gameObject.getThisInt(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE);
				}
			}
			else {
				icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
				iconDir = gameObject.getThisAttribute(Constants.ICON_FOLDER);
				if (gameObject.hasThisAttribute(Constants.ICON_SIZE)) {
					size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE));
				}
			}
			drawIcon(g, iconDir, icon_type, size, 0, yOffset, null);
			
			tt = new TextType(getGameObject().getName(),getChitSize(),"WHITE_NOTE");
			tt.draw(g,0,3,Alignment.Center);
			
			ImageIcon die = getDieIcon(getGameObject().getThisInt("monster_die"));
			g.drawImage(die.getImage(),50,31,null);
			if (getGameObject().hasThisAttribute("monster_die2")) {
				ImageIcon die2 = getDieIcon(getGameObject().getThisInt("monster_die2"));
				g.drawImage(die2.getImage(),50,0,null);
			}
			
			int x = 5;
			int y = 35; 
			int rad = 20;
			if (getGameObject().hasThisAttribute("base_price")) {
				int price = getGameObject().getThisInt("base_price");
				g.setColor(Color.black);
				g.fillOval(x, y, rad, rad);
				g.setColor(MagicRealmColor.GOLD);
				g.fillOval(x + 1, y + 1, rad - 2, rad - 2);
				g.setColor(Color.black);
				g.setFont(Constants.ATTRIBUTE_FONT);
				GraphicsUtil.drawCenteredString(g,x,y-2,rad,rad,String.valueOf(price));
			}
			else if (getGameObject().hasThisAttribute(Constants.CAPTURE)) {
				int capture = getGameObject().getThisInt(Constants.CAPTURE);
				String val = String.valueOf(capture);
				if (capture>=0) {
					val = "+"+val;
				}
				g.setColor(Color.black);
				g.fillOval(x, y, rad, rad);
				g.setColor(Color.white);
				GraphicsUtil.drawCenteredString(g,x,y-2,rad,rad,val);
			}
			else if (getGameObject().hasThisAttribute(Constants.STORE)) {
				g.setColor(Color.green);
				g.fillRect(x,y,rad-2,rad-2);
				g.setColor(Color.black);
				g.drawRect(x,y,rad-2,rad-2);
				GraphicsUtil.drawCenteredString(g,x,y-2,rad,rad,"$$");
			}
			else if (getGameObject().hasThisAttribute(Constants.DOPPLEGANGER)) {
				g.setColor(Color.white);
				g.fillRect(x, y, rad-4, rad-4);
			}
			String vul = getGameObject().getThisAttribute("vulnerability");
			if (vul!=null) {
				x = 53;
				y = 16;
				boolean armored = getGameObject().hasThisAttribute(Constants.ARMORED);
				g.setColor(Color.black);
				g.fillOval(x, y, rad, rad);
				g.setColor(armored?Color.lightGray:Color.yellow);
				g.fillOval(x + 1, y + 1, rad - 2, rad - 2);
				
				g.setFont(Constants.VUL_FONT);
				g.setColor(Color.black);
				g.drawString(vul,57,32);
			}
			// Add the NOT READY flag, if needed
			if (gameObject.hasThisAttribute("notready")) {
				tt = new TextType("UNFINISHED",getChitSize(),"TITLE_RED");
				tt.setRotate(25);
				tt.draw(g,0,20,Alignment.Center);
			}
			drawAttributes(g);
			drawEmployer(g);
			drawHiddenStatus(g);
			drawDamageAssessment(g);
		}
	}
	private void drawAttributes(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		String strength = getGameObject().getThisAttribute("strength");
		String attackSpeedString = getGameObject().getThisAttribute("attack_speed");
		int sharpness = getGameObject().getThisInt("sharpness");
		if (attackSpeedString!=null) {
			alteredAttackSpeed = false;
			alteredMoveSpeed = false;
			int attackSpeed = getAttackSpeed().getNum();
			TextType tt = new TextType(strength + attackSpeed, getChitSize(),alteredAttackSpeed?"YELLOW_BOLD":"WHITE_NOTE");
			int x = 5;
			int y = getChitSize() - 5 - tt.getHeight(g);
			tt.draw(g, x, y, Alignment.Left);
			x += tt.getWidth(g) + 4;
			y += tt.getHeight(g) - 6;
			g.setColor(Color.white);
			for (int i = 0; i < sharpness; i++) {
				StarShape star = new StarShape(x, y, 5, 7);
				g.fill(star);
				x += 10;
			}
			
			String moveString = getGameObject().getThisAttribute("move_speed");
			tt = new TextType(moveString, getChitSize(), alteredMoveSpeed?"YELLOW_BOLD":"WHITE_NOTE");
			x = getChitSize() - 5 - tt.getWidth(g);
			y = getChitSize() - 5 - tt.getHeight(g);
			tt.draw(g, x, y, Alignment.Left);
		}
	}
	public boolean applyHit(GameWrapper game, HostPrefWrapper hostPrefs, BattleChit attacker, int box, Harm attackerHarm, int attackOrderPos) {
		CombatWrapper combat = new CombatWrapper(getGameObject());
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
		Strength vulnerability = new Strength(getThisAttribute( "vulnerability"));
		if (!harm.getIgnoresArmor() && getGameObject().hasThisAttribute(Constants.ARMORED)) {
			harm.dampenSharpness();
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits armor, and reduces sharpness: "+harm.toString());
		}
		else if (!getGameObject().hasThisAttribute(Constants.ARMORED) && getGameObject().hasThisAttribute(Constants.BARKSKIN)) {
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
			// Dead traveler!
			combat.setKilledBy(attacker.getGameObject());
			combat.setKilledLength(attacker.getLength());
			combat.setKilledSpeed(attacker.getAttackSpeed());
			if (!hostPrefs.hasPref(Constants.SR_ENDING_COMBAT)) return true;
		}
		return false;
	}
	public void changeWeaponState(HostPrefWrapper hostPrefs) {
		// Do nothing
	}
	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBoxAttack();
	}
	public Speed getAttackSpeed() {
		String specialType = getGameObject().getThisAttribute(Constants.ATTACK_SPEED_TARGET);
		if (specialType!=null) {
			RealmComponent target = getTarget();
			if (target!=null && target.getGameObject().getName().startsWith(specialType)) {
				alteredAttackSpeed = true;
				return new Speed(getGameObject().getThisInt(Constants.ATTACK_SPEED_NEW));
			}
		}
		
		return new Speed(Integer.valueOf(getGameObject().getThisInt("attack_speed")),speedModifier());
	}
	public Speed getFlySpeed() {
		return null; // For now, there are no flying travelers.
	}
	public Harm getHarm() {
		return new Harm(getStrength(), getSharpness(),isIgnoreArmor());
	}
	private boolean isIgnoreArmor() {
		String ignoreArmorRegex = getGameObject().getThisAttribute("ignore_armor");
		if (ignoreArmorRegex!=null) {
			RealmComponent rc = getTarget();
			if (rc!=null) {
				String icon = rc.getGameObject().getAttribute(rc.getThisBlock(),"icon_type");
				Pattern regex = Pattern.compile(ignoreArmorRegex,Pattern.CASE_INSENSITIVE);
				return regex.matcher(icon).matches();
			}
		}
		return false;
	}
	public Integer getLength() {
		return Integer.valueOf(getThisInt("length"));
	}
	public String getMagicType() {
		return null; // For now, there are no magic flinging travelers.
	}
	public String getAttackSpell() {
		return null; // For now, there are no magic flinging travelers.
	}
	public int getManeuverCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBoxDefense();
	}
	public String getMissileType() {
		return gameObject.getThisAttribute("missile");
	}
	public Speed getMoveSpeed() {
		int otherSpeed = getGameObject().getThisInt("move_speed_change");
		if (otherSpeed>0) {
			alteredMoveSpeed = true;
			return new Speed(Integer.valueOf(otherSpeed),speedModifier());
		}
		return new Speed(Integer.valueOf(getThisInt("move_speed")),speedModifier());
	}
	public boolean hasAnAttack() {
		return (getThisAttribute("strength")!=null && getThisAttribute("strength").length()>0)
				|| (getMagicType()!=null && getMagicType().length()>0);
	}
	public boolean hitsOnTie() {
		return false; // default
	}
	public boolean isMissile() {
		return gameObject.hasThisAttribute("missile");
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
		return mod;
	}
	public Strength getStrength() {
		Strength strength = new Strength(getThisAttribute("strength"));
		strength.modify(sizeModifier());
		if (strength.getChar()!="T" && SpellUtility.affectedByBewitchingSpellKey(getGameObject(),Constants.STRONG_MF)) {
			strength.modify(1);
		}
		return strength;
	}
	public int getSharpness() {
		int sharpness = getThisInt("sharpness");
		sharpness += getGameObject().getThisInt(Constants.ADD_SHARPNESS);
		if (sharpness>0) {
			TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
			if (tl!=null && tl.isInClearing() && tl.clearing.hasSpellEffect(Constants.BLUNTED)) {
				sharpness--;
			}
		}
		return sharpness;
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
	private static String getInfo(String delim,GameObject go) {
		StringBuffer sb = new StringBuffer();
		sb.append(go.hasThisAttribute("notready")?"*":"");
		sb.append(go.hasThisAttribute("test")?"test":"");
		sb.append(delim);
		sb.append(go.getName());
		//if (go.getName().length()<7) sb.append(delim);
		sb.append(delim);
		sb.append(go.hasThisAttribute(Constants.STORE)?"STORE":(go.hasThisAttribute(Constants.CAPTURE)?"CAPTURE":"HIRE"));
		sb.append(delim);
		if (go.hasThisAttribute(Constants.CAPTURE)) {
			sb.append(go.getThisInt(Constants.CAPTURE));
		}
		else if (go.hasThisAttribute("base_price")) {
			sb.append(go.getThisInt("base_price"));
		}
		sb.append(delim);
		sb.append(go.getThisAttribute("text"));
		return sb.toString();
	}
	public static void main(String[] args) {
		RealmLoader loader = new RealmLoader();
		GamePool pool = new GamePool(loader.getData().getGameObjects());
		String delim = "\t";
		int n=1;
		for (GameObject go:pool.find("traveler_template")) {
			System.out.println((n++)+delim+getInfo(delim,go));
		}
	}
}