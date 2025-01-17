package com.robin.magic_realm.components.table;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.game.server.GameClient;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.ArmorChitComponent;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.swing.RealmPaymentDialog;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public abstract class Meeting extends Trade {
	
	public static final String BLOCK_BATTLE = "Block/Battle";
	
	protected GameObject merchandise; // might be null if hiring or rolling for meeting
	protected Collection<RealmComponent> hireGroup; // might be null if trading or rolling for meeting
	
	protected boolean blockBattle;
	
	public Meeting(JFrame frame,TradeInfo trader,GameObject merchandise,Collection<RealmComponent> hireGroup) {
		super(frame,trader);
		this.merchandise = merchandise;
		this.hireGroup = hireGroup;
		this.blockBattle = false;
	}
	/**
	 * Override method here to guarantee specificAction is applied
	 */
	public void setNewTable(RealmTable newTable) {
		if (specificAction.length()>0) {
			if (newTable instanceof Meeting) { // guarantees specific action is translated across
				((Meeting)newTable).setSpecificAction(specificAction.substring(1)); // trim comma
			}
		}
		super.setNewTable(newTable);
	}
	public boolean isBlockBattle() {
		return blockBattle;
	}
	public String getTableName(boolean longDescription) {
		return "Buy from "+tradeInfo.getName()+" (as "+getMeetingTableName()+")";
	}
	public String getTableKey() {
		return "Meeting"+specificAction;
	}
	public abstract String getMeetingTableName();
	protected void doBlockBattle(CharacterWrapper character) {
		character.setBlocked(true);
		if ((merchandise!=null || hireGroup!=null) && character.isHidden()) { // only cause character to become unhidden if doing a TRADE
			character.setHidden(false);
		}
		blockBattle = true;
	}
	protected boolean useNoNegativeRule() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(tradeInfo.getGameData());
		return hostPrefs.hasPref(Constants.HOUSE1_NO_NEGATIVE_POINTS);
	}
	protected boolean useDeclineOpportunityRule() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(tradeInfo.getGameData());
		return hostPrefs.hasPref(Constants.HOUSE2_DECLINE_OPPORTUNITY);
	}
	protected String doOpportunity(CharacterWrapper character,Meeting newTable) {
		if (useDeclineOpportunityRule() && (merchandise!=null || hireGroup!=null)) { // only ask the question, if this is a TRADE or HIRE (not for battling results!!)
			int ret = JOptionPane.showConfirmDialog(
					getParentFrame(),
					"You have rolled OPPORTUNITY.  Do wish to keep this result, or decline it and\n"
					+"take the result from next result down on the "+getMeetingTableName()+" table?\n\n"
					+"   Answer YES to take the opportunity, and get a new roll on the "+newTable.getMeetingTableName()+" table"
					+"   Answer NO to decline the opportunity, take the result from rolling a 2 instead",
					"OPPORTUNITY",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret==JOptionPane.NO_OPTION) {
				return "Opportunity(declined) - "+applyTwo(character);
			}
		}
		setNewTable(newTable);
		return "Opportunity";
	}
	protected void doInsult(CharacterWrapper character) {
		String pronoun = character.getGameObject().getThisAttribute("pronoun");
		/*
		 * Character can choose to lose 5 notoriety points and get a No Deal result - otherwise block/battle
		 */
		if (character.getNotoriety()>=5 || !useNoNegativeRule()) {
			int ret = JOptionPane.showConfirmDialog(
					getParentFrame(),
					"The "+character.getGameObject().getName()
					+" must lose 5 notoriety points (current notoriety="
					+character.getRoundedNotoriety()
					+"),\nor be blocked by the "
					+getDenizenName()
					+".\n\nDo you want to lose the points?","INSULT",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,getRollerImage());
			if (ret==JOptionPane.YES_OPTION) {
				character.addNotoriety(-5);
				return;
			}
		}
		else {
			StringBuffer message = new StringBuffer();
			message.append("The "+character.getGameObject().getName()+" has insulted the "+tradeInfo.getThisAttribute("native"));
			message.append(" but does not have the option to subtract\nfrom notoriety because "+pronoun);
			message.append(" does not have enough points (5 needed).");
			JOptionPane.showMessageDialog(getParentFrame(),message,"Insult - Block/Battle",JOptionPane.WARNING_MESSAGE);
		}
		
		doBlockBattle(character);
	}
	protected void doChallenge(CharacterWrapper character) {
		String pronoun = character.getGameObject().getThisAttribute("pronoun");
		/*
		 * Character can choose to lose 5 fame points and get a No Deal result - otherwise block/battle
		 * DISGUST curse makes it impossible to pay fame points
		 */
		if ((character.getFame()>=5 || !useNoNegativeRule()) && !character.hasCurse(Constants.DISGUST)) {
			int ret = JOptionPane.showConfirmDialog(
					getParentFrame(),
					"The "+character.getGameObject().getName()
					+" must lose 5 fame points (current fame="
					+character.getRoundedFame()
					+"),\nor be blocked by the "
					+getDenizenName()
					+".\n\nDo you want to lose the points?","CHALLENGE",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,getRollerImage());
			if (ret==JOptionPane.YES_OPTION) {
				character.addFame(-5);
				return;
			}
		}
		else {
			StringBuffer message = new StringBuffer();
			message.append("The "+character.getGameObject().getName()+" has been challenged by the "+tradeInfo.getThisAttribute("native"));
			message.append(" but does not have the option to subtract\nfrom fame because "+pronoun);
			if (character.hasCurse(Constants.DISGUST)) {
				message.append(" is currently cursed with DISGUST.");
			}
			else {
				message.append(" does not have enough points (5 needed).");
			}
			JOptionPane.showMessageDialog(getParentFrame(),message,"Challenge - Block/Battle",JOptionPane.WARNING_MESSAGE,getRollerImage());
		}
		
		doBlockBattle(character);
	}
	protected String applyInsult(CharacterWrapper character) {
		String text = "Insult";
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		doInsult(character);
		return text;
	}
	protected String applyChallenge(CharacterWrapper character) {
		String text = "Challenge";
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		doChallenge(character);
		return text;
	}
	protected String applyNoDeal(CharacterWrapper character) {
		String text = "No Deal";
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		return text;
	}
	protected String applyBlockBattle(CharacterWrapper character) {
		String text = BLOCK_BATTLE;
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		doBlockBattle(character);
		return text;
	}
	protected String applyPrice(CharacterWrapper character, int mult) {
		String text = "Price x "+mult;
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		processPrice(character,mult);
		return text;
	}
	protected void processPrice(CharacterWrapper character,int mult) {
		if (merchandise!=null) {
			RealmComponent rc = RealmComponent.getRealmComponent(merchandise);
			if (rc.isArmor() && ((ArmorChitComponent)rc).isDamaged()) {
				repairingMerchandise(character,mult);
			}
			else {
				buyingMerchandise(character,mult);
			}
		}
		else if (hireGroup!=null) {
			hiringNatives(character,mult);
		}
	}
	protected void repairingMerchandise(CharacterWrapper character,int mult) {
		// Everything is handled by the RealmPaymentDialog now.
		RealmPaymentDialog dialog = new RealmPaymentDialog(getParentFrame(),"REPAIR",character,tradeInfo,merchandise,mult,getListener());
		dialog.setVisible(true);
	}
	protected void buyingMerchandise(CharacterWrapper character,int mult) {
		// Everything is handled by the RealmPaymentDialog now.
		RealmPaymentDialog dialog = new RealmPaymentDialog(getParentFrame(),"TRADE",character,tradeInfo,merchandise,mult,getListener());
		dialog.setVisible(true);
	}
	public void hiringNatives(CharacterWrapper character,int mult) {
		int basePrice = 0;
		RealmComponent last = null;
		boolean hireWithChit = false;		
		for (RealmComponent hire : hireGroup) {
			basePrice += hire.getGameObject().getThisInt("base_price");
			if (hire.getGameObject().hasThisAttribute(Constants.HIRE_WITH_CHIT)) {
				hireWithChit = true;
			}
			last = hire;
		}
		if (last == null) return;
		
		if (hireWithChit && hireGroup.size()>1) {
			JOptionPane.showMessageDialog(getParentFrame(),"Cannot hire multiple natives, if costs include a character chit.","Cannot hire",JOptionPane.INFORMATION_MESSAGE,last.getIcon());
			return;
		}
		if (hireWithChit) {
			if (last.getOwner() != null) {
				JOptionPane.showMessageDialog(getParentFrame(),"You cannot rehire this hireling.","Cannot rehire "+last.getName(),JOptionPane.INFORMATION_MESSAGE,last.getIcon());
				return;
			}
			String amountString = last.getGameObject().getThisAttribute(Constants.HIRE_WITH_CHIT);
			if (amountString.isEmpty()) amountString = "1";
			int amount = Integer.valueOf(amountString);
			ArrayList<CharacterActionChitComponent> chits = character.getActiveChits();
			if (chits == null || chits.size() == 0 || chits.size()<amount) {
				JOptionPane.showMessageDialog(getParentFrame(),"You have not enough active character chits for hiring available.","Cannot hire "+last.getName(),JOptionPane.INFORMATION_MESSAGE,last.getIcon());
				return;
			}
			int amountLeft = amount;
			int amountSelected = 0;
			ArrayList<RealmComponent> allSelectedChits = new ArrayList<RealmComponent>();
			
			while (amountSelected < amountLeft) {
				RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(getParentFrame(),"You want to hire the "+last.getName()+" for "+amount+" character chit(s)?",true);
				for (CharacterActionChitComponent chit : chits) {
					chooser.addRealmComponent(chit);
				}
				chooser.setVisible(true);
							
				Collection<RealmComponent> selectedChits = chooser.getSelectedComponents();
				if (selectedChits == null) return;
				amountSelected = amountSelected+1;
				chits.removeAll(selectedChits);
				allSelectedChits.addAll(selectedChits);
			}
			
			for (RealmComponent chit : allSelectedChits) {
				last.getGameObject().addThisAttributeListItem(Constants.ABSORBED_CHITS, chit.getGameObject().getStringId());
				last.getGameObject().add(chit.getGameObject());
			}
			character.updateChitEffects();
			character.addHireling(last.getGameObject());
			return;
		}
		
		int askingPrice = basePrice * mult;
		
		String groupName = tradeInfo.getThisAttribute("native");
		if (hireGroup.size()==1 || groupName==null) {
			groupName = tradeInfo.getName();
			if (hireGroup.size()>1) {
				groupName = groupName+"s";
			}
		}
		groupName = StringUtilities.capitalize(groupName);
		
		StringBuffer sb = new StringBuffer();
		if (getTradeInfo().getNoDrinksReason()!=null) {
			sb.append("(");
			sb.append(getTradeInfo().getNoDrinksReason());
			sb.append(")\n");
		}
		sb.append("You can hire the ");
		sb.append(groupName);
		boolean isBoon = askingPrice==0 && !last.isTraveler();
		if (isBoon) {
			if (last.isMonster()) {
				if (hireGroup.size()>1) {
					sb.append("s");
				}
				askingPrice = basePrice;
			}
			else {
				sb.append(" as a boon, or ");
			}
		}
		sb.append(" for ");
		sb.append(askingPrice==0?basePrice:askingPrice);
		sb.append(" gold.");
		
		String offerTitle = tradeInfo.getName()+" Offer - Price x "+mult;
		
		int charGold = character.getRoundedGold();
		if (character.hasCurse(Constants.ASHES)) {
			sb.append("  Unfortunately, your gold is worthless as long as you have the ASHES curse.");
			JOptionPane.showMessageDialog(getParentFrame(),sb.toString(),offerTitle,JOptionPane.INFORMATION_MESSAGE,last.getIcon());
		}
		else if (charGold>=askingPrice) {
			sb.append("  Will you hire?");
			int ret = JOptionPane.showConfirmDialog(getParentFrame(),sb.toString(),offerTitle,
						JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,last.getIcon());
			
			if (ret==JOptionPane.YES_OPTION) {
				if (isBoon) {
					// special case for BOON
					ret = JOptionPane.showConfirmDialog(getParentFrame(),"Take the boon?",tradeInfo.getName()+" Offers Boon",
								JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,last.getIcon());
					if (ret==JOptionPane.YES_OPTION) {
						character.changeRelationship(tradeInfo.getGameObject(),-1);
						character.getGameObject().add(RealmPaymentDialog.createBoon(tradeInfo.getGameObject(),basePrice));
					}
					else {
						askingPrice = basePrice;
					}
				}
				
				if (charGold>=askingPrice) {
					// Subtract price
					character.addGold(-askingPrice);
					
					// Hire the group!
					for (RealmComponent rc : hireGroup) {
						character.addHireling(rc.getGameObject());
					}
				}
				else {
					JOptionPane.showMessageDialog(
							getParentFrame(),
							"You only have "+charGold+" gold, so without the BOON, you cannot hire.",
							offerTitle,
							JOptionPane.INFORMATION_MESSAGE,
							last.getIcon());
				}
			}
		}
		else {
			sb.append("  You only have "+charGold+" gold.");
			JOptionPane.showMessageDialog(getParentFrame(),sb.toString(),offerTitle,JOptionPane.INFORMATION_MESSAGE,last.getIcon());
		}
	}
	protected String useCompletedActiveTask(CharacterWrapper character, String text) {
		GameObject task = character.getCompletedActiveTask();
		if (task!=null) {
			int ret = JOptionPane.showConfirmDialog(
					getParentFrame(),
					"Do you want to use your completed Task chit instead of the current result ('"+text+"')?",
					"Use reward of completed Task, instead of '"+text+"'?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
			if (ret==JOptionPane.YES_OPTION) {
				TileLocation loc = character.getCurrentLocation();
				if (loc!=null && loc.isInClearing()) {
					task.removeThisAttribute(Constants.TASK_COMPLETED);
					GameClient.broadcastClient("host",task.getName()+" is dropped in "+loc);
					loc.clearing.add(task,null);
					Object[] choices = {"Price x 1", "Boon"};
					Object defaultChoice = choices[0];
					ret = JOptionPane.showOptionDialog(
							getParentFrame(),
							"Treat as 'Price x 1' or 'Boon' result?",
							"Price x 1 or Boon?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,choices,defaultChoice);
					if (ret==JOptionPane.YES_OPTION || ret==JOptionPane.CLOSED_OPTION) {
						processPrice(character,0);
						return "Price x 1";
					}
					processPrice(character,1);
					return "Boon (x 1)";
				}
			}
		}
		return null;
	}
	public static Meeting createMeetingTable(JFrame frame,CharacterWrapper character,TileLocation currentLocation,RealmComponent trader,RealmComponent merchandise,Collection<RealmComponent> hireGroup,int ignoreBuyDrinksLimit) {
		TradeInfo tradeInfo = getTradeInfo(frame,character,trader,currentLocation,ignoreBuyDrinksLimit,hireGroup==null?0:hireGroup.size());
		
		GameObject merchObj = merchandise==null?null:merchandise.getGameObject();
		
		Meeting meeting = null;
		switch(tradeInfo.getRelationshipType()) {
			case RelationshipType.ENEMY:
				meeting = new MeetingEnemy(frame,tradeInfo,merchObj,hireGroup);
				break;
			case RelationshipType.UNFRIENDLY:
				meeting = new MeetingUnfriendly(frame,tradeInfo,merchObj,hireGroup);
				break;
			case RelationshipType.NEUTRAL:
				meeting = new MeetingNeutral(frame,tradeInfo,merchObj,hireGroup);
				break;
			case RelationshipType.FRIENDLY:
				meeting = new MeetingFriendly(frame,tradeInfo,merchObj,hireGroup);
				break;
			case RelationshipType.ALLY:
				meeting = new MeetingAlly(frame,tradeInfo,merchObj,hireGroup);
				break;
		}
		return meeting;
	}
}