package com.robin.magic_realm.components;

import java.awt.*;
import java.util.*;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.swing.DieRoller;
import com.robin.general.swing.ImageCache;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.utility.TreasureUtility.ArmorType;
import com.robin.magic_realm.components.wrapper.*;

public class CharacterChitComponent extends RoundChitComponent implements BattleChit,Horsebackable {

	public static final int DISPLAY_STYLE_CLASSIC = 0;
	public static final int DISPLAY_STYLE_LEGENDARY_CLASSIC = 1;
	public static final int DISPLAY_STYLE_LEGENDARY = 2;
	public static final String HIDDEN = LIGHT_SIDE_UP;
	public static final String UNHIDDEN = DARK_SIDE_UP;
	
	public static int displayStyle = DISPLAY_STYLE_CLASSIC;
	
	private RealmComponent AttackChit;
	
	public CharacterChitComponent(GameObject obj) {
		super(obj);
		lightColor = MagicRealmColor.FORESTGREEN;
		darkColor = MagicRealmColor.PEACH;
	}

	public void setHidden(boolean val) {
		if (val) {
			setLightSideUp();
		}
		else {
			setDarkSideUp();
		}
	}

	public boolean isHidden() {
		return isLightSideUp();
	}

	public String getName() {
		return CHARACTER;
	}

	private static boolean imageExists(String iconName, String iconFolder) {
		return ImageCache.iconExists(iconFolder+"/" + iconName);
	}
	
