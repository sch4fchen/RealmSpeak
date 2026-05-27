package com.robin.magic_realm.RealmSpeak;

import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.*;
import com.robin.general.util.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.attribute.DayAction.ActionId;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.store.GuildStore;
import com.robin.magic_realm.components.store.Store;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.table.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellMasterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;
import com.robin.magic_realm.components.wrapper.CharacterWrapper.ActionState;

public class ActionRow {
	private static final ImageIcon PENDING_ICON = null;
	private static final ImageIcon COMPLETED_ICON = IconFactory.findIcon("images/actions/greencheck.gif");
	private static final ImageIcon CANCELLED_ICON = IconFactory.findIcon("images/actions/redx.gif");
	private static final ImageIcon INVALID_ICON = IconFactory.findIcon("images/actions/ban.gif");
	
	public static final String SLEEPING = "Sleeping";
	
	public static boolean askAboutAbandoningFollowers = false;

	private ImageIcon icon;
	private String action;
	private String actionTypeCode;
	private String result;
	private boolean completed;
	private boolean cancelled;
	private boolean negate;
	
	private boolean autoMarkInventory;
	
	private String blankReason = null; // identifies a BLANK phase
	private boolean spawned = false; // identifies "spawned" actions that aren't recorded or tracked
	private boolean invalid = false; // identifies an INVALID phase (this doesn't count as a real phase!!)
	private boolean invalidPlanned = false; // identifies an phase, which was INVALID when planned

	private RealmTurnPanel turnPanel;
	private RealmGameHandler gameHandler;
	private CharacterWrapper character;
	private TileLocation location; // for moves only
	private DieRoller roller;
	
	private int count=1;
	private int bonusCount=0; // The number of "bonus" phases that don't apply towards the phase manager
	
	private ActionRow newAction = null;

	private boolean isContinuation = false;        // true when this row is the 2nd+ sub-phase of a split multi-phase action
	private boolean isFirstPhaseOfMultiPhaseMove = false; // true on phase 1 of a split — skips dispatch so char stays in starting clearing

	private RealmTable realmTable = null;

	private boolean isFollowing;
	
	private boolean ponyLock = false;

	/**
	 * For TESTING ONLY!!!!
	 */
	public ActionRow(String action,String actionTypeCode) {
		this.action = action;
		this.actionTypeCode = actionTypeCode;
		icon = CharacterWrapper.getIconForAction(action);
		result = "";
		completed = false;
		cancelled = false;
		roller = null;
	}

	/**
	 * Primary action constructor
	 */
	public ActionRow(RealmTurnPanel turnPanel,CharacterWrapper character, String action,String actionTypeCode,boolean isFollowing) {
		this.turnPanel = turnPanel;
		this.gameHandler = turnPanel.getGameHandler();
		this.character = character;
		this.action = action;
		this.actionTypeCode = actionTypeCode;
		this.isFollowing = isFollowing;
		icon = CharacterWrapper.getIconForAction(action);
		result = "";
		completed = false;
		cancelled = false;
		roller = null;
	}
	/**
	 * This constructor is used to handle new rolls on tables
	 */
	private ActionRow(RealmTurnPanel turnPanel,CharacterWrapper character, RealmTable table,boolean isFollowing) {
		this.turnPanel = turnPanel;
		this.gameHandler = turnPanel.getGameHandler();
		this.character = character;
		this.action = null;
		this.actionTypeCode = null;
		this.isFollowing = isFollowing;
		icon = null;
		result = "";
		completed = false;
		cancelled = false;
		roller = null;
		realmTable = table;
	}
	public int getPhaseCount() {
		int pc = 1; // default
		if (action.indexOf(",")>=0) {
			StringTokenizer phases = new StringTokenizer(action,",");
			pc = phases.countTokens();
		}
		return pc;
	}
	public String toString() {
		String condition = cancelled?"cancelled":(completed?"completed":"pending");
		return action+" ("+condition+"): "+result;
	}
	public String getAction() {
		return action;
	}
	public ActionId getActionId() {
		return CharacterWrapper.getIdForAction(action);
	}
	private void handleTable(boolean foresigthPossible) {
		result = realmTable.getTableName(false); // show the short name!
		String message = null;
		if (realmTable.hideRoller()) {
			message = realmTable.applyOne(character);
		}
		else {
			roller = DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createRoller(realmTable);
			if (foresigthPossible && character.affectedByKey(Constants.FORESIGHT)
					&& !character.getGameObject().hasThisAttribute(Constants.DRINKS_BOUGHT) && !character.getGameObject().hasThisAttribute(Constants.FORESIGHT_USED)) {
				character.getGameObject().setThisAttribute(Constants.FORESIGHT_USED);
				int ret = JOptionPane.showConfirmDialog(
						new JFrame(),
						"Do you want to cancel your current activity?\n"+realmTable.getTableName(false)+" - "+roller.getStringResult(),
						"Foresight",
						JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,character.getIcon());
				if (ret == JOptionPane.YES_OPTION) {
					negate = true;
					message = character.getGameObject().getName() + " negates result of "+realmTable.getTableName(false);
					// revert stats
					ArrayList<String> phaseChitIds = new ArrayList<>();
					if (character.getGameObject().hasThisAttribute(CharacterWrapper.PHASE_CHITS)) {
						for (String id : character.getGameObject().getThisAttributeList(CharacterWrapper.PHASE_CHITS)) {
							phaseChitIds.add(id);
						}
						if (!phaseChitIds.isEmpty()) {
							GameData gameData = character.getGameObject().getGameData();
							for (String id : phaseChitIds) {
								GameObject chitGo = gameData.getGameObject(id);
								RealmComponent chitRc = RealmComponent.getRealmComponent(chitGo);
								chitRc.setActivated(false);
								String spellId = chitGo.getThisAttribute(Constants.SPELL_ID);
								GameObject spellGo = gameData.getGameObject(Long.valueOf(spellId));
								SpellWrapper spell = new SpellWrapper(spellGo);
								spell.affectTargets(new JFrame(),GameWrapper.findGame(gameData),false,null);
								SpellMasterWrapper.getSpellMaster(gameData).addSpell(spell);
								character.unapplyPhaseChit(new JFrame(), chitGo, spell);
								character.getGameObject().add(chitGo);
							}
						}
					}
					if (character.getGameObject().hasThisAttribute(Constants.FORESIGHT_SAVED_STATS)) {
						for (String stat : character.getGameObject().getThisAttributeList(Constants.FORESIGHT_SAVED_STATS)) {
							if (stat.startsWith(Constants.FORESIGHT_SAVED_STATS_WISHED_STRENGTH)) {
								stat.replace(Constants.FORESIGHT_SAVED_STATS_WISHED_STRENGTH,"");
								character.setWishStrength(new Strength(stat));
							}
							else {
								StringTokenizer tokens = new StringTokenizer(stat,"_");
								String id = tokens.nextToken();
								String statusId = tokens.nextToken();
								for (CharacterActionChitComponent chit : character.getAllChits()) {
									if (chit.getGameObject().getStringId().matches(id)) {
										chit.setStateById(new Integer(statusId));
										break;
									}
								}
							}
						}
					}
				}
			}
			if (!negate) {
				message = realmTable.apply(character,roller);
			}
		}
		if (message!=null) {
			result = result + " - " + message;
			gameHandler.updateCharacterFrames();
		}
		if (!negate && realmTable.getNewTable()!=null) {
			newAction = new ActionRow(turnPanel,character,realmTable.getNewTable(),isFollowing);
			newAction.handleTable();
		}
		completed = true;
	}
	private void handleTable() {
		handleTable(true);
	}
	public String getResult() {
		return result;
	}
	public ActionRow makeCopy() {
		return new ActionRow(turnPanel,character,action,actionTypeCode,isFollowing);
	}
	public void setCount(int val) {
		count = val;
	}
	public int getCount() {
		return count;
	}
	public int getBonusCount() {
		return bonusCount;
	}
	public void incrementCount() {
		count++;
	}
	
	public ImageIcon getIcon() {
		return icon;
	}

	public String getDescription() {
		String description = "";
		if (blankReason!=null) {
			return "Invalid recorded action!  Blank Phase: "+blankReason;
		}
		if (invalid) {
			return "Invalid phase!";
		}
		ActionId id = CharacterWrapper.getIdForAction(action);
		if (ActionId.Hide==id) {
			description = "Hide";
		}
		else if (ActionId.Move==id) {
			description = "Move to " + location.clearing.getDescription();
			if (ponyLock) {
				description = description + " (Non-Pony Move)";
			}
		}
		else if (ActionId.Offroad==id) {
			description = "Offroad Travel";
			if (ponyLock) {
				description = description + " (Non-Pony Move)";
			}
		}
		else if (ActionId.Search==id) {
			description = "Search";
		}
		else if (ActionId.Trade==id) {
			description = "Trade";
		}
		else if (ActionId.Steal==id) {
			description = "Steal";
		}
		else if (ActionId.Rest==id) {
			description = "Rest "+count+" time"+(count==1?"":"s");
		}
		else if (ActionId.Alert==id) {
			description = "Alert"+(isFollowing?" (Optional)":"");
		}
		else if (ActionId.Hire==id) {
			description = "Hire";
		}
		else if (ActionId.Follow==id) {
			description = "Follow";
		}
		else if (ActionId.Spell==id) {
			description = "Enchant";
		}
		else if (ActionId.SpellPrep==id) {
			description = "Enchant Meditating";
		}
		else if (ActionId.EnhPeer==id) {
			description = "Enhanced Peer";
		}
		else if (ActionId.Fly==id) {
			description = "Fly to " + location.tile.getTileName();
		}
		else if (ActionId.RemSpell==id) {
			description = "Remote Spell";
		}
		else if (ActionId.Cache==id) {
			description = "Cache";
		}
		else if (ActionId.Heal==id) {
			description = "Heal";
		}
		else if (ActionId.Repair==id) {
			description = "Repair";
		}
		else if (ActionId.Fortify==id) {
			description = "Fortify";
		}
		if (result!=null && result.trim().length()>0) {
			return description + " - " + result;
		}
		return description;
	}
	
	public ImageIcon getStatusIcon() {
		switch(getActionState()) {
			case Pending:	return PENDING_ICON;
			case Invalid:	return INVALID_ICON;
			case Completed:	return COMPLETED_ICON;
			case Cancelled:	return CANCELLED_ICON;
		}
		throw new IllegalStateException("Unknown status");
	}
	private ActionState getActionState() {
		ActionState state = ActionState.Cancelled;
		if (invalid) {
			state = ActionState.Invalid;
		}
		if (!cancelled) {
			if (completed) {
				state = ActionState.Completed;
			}
			else {
				state = ActionState.Pending;
			}
		}
		return state;
	}
	public void setActionState(ActionState state) {
		completed = false;
		cancelled = false;
		invalid = false;
		switch(state) {
			case Pending:
				break;
			case Invalid:
				completed = true;
				invalid = true;
				break;
			case Completed:
				completed = true;
				break;
			case Cancelled:
				completed = true;
				cancelled = true;
				break;
		}
	}

	public boolean isPending() {
		return !cancelled && !completed;
	}

	public TileLocation getLocation() {
		return location;
	}
	public void setLocation(TileLocation location) {
		this.location = location;
	}

	public boolean isCancelled() {
		return cancelled;
	}
	public boolean isContinuation() {
		return isContinuation;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public void landCharacterIfNeeded() {
		ActionId id = CharacterWrapper.getIdForAction(action);
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		if (id!=ActionId.Fly && !(hostPrefs.hasPref(Constants.ADV_FLYING_ACTIVITIES) && (id==ActionId.Alert || id==ActionId.EnhPeer))) {
			// Make sure character is on the ground if not flying
			if (character.land(gameHandler.getMainFrame())) {
				// Check for blocking immediately
				if (!character.isBlocked() && RealmUtility.willBeBlocked(character,isFollowing,true)) {
					character.setBlocked(true);
				}
				gameHandler.getInspector().redrawMap();
			}
		}
	}

	/**
	 * The meat of an action - is this really the best place for this?
	 * 
	 * Mmmmmm.  ACTION MEAT!!!!
	 */
	public void process() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());

		// NOTE: "character" herein means a character, hired leader, or controlled monster.  If something
		//       applies to characters only and not hired leaders or monsters, it will say "character-only".
		//
		// Followers check: Followers will skip the following block, but instead will be processed together
		// with their phasing guide while the guide is being processed through the block.
		//
		// isBlocked check: while phasing, a character may become blocked by a non-phasing monster or character
		// in their clearing during post-phase (or may become blocked by initiating blocking of such a character
		// themselves).  In either case, the phasing character is blocked and will short-circuit any further
		// phases which call process() to be resolved.
		//
		// SLEEP check: checkSleep() inspects the character's chit states and their SLEEP attribute to
		// determine whether they must sleep this phase (e.g. exhausted all action chits, or a spell
		// put them to sleep).  If so, the action is cancelled and the result is set to "Sleeping".
		//
		// POST-PHASE PENDING guard runs first — if the previous action's post-phase dialogs are
		// still outstanding, pre-phase must not fire yet (blocking may still occur, which would
		// cancel the coming action and make the pre-phase moot).
		//
		// PRE-PHASE SEGMENT: handlePrePhase() evaluates whether any non-phasing character in the
		// clearing qualifies for pre-phase activities. There is a gate, and if passed, two 
		// independent triggers.
		//
		//   GATE: Reacting=ON is a hard requirement for any non-phasing character pre-phase activity.
		//
		//   Subsequent qualification for a pre-phase segment is determined by two independent triggers:
		//
		//     (A) Followers of a phasing guide always qualify.  Followers can use the pre-phase segment
		//         to: rearrange belongings, trade with anyone in the clearing, play color chits, pick up
		//         mission chits, or choose to stop following.
		//
		//     (B) Non-followers with Reacting ON who hold color chits — qualify only in
		//         3rd-edition mode (FE_PHASE_END_PLAYING_COLOR_CHIT is off). In 1st-edition, color chit
		//         play shifts to post-phase, so non-followers have nothing to do pre-phase unless they
		//         are followers of this guide (see A).
		//
		// Phasing characters always qualify for pre-phase activities: trading, rearranging items, color
		// chit play, and mission chit pickup.  There is no special dialog needed for phasing characters
		// to do these things, but if any non-phasing character qualifies for pre-phase activities, the
		// phasing character must resolve their pre-phase segment before the non-phasing characters will
		// be presented with their pre-phase activies dialog.
		//
		// Phasing guides who are hidden may drop followers who cannot detect them during the pre-phase if
		// the phase action will be a move.
		//
		// If any qualifying character exists and the segment hasn't fired for this action yet,
		// NeedsPrePhaseActivityDecision is set on the phasing character and we return true here,
		// deferring the action. RealmTurnPanel calls process() again after all dialogs are dismissed.
		//
		// POST-PHASE PENDING guard: isPostPhasePending() returns true when the PREVIOUS action's
		// post-phase dialogs haven't been cleared yet (another player in the clearing is still deciding).
		// This prevents Play All from racing ahead and executing the next action before everyone has
		// finished responding to the last one.
		if (!isFollowing) {
			// Auto-release lingering followers whenever the phasing char is mist-like.
			// TransmorphEffect doesn't re-fire when a permanent spell is re-energized
			// (e.g. a follower plays a color chit to re-activate the guide's Melt into Mist),
			// so followers can persist past the point where they should have been released.
			// Passing null dice skips monster summoning (same as the sleep/mist path).
			if (character.isMistLike()) {
				for (CharacterWrapper follower : character.getActionFollowers()) {
					if (!follower.getGameObject().hasThisAttribute(Constants.IGNORE_MIST_LIKE)) {
						RealmLogging.logMessage(character.getGameObject().getName(),
							follower.getGameObject().getName() + " can no longer follow (guide is mist-like).");
						follower.setStopFollowing(true);
						character.removeActionFollower(follower, null, null);
					}
				}
				gameHandler.updateCharacterFramesWithoutMap();
			}
			if (character.isBlocked()) {
				gameHandler.broadcast(character.getGameObject().getName(),"BLOCKED - Cannot perform action "+action);
				cancelled = true;
				result = "BLOCKED";
				return;
			}
			checkSleep();
			if (character.isSleep()) {
				cancelled = true;
				result = SLEEPING;
				return;
			}
			if (blankReason == null && !invalid) {
				if (isPostPhasePending()) return;
				if (handlePrePhase(hostPrefs)) return;
			}
		}

		// MULTI-PHASE SPLIT: a comma in the action string (e.g. "M-B14,M-B14" for a mountain move,
		// "M-B14,M-B14,M-B14" for a 3-phase weather move) means multiple phases are required. Split
		// at the first comma so each sub-phase gets its own full pre/post-phase cycle. Intermediate
		// phases skip dispatch (isFirstPhaseOfMultiPhaseMove); only the final phase executes the action.
		// Continuations are split the same way — this handles N phases for any N.
		if (action != null && action.indexOf(',') >= 0) {
			splitMultiPhaseAction();
		}

		// REMOVED: old per-character color-chit interrupt mechanism (formerly OPT_PHASE_BEGIN_PLAYING_COLOR_CHIT).
		// That option gate was removed and pre-phase color chit play is now the 3rd edition default, handled
		// entirely by the pre-phase dialog system above. The old mechanism used checkForColorChitInterruptionState()
		// to set NeedsPlayColorChitInterruptPhaseBeginningDecision on each character and then showed the
		// "Play Color Chit Now?!" button. That button is also commented out. The 1st edition equivalent
		// (FE_PHASE_END_PLAYING_COLOR_CHIT, post-phase) remains intact further below after the action executes.

		// TBD(3): Followers need an additional interphase window that fires DURING certain guide
		// actions (e.g. while the guide is moving, a follower may need to choose whether to move
		// with the guide or stay behind, pick up items, etc.). This mid-action dialog is distinct
		// from the pre-phase dialog: it fires after the guide's action dispatch begins but before
		// it resolves. The dispatch block below is the natural injection point — a new
		// handleMidActionFollowerPhase() method would gate execution here and defer via a flag,
		// similar to handlePrePhase(). Each follower ActionRow would need its own continuation
		// path once the guide's action completes.

