package com.robin.magic_realm.components.attribute;

import java.util.StringTokenizer;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Spoils {
	private boolean useMultiplier;
	private double fame = 0.0;
	private double notoriety = 0.0;
	private double goldBounty = 0.0;
	private double goldRecord = 0.0;
	
	private int divisor = 1;
	private int multiplier = 1;
	
	public Spoils() {
	}
	public Spoils(String key) {
		StringTokenizer tokens = new StringTokenizer(key,";");
		if (tokens.countTokens()==7) {
			useMultiplier = "T".equals(tokens.nextToken());
			fame = Double.valueOf(tokens.nextToken());
			notoriety = Double.valueOf(tokens.nextToken());
			goldBounty = Double.valueOf(tokens.nextToken());
			goldRecord = Double.valueOf(tokens.nextToken());
			divisor = Integer.parseInt(tokens.nextToken());
			multiplier = Integer.parseInt(tokens.nextToken());
		}
		else throw new IllegalArgumentException("Invalid argument: "+key);
	}
	public String asKey() {
		StringBuffer sb = new StringBuffer();
		sb.append(useMultiplier?"T":"F");
		sb.append(";");
		sb.append(fame);
		sb.append(";");
		sb.append(notoriety);
		sb.append(";");
		sb.append(goldBounty);
		sb.append(";");
		sb.append(goldRecord);
		sb.append(";");
		sb.append(divisor);
		sb.append(";");
		sb.append(multiplier);
		return sb.toString();
	}
	public String getFameNotorietyString() {
		int rfame = (int)Math.floor(fame);
		int rnot = (int)Math.floor(notoriety);
		String ret = (rfame>0?(rfame+" fame (x "+multiplier+") and "):"")+rnot+" notoriety (x "+multiplier+")";
		if (divisor>1) {
			return ret+" [split "+divisor+" ways]";
		}
		return ret;
	}
	public String getGoldString() {
		int rgoldb = (int)Math.floor(goldBounty);
		int rgoldr = (int)Math.floor(goldRecord);
		StringBuffer sb = new StringBuffer();
		if (rgoldb>0) {
			sb.append(rgoldb+" gold bounty");
			if (divisor>1) {
				sb.append(" [split "+divisor+" ways]"); 
			}
		}
		if (rgoldr>0) {
			if (sb.length()>0) {
				sb.append(" and ");
			}
			sb.append(rgoldr+" recorded gold");
			if (divisor>1) {
				sb.append(" [split "+divisor+" ways]"); 
			}
		}
		return sb.toString();
	}
	public boolean hasGold() {
		return goldBounty>0 || goldRecord>0;
	}
	public boolean hasFameOrNotoriety() {
		return (fame+notoriety)>0.0;
	}
	public void setDivisor(int val) {
		divisor = val;
	}
	public void setMultiplier(int val) {
		multiplier = useMultiplier?val:1;
	}
	public int getMultiplier() {
		return multiplier;
	}
	/**
	 * @return Returns the fame.
	 */
	public double getFame() {
		return (fame*multiplier)/divisor;
	}
	/**
	 * @param fame The fame to set.
	 */
	public void addFame(int inFame) {
		this.fame += inFame;
	}
	/**
	 * @return Returns the goldBounty.
	 */
	public double getGoldBounty() {
		return goldBounty/divisor;
	}
	/**
	 * @param goldBounty The goldBounty to set.
	 */
	public void setGoldBounty(int goldBounty) {
		this.goldBounty = goldBounty;
	}
	/**
	 * @return Returns the goldRecord.
	 */
	public double getGoldRecord() {
		return goldRecord/divisor;
	}
	/**
	 * @param goldRecord The goldRecord to set.
	 */
	public void setGoldRecord(int goldRecord) {
		this.goldRecord = goldRecord;
	}
	/**
	 * @return Returns the multiplier.
	 */
	public boolean getUseMultiplier() {
		return useMultiplier;
	}
	/**
	 * @param multiplier The multiplier to set.
	 */
	public void setUseMultiplier(boolean useMultiplier) {
		this.useMultiplier = useMultiplier;
	}
	/**
	 * @return Returns the notoriety.
	 */
	public double getNotoriety() {
		return (notoriety*multiplier)/divisor;
	}
	/**
	 * @param notoriety The notoriety to set.
	 */
	public void addNotoriety(int inNotoriety) {
		this.notoriety += inNotoriety;
	}
	
	public static Spoils getSpoils(GameObject attackerGo,GameObject victimGo) {
		Spoils spoils = new Spoils();
		RealmComponent attacker = RealmComponent.getRealmComponent(attackerGo);
		RealmComponent victim = RealmComponent.getRealmComponent(victimGo);
		RealmComponent attackerOwner = attacker.getOwner();
		
		if (attacker.isCharacter() || attackerOwner!=null) { // Attacker is Character or Hireling
			// Use multiplier only if attacker is a character, and the victim is not
			spoils.setUseMultiplier(attacker.isCharacter() && !victim.isCharacter());
			
			// Fame and Notoriety Bounty
			boolean seriousWound = victimGo.hasThisAttribute(Constants.SERIOUS_WOUND);
			int fame = victim.getGameObject().getThisInt("fame");
			int notoriety = victim.getGameObject().getThisInt("notoriety");
			if (seriousWound) {
				// divide by 2
				fame = (fame+2-1)/2;
				notoriety = (notoriety+2-1)/2;
			}
			spoils.addFame(fame);
			spoils.addNotoriety(notoriety);
			if (victim.isCharacter()) {
				CharacterWrapper victimRecord = new CharacterWrapper(victim.getGameObject());
				spoils.addNotoriety(victimRecord.getRoundedNotoriety()); // stored a bit differently
				if (victimRecord.isTransmorphed()) {
					GameObject go = victimRecord.getTransmorph();
					spoils.addFame(go.getThisInt("fame"));
					spoils.addNotoriety(go.getThisInt("notoriety"));
				}
			}
			
			if (attacker.isPlayerControlledLeader()) { // Attacker is a Character or hired leader or controlled monster
				// Gold Bounty
				if (!victim.isMonster()) {
					spoils.setGoldBounty(victim.getGameObject().getThisInt("base_price"));
				}
				
				// Recorded Gold
				CharacterWrapper victimRecord = new CharacterWrapper(victim.getGameObject());
				spoils.setGoldRecord(victimRecord.getRoundedGold());
			}
		}
		return spoils;
	}
}