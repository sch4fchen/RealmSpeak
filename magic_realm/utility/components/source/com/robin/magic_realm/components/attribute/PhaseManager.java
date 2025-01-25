package com.robin.magic_realm.components.attribute;

import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.swing.QuietOptionPane;
import com.robin.general.util.HashLists;
import com.robin.magic_realm.components.BattleHorse;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.SteedChitComponent;
import com.robin.magic_realm.components.attribute.DayAction.ActionId;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

/**
 * This class will replace the DayActions class.  It will manage the possible actions you can make during a day,
 * lock certain items in play (can't drop a treasure that you are using phases from), and even enable a nice display.
 * 
 * Some notes:
 * 	- Likely, the only items to be deactivated will be horses when entering caves.  In fact, I may "disallow" deactivation
 * 		of any object that is held by the PhaseManager during the turn (unless of course it is automatic!)
 * 	
 * 	- The only way "new" free actions will be added here, is if an inert permanent spell is activated with color magic
 */
public class PhaseManager {
	public static final String REGULAR_PHASE = "REGULAR_PHASE";
	public static final String EXTRA_CAVE_PHASE = "X";
	
	private int basic = 0;
	private int sunlight = 0;
	private int sheltered = 0;
	
	private boolean usedSheltered = false;
	private boolean usedSunlight = false;
	
	private boolean ponyLock = false;
	private GameObject ponyObject = null;
	private int ponyMoves = 0;
	private int extraCavePhase = 0; // These get added to basic when entering a cave
	private int extraDwellingPhase = 0; // These get added to basic when entering a dwelling
	private HashLists<String, GameObject> freeActions = new HashLists<>(false); // These key Strings to GameObjects, where the string is like "M" or "SP" or "H", etc.
	private ArrayList<GameObject> allObjects = new ArrayList<>();
	private ArrayList<GameObject> usedObjects = new ArrayList<>();
	
	private boolean inactiveItemWarning = true;
	
	private CharacterWrapper character;
	
