package com.robin.magic_realm.components.store;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.GuildChitComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.swing.RealmObjectChooser;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class MagicGuild extends GuildStore {
	
	private static int GT_PRICE = 2;
	
	private static String CURE_SERVICE = "Cancel curse or spell for 5 gold.";
	private static String CURE_FREE_SERVICE = "Cancel curse or spell for free.";
	private static String GENERATE_COLOR_SERVICE = "Generate any color for a day for 5 gold.";
	private static String ADVANCEMENT_SERVICE = "Pay "+GT_PRICE+" Great Treasures to advance to next level.";
	
	private ArrayList<GameObject> greatTreasures;
	private ArrayList<SpellWrapper> bewitchingSpells;
	private ArrayList<String> activeCurses;
	
	public MagicGuild(GuildChitComponent guild, CharacterWrapper character) {
		super(guild, character);
	}
	protected void setupGuildSpecific() {
		boolean journeymanMage = character.isGuildMember(trader) && character.getCurrentGuildLevel()==2; 
		if (!journeymanMage && character.hasCurse(Constants.ASHES)) {
			reasonStoreNotAvailable = "The "+getTraderName()+" does not like your ASHES curse!";
			return;
		}
		
		greatTreasures = new ArrayList<GameObject>();
		for (GameObject go:character.getInventory()) {
			if (go.hasThisAttribute("treasure") && go.hasThisAttribute("great")) {
				greatTreasures.add(go);
			}
		}
		
		activeCurses = character.getAllCurses();
		SpellMasterWrapper smw = SpellMasterWrapper.getSpellMaster(character.getGameData());
		bewitchingSpells = smw.getAffectingSpells(character.getGameObject());
	}
	private boolean madePayment() {
		RealmObjectChooser chooser = new RealmObjectChooser("Choose two Great Treasures to give to the Magic Guild.",character.getGameData(),false);
		chooser.setValidCount(2);
		chooser.addObjectsToChoose(greatTreasures);
		chooser.setVisible(true);
		if (chooser.pressedOkay()) {
			ArrayList<GameObject> toGive = chooser.getChosenObjects();
			for(GameObject go:toGive) {
				TradeUtility.loseItem(character,go,trader.getGameObject(),false);
				go.removeThisAttribute("great");
			}
			trader.getGameObject().addAll(toGive);
			return true;
		}
		return false;
	}
	private String chooseMagicType(JFrame frame) {
		ButtonOptionDialog chooser = new ButtonOptionDialog(frame,trader.getIcon(),"Which type of speed 2 MAGIC chit will you take?",getTraderName()+" Reward",false);
		chooser.addSelectionObject("I");
		chooser.addSelectionObject("II");
		chooser.addSelectionObject("III");
		chooser.addSelectionObject("IV");
		chooser.addSelectionObject("V");
		chooser.addSelectionObject("VI");
		chooser.addSelectionObject("VII");
		chooser.addSelectionObject("VIII");
		chooser.setVisible(true);
		return (String)chooser.getSelectedObject();
	}
	private String cureSpellOrCurse(JFrame frame) {
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Break which spell/curse?",true);
		for(SpellWrapper spell:bewitchingSpells) {
			String optionKey = chooser.generateOption();
			chooser.addGameObjectToOption(optionKey,spell.getGameObject());
			for (GameObject hgo : spell.getGameObject().getHold()) {
				chooser.addGameObjectToOption(optionKey,hgo);
			}
		}
		for (String curse:activeCurses) {
			chooser.generateOption(curse);
		}
		chooser.setVisible(true);
		
		String selText = chooser.getSelectedText();
		if (selText!=null) {
			if (character.hasCurse(selText)) {
				character.removeCurse(selText);
				return "Removed "+selText+" curse.";
			}
			GameObject go = chooser.getFirstSelectedComponent().getGameObject();
			SpellWrapper spell = new SpellWrapper(go);
			spell.expireSpell();
			return "Breaked "+go.getName()+" spell.";
		}
		return null;
	}
	protected String generateColor(JFrame frame) {
		String colorAlready = trader.getGameObject().getThisAttribute("color_source");
		ButtonOptionDialog chooser = new ButtonOptionDialog(frame,trader.getIcon(),"Generate which color?","Generate Color",true);
		for (int i=1;i<=5;i++) {
			ColorMagic cm = new ColorMagic(i,true);
			if (!cm.getColorName().toLowerCase().equals(colorAlready)) {
				chooser.addSelectionObject(cm.getColorName());
			}
		}
		chooser.setVisible(true);
		String chosenColor = (String)chooser.getSelectedObject();
		if (chosenColor!=null) {
			trader.getGameObject().setThisAttribute("color_source",chosenColor.toLowerCase());
			return "Generated "+chosenColor+" magic.";
		}
		return null;
	}
	protected String doGuildService(JFrame frame,int level) {
		int gold = (int)character.getGold();
		int gtCount = greatTreasures.size();
		int activeCursesOrSpells = activeCurses.size() + bewitchingSpells.size();
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
		
		ButtonOptionDialog chooser = new ButtonOptionDialog(frame,trader.getIcon(),"Which service?",getTraderName()+" Services",true);
		if (!hostPrefs.hasPref(Constants.GUILDS_NO_ADVANCEMENT_SERVICE)) {
			if (level<3) chooser.addSelectionObject(ADVANCEMENT_SERVICE,gtCount>=GT_PRICE);
			updateButtonChooser(chooser,level);
		}
		if (level==1) chooser.addSelectionObject(CURE_SERVICE,(gold>=5) && activeCursesOrSpells>0);
		if (level>=2) chooser.addSelectionObject(CURE_FREE_SERVICE,(gold>=10) && activeCursesOrSpells>0);
		if (level>=2) chooser.addSelectionObject(GENERATE_COLOR_SERVICE,gold>=5);
		chooser.setVisible(true);
		
		String selected = (String)chooser.getSelectedObject();
		if (selected!=null) {
			boolean freeAdvancement = isFreeAdvancement(selected);
			if (CURE_SERVICE.equals(selected)) {
				character.addGold(-5);
				return cureSpellOrCurse(frame);
			}
			else if (CURE_FREE_SERVICE.equals(selected)) {
				return cureSpellOrCurse(frame);
			}
			else if (GENERATE_COLOR_SERVICE.equals(selected)) {
				character.addGold(-5);
				return generateColor(frame);
			}
			else if (freeAdvancement || ADVANCEMENT_SERVICE.equals(selected)) {
				if (!freeAdvancement && !madePayment()) {
					return null;
				}
				
				int newLevel = character.getCurrentGuildLevel()+1;
				character.setCurrentGuildLevel(newLevel);
				chooseFriendlinessGain(frame);
				if (newLevel!=3 && hostPrefs.hasPref(Constants.GUILDS_BENEFITS)) {
					applyGuildBenefit(frame,character,newLevel);
				}
				if (newLevel==3) {
					applyGuildBenefit3(frame,character);
				}
				return "Advanced to "+character.getCurrentGuildLevelName()+"!";
			}
		}
		
		return null;
	}
	public void applyGuildBenefit1(JFrame frame, CharacterWrapper character) {
		if (!character.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT+"_1")) {
			character.getGameObject().addThisAttributeListItem(Constants.EXTRA_ACTIONS,"SP");
			character.getGameObject().setThisAttribute(Constants.GUILD_BENEFIT+"_1");
		}
	}
	public void unapplyGuildBenefit1(JFrame frame, CharacterWrapper character) {
		if (character.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT+"_1")) {
			character.getGameObject().removeThisAttributeListItem(Constants.EXTRA_ACTIONS,"SP");
			character.getGameObject().removeThisAttribute(Constants.GUILD_BENEFIT+"_1");
		}
	}
	public void applyGuildBenefit2(JFrame frame, CharacterWrapper character) {
		if (!character.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT+"_2")) {
			character.getGameObject().setThisAttribute(Constants.FREE_ENCHANT_CHIT);
			character.getGameObject().setThisAttribute(Constants.GUILD_BENEFIT+"_2");
		}
	}
	public void unapplyGuildBenefit2(JFrame frame, CharacterWrapper character) {
		if (character.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT+"_2")) {
			character.getGameObject().removeThisAttribute(Constants.FREE_ENCHANT_CHIT);
			character.getGameObject().removeThisAttribute(Constants.GUILD_BENEFIT+"_2");
		}
	}
	public void applyGuildBenefit3(JFrame frame, CharacterWrapper character) {
		if (!character.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT+"_3")) {
			character.getGameObject().setThisAttribute(Constants.GUILD_BENEFIT+"_3");
			String chosenMagicType = chooseMagicType(frame);
			GameObject go = getNewCharacterChit();
			Strength vul = new Strength(character.getGameObject().getThisAttribute("vulnerability"));
			if (!vul.isTremendous()) {
				vul = vul.addStrength(1);
			}
			go.setThisAttribute("action","magic");
			go.setThisAttribute("speed","2");
			go.setThisAttribute("magic",chosenMagicType);
			go.setThisAttribute("effort","2");
			go.setName(character.getCharacterLevelName(4)+" MAGIC "+chosenMagicType+"2**");
			go.setThisAttribute(Constants.GUILD_BENEFIT+"_3");
			RealmLogging.logMessage(character.getGameObject().getName(),"Gained a "+go.getName()+" chit.");
		}
	}
	public void unapplyGuildBenefit3(JFrame frame, CharacterWrapper character) {
		character.getGameObject().removeThisAttribute(Constants.GUILD_BENEFIT+"_3");
		for (CharacterActionChitComponent chit : character.getAllChits()) {
			if (chit.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT+"_3")) {
				character.getGameObject().remove(chit.getGameObject());
				chit.getGameObject().clearAllAttributes();
			}
		}
	}
}