		// ── BLOCK 2: FORESIGHT SNAPSHOT + ACTION DISPATCH ────────────────────────────────────────────
		//
		// completed=true is the optimistic default. Individual doXxxAction() methods set it to false if
		// the action fails or is interrupted mid-execution (e.g. a move into a blocked road, a search
		// that finds nothing, a hire that the native refuses). RealmTurnPanel checks completed after
		// process() returns to decide whether to advance the turn or stay on this row.
		//
		// BLANK / INVALID guard: blankReason!=null means this row was deliberately replaced with a
		// blank phase (e.g. illegal sunlight use in a cave — see RealmTurnPanel.playNext). invalid
		// means the action was never legal. Both skip the dispatch block entirely; the row is still
		// marked completed so the turn sequence advances past it.
		//
		// FORESIGHT: if the character has the Foresight advantage and hasn't used it this phase, we
		// snapshot the character's current wish-strength and all chit states into FORESIGHT_SAVED_STATS
		// before the action mutates anything. If the action goes badly, Foresight allows the player
		// to roll back to this snapshot (the rollback logic lives elsewhere).
		//
		// autoMarkInventory: most actions don't require the player to select which item they used, so
		// we default to marking all inventory as "not new" after the action. Trade, Search, Cache, and
		// Repair set this to false because they manage inventory visibility themselves.
		//
		// ACTION DISPATCH: a simple if/else chain maps the action's ActionId enum to a private
		// doXxxAction() method. Each method is responsible for the full resolution of that action type:
		//   - doMoveAction():   moves the character token to the target clearing; triggers road blocks,
		//                       hidden-path discovery, and follower movement.
		//   - doHideAction():   rolls to set the character hidden; result depends on chit strength.
		//   - doSearchAction(): rolls on the appropriate search table for the clearing type.
		//   - doTradeAction():  opens the trade dialog with the native group in the clearing.
		//   - doRestAction():   recovers one fatigued or wounded chit.
		//   - doAlertAction():  readies an alerted weapon or armor chit.
		//   - doHireAction():   attempts to hire a native leader.
		//   - doSpellAction():  casts the readied spell; consumes the spell energy.
		//   - (Follow is intentionally excluded — followers are set up during birdsong, not here.)
		//   - doEnhancedPeerAction(), doFlyAction(), doRemoteSpellAction(), doCacheAction(),
		//     doHealAction(), doRepairAction(), doFortifyAction(): less common actions.
		//
		// logAction() records the action result to the game log after dispatch so all paths
		// (completed, blank, or invalid) produce a log entry.
		//
		// The final isActive() check covers the rare case where an action kills the character mid-
		// execution (e.g. a fatigue/wound from a fly chit). If the character died during their own
		// action, there is nothing left to post-process.
		completed = true; // the default - can be modified if there are problems

		if (blankReason==null && !invalid && !isFirstPhaseOfMultiPhaseMove) {
			
			if (character.affectedByKey(Constants.FORESIGHT) && !character.getGameObject().hasThisAttribute(Constants.FORESIGHT_USED)) {
				Strength wishStrength = character.getWishStrength();
				if (wishStrength!=null) {
					character.getGameObject().addThisAttributeListItem(Constants.FORESIGHT_SAVED_STATS,Constants.FORESIGHT_SAVED_STATS_WISHED_STRENGTH+wishStrength.getChitString());
				}
				for (CharacterActionChitComponent chit : character.getAllChits()) {
					character.getGameObject().addThisAttributeListItem(Constants.FORESIGHT_SAVED_STATS,chit.getGameObject().getStringId()+"_"+chit.getStateId());
				}
			}
			
			autoMarkInventory = true;
			ActionId id = CharacterWrapper.getIdForAction(action);
			if (ActionId.Hide==id) {
				doHideAction();
			}
			else if (ActionId.Move==id) {
				doMoveAction();
			}
			else if (ActionId.Offroad==id) {
				doOffroadAction();
			}
			else if (ActionId.Search==id) {
				autoMarkInventory = false;
				doSearchAction();
			}
			else if (ActionId.Trade==id) {
				autoMarkInventory = false;
				boolean tj = character.canDoDaytimeRecord();
				doTradeAction();
				if (tj!=character.canDoDaytimeRecord()) {
					// Something happened, so BLOCK!!
					character.setBlocked(true);
				}
			}
			else if (ActionId.Steal==id) {
				doStealAction();
			}
			else if (ActionId.Rest==id) {
				doRestAction();
			}
			else if (ActionId.Alert==id) {
				doAlertAction();
			}
			else if (ActionId.Hire==id) {
				doHireAction();
			}
			//else if (ActionId.Follow==id) {
				// This is handled differently - not here!
			//}
			else if (ActionId.Spell==id) {
				doSpellAction();
			}
			//else if (ActionId.SpellPrep==id) {
				// does nothing
			//}
			else if (ActionId.EnhPeer==id) {
				doEnhancedPeerAction();
			}
			else if (ActionId.Fly==id) {
				doFlyAction();
			}
			else if (ActionId.RemSpell==id) {
				doRemoteSpellAction();
			}
			else if (ActionId.Cache==id) {
				autoMarkInventory = false;
				doCacheAction();
			}
			else if (ActionId.Heal==id) {
				doHealAction();
			}
			else if (ActionId.Repair==id) {
				autoMarkInventory = false;
				doRepairAction();
			}
			else if (ActionId.Fortify==id) {
				doFortifyAction();
			}
			
			if (autoMarkInventory) {
				character.markAllInventoryNotNew();
			}
			
			logAction();
			
			if (!character.isActive()) {
				return;
			}
		}
		
		// ── BLOCK 3: POST-ACTION CLEANUP ─────────────────────────────────────────────────────────────
		//
		// Runs unconditionally after the dispatch block (whether the action was blank, invalid, or
		// fully executed). The one exception is the isActive() early-return above — a dead character
		// skips everything from here on.
		//
		// SECOND SLEEP CHECK: checkSleep() is called again because some actions can exhaust chits
		// (e.g. a fly chit fatiguing, a wound from a fall) that push the character into sleep. If
		// the character fell asleep during the action itself we need to know before evaluating blocking.
		//
		// NO_HIDE ITEM: some clearings contain items or effects with the NO_HIDE key (e.g. certain
		// dwellings or map cards). If one is present in the character's current clearing, the character
		// — and every other character in that clearing — is forcibly revealed. This check runs after
		// movement so it fires correctly when a character just moved into a NO_HIDE clearing.
		//
		// PER-PHASE ATTRIBUTE CLEANUP: three attributes are cleared after every action:
		//   - DRINKS_BOUGHT: native goodwill bonus from buying drinks lasts only one phase.
		//   - FORESIGHT_USED: prevents Foresight from snapshotting more than once per phase.
		//   - FORESIGHT_SAVED_STATS: the snapshot itself; cleared so stale data can't be replayed.
		//   - MEDITATE_DISCOVER_SITES: if the character performed a meditation action, they
		//     automatically discover all treasure location chits in their current clearing. This runs
		//     here (after the action) so the clearing reflects the post-move location.
		checkSleep(); // check again, in case something changed during the action

		GameObject noHideItem = ClearingUtility.getItemInClearingWithKey(location,Constants.NO_HIDE);
		if (noHideItem!=null) {
			character.setHidden(false);
			for (RealmComponent rc:location.clearing.getClearingComponents()) {
				if (rc.isCharacter()) {
					(new CharacterWrapper(rc.getGameObject())).setHidden(false);
				}
			}
		}
		
		character.getGameObject().removeThisAttribute(Constants.DRINKS_BOUGHT);
		character.getGameObject().removeThisAttribute(Constants.FORESIGHT_USED);
		character.getGameObject().removeThisAttribute(Constants.FORESIGHT_SAVED_STATS);
		if (character.getGameObject().hasThisAttribute(Constants.MEDITATE_DISCOVER_SITES)) {
			TileLocation current = character.getCurrentLocation();
			if (current.isInClearing()) {
				for (RealmComponent rc : current.clearing.getClearingComponents(false)) {
					if (rc.isTreasureLocation()) {
						character.addTreasureLocationDiscovery(rc.getGameObject().getName());
					}
				}
			}
		}
		
		if (completed) { // don't check for blocking until completed!
			// Check for Violent Storm
			if (willBeAffectedByStorm()) {
				TileLocation current = character.getCurrentLocation();
				int phasesLost1 = current.tile.getGameObject().getThisInt(Constants.SP_STORMY);
				int phasesLost2 = current.tile.getGameObject().getThisInt(Constants.EVENT_VIOLENT_STORM);
				int phasesLost = phasesLost1+phasesLost2;
				gameHandler.broadcast(character.getGameObject().getName(),"Caught in Violent Storm!!  Loses "+phasesLost+" phases.");
				turnPanel.doLosePhases(phasesLost);
				character.setStormed(true);
			}
		
			character.addActionPerformedToday(action,getActionState(),result,roller);
			
			boolean blockEvaluation = true;
			if (hostPrefs.hasPref(Constants.FE_PHASE_END_PLAYING_COLOR_CHIT)) {
				TileLocation current = character.getCurrentLocation();
				int actionsTaken = character.getNumberOfPerformedActionsToday();
				boolean interruptionAlreadyOccured = character.getColorChitInterruptionActionCountPhaseEnd() == actionsTaken;
				character.setColorChitInterruptionActionCountPhaseEnd(actionsTaken);
				ArrayList<GameObject> livingCharacters = RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData());
				for (GameObject livingCharacter : livingCharacters) {
					if (!interruptionAlreadyOccured) new CharacterWrapper(livingCharacter).removeAllColorChitInterruptPhaseEndDecisions();
				}
				if (current.isInClearing()) {
					for (RealmComponent rc :current.clearing.getClearingComponents()) {
						if (rc.isPlayerControlledLeader()) {
							new CharacterWrapper(rc.getGameObject()).checkForColorChitInterruptionState(current,false,true);
						}
					}
				}
				gameHandler.updateCharacterFramesWithoutMap();
				ArrayList<RealmComponent> interrupters = character.getPossibleColorChitInterrupters(current,false,true);
				if (interrupters!=null && !interrupters.isEmpty()) {
					character.setNeedsBlockEvaluation(true);
					blockEvaluation = false;
					for (GameObject livingCharacter : livingCharacters) {
						new CharacterWrapper(livingCharacter).setNeedsReactDecision(false);
					}
				}
			}
			
			if (blockEvaluation) {
				for (GameObject livingCharacter : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
					if (hostPrefs.hasPref(Constants.OPT_BLOCKING_PHASES)) {
						new CharacterWrapper(livingCharacter).removeAllReactDecisions();
					}
					new CharacterWrapper(livingCharacter).setInterruptPhaseDecision(false);
				}
				gameHandler.updateCharacterFramesWithoutMap();
			}