	public PhaseManager(CharacterWrapper character,GameObject ponyObject,int basic,int sunlight,int sheltered) {
		this.character = character;
		this.basic = basic;
		this.sunlight = sunlight;
		this.sheltered = sheltered;
		this.ponyObject = ponyObject;
	}
	public boolean hasActionsLeft() {
		return getTotal() + freeActions.size() - character.getLostPhases() > 0;
	}
	public void setPonyLock(boolean val) {
		ponyLock = val;
	}
	public boolean isPonyLock() {
		return ponyLock;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(basic);
		sb.append(":");
		sb.append(sunlight);
		sb.append(":");
		sb.append(sheltered);
		sb.append(":");
		sb.append(ponyMoves);
		sb.append(":");
		sb.append(freeActions.keySet());
		sb.append(":xCave");
		sb.append(extraCavePhase);
		sb.append(":xDwell");
		sb.append(extraDwellingPhase);
		return sb.toString();
	}
	/**
	 * Returns false only when the thing is a horse, and you have already used a horse!
	 */
	public boolean canActivateThing(GameObject thing) {
		RealmComponent rc = RealmComponent.getRealmComponent(thing);
		if (rc.isHorse()) {
			// Make sure there isn't already a different horse used
			for (GameObject go : usedObjects) {
				if (!go.equals(thing)) {
					rc = RealmComponent.getRealmComponent(go);
					if (rc.isHorse()) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	/**
	 * This is called when activating a treasure while using Timeless Jewel to make sure any new extra actions are added.
	 */
	public void updateNewActivatedTreasure(GameObject thing) {
		if (!allObjects.contains(thing)) {
			ArrayList<String> free = thing.getThisAttributeList(Constants.EXTRA_ACTIONS);
			if (free!=null) {
				for (String freeAction : free) {
					addFreeAction(freeAction,thing);
				}
			}
			if (thing.hasThisAttribute(Constants.EXTRA_CAVE_PHASE)) {
				addExtraCavePhase(thing);
			}
			RealmComponent rc = RealmComponent.getRealmComponent(thing);
			if (rc.isHorse()) {
				BattleHorse steed = (BattleHorse)rc;
				if (steed.extraMove()) {
					addFreeAction("M",steed.getGameObject());
					ponyObject = null;
				}
				else if (steed.doublesMove()) {
					ponyObject = thing;
				}
			}
		}
	}
	public void updateInactiveThings() {
		// Check to see if anything became inactive that needs to be removed from free actions
		ArrayList<Requirement> toRemove = new ArrayList<>();
		for (GameObject go : allObjects) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isItem() && !go.hasThisAttribute(Constants.ACTIVATED)) {
				if (!usedObjects.contains(go)) {
					toRemove.add(new Requirement(go));
				}
			}
		}
		allObjects.removeAll(toRemove);
		ArrayList<String> freeToRemove = new ArrayList<>();
		for (String val : freeActions.keySet()) {
			ArrayList<GameObject> list = freeActions.getList(val);
			if (list.removeAll(toRemove)) {
				if (list.isEmpty()) {
					freeToRemove.add(val);
				}
			}
		}
		for (String val : freeToRemove) {
			freeActions.remove(val);
		}
	}
	public void addExtraBasicPhase() {
		basic++;
	}
	public void addFreeAction(String phase,GameObject go) {
		addFreeAction(phase,go,null,false);
	}
	public void addFreeAction(String phase,GameObject go,boolean force) {
		addFreeAction(phase,go,null,force);
	}
	public void addFreeAction(String phase,GameObject go,TileLocation requiredLocation,boolean force) {
		if (force || !allObjects.contains(go)) { // RW 5/22/2011 - Commented out for quest support - See BUG 1050 for why this was added in the first place...
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
			if (rc.isHorse() && ((SteedChitComponent)rc).doublesMove() && hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS)) {
				freeActions.put(phase,new Requirement(go,requiredLocation,null,"!M"));
			}
			else {
				freeActions.put(phase,new Requirement(go,requiredLocation));
			}
			allObjects.add(go);
		}
	}
	public void addExtraCavePhase(GameObject thing) {
		if (thing==null || !allObjects.contains(thing)) {
			if (thing!=null) {
				freeActions.put(EXTRA_CAVE_PHASE,new Requirement(thing));
				allObjects.add(thing);
			}
			extraCavePhase++;
		}
	}
	public void addExtraDwellingPhase() {
		extraDwellingPhase++;
	}
	public void updateClearing(TileLocation tl) {
		updateClearing(tl,false);
	}
	public void updateClearing(TileLocation tl,boolean isCurrent) {
		if (tl==null || !tl.isInClearing()) return;
		
		// Check the clearing itself!  (Blazing Light)
		ArrayList<String> clist = tl.clearing.getFreeActions();
		if (clist!=null) {
			for (String free  : clist) {
				if (Constants.EXTRA_CAVE_PHASE.equals(free)) {
					GameObject go = tl.clearing.getFreeActionObject(free);
					addExtraCavePhase(go);
				}
			}
		}
		if (tl.clearing.isCave() && tl.clearing.isLighted()) {
			markInCave(true);
		}
		else if (tl.clearing.isCave()) {
			markInCave();
		}
		else if (tl.clearing.holdsDwelling()) {
			markInDwelling();
		}
		else {
			// Not a cave or a dwelling?  You are outside!
			markOutside();
		}
		
		// Now we can search for this case (currently ONLY Toadstool Circle)
		if (isCurrent) {
			removeLocationSpecificFreeActions(tl);
		}
		for (RealmComponent rc : tl.clearing.getClearingComponents()) {
			String free = rc.getGameObject().getThisAttribute(Constants.EXTRA_ACTIONS_CLEARING);
			if (free!=null) {
				addFreeAction(free,rc.getGameObject(),tl,false);
			}
		}
	}
	public void markInCave() {
		markInCave(false);
	}
	public void markInCave(boolean lighted) {
		if (extraCavePhase>0) {
			basic += extraCavePhase;
			extraCavePhase = 0;
			List list = freeActions.getList(EXTRA_CAVE_PHASE);
			if (list!=null) {
				for (Iterator i=list.iterator();i.hasNext();) {
					Requirement r = (Requirement)i.next();
					usedObjects.add(r.getGameObject());
				}
//				usedObjects.addAll(list);
				freeActions.remove(EXTRA_CAVE_PHASE);
			}
		}
		if (!lighted) {
			sunlight=0;
			if (usedSunlight) {
				basic = 0;
				sheltered = 0;
			}
		}
		ponyObject = null;
	}
	public void markInDwelling() {
		if (extraDwellingPhase>0) {
			basic += extraDwellingPhase;
			extraDwellingPhase = 0;
		}
	}
	public void markOutside() {
		sheltered = 0; // No sheltered phases outside!
		if (hasUsedSheltered()) {
			freeActions.clear();
			allObjects.clear();
			ponyMoves = 0;
			extraCavePhase = 0;
			extraDwellingPhase = 0;
		}
	}
	public boolean hasUsedSheltered() {
		return usedSheltered;
	}
	public boolean hasUsedSunlight() {
		return usedSunlight;
	}
	public boolean willRequireSunlight(String phase) {
		if (basic<=0 && sheltered<=0) {
			// Convert detail action into plain action (M-CV3 becomes M)
			phase = simplifyAction(phase);
			ArrayList list = freeActions.getListAsNew(trimmedPhase(phase));
			return list==null || list.isEmpty();
		}
		return false;
	}
	public int getTotal() {
		return basic+sunlight+sheltered;
	}
	private static String trimmedPhase(String phase) {
		if (phase.endsWith("!")) {
			return phase.substring(0,phase.length()-1);
		}
		return phase;
	}
	private boolean phaseRequiresObject(String phase) {
		ActionId action = CharacterWrapper.getIdForAction(phase);
		if (action==ActionId.EnhPeer) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
			boolean flyingActivity = hostPrefs.hasPref(Constants.ADV_FLYING_ACTIVITIES) && character.getCurrentActionsCodes().contains(DayAction.FLY_ACTION.getCode());
			if (flyingActivity) {
				return false;
			}
			if (!character.hasActiveInventoryThisKeyAndValue(Constants.SPECIAL_ACTION,"ENHANCED_PEER")) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return	null if the phase is impossible.  Otherwise, you get a list of required objects and/or the "PHASE"
	 * 			string which indicates a regular phase can be used.
	 */
	private ArrayList getRequiredObjects(String phase,TileLocation newLocation) {
		ArrayList list;
		if (phase.endsWith("!")) {
			phase = phase.substring(0,phase.length()-1);
		}
		list = freeActions.getListAsNew(trimmedPhase(phase));
		if (list!=null) {
			if (ponyLock && ponyObject!=null) {
				list.remove(ponyObject);
			}
			else if (!ponyLock && ponyObject!=null) {
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
				if (hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS)
						&& newLocation!=null && newLocation.hasClearing() && newLocation.clearing.isMountain()) {
					list.remove(ponyObject);
				}
			}
			Collections.sort(list,new Comparator<Requirement>() {
				public int compare(Requirement r1,Requirement r2) {
					int ret = 0;
										
					GameObject go1 = r1.getGameObject();
					GameObject go2 = r2.getGameObject();
					
					int n1 = go1.hasThisAttribute("horse")?0:1;
					int n2 = go2.hasThisAttribute("horse")?0:1;
					
					ret = n1-n2;
					
					return ret;
				}
			});
		}
		int regularPhases = getTotal();
		if (regularPhases>0 && !phaseRequiresObject(phase)) {
			if (list==null) {
				list = new ArrayList<>();
			}
			for (int i=0;i<regularPhases;i++) {
				list.add(REGULAR_PHASE); // this is added to the end of the list, because it is lowest priority
			}
		}
		if (list!=null) {
			if (!list.isEmpty()) {
				return list;
			}
		}
		return null;
	}
	private ArrayList getRequiredObjects(String phase) {
		return getRequiredObjects(phase,null);
	}
	/**
	 * @return		A valid activated game object or null, if none required.  This method is called with the assumption
	 * 				that the appropriate checks were already made regarding the viability of the presented phase.
	 */
	public GameObject getNextRequiredObject(String fullPhase,boolean ponyActive) {
		// Convert detail action into plain action (M-CV3 becomes M)
		String phase = simplifyAction(fullPhase);
		Collection<GameObject> activeInventory = character.getActiveInventory();
		Collection<GameObject> travelers = character.getFollowingTravelers();
		Collection<GameObject> allSpells = character.getSpellExtraSources();
		Collection<GameObject> clearingObjects = character.getCurrentClearingExtraActionObjects();
		
		boolean movePhase = "M".equals(phase) || "M!".equals(phase);
		TileLocation newLocation = null;
		if (movePhase) {
			newLocation = ClearingUtility.deduceLocationFromAction(character.getGameObject().getGameData(),fullPhase);
		}
		ArrayList list = getRequiredObjects(phase,newLocation);
		if (list!=null) {
			TileLocation current = character.getCurrentLocation();
			for (Iterator i=list.iterator();i.hasNext();) {
				Object o = i.next();
				if (o instanceof Requirement) {
					Requirement r = (Requirement)o;
					if (r.isMet(current)) {
						if (r.willBeMet(newLocation)) {
							GameObject go = r.getGameObject();
							if (character.getGameObject().equals(go)
									|| activeInventory.contains(go)
									|| travelers.contains(go)
									|| clearingObjects.contains(go)
									|| (allSpells!=null && allSpells.contains(go))) {
								
								return go;
							}
						}
					}
				}
				else {
					// Once you reach strings, then you are beyond the list of required objects
					break;
				}
			}
		}
		
		return null;
	}
	public void addPerformedPhase(String phase,GameObject go,boolean ponyActive,TileLocation actionLocation) {
		_addPerformedPhase(phase,go,ponyActive,actionLocation);
	}
	private void _addPerformedPhase(String fullPhase,GameObject go,boolean ponyActive,TileLocation actionLocation) {
		// Convert detail action into plain action (M-CV3 becomes M)
		String phase = simplifyAction(fullPhase);
		boolean movePhase = "M".equals(phase) || "M!".equals(phase);

		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
		TileLocation newLocation = null;
		if (hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS) && movePhase) {
			newLocation = ClearingUtility.deduceLocationFromAction(character.getGameObject().getGameData(),fullPhase);
		}
				
		if (ponyObject!=null && go!=ponyObject && ponyActive && movePhase && ponyMoves==0) {
			TileLocation current = ClearingUtility.getTileLocation(ponyObject);
			if (current!=null && (!current.isInClearing() || (!current.clearing.isCave() && !current.clearing.isWater()))) {			
				if (hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS)) {
					if (newLocation==null || !newLocation.hasClearing() || !newLocation.clearing.isMountain()) {
						ponyMoves++;
						freeActions.put("M",new Requirement(ponyObject,null,null,"!M"));
						allObjects.add(ponyObject);
					}
				}
				else {
					ponyMoves++;
					freeActions.put("M",new Requirement(ponyObject));
					allObjects.add(ponyObject);
				}
			}
		}
		