	private boolean legendaryImageExists() {
		String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE);
		String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
		iconFolder = iconFolder+"_legendary";
		if (isHidden()) {
			iconName=iconName+"_h";
		}
		return imageExists(iconName, iconFolder);
	}
	
	private boolean legendaryClassicImageExists() {
		String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE);
		String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
		iconFolder = iconFolder+"_legendary_classic";
		if (isHidden()) {
			iconName=iconName+"_h";
		}
		return imageExists(iconName, iconFolder);
	}
	
	public int getChitSize() {
		if (displayStyle == DISPLAY_STYLE_LEGENDARY && legendaryImageExists()) {
			return T_CHIT_SIZE-ChitComponent.SHADOW_BORDER;
		}
		if (displayStyle == DISPLAY_STYLE_LEGENDARY_CLASSIC && legendaryClassicImageExists()) {
			return T_CHIT_SIZE-ChitComponent.SHADOW_BORDER;
		}
		return T_CHIT_SIZE;
	}
	
	public ImageIcon getSmallSymbol() {
		String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
		String iconType = gameObject.getThisAttribute(Constants.ICON_TYPE);
		return ImageCache.getIcon(iconFolder+"/" + iconType, 25);
	}

	public void paintComponent(Graphics g1) {
		if (displayStyle == DISPLAY_STYLE_LEGENDARY && legendaryImageExists()) {
			super.paintComponent(g1, false);
		}
		else if (displayStyle == DISPLAY_STYLE_LEGENDARY_CLASSIC && legendaryClassicImageExists()) {
			super.paintComponent(g1, false);
		}
		else {
			super.paintComponent(g1, true);
		}
		
		Graphics2D g = (Graphics2D)g1;
		
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject transmorph = character.getTransmorph();
		if (transmorph != null) {
			// Draw image
			String icon_type = transmorph.getThisAttribute(Constants.ICON_TYPE);
			if (icon_type != null) {
				boolean transformSkin = false;
				String iconDir = transmorph.getThisAttribute(Constants.ICON_FOLDER);
				if (displayStyle == DISPLAY_STYLE_LEGENDARY) {
					String iconDir_l = "characters_legendary";
					String icon_type_l = icon_type;
					if (isHidden()) {
						icon_type_l=icon_type_l+"_h";
					}
					if (imageExists(icon_type_l,iconDir_l)) {
						drawIcon(g,iconDir_l, icon_type_l, 0.26);
						transformSkin = true;
					}
				}
				else if (displayStyle == DISPLAY_STYLE_LEGENDARY_CLASSIC) {
					String iconDir_l = "characters_legendary_classic";
					String icon_type_l = icon_type;
					if (isHidden()) {
						icon_type_l=icon_type_l+"_h";
					}
					if (imageExists(icon_type_l,iconDir_l)) {
						drawIcon(g,iconDir_l, icon_type_l, 0.26);
						transformSkin = true;
					}
				}
				if (!transformSkin) {
					if (useColorIcons()) {
						iconDir = iconDir+"_c";
					}
					drawIcon(g,iconDir, icon_type, 0.75);
				}
			}
			icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
			if (icon_type != null && displayStyle == DISPLAY_STYLE_CLASSIC) {
				String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
				int offset = (getChitSize()>>2);
				drawIcon(g, iconFolder, icon_type, 0.30,0,offset,BACKING);
			}
		}
		else {
			// Draw image
			if (gameObject.hasThisAttribute(Constants.SUPER_REALM) && gameObject.hasThisAttribute(Constants.ICON_TYPE+"_sr") && gameObject.hasThisAttribute(Constants.ICON_TYPE+"_sr")) {
				String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE+"_sr");
				String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER+"_sr");
				Shape shape = getShape(SHADOW_BORDER,SHADOW_BORDER,getChitSize()-SHADOW_BORDER);
				if (isHidden()) {
					g.setColor(Color.green);
				} else {
					g.setColor(Color.white);
				}
				g.fill(shape);
				drawIcon(g,iconFolder,iconName,1.2);
			}
			else {
				String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE);
				String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
				if (iconName!=null && iconFolder!=null) {
					if (displayStyle == DISPLAY_STYLE_LEGENDARY && legendaryImageExists()) {
						iconFolder = iconFolder+"_legendary";
						if (isHidden()) {
							iconName=iconName+"_h";
						}
						drawIcon(g,iconFolder,iconName,0.26);
					}
					else if (displayStyle == DISPLAY_STYLE_LEGENDARY_CLASSIC && legendaryClassicImageExists()) {
						iconFolder = iconFolder+"_legendary_classic";
						if (isHidden()) {
							iconName=iconName+"_h";
						}
						drawIcon(g,iconFolder,iconName,0.26);
					}
					else {
						drawIcon(g,iconFolder,iconName,0.75);
					}
				}
			}
		}

		if (character.isFortified()) {
			g.setColor(Color.blue);
			Stroke old = g.getStroke();
			g.setStroke(Constants.THICK_STROKE);
			int m = T_CHIT_SIZE>>1;
			if ((displayStyle == DISPLAY_STYLE_LEGENDARY && legendaryImageExists())
					 || (displayStyle == DISPLAY_STYLE_LEGENDARY_CLASSIC && legendaryClassicImageExists())) {
				g.drawOval(0,0,2*m,2*m);
			}
			else {
				g.drawLine(4,m-4,T_CHIT_SIZE-4,m-4);
				g.drawLine(4,m+4,T_CHIT_SIZE-4,m+4);
			}
			g.setStroke(old);
		}
		
		// Show Wish Strength, if any
		Strength wishStrength = character.getWishStrength();
		if (wishStrength != null) {
			TextType tt = new TextType("HARM=" + wishStrength.toString(), T_CHIT_SIZE, "CLOSED_RED");
			tt.draw(g, 0, (T_CHIT_SIZE - (T_CHIT_SIZE >> 2)), Alignment.Center);
		}

		drawAttentionMarkers(g);
		drawDamageAssessment(g);
	}

	// BattleChit Interface - by itself, a character has no stats - its all in the chits
	public boolean targets(BattleChit chit) {
		RealmComponent rc = getTarget();
		RealmComponent rc2 = get2ndTarget();
		return ((rc != null && rc.equals((RealmComponent)chit)) || (rc2 != null && rc2.equals((RealmComponent)chit)));
	}
	
	public Integer getLength() {
		MonsterChitComponent transmorph = getTransmorphedComponent();
		if (transmorph!=null) {
			return transmorph.getLength();
		}
		int length = 0; // default length (dagger)
		// Derive this from the weapon used.
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
		RealmComponent rc = getAttackChit();
		CombatWrapper combatChit = null;
		if (rc!=null) {
			combatChit = new CombatWrapper(rc.getGameObject());
		}
		if (weapons != null) {
			for (WeaponChitComponent weapon : weapons) {
				if (combatChit==null || combatChit.getWeaponId().equals(weapon.getGameObject().getStringId())) {
					CombatWrapper wCombat = new CombatWrapper(weapon.getGameObject());
					if (wCombat.getCombatBox()>0) {
						if (length < weapon.getLength()) {
							length = weapon.getLength();
						}
					}
				}
			}
		}
		for (GameObject tw : getTreasureWeaponObjects()) {
			if (tw!=null && (combatChit==null || combatChit.getWeaponId().equals(tw.getStringId()))) {
				if (length < tw.getThisInt("length")) {
					length = tw.getThisInt("length");
				}
			}
		}
		return Integer.valueOf(length);
	}

	public String getLightSideStat() {
		return "this"; // only one stat side
	}

	public String getDarkSideStat() {
		return "this"; // only one stat side
	}

	public MonsterChitComponent getTransmorphedComponent() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject transmorph = character.getTransmorph();
		if (transmorph != null) {
			return (MonsterChitComponent) RealmComponent.getRealmComponent(transmorph);
		}
		return null;
	}

	public RealmComponent getManeuverChit() {
		return getManeuverChit(true);
	}
	public RealmComponent getManeuverChit(boolean includeHorse) {
		RealmComponent rc = null;
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject transmorph = character.getTransmorph();
		if (transmorph == null) {
			rc = BattleUtility.findMoveComponentWithCombatBox(character.getMoveSpeedOptions(new Speed(), true,false),includeHorse);
		}
		else {
			// Maneuvers are handled in the Character object
			rc = this;
		}
		return rc;
	}
	
	public Speed getMoveSpeed() {
		return getMoveSpeed(true);
	}

	/**
	 * @return The speed of the character's maneuver, which might be "stopped" if none was played
	 */
	public Speed getMoveSpeed(boolean includeHorse) {
		// Find the character's maneuver for this round
		RealmComponent rc = getManeuverChit(includeHorse);
		if (rc != null) {
			if (rc == this) {
				return BattleUtility.getMoveSpeed(getTransmorphedComponent());
			}
			return BattleUtility.getMoveSpeed(rc);
		}
		return new Speed();
	}
	
	public Speed getFlySpeed() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		StrengthChit flyChit = character.getFastestFlyStrengthChit(false);
		if (flyChit!=null) {
			return flyChit.getSpeed();
		}
		return null;
	}

	public RealmComponent getAttackChit() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject transmorph = character.getTransmorph();
		if (transmorph != null) {
			// Fight is handled in the transmorphed monster object
			return RealmComponent.getRealmComponent(transmorph);
		}
		return this.AttackChit;
	}
	public void setAttackChit(RealmComponent rc) {
		this.AttackChit = rc;
	}

	public boolean hasAnAttack() {
		return getAttackCombatBox()>0;
	}
	
	public WeaponChitComponent getAttackingWeapon() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		RealmComponent rc = getAttackChit();
		if (rc != null) {
			ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
			if (weapons != null) {
				CombatWrapper combatChit = new CombatWrapper(rc.getGameObject());
				for (WeaponChitComponent weapon : weapons) {
					if (combatChit.getWeaponId().equals(weapon.getGameObject().getStringId())) {
						CombatWrapper combat = new CombatWrapper(weapon.getGameObject());
						if (combat.getCombatBox() > 0) { // only if it was played!
							return weapon;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @return The speed of the character's attack, which might be "stopped" if none was played
	 */
	public Speed getAttackSpeed() {
		// Find the character's attack for this round
		Speed speed = new Speed();
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		RealmComponent rc = getAttackChit();
		if (rc != null) {
			speed = BattleUtility.getFightSpeed(rc);
			// Weapon speed overrides anything else
			ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
			if (weapons != null) {
				CombatWrapper combatChit = new CombatWrapper(rc.getGameObject());
				for (WeaponChitComponent weapon : weapons) {
					if (combatChit.getWeaponId().equals(weapon.getGameObject().getStringId())) {
						CombatWrapper combat = new CombatWrapper(weapon.getGameObject());
						if (combat.getCombatBox() > 0) { // only if it was played!
							Speed weaponSpeed = weapon.getSpeed();
							if (weaponSpeed != null) {
								speed = weaponSpeed;
							}
						}
					}
				}
			}
		}
		return speed;
	}
	
	/**
	 * Returns a GameObject that is either a WeaponChitComponent, or a TreasureCardComponent with an attack attribute (Alchemists's Mixture)
	 */
	public ArrayList<GameObject> getActiveWeaponsObjects() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		ArrayList<GameObject> weaponsGameObjects = new ArrayList<>();
		ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
		if (weapons != null) {
			for (WeaponChitComponent weapon : weapons) {
				weaponsGameObjects.add(weapon.getGameObject());
			}
		}
		if (getTreasureWeaponObjects() != null) {
			for (GameObject tw : getTreasureWeaponObjects()) {
				weaponsGameObjects.add(tw);
			}
		}
		return weaponsGameObjects;
	}
	
	public ArrayList<GameObject> getTreasureWeaponObjects() {
		ArrayList<GameObject> items = new ArrayList<>();
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		for (GameObject item : character.getActiveInventory()) {
			if (item.hasThisAttribute("attack")) {
				items.add(item);
			}
		}
		return items;
	}

	/**
	 * @return The total harm the attack would cause. It is up to the caller to handle reducing sharpness or harm based on factors like armor or using the missile table (or fumble table)
	 */
	public Harm getHarm() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		Strength wishStrength = character.getWishStrength();
		if (wishStrength != null) {
			Harm harm = new Harm(wishStrength, 0);
			harm.setAdjustable(false);
			return harm;
		}
		Strength weaponStrength = new Strength(); // negligible strength (dagger) to start
		int sharpness = 1; // default of a dagger
		sharpness += getGameObject().getThisInt(Constants.ADD_SHARPNESS); // in case poison is applied to a dagger
		boolean ignoreArmor = getGameObject().hasThisAttribute(Constants.IGNORE_ARMOR); // false, unless penetrating grease was applied to dagger

		RealmComponent rc = getAttackChit();
		if (rc != null) {
			if (rc.isMonster()) { // character is transmorphed!
				return ((BattleChit) rc).getHarm();
			}
			boolean hasWeapon = false;
			boolean missileWeapon = false;
			Harm baseHarm = getHarmForRealmComponent(rc); // harm from the attack (ignoring the weapon)
			CombatWrapper combatChit = new CombatWrapper(rc.getGameObject());
			ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
			if (weapons != null) {
				for (WeaponChitComponent weapon : weapons) {
					if (combatChit.getWeaponId().equals(weapon.getGameObject().getStringId()))
						if (weapon.getGameObject().hasThisAttribute(Constants.IGNORE_ARMOR)) {
							ignoreArmor = true;
						}
						CombatWrapper wCombat = new CombatWrapper(weapon.getGameObject());
						if (wCombat.getCombatBox()>0) {
							hasWeapon = true;
							missileWeapon = weapon.isMissile();
							weaponStrength = weapon.getStrength();
							sharpness = weapon.getSharpness();
						}
					}
			}
			if (!hasWeapon) {
				// Check for treasure weapons
				for (GameObject tw : getTreasureWeaponObjects()) {
					if (tw!=null && combatChit.getWeaponId().equals(tw.getStringId())) {
						if (tw.hasThisAttribute(Constants.IGNORE_ARMOR)) {
							ignoreArmor = true;
						}
						hasWeapon = true;
						missileWeapon = tw.hasThisAttribute("missile");
						weaponStrength = new Strength(tw.getThisAttribute("strength"));
						sharpness = tw.getThisInt("sharpness");
						sharpness += tw.getThisInt(Constants.ADD_SHARPNESS);
						break;
					}
				}
			}
			if (!hasWeapon && getGameObject().hasThisAttribute(Constants.FIGHT_NO_WEAPON)) {
				weaponStrength = baseHarm.getStrength();
				sharpness = 0;
			}
			if (!missileWeapon && baseHarm.getStrength().strongerThan(weaponStrength)) {
				weaponStrength.bumpUp();
			}
			if (combatChit.getGameObject().hasThisAttribute(Constants.FINAL_CHIT_HARM)) {
				Strength chitStrength = new Strength(combatChit.getGameObject().getThisAttribute(Constants.FINAL_CHIT_HARM));
				if (chitStrength.strongerThan(weaponStrength)) {
					weaponStrength = chitStrength;
				}
			}
		}

		Harm totalHarm = new Harm(weaponStrength, sharpness, ignoreArmor);
		return totalHarm;
	}
	
	private static Harm getHarmForRealmComponent(RealmComponent rc) {
		Harm harm = null;
		if (rc.isTreasure() && rc.getGameObject().hasThisAttribute("strength")) {
			// This wont work for weapons, but that's what I want here!  Weapons are in ADDITION to fight strength.
			Strength strength = new Strength(rc.getGameObject().getThisAttribute("strength"));
			harm = new Harm(strength,0);
		}
		if (rc.isBattleChit()) { // this handles character action chits, natives, and horses
			BattleChit bc = (BattleChit)rc;
			harm = bc.getHarm();
		}
		return harm;
	}

	public boolean hasNaturalArmor() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		return getGameObject().hasThisAttribute(Constants.ARMORED)
				|| character.affectedByKey(Constants.ADDS_ARMOR);
	}
	
	/**
	 * @return The first piece of armor that would be hit by the specified box number. (chits before cards)
	 */
	private RealmComponent getArmor(Speed attackSpeed,int box,int attackOrderPos) {
		ArrayList<RealmComponent> armors = getArmors(attackSpeed,box,attackOrderPos);
		if (armors!=null && !armors.isEmpty()) {
			return armors.get(0); // Simply return the first (its sorted)
		}
		return null;
	}
	private ArrayList<RealmComponent> getArmors(Speed attackerSpeed,int box,int attackOrderPos) {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		ArrayList<RealmComponent> armors = new ArrayList<>();
		
		ArrayList<WeaponChitComponent> activeWeapons = character.getActiveWeapons();
		for (CharacterActionChitComponent chit : character.getActiveFightChits()) {
			CombatWrapper combatChit = new CombatWrapper(chit.getGameObject());
			if(!combatChit.getPlacedAsParryShield()) continue;
			if (chit.getSpeed().fasterThanOrEqual(attackerSpeed) || combatChit.getCombatBox() == box) {
				if (combatChit.getWeaponId() == null) {
					armors.add(chit);
					continue;
				}
				for (WeaponChitComponent weapon : activeWeapons) {
					if (combatChit.getWeaponId().equals(weapon.getGameObject().getStringId())) {
						armors.add(weapon);
						break;
					}
				}
			}
		}
		
		ArrayList<GameObject> search = new ArrayList<>();
		search.addAll(character.getActiveInventory());
		if (character.isFortified()) {
			search.add(character.getGameObject());
		}
		
		for (GameObject go:search) {
			RealmComponent item = RealmComponent.getRealmComponent(go);
			CombatWrapper combat = new CombatWrapper(go);
			if (combat.getKilledBy() == null || combat.getHitByOrderNumber()==attackOrderPos) { // not destroyed or simultaneous hit
				ArmorType armorType = TreasureUtility.getArmorType(go);
				if (armorType==ArmorType.Special) {
					int boxNum = item.getGameObject().getThisInt("armor_box");
					if (boxNum == 0 || boxNum == box) {
						armors.add(item);
					}
				}
				else if (armorType!=ArmorType.None) {
					if (armorType==ArmorType.Shield) {
						if (combat.getCombatBox() == box) {
							armors.add(item);
						}
					}
					else if (armorType==ArmorType.Helmet && box == 3) {
						armors.add(item);
					}
					else if (armorType==ArmorType.Breastplate && box < 3) {
						armors.add(item);
					}
					else if (armorType==ArmorType.Armor) {
						armors.add(item);
					}
				}
				else if (item.isCharacter()) {
					armors.add(item); // fortification
				}
			}
		}
		if (armors.size() > 0) {
			// Sort chits ahead of treasure cards (exception: Ointment of Steel ahead of full suit of armor), and fortification to the front
			Collections.sort(armors, new Comparator<RealmComponent>() {
				public int compare(RealmComponent o1, RealmComponent o2) {
					int ret = 0;

					RealmComponent r1 = o1;
					RealmComponent r2 = o2;

					// Sort first by armor row (row 1 is the shield row); parrying weapon is in shield row
					int armorRow1 = r1.getGameObject().getThisInt("armor_row");
					int armorRow2 = r2.getGameObject().getThisInt("armor_row");
					if (r1.isWeapon()) armorRow1 = 1;
					if (r2.isWeapon()) armorRow2 = 1;
					ret = armorRow1 - armorRow2;
					if (ret == 0) {
						// handle Ointment of Steel + suit of armor cases (roundabout way to determine if r2 is ointment of steel) */
						if (r1 instanceof ArmorChitComponent) {
							if (((ArmorChitComponent) r1).isSuitOfArmorType() && (!r2.isChit() && r2.getGameObject().getThisInt("armor_row") == 3)) {
								return 1;
							}
						}
						else if (r2 instanceof ArmorChitComponent) {
							if ((!r1.isChit() && r1.getGameObject().getThisInt("armor_row") == 3) && ((ArmorChitComponent)r2).isSuitOfArmorType()) {
								return -1;
							}
						}
						else {
							// Then by chit vs card
							int score1 = r1.isChit() ? 0 : 1;
							int score2 = r2.isChit() ? 0 : 1;
							ret = score1 - score2;
						}
					}
					return ret;
				}
			});
			return armors;
		}
		return null;
	}
	
	/**
	 * @return true if damage was applied in some way
	 */
	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs, BattleChit attacker, int box, Harm attackerHarm,int attackOrderPos) {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		CombatWrapper combat = new CombatWrapper(getGameObject());
		
		boolean damageTaken = false;

		// Start off with the assumption that the character was NOT killed
		boolean characterWasKilled = false;

		ArrayList<SpellWrapper> holyShields = SpellUtility.getBewitchingSpellsWithKey(getGameObject(),Constants.HOLY_SHIELD);
		if ((holyShields!=null&&!holyShields.isEmpty()) || combat.hasHolyShield(attacker.getAttackSpeed(),attacker.getLength())) {
			for (SpellWrapper spell : holyShields) {
				spell.expireSpell();
			}
			combat.setHolyShield(attacker.getAttackSpeed(), attacker.getLength());
			RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits Holy Shield and attack is blocked.");
			return false;
		}
		
		MonsterChitComponent transmorph = getTransmorphedComponent();
		if (transmorph != null) {
			boolean ret = transmorph.applyHit(game,hostPrefs, attacker, box, attackerHarm,attackOrderPos);
			CombatWrapper monCombat = new CombatWrapper(transmorph.getGameObject());
			if (monCombat.getKilledBy() != null) {
				combat.setKilledBy(attacker.getGameObject());
				combat.setKilledLength(attacker.getLength());
				combat.setKilledSpeed(attacker.getAttackSpeed());
				ret = true;
			}
			return ret;
		}
		
		Harm harm = new Harm(attackerHarm); // clone the harm
		if (harm.getStrength().isRed()) {
			// character is killed automatically if hit by a red side up monster
			characterWasKilled = true;
		}
		else {
			// First thing, check to see if a Horse maneuver was played
			SteedChitComponent horse = (SteedChitComponent) character.getActiveSteed(attackOrderPos);
			if (horse != null && !combat.isTargetingRider(attacker.getGameObject())) {
				CombatWrapper horseCombat = new CombatWrapper(horse.getGameObject());
				if (horseCombat.getCombatBox() > 0) {
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits the "
							+getGameObject().getNameWithNumber()+"'s "
							+horse.getGameObject().getNameWithNumber());
					// Horse was active and played - it takes the hit!
					return horse.applyHit(game,hostPrefs, attacker, box, harm,attackOrderPos);// INSTEAD of the character!
				}
			}

			Strength vulnerability = character.getVulnerability();

			// Find armor (if any) at box - chits before cards...
			RealmComponent armor = null;
			boolean isDragonBreath = attacker.isMissile() && attacker.getGameObject().hasThisAttribute("dragon_missile");
			if (harm.getAppliedStrength().strongerThan(new Strength("T")) && attacker.isMissile()) {
				// If harm exceeds T, armor is ignored, and target is killed, regardless of armor (damn!)
				harm.setIgnoresArmor(true);
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Harm is greater than Tremendous ("+harm+")!");
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Missile attack hits a vital unarmored spot!");
			}
			else if (attacker.isMissile() && (hostPrefs.hasPref(Constants.OPT_PENETRATING_ARMOR) || (attacker.isCharacter() && (new CharacterWrapper(attacker.getGameObject())).affectedByKey(Constants.SHARPSHOOTER)))) {
				// When Penetrating Armor is in play, and the attack is a missile attack, then the armor is never actually "hit".
				
				/*
				 * Harm exceeds T			= target is killed, regardless of armor
				 * Armor exceeds Harm		= harm does nothing
				 * Armor equals Harm		= target takes one wound, but nothing else happens (no damaged/destroyed armor)
				 * Harm exceeds Armor		= harm drops by one level (in ADDITION to sharpness already lost), and inflicts the target
				 * 
				 * Multiple layers can cause multiple reductions
				 */
				
				if (harm.getAppliedStrength().strongerThan(new Strength("T"))) {
				}
				else {
					ArrayList<RealmComponent> armors = getArmors(attacker.getAttackSpeed(),box,attackOrderPos);
					if (armors!=null && !armors.isEmpty()) {
						harm.dampenSharpness();
						RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits armor, and reduces sharpness: "+harm.toString());
						
						for (RealmComponent test:armors) {
							if (isDragonBreath && test.getGameObject().hasThisAttribute(Constants.IMMUNE_BREATH)) {
								harm = new Harm(new Strength(),0); // negate harm!
								RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Dragon breath attack is stopped by "+test.getGameObject().getNameWithNumber());
							}
							else {
								Strength armorVulnerability = new Strength(test.getGameObject().getThisAttribute("vulnerability"));
								if (test.getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE) && test.getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ARMOR)) {
									String immunity = attacker.getGameObject().getThisAttribute(Constants.MAGIC_IMMUNITY);
									ColorMagic attackerImmunityColor = ColorMagic.makeColorMagic(immunity,true);
									ColorMagic itemMagicColorBonus = ColorMagic.makeColorMagic(test.getGameObject().getThisAttribute(Constants.MAGIC_COLOR_BONUS),true);
									if (immunity==null || (!immunity.matches("prism") && (attackerImmunityColor==null || !attackerImmunityColor.sameColorAs(itemMagicColorBonus)))) {
										armorVulnerability = new Strength(test.getGameObject().getThisAttribute(Constants.MAGIC_COLOR_BONUS_ARMOR));
									}
									else {
										RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Ignores magic vulnerability of armor.");
									}
								}
								if (!test.isArmor()) {
									if (test.isWeapon()) armorVulnerability = new Strength(test.getGameObject().getThisAttribute("weight")); // parrying with weapon
									if (test instanceof CharacterActionChitComponent) armorVulnerability = new Strength(test.getGameObject().getThisAttribute("strength")); // parrying with FIGHT chit
								}
								if (armorVulnerability.strongerThan(harm.getAppliedStrength())) {
									harm = new Harm(new Strength(),0); // negate harm!
									RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Missile attack is stopped by "+test.getGameObject().getNameWithNumber());
									break;
								}
								else if (armorVulnerability.equals(harm.getAppliedStrength())) {
									harm.setWound(true);
									RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Missile attack is stopped by "+test.getGameObject().getNameWithNumber()+", but causes 1 wound.");
									break;
								}
								else { // can assume harm is greater than armor now
									harm.dropOneLevel();
									RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Missile attack penetrates "+test.getGameObject().getNameWithNumber()+"!  Drops one level: "+harm);
								}
							}
						}
					}
				}
			}
			else {
				armor = getArmor(attacker.getAttackSpeed(),box,attackOrderPos);
				if (armor!=null && isDragonBreath && armor.getGameObject().hasThisAttribute(Constants.IMMUNE_BREATH)) {
					harm = new Harm(new Strength(),0); // negate harm!
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Dragon breath attack is stopped by "+armor.getGameObject().getNameWithNumber());
				}
			}

			boolean tookSeriousWounds = false;
			Strength minForWound = new Strength("L"); // Without armor, L is all that is required to wound
			
			if (armor==null && hasNaturalArmor()) { // custom character possibility
				harm.dampenSharpness();
				RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),getGameObject().getNameWithNumber() + " has natural armor, which reduces sharpness: "+harm.toString());
			}
						
			if (!harm.getIgnoresArmor() && armor != null) {
				// Wound minimum is increased to M if there is armor involved
				minForWound = new Strength("M");
				
				// If armor, reduce harm by one star, determine if armor is damaged/destroyed, apply wounds
				harm.dampenSharpness();
				if (armor.isCharacter()) {
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits characater fortification, and reduces sharpness: "+harm.toString());
				}
				else if (armor.isWeapon() || armor instanceof CharacterActionChitComponent) {
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hit is parried ("+armor.getGameObject().getNameWithNumber()+"), and reduces sharpness: "+harm.toString());
				}
				else {
					RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Hits armor ("+armor.getGameObject().getNameWithNumber()+"), and reduces sharpness: "+harm.toString());
				}
				Strength armorVulnerability = new Strength(armor.getGameObject().getThisAttribute("vulnerability"));
				if (armor.getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE) && armor.getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ARMOR)) {
					String immunity = attacker.getGameObject().getThisAttribute(Constants.MAGIC_IMMUNITY);
					ColorMagic attackerImmunityColor = ColorMagic.makeColorMagic(immunity,true);
					ColorMagic itemMagicColorBonus = ColorMagic.makeColorMagic(armor.getGameObject().getThisAttribute(Constants.MAGIC_COLOR_BONUS),true);
					if (immunity==null || (!immunity.matches("prism") && (attackerImmunityColor==null || !attackerImmunityColor.sameColorAs(itemMagicColorBonus)))) {
						armorVulnerability = new Strength(armor.getGameObject().getThisAttribute(Constants.MAGIC_COLOR_BONUS_ARMOR));
					}
					else {
						RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Ignores magic vulnerability of armor.");
					}
				}
				if (!armor.isArmor()) {
					if (armor.isWeapon()) armorVulnerability = new Strength(armor.getGameObject().getThisAttribute("weight")); // parrying with weapon
					if (armor instanceof CharacterActionChitComponent) {
						armorVulnerability = new Strength(armor.getGameObject().getThisAttribute("strength")); // parrying with FIGHT chit
						minForWound = new Strength(armorVulnerability);
					}
				}
				if (harm.getAppliedStrength().strongerOrEqualTo(armorVulnerability)) {
					boolean destroyed = true;
					damageTaken = true;
					if (armor.isArmor() && !harm.getAppliedStrength().strongerThan(armorVulnerability)) {
						// damaged
						if (armor.isCharacter()) {
							if (!character.isFortDamaged()) {
								RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Damages the character fortification.");
								character.setFortDamaged(true);
							}
						}
						else {
							ArmorChitComponent armorChit = (ArmorChitComponent) armor;
							if (!armorChit.isDamaged()) {
								RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Damages the "
										+getGameObject().getName()+"'s "
										+armor.getGameObject().getName());
								destroyed = false;
								armorChit.setIntact(false); // NOW its damaged
							}
						}
					} else if ((armor.isWeapon() && !harm.getAppliedStrength().strongerThan(armorVulnerability)) || armor instanceof CharacterActionChitComponent) {
						destroyed = false;
					}
					if (destroyed) {
						if (armor.isCharacter()) {
							character.setFortified(false);
							character.setFortDamaged(true);
						}
						else {
							CombatWrapper combatArmor = new CombatWrapper(armor.getGameObject());
	
							// Instead of removing right now, how about "killing" it, so it shows up with a red X
							if (!combatArmor.isDead()) {
								combatArmor.setKilledBy(attacker.getGameObject());
								combatArmor.setHitByOrderNumber(attackOrderPos);
								RealmLogging.logMessage(attacker.getGameObject().getNameWithNumber(),"Destroys the "
										+getGameObject().getName()+"'s "
										+armor.getGameObject().getName());
								
								// Treasure armor, should convert to its gold value.
								if (armor.getGameObject().hasAttribute("destroyed", "base_price")) {
									int basePrice = armor.getGameObject().getAttributeInt("destroyed", "base_price");
									if (basePrice > 0) {
										character.addGold(basePrice);
										RealmLogging.logMessage(getGameObject().getName(),"Gains "
												+basePrice+" gold for the loss of the "
												+armor.getGameObject().getName());
									}
								}
							}
						}
						// Armor cards are simply removed (not relocated anywhere)
					}
				}
				// else armor is unharmed
			}
			else if (harm.getAppliedStrength().strongerOrEqualTo(vulnerability)) {
				// Direct hit (no armor)
				if ((hostPrefs.hasPref(Constants.ADV_SERIOUS_WOUNDS) || character.affectedByKey(Constants.TOUGHNESS)) && harm.getAppliedStrength().equalTo(vulnerability)) {
					// Serious wounds
					Collection<CharacterActionChitComponent> c = character.getNonWoundedChits();
					DieRoller roller = DieRollBuilder.getDieRollBuilder(null,character).createRoller("wounds");
					int seriousWounds = roller.getHighDieResult();
					int currentWounds = combat.getNewWounds();
					
					RealmLogging.logMessage(getGameObject().getName(),"Takes a serious wound!");
					RealmLogging.logMessage(getGameObject().getName(),roller.getDescription());
					RealmLogging.logMessage(getGameObject().getName(),"Serious wound = "+seriousWounds+" wound"+(seriousWounds==1?"":"s")+")");
					if (c != null && c.size() > (currentWounds + seriousWounds)) {
						combat.addNewWounds(seriousWounds);
						combat.addSeriousWoundRoll(roller.getStringResult());
						tookSeriousWounds = true;
						damageTaken = true;
					}
					else {
						// Dead character!
						characterWasKilled = true;
					}
				}
				else {
					// Dead character!
					characterWasKilled = true;
				}
			}
			else if (harm.isWound()) {
				Collection<CharacterActionChitComponent> c = character.getNonWoundedChits();
				if (c.size() > 1) {
					combat.addNewWounds(1);
				}
				else {
					// Dead character!
					characterWasKilled = true;
				}
			}

			// Check for wounds
			if (!characterWasKilled && !tookSeriousWounds && harm.getAppliedStrength().strongerOrEqualTo(minForWound)) {
				// Wound character here, unless character is immune...
				if (armor==null || !character.hasActiveInventoryThisKey(Constants.STOP_WOUNDS)) {
					Collection<CharacterActionChitComponent> c = character.getActiveChits();
					int currentWounds = combat.getNewWounds();
					if (c != null && c.size() > currentWounds) {
						// Can't do the selection here! (this is called from the host, not the client)
						combat.addNewWounds(1);
						damageTaken = true;
					}
					else {
						// Dead character!
						characterWasKilled = true;
					}
				}
				else {
					RealmLogging.logMessage(getGameObject().getName(),"Avoided taking a wound!");
				}
			}
		}
		if (characterWasKilled) {
			combat.setKilledBy(attacker.getGameObject());
			damageTaken = true;
		}
		return damageTaken;
	}

	public String getMagicType() {
		RealmComponent rc = getAttackChit();
		if (rc.isMonster()) {
			return ((MonsterChitComponent)rc).getMagicType();
		}
		return null;
	}
	
	public String getAttackSpell() {
		RealmComponent rc = getAttackChit();
		if (rc.isMonster()) {
			return ((MonsterChitComponent)rc).getAttackSpell();
		}
		return null;
	}

	public int getManeuverCombatBox() {
		return getManeuverCombatBox(true);
	}
	public int getManeuverCombatBox(boolean includeHorse) {
		RealmComponent rc = getManeuverChit(includeHorse);
		if (rc != null) {
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			return combat.getCombatBox();
		}
		return 0;
	}

	public int getAttackCombatBox() {
		RealmComponent rc = getAttackChit();
		if (rc != null) {
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			return combat.getCombatBox();
		}
		return 0;
	}
	public boolean isMissile() {
		// This depends on the weapon
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject transmorph = character.getTransmorph();
		if (transmorph == null) { // Character must not be transmorphed!
			ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
			RealmComponent attackChit = getAttackChit();
			CombatWrapper combatChit = null;
			if (attackChit != null) {
				combatChit = new CombatWrapper(attackChit.getGameObject());
			}
			if (weapons != null) {
				for (WeaponChitComponent weapon : weapons) {
					if (weapon != null && (attackChit == null || combatChit.getWeaponId().equals(weapon.getGameObject().getStringId()))) {
						if (weapon.isMissile()) return true;
					}
				}
			}
			for (GameObject tw : getTreasureWeaponObjects()) {
				if (tw!=null && (attackChit == null || combatChit.getWeaponId().equals(tw.getStringId()))) {
					if (tw.hasThisAttribute("missile")) return true;
				}
			}
		}
		return false;
	}
	public String getMissileType() {
		// This depends on the weapon
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject transmorph = character.getTransmorph();
		if (transmorph == null) { // Character must not be transmorphed!
			ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
			RealmComponent rc = getAttackChit();
			CombatWrapper combatChit = new CombatWrapper(rc.getGameObject());
			for (WeaponChitComponent weapon : weapons) {
				if (weapon != null && combatChit.getWeaponId().equals(weapon.getGameObject().getStringId())) {
					return weapon.getGameObject().getThisAttribute("missile");
				}
			}
			for (GameObject tw : getTreasureWeaponObjects()) {
				if (tw!=null && combatChit.getWeaponId().equals(tw.getStringId())) {
					return tw.getThisAttribute("missile");
				}
			}
		}
		return "";
	}
	
	private static boolean testEffectIsOnActiveWeapon(GameObject effector, WeaponChitComponent weapon) {	
		if (weapon != null) {
			String affectedWeaponId = effector.getThisAttribute(Constants.AFFECTED_WEAPON_ID);
			if (affectedWeaponId != null && affectedWeaponId.equals(weapon.getGameObject().getStringId())) {
				return true;
			}
		}
		return false;
	}
	public boolean activeWeaponStaysAlerted(WeaponChitComponent weapon) {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		GameObject dust = character.getActiveInventoryThisKey(Constants.ALERTED_WEAPON);
		return dust!=null && testEffectIsOnActiveWeapon(dust, weapon);
	}
	public boolean hitsOnTie() {
		boolean hitsOnTie = getGameObject().hasThisAttribute(Constants.HIT_TIE); // In case Ointment of Bite was applied to dagger
		boolean weaponHitsOnTie = false;
		ArrayList<GameObject> weapons = getActiveWeaponsObjects();
		RealmComponent rc = getAttackChit();
		CombatWrapper combatChit = new CombatWrapper(rc.getGameObject());
		if (weapons != null) {
			for (GameObject weapon : weapons) {
				if (combatChit.getWeaponId().equals(weapon.getStringId())) {
					CombatWrapper wCombat = new CombatWrapper(weapon);
					if (wCombat.getCombatBox()>0) {
						if (weapon.hasThisAttribute(Constants.HIT_TIE)) {
							weaponHitsOnTie = true;
						}
					}
				}
			}
		}
		return hitsOnTie || weaponHitsOnTie;
	}
	public void changeWeaponState() {
		CharacterWrapper character = new CharacterWrapper(getGameObject());
		ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
		if (weapons != null && !weapons.isEmpty()) {
			CombatWrapper charCombat = new CombatWrapper(getGameObject());
			boolean hit = false;
			for (WeaponChitComponent weapon : weapons) {
				if (charCombat.weaponHasHit(weapon.getGameObject().getStringId())) {
					hit = true;
				}
				alertWeapon(weapon, hit);
			}
		}
	}
	private void alertWeapon(WeaponChitComponent weapon, boolean hit) {
		// make sure weapon was played in combat this round (otherwise it doesn't change)
		int box = (new CombatWrapper(weapon.getGameObject())).getCombatBox();
		if (box > 0) {
			if (activeWeaponStaysAlerted(weapon) || weapon.getGameObject().hasThisAttribute(Constants.ALERTED_WEAPON)) {
				// Treat like the character just missed - keeps weapon alerted
				hit = false;
			}
			// hits should unalert weapons, misses should alert them
			weapon.setAlerted(!hit);
		}
	}
	public void setTarget(RealmComponent comp) {
		super.setTarget(comp);
		MonsterChitComponent monster = getTransmorphedComponent();
		if (monster!=null) {
			monster.setTarget(comp);
		}
	}
	public void set2ndTarget(RealmComponent comp) {
		super.set2ndTarget(comp);
		MonsterChitComponent monster = getTransmorphedComponent();
		if (monster!=null) {
			monster.set2ndTarget(comp);
		}
	}
	public void clearTarget() {
		super.clearTarget();
		MonsterChitComponent monster = getTransmorphedComponent();
		if (monster!=null) {
			monster.clearTarget();
		}
	}
	public void clear2ndTarget() {
		super.clear2ndTarget();
		MonsterChitComponent monster = getTransmorphedComponent();
		if (monster!=null) {
			monster.clear2ndTarget();
		}
	}
	public void clearTargets() {
		super.clearTargets();
		MonsterChitComponent monster = getTransmorphedComponent();
		if (monster!=null) {
			monster.clearTargets();
		}
	}
	public void setTargetAttacked() {
		super.setTargetAttacked();
	}
	public boolean getTargetAttacked() {
		return super.getTargetAttacked();
	}
	public boolean isMistLike() {
		RealmComponent rc = getTransmorphedComponent();
		return rc!=null && rc.isMistLike();
	}
}