			if (!isFollowing && blankReason == null && !invalid) {
				triggerPostPhase();
				releaseLastPhaseFollowers();
			}
		}
	}
	/**
	 * Evaluates whether a pre-phase segment is needed before the current action executes,
	 * sets the appropriate flags if so, and reports whether the action must be deferred.
	 * <p>
	 * Two independent conditions trigger a non-phasing character's pre-phase segment:
	 * <ul>
	 *   <li><b>Active followers of the phasing guide</b> with Reacting ON always qualify.
	 *       Followers may use the pre-phase segment to rearrange belongings, trade with anyone in
	 *       the clearing, play color chits, pick up mission chits, or choose to stop following —
	 *       all of these are available every phase, so followers are unconditionally eligible.</li>
	 *   <li><b>Non-followers</b> with Reacting ON who hold color chits qualify only in
	 *       3rd-edition mode ({@code FE_PHASE_END_PLAYING_COLOR_CHIT} absent). In 1st-edition, color
	 *       chit play shifts to post-phase, leaving non-followers nothing to do pre-phase.</li>
	 * </ul>
	 * When any qualifying character exists and the segment has not already fired for this action
	 * (action-count guard via {@code getPrePhaseActivityActionCount()}), the phasing character's
	 * {@code NeedsPrePhaseActivityDecision} flag is set and
	 * {@code updateCharacterFramesWithoutMap()} is called so the CharacterFrame can display the
	 * "Done: Pre-Phase" button. The method then scans the clearing a second time: if any
	 * player-controlled leader still has the flag set (including flags set by earlier calls),
	 * it returns {@code true} so the caller defers the action.
	 *
	 * @param hostPrefs game-wide host preferences; consulted to determine whether pre-phase
	 *                  color-chit play is active ({@code FE_PHASE_END_PLAYING_COLOR_CHIT} absent
	 *                  = 3rd-edition default = color chits trigger pre-phase)
	 * @return {@code true} if any player-controlled leader in the current clearing has an
	 *         unresolved pre-phase decision and the action must not yet execute;
	 *         {@code false} if the action may proceed
	 */

	// PRE-PHASE SEGMENT: handlePrePhase() evaluates whether any non-phasing character in the
	// clearing qualifies for pre-phase activities. There is a gate, and if passed, two 
	// independent triggers.
	//
	//   GATE: Reacting=ON is a hard requirement for any non-phasing character pre-phase activity.
	//
	//   Subsequent qualification for a pre-phase segment is determined by two independent triggers:
	//
	//     (A) Followers of a phasing guide always qualify.  Followers can use the pre-phase segment
	//         to: rearrange belongings, trade with anyone in the clearing, play color chits, pick up
	//         mission chits, or choose to stop following.
	//
	//     (B) Non-followers with Reacting ON who hold color chits — qualify only in
	//         3rd-edition mode (FE_PHASE_END_PLAYING_COLOR_CHIT is off). In 1st-edition, color chit
	//         play shifts to post-phase, so non-followers have nothing to do pre-phase unless they
	//         are followers of this guide (see A).
	//
	// Phasing characters always qualify for pre-phase activities: trading, rearranging items, color
	// chit play, and mission chit pickup.  There is no special dialog needed for phasing characters
	// to do these things, but if any non-phasing character qualifies for pre-phase activities, the
	// phasing character must resolve their pre-phase segment before the non-phasing characters will
	// be presented with their pre-phase activies dialog.
	//
	// Phasing guides who are hidden may drop followers who cannot detect them during the pre-phase if
	// the phase action will be a move.
	//
	// If any qualifying character exists and the segment hasn't fired for this action yet,
	// NeedsPrePhaseActivityDecision is set on the phasing character and we return true here,

	private boolean handlePrePhase(HostPrefWrapper hostPrefs) {
		if (character.isMinion()) return false;
		TileLocation loc = character.getCurrentLocation();
		if (loc == null || !loc.isInClearing()) return false;

		int actionsTaken = character.getNumberOfPerformedActionsToday();
		int currentStamp = character.getPrePhaseActivityActionCount();
		boolean alreadyOccurred = currentStamp == actionsTaken;
		// TBD(6): Remove all [IPD] triage logging (System.err.println("[IPD]...")) before shipping.
		// There are 36 occurrences across ActionRow, CharacterFrame, and RealmTurnPanel.
		// grep -rn 'System.err.println.*\[IPD\]' magic_realm/
		System.err.println("[IPD] handlePrePhase ENTER: phasingChar=" + character.getGameObject().getName()
			+ " actionsTaken=" + actionsTaken + " stamp=" + currentStamp + " alreadyOccurred=" + alreadyOccurred);
		if (!alreadyOccurred) {
			// true when pre-phase color chit play is enabled: 3rd edition (FE_PHASE_END_PLAYING_COLOR_CHIT OFF)
			// AND the host option OPT_PHASE_BEGIN_PLAYING_COLOR_CHIT is checked.
			boolean prePhaseColorChitPlay = !hostPrefs.hasPref(Constants.FE_PHASE_END_PLAYING_COLOR_CHIT)
				&& hostPrefs.hasPref(Constants.OPT_PHASE_BEGIN_PLAYING_COLOR_CHIT);
			ArrayList<CharacterWrapper> phasingFollowers = character.getActionFollowers();
			// Track separately: chars who can receive their flag immediately (combined case) vs those who
			// still need to wait for the phasing char's "Done: Pre-Phase" acknowledgement.
			boolean anyNeedsDoneButton = false;
			boolean anyCombined = false;
			for (RealmComponent rc : loc.clearing.getClearingComponents()) {
				if (rc.getGameObject().equals(character.getGameObject())) continue;
				CharacterWrapper cw = new CharacterWrapper(rc.getGameObject());
				if (!rc.isPlayerControlledLeader() && !cw.isMinion()) continue;
				boolean isReacting = cw.isReacting();
				boolean isFollower = phasingFollowers.stream().anyMatch(f -> f.getGameObject().equals(rc.getGameObject()));
				boolean canPlayColorChits = !cw.getColorMagicChits().isEmpty() && prePhaseColorChitPlay;
				System.err.println("[IPD]   checking char=" + cw.getGameObject().getName()
					+ " isReacting=" + isReacting + " isFollower=" + isFollower + " canColorChits=" + canPlayColorChits
					+ " preFlag=" + cw.getNeedsPrePhaseActivityDecision()
					+ " postFlag=" + cw.getNeedsPostPhaseActivityDecision()
					+ " cwStamp=" + cw.getPrePhaseActivityActionCount());
				// Followers always qualify (stop-following is independent of reactions).
				// Non-followers qualify only when reactions are ON and they hold color chits.
				if (isFollower || (isReacting && canPlayColorChits)) {
					if (cw.getNeedsPostPhaseActivityDecision()) {
						// Post-phase still outstanding — set the pre-phase flag directly so CharacterFrame
						// sees both flags simultaneously and shows the combined dialog.
						System.err.println("[IPD]     -> COMBINED case for " + cw.getGameObject().getName());
						cw.setNeedsPrePhaseActivityDecision(true);
						anyCombined = true;
					} else {
						// No outstanding post-phase — must wait for phasing char's "Done: Pre-Phase".
						// Skip chars already stamped for this action (handled via combined/deferred path).
						if (cw.getPrePhaseActivityActionCount() != actionsTaken) {
							System.err.println("[IPD]     -> DONE-BUTTON case for " + cw.getGameObject().getName());
							anyNeedsDoneButton = true;
						} else {
							System.err.println("[IPD]     -> DONE-BUTTON skipped (already stamped) for " + cw.getGameObject().getName());
						}
					}
				}
			}
			System.err.println("[IPD]   result: anyCombined=" + anyCombined + " anyNeedsDoneButton=" + anyNeedsDoneButton);
			if (anyCombined || anyNeedsDoneButton) {
				character.setPrePhaseActivityActionCount(actionsTaken);
				if (anyNeedsDoneButton) {
					// Phasing char's Done button is only needed when some non-phasing chars don't have
					// the combined dialog (their pre-phase flag will be set from doPrePhaseActivities()).
					System.err.println("[IPD]   SETTING Done:Pre-Phase on " + character.getGameObject().getName());
					character.setNeedsPrePhaseActivityDecision(true);
				}
				gameHandler.updateCharacterFramesWithoutMap();
			}
		}
		for (RealmComponent rc : loc.clearing.getClearingComponents()) {
			if (rc.isPlayerControlledLeader() && new CharacterWrapper(rc.getGameObject()).getNeedsPrePhaseActivityDecision()) {
				System.err.println("[IPD] handlePrePhase RETURN TRUE (waiting on "
					+ rc.getGameObject().getName() + ")");
				return true;
			}
		}
		System.err.println("[IPD] handlePrePhase RETURN FALSE (action may proceed)");
		return false;
	}

	/**
	 * Reports whether post-phase dialogs from the previous action are still outstanding.
	 * <p>
	 * The action-count match ({@code postPhaseActionCount == actionsPerformedToday}) identifies
	 * the pending window: the count is stamped after the triggering action completes and therefore
	 * equals the current pre-action total only while that action's post-phase is unresolved. Once
	 * all participants dismiss their dialogs and their flags clear, this method returns
	 * {@code false} and the action may proceed.
	 * <p>
	 * Only the phasing character's current clearing is scanned. Post-phase activity from an
	 * earlier clearing is irrelevant once the character has moved on — those participants were
	 * already notified and resolved their dialogs before the character could move.
	 *
	 * @return {@code true} if any player-controlled leader in the current clearing still has an
	 *         unresolved post-phase decision stamped at the current action count, meaning the
	 *         next action must not yet execute; {@code false} if the action may proceed
	 */
	private boolean isPostPhasePending() {
		TileLocation loc = character.getCurrentLocation();
		if (loc == null || !loc.isInClearing()) return false;
		if (character.getPostPhaseActivityActionCount() != character.getNumberOfPerformedActionsToday()) return false;
		for (RealmComponent rc : loc.clearing.getClearingComponents()) {
			if (rc.isPlayerControlledLeader() && new CharacterWrapper(rc.getGameObject()).getNeedsPostPhaseActivityDecision()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Releases all active followers at the end of the phasing character's last scheduled action.
	 * <p>
	 * Called only when {@code actionsPerformedToday == currentActionCount} (the final action of
	 * the day). Every active follower is stopped in place and removed from the phasing
	 * character's follower list. The followers remain in the same clearing as free individuals
	 * but do not participate in post-phase dialogs — {@link #triggerPostPhase()} is called
	 * before this method so the follower list is still intact when the clearing is scanned,
	 * ensuring released followers are excluded.
	 * <p>
	 * {@link CharacterWrapper#setStopFollowing(boolean)} is called <em>before</em>
	 * {@link CharacterWrapper#removeActionFollower} so the follower's flag is already set when
	 * the monster-summon logic inside {@code removeActionFollower} runs, preserving the standard
	 * stopped-follower summoning behavior. The snapshot returned by
	 * {@link CharacterWrapper#getActionFollowers()} is safe to iterate while the underlying list
	 * is mutated by each {@code removeActionFollower} call.
	 */
	private void splitMultiPhaseAction() {
		int commaIdx = action.indexOf(',');
		String firstPhase = action.substring(0, commaIdx);
		String restPhases = action.substring(commaIdx + 1);
		action = firstPhase;
		isFirstPhaseOfMultiPhaseMove = true;
		ActionRow continuation = new ActionRow(turnPanel, character, restPhases, actionTypeCode, isFollowing);
		continuation.isContinuation = true;
		continuation.location = this.location;
		newAction = continuation;
	}

	private void releaseLastPhaseFollowers() {
		if (newAction != null) return; // continuation pending — don't release until the final phase
		if (turnPanel.hasPendingActionsAfterCurrent()) return;
		for (CharacterWrapper follower : character.getActionFollowers()) {
			follower.setStopFollowing(true);
			character.removeActionFollower(follower, gameHandler.getGame().getMonsterDie(), gameHandler.getGame().getNativeDie());
		}
	}

	/**
	 * Notifies qualifying individuals in the phasing character's current clearing that
	 * post-phase activities are available after the action just completed.
	 * <p>
	 * This is called <em>before</em> {@link #releaseLastPhaseFollowers()}, so
	 * {@code character.getActionFollowers()} still contains any followers who are about to be
	 * released at end-of-turn.
	 * <p>
	 * In 3rd-edition case ({@code FE_PHASE_END_PLAYING_COLOR_CHIT} OFF), followers of <em>any</em>
	 * guide are excluded from post-phase — they have no independent post-phase reactions while
	 * following (pre-phase reactions via {@code isReacting()} still apply normally).
	 * Followers who voluntarily stopped during pre-phase are already off every guide's follower
	 * list and are eligible as independent individuals.
	 * <p>
	 * In 1st-edition case ({@code FE_PHASE_END_PLAYING_COLOR_CHIT} ON), no characters are
	 * excluded by follower status — all characters in the clearing (including followers of any
	 * guide) who have Reactions ON and hold color chits are eligible for the phase-end
	 * color-chit dialog.
	 * <p>
	 * The clearing is scanned once to collect two things simultaneously: the list of eligible
	 * non-following player-controlled leaders, and whether any unhired, non-mist-like monsters
	 * are present. A character qualifies as a post-phase participant if all of the following
	 * are true:
	 * <ul>
	 *   <li>They are not an active follower of the phasing character.</li>
	 *   <li>They have Reacting ON ({@code isReacting()}).</li>
	 *   <li>If they are the <em>phasing</em> character: at least one other individual in the
	 *       clearing is detectable, or unhired monsters are present.</li>
	 *   <li>If they are a <em>non-phasing</em> character: they can detect the phasing character
	 *       (phasing character is not hidden, or this observer has found hidden enemies today
	 *       via {@code foundHiddenEnemy()}).</li>
	 * </ul>
	 * When at least one participant is found, the post-phase action count is stamped (preventing
	 * re-triggering via {@link #isPostPhasePending()}), each participant's
	 * {@code NeedsPostPhaseActivityDecision} flag is set, and
	 * {@code updateCharacterFramesWithoutMap()} is called so each participant's CharacterFrame
	 * auto-shows the dialog via {@code SwingUtilities.invokeLater}.
	 */
	private void triggerPostPhase() {
		if (character.isMinion()) return;
		TileLocation loc = character.getCurrentLocation();
		if (loc == null || !loc.isInClearing()) return;

		int actionsTakenNow = character.getNumberOfPerformedActionsToday();
		if (character.getPostPhaseActivityActionCount() == actionsTakenNow) return;

		// Build the follower exclusion set.
		//
		// In 3rd-edition case (FE_PHASE_END_PLAYING_COLOR_CHIT OFF), followers of ANY guide are
		// excluded from post-phase entirely — they are operating under their guide's turn and have
		// no independent inter-phase reactions until they stop following.
		//
		// In 1st-edition case (FE_PHASE_END_PLAYING_COLOR_CHIT ON), followers may play color chits
		// at phase-end, so we restrict exclusion only to the phasing character's active followers
		// (same rule as before this change — voluntarily-stopped followers are already off the list).
		//
		// willBeBlocked() skips hidden characters, so monster presence is checked directly —
		// a hidden character can still elect to react to monsters even though monsters won't block them.
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		boolean colorChitPostPhase = hostPrefs.hasPref(Constants.FE_PHASE_END_PLAYING_COLOR_CHIT);

		Set<GameObject> excludedFollowers = new HashSet<>();
		if (!colorChitPostPhase) {
			// 3rd edition: exclude followers of ANY guide — they have no independent post-phase reactions.
			for (GameObject living : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
				for (CharacterWrapper f : new CharacterWrapper(living).getActionFollowers()) {
					excludedFollowers.add(f.getGameObject());
				}
			}
		}
		// TBD(10): 1st-edition (FE_PHASE_END_PLAYING_COLOR_CHIT ON) following and color chit play
		// in interphase dialogs are not fully implemented.
		//   - Follower exclusion: currently NO followers are excluded, so a follower of any guide
		//     can receive a post-phase color chit dialog. The original master behavior excluded only
		//     the phasing character's active followers; that nuance is not yet replicated here.
		//   - Combined pre+post dialog: canPlayColorChits (below) is false in 1st-edition mode, so
		//     1st-edition followers who qualify for post-phase never get the eager pre-phase stamp —
		//     they receive two sequential dialogs instead of one combined dialog.
		//   - The !isFollowing guard in process() may additionally prevent followers from ever
		//     reaching triggerPostPhase(), suppressing the dialog entirely.
		// All three issues require coordinated testing with FE_PHASE_END_PLAYING_COLOR_CHIT enabled.

		ArrayList<CharacterWrapper> nonFollowers = new ArrayList<>();
		boolean monstersPresent = false;
		for (RealmComponent rc : loc.clearing.getClearingComponents()) {
			if (rc.isPlayerControlledLeader()) {
				CharacterWrapper cw = new CharacterWrapper(rc.getGameObject());
				if (!excludedFollowers.contains(cw.getGameObject())) {
					nonFollowers.add(cw);
				}
			} else if (rc instanceof MonsterChitComponent && rc.getOwner() == null && !rc.isMistLike()) {
				monstersPresent = true;
			}
		}

		boolean phasingCharHidden = character.isHidden();
		boolean smallHouseRule = hostPrefs.hasPref(Constants.HOUSE3_SMALL_MONSTERS);
		// Whether the phasing character can legally be blocked (blockee guards).
		boolean phasingCharBlockable = !character.isMistLike()
			&& !character.isSleep()
			&& !character.getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING)
			&& !character.getGameObject().hasThisAttribute(Constants.BLINDING_LIGHT)
			&& !(character.isSmall() && smallHouseRule);

		// Count others that the phasing char could actually block — mirrors isValidBlockTarget() guards.
		// Misted chars are detectable but not blockable, so counting them would cause a blank dialog.
		boolean phasingIgnoresMist = character.getGameObject().hasThisAttribute(Constants.IGNORE_MIST_LIKE);
		int detectableOthers = 0;
		for (CharacterWrapper other : nonFollowers) {
			if (other.getGameObject().equals(character.getGameObject())) continue;
			if (other.isMistLike() && !phasingIgnoresMist) continue;
			if (other.isSleep() || other.isBlocked()) continue;
			if (other.getGameObject().hasThisAttribute(Constants.BLINDING_LIGHT)) continue;
			if (other.getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING)) continue;
			if (other.isSmall() && smallHouseRule) continue;
			if (!other.isHidden() || character.foundHiddenEnemy(other.getGameObject())) {
				detectableOthers++;
			}
		}

		ArrayList<CharacterWrapper> postPhaseParticipants = new ArrayList<>();
		for (CharacterWrapper cw : nonFollowers) {
			if (!cw.isReacting()) continue;
			boolean isPhasingChar = cw.getGameObject().equals(character.getGameObject());
			if (isPhasingChar) {
				// Phasing char qualifies only if they can also block (blocker guards).
				boolean phasingCanBlock = !cw.isMistLike() && !cw.isSleep()
					&& !cw.getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING)
					&& !cw.isMinion()
					&& !(cw.isSmall() && smallHouseRule);
				if (phasingCanBlock && (detectableOthers >= 1 || monstersPresent)) {
					postPhaseParticipants.add(cw);
				}
			} else {
				boolean canDetectPhasing = !phasingCharHidden || cw.foundHiddenEnemy(character.getGameObject());
				boolean blockerCanBlock = !cw.isMistLike() && !cw.isSleep()
					&& !cw.getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING)
					&& !cw.isMinion()
					&& !(cw.isSmall() && smallHouseRule);
				boolean ignoreMist = cw.getGameObject().hasThisAttribute(Constants.IGNORE_MIST_LIKE);
				boolean canBlockPhasing = canDetectPhasing && blockerCanBlock
					&& (phasingCharBlockable || ignoreMist);
				boolean hasColorChitsForPostPhase = colorChitPostPhase && !cw.getColorMagicChits().isEmpty();
				if (canBlockPhasing || hasColorChitsForPostPhase) {
					postPhaseParticipants.add(cw);
				}
			}
		}

		System.err.println("[IPD] triggerPostPhase: phasingChar=" + character.getGameObject().getName()
			+ " actionsTakenNow=" + actionsTakenNow
			+ " postPhaseParticipants=" + postPhaseParticipants.size());
		if (!postPhaseParticipants.isEmpty()) {
			character.setPostPhaseActivityActionCount(actionsTakenNow);

			// Eagerly check if a pre-phase will immediately follow. If the phasing character has
			// more actions queued and a non-phasing post-phase participant also qualifies for
			// pre-phase (follower OR holds color chits in 3rd-edition mode), set both flags at
			// once so CharacterFrame shows a single combined dialog instead of two sequential ones.
			boolean nextActionExists = (newAction != null) || turnPanel.hasPendingActionsAfterCurrent();
			System.err.println("[IPD]   nextActionExists=" + nextActionExists);
			ArrayList<CharacterWrapper> phasingFollowers = character.getActionFollowers();
			for (CharacterWrapper cw : postPhaseParticipants) {
				System.err.println("[IPD]   participant=" + cw.getGameObject().getName()
					+ " isPhasing=" + cw.getGameObject().equals(character.getGameObject()));
				cw.setNeedsPostPhaseActivityDecision(true);
				if (nextActionExists && !cw.getGameObject().equals(character.getGameObject())) {
					boolean isFollower = phasingFollowers.stream()
						.anyMatch(f -> f.getGameObject().equals(cw.getGameObject()));
					boolean canPlayColorChits = !cw.getColorMagicChits().isEmpty() && !colorChitPostPhase;
					if (cw.isReacting() && (isFollower || canPlayColorChits)) {
						cw.setNeedsPrePhaseActivityDecision(true);
						// Stamp so doPrePhaseActivities() knows this char's pre-phase for this action
						// is already handled via the combined dialog and must not be re-triggered.
						cw.setPrePhaseActivityActionCount(actionsTakenNow);
						System.err.println("[IPD]   eagerly set PRE flag on " + cw.getGameObject().getName()
							+ " with stamp=" + actionsTakenNow + " isFollower=" + isFollower + " canColorChits=" + canPlayColorChits);
					}
				}
			}
			gameHandler.updateCharacterFramesWithoutMap();
		}
	}

	public void updateBlocked(HostPrefWrapper hostPrefs) {
		if (!character.isBlocked() && RealmUtility.willBeBlocked(character,isFollowing,true)) {
			character.setBlocked(true);
		}
		if (hostPrefs.hasPref(Constants.SR_NATIVE_BLOCKING) && !character.isBlocked()) {
			ArrayList<RealmComponent> natives = RealmUtility.willBeBlockedByNatives(character,isFollowing);
			
			HashMap<String,Integer> groups = new HashMap<String,Integer>();
			HashMap<String,RealmComponent> groupLeaders = new HashMap<String,RealmComponent>();
			if (natives!=null && !natives.isEmpty()) {
				for (RealmComponent denizen : natives) {
					String group = RealmUtility.getRelationshipGroupName(denizen.getGameObject());
					boolean unfriendlyOrEnemy = false;
					boolean rovingNative = denizen.getGameObject().hasThisAttribute(Constants.ROVING_NATIVE);
					int relationship = character.getRelationship(RealmUtility.getRelationshipBlockFor(denizen.getGameObject()),group,rovingNative);
					if (relationship < RelationshipType.NEUTRAL) {
						unfriendlyOrEnemy = true;
					}
					if (!groups.containsKey(group) && unfriendlyOrEnemy) {
						groups.put(group, relationship);
					}
					if (unfriendlyOrEnemy) {
						if (!groupLeaders.containsKey(group)) {
							groupLeaders.put(group, denizen);
						}
						else {
							String rankStringLeader = (groupLeaders.get(group)).getGameObject().getThisAttribute("rank");
							int rankLeader = "HQ".equals(rankStringLeader)?Integer.valueOf(0):Integer.parseInt(rankStringLeader);
							String rankStringDenizen = denizen.getGameObject().getThisAttribute("rank");
							int rankDenizen = "HQ".equals(rankStringDenizen)?Integer.valueOf(0):Integer.parseInt(rankStringDenizen);					
							if (rankDenizen < rankLeader) {
								groupLeaders.put(group, denizen);						
							}
						}
					}
				}
			}
			
			if (!groups.isEmpty()) {
				for (String group : groups.keySet()) {
					ActionRow newAction = new ActionRow(turnPanel,character,Meeting.createMeetingTable(
							gameHandler.getMainFrame(),
							character,
							character.getCurrentLocation(),
							groupLeaders.get(group),
							null,
							null,
							groups.get(group)),isFollowing);
					newAction.handleTable(false);
				}
			}
		}
	}
	public boolean willHavePhaseEndUpdates() {
		boolean sleepable = !character.getFatiguedChits().isEmpty() || !character.getWoundedChits().isEmpty();
		return willBeAffectedByStorm()
			|| (TreasureUtility.getSleepObject(character.getCurrentLocation())!=null && sleepable)
			|| RealmUtility.willBeBlocked(character,isFollowing,false);
	}
	public boolean willBeAffectedByStorm() {
		if (!character.getStormed()) {
			TileLocation current = character.getCurrentLocation();
			if (current.isInClearing()
					&& (current.tile.getGameObject().hasThisAttribute(Constants.SP_STORMY) || current.tile.getGameObject().hasThisAttribute(Constants.EVENT_VIOLENT_STORM))
					&& !current.clearing.isCave()
					&& !current.clearing.holdsDwellingWithShelter()) {
				// Ended a phase in a stormy clearing, and haven't been affected yet, so...
				return true;
			}
		}
		return false;
	}
	public void logAction() {
		if (completed) {
			DayAction da = CharacterWrapper.getActionForString(action);
			String actionName = da==null?"":da.getName();			
			gameHandler.broadcast(character.getGameObject().getName(),actionName+" - "+getNonsecretKey(result));
			
			// Now that the non-secret portion has been logged, we can convert the result fully to the secret key.
			result = getSecretKey(result);
		}
	}
	/**
	 * The string coming in may have a secret key in it that looks like:
	 * 
	 *   ##Treasure|Deft Gloves##
	 * 
	 * See Loot.characterFindsItem
	 */
	private static String[] breakOutKeys(String in) {
		String[] ret = null;
		if (in==null) return null;
		int start = in.indexOf("##");
		if (start>=0) {
			int end = in.indexOf("##",start+1);
			if (end>=0) {
				int mid = in.indexOf('|');
				if (mid>=0 && mid>start && mid<end) {
					ret = new String[2];
					String front = in.substring(0,start);
					String back = in.substring(end+2);
					ret[0] = front + in.substring(start+2,mid) + back;
					ret[1] = front + in.substring(mid+1,end) + back;
				}
			}
		}
		return ret;
	}
	/**
	 * This is the secret portion of the string.  In the example above, this would return "Deft Gloves".
	 */
	private static String getSecretKey(String in) {
		String[] ret = breakOutKeys(in);
		if (ret!=null) {
			return ret[1];
		}
		return in;
	}
	/**
	 * This is the nonsecret portion of the string.  In the example above, this would return "Treasure".
	 */
	private static String getNonsecretKey(String in) {
		String[] ret = breakOutKeys(in);
		if (ret!=null) {
			return ret[0];
		}
		return in;
	}
	public void checkSleep() {
		// Find other characters in the clearing, and put them to sleep too
		TileLocation tl = character.getCurrentLocation();
		if (tl!=null && tl.isInClearing()) {
			for (CharacterWrapper testCharacter:ClearingUtility.getCharactersInClearing(tl)) {
				checkSleep(testCharacter);
			}
		}
	}
	private void checkSleep(CharacterWrapper testCharacter) {
		if (!testCharacter.isSleep()) {
			GameObject sleepObject = getSleepObject(testCharacter);
			if (sleepObject!=null) {
				RealmComponent rc = RealmComponent.getRealmComponent(sleepObject);
				if (rc.isTreasure()) {
					TreasureCardComponent treasure = (TreasureCardComponent)rc;
					if (!treasure.isFaceUp()) {
						treasure.setFaceUp();
					}
				}
				testCharacter.setSleep(true);
				if (testCharacter.isFollowingCharacterPlayingTurn()) {
					character.removeActionFollower(testCharacter,null,null);
					JOptionPane.showMessageDialog(
							gameHandler.getMainFrame(),
							"The "+testCharacter.getGameObject().getName()+" has fallen asleep.",
							sleepObject.getName()+" in clearing",JOptionPane.INFORMATION_MESSAGE,rc.getIcon());
				}
				
				int order = testCharacter.getPlayOrder();
				if (order<2) { // Report sleep effect if testCharacter is current turn, or played turn (NOT future turns!)
					JOptionPane.showMessageDialog(
							gameHandler.getMainFrame(),
							"The "+testCharacter.getGameObject().getName()+" has fallen asleep.",
							sleepObject.getName()+" in clearing",JOptionPane.INFORMATION_MESSAGE,rc.getIcon());
				}
				RealmLogging.logMessage(
						testCharacter.getGameObject().getName(),
						"Has fallen asleep due to the presence of the "+sleepObject.getName()+".");
			}
		}
	}
	private static GameObject getSleepObject(CharacterWrapper testCharacter) {
		TileLocation current = testCharacter.getCurrentLocation();
		if (current.isInClearing()) {
			// First check to see if character has fatigued chits, and can rest
			if (testCharacter.getFatiguedChits().size()>0 && !testCharacter.hasCurse(Constants.WITHER)) {
				// Now see if there are any sleep treasures
				return TreasureUtility.getSleepObject(current);
			}
		}
		return null;
	}
	public DieRoller getRoller() {
		return roller;
	}
	public void setRoller(DieRoller roller) {
		this.roller = roller;
	}
	
	private void doHideAction() {
		if (!character.isHidden()) {
			TileLocation location = character.getCurrentLocation();
			GameObject noHideItem = ClearingUtility.getItemInClearingWithKey(location,Constants.NO_HIDE);
			if (character.hasCurse(Constants.SQUEAK)) {
				result = "Failed due to SQUEAK curse";
			}
			else if (noHideItem!=null) {
				result = "Failed due to the "+noHideItem.getName();
			}
			else {
				RealmCalendar cal = RealmCalendar.getCalendar(gameHandler.getClient().getGameData());
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
				boolean canHide = !cal.isHideDisabled(character.getCurrentMonth());
				if (!canHide && hostPrefs.hasPref(Constants.HOUSE3_SNOW_HIDE_EXCLUDE_CAVES) && location.isInClearing() && location.clearing.isCave()) {
					canHide = true;
				}
				
				if (canHide) {
					roller = DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createHideRoller();
					if (roller.getHighDieResult() < 6) {
						result = "Succeeded";
						character.setHidden(true);
						for (CharacterWrapper follower : character.getActionFollowers()) {
							if (!follower.hasCurse(Constants.SQUEAK)) {
								follower.setHidden(true);
							}
						}
					}
					else {
						result = "Failed";
						if (character.hasLuck()) {
							int ret = JOptionPane.showConfirmDialog(
									gameHandler.getMainFrame(),
									"Do you want to re-roll this Hide roll?",
									"Luck",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.INFORMATION_MESSAGE);
							if (ret==JOptionPane.YES_OPTION) {
								character.removeLuck(gameHandler.getMainFrame());
								doHideAction();
								return;
							}
						}
					}
					
					QuestRequirementParams params = new QuestRequirementParams();
					params.actionType = CharacterActionType.Hide;
					character.testQuestRequirements(gameHandler.getMainFrame(),params);
				}
				else {
					result = "HIDE table is disabled, due to inclement weather.";
				}
			}
		}
		else {
			result = "N/A";
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Hide;
			character.testQuestRequirements(gameHandler.getMainFrame(),params);
		}
	}
	private void doFortifyAction() {
		if (!character.isFortified()) {
			roller = DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createFortifyRoller();
			if (roller.getHighDieResult() < 6) {
				result = "Succeeded";
				character.setFortified(true);
			}
			else {
				result = "Failed";
			}
		}
		else {
			result = "N/A";
		}
		
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.Fortify;
		character.testQuestRequirements(gameHandler.getMainFrame(),params);
	}
	private void doMoveAction() {
		doMoveAction(false);
	}
	private void doOffroadAction() {
		TileLocation current = character.getCurrentLocation();
		if (current.tile.getGameObject().hasThisAttribute(Constants.NO_OFFROAD_TRAVEL)) {
			result = "Cannot offroad travel in this tile.";
			completed = true;
			return;
		}
		
		character.checkForLostInTheMaze(current); // Lost in the Maze rule for Super Realm
		
		// Before starting, make sure that you aren't "lost in the maze" (expansion 1)
		if ((character.isCharacter() || character.isHiredLeader()) && !character.isMinion()) {
			RealmComponent discoverToLeave = ClearingUtility.findDiscoverToLeaveComponent(current,character);
			if (discoverToLeave!=null && CharacterWrapper.getIdForAction(character.getLastPerformedActionToday()) != ActionId.Move) {
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"You are trapped in the "+discoverToLeave.getGameObject().getName()+"! MOVE is cancelled.",
						"Trapped!",JOptionPane.PLAIN_MESSAGE,discoverToLeave.getFaceUpIcon());
				cancelled = true;
				return;
			}
		}
		
		if (character.isTransmorphed()) {
			if (RealmComponent.getRealmComponent(character.getTransmorph()).getWeight().isMaximum()) {
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"Your transmorphed form cannot move.");
				cancelled = true;
				return;
			}
		}
		
		result = "";
		character.removeOffroadTravelClearing();
		realmTable = new OffroadTravel(gameHandler.getMainFrame(),current);
		handleTable();
		if (character.getOffroadTravelClearing()!=0) {
			location = new TileLocation(current.tile.getClearing(character.getOffroadTravelClearing()));
		} else {
			completed = true;
			return;
		}
		character.removeOffroadTravelClearing();
		
		doMoveAction(true);
	}
	private void doMoveAction(boolean offroadTravel) {
		TileLocation current = character.getCurrentLocation();
		
		character.checkForLostInTheMaze(current); // Lost in the Maze rule for Super Realm
		
		// Before starting, make sure that you aren't "lost in the maze" (expansion 1)
		if ((character.isCharacter() || character.isHiredLeader()) && !character.isMinion()) {
			RealmComponent discoverToLeave = ClearingUtility.findDiscoverToLeaveComponent(current,character);
			if (discoverToLeave!=null && CharacterWrapper.getIdForAction(character.getLastPerformedActionToday()) != ActionId.Move) {
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"You are trapped in the "+discoverToLeave.getGameObject().getName()+"! MOVE is cancelled.",
						"Trapped!",JOptionPane.PLAIN_MESSAGE,discoverToLeave.getFaceUpIcon());
				cancelled = true;
				return;
			}
		}
		
		if (character.isTransmorphed()) {
			if (RealmComponent.getRealmComponent(character.getTransmorph()).getWeight().isMaximum()) {
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"Your transmorphed form cannot move.");
				cancelled = true;
				return;
			}
		}
		
		if (!offroadTravel) {
			result = "";
		}
		if (character.moveRandomly() && !current.isBetweenClearings()) {
			// Pick a random location
			DieRoller roller = new DieRoller();
			roller.adjustDieSize(25, 6);
			roller.addRedDie();
			roller.rollDice("Random Clearing");
			int c = roller.getTotal();
			
			// Find all clearings that match the number
			ArrayList<ClearingDetail> clearings = new ArrayList<>();
			for (PathDetail path : current.clearing.getConnectedPaths()) {
				ClearingDetail clearing = path.findConnection(current.clearing);
				if (clearing!=null) {
					if (clearing.getNum()==c) {
						if (!clearing.connectionHasThorns(current)) {
							clearings.add(clearing);
						}
					}
				}
			}
			
			// If none, cancel move action
			if (clearings.isEmpty()) {
				cancelled = false;
				completed = true;
				result = "Random move to clearing "+c+" is invalid!";
				return;
			}
			result = "Random move to clearing "+c+": ";
			
			if (clearings.size()==1) {
				// If one, do move action
				location = new TileLocation(clearings.get(0));
			}
			else {
				// If more than one, let player choose
				CenteredMapView.getSingleton().setMarkClearingAlertText("Random move to clearing "+c+": pick one!");
				CenteredMapView.getSingleton().markClearings(clearings,true);
				TileLocationChooser chooser = new TileLocationChooser(gameHandler.getMainFrame(),CenteredMapView.getSingleton(),current);
				chooser.setVisible(true);
				
				// Update the location
				CenteredMapView.getSingleton().markClearings(clearings,false);
				location = chooser.getSelectedLocation();
			}
		}
		
		// TBD(11): A character who is between clearings (TileLocation.isBetweenClearings()) can
		// record an illegal move if the target TileLocation has a null clearing — the recording-time
		// guard in CharacterActionControlManager only fires when tl.hasClearing() is true, so a
		// null-clearing target silently bypasses it. At execution time, every dereference of
		// location.clearing below (correctSide, getConnectingPath, canWalkWoods, moveCost, etc.)
		// will NPE because the outer guard only checks location != null, not location.isInClearing().
		// The same exposure exists on game load: RealmTurnPanel.initActionRow() decodes the action
		// string via ClearingUtility.deduceLocationFromAction() with no post-decode validation, so
		// a save file with a between-clearings character and a bad planned move will crash here.
		// Fix: add isInClearing() guard here and in getDescription(), and re-validate on load.

		// Player is moved to clearing
		if (location != null) {
			// clearing might NOT be on the same side, if a tile flipped somewhere, so update it here
			location.clearing = location.clearing.correctSide();
			
			// Validate that the player CAN move along the path (if discovery was needed)
			PathDetail path = current.hasClearing()
					? current.clearing.getConnectingPath(location.clearing)
					: null;
					
			boolean overridePath = false;
			boolean magicPath = false;
			boolean gates = false;
			boolean pathfinder = false;
			
			boolean validMove = true;
			
			if (!offroadTravel) {
				if (character.affectedByKey(Constants.MAGIC_PATH_EFFECT)) {
					magicPath = path == null && current.tile == location.tile?true:false;
					if (magicPath) {
						overridePath = true;
					}
				}
				
				if (character.canWalkWoods(current.tile,current.clearing,location.clearing) || (current.isTileOnly() && !current.isFlying())) {
					ArrayList<ClearingDetail> validClearings = new ArrayList<>();
					if (current.clearing!=null) {
						validClearings.addAll(current.clearing.getParent().getClearings());
					}
					else if (current.tile.equals(location.tile)) {
						validClearings.addAll(location.clearing.getParent().getClearings());
					}
					if (current.isBetweenClearings()) {
						validClearings.addAll(current.getOther().tile.getClearings());
					}
					if (validClearings.contains(location.clearing)) {
						overridePath = true;
					}
				}
				
				if (current.isBetweenClearings()) {
					validMove = current.clearing.equals(location.clearing) || current.getOther().clearing.equals(location.clearing);
					if(current.clearing.connectionHasThorns(current.getOther())) validMove = false;
				}
				
				if (!overridePath && path==null) {
					overridePath = ClearingUtility.canUseGates(character,location.clearing);
					gates = true;
				}
				
				if (path!=null && path.isHidden() && character.hasActiveInventoryThisKey(Constants.PATHFINDER)) {
					overridePath = true;
					pathfinder = true;
				}
			}
			
			if (validMove && (overridePath || magicPath || current.isBetweenClearings() || path!=null || offroadTravel)) {
				if (overridePath || offroadTravel || magicPath || current.isBetweenClearings() || character.validPath(path)) {					
					// Make sure that if the character is moving into a mountain clearing, check current clearing
					// to make sure monsters don't block the first half of that move
					if ((location.clearing.moveCost(character,current)>1 || offroadTravel) && RealmUtility.willBeBlocked(character,isFollowing,true)) {
						character.setBlocked(true);
						cancelled = true;
						result = "BLOCKED";
						return;
					}
					// Move followers - FIXME Not totally right... but close!
					ArrayList<CharacterWrapper> actionFollowers = character.getActionFollowers();
					
					if (actionFollowers.size()>0) {
						ArrayList<CharacterWrapper> canLeaveBehind = new ArrayList<>();
						ArrayList<CharacterWrapper> encumberedFollowers = new ArrayList<>();
						for (CharacterWrapper follower : actionFollowers) {
							if (!follower.foundHiddenEnemy(character.getGameObject())) {
								canLeaveBehind.add(follower);
							}
							if (!follower.canFollow()) {
								encumberedFollowers.add(follower);
							}
						}
						if (!encumberedFollowers.isEmpty()) {
							StringBuffer message = new StringBuffer();
							for (Iterator<CharacterWrapper> i=encumberedFollowers.iterator();i.hasNext();) {
								CharacterWrapper follower = i.next();
								if (message.length()>0) {
									if (i.hasNext()) {
										message.append(", ");
									}
									else {
										message.append(" and ");
									}
									message.append("the ");
								}
								else {
									message.append("The ");
								}
								message.append(follower.getGameObject().getName());
							}
							message.append(encumberedFollowers.size()==1?" is":" are");
							message.append(" encumbered\nand will be left behind.  Move anyway?");
							int ret = JOptionPane.showConfirmDialog(
									gameHandler.getMainFrame(),
									message.toString(),
									"Encumbered Followers",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.INFORMATION_MESSAGE);
							if (ret==JOptionPane.NO_OPTION) {
								completed = false;
								return;
							}
						}
						// REMOVED: "Unwanted Followers?" dialog — Rule 27.6/1a gave the phasing guide a chance
						// during each Move action to leave behind followers who hadn't found hidden enemies.
						// This decision is now handled in the pre-phase activity dialog system: a hidden guide
						// with unaware followers triggers a pre-phase dialog before each action, where the
						// guide can choose to ditch followers. The mid-move popup is redundant and removed.
						//if (character.isHidden()) {
						//	int totalCanLeaveBehind = canLeaveBehind.size();
						//	if (askAboutAbandoningFollowers && totalCanLeaveBehind>0) {
						//		...QuietOptionPane "Unwanted Followers?" dialog...
						//		if (ret==JOptionPane.YES_OPTION) {
						//			turnPanel.doAbandonActionFollowers();
						//			actionFollowers = character.getActionFollowers();
						//		}
						//	}
						//}
					}
					
					HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
					// First and foremost, make sure character can carry everything
					if (!character.canMove() && current.isInClearing()) {
						if (character.getWeight().isMaximum() || character.mustFly() || (hostPrefs.hasPref(Constants.SR_OPT_MOVEMENT_RESTRICTION) && !character.hasMoveChit(true,false))) {
							JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"You cannot move.  Move action cancelled.");
							cancelled = true;
							return;
						}
						JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"You cannot move with your current inventory.  Drop something first.");
						completed = false;
						return;
					}
					
					if (character.isMistLike()) {
						ArrayList<RealmComponent> followingHirelings = character.getFollowingHirelings();
						if (!followingHirelings.isEmpty()) {
							// Drop following hirelings in the clearing
							for (RealmComponent fh:followingHirelings) {
								if (!fh.getGameObject().hasThisAttribute(Constants.IGNORE_MIST_LIKE)) {
									ClearingUtility.moveToLocation(fh.getGameObject(),current);
								}
							}
						}
					}
					
					if (hostPrefs.hasPref(Constants.SR_NO_HORSES_IN_CAVES) && location.hasClearing() && location.clearing.isCave()) {
						for (GameObject item : character.getInventory()) {
							abandonHorse(item,character);
						}
						for (RealmComponent hireling : character.getFollowingHirelings()) {
							for (GameObject item : hireling.getHold()) {
								abandonHorse(item,hireling);
							}
						}
						for (CharacterWrapper follower : character.getActionFollowers()) {
							for (GameObject item : follower.getInventory()) {
								abandonHorse(item,follower);
							}
							for (RealmComponent hireling : follower.getFollowingHirelings()) {
								for (GameObject item : hireling.getHold()) {
									abandonHorse(item,hireling);
								}
							}
						}
					}
					
					if (hostPrefs.hasPref(Constants.SR_ADV_GROUNDED_MISSIONS_AND_TASKS)) {
						if (!character.moveRandomly() && !magicPath && !gates && !pathfinder && (!current.isTileOnly() || current.isFlying()) &&
								(path==null || character.usesWalkingTheWoods(path)) && character.canWalkWoods(current.tile,current.clearing,location.clearing)) {
							for (GameObject item:character.getInventory()) {
								if (RealmComponent.getRealmComponent(item).isGoldSpecial()) {
									GoldSpecialChitComponent gs = (GoldSpecialChitComponent)RealmComponent.getRealmComponent(item);
									if (gs.isMission() || gs.isTask()) {
										gs.expireEffect(character);
										character.addFailedGoldSpecial(gs);
										TreasureUtility.doDrop(character,item,gameHandler.getUpdateFrameListener(),false);
										
										QuestRequirementParams qp = new QuestRequirementParams();
										qp.actionName = item.getName();
										qp.actionType = CharacterActionType.AbandonMissionCampaign;
										qp.targetOfSearch = gs.getGameObject();
										character.testQuestRequirements(gameHandler.getMainFrame(),qp);
									}
								}
							}
						}
					}
					
					// Here is the ACTUAL MOVE
					character.moveToLocation(gameHandler.getMainFrame(),location);
					if (location.hasClearing() && location.clearing.isEdge()) {
						// Character has left the map
						character.makeGone();
					}
					PathDetail reverse = null;
					if (path!=null) {
						reverse = path.getEdgePathFromOtherTile();
						if (!overridePath && !offroadTravel) {
							character.updatePathKnowledge(path);
						}
					}
					if (reverse!=null && !overridePath && !offroadTravel) {
						character.updatePathKnowledge(reverse);
					}
					
					if (magicPath) {
						character.getGameObject().removeThisAttribute(Constants.MAGIC_PATH_EFFECT);
					}
					
					if ((hostPrefs.hasPref(Constants.FE_KILLER_CAVES)) && location.hasClearing() && location.clearing.isCave()) {
						for (GameObject item : character.getInventory()) {
							if (RealmComponent.getRealmComponent(item).isHorse() && !item.hasThisAttribute(Constants.STEED_IN_CAVES_AND_WATER)) {
								TreasureUtility.doDeactivate(gameHandler.getMainFrame(), character, item);
								if (!item.hasThisAttribute(Constants.STEED_SURVIVES_CAVES)) {
									if (hostPrefs.hasPref(Constants.FE_KILLER_CAVES)) {
										RealmUtility.makeDead(RealmComponent.getRealmComponent(item));
									}
								}
								if (item.hasThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE)) {
									SpellMasterWrapper spellmaster = SpellMasterWrapper.getSpellMaster(gameHandler.getClient().getGameData());
									for (SpellWrapper spell : spellmaster.getAffectingSpells(item)) {
										if (spell.isControlHorseSpell()) spell.expireSpell();
									}
								}
							}
						}
					}
					
					if (location.hasClearing() && location.clearing.isWater()) {
						RealmCalendar cal = RealmCalendar.getCalendar(gameHandler.getClient().getGameData());
						if (cal.isFlood(gameHandler.getGame().getMonth())) {
							character.setWeatherFatigue(character.getWeatherFatigue()+1);
						}
					}
					
					for (CharacterWrapper follower :  actionFollowers) {
						if ((!overridePath && !offroadTravel) || path!=null) {
							if (follower.canFollow()) {
								follower.moveToLocation(gameHandler.getMainFrame(),location);
								// Followers ALWAYS learn secrets (unless walking woods...?)
								if (!overridePath && !offroadTravel) {
									follower.updatePathKnowledge(path);
									if (reverse!=null) {
										follower.updatePathKnowledge(reverse);
									}
								}
								if (location.hasClearing() && location.clearing.isEdge()) {
									follower.makeGone();
								}
							}
							else {
								// Oops, follower was likely encumbered!  Take 'em off the list!
								character.removeActionFollower(follower,gameHandler.getGame().getMonsterDie(),gameHandler.getGame().getNativeDie());
							}
						}
						else {
							// Oops, follower can't follow character because he is walking woods
							character.removeActionFollower(follower,gameHandler.getGame().getMonsterDie(),gameHandler.getGame().getNativeDie());
						}
					}
					
					QuestRequirementParams params = new QuestRequirementParams();
					params.actionType = CharacterActionType.Move;
					character.testQuestRequirements(gameHandler.getMainFrame(),params);
					
					if (gameHandler.isOption(RealmSpeakOptions.MAP_FOLLOW_CHARACTER)) {
						gameHandler.getInspector().getMap().centerOn(character.getCurrentLocation());
					}
					gameHandler.updateCharacterFrames();
					if (!offroadTravel) {
						result = result+"moved";
					}
					
					if (magicPath) {
						result = result+" (using Magic Path)";
					}
					if (!overridePath && !offroadTravel && !current.isBetweenClearings() && (path.isNarrow() || (reverse!=null && reverse.isNarrow()))) {
						// Other characters in the same clearing who have found hidden enemies
						// for the day should gain a discovery when this move occurs (on either end of the path!)
						if (current.hasClearing() && !hostPrefs.hasPref(Constants.SR_NO_SPYING)) {
							for (RealmComponent rc:current.clearing.getClearingComponents()) {
								if (rc.canSpy() && !rc.getGameObject().equals(character.getGameObject())) {
									CharacterWrapper spy = new CharacterWrapper(rc.getGameObject());
									if (!character.isHidden() || spy.foundHiddenEnemy(character.getGameObject())) {
										spy.updatePathKnowledge(path); // spy's that see character leave only get the path they are leaving on!
									}
								}
							}
							for (RealmComponent rc:location.clearing.getClearingComponents()) {
								if (rc.canSpy() && !rc.getGameObject().equals(character.getGameObject())) {
									CharacterWrapper spy = new CharacterWrapper(rc.getGameObject());
									if (!character.isHidden() || spy.foundHiddenEnemy(character.getGameObject())) {
										// spy's that see a character enter only get the reverse, unless there isn't one.
										if (reverse==null) {
											spy.updatePathKnowledge(path);
										}
										else {
											spy.updatePathKnowledge(reverse);
										}
									}
								}
							}
						}
						for (CharacterWrapper follower : character.getActionFollowers()) {
							follower.updatePathKnowledge(path);
							follower.updatePathKnowledge(reverse);
						}
					}
				}
				else {
					cancelled = true;
					result = "Cannot Move: undiscovered or thorned path";
				}
			}
			else {
				cancelled = true;
				result = "Cannot Move: no path";
			}
		}
		else {
			// this should never happen
			throw new IllegalStateException("null clearing during ActionRow.process!");
		}
		if (cancelled && turnPanel.getActionControlManager()!=null) {
			JOptionPane.showMessageDialog(gameHandler.getMainFrame(),result,"Move Cancelled",JOptionPane.WARNING_MESSAGE);
		}
	}
	private void abandonHorse(GameObject item, CharacterWrapper character) {
		if (RealmComponent.getRealmComponent(item).isHorse() && !item.hasThisAttribute(Constants.STEED_IN_CAVES_AND_WATER)) {
			TreasureUtility.doDeactivate(gameHandler.getMainFrame(), character, item);
			if (!item.hasThisAttribute(Constants.STEED_SURVIVES_CAVES)) {
				TreasureUtility.doDrop(character,item,gameHandler.getUpdateFrameListener(),false);
			}
			if (item.hasThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE)) {
				SpellMasterWrapper spellmaster = SpellMasterWrapper.getSpellMaster(gameHandler.getClient().getGameData());
				for (SpellWrapper spell : spellmaster.getAffectingSpells(item)) {
					if (spell.isControlHorseSpell()) {
						spell.expireSpell();
					}
				}
			}
		}
	}
	private void abandonHorse(GameObject item, RealmComponent hireling) {
		if (RealmComponent.getRealmComponent(item).isHorse() && !item.hasThisAttribute(Constants.STEED_IN_CAVES_AND_WATER)) {
			item.removeThisAttribute(Constants.ACTIVATED);
			if (!item.hasThisAttribute(Constants.STEED_SURVIVES_CAVES)) {
				TreasureUtility.doDrop(new CharacterWrapper(hireling.getGameObject()),item,null,false);
			}
			if (item.hasThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE)) {
				SpellMasterWrapper spellmaster = SpellMasterWrapper.getSpellMaster(gameHandler.getClient().getGameData());
				for (SpellWrapper spell : spellmaster.getAffectingSpells(item)) {
					if (spell.isControlHorseSpell()) {
						spell.expireSpell();
					}
				}
			}
		}
	}
	private void doSearchAction() {
		if (character.hasCurse(Constants.EYEMIST)) {
			result = "Cannot SEARCH with EYEMIST curse.";
			return;
		}
		// Player chooses from one type of search table
		RealmTable searchTable = null;
		TileLocation current = character.getCurrentLocation(); // shouldn't be able to do a search if not in a clearing!
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		boolean canUseMagicSight = false;
		boolean mustUseMagicSight = false;
		boolean optionalRule = hostPrefs.hasPref(Constants.SR_MAGIC_SIGHT_OPTIONAL);
		if (optionalRule) {
			canUseMagicSight = character.canUseMagicSight();
		}
		mustUseMagicSight = character.mustUseMagicSight(optionalRule); // magic sight limits what character can do
		
		// choose from Peer, Locate, Loot, ReadingRunes
		// Should be able to cancel to stop a playAll
		ButtonOptionDialog chooseSearch = new ButtonOptionDialog(gameHandler.getMainFrame(), null, "Search:", "", true);
		if (mustUseMagicSight && !canUseMagicSight) {
			addTableToChooser(chooseSearch,RealmTable.magicSight(gameHandler.getMainFrame()));
		}
		else {
			if (canUseMagicSight) {
				addTableToChooser(chooseSearch,RealmTable.magicSight(gameHandler.getMainFrame()));
			}
			if (hostPrefs.hasPref(Constants.FE_SEARCH_TABLES)) {
				addTableToChooser(chooseSearch,RealmTable.search1ed(gameHandler.getMainFrame(),null));
			}
			
			if (character.getPeerAny()) {
				if (!hostPrefs.hasPref(Constants.FE_SEARCH_TABLES)) {
					addTableToChooser(chooseSearch,RealmTable.peerAny(gameHandler.getMainFrame()));
				}
				else {
					addTableToChooser(chooseSearch,RealmTable.peerAny1ed(gameHandler.getMainFrame()));
				}
			}
			else {
				RealmCalendar cal = RealmCalendar.getCalendar(gameHandler.getClient().getGameData());
				boolean canPeer = character.canPeer() && !cal.isPeerDisabled(character.getCurrentMonth());
				
				if (!hostPrefs.hasPref(Constants.FE_SEARCH_TABLES)) {
					addTableToChooser(chooseSearch,RealmTable.peer(gameHandler.getMainFrame(),null),canPeer);
					if (current.clearing.isMountain()) {
						// If in a mountain clearing, allow peer into mountain/woods clearing in same or adjacent tiles
						addTableToChooser(chooseSearch,RealmTable.mountainPeer(gameHandler.getMainFrame()),canPeer);
					}
				}
				else {
					addTableToChooser(chooseSearch,RealmTable.peer1ed(gameHandler.getMainFrame(),null),canPeer);
					if (current.clearing.isMountain()) {
						// If in a mountain clearing, allow peer into mountain/woods clearing in same or adjacent tiles
						addTableToChooser(chooseSearch,RealmTable.mountainPeer1ed(gameHandler.getMainFrame()),canPeer);
					}
				}
			}
			
			if (!hostPrefs.hasPref(Constants.FE_SEARCH_TABLES)) {
				addTableToChooser(chooseSearch,RealmTable.locate(gameHandler.getMainFrame(),null));
			}
			else {
				addTableToChooser(chooseSearch,RealmTable.locate1ed(gameHandler.getMainFrame(),null));
			}
		}
		
		for (RealmComponent rc:current.clearing.getClearingComponents()) {
			// Loot is a special case, as it requires a TL
			if (rc.getGameObject().hasThisAttribute(RealmComponent.TREASURE_LOCATION)) {
				if (/*!rc.getGameObject().hasThisAttribute("discovery") ||*/ // Why did I have this?
						character.hasTreasureLocationDiscovery(rc.getGameObject().getName())) {
					
					// no point in looting if nothing is left! (exception: Sites with TableLoot)
					if (TreasureUtility.getTreasureCount(rc.getGameObject(),character)>0 || rc.getGameObject().hasAttributeBlock("table")) {
						// can't loot sites that still need to be opened (crypt, vault)
						if (!rc.getGameObject().hasThisAttribute(Constants.NEEDS_OPEN)) {
							Loot loot = (Loot)RealmTable.loot(gameHandler.getMainFrame(),character,rc.getGameObject(),gameHandler.getUpdateFrameListener());
							if (((!mustUseMagicSight || canUseMagicSight) || (loot instanceof TableLoot)) && character.canLoot(rc)) {
								addTableToChooser(chooseSearch,loot);
							}
						}
					}
										
					// any spells for Read Runes?
					if ((!mustUseMagicSight || canUseMagicSight) && !rc.getGameObject().hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE) && character.isCharacter() && SpellUtility.getSpellCount(rc.getGameObject(),null,true)>0) {
						addTableToChooser(chooseSearch,RealmTable.readRunes(gameHandler.getMainFrame(),rc.getGameObject()));
					}
				}
			}
			else if (rc.isTraveler() && rc.getOwnerId()==null && rc.getGameObject().hasThisAttribute(Constants.CAPTURE)) {
				addTableToChooser(chooseSearch,RealmTable.capture(gameHandler.getMainFrame(),(TravelerChitComponent)rc));
			}
		}
		
		ArrayList<GameObject> openableSites = character.getAllOpenableSites();
		if (!openableSites.isEmpty()) {
			String message = "Open";
			chooseSearch.addSelectionObject(message);
			IconGroup group = new IconGroup(IconGroup.HORIZONTAL,2);
			for (GameObject go:openableSites) {
				group.addIcon(RealmComponent.getRealmComponent(go).getIcon());
			}
			chooseSearch.setSelectionObjectIcon(message,group);
		}
		
		if ((!mustUseMagicSight || canUseMagicSight) && ClearingUtility.getAbandonedItemCount(current)>0) {
			// don't need hint icons for clearing loots...
			chooseSearch.addSelectionObject(RealmTable.loot(gameHandler.getMainFrame(),character,current,gameHandler.getUpdateFrameListener()));
		}
		
		// check player inventory
		if ((!mustUseMagicSight || canUseMagicSight)) {
			for (GameObject item:character.getEnhancingItems()) {
				if (!item.hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE) && SpellUtility.getSpellCount(item,null,true)>0) {
					addTableToChooser(chooseSearch,RealmTable.readRunes(gameHandler.getMainFrame(),item));
				}
			}
		}
		
		chooseSearch.setLocationRelativeTo(gameHandler.getMainFrame());
		chooseSearch.pack();
		chooseSearch.setVisible(true);
		
		Object selected = chooseSearch.getSelectedObject();
		if (selected instanceof String) {
			// Currently, the only String possibility, is the option to open the VAULT
			// This is a bit hacky, but will work for now.
			character.markAllInventoryNotNew();
			TreasureUtility.openOneObject(turnPanel.getGameHandler().getMainFrame(),character,openableSites,turnPanel.getGameHandler().getUpdateFrameListener(),false);
			doSearchAction(); // recurses because we don't want to use up a search
			return;
		}
		searchTable = (RealmTable)selected;

		if (searchTable==null) {
			// player cancelled the dialog, so get out of here
			completed = false;
			return;
		}

		if (searchTable.fulfilledPrerequisite(gameHandler.getMainFrame(),character)) {
			character.markAllInventoryNotNew();
			realmTable = searchTable;
			handleTable();
		}
		else {
			// Didn't fulfill prerequisite (ie., fatigue a chit), so cancel the action.
			completed = false;
		}
	}
	private void addTableToChooser(ButtonOptionDialog chooser,RealmTable table) {
		addTableToChooser(chooser,table,true);
	}
	private void addTableToChooser(ButtonOptionDialog chooser,RealmTable table,boolean enabled) {
		chooser.addSelectionObject(table,enabled);
		chooser.setSelectionObjectIcon(table,table.getHintIcon(character));
	}
	private static final String TRADE_BUY = "BUY";
	private static final String TRADE_SELL = "SELL";
	private static final String TRADE_REPAIR = "Repair Armor";
	private static final String TRADE_REPAIR_BLACKSMITH = "Repair Armor (Blacksmith)";
	private static final String TRADE_CLERIC = "Cleric Services";
	private static final String TRADE_JOIN = "Join Guild";
	private static final String TRADE_SERVICES = "Guild Services";
	private void doTradeAction() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		// Player chooses from all native leaders in the clearing
		// Player then chooses from items for sale
		TileLocation tl = character.getCurrentLocation();
		ArrayList<RealmComponent> traders = ClearingUtility.getAllTraders(character,tl.clearing);
		if (!traders.isEmpty()) { // need traders to trade!
			// Select a trader
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Select trade action:",true);
			int keyN = 0;
			for (RealmComponent rc:traders) {
				if (rc.isTraveler()) {
					chooser.addRealmComponent(rc,rc.getGameObject().getName());
				}
				else if (rc.isGuild()) {
					if (character.hasOtherChitDiscovery(rc.getGameObject().getName())) {
						String key = null;
						if (character.isGuildMember(rc)) {
							key = chooser.generateOption(TRADE_SERVICES);
						}
						else if (character.getCurrentGuild()==null) {
							key = chooser.generateOption(TRADE_JOIN);
						}
						if (key!=null) {
							chooser.addRealmComponentToOption(key,rc,RealmComponentOptionChooser.DisplayOption.Darkside);
						}
						for (int n=0;n<2;n++) {
							int relationship = RealmUtility.getRelationshipBetween(character,rc);
							String relName = RealmUtility.getRelationshipNameFor(relationship);
							key = "N"+(keyN++);
							String text = (n==0?TRADE_BUY:TRADE_SELL)+" ("+relName+")";
							chooser.addOption(key,text);
							chooser.addRealmComponentToOption(key,rc,RealmComponentOptionChooser.DisplayOption.Darkside);
						}
					}
				}
				else {
					if (!rc.getGameObject().hasThisAttribute(Constants.NO_BUYING)) {
						String key = "N"+(keyN++);
						chooser.addOption(key,TRADE_BUY);
						chooser.addRealmComponentToOption(key,rc);
					}
					if (!rc.getGameObject().hasThisAttribute(Constants.NO_SELLING)) {
						String key = "N"+(keyN++);
						chooser.addOption(key,TRADE_SELL);
						chooser.addRealmComponentToOption(key,rc);
					}
					if (hostPrefs.hasPref(Constants.HOUSE3_DWELLING_ARMOR_REPAIR)) {
						if (TreasureUtility.getDamagedArmor(character.getSellableInventory()).size()>0) {
							String key = chooser.generateOption(TRADE_REPAIR);
							chooser.addRealmComponentToOption(key,rc);
						}
					}
				}
				if (rc.getGameObject().hasThisAttribute(Constants.BLACKSMITH)) {
					if (TreasureUtility.getDamagedArmor(character.getSellableInventory()).size()>0) {
						String key = chooser.generateOption(TRADE_REPAIR_BLACKSMITH);
						chooser.addRealmComponentToOption(key,rc);
					}
				}
				if (rc.getGameObject().hasThisAttribute(Constants.CLERIC)) {
					String key = chooser.generateOption(TRADE_CLERIC);
					chooser.addRealmComponentToOption(key,rc);
				}
			}
			chooser.addOption("none","No Trade");
			chooser.setVisible(true);
			String selText = chooser.getSelectedText();
			if (selText!=null) {
				character.markAllInventoryNotNew();
				if ("No Trade".equals(selText)) {
					result = "Cancelled Trade";
					return;
				}
				RealmComponent trader = chooser.getFirstSelectedComponent();
				if (trader.isTraveler()) {
					Store store = Store.getStore((TravelerChitComponent)trader,character);
					if (store!=null && store.canUseStore()) {
						result = store.doService(gameHandler.getMainFrame());
						if (result==null) completed = false;
					}
					else {
						JOptionPane.showMessageDialog(gameHandler.getMainFrame(),store.getReasonStoreNotAvailable(),"Store Not Available!",JOptionPane.PLAIN_MESSAGE,trader.getIcon());
						completed = false;
					}
				}
				else if (trader.isGuild() && selText.equals(TRADE_JOIN)) {
					character.setCurrentGuild(trader.getGameObject().getThisAttribute("guild"));
					if (hostPrefs.hasPref(Constants.GUILDS_START_LEVEL)) {
						character.setCurrentGuildLevel(0);
					}
					else {
						character.setCurrentGuildLevel(1);
						if (hostPrefs.hasPref(Constants.GUILDS_BENEFITS)) {
							GuildStore store = Store.getGuildStore((GuildChitComponent)trader,character);
							store.applyGuildBenefit1(gameHandler.getMainFrame(), character);
						}
					}
					result = "Joined the "+trader.getGameObject().getName();
				}
				else if (trader.isGuild() && selText.equals(TRADE_SERVICES)) {
					GuildStore store = Store.getGuildStore((GuildChitComponent)trader,character);
					if (hostPrefs.hasPref(Constants.GUILDS_NO_SERVICES)) {
						JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"No Guild Services","Store Not Available!",JOptionPane.PLAIN_MESSAGE,trader.getIcon());
						completed = false;
					}
					else if (store!=null && store.canUseStore()) {
						result = store.doService(gameHandler.getMainFrame());
						if (result==null) completed = false;
					}
					else {
						String reason = store==null?"No Store Found?!?":store.getReasonStoreNotAvailable();
						JOptionPane.showMessageDialog(gameHandler.getMainFrame(),reason,"Store Not Available!",JOptionPane.PLAIN_MESSAGE,trader.getIcon());
						completed = false;
					}
				}
				else {
					if (selText.startsWith(TRADE_BUY)) selText = TRADE_BUY;
					if (selText.startsWith(TRADE_SELL)) selText = TRADE_SELL;
					processTrade(trader,selText,hostPrefs);
				}
				
				if (!negate && completed && trader.isNative()) {
					String nativeName = trader.getGameObject().getThisAttribute(RealmComponent.NATIVE);
					GamePool pool = new GamePool(character.getGameData().getGameObjects());
					ArrayList<GameObject> boxes = pool.find("summon_n="+nativeName.toLowerCase());
					for (GameObject box : boxes) {
						ClearingUtility.dumpTravelersToTile(tl.tile.getGameObject(),box,tl.clearing.getNum());
					}
				}
				
				if (!negate && completed && hostPrefs.hasPref(Constants.QST_SR_QUESTS) && !character.isBlocked() && (trader.isNative() || trader.isVisitor() || trader.isTraveler())) {
					boolean tradedQuests = false;
					ArrayList<QuestCardComponent> unfinishedQuests = character.getUnfinishedNotAllPlayQuests();
					ArrayList<QuestCardComponent> characterQuests = new ArrayList<>();
					for (QuestCardComponent quest : unfinishedQuests) {
						if (!(new Quest(quest.getGameObject()).isSticky())) {
							characterQuests.add(quest);
						}
					}
					ArrayList<QuestCardComponent> traderQuests = new ArrayList<>();
					ArrayList<GameObject> questsToNote = new ArrayList<>();
					GameObject holder = null;
					if (trader.isNative()) {
						holder = SetupCardUtility.getDenizenHolder(trader.getGameObject());
						for(GameObject item:holder.getHold()) {
							if ((RealmComponent.getRealmComponent(item)).isQuest()) {
								traderQuests.add((QuestCardComponent) RealmComponent.getRealmComponent(item));
								questsToNote.add(item);
							}
						}
					}
					else {
						for (GameObject item : trader.getHold()) {
							if ((RealmComponent.getRealmComponent(item)).isQuest()) {
								traderQuests.add((QuestCardComponent) RealmComponent.getRealmComponent(item));
								questsToNote.add(item);	
							}
						}
					}
					
					if (!characterQuests.isEmpty() && traderQuests!=null && !traderQuests.isEmpty()) {
						RealmComponentOptionChooser traderQuestChooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Select quest to trade:",true);
						for (RealmComponent quest:traderQuests) {
							traderQuestChooser.addRealmComponent(quest,quest.getGameObject().getName());
						}
						traderQuestChooser.addOption("none","No Trade");
						traderQuestChooser.setVisible(true);
						String selectedTraderQuest = traderQuestChooser.getSelectedText();
						if (selectedTraderQuest!=null && selectedTraderQuest!="No Trade") {
							RealmComponentOptionChooser characterQuestChooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Select quest to trade:",true);
							for (RealmComponent quest:characterQuests) {
								characterQuestChooser.addRealmComponent(quest,quest.getGameObject().getName());
							}
							characterQuestChooser.addOption("none","No Trade");
							characterQuestChooser.setVisible(true);
							String selectedCharacterQuest = characterQuestChooser.getSelectedText();
							if (selectedCharacterQuest!=null && selectedTraderQuest!="No Trade") {
								RealmComponent quest1 = traderQuestChooser.getFirstSelectedComponent();
								RealmComponent quest2 = characterQuestChooser.getFirstSelectedComponent();
								
								character.removeQuest(new Quest(quest2.getGameObject()));
								if (trader.isNative()) {
									holder.remove(quest1.getGameObject());
									holder.add(quest2.getGameObject());
								} else {
									trader.getGameObject().remove(quest1.getGameObject());
									trader.getGameObject().add(quest2.getGameObject());
								}
								character.addQuest(gameHandler.getMainFrame(), new Quest(quest1.getGameObject()));
								tradedQuests = true;
								questsToNote.remove(quest1.getGameObject());
								questsToNote.add(quest2.getGameObject());
								character.addNoteTrade(trader.getGameObject(),questsToNote);
							}
						}
					}
					if (!tradedQuests) {
						character.addNoteTrade(trader.getGameObject(),questsToNote);
					}
				}
			}
			else {
				completed = false;
			}
		}
		
		if (!negate) {
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Trading;
			character.testQuestRequirements(gameHandler.getMainFrame(),params);
		}
	}
	private void processTrade(RealmComponent trader,String tradeAction,HostPrefWrapper hostPrefs) {
		ArrayList<GameObject> hold = null;
		ArrayList<GameObject> holdToNote = null;
		String traderName = trader.isNative()?trader.getGameObject().getThisAttribute("native"):trader.getGameObject().getName();
		String relName = RealmUtility.getRelationshipNameFor(character,trader);
		String traderRel = traderName+" ("+relName+")";
		if (TRADE_BUY.equals(tradeAction)) {
			hold = new ArrayList<>();
			holdToNote = new ArrayList<>();
			if (trader.isNative()) {
				// Native Leader - trade with their dwelling's hold
				GameObject holder = SetupCardUtility.getDenizenHolder(trader.getGameObject());
				for(GameObject go:holder.getHold()) {
					holdToNote.add(go);
					if (!go.hasThisAttribute(Constants.VALUABLE) && !(RealmComponent.getRealmComponent(go)).isQuest()) {
						hold.add(go);
					}
				}
			}
			else {
				// Visitor or Guild - trade directly with their hold
				for(GameObject go:trader.getGameObject().getHold()) {
					holdToNote.add(go);
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if (!go.hasThisAttribute(Constants.VALUABLE) && !rc.isQuest() &&(!rc.isSpell() || character.canLearn(go))) {
						hold.add(go);
					}
				}
			}
			
			// Find and add any boons for this trader
			hold.addAll(character.getBoons(trader.getGameObject()));
			
			// Update the character notebook accordingly
			character.addNoteTrade(trader.getGameObject(),holdToNote);
		}
		else if (TRADE_REPAIR.equals(tradeAction) || TRADE_REPAIR_BLACKSMITH.equals(tradeAction)) {
			hold = TreasureUtility.getDamagedArmor(character.getSellableInventory());
		}
		else if (TRADE_CLERIC.equals(tradeAction)) {
			completed = handleClericService();
			return;
		}
		else { // TRADE_SELL
			hold = character.getSellableInventory();
		}
		
		if (!hold.isEmpty()) {
			// Cool - now do trading
			
			boolean unhide = false;
			// First, make sure all treasures are marked as "seen"
			for (GameObject item : hold) {
				if (!item.hasThisAttribute(Constants.TREASURE_SEEN)) {
					item.setThisAttribute(Constants.TREASURE_SEEN);
					if (item.hasThisAttribute(Constants.NO_HIDE)) {
						unhide = true;
					}
				}
			}
			if (unhide) {
				TileLocation current = character.getCurrentLocation();
				character.setHidden(false);
				if (current!=null && current.hasClearing()) {
					for (RealmComponent rc:current.clearing.getClearingComponents()) {
						if (rc.isCharacter()) {
							(new CharacterWrapper(rc.getGameObject())).setHidden(false);
						}
					}
				}
			}
			
			// Just in case the Flowers of Rest are here, we'd better check for sleep...
			checkSleep();
			if (character.isSleep()) {
				// oops!
				return;
			}
			
			RealmTradeDialog tradeDialog;
			if (TRADE_BUY.equals(tradeAction)) {
				// Buying
				tradeDialog = new RealmTradeDialog(gameHandler.getMainFrame(),"Select an item or spell to BUY from "+traderRel+":",false,false,true);
				
				// Log what is being offered up
				StringBufferedList sb = new StringBufferedList();
				for(GameObject go:hold) {
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if (go.hasThisAttribute(RealmComponent.TREASURE) && !hostPrefs.hasPref(Constants.HOUSE1_NO_SECRETS)) {
						sb.append("Treasure");
					}
					else if (rc.isSpell() && !hostPrefs.hasPref(Constants.HOUSE1_NO_SECRETS)) {
						sb.append("Spell");
					}
					else {
						sb.append(go.getName());
					}
				}
				sb.countIdenticalItems();
				gameHandler.broadcast(character.getGameObject().getName(),"Buying from "+trader.getGameObject().getName()+".");
				gameHandler.broadcast(character.getGameObject().getName(),"Available for sale: "+sb.toString());
			}
			else if (TRADE_REPAIR.equals(tradeAction)) {
				// Repair
				tradeDialog = new RealmTradeDialog(gameHandler.getMainFrame(),"Select armor to have "+traderRel+" REPAIR:",false,true,false);
				tradeDialog.setRepairMode(true);
			}
			else if (TRADE_REPAIR_BLACKSMITH.equals(tradeAction)) {
				// Repair
				tradeDialog = new RealmTradeDialog(gameHandler.getMainFrame(),"Select armor to have "+traderRel+" REPAIR:",false,true,false);
				tradeDialog.setRepairMode(true);
			}
			else {
				// Selling
				boolean allowMultiple = true;
				if (hostPrefs.hasPref(Constants.SR_ADV_SELLING)) {
					allowMultiple = false;
				}
				tradeDialog = new RealmTradeDialog(gameHandler.getMainFrame(),"Select item(s) to SELL to "+traderRel+":",allowMultiple,true,false);
			}
			tradeDialog.setDealingCharacter(character);
			tradeDialog.setTrader(trader);
			tradeDialog.setTradeObjects(hold);
			tradeDialog.setVisible(true);
			
			Collection<RealmComponent> selComponents = tradeDialog.getSelectedRealmComponents();
			// Cancel when buying ends action
			if (selComponents == null && TRADE_BUY.equals(tradeAction)) {
				completed = false;
				return;
			}
			if (selComponents == null && TRADE_SELL.equals(tradeAction)) {
				completed = false;
				return;
			}
			
			if (selComponents!=null && selComponents.size()>0) {
				boolean repair = TRADE_REPAIR.equals(tradeAction) || TRADE_REPAIR_BLACKSMITH.equals(tradeAction);
				if (TRADE_BUY.equals(tradeAction) || repair) { // TRADE_BUY or TRADE_REPAIR
					
					// Can only be one item purchased
					RealmComponent merchandise = selComponents.iterator().next();
					
					// Let's make sure this item CAN be bought
					if (!repair) {
						int famePrice = TreasureUtility.getFamePrice(merchandise.getGameObject(),trader.getGameObject());
						if (famePrice>0 && character.hasCurse(Constants.DISGUST)) {
							JOptionPane.showMessageDialog(
									gameHandler.getMainFrame(),
									"That item would cost fame, but you are affected by the curse DISGUST.",
									"Invalid Purchase",
									JOptionPane.INFORMATION_MESSAGE,
									merchandise.getFaceUpIcon());
							completed = false;
							return;
						}
						if (famePrice>character.getFame() && hostPrefs.hasPref(Constants.HOUSE1_NO_NEGATIVE_POINTS)) {
							if (famePrice>character.getFame()) {
								JOptionPane.showMessageDialog(
										gameHandler.getMainFrame(),
										"That item would cause your fame to be negative, which violates the host's rules.",
										"Invalid Purchase",
										JOptionPane.INFORMATION_MESSAGE,
										merchandise.getFaceUpIcon());
								completed = false;
								return;
							}
						}
					}
					
					GameObject go = merchandise.getGameObject();
					String merchandiseName = "the "+go.getName();
					if (go.hasThisAttribute(RealmComponent.TREASURE) && !hostPrefs.hasPref(Constants.HOUSE1_NO_SECRETS)) {
						merchandiseName = "a treasure";
					}
					
					if (repair) {
						gameHandler.broadcast(character.getGameObject().getName(),"Bidding to repair "+merchandiseName);
					}
					else {
						gameHandler.broadcast(character.getGameObject().getName(),"Bidding for "+merchandiseName);
					}
					
					boolean credit = false;
					if (!repair && hostPrefs.hasPref(Constants.SR_ADV_CREDIT) && character.getFame()>0) {
						int ret = JOptionPane.showConfirmDialog(
								new JFrame(),
								"Do you want to buy on credit (pay with fame)?",
								"Buy on Credit",
								JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,character.getIcon());
						if (ret == JOptionPane.YES_OPTION) {
							credit = true;
						}
					}
					
					// Determine price, and then verify with player that they want to buy
					realmTable = Meeting.createMeetingTable(
							gameHandler.getMainFrame(),
							character,
							character.getCurrentLocation(),
							trader,
							merchandise,
							null,
							RelationshipType.ALLY);
					((Meeting)realmTable).setSpecificAction("Trade");
					if (credit) {
						((Meeting)realmTable).setCreditFame();
					}
					handleTable();
				}
				else {
					// Log what is being sold
					StringBufferedList sb = new StringBufferedList();
					for(RealmComponent rc : selComponents) {
						sb.append(rc.getGameObject().getName());
					}
					sb.countIdenticalItems();
					gameHandler.broadcast(character.getGameObject().getName(),"Selling to "+trader.getGameObject().getName()+".");
					gameHandler.broadcast(character.getGameObject().getName(),"Attempting to sell: "+sb.toString());
					
					realmTable = Commerce.createCommerceTable(
							gameHandler.getMainFrame(),
							character,
							character.getCurrentLocation(),
							trader,
							selComponents,
							RelationshipType.ALLY,
							hostPrefs);
					((Commerce)realmTable).setSpecificAction("Trade");
					handleTable();
				}
			}
			else {
				completed = false;
			}
		}
		else {
			JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"Nothing to trade!");
			completed = false;
		}
	}
	private boolean handleClericService() {
		if (character.hasCurse(Constants.ASHES)) {
			JOptionPane.showMessageDialog(gameHandler.getMainFrame(),
					"You are cursed by the ASHES curse, and cannot pay the cleric.",
					"No Cleric service possible",
					JOptionPane.INFORMATION_MESSAGE,
					character.getIcon());
			return false;
		}
		RealmComponent rc = RealmComponent.getRealmComponent(character.getGameObject());
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Cancel which Curse, Mesmerize or Spell?",true);
		boolean clericServiceNeeded = false;
		for (String curse:character.getAllCurses()) {
			chooser.addOption(curse,"Remove "+curse+" Curse (5 gold)");
			chooser.addRealmComponentToOption(curse,rc);
			clericServiceNeeded = true;
		}
		SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(character.getGameData());
		Collection<SpellWrapper> spells = sm.getAffectingSpells(character.getGameObject());
		if (!spells.isEmpty()) {
			clericServiceNeeded = true;
			for (SpellWrapper spell : spells) {
				chooser.addRealmComponentToOption(spell.getName(),RealmComponent.getRealmComponent(spell.getGameObject()));
			}
		}
		if (!clericServiceNeeded) {
			JOptionPane.showMessageDialog(gameHandler.getMainFrame(),
					"There are no curses, mesmerize effects or spells to cancel.",
					"No Cleric service needed",
					JOptionPane.INFORMATION_MESSAGE,
					character.getIcon());
			return false;
		}
		chooser.setVisible(true);
		RealmComponent selectedRc = chooser.getFirstSelectedComponent();
		String optionKey = chooser.getSelectedOptionKey();
		if (optionKey!=null) {
			if (selectedRc.isSpell()) {
				if (character.getGold()<10) {
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(),
								"You do not have enough gold to cancel a spell.",
								"Not enough gold",
								JOptionPane.INFORMATION_MESSAGE,
								selectedRc.getFaceUpIcon());
					return false;
				}
				character.addGold(-10);
				character.removeCurse(optionKey);
				gameHandler.broadcast(character.getGameObject().getName(),"Canceled the "+optionKey+" spell by the Cleric.");
				return true;
			}
			else if (character.getAllCurses().contains(optionKey)) {
				if (character.getGold()<5) {
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(),
								"You do not have enough gold to cancel a curse or mesmerize effect.",
								"Not enough gold",
								JOptionPane.INFORMATION_MESSAGE,
								character.getIcon());
					return false;
				}
				character.addGold(-5);
				character.removeCurse(optionKey);
				gameHandler.broadcast(character.getGameObject().getName(),"Removed the "+optionKey+" curse by the Cleric.");
				return true;
			}
		}
		return false;
	}
	private void doStealAction() {
		TileLocation tl = character.getCurrentLocation();
		ArrayList<RealmComponent> victims = ClearingUtility.getAllVictimsForStealing(character,tl.clearing);
		
		if (victims.isEmpty()) {
			result = "Nobody to steal from.";
			completed = true;
			return;
		}	
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Select victim to steal from:",false);
		for (RealmComponent rc:victims) {
			chooser.addRealmComponent(rc,rc.getGameObject().getName());
		}
		chooser.setVisible(true);
		String message = null;
		RealmComponent victim = chooser.getFirstSelectedComponent();
		realmTable = new StealAttempt(gameHandler.getMainFrame(),victim);
		roller = DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createRoller(realmTable);
		roller.addModifier(character.getStealAttempts());
		message = realmTable.apply(character,roller);
		result = realmTable.getTableName(false) + " - " + message;
		gameHandler.updateCharacterFrames();
		character.addStealAttempt();
		
		if (realmTable.getNewTable()!=null) {
			RealmTable newTable = realmTable.getNewTable();
			newAction = new ActionRow(turnPanel,character,newTable,isFollowing);
			newAction.setRoller(DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createRoller(newTable));
			message = newTable.apply(character,roller);
			newAction.setResult(newTable.getTableName(false) + " - " + message);
			newAction.completed = true;
			gameHandler.updateCharacterFrames();
		}
		
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.Stealing;
		character.testQuestRequirements(gameHandler.getMainFrame(),params);
		completed = true;
	}
	private void doRestAction() {
		if (character.hasCurse(Constants.ILL_HEALTH)) {
			result = "Cannot REST with ILL HEALTH curse.";
		}
		else if (character.isTransmorphed()) {
			result = "Cannot REST while transmorphed.";
		}
		else {
			ArrayList<CharacterActionChitComponent> restChoices = character.getRestableChits();
			if (!restChoices.isEmpty()) { // has to be chits to rest!
				boolean blockRestAction = false;
				if (RealmUtility.willBeBlocked(character,isFollowing,false)) {
					blockRestAction = true;
				}
				else if (count > 1) {
					TileLocation current = character.getCurrentLocation();
					HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
					boolean blockingPhases = hostPrefs.hasPref(Constants.OPT_BLOCKING_PHASES);
					for (GameObject livingCharacter : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
						if (blockingPhases) {
							new CharacterWrapper(livingCharacter).removeAllReactDecisions();
						}
						new CharacterWrapper(livingCharacter).checkForBlockingState(true,current);
					}
					gameHandler.updateCharacterFramesWithoutMap();
					if (turnPanel.isAwaitingReactDecision(true,current)) {
						blockRestAction = true;
					}
				}
				
				// Block after the first phase!
				if (blockRestAction) {	
					// Make this one 1 phase, and then split any remaining count into a new action row
					int newCount = count-1;
					count = 1;
					if (newCount>0) {
						newAction = makeCopy();
						newAction.setCount(newCount);
					}
				}
				
				bonusCount = character.getRestBonus(count);
				ChitRestManager rester = new ChitRestManager(gameHandler.getMainFrame(),character,count+bonusCount);
				rester.setVisible(true);
				if (rester.isFinished()) {
					result = "Rested "+(count+bonusCount)+" asterisk"+((count+bonusCount)==1?"":"s");
					
					QuestRequirementParams params = new QuestRequirementParams();
					params.actionType = CharacterActionType.Rest;
					character.testQuestRequirements(gameHandler.getMainFrame(),params);
				}
				else {
					// Cancelled!
					completed = false;
				}
			}
			else {
				if (character.hasCurse(Constants.WITHER) && character.getFatiguedChits().size()>0) {
					result = "Unable to rest fully, due to WITHER curse.";
				}
				else {
					result = "You are fully rested.";
					QuestRequirementParams params = new QuestRequirementParams();
					params.actionType = CharacterActionType.Rest;
					character.testQuestRequirements(gameHandler.getMainFrame(),params);
				}
			}
		}
		// Make sure followers get a rest too!
		for (CharacterWrapper follower : character.getActionFollowers()) {
			if (!follower.hasCurse(Constants.ILL_HEALTH)
					&& !follower.isTransmorphed()
					&& !follower.getRestableChits().isEmpty()) {
				follower.setFollowRests(count);
			}
		}
	}
	private void doHealAction() {
		// Select a character in the same clearing that has wounds/fatigue, and does not have ILL_HEALTH, or is transmorphed
		TileLocation current = character.getCurrentLocation();
		ArrayList<RealmComponent> canBeHealed = new ArrayList<>();
		for (RealmComponent rc:current.clearing.getClearingComponents()) {
			if (rc.isCharacter()) {
				if (!rc.getGameObject().equals(character.getGameObject())) { // can't be you!
					CharacterWrapper aCharacter = new CharacterWrapper(rc.getGameObject());
					if (!aCharacter.hasCurse(Constants.ILL_HEALTH) && !aCharacter.isTransmorphed()) {
						if (aCharacter.getRestableChits().size()>0) {
							canBeHealed.add(rc);
						}
					}
				}
			}
		}
		
		if (canBeHealed.size()>0) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Who will you heal?",true);
			chooser.addRealmComponents(canBeHealed,false);
			chooser.setVisible(true);
			if (chooser.getSelectedText()!=null) {
				RealmComponent rc = chooser.getFirstSelectedComponent();
				CharacterWrapper aCharacter = new CharacterWrapper(rc.getGameObject());
				ChitRestManager rester = new ChitRestManager(gameHandler.getMainFrame(),aCharacter,1);
				rester.setVisible(true);
				if (rester.isFinished()) {
					result = "Healed the "+aCharacter.getGameObject().getName()+" 1 asterisk.";
				}
				else {
					// Cancelled!
					completed = false;
				}
			}
		}
		else {
			result = "no one to heal";
		}
		
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.Heal;
		character.testQuestRequirements(gameHandler.getMainFrame(),params);
	}
	public static RealmComponentOptionChooser alertChooser(CharacterWrapper character, RealmGameHandler gameHandler) {
		RealmComponentOptionChooser chooser = null;
		// Player chooses from all inactive weapons and spell chits
		ArrayList<ChitComponent> alertChoices = new ArrayList<>();
		Collection<CharacterActionChitComponent> c = character.getActiveChits();
		for (CharacterActionChitComponent chit : c) {
			if (chit.isMagic() || chit.isFightAlert()) {
				alertChoices.add(chit);
			}
		}
		ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
		if (weapons!=null && !weapons.isEmpty()) {
			if (character.affectedByKey(Constants.DUAL_WIELDING_ALERT)) {
				for (WeaponChitComponent weapon : weapons) {
					alertChoices.add(weapon);
				}
			}
			else {
				alertChoices.add(weapons.get(0));
			}
		}
		if (alertChoices.size()<1) {
			return null;
		}
		
		chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Alert which?",true);
		int keyN = 0;
		for (RealmComponent rc : alertChoices) {
			if (rc.isWeapon()) {
				// Add both sides of weapon, if any
				WeaponChitComponent weapon = (WeaponChitComponent)rc;
				String key = "a"+(keyN++);
				chooser.addOption(key,"Alert");
				chooser.addRealmComponentToOption(key,weapon,weapon.isAlerted()?RealmComponentOptionChooser.DisplayOption.Normal:RealmComponentOptionChooser.DisplayOption.Flipside);
				key = "u"+(keyN++);
				chooser.addOption(key,"Unalert");
				chooser.addRealmComponentToOption(key,weapon,weapon.isAlerted()?RealmComponentOptionChooser.DisplayOption.Flipside:RealmComponentOptionChooser.DisplayOption.Normal);
			}
			else {
				String key = "k"+(keyN++);
				chooser.addOption(key,"Alert");
				chooser.addRealmComponentToOption(key,rc);
			}
		}
		
		return chooser;
	}
	public static RealmComponent alertChosenObject(CharacterWrapper character, RealmComponentOptionChooser chooser) {
		RealmComponent rc = chooser.getFirstSelectedComponent();
		if (rc.isWeapon()) {
			boolean alert = chooser.getSelectedOptionKey().startsWith("a");
			((WeaponChitComponent)rc).setAlerted(alert);
		}
		else {
			CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
			if (chit.isFightAlert()) {
			chit.makeFatigued(); // fatigues instantly
				character.getGameObject().setThisAttribute(Constants.ENHANCED_VULNERABILITY,chit.getFightAlertVulnerability());
			}
			else {
				chit.makeAlerted();
			}
		}
		return rc;
	}
	private void doAlertAction() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		// Make sure followers get an alert too!
		if (hostPrefs.hasPref(Constants.OPT_FOLLOWERS_ACTIONS_DURING_GUIDES_PHASE)) {
			// Make sure followers get a alert too!
			for (CharacterWrapper follower : character.getActionFollowers()) {
				if (!follower.hasMesmerizeEffect(Constants.TIRED)) {
					follower.setFollowAlerts(follower.getFollowAlerts()+1);
				}
			}
		} else {
			for (CharacterWrapper follower : character.getActionFollowers()) {
				follower.addCurrentAction(DayAction.ALERT_ACTION.getCode());
				follower.addCurrentActionTypeCode(actionTypeCode);
				follower.addCurrentActionValid(true);
			}
		}
		if (character.hasMesmerizeEffect(Constants.TIRED)) {
			result = "Cannot ALERT while tired.";
			return;
		}
		
		RealmComponentOptionChooser chooser = alertChooser(character, gameHandler);
		
		if (chooser!=null) {
			chooser.setVisible(true);
			if (chooser.getSelectedText()!=null) {
				RealmComponent rc = alertChosenObject(character, chooser);
				
				result = "alerted "+rc.getGameObject().getName();				
				
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Alert;
				character.testQuestRequirements(gameHandler.getMainFrame(),params);
				
				gameHandler.updateCharacterFrames();
			}
			else {
				if (isFollowing) {
					int ret = JOptionPane.showConfirmDialog(
							gameHandler.getMainFrame(),
							"Do you want to skip the ALERT action?",
							"ALERT is optional for followers",
							JOptionPane.YES_NO_OPTION);
					if (ret==JOptionPane.YES_OPTION) {
						cancelled = true;
						return;
					}
				}
				completed = false;
				return;
			}
		}
		else {
			result = "nothing to alert";
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Alert;
			character.testQuestRequirements(gameHandler.getMainFrame(),params);
		}
	}
	private void doRepairAction() {
		ArrayList<ArmorChitComponent> damagedArmor = new ArrayList<>();
		for(GameObject go:character.getInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isArmor()) {
				ArmorChitComponent armor = (ArmorChitComponent)rc;
				if (armor.isDamaged()) {
					damagedArmor.add(armor);
				}
			}
		}
		
		if (damagedArmor.size()>0) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Repair which?",true);
			chooser.addRealmComponents(damagedArmor,false);
			chooser.setVisible(true);
			if (chooser.getSelectedText()!=null) {
				ArmorChitComponent armor = (ArmorChitComponent)chooser.getFirstSelectedComponent();
				armor.setIntact(true);
				result = "repaired "+armor.getGameObject().getName();
				gameHandler.updateCharacterFrames();
			}
		}
		else {
			result = "nothing to repair";
		}
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.Repair;
		character.testQuestRequirements(gameHandler.getMainFrame(),params);
	}
	private void doHireAction() {
		// Player chooses from native groups, and then gets to bid on lowest ranked native
		
		// hire_type=single or group
		// If "group", hire highest number first, down to HQ last (HQ is essentially 0)
		// HIRE same as TRADE, only the merchandise is the natives themselves
		// Term of hire is fourteen days, or until the character is killed
		TileLocation tl = character.getCurrentLocation();
		ArrayList<RealmComponent> hireables = ClearingUtility.getAllHireables(character,tl.clearing);
		HashLists<String, RealmComponent> hash = RealmUtility.hashNativesByGroupName(hireables);
		if (hash.size()>0) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Hire which?",true);
			chooser.setButtonTextPosition(SwingConstants.CENTER,SwingConstants.BOTTOM);
			//chooser.setForceColumns(1);
			chooser.setFillByRow(false);
			chooser.setSortBiggestFirst(true);
			for (String groupName : hash.keySet()) {
				ArrayList<RealmComponent> list = hash.getList(groupName);
				Collections.sort(list,new Comparator<RealmComponent>() {
					public int compare(RealmComponent o1,RealmComponent o2) {
						String rs1 = o1.getGameObject().getThisAttribute("rank");
						if (rs1==null) rs1 = "0";
						Integer rank1 = "HQ".equals(rs1)?Integer.valueOf(0):Integer.parseInt(rs1);
						
						String rs2 = o2.getGameObject().getThisAttribute("rank");
						if (rs2==null) rs2 = "0";
						Integer rank2 = "HQ".equals(rs2)?Integer.valueOf(0):Integer.parseInt(rs2);
						
						return rank1.compareTo(rank2);
					}
				});
				int basePrice;
				ChitComponent last = (ChitComponent)list.get(list.size()-1);
				if ("group".equals(last.getGameObject().getThisAttribute("hire_type")) || last.isMonster()) {
					// Add the whole group if hire_type is group, or we are referring to monsters
					String option = chooser.generateOption(StringUtilities.capitalize(groupName));
					basePrice = 0;
					int rehire = 0;
					int newhire = 0;
					for (RealmComponent rc : list) {
						if (rc.getOwnerId()==null) newhire++; else rehire++;
						chooser.addRealmComponentToOption(option,rc);
						basePrice += rc.getGameObject().getThisInt("base_price");
					}
					String prefix = "Hire ";
					if (rehire>0) {
						if (newhire==0) {
							prefix = "Rehire ";
						}
						else {
							prefix = "Hire (some rehire) ";
						}
					}
					String postfix = "";
					if (last.isMonster() && list.size()>1) {
						postfix = "s";
					}
					if (character.hasActiveInventoryThisKeyAndValue(Constants.HALF_PRICE,groupName)) {
						basePrice >>= 1;
					}
					chooser.addOption(option,prefix+StringUtilities.capitalize(groupName)+postfix+" (base: "+basePrice+" gold)");
				}
				else { // hire_type=="single" or a traveler
					// Add only the last unhired member of the group
					// Add all the hired natives (one at a time)
					last = null;
					for (RealmComponent rc : list) {
						if (rc.getOwnerId()==null) {
							last = (ChitComponent)rc;
						}
						else {
							// Re-hire
							String option = chooser.generateOption(StringUtilities.capitalize(groupName));
							chooser.addRealmComponentToOption(option,rc);
							basePrice = rc.getGameObject().getThisInt("base_price");
							chooser.addOption(option,"Rehire "+StringUtilities.capitalize(groupName)+" (base: "+basePrice+" gold)");
						}
					}
					if (last!=null) {
						// New hire single
						String option = chooser.generateOption(StringUtilities.capitalize(groupName));
						chooser.addRealmComponentToOption(option,last);
						if (last.getGameObject().hasThisAttribute(Constants.HIRE_WITH_CHIT)) {
							String chitAmount = last.getGameObject().getThisAttribute(Constants.HIRE_WITH_CHIT);
							if (chitAmount == "") chitAmount = "1";
							chooser.addOption(option,"Hire "+StringUtilities.capitalize(groupName)+" (price: "+chitAmount+" chit(s))");
						}
						else {
							basePrice = last.getGameObject().getThisInt("base_price");
							if (character.hasActiveInventoryThisKeyAndValue(Constants.HALF_PRICE,groupName)) {
								basePrice >>= 1;
							}
							chooser.addOption(option,"Hire "+StringUtilities.capitalize(groupName)+" (base: "+basePrice+" gold)");
						}
					}
				}
			}
			
			chooser.addOption("none","Cancel Hire");
			chooser.setVisible(true);
			String selText = chooser.getSelectedText();
			if (selText!=null) {
				if ("Cancel Hire".equals(selText)) {
					result = "Cancelled Hire";
					return;
				}
				ArrayList<RealmComponent> list = new ArrayList<>(chooser.getSelectedComponents());
				ChitComponent last = (ChitComponent)list.get(list.size()-1);
				
				boolean credit = false;
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
				if (hostPrefs.hasPref(Constants.SR_ADV_CREDIT) && !last.isTraveler() && character.getFame()>0) {
					int ret = JOptionPane.showConfirmDialog(
							new JFrame(),
							"Do you want to hire on credit (pay with fame)?",
							"Hire on Credit",
							JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,character.getIcon());
					if (ret == JOptionPane.YES_OPTION) {
						credit = true;
					}
				}
				
				// Now we have the group to hire.  Need to do the Meeting table...
				realmTable = Meeting.createMeetingTable(
						gameHandler.getMainFrame(),
						character,
						character.getCurrentLocation(),
						last,
						null,
						list,
						last.isTraveler()?RelationshipType.NEUTRAL:RelationshipType.ALLY);
				((Meeting)realmTable).setSpecificAction("Hire");
				if (credit) {
					((Meeting)realmTable).setCreditFame();
				}
				
				if (last.isTraveler()) {
					// No need to roll for travelers
					((Meeting)realmTable).hiringNatives(character,1);
				}
				else {
					handleTable();
				}
				
				if (!negate) {
					QuestRequirementParams params = new QuestRequirementParams();
					params.actionType = CharacterActionType.Hire;
					if (realmTable!=null && ((Meeting)realmTable).getSucessfullyHiredGroup()!=null) {
						for (RealmComponent hired : ((Meeting)realmTable).getSucessfullyHiredGroup()) {
							params.objectList.add(hired.getGameObject());
						}
					}
					character.testQuestRequirements(gameHandler.getMainFrame(),params);
				}
			}
			else {
				completed = false;
			}
		}
		else {
			result = "Nobody to hire";
		}
		
		if (!negate) {
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Hire;
			character.testQuestRequirements(gameHandler.getMainFrame(),params);
		}
	}
	public static TileLocation getTargetClearingForSpellAction(CharacterWrapper character, RealmGameHandler gameHandler) {
		TileLocation targetClearing = character.getCurrentLocation();
		if (character.getPeerAny()) {
			CenteredMapView.getSingleton().setMarkClearingAlertText("Enchant in which clearing?");
			CenteredMapView.getSingleton().markAllClearings(true);
			TileLocationChooser chooser = new TileLocationChooser(gameHandler.getMainFrame(),CenteredMapView.getSingleton(),targetClearing);
			chooser.setVisible(true);
			CenteredMapView.getSingleton().markAllClearings(false);
			targetClearing = chooser.getSelectedLocation();
		}
		return targetClearing; 
	}
	private void doSpellAction() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		if (hostPrefs.hasPref(Constants.SR_FOLLOWERS_ENCHANTING_ACTION)) {
			if (hostPrefs.hasPref(Constants.OPT_FOLLOWERS_ACTIONS_DURING_GUIDES_PHASE)) {
				// Make sure followers get a alert too!
				for (CharacterWrapper follower : character.getActionFollowers()) {
					if (!character.hasMesmerizeEffect(Constants.SAPPED)) {
						follower.setFollowSpellActions(follower.getFollowSpellActions()+1);
					}
				}
			} else {
				for (CharacterWrapper follower : character.getActionFollowers()) {
					follower.addCurrentAction(DayAction.SPELL_ACTION.getCode());
					follower.addCurrentActionTypeCode(actionTypeCode);
					follower.addCurrentActionValid(true);
				}
			}
		}
		if (character.hasMesmerizeEffect(Constants.SAPPED)) {
			result = "Cannot ENCHANT while sapped.";
			return;
		}
		
		TileLocation targetClearing = getTargetClearingForSpellAction(character, gameHandler);
		doSpellAction(character.getInfiniteColorSources(),targetClearing);
	}
	public static RealmComponentOptionChooser enchantChooser(CharacterWrapper character, RealmGameHandler gameHandler, TileLocation targetClearing, Collection<ColorMagic> colorMagicSources) {
		// SPX actions are ignored.  Need to ask player if they want to enchant a chit, or a tile.
		// The tile option would only be available if the conditions are right (right color/invocation combination available)		
		ArrayList<MagicChit> enchantable = new ArrayList<>();
		
		// Chits
		ArrayList<CharacterActionChitComponent> enchantableChits = character.getEnchantableChits();
		Collections.sort(enchantableChits);
		enchantable.addAll(enchantableChits);
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameHandler.getClient().getGameData());
		if (hostPrefs.hasPref(Constants.OPT_ENHANCED_ARTIFACTS) || character.affectedByKey(Constants.ENHANCED_ARTIFACTS)) {
			// Enchantable Artifacts and Books
			for(GameObject item:character.getActiveInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(item);
				if (rc.isMagicChit()) {
					MagicChit mc = (MagicChit)rc;
					if (mc.isEnchantable()) {
						enchantable.add(mc);
					}
				}
			}
		}
	
		ArrayList<RealmComponent[]> tileEnchantableSets = new ArrayList<>(); // CharacterChitActionComponent[] set
		// Determine if any of the color magic (infinite sources first) are available to enchant the tile
		for (MagicChit chit:enchantable) {
			for (ColorMagic infiniteSource : colorMagicSources) {
				if (chit.compatibleWith(infiniteSource)) {
					// Create a set of one (no need to use up your own color magic when there is an infinite source!)
					RealmComponent[] set = new RealmComponent[2];
					set[0] = (RealmComponent)chit;
					set[1] = targetClearing.tile;
					tileEnchantableSets.add(set);
					break; // no need to keep searching infinite sources!  Any one is good enough.
				}
			}
		}
		// check own color chits (player may not want to use infinite source if it uses the wrong chit!)
		ArrayList<MagicChit> colorMagicChits = new ArrayList<>();
		if ((!hostPrefs.hasPref(Constants.FE_STEEL_AGAINST_MAGIC) && !character.affectedByKey(Constants.STAFF_RESTRICTED_SPELLCASTING)) || character.hasOnlyStaffAsActivatedWeapon()) {
			colorMagicChits.addAll(character.getColorChits());
		}
		if (hostPrefs.hasPref(Constants.OPT_ENHANCED_ARTIFACTS)) {
			// Artifacts and Books enchanted into color
			for(GameObject item:character.getActiveInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(item);
				if (rc.isMagicChit()) {
					MagicChit mc = (MagicChit)rc;
					if (mc.isColor()) {
						colorMagicChits.add(mc);
					}
				}
			}
		}
		for (MagicChit chit:enchantable) {
			for (MagicChit colorChit:colorMagicChits) {
				ColorMagic consumableSource = colorChit.getColorMagic();
				if (chit.compatibleWith(consumableSource)) {
					// Create a set of one (no need to use up your own color magic when there is an infinite source!)
					RealmComponent[] set = new RealmComponent[3];
					set[0] = (RealmComponent)chit;
					set[1] = (RealmComponent)colorChit;
					set[2] = targetClearing.tile;
					tileEnchantableSets.add(set);
					// find all possible combinations!
				}
			}
		}		
		for (GameObject treasure:character.getActiveInventory()) {
			if (treasure.hasThisAttribute(Constants.RING) && !treasure.hasThisAttribute(Constants.RING_USED)) {
				if (treasure.hasThisAttribute(SpellWrapper.INCANTATION_TIE)) {
					continue; // tied up treasures cannot be used again
				}
				MagicChit treasureChit = (MagicChit)RealmComponent.getRealmComponent(treasure);
				if (!treasureChit.isColor()) {
					for (ColorMagic infiniteSource : colorMagicSources) {
						if (treasureChit.compatibleWith(infiniteSource)) {
							// Create a set of one (no need to use up your own color magic when there is an infinite source!)
							RealmComponent[] set = new RealmComponent[2];
							set[0] = (RealmComponent)treasureChit;
							set[1] = targetClearing.tile;
							tileEnchantableSets.add(set);
							break; // no need to keep searching infinite sources!  Any one is good enough.
						}
					}
					for (MagicChit colorChit:colorMagicChits) {
						ColorMagic consumableSource = colorChit.getColorMagic();
						if (treasureChit.compatibleWith(consumableSource)) {
							RealmComponent[] set = new RealmComponent[3];
							set[0] = RealmComponent.getRealmComponent(treasure);
							set[1] = (RealmComponent)colorChit;
							set[2] = targetClearing.tile;
							tileEnchantableSets.add(set);
						}
					}
				}
			}
		}
			
		RealmComponentOptionChooser compChooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Enchant which?",true);
		int keyN = 0;
		for (MagicChit magicChit : enchantable) {
			RealmComponent chit = (RealmComponent)magicChit;
			String key = "k"+(keyN++);
			if (chit.isActionChit()) {
				compChooser.addOption(key,"MAGIC Chit");
			}
			else {
				compChooser.addOption(key,"Artifact/Book");
			}
			compChooser.addRealmComponentToOption(key,chit);
		}
		for (RealmComponent[] chit : tileEnchantableSets) {
			String key = "k"+(keyN++);
			compChooser.addOption(key,"Tile");
			for (int n=0;n<chit.length;n++) {
				compChooser.addRealmComponentToOption(key,chit[n]);
			}
		}
		return compChooser;
	}
	public static String enchantTileOrChit(CharacterWrapper character, RealmComponentOptionChooser compChooser, String text, TileLocation targetClearing, RealmGameHandler gameHandler) {
		String result = "";
		if ("Tile".equals(text)) {
			// enchant a tile
			TileComponent tile = targetClearing.tile;
			tile.flip();
			result = "enchanted "+tile.getTileName();
			// fatigue the chit(s) used to do it
			Collection<RealmComponent> chits = compChooser.getSelectedComponents();
			for (RealmComponent rc : chits) {
				if (rc.isMagicChit() && !character.affectedByKey(Constants.TALISMAN)) {
					MagicChit chit = (MagicChit)rc;
					if (chit.isColor()) { // Only fatigue the color chit - not the incantation
						chit.makeFatigued();
						RealmUtility.reportChitFatigue(character,chit,"Fatigued color chit: ");
					}
				}
				if (rc.getGameObject().hasThisAttribute(Constants.RING)) {
					rc.getGameObject().setThisAttribute(Constants.RING_USED);
				}
			}
			gameHandler.updateCharacterFrames();
			gameHandler.broadcastMapReplot();
			
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Enchant;
			params.actionName = RealmComponent.TILE;
			params.objectList.add(tile.getGameObject());
			character.testQuestRequirements(gameHandler.getMainFrame(), params);
		}
		else {
			// enchant a chit
			MagicChit chit = (MagicChit)compChooser.getFirstSelectedComponent();
			if (chit!=null) {
				RealmUtility.enchantChit(gameHandler.getMainFrame(),chit);
				
				result = "enchanted "+chit.getGameObject().getName();
				gameHandler.updateCharacterFrames();
				
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Enchant;
				params.actionName = "chit";
				character.testQuestRequirements(gameHandler.getMainFrame(),params);
			}// this shouldn't happen
		}
		return result;
	}
	private void doSpellAction(Collection<ColorMagic> colorMagicSources,TileLocation targetClearing) {
		RealmComponentOptionChooser compChooser = enchantChooser(character, gameHandler, targetClearing, colorMagicSources);
		if (compChooser.hasOptions()) {
			compChooser.setVisible(true);
			String text = compChooser.getSelectedText();
			if (text!=null) {
				result = enchantTileOrChit(character, compChooser, text, targetClearing, gameHandler);
			}
			else {
				completed = false;
				return;
			}
		}
		else {
			result = "nothing to enchant";
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Enchant;
			character.testQuestRequirements(gameHandler.getMainFrame(), params);
		}
	}
	private void doEnhancedPeerAction() {
		if (character.isMinion()) {
			location = character.getCurrentLocation();
		}
		// Player peers into clearing
		if (location.clearing != null) {
			// clearing might NOT be on the same side, if a tile flipped somewhere, so update it here
			location.clearing = location.clearing.correctSide();
			realmTable = new EnhancedPeer(gameHandler.getMainFrame(),location.clearing);
			handleTable();
		}
	}
	private void doFlyAction() {
		TileLocation current = character.getCurrentLocation();
		
		// Before starting, make sure that you aren't "lost in the maze" (expansion 1)
		RealmComponent discoverToLeave = ClearingUtility.findDiscoverToLeaveComponent(current,character);
		if (discoverToLeave!=null) {
			JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"You are trapped in the "+discoverToLeave.getGameObject().getName()+"! MOVE is cancelled.",
					"Trapped!",JOptionPane.PLAIN_MESSAGE,discoverToLeave.getFaceUpIcon());
			cancelled = true;
			return;
		}
		
		character.checkForLostInTheMaze(current); // Lost in the Maze rule for Super Realm
		if (character.getGameObject().hasThisAttribute(Constants.LOST_IN_THE_MAZE) && !character.affectedByKey(Constants.REALM_MAP)) {
			doMoveAction();
			return;
		}
		
		boolean violentWinds = false;
		if (current.tile.getGameObject().hasThisAttribute(Constants.EVENT_VIOLENT_WINDS)) {
			violentWinds = true;
			if (!current.isFlying() || character.hasDoneActionsToday()) {
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"You cannot fly as Violent Winds are blowing! MOVE is cancelled",
						"Violent Winds Event",JOptionPane.PLAIN_MESSAGE);
				cancelled = true;
				return;
			}
		}
		
		// First, make sure Flying is a possibility - otherwise BLOCK character..? See Rule 47.2
		ArrayList<StrengthChit> flyStrengthChits = character.getFlyStrengthChits(true);
		boolean startedBetweenTiles = current.isBetweenTiles();
		if (current.isFlying() && (startedBetweenTiles || current.isTileOnly())) {
			// Must be able to fly
			flyStrengthChits.add(0,new StrengthChit(null,new Strength("T"),new Speed(0))); // this will ALWAYS be the strongest chit, so add it first
		}
		if (flyStrengthChits.isEmpty()) {
			result = "Unable to fly.";
			cancelled = true;
			return;
		}
		
		// Strip out any chits that aren't strong enough to support character
		Strength vul = character.getVulnerability();
		ArrayList<StrengthChit> strongEnough = new ArrayList<>();
		for (StrengthChit sc:flyStrengthChits) {
			if (sc.getStrength().strongerOrEqualTo(vul)) {
				strongEnough.add(sc);
			}
		}
		
		// Make sure Character is not too heavy
		if (strongEnough.isEmpty()) {
			result = "Too heavy to fly.";
			cancelled = true;
			return;
		}
		
		flyStrengthChits = strongEnough;
		
		// Make sure intended target tile for flying is possible (might not be if a previously recorded move is invalid!)
		ArrayList<TileComponent> allAvailableTiles = new ArrayList<>(current.tile.getAllAdjacentTiles());
		allAvailableTiles.add(current.tile);
		if (!allAvailableTiles.contains(location.tile)) {
			result = "Target tile too far away.";
			cancelled = true;
			return;
		}
		
		// Choose a fly chit (if necessary)
		StrengthChit flyStrengthChit = null;
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Choose a FLY chit:",true);
		for (StrengthChit sc:flyStrengthChits) {
			if (sc.getGameObject()==null) {
				flyStrengthChit = sc;
				break;
			}
			chooser.addRealmComponent(sc.getRealmComponent());
		}
		Fly fly = null;
		if (flyStrengthChit==null) {
			chooser.setVisible(true);
			RealmComponent rc = chooser.getFirstSelectedComponent();
			if (rc==null) {
				// cancelled allows you to back out
				completed = false;
				return;
			}
			fly = new Fly(rc);
		}
		else {
			fly = new Fly(flyStrengthChit);
		}
		
		// Next, drop all items heavier than the fly chit, AND any horses, regardless of weight
		if (!current.isBetweenTiles()) {
			ArrayList<GameObject> toDrop = RealmUtility.dropNonFlyableStuff(gameHandler.getMainFrame(),character,fly,current);
			if (toDrop==null) {
				completed = false;
				return;
			}
			for (GameObject item:toDrop) {
				gameHandler.broadcast(character.getGameObject().getName(),item.getName()+" was left behind!");
			}
		}
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
		if (hostPrefs.hasPref(Constants.SR_ADV_GROUNDED_MISSIONS_AND_TASKS)) {
			for (GameObject item:character.getInventory()) {
				if (RealmComponent.getRealmComponent(item).isGoldSpecial()) {
					GoldSpecialChitComponent gs = (GoldSpecialChitComponent)RealmComponent.getRealmComponent(item);
					if (gs.isMission() || gs.isTask()) {
						gs.expireEffect(character);
						character.addFailedGoldSpecial(gs);
						TreasureUtility.doDrop(character,item,gameHandler.getUpdateFrameListener(),false);
						
						QuestRequirementParams qp = new QuestRequirementParams();
						qp.actionName = item.getName();
						qp.actionType = CharacterActionType.AbandonMissionCampaign;
						qp.targetOfSearch = gs.getGameObject();
						character.testQuestRequirements(gameHandler.getMainFrame(),qp);
					}
				}
			}
		}
		
		// Good.  Flying is possible, and will happen.  Check to see if we are using up a FLY chit.
		if (fly!=null) {
			fly.useFly();
		}
		
		// Player is moved to new location
		if (location != null) {
			character.moveToLocation(gameHandler.getMainFrame(),location);
			result = "Flew to tile.";
			if (violentWinds || startedBetweenTiles) {
				character.land(gameHandler.getMainFrame());
				result = "Flew to tile and landed.";
			}
			if (gameHandler.isOption(RealmSpeakOptions.MAP_FOLLOW_CHARACTER)) {
				gameHandler.getInspector().getMap().centerOn(character.getCurrentLocation());
			}
			
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Fly;
			character.testQuestRequirements(gameHandler.getMainFrame(),params);
			
			gameHandler.updateCharacterFrames();
			
			// Character's do not stay hidden when they fly
			if (character.isHidden()) {
				character.setHidden(false);
			}
			
			if (fly!=null&&fly.mustLand()) {
				character.land(gameHandler.getMainFrame());
			}
			
			// Followers shouldn't follow here, unless they can fly, or they are a familiar
			for (CharacterWrapper follower : character.getActionFollowers()) {
				if (follower.mustFly() || follower.isFamiliar()) {
					follower.moveToLocation(gameHandler.getMainFrame(),location);
					if (follower.isHidden()) {
						follower.setHidden(false);
					}
				}
				else {
					character.removeActionFollower(follower,gameHandler.getGame().getMonsterDie(),gameHandler.getGame().getNativeDie());
				}
			}
		}
		else {
			// this should never happen
			throw new IllegalStateException("null location during ActionRow.process!");
		}
	}
	private void doRemoteSpellAction() {
		Collection<ColorMagic> colorSources = character.getInfiniteColorSources();
		colorSources.addAll(location.clearing.getAllSourcesOfColor(true));
		doSpellAction(colorSources,location);
	}
	private void doCacheAction() {
		RealmComponent charRc = RealmComponent.getRealmComponent(character.getGameObject());
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Select cache:",true);
		TileLocation tl = character.getCurrentLocation();
		if (tl.isInClearing()) {
			// Add an option to open a new cache
			chooser.generateOption("New CACHE");
			
			// Add all existing caches in clearing
			for (RealmComponent rc : tl.clearing.getClearingComponents()) {
				if (rc.isCacheChit()) {
					if (rc.getOwner()==charRc) { // Only the individual that created the cache can open it!
						String key = chooser.generateOption("Open");
						chooser.addRealmComponentToOption(key, rc);
					}
				}
			}
			
			chooser.setVisible(true);
			String sel = chooser.getSelectedText();
			if (sel!=null) {
				CacheChitComponent cache;
				if (sel.startsWith("New")) {
					// New CACHE
					GameObject go = character.getGameObject().getGameData().createNewObject();
					int num = character.getNextCacheNumber();
					go.setName(character.getGameObject().getName()+Constants.CACHE_NAME+num);
					go.setThisAttribute(RealmComponent.CACHE_CHIT);
					character.addTreasureLocationDiscovery(go.getName());
					go.setThisAttribute("clearing",tl.clearing.getNumString());
					go.setThisAttribute(RealmComponent.TREASURE_LOCATION,character.getGameObject().getName());
					go.setThisAttribute("cache_number",num);
					go.setThisAttribute("discovery");
					go.setThisAttribute("chit");
					
					// These attributes will enable the cache to appear on the setup card
					HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(go.getGameData());
					go.setThisAttribute("ts_section","zcache"); // z so that it sorts to the end
					go.setThisAttribute("ts_color","gold");
					go.setThisKeyVals(hostPrefs.getGameKeyVals());
					go.setThisAttribute("ts_cansee",character.getPlayerName());
					if (character.getGameObject().hasThisAttribute(Constants.BOARD_NUMBER)) {
						// Might as well put the B character caches on the B setup card, and so on.
						go.setThisAttribute(Constants.BOARD_NUMBER,character.getGameObject().getThisAttribute(Constants.BOARD_NUMBER));
					}
					
					cache = (CacheChitComponent)RealmComponent.getRealmComponent(go);
					cache.setOwner(RealmComponent.getRealmComponent(character.getGameObject()));
					cache.setFaceUp();
					tl.clearing.add(go,null);
				}
				else {
					// Existing CACHE
					cache = (CacheChitComponent)chooser.getFirstSelectedComponent();
				}
				
				// Trade with CACHE
				CharacterWrapper cacheCharacter = new CharacterWrapper(cache.getGameObject());
				CacheTransferDialog transferDialog = new CacheTransferDialog(
													gameHandler.getMainFrame(),
													character,
													cacheCharacter,
													gameHandler.getUpdateFrameListener());
				transferDialog.setVisible(true);
				
				// If cache is empty, delete it
				cache.testEmpty();
				RealmUtility.sortGameObjectsHold(cache.getGameObject(),false);
				
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Cache;
				character.testQuestRequirements(gameHandler.getMainFrame(), params);
			}
			else {
				completed = false;
			}
		}
		else {
			result = "Can only CACHE in a clearing!";
		}
	}
	/**
	 * @return Returns the newAction.
	 */
	public ActionRow getNewAction() {
		return newAction;
	}
	/**
	 * @param result The result to set.
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return Returns the spawned.
	 */
	public boolean isSpawned() {
		return spawned;
	}
	/**
	 * @param spawned The spawned to set.
	 */
	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}
	public void makeBlankPhase(String reason) {
		blankReason = reason;
	}
	public boolean isBlankPhase() {
		return blankReason!=null;
	}
	public void makeInvalidPhase() {
		invalid = true;
	}
	public boolean isInvalidPhase() {
		return invalid;
	}
	public void setInvalidPlannedPhase() {
		invalidPlanned = true;
	}
	public boolean isInvalidPlannedPhase() {
		return invalidPlanned;
	}
	public boolean willMoveToDarkCave() {
		return action.startsWith("M") && location.isInClearing() && location.clearing.isCave() && !location.clearing.isLighted();
	}
	/**
	 * @return Returns the isFollowing.
	 */
	public boolean getIsFollowing() {
		return isFollowing;
	}

	public void setPonyLock(boolean ponyLock) {
		this.ponyLock = ponyLock;
	}
	public boolean isPonyLock() {
		return ponyLock;
	}
	
	public boolean isFollow() {
		ActionId id = CharacterWrapper.getIdForAction(action);
		return (id==ActionId.Follow);
	}
	
	public static void main(String[] args) {
		RealmLoader loader = new RealmLoader();
		CardComponent em = (CardComponent)RealmComponent.getRealmComponent(loader.getData().getGameObjectByName("Enchanted Meadow"));
		ChitComponent tr = (ChitComponent)RealmComponent.getRealmComponent(loader.getData().getGameObjectByName("Guard 1"));
		em.setFaceUp();
		ButtonOptionDialog chooser = new ButtonOptionDialog(new JFrame(),null,"Foobar!","Hey there!");
		chooser.addSelectionObject("Test");
		IconGroup group = new IconGroup(IconGroup.HORIZONTAL,2);
		group.addIcon(em.getMediumIcon());
		group.addIcon(tr.getMediumIcon());
		chooser.setSelectionObjectIcon("Test",group);
		chooser.setVisible(true);
		System.exit(0);
	}
}