		if (phase.indexOf('!')==1 && go==ponyObject) {
			go = null;
		}
		
		if (hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS) && go==ponyObject
				&& newLocation!=null && newLocation.hasClearing() && newLocation.clearing.isMountain()) {
			go = null;
		}
		
		if (go==null) {
			if (sheltered>0 && basic==0 && sunlight==0) { // use sheltered LAST
				sheltered--;
				usedSheltered = true;
			}
			else if (basic>0 || sunlight==0) {
				basic--;
			}
			else {
				sunlight--;
				usedSunlight = true;
			}
		}
		else {
			ArrayList list = freeActions.getList(trimmedPhase(phase));
			list.remove(new Requirement(go));
			usedObjects.add(go);
			if (go==ponyObject) {
				ponyMoves--;
			}
			if (list.isEmpty()) {
				freeActions.remove(trimmedPhase(phase));
			}
		}
		
		if (movePhase && actionLocation!=null && actionLocation.hasClearing() && actionLocation.clearing.isWater()) {
			GameObject item = character.getActiveInventoryThisKey(Constants.SAILS);
			if (item!=null) {
				String lastLoc = character.getGameObject().getThisAttribute(Constants.SAILS_LAST_CLEARING);
				if (lastLoc==null || !lastLoc.matches(actionLocation.toString())) {
					character.getGameObject().setThisAttribute(Constants.SAILS_LAST_CLEARING,actionLocation.toString());
					addFreeAction("M",item,true);
				}
			}
		}
			
//		if (movePhase) {
//			removeLocationSpecificFreeActions(actionLocation);
//		}
	}
	public void removeLocationSpecificFreeActions(TileLocation tl) {
		// Moved, so make sure that any free actions gained by location are removed
		ArrayList<String> removeKeys = new ArrayList<>();
		for (String key : freeActions.keySet()) {
			ArrayList<Requirement> remove = new ArrayList<>();
			List<GameObject> list = freeActions.getList(key);
			for (Object o : list) {
				if (o instanceof Requirement) {
					Requirement r = (Requirement)o;
					if (!usedObjects.contains(r.getGameObject())) {
						if (r.requiresLocation() && r.isMet(tl)) {
							remove.add(r);
							// If the free action was never used, then be sure to free it up if it becomes available again
							// (like when the player moves BACK into the clearing with the TOADSTOOL CIRCLE)
							allObjects.remove(r.getGameObject());
						}
					}
				}
			}
			list.removeAll(remove);
			if (list.isEmpty()) {
				removeKeys.add(key);
			}
		}
		for (String key:removeKeys) {
			freeActions.remove(key);
		}
	}
	public int getNumberOfActionsAllowed(String action,boolean pony) {
		// Convert detail action into plain action (M-CV3 becomes M)
		action = simplifyAction(action);

		ArrayList list = getRequiredObjects(action);
		return list==null?0:list.size();
	}
	private static String simplifyAction(String action) {
		DayAction da = DayAction.getDayAction(CharacterWrapper.getIdForAction(action));
		String simple = da.getCode();
		if (action.indexOf('!')==1) {
			simple = simple + "!";
		}
		if (simple.equals("SPX")) {
			simple = "SP";
		}
		return simple;
	}
	public boolean canAddAction(String action,boolean pony) {
		return canAddAction(action,pony,null);
	}
	private ArrayList<GameObject> active;
	private ArrayList<GameObject> inactive;

	public boolean canAddAction(String fullAction,boolean pony,JFrame parent) {
		// First, count the actions
		int count = 1; // default
		StringTokenizer phases = null;
		if (fullAction.indexOf(",")>=0) {
			phases = new StringTokenizer(fullAction,",");
			count = phases.countTokens();
		}
		

		// Convert detail action into plain action (M-CV3 becomes M)
		String action = simplifyAction(fullAction);
		boolean movePhase = "M".equals(action) || "M!".equals(action);
		TileLocation newLocation = null;
		if (movePhase) {
			newLocation = ClearingUtility.deduceLocationFromAction(character.getGameObject().getGameData(),fullAction);
		}
		ArrayList list = getRequiredObjects(action,newLocation);
		
		// Have to check for a special case here - generalizing is just too damned complicated!
		boolean specialCaseOverride = false;
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
		if (count>1 && list!=null && list.contains(REGULAR_PHASE) && "M".equals(action) && pony) {
			// This override happens when you only have one phase left, and you are trying to enter the mountains with a pony
			if (!hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS)) {
				specialCaseOverride = true;
			}
		}
		if (specialCaseOverride || (list!=null && list.size()>=count)) {
			if (parent!=null) {
				// Sort the strings from the gameobjects
				ArrayList<String> strings = new ArrayList<String>();
				ArrayList<GameObject> requiredObjects = new ArrayList<>();
				Collection<GameObject> clearingObjects = character.getCurrentClearingExtraActionObjects();
				refreshInventoryLists();
				for (Iterator i=list.iterator();i.hasNext();) {
					Object o = i.next();
					if (o instanceof String) {
						strings.add((String)o);
					}
					else {
						Requirement r = (Requirement)o;
						GameObject go = r.getGameObject();
							requiredObjects.add(go);
					}
				}
				if (requiredObjects.size()>0) {
					// There are required items here!
					GameObject toUse = null;
					ArrayList<GameObject> needValidate = new ArrayList<>();
					for (GameObject go:requiredObjects) {
						if (character.getGameObject().equals(go) || active.contains(go) || clearingObjects.contains(go)) {
							return true;
						}
						needValidate.add(go);
						if (validateRequirement(parent,strings,go,false)) {
							toUse = go;
							break;
						}
					}
					if (toUse==null) { // just use the first one then
						if (needValidate.isEmpty()) {
							return false;
						}
						validateRequirement(parent,strings,needValidate.get(0),true);
						return false;
					}
					
					validateRequirement(parent,strings,toUse,true);
				}
			}
			return true;
		}
		return false;
	}
	private boolean validateRequirement(JFrame parent,ArrayList<String> strings,GameObject go,boolean process) {
		RealmComponent rc = RealmComponent.getRealmComponent(go);
		
		if (rc.isSpell()) {
			if (!validateSpellRequirement(parent,strings,go,process)) {
				return false;
			}
		}
		else {
			if (!validateNonSpellRequirement(parent,strings,go,process)) {
				return false;
			}
		}
		return true;
	}
	private boolean validateSpellRequirement(JFrame parent,ArrayList<String> strings,GameObject go,boolean process) {
		RealmComponent rc = RealmComponent.getRealmComponent(go);
		SpellWrapper spell = new SpellWrapper(go);
		if (!spell.isAlive()) {
			if (strings.size()>0) {
				if (process) {
					if (inactiveItemWarning) {
						QuietOptionPane.showMessageDialog(parent,
								"The "+character.getGameObject().getName()
								+" used the spell \""+go.getName()+"\" to record actions during BirdSong.\n"
								+"It is not currently alive, so a regular phase will be used instead.",
								"Required Spell",
								JOptionPane.WARNING_MESSAGE,
								rc.getIcon(),
								"Don't warn me again this turn",
								false);
						inactiveItemWarning = !QuietOptionPane.isLastWasSilenced();
					}
					strings.remove(0);
				}
			}
			else {
				if (process) {
					JOptionPane.showMessageDialog(parent,
							"The "+character.getGameObject().getName()
							+" used the spell \""+go.getName()+"\" to record actions during BirdSong.\n"
							+"It is not currently alive, so the current phase is invalid.",
							"Required Spell",
							JOptionPane.WARNING_MESSAGE,
							rc.getIcon());
				}
				return false;
			}
		}
		return true;
	}
	private boolean validateNonSpellRequirement(JFrame parent,ArrayList<String> strings,GameObject go,boolean process) {
		RealmComponent rc = RealmComponent.getRealmComponent(go);
		boolean transmorphed = character.isTransmorphed();
		// Required Treasure
		// Oops, not active!  Does the character even still have it?
		if (inactive.contains(go) && !transmorphed) {
			if (!manageInactiveInventoryRequirement(parent,strings,go,process)) {
				return false;
			}
		}
		else {
			// Not sure how this is possible, but if the character managed to lose the
			// necessary item before taking his/her turn, it should use up a regular
			// phase instead, or return false
			if (strings.size()>0) {
				if (process) {
					strings.remove(0); // doesn't matter which is removed
				}
			}
			else {
				if (process) {
					JOptionPane.showMessageDialog(parent,
							"The "+character.getGameObject().getName()
							+" used the "+go.getName()+" to record actions during BirdSong.\n"
							+"It is currently missing or unavailable, so the current phase is invalid.",
							"Missing Required Item!",
							JOptionPane.WARNING_MESSAGE,
							rc.getIcon());
				}
				return false;
			}
		}
		return true;
	}
	private boolean manageInactiveInventoryRequirement(JFrame parent,ArrayList<String> strings,GameObject go,boolean process) {
		RealmComponent rc = RealmComponent.getRealmComponent(go);
		
		// Okay, just need to reactivate it, if possible
		if (process) {
			JOptionPane.showMessageDialog(parent,
					"The "+character.getGameObject().getName()
					+" used the "+go.getName()+" to record actions during BirdSong.\n"
					+"It is currently inactive, and will be activated now to continue.",
					"Activating Required Item",
					JOptionPane.WARNING_MESSAGE,
					rc.getIcon());
		}
		if (process && active.contains(go)) return true; // already done by process==false, so don't do it again!
		if (!TreasureUtility.doActivate(parent,character,go,null,false)) {
			// Wasn't possible, so see if there is a string we can use
			if (strings.size()>0) {
				if (process) {
					if (inactiveItemWarning) {
						QuietOptionPane.showMessageDialog(parent,
								"The "+go.getName()+" could not be activated, so a regular phase will be used instead.",
								"Activating Required Item",
								JOptionPane.WARNING_MESSAGE,
								rc.getIcon(),
								"Don't warn me again this turn",
								false);
						inactiveItemWarning = !QuietOptionPane.isLastWasSilenced();
					}
					strings.remove(0);
				}
			}
			else {
				if (process) {
					JOptionPane.showMessageDialog(parent,
							"The "+go.getName()+" could not be activated, so the current phase is invalid.",
							"Activating Required Item",
							JOptionPane.WARNING_MESSAGE,
							rc.getIcon());
				}
				return false;
			}
		}
		else {
			refreshInventoryLists();
		}
		return true;
	}
	public void refreshInventoryLists() {
		active = character.getActiveInventory();
		active.addAll(character.getFollowingTravelers());
		inactive = character.getInactiveInventory();
	}
	
	/**
	 * Ignores activated object state
	 * 
	 * @return	true on success
	 */
	public boolean forcePerformedAction(String fullAction,boolean pony,TileLocation actionLocation) {
		if (fullAction.indexOf(",")>=0) {
			boolean ret = true;
			StringTokenizer phases = new StringTokenizer(fullAction,",");
			while(phases.hasMoreTokens()) {
				if (!forcePerformedAction(phases.nextToken(),pony,actionLocation)) {
					ret = false;
				}
			}
			return ret;
		}
		// Convert detail action into plain action (M-CV3 becomes M)
		DayAction da = DayAction.getDayAction(CharacterWrapper.getIdForAction(fullAction));
		if (da==null) { // this can happen if an action becomes a blank phase, and this method is called too early
			return false;
		}
		String action = simplifyAction(fullAction);
		
		boolean movePhase = "M".equals(action) || "M!".equals(action);
		TileLocation newLocation = null;
		if (movePhase) {
			newLocation = ClearingUtility.deduceLocationFromAction(character.getGameObject().getGameData(),fullAction);
		}
		boolean didit = false;
		ArrayList list = getRequiredObjects(action,newLocation);
		if (list!=null) {
			if (!pony && ponyObject!=null) {
				while(list.contains(ponyObject)) {
					list.remove(ponyObject);
				}
			}
			for (Iterator n=list.iterator();n.hasNext();) {
				Object req = n.next();
				if (req instanceof String) {
					addPerformedPhase(fullAction,(GameObject)null,pony,actionLocation);
					didit = true;
					break;
				}
				else if (req!=ponyObject || pony) {
					Requirement r = (Requirement)req;
					if (r.isMet(actionLocation)) {
						if (r.willBeMet(newLocation)) {
							GameObject go = r.getGameObject();
							addPerformedPhase(fullAction,go,pony,actionLocation);
							didit = true;
							break;
						}
					}
				}
			}
		}
		if (!didit) {
			addPerformedPhase(fullAction,(GameObject)null,pony,actionLocation);
		}
		return didit;
	}
	public int getBasic() {
		return basic;
	}
	public int getSheltered() {
		return sheltered;
	}
	public int getSunlight() {
		return sunlight;
	}
	public List getExtraActionsList() {
		ArrayList list = new ArrayList();
		for (Iterator i=freeActions.keySet().iterator();i.hasNext();) {
			String action = (String)i.next();
			ArrayList requirements = freeActions.getList(action);
			for (Iterator n=requirements.iterator();n.hasNext();) {
				Requirement r = (Requirement)n.next();
				GameObject go = r.getGameObject();
				Object[] o = new Object[2];
				o[0] = action;
				o[1] = go;
				list.add(o);
			}
		}
		return list;
	}
	public int getPonyMoves() {
		return ponyMoves;
	}
	/**
	 * For testing only!
	 */
	public static void main(String[] args) {
//		PhaseManager pm = _getTest();
//		PhaseManager pm1 = _getTest();
//		GameData data = new GameData();
		
//		JOptionPane.showMessageDialog(null, new PhaseManagerIcon(pm));
//		pm.forcePerformedAction("S", false);
//		JOptionPane.showMessageDialog(null, new PhaseManagerIcon(pm));
//	GameObject thing1 = data.createNewObject();
//	pm.addFreeAction("S",thing1);
//	pm1.addFreeAction("S",thing1);
//		JOptionPane.showMessageDialog(null, new PhaseManagerIcon(pm));
//		pm.forcePerformedAction("M", false);
//		JOptionPane.showMessageDialog(null, new PhaseManagerIcon(pm));
//		
//		pm1.remember(pm.getMemory());
//		JOptionPane.showMessageDialog(null, new PhaseManagerIcon(pm1));
	}
//	private static PhaseManager _getTest() {
//		PhaseManager pm = new PhaseManager(null,null,2,2,0);
//		GameData data = new GameData();
////		GameObject thing1 = data.createNewObject();
//		GameObject thing2 = data.createNewObject();
//		pm.addFreeAction("H",thing2);
//		return pm;
//	}
	public static void _main(String[] args) {
		String[] test = {"M","H","M","S","S","M","H","M","M","M","M","M","M","M","M"};
		GameData data = new GameData();
		GameObject thing1 = data.createNewObject();
		GameObject thing2 = data.createNewObject();
		ArrayList<GameObject> activatedObjects = new ArrayList<>();
		activatedObjects.add(thing1);
//		activatedObjects.add(thing2);
		
		PhaseManager pm = new PhaseManager(null,null,2,0,0);
		pm.addFreeAction("S",thing1);
		pm.addFreeAction("H",thing2);
		
		System.out.println(pm);
		boolean pony = true;
//		int t=0;
		for (int i=0;i<test.length;i++) {
			System.out.print("Trying "+test[i]+": ");
			ArrayList list = pm.getRequiredObjects(test[i]);
			if (list!=null) {
				boolean didit = false;
				for (Iterator n=list.iterator();n.hasNext();) {
					Object req = n.next();
					if (req instanceof String) {
						System.out.println(req);
						pm.addPerformedPhase(test[i],(GameObject)null,pony,null); // probably broken
						didit = true;
						break;
					}
					if (activatedObjects.contains(req)) {
						System.out.println("Used "+req);
						pm.addPerformedPhase(test[i],(GameObject)req,pony,null); // probably broken
						didit = true;
						break;
					}
				}
				if (!didit) {
					System.out.println("Pause - You need to activate: "+list);
				}
			}
			else {
				System.out.println("Impossible");
			}
//			if (t++ == 5) {
//				System.out.println("Disable Pony");
//				pony = false;
//			}
		}
		System.out.println("Done!");
		System.out.println(pm);
	}
	private static class Requirement {
		private GameObject go;
		private TileLocation tl;
		private String clearingType;
		private String plannedClearingType;
		public Requirement(GameObject go) {
			this(go,null,null,null);
		}
		public Requirement(GameObject go,TileLocation tl) {
			this(go,tl,null,null);
		}
		public Requirement(GameObject go,TileLocation tl,String clearingType,String plannedClearingType) {
			this.go = go;
			this.tl = tl;
			this.clearingType = clearingType;
			this.plannedClearingType = plannedClearingType;
			
//			// Handle this special case (Ancient Telescope)
//			String ep = go.getThisAttribute(Constants.ENHANCED_PEER);
//			if ("M1".equals(ep)) {
//				clearingType = "mountain";
//			}
		}
		public String toString() {
			return go.toString();
		}
		public GameObject getGameObject() {
			return go;
		}
		public boolean requiresLocation() {
			return tl!=null || clearingType!=null;
		}
		public boolean isMet(TileLocation current) {
			if (current!=null) {
				if (tl!=null) {
					return tl.equals(current);
				}
				else if (clearingType!=null && current.hasClearing()) {
					if (clearingType.startsWith("!")) {
						return !clearingType.substring(1).matches(current.clearing.getType());
					}
					return clearingType.equals(current.clearing.getType());
				}
			}
			return true;
		}
		public boolean willBeMet(TileLocation plannedLocation) {
			if (plannedClearingType!=null && plannedLocation!=null) {
				if (plannedLocation.hasClearing()) {
					if (plannedClearingType.startsWith("!")) {
						return !plannedClearingType.substring(1).matches(plannedLocation.clearing.getType());
					}
					return plannedClearingType.matches(plannedLocation.clearing.getType());
				}
			}
			return true;
		}
		public boolean equals(Object o1) {
			Requirement r = (Requirement)o1;
			return go.equals(r.go);
		}
	}
}