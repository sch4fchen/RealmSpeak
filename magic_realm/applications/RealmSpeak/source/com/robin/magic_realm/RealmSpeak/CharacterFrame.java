package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.table.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class CharacterFrame extends RealmSpeakInternalFrame implements ICharacterFrame {

	private static final Font EMBOSS_FONT = new Font("Dialog",Font.ITALIC|Font.BOLD,28);
	private static final Color EMBOSS_COLOR = new Color(0,0,0,20);
	
	protected long lastVersion;

	protected RealmGameHandler gameHandler;
	protected ArrayList<RealmComponent> blockees;

	protected JPanel tokenPanel;
	protected JLabel charLabel;
	protected MoveMarker mountainMoveIcon;
	protected CharacterWrapper character;
	protected HostPrefWrapper hostPrefs;
	protected JButton showCharCardButton;
	protected Badge familiarBadge;
	
	protected CharacterChatPanel chatPanel;
	
	// These buttons are lit, and only displayed one at a time
	protected SingleButton vpSetupButton;
	protected SingleButton vpDeductButton;
	protected SingleButton chooseQuestButton;
	protected SingleButton advancementButton;
	protected SingleButton gsPlacementButton;
	protected SingleButton enchantButton;
	protected SingleButton alertButton;
	protected SingleButton restButton;
	protected SingleButton fatigueButton;
	protected SingleButton woundButton;
	protected SingleButton energizeChoiceButton;
	protected SingleButton playColorChitNowButton;
	protected SingleButton blockNowButton;
	protected SingleButton prePhaseActivityDoneButton;
	protected SingleButton showPrePhaseDialogButton;
	protected SingleButton showPostPhaseDialogButton;
	private JDialog prePhaseActivityDialog = null;
	private JDialog postPhaseActivityDialog = null;
	private final ArrayList<ChitSelection> currentChitSelections = new ArrayList<>();
	private JCheckBox stopFollowingCheckbox = null;
	private Map<JToggleButton, RealmComponent> blockingButtonMap = null;

	private static class ChitSelection {
		final MagicChit chit;
		final JCheckBox checkbox;
		final ArrayList<JToggleButton> toggles;
		final Map<JToggleButton, SpellWrapper> spellByButton; // null value = no target
		ChitSelection(MagicChit chit, JCheckBox checkbox, ArrayList<JToggleButton> toggles, Map<JToggleButton, SpellWrapper> spellByButton) {
			this.chit = chit; this.checkbox = checkbox; this.toggles = toggles; this.spellByButton = spellByButton;
		}
		boolean isPlayed() { return checkbox.isSelected(); }
		SpellWrapper getSelectedSpell() {
			for (JToggleButton btn : toggles) { if (btn.isSelected()) return spellByButton.get(btn); }
			return null;
		}
	}
	private boolean prePhaseDialogShowing = false;
	private boolean postPhaseDialogShowing = false;
	protected SingleButton doneTradingButton;
	protected SingleButton stopFollowingButton;
	protected SingleButton approveInventoryButton;
	protected SingleButton gsPickupButton; // This is the only optional SingleButton
	protected SingleButton guildBenefitButton; // This is the only optional SingleButton
	protected SingleButtonManager singleButtonManager;
	
	protected JButton viewChitsButton;
	protected JToggleButton reactButton;
	protected JButton shoutButton;
	protected JButton unhideButton;
	protected JButton tradeButton;
	protected JCheckBox dailyCombatCheckbox;
	protected JCheckBox dayEndRearrangmentCheckbox;
	protected JCheckBox keepReactingCheckbox;
	protected JLabel characterVulnerability;

	protected JPanel characterDetailPanel;
	protected JLabel recordedFameLabel;
	protected JLabel recordedNotorietyLabel;
	protected JLabel recordedGoldLabel;
	protected Box currentBadgesBox;
	protected Box availableColorMagicBox;
	protected Box activeCursesBox;
	protected PointBar developmentProgress;
	
	protected PhaseManagerIcon phaseManagerIcon;
	protected JLabel phaseManagerLabel;
	protected JLabel hiddenEnemiesLabel;
	protected JLabel blockedLabel;

	// Tab panels
	protected JTabbedPane tabs;
	protected CharacterActionPanel actionPanel;
	protected CharacterChitPanel chitPanel;
	protected CharacterInventoryPanel inventoryPanel;
	protected CharacterSpellsPanel spellsPanel;
	protected CharacterDiscoveriesPanel discoveriesPanel;
	protected CharacterRelationshipPanel relationshipPanel;
	protected CharacterHirelingPanel hirelingPanel;
	protected CharacterVictoryPanel victoryPanel;
	protected CharacterNotesPanel notesPanel;
	protected CharacterExpansionOnePanel expansionOnePanel;
	protected CharacterQuestPanel questPanel;
	protected RealmTurnPanel turnPanel;
	protected GameOverPanel gameOverPanel;

	/**
	 * Sole constructor
	 */
	public CharacterFrame(RealmGameHandler handler, CharacterWrapper character, int iconSize) {
		super(character.getCharacterName(), true, false, true, true);
		this.gameHandler = handler;
		this.character = character;
		turnPanel = null;
		lastVersion = -1; // forces a "first" update
		hostPrefs = HostPrefWrapper.findHostPrefs(handler.getClient().getGameData());
		initComponents(iconSize);
	}
	public void cleanup() {
		getChatPanel().cleanup();
	}
	public CharacterWrapper getCharacter() {
		return character;
	}
	private void updateCharLabel() {
		String name;
		if (character.isCharacter()) {
			name = character.getCharacterLevelName();
		}
		else {
			name = character.getGameObject().getName();
		}
		if (character.isSleep()) {
			name = name + " (Asleep)";
		}
		charLabel.setText(name);
	}
	private void updateBadges() {
		if (character.isCharacter()) {
			currentBadgesBox.removeAll();
			
			Badge guildBadge = Badge.getGuildBadge(character);
			if (guildBadge!=null) {
				currentBadgesBox.add(guildBadge);
			}
			
			ArrayList<String> all = character.getLevelAdvantages();
			all.addAll(character.getOptionalLevelAdvantages());
			for (String val:all) {
				currentBadgesBox.add(Box.createHorizontalStrut(2));
				Badge badge = Badge.getBadge(character,val);
				if (familiarBadge==null && badge.isFamiliar()) {
					familiarBadge = badge;
					setFamiliarBadgeActive(familiarBadge.isActive()); // init
					familiarBadge.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent ev) {
							setFamiliarBadgeActive(!familiarBadge.isActive()); // toggle
							gameHandler.submitChanges();
						}
					});
				}

				currentBadgesBox.add(badge);
			}
			
			revalidate();
			repaint();
		}
	}
	private void setFamiliarBadgeActive(boolean active) {
		familiarBadge.setActive(active);
		character.setActiveFamiliar(active);
	}
	private void updateAvailableColorMagic() {
		Collection<ColorMagic> colors = character.getChitColorSources();
		if (hostPrefs.hasPref(Constants.OPT_ENHANCED_ARTIFACTS) || character.affectedByKey(Constants.ENHANCED_ARTIFACTS)) {
			colors.addAll(character.getEnchantedArtifactColorSources());
		}
		colors.addAll(character.getInfiniteColorSources());

		// Update the GUI
		availableColorMagicBox.removeAll();
		for (ColorMagic cm : colors) {
			JLabel label = new JLabel();
			label.setIcon(cm.getIcon());
			availableColorMagicBox.add(label);
		}
		availableColorMagicBox.add(Box.createHorizontalGlue());
	}
	
	protected void updateActiveCurses() {
		if (character.isCharacter()) {
			activeCursesBox.removeAll();
			updateActiveCurses(character,activeCursesBox);
			revalidate();
			repaint();
		}
	}

	public static void updateActiveCurses(CharacterWrapper character,Box box) {
		boolean nullified = character.isNullifiedCurses();
		String postfix = nullified ? " (NULLIFIED)" : "";
		Collection<String> curses = character.getAllCurses();
		if (curses.contains(Constants.EYEMIST)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/eyemist"));
			label.setEnabled(!nullified);
			label.setToolTipText("Eyemist - Cannot SEARCH" + postfix);
			box.add(label);
		}
		if (curses.contains(Constants.SQUEAK)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/squeak"));
			label.setEnabled(!nullified);
			label.setToolTipText("Squeak - Cannot HIDE" + postfix);
			box.add(label);
		}
		if (curses.contains(Constants.WITHER)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/wither"));
			label.setEnabled(!nullified);
			label.setToolTipText("Wither - Cannot have active effort chits" + postfix);
			box.add(label);
		}
		if (curses.contains(Constants.ILL_HEALTH)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/illhealth"));
			label.setEnabled(!nullified);
			label.setToolTipText("Ill Health - Cannot REST" + postfix);
			box.add(label);
		}
		if (curses.contains(Constants.ASHES)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/ashes"));
			label.setEnabled(!nullified);
			label.setToolTipText("Ashes - GOLD is worthless" + postfix);
			box.add(label);
		}
		if (curses.contains(Constants.DISGUST)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/disgust"));
			label.setEnabled(!nullified);
			label.setToolTipText("Disgust - FAME is worthless" + postfix);
			box.add(label);
		}
		if (character.getGameObject().hasThisAttribute(Constants.MESMERIZE)) {
			JLabel label = new JLabel(ImageCache.getIcon("curse/illhealth"));
			label.setEnabled(!nullified);
			Collection<String> list = character.getGameObject().getThisAttributeList(Constants.MESMERIZE);
			String effects = String.join(", ", list);
			label.setToolTipText("Mesmerize - "+effects + postfix);
			box.add(label);
		}
	}
	private void doBlockNow() {
		if (blockees!=null && !blockees.isEmpty()) {
			for (RealmComponent target:blockees) {
				handleBlockCharacter(target);
			}
			blockees = null;
			gameHandler.submitChanges();
			gameHandler.updateCharacterList(); // This is necessary so that THIS client is updated
		}
		else if (getCharacter().getNeedsInterruptPhaseDecision()){
			for (RealmComponent target:getCharacter().checkForBlockingState(true,null)) {
				handleBlockCharacter(target);
			}
			getCharacter().setInterruptPhaseDecision(false);
			gameHandler.submitChanges();
			gameHandler.updateCharacterList(); // This is necessary so that THIS client is updated
		}
		updateControls();
	}
	// doPrePhaseActivities() is the shared resolution point for both phasing and non-phasing characters.
	// For non-phasing characters it shows a modal dialog (they can only click OK for now; future content
	// will add stop-following and color-chit-play controls). The main JFrame is brought to front before
	// the dialog so it is never obscured by other OS windows — same reasoning as doPostPhaseActivities().
	// For the phasing character there is no dialog here — they use the non-modal prePhaseActivityDoneButton
	// instead and call this directly on click. After clearing the caller's own flag, if the caller is the
	// phasing character, it walks the clearing and sets the flag on each qualifying non-phasing character,
	// triggering their dialogs in sequence.
	private void bringPhasingCharacterToFront() {
		for (GameObject go : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
			CharacterWrapper cw = new CharacterWrapper(go);
			if (cw.isPlayingTurn()) {
				gameHandler.showCharacterFrame(cw);
				return;
			}
		}
	}

	private void doPrePhaseActivities() {
		if (!getCharacter().isPlayingTurn()) {
			// Non-phasing character: show the non-modal dialog and return. Resolution
			// happens when the player clicks SUBMIT in the dialog.
			showPrePhaseActivityDialog();
			return;
		}
		// Phasing character: clear own flag, then notify each qualifying non-phasing
		// character so their dialog auto-shows on their machine.
		getCharacter().setNeedsPrePhaseActivityDecision(false);
		TileLocation loc = getCharacter().getCurrentLocation();
		if (loc != null && loc.isInClearing()) {
			ArrayList<CharacterWrapper> followers = getCharacter().getActionFollowers();
			for (RealmComponent rc : loc.clearing.getClearingComponents()) {
				if (rc.isPlayerControlledLeader() && !rc.getGameObject().equals(getCharacter().getGameObject())) {
					CharacterWrapper cw = new CharacterWrapper(rc.getGameObject());
					boolean isFollower = followers.stream().anyMatch(f -> f.getGameObject().equals(rc.getGameObject()));
					boolean prePhaseColorChits = !cw.getColorMagicChits().isEmpty()
						&& !hostPrefs.hasPref(Constants.FE_PHASE_END_PLAYING_COLOR_CHIT);
					if (cw.isReacting() && (isFollower || prePhaseColorChits)) {
						cw.setNeedsPrePhaseActivityDecision(true);
					}
				}
			}
		}
		gameHandler.submitChanges();
		gameHandler.updateCharacterFramesWithoutMap();
	}

	private JPanel buildPrePhaseDialogHeader() {
		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
		Font headerFont = header.getFont().deriveFont(Font.BOLD, 16f);
		RealmComponent selfRc = RealmComponent.getRealmComponent(getCharacter().getGameObject());
		header.add(new JLabel(selfRc.getMediumIcon()));
		JLabel forLabel = new JLabel(" -- Phase Start Activities before");
		forLabel.setFont(headerFont);
		header.add(forLabel);
		// Find the phasing character and their current action
		for (GameObject go : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
			CharacterWrapper cw = new CharacterWrapper(go);
			if (cw.isPlayingTurn()) {
				RealmComponent phasingRc = RealmComponent.getRealmComponent(go);
				header.add(new JLabel(phasingRc.getMediumIcon()));
				String nextAction = cw.getNextPendingAction();
				if (nextAction != null) {
					ImageIcon actionIcon = CharacterWrapper.getIconForAction(nextAction);
					if (actionIcon != null) {
						Image scaled = actionIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
						header.add(new JLabel(new ImageIcon(scaled)));
					}
				}
				JLabel actionLabel = new JLabel("Action");
				actionLabel.setFont(headerFont);
				header.add(actionLabel);
				break;
			}
		}
		return header;
	}

	private boolean isFollowerOfPhasingChar() {
		for (GameObject go : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
			CharacterWrapper cw = new CharacterWrapper(go);
			if (cw.isPlayingTurn()) {
				for (CharacterWrapper follower : cw.getActionFollowers()) {
					if (follower.getGameObject().equals(getCharacter().getGameObject())) return true;
				}
				break;
			}
		}
		return false;
	}

	private JPanel buildStopFollowingPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
		RealmComponent selfRc = RealmComponent.getRealmComponent(getCharacter().getGameObject());
		titleRow.add(new JLabel(selfRc.getMediumIcon()));
		titleRow.add(new JLabel("is following"));
		for (GameObject go : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
			CharacterWrapper cw = new CharacterWrapper(go);
			if (cw.isPlayingTurn()) {
				titleRow.add(new JLabel(RealmComponent.getRealmComponent(go).getMediumIcon()));
				break;
			}
		}
		panel.add(titleRow);

		stopFollowingCheckbox = new JCheckBox("Stop Following");
		JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
		checkRow.add(stopFollowingCheckbox);
		panel.add(checkRow);
		return panel;
	}

	private void showPrePhaseActivityDialog() {
		if (prePhaseActivityDialog == null) {
			prePhaseActivityDialog = new JDialog(gameHandler.getMainFrame(), "Pre-Phase Activities", false);
		}
		// Rebuild content each time — available spells and follower state change each turn.
		currentChitSelections.clear();
		stopFollowingCheckbox = null;
		JPanel content = new JPanel(new BorderLayout(8, 8));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
		JPanel northArea = new JPanel();
		northArea.setLayout(new BoxLayout(northArea, BoxLayout.Y_AXIS));
		northArea.add(buildPrePhaseDialogHeader());
		northArea.add(new JSeparator(JSeparator.HORIZONTAL));
		content.add(northArea, BorderLayout.NORTH);

		boolean showColorChits = !getCharacter().getColorMagicChits().isEmpty()
			&& !hostPrefs.hasPref(Constants.FE_PHASE_END_PLAYING_COLOR_CHIT);
		boolean showStopFollowing = isFollowerOfPhasingChar();

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		if (showColorChits) {
			centerPanel.add(buildColorChitPanel());
		}
		if (showColorChits && showStopFollowing) {
			centerPanel.add(Box.createVerticalStrut(6));
			centerPanel.add(new JSeparator(JSeparator.HORIZONTAL));
			centerPanel.add(Box.createVerticalStrut(6));
		}
		if (showStopFollowing) {
			centerPanel.add(buildStopFollowingPanel());
		}
		content.add(centerPanel, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
		JButton hideButton = new JButton("Hide");
		hideButton.addActionListener(e -> prePhaseActivityDialog.setVisible(false));
		JButton submitButton = new JButton("SUBMIT");
		submitButton.addActionListener(e -> submitPrePhaseActivities());
		buttons.add(hideButton);
		buttons.add(submitButton);
		JPanel southArea = new JPanel();
		southArea.setLayout(new BoxLayout(southArea, BoxLayout.Y_AXIS));
		southArea.add(new JSeparator(JSeparator.HORIZONTAL));
		southArea.add(Box.createVerticalStrut(6));
		southArea.add(buttons);
		content.add(southArea, BorderLayout.SOUTH);

		prePhaseActivityDialog.setContentPane(content);
		prePhaseActivityDialog.pack();
		prePhaseActivityDialog.setLocationRelativeTo(this);
		prePhaseActivityDialog.setVisible(true);
		prePhaseActivityDialog.toFront();
	}

	private static void styleChitToggleButton(JToggleButton btn) {
		Border selected = BorderFactory.createLineBorder(new Color(80, 130, 255), 3);
		Border empty    = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		btn.setBorder(empty);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.addChangeListener(e -> btn.setBorder(btn.isSelected() ? selected : empty));
	}

	private JPanel buildColorChitPanel() {
		Color gridColor = Color.GRAY;
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		ArrayList<MagicChit> colorChits = getCharacter().getColorMagicChits();
		if (colorChits.isEmpty() || hostPrefs.hasPref(Constants.FE_PHASE_END_PLAYING_COLOR_CHIT)) return wrapper;
		JLabel colorChitsLabel = new JLabel("Play Color Chits:");
		colorChitsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		wrapper.add(colorChitsLabel);

		JPanel grid = new JPanel();
		grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
		grid.setAlignmentX(Component.CENTER_ALIGNMENT);
		grid.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, gridColor));

		boolean filterHidden = hostPrefs.hasPref(Constants.OPT_COLOR_CHIT_TARGETING_NO_HIDDEN_TARGETS);
		for (MagicChit chit : colorChits) {
			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
			RealmComponent chitRc = RealmComponent.getRealmComponent(chit.getGameObject());
			row.add(chitCell(new JLabel(chitRc.getMediumIcon()), gridColor));

			JCheckBox checkbox = new JCheckBox();
			checkbox.setEnabled(false);
			checkbox.setHorizontalAlignment(SwingConstants.CENTER);
			row.add(chitCell(checkbox, gridColor));

			ArrayList<JToggleButton> toggles = new ArrayList<>();
			Map<JToggleButton, SpellWrapper> spellByButton = new LinkedHashMap<>();

			for (SpellWrapper spell : getCompatibleInertSpells(chit, filterHidden)) {
				JToggleButton spellBtn = new JToggleButton(buildSpellIcon(spell));
				spellBtn.setToolTipText(spell.getName());
				styleChitToggleButton(spellBtn);
				toggles.add(spellBtn);
				spellByButton.put(spellBtn, spell);
				row.add(chitCell(spellBtn, gridColor));
			}
			JToggleButton noTargetBtn = new JToggleButton(chitRc.getMediumIcon());
			noTargetBtn.setText("Fatigue without Target");
			noTargetBtn.setHorizontalTextPosition(SwingConstants.CENTER);
			noTargetBtn.setVerticalTextPosition(SwingConstants.TOP);
			noTargetBtn.setToolTipText("No target – fatigue chit only");
			styleChitToggleButton(noTargetBtn);
			toggles.add(noTargetBtn);
			spellByButton.put(noTargetBtn, null);
			row.add(chitCell(noTargetBtn, gridColor));

			// Mutual exclusion + checkbox sync: selecting any toggle deselects others and
			// enables/checks the checkbox; deselecting the last one disables/unchecks it.
			for (JToggleButton btn : toggles) {
				btn.addItemListener(e -> {
					if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
						for (JToggleButton other : toggles) { if (other != btn) other.setSelected(false); }
						checkbox.setEnabled(true);
						checkbox.setSelected(true);
					} else {
						if (toggles.stream().noneMatch(JToggleButton::isSelected)) {
							checkbox.setEnabled(false);
							checkbox.setSelected(false);
						}
					}
				});
			}
			// Unchecking the checkbox deselects whichever toggle is currently selected.
			checkbox.addItemListener(e -> {
				if (e.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
					toggles.forEach(b -> b.setSelected(false));
				}
			});

			currentChitSelections.add(new ChitSelection(chit, checkbox, toggles, spellByButton));
			grid.add(row);
		}
		wrapper.add(grid);
		return wrapper;
	}

	private static JPanel chitCell(JComponent comp, Color gridColor) {
		JPanel cell = new JPanel(new BorderLayout());
		cell.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 1, gridColor),
			BorderFactory.createEmptyBorder(4, 4, 4, 4)
		));
		cell.add(comp, BorderLayout.CENTER);
		return cell;
	}

	private ArrayList<SpellWrapper> getCompatibleInertSpells(MagicChit chit, boolean filterHidden) {
		ArrayList<SpellWrapper> result = new ArrayList<>();
		ColorMagic chitColor = chit.getColorMagic();
		TileLocation loc = getCharacter().getCurrentLocation();
		if (loc == null || !loc.isInClearing()) return result;
		SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(gameHandler.getClient().getGameData());
		for (SpellWrapper spell : sm.getAllSpellsInClearing(loc, false)) {
			if (!spell.isInert()) continue;
			ColorMagic spellColor = spell.getRequiredColorMagic();
			if (spellColor != null && !spellColor.sameColorAs(chitColor)) continue;
			if (filterHidden && spell.getTargets() != null) {
				boolean canTarget = spell.getTargets().stream()
					.anyMatch(t -> !t.isHidden() || getCharacter().foundHiddenEnemy(t.getGameObject()));
				if (!canTarget) continue;
			}
			result.add(spell);
		}
		return result;
	}

	private ImageIcon buildSpellIcon(SpellWrapper spell) {
		int charSz = 50;
		int gap = 6;
		int cardW = CardComponent.CARD_WIDTH;
		int cardH = CardComponent.CARD_HEIGHT;
		int totalW = charSz + gap + cardW;
		int totalH = Math.max(charSz, cardH);
		int charY = (totalH - charSz) / 2;
		Image targetImg = spell.getAffectedTarget().getMediumImage();
		Image spellImg = RealmComponent.getRealmComponent(spell.getGameObject()).getImage();
		BufferedImage combined = new BufferedImage(totalW, totalH, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = combined.getGraphics();
		g.drawImage(targetImg, 0, charY, charSz, charSz, null);
		g.drawImage(spellImg, charSz + gap, 0, cardW, cardH, null);
		g.dispose();
		return new ImageIcon(combined);
	}

	private void submitPrePhaseActivities() {
		if (prePhaseActivityDialog != null) prePhaseActivityDialog.setVisible(false);
		// Execute any color chit play decisions the player made.
		GameWrapper game = gameHandler.getGame();
		for (ChitSelection sel : currentChitSelections) {
			if (!sel.isPlayed()) continue;
			SpellWrapper spell = sel.getSelectedSpell();
			String colorName = sel.chit.getColorMagic().getColorName();
			if (spell != null) {
				// Check that no stronger conflicting spell blocks this one.
				boolean blocked = false;
				if (spell.canConflict()) {
					int str = spell.getConflictStrength();
					SpellMasterWrapper smw = SpellMasterWrapper.getSpellMaster(gameHandler.getClient().getGameData());
					for (SpellWrapper aff : smw.getAffectingSpells(spell.getAffectedTarget().getGameObject())) {
						if (!aff.isInert() && aff.canConflict() && !aff.equals(spell) && aff.getConflictStrength() > str) {
							blocked = true;
							break;
						}
					}
				}
				if (!blocked) {
					RealmLogging.logMessage(getCharacter().getGameObject().getName(), "Burns a " + colorName + " chit to energize " + spell.getName());
					spell.affectTargets(gameHandler.getMainFrame(), game, false, null);
					sel.chit.makeFatigued();
					RealmUtility.reportChitFatigue(getCharacter(), sel.chit, "Fatigued color chit: ");
				}
			} else {
				RealmLogging.logMessage(getCharacter().getGameObject().getName(), "Burns a " + colorName + " chit.");
				sel.chit.makeFatigued();
				RealmUtility.reportChitFatigue(getCharacter(), sel.chit, "Fatigued color chit: ");
			}
		}
		if (stopFollowingCheckbox != null && stopFollowingCheckbox.isSelected()) {
			getCharacter().setStopFollowing(true);
			RealmLogging.logMessage(getCharacter().getGameObject().getName(), "Stops following.");
		}
		stopFollowingCheckbox = null;
		getCharacter().setNeedsPrePhaseActivityDecision(false);
		prePhaseDialogShowing = false;
		gameHandler.submitChanges();
		gameHandler.updateCharacterFramesWithoutMap();
		bringPhasingCharacterToFront();
	}
	private void doPostPhaseActivities() {
		showPostPhaseActivityDialog();
	}

	private void showPostPhaseActivityDialog() {
		if (postPhaseActivityDialog == null) {
			postPhaseActivityDialog = new JDialog(gameHandler.getMainFrame(), "Post-Phase Activities", false);
		}
		// Rebuild each time — blockable candidates change each action.
		blockingButtonMap = new LinkedHashMap<>();
		JPanel content = new JPanel(new BorderLayout(8, 8));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

		JPanel northArea = new JPanel();
		northArea.setLayout(new BoxLayout(northArea, BoxLayout.Y_AXIS));
		northArea.add(buildPostPhaseDialogHeader());
		northArea.add(new JSeparator(JSeparator.HORIZONTAL));
		content.add(northArea, BorderLayout.NORTH);

		content.add(buildBlockingPanel(), BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
		JButton hideButton = new JButton("Hide");
		hideButton.addActionListener(e -> postPhaseActivityDialog.setVisible(false));
		JButton submitButton = new JButton("SUBMIT");
		submitButton.addActionListener(e -> submitPostPhaseActivities());
		buttons.add(hideButton);
		buttons.add(submitButton);
		JPanel southArea = new JPanel();
		southArea.setLayout(new BoxLayout(southArea, BoxLayout.Y_AXIS));
		southArea.add(new JSeparator(JSeparator.HORIZONTAL));
		southArea.add(Box.createVerticalStrut(6));
		southArea.add(buttons);
		content.add(southArea, BorderLayout.SOUTH);

		postPhaseActivityDialog.setContentPane(content);
		postPhaseActivityDialog.pack();
		postPhaseActivityDialog.setLocationRelativeTo(this);
		postPhaseActivityDialog.setVisible(true);
		postPhaseActivityDialog.toFront();
	}

	private JPanel buildPostPhaseDialogHeader() {
		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
		Font headerFont = header.getFont().deriveFont(Font.BOLD, 16f);
		RealmComponent selfRc = RealmComponent.getRealmComponent(getCharacter().getGameObject());
		header.add(new JLabel(selfRc.getMediumIcon()));
		JLabel label = new JLabel("Phase End Activities for");
		label.setFont(headerFont);
		header.add(label);
		for (GameObject go : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
			CharacterWrapper cw = new CharacterWrapper(go);
			if (cw.isPlayingTurn()) {
				header.add(new JLabel(RealmComponent.getRealmComponent(go).getMediumIcon()));
				String nextAction = cw.getNextPendingAction();
				if (nextAction != null) {
					ImageIcon actionIcon = CharacterWrapper.getIconForAction(nextAction);
					if (actionIcon != null) {
						Image scaled = actionIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
						header.add(new JLabel(new ImageIcon(scaled)));
					}
				}
				JLabel actionLabel = new JLabel("Action");
				actionLabel.setFont(headerFont);
				header.add(actionLabel);
				break;
			}
		}
		return header;
	}

	private ArrayList<RealmComponent> getPostPhaseBlockCandidates() {
		ArrayList<RealmComponent> candidates = new ArrayList<>();
		TileLocation loc = getCharacter().getCurrentLocation();
		if (loc == null || !loc.isInClearing()) return candidates;

		if (getCharacter().isPlayingTurn()) {
			// Phasing character: can block all detectable characters, denizens, and monsters
			// in the clearing except own current followers.
			Set<GameObject> ownFollowers = new HashSet<>();
			for (CharacterWrapper f : getCharacter().getActionFollowers()) {
				ownFollowers.add(f.getGameObject());
			}
			for (RealmComponent rc : loc.clearing.getClearingComponents()) {
				if (rc.getGameObject().equals(getCharacter().getGameObject())) continue;
				if (ownFollowers.contains(rc.getGameObject())) continue;
				if (rc.isPlayerControlledLeader()) {
					CharacterWrapper cw = new CharacterWrapper(rc.getGameObject());
					if (!cw.isHidden() || getCharacter().foundHiddenEnemy(rc.getGameObject())) {
						candidates.add(rc);
					}
				} else if (rc.isDenizen() || rc.isMonster()) {
					if (!rc.isMonsterPart() && (rc.getOwner() == null || rc.isDenizen())) {
						candidates.add(rc);
					}
				}
			}
		} else {
			// Non-phasing character: can only block the phasing character if detectable.
			for (GameObject go : RealmUtility.getLivingCharacters(gameHandler.getClient().getGameData())) {
				CharacterWrapper cw = new CharacterWrapper(go);
				if (cw.isPlayingTurn()) {
					boolean detectable = !cw.isHidden() || getCharacter().foundHiddenEnemy(go);
					if (detectable) candidates.add(RealmComponent.getRealmComponent(go));
					break;
				}
			}
		}
		return candidates;
	}

	private JCheckBox blockCheckbox = null;

	private JPanel buildBlockingPanel() {
		ArrayList<RealmComponent> candidates = getPostPhaseBlockCandidates();
		blockCheckbox = null;

		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		if (candidates.isEmpty()) return wrapper;

		if (!getCharacter().isPlayingTurn()) {
			// Non-phasing: at most one candidate (the phasing char) — simple section like Stop Following.
			RealmComponent target = candidates.get(0);
			JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
			titleRow.add(new JLabel(RealmComponent.getRealmComponent(getCharacter().getGameObject()).getMediumIcon()));
			titleRow.add(new JLabel("can block"));
			titleRow.add(new JLabel(target.getMediumIcon()));
			wrapper.add(titleRow);
			blockCheckbox = new JCheckBox("Block");
			JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
			checkRow.add(blockCheckbox);
			wrapper.add(checkRow);
		} else {
			// Phasing character: N×M toggle-button table.
			// Use BorderLayout so the title is left-aligned at top and the grid is centered below.
			wrapper.setLayout(new BorderLayout(0, 4));
			JLabel title = new JLabel("Block:");
			wrapper.add(title, BorderLayout.NORTH);
			int X = candidates.size();
			int N = 1;
			for (; N <= 10; N++) { if (X <= N * N) break; }
			int M = 1;
			for (; M <= 10; M++) { if (X <= N * M) break; }
			Color gridColor = Color.GRAY;
			JPanel grid = new JPanel(new GridLayout(M, N, 0, 0));
			grid.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, gridColor));
			for (RealmComponent rc : candidates) {
				JToggleButton btn = new JToggleButton(rc.getMediumIcon());
				btn.setToolTipText(rc.getGameObject().getName());
				styleChitToggleButton(btn);
				blockingButtonMap.put(btn, rc);
				grid.add(chitCell(btn, gridColor));
			}
			for (int i = X; i < N * M; i++) {
				grid.add(chitCell(new JPanel(), gridColor));
			}
			JPanel gridHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			gridHolder.add(grid);
			wrapper.add(gridHolder, BorderLayout.CENTER);
		}
		return wrapper;
	}

	private void applyBlock(RealmComponent rc) {
		getCharacter().addReactDecision(rc.getGameObject());
		if (rc.isPlayerControlledLeader()) {
			CharacterWrapper target = new CharacterWrapper(rc.getGameObject());
			target.setBlocked(true);
			if (target.isHidden()) target.setHidden(false);
		} else if (rc.isMonster()) {
			MonsterChitComponent monster = (MonsterChitComponent) rc;
			if (!monster.isBlocked()) monster.setBlocked(true);
		}
		if (getCharacter().isHidden()) getCharacter().setHidden(false);
		if (!getCharacter().isBlocked()) getCharacter().setBlocked(true);
		gameHandler.broadcast(getCharacter().getGameObject().getName(), "Blocks the " + rc.getGameObject().getName());
		RealmLogging.logMessage(getCharacter().getGameObject().getName(), "Blocks " + rc.getGameObject().getName());
	}

	private void submitPostPhaseActivities() {
		if (postPhaseActivityDialog != null) postPhaseActivityDialog.setVisible(false);
		if (blockCheckbox != null && blockCheckbox.isSelected()) {
			// Non-phasing path: single candidate (the phasing char).
			ArrayList<RealmComponent> candidates = getPostPhaseBlockCandidates();
			if (!candidates.isEmpty()) applyBlock(candidates.get(0));
			blockCheckbox = null;
		}
		if (blockingButtonMap != null) {
			for (Map.Entry<JToggleButton, RealmComponent> entry : blockingButtonMap.entrySet()) {
				if (entry.getKey().isSelected()) applyBlock(entry.getValue());
			}
			blockingButtonMap = null;
		}
		getCharacter().setNeedsPostPhaseActivityDecision(false);
		postPhaseDialogShowing = false;
		gameHandler.submitChanges();
		gameHandler.updateCharacterFramesWithoutMap();
		bringPhasingCharacterToFront();
	}
	private void doPlayColorChitNow() {
		boolean phaseBeginning = getCharacter().getNeedsPlayColorChitInterruptPhaseBeginningDecision();
		boolean phaseEnd = getCharacter().getNeedsPlayColorChitInterruptPhaseEndDecision();
		if (phaseBeginning||phaseEnd) {
			ArrayList<RealmComponent> interruptions = character.checkForColorChitInterruptionState(null,phaseBeginning,phaseEnd);
			if (interruptions!=null && !interruptions.isEmpty()) {
				for (RealmComponent target:interruptions) {
					getCharacter().addColorChitInterruptPhaseBeginningDecision(target.getGameObject());
				}
				while(true) {
					MagicChit playedChit = RealmUtility.burnColorChit(gameHandler.getMainFrame(),gameHandler.getGame(),getCharacter(),hostPrefs.hasPref(Constants.OPT_COLOR_CHIT_TARGETING_NO_HIDDEN_TARGETS));
					if (playedChit!=null) {
						int ret = JOptionPane.showConfirmDialog(this,
								"Do you want to play another color chit?",
								getCharacter().getGameObject().getName()+" - Playing Color Chit",
								JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE);
						if (ret != JOptionPane.YES_NO_OPTION) {
							break;
						}
					} else {
						break;
					}
				}
				character.setNeedsPlayColorChitInterruptPhaseDecision(false,phaseBeginning,phaseEnd);
				gameHandler.submitChanges();
				gameHandler.updateCharacterList(); // This is necessary so that THIS client is updated
			}
			updateControls();
		}
	}
	private void handleBlockCharacter(RealmComponent rc) {
		if (getCharacter().isSleep()) {
			JOptionPane.showMessageDialog(this,"Cannot block if sleeping (affected by Flowers of Rest).","Cannot block - Flowers of Rest",JOptionPane.ERROR_MESSAGE);
			getCharacter().addReactDecision(rc.getGameObject());
			return;
		}
		if (((getCharacter().getTransmorph()==null && getCharacter().getGameObject().hasThisAttribute(Constants.SMALL)) || (getCharacter().getTransmorph()!=null && getCharacter().getTransmorph().hasThisAttribute(Constants.SMALL))) && hostPrefs.hasPref(Constants.HOUSE3_SMALL_MONSTERS)) {
			JOptionPane.showMessageDialog(this,"Small individuals cannot block.","Cannot block - Small",JOptionPane.ERROR_MESSAGE);
			getCharacter().addReactDecision(rc.getGameObject());
			return;
		}
		if (rc.getGameObject().hasThisAttribute(Constants.BLINDING_LIGHT)) {
			JOptionPane.showMessageDialog(this,"Cannot block characters affected by Blinding Light.","Cannot block - Blinding Light",JOptionPane.ERROR_MESSAGE);
			getCharacter().addReactDecision(rc.getGameObject());
			return;
		}
		CharacterWrapper target = new CharacterWrapper(rc.getGameObject());
		if (getCharacter().isMistLike() || (target.isMistLike() && !getCharacter().getGameObject().hasThisAttribute(Constants.IGNORE_MIST_LIKE))) {
			JOptionPane.showMessageDialog(this,"Cannot block as Melt-into-Mist character or block other Melt-into-Mist characters.","Cannot block - Melt into Mist",JOptionPane.ERROR_MESSAGE);
			getCharacter().addReactDecision(rc.getGameObject());
			return;
		}
		if (getCharacter().getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING) || target.getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING)) {
			JOptionPane.showMessageDialog(this,"You are affected by the Meditate effect or your target is affected by the Medidate effect.","Cannot block - Medidate effect",JOptionPane.ERROR_MESSAGE);
			getCharacter().addReactDecision(rc.getGameObject());
			return;
		}
		int ret = JOptionPane.showConfirmDialog(
				this,
				"Do you want to block the "+rc.getGameObject().getName()+" ?",
				getCharacter().getGameObject().getName()+" Blocking",
				JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,rc.getIcon());
		getCharacter().addReactDecision(rc.getGameObject());
		if (ret == JOptionPane.YES_OPTION) {
			if (rc.isPlayerControlledLeader()) {
				target.setBlocked(true);
				if (target.isHidden()) { // Getting blocked brings them out of hiding
					target.setHidden(false);
				}
			}
			else if (rc.isMonster()) {
				MonsterChitComponent monster = (MonsterChitComponent)rc;
				if (!monster.isBlocked()) {
					monster.setBlocked(true);
				}
			}
			if (getCharacter().isHidden()) {
				// Blocking brings you out of hiding
				getCharacter().setHidden(false);
			}
			if (!getCharacter().isBlocked()) {
				// Blocking also causes you to be blocked
				getCharacter().setBlocked(true);
			}
			gameHandler.broadcast(character.getGameObject().getName(),"Blocks the "+rc.getGameObject().getName());
		}
	}

	public void updateCharacter() {
		//		if (needsUpdate()) { // This would be nice, but is very problematic, like when an object in the inventory changes, or a chit flips!
		// Get rid of GameOver panel, if any
		if (!character.isGameOver()) {
			hideGameOver();
		}
		
		String phaseName = "BIRDSONG";
		if (gameHandler.getGame().isDaylight()) {
			phaseName = "DAYLIGHT";
		}
		else if (gameHandler.getGame().inCombat()) {
			phaseName = "EVENING";
		}
		else if (gameHandler.getGame().isDayEnd()) {
			phaseName = "MIDNIGHT";
		}
		setTitle(character.getCharacterName() + " - Month " + character.getCurrentMonth() + ", Day " + character.getCurrentDay()+" - "+phaseName);
		dailyCombatCheckbox.setSelected(!character.isMinion() && character.getWantsCombat());
		dayEndRearrangmentCheckbox.setSelected(!character.isMinion() && character.getWantsDayEndTrades());
		keepReactingCheckbox.setSelected(!character.isMinion() && character.keepsReacting());
		
		// Update mountain move icon (might change with seasons/weather)
		mountainMoveIcon.setCost(getCharacter().getMountainMoveCost()+(character.addsOneToMoveExceptCaves()?1:0));

		// Reset the recorded spells panel
		if (spellsPanel != null)
			spellsPanel.updatePanel();

		// Inventory
		if (inventoryPanel != null)
			inventoryPanel.updatePanel();

		// Friendships
		relationshipPanel.updatePanel();
		
		// Discoveries
		if (discoveriesPanel !=null) {
			discoveriesPanel.updatePanel();
		}
		
		// Victory Points
		if (victoryPanel != null) {
			victoryPanel.updatePanel();
		}
		
		// Notes
		if (notesPanel != null) {
			notesPanel.updatePanel();
		}
		
		// Expansion
		if (expansionOnePanel != null) {
			expansionOnePanel.updatePanel();
		}

		// Hirelings
		if (hirelingPanel != null) {
			hirelingPanel.updatePanel();
		}
		
		// Quests
		if (questPanel != null) {
			questPanel.updatePanel();
		}

		updateCharLabel();
		characterVulnerability.setText(character.getVulnerability().fullString());

		// Revalidate and Repaint action history table
		actionPanel.updatePanel();

		// Update other things
		if (character.hasHealing()) {
			character.doHealWoundsToFatigue();
		}
		updateBadges();
		updateAvailableColorMagic();
		updateActiveCurses();
		if (chitPanel != null) {
			chitPanel.updatePanel();
		}
		else {
			// In case chitPanel is not called
			updateControls();
		}
		if (turnPanel!=null) {
			turnPanel.updatePanel();
		}
		revalidate();
		repaint();
		
		blockees = getCharacter().checkForBlockingState();
		updateControls();
		boolean isLocalCharacter = gameHandler.getClient().getClientName().equals(getCharacter().getPlayerName());
		// Auto-show hook for non-phasing characters: when the flag is set, queue a non-modal dialog
		// via invokeLater so the current call stack (process()) can return first. prePhaseDialogShowing
		// stays true until SUBMIT is clicked (submitPrePhaseActivities() resets it), preventing the
		// auto-show from firing again while the dialog is open or hidden. isLocalCharacter guard ensures
		// only this client's own character shows a dialog — without it the phasing client would fire
		// dialogs for remote characters before those players' machines can react.
		if (getCharacter().getNeedsPrePhaseActivityDecision() && !getCharacter().isPlayingTurn() && isLocalCharacter && !prePhaseDialogShowing) {
			prePhaseDialogShowing = true;
			SwingUtilities.invokeLater(() -> doPrePhaseActivities());
		}
		// Auto-show hook for post-phase: all qualifying individuals have their flag set simultaneously by
		// ActionRow.process(). Only the LOCAL player's characters show a dialog here — every client has
		// CharacterFrames for all players, so without this guard the phasing client would fire dialogs for
		// remote characters, clearing their flags before those players' machines can react.
		if (getCharacter().getNeedsPostPhaseActivityDecision() && isLocalCharacter && !postPhaseDialogShowing) {
			postPhaseDialogShowing = true;
			SwingUtilities.invokeLater(() -> doPostPhaseActivities());
		}
	}

	public void toFront() {
		super.toFront();
		updateControls();
		if (character.isDoRecord()) {
			ArrayList<TileLocation> clearingPlot = getCharacter().getClearingPlot();
			if (clearingPlot!=null) {
				gameHandler.getInspector().getMap().setClearingPlot(new ArrayList<TileLocation>(clearingPlot));
			}
			else {
				gameHandler.getInspector().getMap().clearClearingPlot();
			}
		}
	}

	protected void updateControls() {
		boolean active = character.isActive();
		TileLocation current = getCharacter().getCurrentLocation();
		boolean partway = current != null && (current.isBetweenClearings() || current.isBetweenTiles());
		
		if (character.getNeedsInventoryCheck()) {
			character.setNeedsInventoryCheck(false);
			character.checkInventoryStatus(gameHandler.getMainFrame(),null,gameHandler.getUpdateFrameListener());
		}
		
		hiddenEnemiesLabel.setVisible(character.foundHiddenEnemies());
		if (character.foundHiddenEnemies()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html>&nbsp;Found Hidden Enemies");
			if (!character.foundAllHiddenEnemies()) {
				sb.append(":");
				ArrayList<String> list = character.getFoundEnemies();
				for (String val:list) {
					sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;");
					sb.append(val);
					sb.append("&nbsp;");
				}
			}
			sb.append("&nbsp;</html>");
			hiddenEnemiesLabel.setToolTipText(sb.toString());
		}
		blockedLabel.setVisible(character.isBlocked());
		
		recordedFameLabel.setText(character.getFameString());
		recordedNotorietyLabel.setText(character.getNotorietyString());
		recordedGoldLabel.setText(character.getGoldString());
		
		updateDevProgress();
		
		boolean recordingActions = character.isDoRecord() && actionPanel.getActionControlManager().getCurrentlyRecordingAction() == null;
		boolean canTrade = getCharacter().isCharacter() || getCharacter().isHiredLeader() || getCharacter().isControlledMonster();
		viewChitsButton.setVisible(character.isHidden() && hostPrefs.hasPref(Constants.OPT_QUIET_MONSTERS));
		if (!character.isMinion() && character.isReacting()) {
			reactButton.setIcon(IconFactory.findIcon("images/interface/blockon.gif"));
			reactButton.setToolTipText("PvP Reactions ON");
			reactButton.setSelected(true);
		}
		else {
			reactButton.setIcon(IconFactory.findIcon("images/interface/blockoff.gif"));
			reactButton.setToolTipText("PvP Reactions OFF");
			reactButton.setSelected(false);
		}
		unhideButton.setEnabled(character.isHidden());
		tradeButton.setEnabled(active && canTrade && !partway && !character.isSleep() && !gameHandler.getGame().getCharacterPoolLock());
		shoutButton.setEnabled(tradeButton.isEnabled());
		singleButtonManager.updateButtonVisibility();
		if (!gameHandler.isLocal() && singleButtonManager.hasMandatoryShowing()) SoundUtility.playAttention();
		actionPanel.updateControls(recordingActions && gameHandler.getGame().getGameStarted() && (!gameHandler.getGame().isGameOver() && !gameHandler.game.getGameEnded()) && !singleButtonManager.hasMandatoryShowing());
		if (chitPanel != null) {
			if (turnPanel != null && hostPrefs.hasPref(Constants.OPT_DISABLE_ACTIONS_WHEN_AWAITING_REACTIONS)) {
				chitPanel.updateControls(turnPanel.isAwaitingReactions());
			} else {
				chitPanel.updateControls(false);
			}
		}
		if (inventoryPanel != null) {
			if (turnPanel != null && hostPrefs.hasPref(Constants.OPT_DISABLE_ACTIONS_WHEN_AWAITING_REACTIONS)) {
				inventoryPanel.updateControls(recordingActions,turnPanel.isAwaitingReactions());
			} else {
				inventoryPanel.updateControls(recordingActions,false);
			}
		}
		if (turnPanel != null) {
			turnPanel.updateControls();
		}

		gameHandler.getMainFrame().updateMenuActions();
	}
	private void updateDevProgress() {
		if (developmentProgress!=null) {
			developmentProgress.setValue(character.getCharacterExtraChitMarkers());
			developmentProgress.setGoal(character.getCharacterStage());
		}
	}
	public boolean isWaitingForSingleButton() {
		return singleButtonManager.hasMandatoryShowing();
	}
	
	public void updateSingleButton() {
		if (singleButtonManager!=null) {
			singleButtonManager.updateButtonVisibility();
		}
	}

	private void setupVPs() {
		int vps = character.getNewVPRequirement();
		CharacterVictoryConditionsDialog vpDialog = new CharacterVictoryConditionsDialog(gameHandler.getMainFrame(), character, vps);
		vpDialog.setLocationRelativeTo(this);
		vpDialog.setVisible(true);
		gameHandler.submitChanges();
		updateControls();
	}
	private void deductVPs() {
		int vps = character.getTotalAssignedVPs();
		int deduct = character.getVPsToDeduct();
		CharacterVictoryConditionsDialog vpDialog = new CharacterVictoryConditionsDialog(gameHandler.getMainFrame(), character, vps, deduct);
		vpDialog.setLocationRelativeTo(this);
		vpDialog.setVisible(true);
		gameHandler.submitChanges();
		updateControls();
	}
	/**
	 * This is really only for the Book of Quests game play
	 */
	private void chooseQuest() {
		ArrayList<Quest> quests = new ArrayList<>();
		if (hostPrefs.isUsingGuildQuests()) {
			String guildName = character.getCurrentLocation().clearing.getGuild().getGameObject().getThisAttribute("guild");
			for (Quest quest : QuestLoader.findAvailableQuests(character,hostPrefs)) {
				if (quest.getGuild().matches(guildName)) {
					quests.add(quest);
				}
			}
		}
		else {
			 quests = QuestLoader.findAvailableQuests(character,hostPrefs);
		}
		Quest quest = QuestChooser.chooseQuest(gameHandler.getMainFrame(),quests,getCharacter());
		if (quest!=null) {
			if (quest.isMultipleUse()) {
				quest = quest.copyQuestToGameData(character.getGameObject().getGameData());
			}
			
			// Make the quest active by default (Book of Quests)
			quest.setState(QuestState.Active,getCharacter().getCurrentDayKey(), getCharacter());
			
			// Add the quest
			getCharacter().addQuest(gameHandler.getMainFrame(),quest);
			
			// Submit changes
			gameHandler.submitChanges();
			
			// finally update the character
			updateCharacter();
			
			// update map, some quests need it
			gameHandler.getInspector().redrawMap();
		}
		updateControls();
	}

	private void placeOneGoldSpecial() {
		if (vpSetupButton.isVisible() || !getCharacter().getNeedsChooseGoldSpecial()) {
			// Not sure how you can get here in the first place if the tests are true, but apparently
			// someone did.  This will prevent any further issue with this.
			return;
		}
		
		// Need to recognize when you are playing a local game
		
		GoldSpecialPlacementDialog gsDialog = new GoldSpecialPlacementDialog(gameHandler.getMainFrame(),character);
		gsDialog.setLocationRelativeTo(gameHandler.getMainFrame());
		
		boolean local = gameHandler.isLocal();
		do {
			gsDialog.rebuildAndShow();
			if (!gsDialog.isCancel()) {
				GoldSpecialChitComponent chit = gsDialog.getActiveChit();
				GameObject destination = gsDialog.getActiveDestination();
				
				gameHandler.broadcast(getCharacter().getGameObject().getName(), "Places " + chit.getGameObject().getName() + " in the " + destination.getName());
				destination.add(chit.getGameObject());
				destination.setThisAttribute(Constants.GOLD_SPECIAL_PLACED);
				chit.getGameObject().setThisAttribute(Constants.GOLD_SPECIAL_PLACED);
				if (!hostPrefs.hasPref(Constants.HOUSE2_NO_MISSION_VISITOR_FLIPSIDE) && !hostPrefs.usesSuperRealm()) {
					chit.getOtherSide().getGameObject().setThisAttribute(Constants.GOLD_SPECIAL_PLACED);
				}
				if (getCharacter().getNeedsChooseGoldSpecial() && SetupCardUtility.stillChitsToPlace(hostPrefs)) {
					if (!local) {
						gameHandler.incrementCharacterToPlace();
					}
				}
				else {
					getCharacter().setNeedsChooseGoldSpecial(false);
					gameHandler.startGame();
					break;
				}
			}
			else {
				break;
			}
		}
		while(local);
	}
	
	private void approveInventory() {
		ApproveInventoryDialog dialog = new ApproveInventoryDialog(gameHandler.getMainFrame(),getCharacter().getInventoryToApprove());
		dialog.setVisible(true);
		
		for (GameObject go:dialog.getRejected()) {
			// Return the object to the original owner
			GameObject originalOwner = go.getGameObjectFromThisAttribute(Constants.REQUIRES_APPROVAL);
			CharacterWrapper owningCharacter = new CharacterWrapper(originalOwner);
			ArrayList<GameObject> stuff = new ArrayList<>();
			stuff.add(go);
			go.removeThisAttribute(Constants.REQUIRES_APPROVAL);
			RealmUtility.transferInventory(gameHandler.getMainFrame(),getCharacter(),owningCharacter,stuff,gameHandler.getUpdateFrameListener(),false);
		}
		
		for (GameObject go:dialog.getApproved()) {
			go.removeThisAttribute(Constants.REQUIRES_APPROVAL);
		}
		
		gameHandler.submitChanges();
		gameHandler.updateCharacterFrames();
	}

	private void pickupGoldSpecial() {
		RealmComponent chosenGS = null;
		ArrayList<RealmComponent> list = new ArrayList<>();
		TileLocation tl = getCharacter().getCurrentLocation();
		if (tl.isInClearing()) {
			for (RealmComponent rc : tl.clearing.getClearingComponents()) {
				if (rc.isGoldSpecial() && !rc.isVisitor() && !rc.isNomad() && !rc.getGameObject().hasThisAttribute(Constants.DRAW_BACKSIDE)) {
					list.add(rc);
					chosenGS = rc;
				}
			}
		}
		if (!list.isEmpty()) {
			if (list.size() > 1) {
				chosenGS = null;
				RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(), "Pickup which chit?", true);
				chooser.addRealmComponents(list, false);
				chooser.setVisible(true);
				if (chooser.getSelectedText() != null) {
					chosenGS = chooser.getFirstSelectedComponent();
				}
			}
			if (chosenGS != null) {
				GoldSpecialChitComponent gsrc = (GoldSpecialChitComponent) chosenGS;
				// Chit has been chosen, now verify this is what they want!
				JEditorPane pane = new JEditorPane("text/html", gsrc.generateHTML(character)) {
					public boolean isFocusable() {
						return false;
					}
				};
				pane.setEditable(false);
				pane.setOpaque(false);

				int ret = JOptionPane.showConfirmDialog(gameHandler.getMainFrame(), pane, "Pickup " + gsrc.getGameObject().getName() + "?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, gsrc.getIcon());
				if (ret == JOptionPane.YES_OPTION) {
					// Verify the character can afford it
					if (gsrc.isCampaign() && hostPrefs.hasPref(Constants.SR_ADV_BOUNTY_HUNTER)) {
						GamePool pool = new GamePool(character.getGameData().getGameObjects());
						ArrayList<GameObject> nativeLeaders = pool.find("native,rank=HQ");
						ArrayList<String> enemyClansToCharacter = new ArrayList<>();
						ArrayList<String> notEnemyClansToCharacter = new ArrayList<>();
						for (GameObject nativeLeader : nativeLeaders) {
							if (!nativeLeader.hasThisAttribute(Constants.CLAN)) continue;
							if (character.getRelationship(nativeLeader)<=-2) {
								if (!enemyClansToCharacter.contains(nativeLeader.getThisAttribute(Constants.CLAN))) {
									enemyClansToCharacter.add(nativeLeader.getThisAttribute(Constants.CLAN));
								}
							} else {
								notEnemyClansToCharacter.add(nativeLeader.getThisAttribute(Constants.CLAN));
							}
						}
						for (String clan : notEnemyClansToCharacter) {
							enemyClansToCharacter.remove(clan);
						}
						if (enemyClansToCharacter.isEmpty()) {	
							ArrayList<RealmComponent> foes = new ArrayList<>();
							ArrayList<RealmComponent> foesToClan1 = new ArrayList<>();
							ArrayList<GameObject> livingCharacters = RealmUtility.getLivingCharacters(character.getGameData());
							for (GameObject livingCharacter : livingCharacters) {
								if (livingCharacter.getStringId().matches(character.getGameObject().getStringId())) continue;
								CharacterWrapper livingCharacterWrapper = new CharacterWrapper(livingCharacter);
								if (livingCharacterWrapper.isHiredLeader() || livingCharacterWrapper.isMinion()) continue;
								ArrayList<String> enemyClans = new ArrayList<>();
								ArrayList<String> notEnemyClans = new ArrayList<>();
								for (GameObject nativeLeader : nativeLeaders) {
									if (!nativeLeader.hasThisAttribute(Constants.CLAN)) continue;
									if (livingCharacterWrapper.getRelationship(nativeLeader)<=-2) {
										if (!enemyClans.contains(nativeLeader.getThisAttribute(Constants.CLAN))) {
											enemyClans.add(nativeLeader.getThisAttribute(Constants.CLAN));
										}
									} else {
										if (!notEnemyClans.contains(nativeLeader.getThisAttribute(Constants.CLAN))) {
											notEnemyClans.add(nativeLeader.getThisAttribute(Constants.CLAN));
										}
									}
								}
								for (String clan : notEnemyClans) {
									enemyClans.remove(clan);
								}
								if (!enemyClans.isEmpty()) {
									if (enemyClans.contains("1")) {
										foesToClan1.add(RealmComponent.getRealmComponent(livingCharacter));
									}
									else {
										foes.add(RealmComponent.getRealmComponent(livingCharacter));
									}
								}
							}
							int bountyHunterQuestion = JOptionPane.showConfirmDialog(gameHandler.getMainFrame(), "Pickup " + gsrc.getGameObject().getName() + " as Bounty Hunter?", "Pickup " + gsrc.getGameObject().getName() + " as Bounty Hunter?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, gsrc.getIcon());
							if (bountyHunterQuestion == JOptionPane.YES_OPTION) {
								if (foesToClan1.isEmpty() && foes.isEmpty()) {
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "There are no targets for a Bounty Hunter.", "No Bounty Hunter targets", JOptionPane.ERROR_MESSAGE);
									return;
								}
								RealmComponent target = null;
								if (foesToClan1.isEmpty() && foes.size()==1) {
									target = foes.get(0);
								} else if (foes.isEmpty() && foesToClan1.size()==1) {
									target = foes.get(0);
								} else {
									RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Which character will be the foe?",false);
									for (RealmComponent foe : foes) {
										chooser.addRealmComponent(foe);
									}
									for (RealmComponent foe : foesToClan1) {
										chooser.addRealmComponent(foe);
									}
									chooser.setVisible(true);
									target = chooser.getFirstSelectedComponent();
								}
								if (character.getNotoriety()<15) {
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You do not have enough recorded notoriety points to collect this campaign as Bounty Hunter.", "Not enough notoriety points", JOptionPane.ERROR_MESSAGE);
									return;
								}
								if (foesToClan1.contains(target) && character.getFame()<8) {
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You do not have enough recorded fame points to collect this campaign as Bounty Hunter.", "Not enough fame points", JOptionPane.ERROR_MESSAGE);
									return;
								}
								gsrc.getGameObject().setThisAttribute(Constants.BOUNTY_HUNTER,target.getGameObject().getStringId());
								gsrc.getGameObject().setThisAttribute(Constants.BOUNTY_HUNTER_TARGET,target.getGameObject().getName());
								
								if (gsrc.getGameObject().hasThisAttribute("fame_cost")) {
									gsrc.getGameObject().setThisAttribute(Constants.CAMPAIGN_FAME,gsrc.getGameObject().getThisAttribute("fame_cost"));
									gsrc.getGameObject().removeThisAttribute("fame_cost");
								}
								if (foesToClan1.contains(target)) {
									gsrc.getGameObject().setThisAttribute("fame_cost",8);
								}
								if (gsrc.getGameObject().hasThisAttribute("notoriety_cost")) {
									gsrc.getGameObject().setThisAttribute(Constants.CAMPAIGN_NOTORIETY,gsrc.getGameObject().getThisAttribute("notoriety_cost"));
									gsrc.getGameObject().removeThisAttribute("notoriety_cost");
								}
								gsrc.getGameObject().setThisAttribute("notoriety_cost",15);
								
								if (gsrc.getGameObject().hasThisAttribute("foe")) {
									gsrc.getGameObject().setThisAttribute(Constants.CAMPAGIN_FOE,gsrc.getGameObject().getThisAttribute("foe"));
									gsrc.getGameObject().removeThisAttribute("foe");
								}
								
								if (gsrc.getGameObject().hasThisAttribute("partner")) {
									gsrc.getGameObject().setThisAttribute(Constants.CAMPAGIN_PARTNER,gsrc.getGameObject().getThisAttribute("partner"));
									gsrc.getGameObject().removeThisAttribute("partner");
								}
								CharacterWrapper targetedCharacter = new CharacterWrapper(target.getGameObject());
								ArrayList<String> foeNatives = new ArrayList<>();
								for (GameObject nativeLeader : nativeLeaders) {
									if (!nativeLeader.hasThisAttribute(Constants.CLAN)) continue;
									if (targetedCharacter.getRelationship(nativeLeader)<=-2) {
										String groupName = nativeLeader.getThisAttribute("native").toLowerCase();
										if (groupName!=null && !foeNatives.contains(groupName)) {
											foeNatives.add(groupName);
										}
									}
								}
								StringBuilder allGroups = new StringBuilder();
								if (!foeNatives.isEmpty()) {
									for (String group : foeNatives) {
										allGroups.append(group+",");
									}
								}
								allGroups.append("mercenaries");
								gsrc.getGameObject().setThisAttribute("partner",allGroups.toString());
								
								gsrc.setup(getCharacter());
								character.getGameObject().add(gsrc.getGameObject());
								gsrc.getGameObject().removeThisAttribute("clearing");
								QuestRequirementParams qp = new QuestRequirementParams();
								qp.actionName = gsrc.getGameObject().getName();
								qp.actionType = CharacterActionType.PickUpMissionCampaign;
								qp.targetOfSearch = gsrc.getGameObject();
								if(getCharacter().testQuestRequirements(gameHandler.getMainFrame(),qp)) {
									gameHandler.getInspector().redrawMap();
								}
		
								gameHandler.submitChanges();
								gameHandler.updateCharacterFrames();
								return;
							}
						}
					}
					if (hostPrefs.hasPref(Constants.HOUSE2_CAMPAIGN_DEBT) || gsrc.meetsPointRequirement(getCharacter())) {
						if (!gsrc.isComplete(getCharacter(),getCharacter().getCurrentLocation())) {
							boolean cannotPickUp = false;
							if (hostPrefs.hasPref(Constants.SR_COMPLETE_GOLD_SPECIAL_ONLY_ONCE)) {
								if (gsrc.isMission() && (character.hasMissionCompleted(gsrc.toString()) || character.hasMissionFailed(gsrc.toString()))) {
									cannotPickUp = true;
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You cannot pick up a Mission which you have already completed (or failed).", "Already completed mission", JOptionPane.ERROR_MESSAGE);
								}
								if (gsrc.isCampaign() && (character.hasCampaignCompleted(gsrc.toString()) || character.hasCampaignFailed(gsrc.toString()) && !hostPrefs.hasPref(Constants.SR_ADV_BOUNTY_HUNTER))) {
									cannotPickUp = true;
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You cannot pick up a Campaign which you have already completed (or failed).", "Already completed campaign", JOptionPane.ERROR_MESSAGE);
								}
								if (gsrc.isTask() && (character.hasTaskCompleted(gsrc.toString()) || character.hasTaskFailed(gsrc.toString()))) {
									cannotPickUp = true;
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You cannot pick up a Task which you have already completed (or failed).", "Already completed task", JOptionPane.ERROR_MESSAGE);
								}
							}
							if (hostPrefs.hasPref(Constants.SR_ONE_OF_EACH_GOLD_SPECIAL)) {
								boolean hasCampaign = false;
								boolean hasTask = false;
								for (GameObject item : character.getInventory()) {
									if (item.hasThisAttribute(Constants.CAMPAIGN)) {
										hasCampaign = true;
									}
									if (item.hasThisAttribute(Constants.TASK)) {
										hasTask = true;
									}
								}
								if (gsrc.isCampaign() && hasCampaign) {
									cannotPickUp = true;
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You cannot pick up a Campaign if you have already one.", "Campaign already active", JOptionPane.ERROR_MESSAGE);
								}
								if (gsrc.isTask() && hasTask) {
									cannotPickUp = true;
									JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You cannot pick up a Task if you have already one.", "Task already active", JOptionPane.ERROR_MESSAGE);
								}
							}
							
							if (!cannotPickUp) {								
								// Setup campaign/mission (do this BEFORE picking up chit, so clearing count is accurate!)
								gsrc.setup(getCharacter());
		
								// Pickup chit
								getCharacter().getGameObject().add(gsrc.getGameObject());
								gsrc.getGameObject().removeThisAttribute("clearing");
								QuestRequirementParams qp = new QuestRequirementParams();
								qp.actionName = gsrc.getGameObject().getName();
								qp.actionType = CharacterActionType.PickUpMissionCampaign;
								qp.targetOfSearch = gsrc.getGameObject();
								if(getCharacter().testQuestRequirements(gameHandler.getMainFrame(),qp)) {
									gameHandler.getInspector().redrawMap();
								}
		
								gameHandler.submitChanges();
								gameHandler.updateCharacterFrames();
							}
						}
						else {
							JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You cannot pick up a chit that is already complete.", "Invalid chit", JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You do not have enough recorded points to collect this chit.", "Not enough points", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	private void selectGuildBenefit() {
		character.getCurrentGuildStore().applyGuildBenefit3(gameHandler.getMainFrame(), character);
		character.getGameObject().removeThisAttribute(Constants.GUILD_BENEFIT_SUCESSOR);
		gameHandler.updateCharacterFrames();
	}
	
	protected void enchantToContinue() {
		int count = character.getFollowSpellActions();
		if (count>0) {
			
			doSpellActionAsFollower();
			
			character.clearFollowSpellActions();
			gameHandler.submitChanges();
			gameHandler.updateCharacterFrames();
		}
	}
	protected void alertToContinue() {
		int count = character.getFollowAlerts();
		if (count>0) {
			
			doAlertActionAsFollower();
			
			character.clearFollowAlerts();
			gameHandler.submitChanges();
			gameHandler.updateCharacterFrames();
		}
	}
	protected void restToContinue() {
		int count = character.getFollowRests();
		if (count>0) {
			int bonusCount = character.getRestBonus(count);
			ChitRestManager rester = new ChitRestManager(gameHandler.getMainFrame(),character,count+bonusCount);
			rester.setVisible(true);
			if (rester.isFinished()) {
				character.clearFollowRests();
				gameHandler.submitChanges();
				gameHandler.updateCharacterFrames();
			}
		}
	}
	private void doSpellActionAsFollower() {
		if (character.hasMesmerizeEffect(Constants.SAPPED)) {
			return;
		}
		
		TileLocation targetClearing = ActionRow.getTargetClearingForSpellAction(character, gameHandler);
		RealmComponentOptionChooser compChooser = ActionRow.enchantChooser(character, gameHandler, targetClearing, character.getInfiniteColorSources());
		if (compChooser.hasOptions()) {
			compChooser.setVisible(true);
			String text = compChooser.getSelectedText();
			if (text!=null) {
				ActionRow.enchantTileOrChit(character, compChooser, text, targetClearing, gameHandler);
			}
			else {
				return;
			}
		}
		else {
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Enchant;
			character.testQuestRequirements(gameHandler.getMainFrame(), params);
		}
	}
	private void doAlertActionAsFollower() {
		if (character.hasMesmerizeEffect(Constants.TIRED)) {
			return;
		}
		
		RealmComponentOptionChooser chooser = ActionRow.alertChooser(character, gameHandler);
		
		if (chooser!=null) {
			chooser.setVisible(true);
			if (chooser.getSelectedText()!=null) {
				ActionRow.alertChosenObject(character, chooser);
								
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Alert;
				character.testQuestRequirements(gameHandler.getMainFrame(),params);
				
				gameHandler.updateCharacterFrames();
			}
			else {
				if (character.isFollowingCharacterPlayingTurn()) {
					int ret = JOptionPane.showConfirmDialog(
							gameHandler.getMainFrame(),
							"Do you want to skip the ALERT action?",
							"ALERT is optional for followers",
							JOptionPane.YES_NO_OPTION);
					if (ret==JOptionPane.YES_OPTION) {
						return;
					}
				}
				return;
			}
		}
		else {
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Alert;
			character.testQuestRequirements(gameHandler.getMainFrame(),params);
		}
	}
	protected void fatigueToContinue() {
		int needToFatigue = character.getWeatherFatigue();
		if (needToFatigue>0) {
			ChitFatigueManager fatiguer = new ChitFatigueManager(gameHandler.getMainFrame(),character,needToFatigue);
			fatiguer.setVisible(true);
			character.clearWeatherFatigue();
			// Test for death
			if (fatiguer.isDead()) {
				// Now what?
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You have died of weather fatigue.", "Dead", JOptionPane.INFORMATION_MESSAGE);
				character.makeDead("Died by weather fatigue");
				// I doubt this is enough...
			}
			gameHandler.submitChanges();
			gameHandler.updateCharacterFrames();
		}
	}
	protected void checkDenizenControlToContinue() {
		for (RealmComponent hireling : character.getAllHirelings()) {
			if (hireling.getGameObject().hasThisAttribute(Constants.CONTROLLED_DEMON)) {
				int ret = JOptionPane.showConfirmDialog(this,"Want to fatigue a Magic Chit to keep the control?","Controlling "+hireling.getGameObject().getNameWithNumber(),JOptionPane.YES_NO_OPTION);
				if (ret==JOptionPane.NO_OPTION) {
					character.removeHireling(hireling.getGameObject());
				}
				else {
					ChitFatigueManager fatiguer = new ChitFatigueManager(gameHandler.getMainFrame(),character,1,0,0,1);
					fatiguer.setVisible(true);
					character.addHireling(hireling.getGameObject(),2);
				}
				gameHandler.submitChanges();
				gameHandler.updateCharacterFrames();
			}
		}
	}
	protected void woundToContinue() {
		int needToWound = character.getExtraWounds();
		if (needToWound>0) {
			ChitWoundManager wounder = new ChitWoundManager(gameHandler.getMainFrame(),character,needToWound);
			wounder.setVisible(true);
			character.clearExtraWounds();
			// Test for death
			if (wounder.isDead()) {
				// Now what?
				JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "You have died of your wounds.", "Dead", JOptionPane.INFORMATION_MESSAGE);
				character.makeDead("Died of wounds.");
				// I doubt this is enough...
			}
			gameHandler.submitChanges();
			gameHandler.updateCharacterFrames();
		}
	}
	
	protected void doEnergizeChoice() {
		if (character.hasSpellConflicts()) {
			ArrayList<SpellWrapper> conflicts = character.getSpellConflicts();
			SpellWrapper spell = (SpellWrapper)RealmUtility.chooseSpell(gameHandler.getMainFrame(),conflicts,false,false);
			if (spell!=null) {
				GameWrapper game = GameWrapper.findGame(character.getGameObject().getGameData());
				spell.affectTargets(gameHandler.getMainFrame(),game,false,null);
			}
			character.clearSpellConflicts();
			gameHandler.submitChanges();
			gameHandler.updateCharacterFrames();
		}
	}

	public void initComponents(int iconSize) {
		setFrameIcon(IconFactory.findIcon("images/actions/hire.gif"));
		setSize(500, 500);
		setMinimumSize(new Dimension(500, 500));
		JPanel layoutPanel = new JPanel(new BorderLayout());
		setContentPane(layoutPanel);
		
		getActionPanel().modifyToolbarIconStyle(iconSize);

		// Build GUI		
		layoutPanel.add(getCharacterDetailPanel(), "North");
		tabs = new JTabbedPane(SwingConstants.BOTTOM);
		tabs.addTab(null, ImageCache.getIcon("tab/record"), getActionPanel(), "Record Actions");
		if (character.isCharacter())
			tabs.addTab(null, ImageCache.getIcon("tab/chits"), getChitPanel(), "Chits");
		if (!character.isMinion())
			tabs.addTab(null, ImageCache.getIcon("tab/inventory"), getInventoryObjectPanel(), "Inventory");
		tabs.addTab(null, ImageCache.getIcon("tab/spells"), getSpellsPanel(), "Spells"); // hired leaders might be bewitched by spells...
		tabs.addTab(null, ImageCache.getIcon("tab/disc"), getDiscoveriesPanel(), "Discoveries");
		tabs.addTab(null, ImageCache.getIcon("tab/relationships"), getRelationshipPanel(), "Native Relationships");
		if (!character.isMinion())
			tabs.addTab(null, ImageCache.getIcon("tab/followers"), getHirelingPanel(), "Hirelings");
		if (character.isCharacter()) {
			tabs.addTab(null, ImageCache.getIcon("tab/victoryreq"), getVictoryPanel(), "Victory Requirements");
			tabs.addTab(null, ImageCache.getIcon("tab/notepad"), getNotesPanel(),"Character Notes");
		}
		if (hostPrefs.getGameKeyVals().contains("rw_expansion_1")) {
			tabs.addTab(null, ImageCache.getIcon("tab/expansionOne"), getExpansionOnePanel(),"Expansion");
		}
		if (hostPrefs.isUsingQuests()) {
			tabs.addTab(null, ImageCache.getIcon("tab/quest"), getQuestPanel(),"Quest");
		}
		tabs.addTab(null, ImageCache.getIcon("tab/chat"), getChatPanel(),"Chat");

		boolean[] tabChangedByThisPress = {false};
		tabs.addChangeListener(e -> tabChangedByThisPress[0] = true);
		tabs.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int clicked = tabs.indexAtLocation(e.getX(), e.getY());
				if (clicked >= 0 && !tabChangedByThisPress[0]) {
					int homeIdx = 0;
					for (int i = 0; i < tabs.getTabCount(); i++) {
						if (RealmTurnPanel.TAB_NAME.equals(tabs.getToolTipTextAt(i))) { homeIdx = i; break; }
					}
					tabs.setSelectedIndex(homeIdx);
				}
				tabChangedByThisPress[0] = false;
			}
		});

		layoutPanel.add(tabs, "Center");

		//		updateCharacter(); // no need to do this here
	}

	public void centerOnToken() {
		if (gameHandler.getGame().getGameStarted() && gameHandler.isOption(RealmSpeakOptions.MAP_CENTER_ON_CHARACTER)) {
			gameHandler.getInspector().getMap().centerOn(character.getCurrentLocation());
		}
	}

	private JPanel getCharacterDetailPanel() {
		if (characterDetailPanel == null) {
			characterDetailPanel = new JPanel(new BorderLayout());
			characterDetailPanel.add(getTokenPanel(), "North");
			characterDetailPanel.add(getInfoPanel(), "Center");
			characterDetailPanel.add(getPhaseInteractionPanel(), "South");
		}
		return characterDetailPanel;
	}
	protected JPanel getTokenPanel() {
		if (tokenPanel==null) {
			tokenPanel = new JPanel(new BorderLayout());
			ChitComponent chit = (ChitComponent) RealmComponent.getRealmComponent(character.getGameObject());
			boolean resetHidden = false;
			if (chit.isHidden()) {
				resetHidden = true;
				chit.setHidden(false);
			}
			GameObject transmorph = getCharacter().getTransmorph();
			if (transmorph != null) {
				getCharacter().setTransmorph(null);
			}
			charLabel = new JLabel(chit.getMediumIcon());
			if (resetHidden) {
				chit.setHidden(true);
			}
			if (transmorph != null) {
				getCharacter().setTransmorph(transmorph);
			}
			charLabel.setFont(new Font("Dialog", Font.BOLD, 36));
			charLabel.setIconTextGap(10);
			charLabel.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent ev) {
					tokenClicked();
				}
			});
			tokenPanel.add(charLabel, "West");
			JPanel sideControls = new JPanel(new GridLayout(4, 1));
			showCharCardButton = new JButton("Show Card");
			showCharCardButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					showCharCard();
				}
			});
			showCharCardButton.setEnabled(character.isCharacter());
			sideControls.add(showCharCardButton);
			dailyCombatCheckbox = new JCheckBox("Daily Combat",!character.isMinion() && character.getWantsCombat());
			dailyCombatCheckbox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					character.setWantsCombat(dailyCombatCheckbox.isSelected());
					gameHandler.submitChanges();
				}
			});
			
			if (hostPrefs.getEnableBattles()) {
				sideControls.add(dailyCombatCheckbox);
			}
			else {
				JLabel nocombatLabel = new JLabel("COMBAT OFF");
				nocombatLabel.setForeground(Color.red);
				nocombatLabel.setFont(new Font("Dialog", Font.BOLD, 12));
				sideControls.add(nocombatLabel);
			}

			dailyCombatCheckbox.setEnabled(!character.isMinion());
			
			dayEndRearrangmentCheckbox = new JCheckBox("Day End Trades");
			dayEndRearrangmentCheckbox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					character.setWantsDayEndTrades(dayEndRearrangmentCheckbox.isSelected());
				}
			});
			sideControls.add(dayEndRearrangmentCheckbox);
			dayEndRearrangmentCheckbox.setEnabled(!character.isMinion() && !hostPrefs.hasPref(Constants.FE_NO_END_OF_DAY_TRADING));
			
			keepReactingCheckbox = new JCheckBox("DayStart: PvP Reactions ON");
			keepReactingCheckbox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					character.setKeepReacting(keepReactingCheckbox.isSelected());
				}
			});
			sideControls.add(keepReactingCheckbox);
			keepReactingCheckbox.setEnabled(!character.isMinion());

			tokenPanel.add(sideControls, "East");
		}
		return tokenPanel;
	}
	
	private JPanel getInfoPanel() {
		JPanel infoPanel = new JPanel(new GridLayout(1, 2));
		UniformLabelGroup group = new UniformLabelGroup();
		
		JPanel attributesMovePanel = new JPanel(new BorderLayout());
		
		boolean development = gameHandler.getHostPrefs().hasPref(Constants.EXP_DEVELOPMENT) && character.isCharacter();
		JPanel attributesPanel = new JPanel(new GridLayout(development?6:5, 1));
		attributesPanel.setBorder(BorderFactory.createEtchedBorder());
		Box box = group.createLabelLine("Player");
		box.add(new JLabel(character.getPlayerName()));
		box.add(Box.createHorizontalGlue());
		attributesPanel.add(box);
		box = group.createLabelLine("Gold");
		recordedGoldLabel = new JLabel("");
		box.add(recordedGoldLabel);
		box.add(Box.createHorizontalGlue());
		attributesPanel.add(box);
		box = group.createLabelLine("Fame");
		recordedFameLabel = new JLabel("");
		box.add(recordedFameLabel);
		box.add(Box.createHorizontalGlue());
		attributesPanel.add(box);
		box = group.createLabelLine("Notoriety");
		recordedNotorietyLabel = new JLabel("");
		box.add(recordedNotorietyLabel);
		box.add(Box.createHorizontalGlue());
		attributesPanel.add(box);
		box = group.createLabelLine("Vulnerability");
		characterVulnerability = new JLabel("", SwingConstants.CENTER);
		box.add(characterVulnerability);
		box.add(Box.createHorizontalGlue());
		attributesPanel.add(box);
		if (development) {
			boolean extendedDevelopment = gameHandler.getHostPrefs().hasPref(Constants.EXP_DEVELOPMENT_PLUS);
			box = group.createLabelLine("Development");
			developmentProgress = new PointBar(3,extendedDevelopment?33:12);
			ComponentTools.lockComponentSize(developmentProgress,120,10);
			box.add(developmentProgress);
			box.add(Box.createHorizontalGlue());
			attributesPanel.add(box);
		}
		
		attributesMovePanel.add(attributesPanel,"Center");
		
		JPanel moveIconPanel = new JPanel(new GridLayout(3,1));
		int nonCaveAdd = character.addsOneToMoveExceptCaves()?1:0;
		moveIconPanel.add(new MoveMarker("normal",1+nonCaveAdd));
		moveIconPanel.add(new MoveMarker("cave",1));
		mountainMoveIcon = new MoveMarker("mountain",2+nonCaveAdd);
		moveIconPanel.add(mountainMoveIcon);
		
		attributesMovePanel.add(moveIconPanel,"West");
		
		infoPanel.add(attributesMovePanel);
		group = new UniformLabelGroup();
		JPanel effectsPanel = new JPanel(new GridLayout(3, 1));
		
		JPanel badgePanel = new JPanel(new BorderLayout()) {
			public void paint(Graphics g) {
				super.paint(g);
				g.setFont(EMBOSS_FONT);
				g.setColor(EMBOSS_COLOR);
				g.drawString("BADGES",120,28);
			}
		};
		badgePanel.setBorder(BorderFactory.createEtchedBorder());
		currentBadgesBox = Box.createHorizontalBox();
		badgePanel.add(currentBadgesBox, "Center");
		
		JPanel colorPanel = new JPanel(new BorderLayout()) {
			public void paint(Graphics g) {
				super.paint(g);
				g.setFont(EMBOSS_FONT);
				g.setColor(EMBOSS_COLOR);
				g.drawString("COLORS",120,28);
			}
		};
		colorPanel.setBorder(BorderFactory.createEtchedBorder());
		availableColorMagicBox = Box.createHorizontalBox();
		colorPanel.add(availableColorMagicBox, "Center");
		
		JPanel cursesPanel = new JPanel(new BorderLayout()) {
			public void paint(Graphics g) {
				super.paint(g);
				g.setFont(EMBOSS_FONT);
				g.setColor(EMBOSS_COLOR);
				g.drawString("CURSES",120,28);
			}
		};
		cursesPanel.setBorder(BorderFactory.createEtchedBorder());
		activeCursesBox = Box.createHorizontalBox();
		cursesPanel.add(activeCursesBox, "Center");
		
		effectsPanel.add(badgePanel);
		effectsPanel.add(colorPanel);
		effectsPanel.add(cursesPanel);
		infoPanel.add(effectsPanel);
		return infoPanel;
	}

	private Box getPhaseInteractionPanel() {
		Box box = Box.createHorizontalBox();
		phaseManagerLabel = new JLabel() {
			public String getToolTipText(MouseEvent ev) {
				if (phaseManagerIcon != null) {
					Point p = ev.getPoint();
					return phaseManagerIcon.getText(p.x);
				}
				return null;
			}
		};
		phaseManagerLabel.setToolTipText("Phases");
		phaseManagerLabel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				if (character.isDoRecord() || (getTurnPanel()!=null && character.canDoDaytimeRecord() && getTurnPanel().hasActionsLeft())){
					phaseManagerIcon.handleClick(getCharacter(),ev.getPoint());
					updateControls();
//						phaseManagerLabel.repaint();
				}
			}
		});
		box.add(phaseManagerLabel);
		hiddenEnemiesLabel = new JLabel(ImageCache.getIcon("interface/hiddenenemies"));
		box.add(hiddenEnemiesLabel);
		blockedLabel = new JLabel(ImageCache.getIcon("phases/blocked"));
		blockedLabel.setToolTipText(character.getGameObject().getName()+" is BLOCKED!");
		box.add(blockedLabel);
		box.add(Box.createHorizontalGlue());
		singleButtonManager = new SingleButtonManager();
		
		// VP Setup Button
		vpSetupButton = new SingleButton("Setup VPs",true) {
			public boolean needsShow() {
				return character.isActive() && character.needsToSetVps();
			}
		};
		vpSetupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setupVPs();
			}
		});
		vpSetupButton.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		vpSetupButton.setVisible(false);
		ComponentTools.lockComponentSize(vpSetupButton, new Dimension(100, 25));
		singleButtonManager.addButton(vpSetupButton);
		box.add(vpSetupButton);
		
		// VP Deduct Button
		vpDeductButton = new SingleButton("Deduct VPs",true) {
			public boolean needsShow() {
				return character.isActive() && character.needsToDeductVps();
			}
		};
		vpDeductButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				deductVPs();
			}
		});
		vpDeductButton.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		vpDeductButton.setVisible(false);
		ComponentTools.lockComponentSize(vpDeductButton, new Dimension(100, 25));
		singleButtonManager.addButton(vpDeductButton);
		box.add(vpDeductButton);

		// Quest Button
		chooseQuestButton = new SingleButton("Choose Quest",true) {
			public boolean needsShow() {				
				return character.isActive()
						&& character.isCharacter()
						&& hostPrefs.isUsingBookOfQuests() && character.getAllNonEventQuests().size()==0
						&& QuestLoader.hasQuestsToLoad(character,hostPrefs);
			}
		};
		chooseQuestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				chooseQuest();
			}
		});
		chooseQuestButton.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		chooseQuestButton.setVisible(false);
		ComponentTools.lockComponentSize(chooseQuestButton, new Dimension(100, 25));
		singleButtonManager.addButton(chooseQuestButton);
		box.add(chooseQuestButton);
		
		// Advancement Button
		advancementButton = new SingleButton("Advancement",true) {
			public boolean needsShow() {
				return character.needsAdvancement();
			}
		};
		advancementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doAdvancement();
			}
		});
		advancementButton.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		advancementButton.setVisible(false);
		ComponentTools.lockComponentSize(advancementButton, new Dimension(100, 25));
		singleButtonManager.addButton(advancementButton);
		box.add(advancementButton);
		
		// Gold Special Placement Button
		gsPlacementButton = new SingleButton("Place Visitor/Mission",true) {
			public boolean needsShow() {
				return character.isActive() && getCharacter().getNeedsChooseGoldSpecial();
			}
		};
		gsPlacementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				placeOneGoldSpecial();
			}
		});
		gsPlacementButton.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		gsPlacementButton.setVisible(false);
		ComponentTools.lockComponentSize(gsPlacementButton, new Dimension(150, 25));
		singleButtonManager.addButton(gsPlacementButton);
		box.add(gsPlacementButton);
		
		// Enchant Button
		enchantButton = new SingleButton("Enchant to Continue",true) {
			public boolean needsShow() {
				return character.getFollowSpellActions()>0;
			}
		};
		enchantButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				enchantToContinue();
			}
		});
		enchantButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		enchantButton.setVisible(false);
		ComponentTools.lockComponentSize(enchantButton, new Dimension(150, 25));
		singleButtonManager.addButton(enchantButton);
		box.add(enchantButton);
		
		// Alert Button
		alertButton = new SingleButton("Alert to Continue",true) {
			public boolean needsShow() {
				return character.getFollowAlerts()>0;
			}
		};
		alertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				alertToContinue();
			}
		});
		alertButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		alertButton.setVisible(false);
		ComponentTools.lockComponentSize(alertButton, new Dimension(150, 25));
		singleButtonManager.addButton(alertButton);
		box.add(alertButton);
		
		// Rest Button
		restButton = new SingleButton("Rest to Continue",true) {
			public boolean needsShow() {
				return character.getFollowRests()>0;
			}
		};
		restButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				restToContinue();
			}
		});
		restButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		restButton.setVisible(false);
		ComponentTools.lockComponentSize(restButton, new Dimension(150, 25));
		singleButtonManager.addButton(restButton);
		box.add(restButton);
		
		// Fatigue Button
		fatigueButton = new SingleButton("Fatigue to Continue",true) {
			public boolean needsShow() {
				boolean weatherFatigue = getCharacter().getWeatherFatigue()>0 && getCharacter().getAllChits().size()>0;
				return character.isActive()
						&& character.isCharacter()
						&& weatherFatigue;
			}
		};
		fatigueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				fatigueToContinue();
			}
		});
		fatigueButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		fatigueButton.setVisible(false);
		ComponentTools.lockComponentSize(fatigueButton, new Dimension(150, 25));
		singleButtonManager.addButton(fatigueButton);
		box.add(fatigueButton);
		
		// Wounds Button
		woundButton = new SingleButton("Wound to Continue",true) {
			public boolean needsShow() {
				boolean extraWounds = getCharacter().getExtraWounds()>0;
				return character.isActive()
						&& character.isCharacter()
						&& extraWounds;
			}
		};
		woundButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				woundToContinue();
			}
		});
		woundButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		woundButton.setVisible(false);
		ComponentTools.lockComponentSize(woundButton, new Dimension(150, 25));
		singleButtonManager.addButton(woundButton);
		box.add(woundButton);
		
//		// REMOVED: Play Color Chit Now Button — was triggered by the old per-character color-chit interrupt
//		// mechanism (OPT_PHASE_BEGIN_PLAYING_COLOR_CHIT / FE_PHASE_END_PLAYING_COLOR_CHIT). It showed when
//		// getNeedsPlayColorChitInterruptPhaseBeginningDecision() or ...PhaseEnd...() was true and let the
//		// player burn a color chit mid-phase. Now that pre-phase color chit play is handled by the new
//		// pre-phase dialog system, this button is superseded. Re-enable if the old interrupt path is restored.
//		playColorChitNowButton = new SingleButton("Play Color Chit Now?!",true) {
//			public boolean needsShow() {
//				return getCharacter().getNeedsPlayColorChitInterruptPhaseBeginningDecision() || getCharacter().getNeedsPlayColorChitInterruptPhaseEndDecision();
//			}
//		};
//		playColorChitNowButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
//		playColorChitNowButton.setVisible(false);
//		ComponentTools.lockComponentSize(playColorChitNowButton, new Dimension(150, 25));
//		playColorChitNowButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//				doPlayColorChitNow();
//			}
//		});
//		singleButtonManager.addButton(playColorChitNowButton);
//		box.add(playColorChitNowButton);

//		// Block Now Button
//		// REMOVED: Block Now Button — was shown when getNeedsReactDecision() or getNeedsInterruptPhaseDecision()
//		// was true, allowing a character with Blocking ON to declare a block after any phase in their clearing.
//		// The old system set those flags in RealmTurnPanel.isAwaitingReactDecision() and cleared them via
//		// removeAllReactDecisions(). Blocking is being reimplemented as part of the post-phase dialog system;
//		// this button and its isAwaitingReactDecision() gate calls are all commented out pending that work.
//		blockNowButton = new SingleButton("Block Now?!",true) {
//			public boolean needsShow() {
//				return getCharacter().getNeedsReactDecision() || getCharacter().getNeedsInterruptPhaseDecision();
//			}
//		};
//		blockNowButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
//		blockNowButton.setVisible(false);
//		ComponentTools.lockComponentSize(blockNowButton, new Dimension(150, 25));
//		blockNowButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//				doBlockNow();
//			}
//		});
//		singleButtonManager.addButton(blockNowButton);
//		box.add(blockNowButton);

		// Pre-Phase Activity Done Button (phasing character only): appears in place of a modal dialog so the
		// phasing player can freely trade, rearrange items, and interact with their character frame while
		// deciding. It is a mandatory SingleButton (locks other controls) and is visible only when this
		// character is the one playing their turn and has an unresolved pre-phase decision. Clicking it
		// calls doPrePhaseActivities(), which clears the flag and then notifies qualifying non-phasing chars.
		prePhaseActivityDoneButton = new SingleButton("Done: Pre-Phase",true) {
			public boolean needsShow() {
				return getCharacter().isPlayingTurn() && getCharacter().getNeedsPrePhaseActivityDecision();
			}
		};
		prePhaseActivityDoneButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		prePhaseActivityDoneButton.setVisible(false);
		ComponentTools.lockComponentSize(prePhaseActivityDoneButton, new Dimension(150, 25));
		prePhaseActivityDoneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doPrePhaseActivities();
			}
		});
		singleButtonManager.addButton(prePhaseActivityDoneButton);
		box.add(prePhaseActivityDoneButton);

		// Show Phase-Start Dialog Button (non-phasing characters): re-opens the non-modal pre-phase
		// dialog if the player dismissed it with Hide. Non-mandatory so it does not lock other controls.
		showPrePhaseDialogButton = new SingleButton("Show Phase-Start Dialog", false) {
			public boolean needsShow() {
				boolean isLocal = gameHandler.getClient().getClientName().equals(getCharacter().getPlayerName());
				return !getCharacter().isPlayingTurn() && getCharacter().getNeedsPrePhaseActivityDecision() && isLocal;
			}
		};
		showPrePhaseDialogButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		ComponentTools.lockComponentSize(showPrePhaseDialogButton, new Dimension(150, 25));
		showPrePhaseDialogButton.addActionListener(ev -> showPrePhaseActivityDialog());
		showPrePhaseDialogButton.setVisible(false);
		singleButtonManager.addButton(showPrePhaseDialogButton);
		box.add(showPrePhaseDialogButton);

		showPostPhaseDialogButton = new SingleButton("Show Phase-End Dialog", false) {
			public boolean needsShow() {
				boolean isLocal = gameHandler.getClient().getClientName().equals(getCharacter().getPlayerName());
				return getCharacter().getNeedsPostPhaseActivityDecision() && isLocal;
			}
		};
		showPostPhaseDialogButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		ComponentTools.lockComponentSize(showPostPhaseDialogButton, new Dimension(150, 25));
		showPostPhaseDialogButton.addActionListener(ev -> showPostPhaseActivityDialog());
		showPostPhaseDialogButton.setVisible(false);
		singleButtonManager.addButton(showPostPhaseDialogButton);
		box.add(showPostPhaseDialogButton);

		// Energize Choice Button
		energizeChoiceButton = new SingleButton("Energize Spells",true) {
			public boolean needsShow() {
				return getCharacter().hasSpellConflicts();
			}
		};
		energizeChoiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doEnergizeChoice();
			}
		});
		energizeChoiceButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		energizeChoiceButton.setVisible(false);
		ComponentTools.lockComponentSize(energizeChoiceButton, new Dimension(150, 25));
		singleButtonManager.addButton(energizeChoiceButton);
		box.add(energizeChoiceButton);
		
		// Done trading button
		doneTradingButton = new SingleButton("Done Trading",true) {
			public boolean needsShow() {
				return character.isDayEndTradingActive();
			}
		};
		doneTradingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setDayEndTradingActive(false);
				gameHandler.submitChanges();
				gameHandler.updateCharacterFrames();
				showActionPanel();
			}
		});
		doneTradingButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		doneTradingButton.setVisible(false);
		ComponentTools.lockComponentSize(doneTradingButton, new Dimension(150, 25));
		singleButtonManager.addButton(doneTradingButton);
		box.add(doneTradingButton);

//		// REMOVED: Stop Following Button — was shown (non-mandatory) to a follower just before the leader's
//		// next action, allowing them to break from the follow chain before that action executed. needsShow()
//		// checked that the character is actively following and the leader has a pending action. Stop-following
//		// is being reimplemented as an option inside the non-phasing character's pre-phase dialog; this
//		// standalone button is commented out pending that work.
//		stopFollowingButton = new SingleButton("Stop Following",false) {
//			public boolean needsShow() {
//				if (!character.isDoRecord() && character.getFollowStringId()!=null && !character.isStopFollowing() && !hostPrefs.hasPref(Constants.SR_NO_STOPPING_FOLLOWING)) {
//					CharacterWrapper followed = character.getCharacterImFollowing();
//					String npa = followed.getNextPendingAction();
//					if (npa!=null) {
//						stopFollowingButton.setText("Stop Following: "+npa);
//						return true;
//					}
//				}
//				return false;
//			}
//		};
//		stopFollowingButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//				character.setStopFollowing(true);
//				gameHandler.submitChanges();
//				gameHandler.updateCharacterFrames();
//				showActionPanel();
//			}
//		});
//		stopFollowingButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
//		stopFollowingButton.setVisible(false);
//		ComponentTools.lockComponentSize(stopFollowingButton, new Dimension(150, 25));
//		singleButtonManager.addButton(stopFollowingButton);
//		box.add(stopFollowingButton);
		
		// Approve Inventory Button
		approveInventoryButton = new SingleButton("Approve Inventory",true) {
			public boolean needsShow() {
				return character.hasItemsToApprove();
			}
		};
		approveInventoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				approveInventory();
			}
		});
		approveInventoryButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		approveInventoryButton.setVisible(false);
		ComponentTools.lockComponentSize(approveInventoryButton, new Dimension(150, 25));
		singleButtonManager.addButton(approveInventoryButton);
		box.add(approveInventoryButton);

		// Gold Special Pickup Button
		gsPickupButton = new SingleButton("Pickup Visitor/Mission",false) {
			public boolean needsShow() {
				TileLocation current = getCharacter().getCurrentLocation();
				return character.isActive()
						&& character.isCharacter()
						&& showingTurn()
						&& current!=null
						&& current.isInClearing()
						&& current.clearing.holdsGoldSpecial(character.getCurrentCampaign());
			}
		};
		gsPickupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				pickupGoldSpecial();
			}
		});
		gsPickupButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		gsPickupButton.setVisible(false);
		ComponentTools.lockComponentSize(gsPickupButton, new Dimension(150, 25));
		singleButtonManager.addButton(gsPickupButton);
		box.add(gsPickupButton);
		
		// Guild Benefit Button
		guildBenefitButton = new SingleButton("Choose Guild Benefit",false) {
			public boolean needsShow() {
				return character.isActive()
						&& character.isCharacter()
						&& showingTurn()
						&& character.getGameObject().hasThisAttribute(Constants.GUILD_BENEFIT_SUCESSOR);
			}
		};
		guildBenefitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				selectGuildBenefit();
			}
		});
		guildBenefitButton.setBorder(BorderFactory.createLineBorder(MagicRealmColor.GOLD, 2));
		guildBenefitButton.setVisible(false);
		ComponentTools.lockComponentSize(guildBenefitButton, new Dimension(150, 25));
		singleButtonManager.addButton(guildBenefitButton);
		box.add(guildBenefitButton);
		
		box.add(Box.createHorizontalGlue());
		
		viewChitsButton = new JButton("Tile Chits");
		viewChitsButton.setFocusable(false);
		viewChitsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// Show character chits privately instead
				TileLocation tl = getCharacter().getCurrentLocation();
				ClearingUtility.showTileChits(gameHandler.getMainFrame(),tl.clearing,tl.tile.getTileName()+" Chits");
			}
		});
		box.add(viewChitsButton);
		
		reactButton = new JToggleButton(IconFactory.findIcon("images/interface/blockoff.gif"),false);
		reactButton.setToolTipText("PvP Reactions OFF");
		ComponentTools.lockComponentSize(reactButton,39,39);
		if (!character.isMinion() && character.isReacting()) {
			reactButton.setIcon(IconFactory.findIcon("images/interface/blockon.gif"));
			reactButton.setToolTipText("PvP Reactions ON");
			reactButton.setSelected(true);
		}
		reactButton.setFocusable(false);
		reactButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (reactButton.isSelected()) {
					reactButton.setIcon(IconFactory.findIcon("images/interface/blockon.gif"));
					reactButton.setToolTipText("PvP Reactions ON");
					character.setReacting(true);
				}
				else {
					reactButton.setIcon(IconFactory.findIcon("images/interface/blockoff.gif"));
					reactButton.setToolTipText("PvP Reactions OFF");
					character.setReacting(false);
				}
				gameHandler.submitChanges();
				gameHandler.updateCharacterList();
			}
		});
		box.add(reactButton);
		if (character.isMinion()) {
			reactButton.setEnabled(false);
		}
		
		unhideButton = new JButton(IconFactory.findIcon("images/interface/unhide.gif"));
		ComponentTools.lockComponentSize(unhideButton,39,39);
		unhideButton.setToolTipText("Voluntary Unhide");
		unhideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setHidden(false);
				if (RealmUtility.willBeBlocked(character,false,true)) {
					character.setBlocked(true);
				}
				gameHandler.updateFrameListener.stateChanged(new ChangeEvent(getCharacter()));
			}
		});
		box.add(unhideButton);
		
		shoutButton = new JButton(IconFactory.findIcon("images/interface/shout.gif"));
		ComponentTools.lockComponentSize(shoutButton,39,39);
		shoutButton.setToolTipText("Shout Out Discoveries");
		shoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doShoutOut(gameHandler.getMainFrame());
			}
		});
		box.add(shoutButton);
		
		tradeButton = new JButton(IconFactory.findIcon("images/interface/tradeshare.gif"));
		ComponentTools.lockComponentSize(tradeButton,70,39);
		tradeButton.setToolTipText("Trade/Share");
		tradeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doCharacterTrade(gameHandler.getMainFrame(),showingTurn() || character.isFollowingCharacterPlayingTurn());
			}
		});
		box.add(tradeButton);
		return box;
	}
	
	protected void setCurrentPhaseManager(PhaseManager pm) {
		if (pm == null) {
			phaseManagerIcon = null;
			phaseManagerLabel.setIcon(null);
		}
		else {
			phaseManagerIcon = new PhaseManagerIcon(pm);
			phaseManagerLabel.setIcon(phaseManagerIcon);
		}
	}

	private void tokenClicked() {
		centerOnToken();
		if (DebugUtility.isCheat()) {
			// If CHEAT is on, then clicking the token will allow you to cheat
			cheat();
		}
	}

	private void cheat() {
		try {
			String thingName = JOptionPane.showInputDialog("Thing name?", "[type name here]");
			if (thingName != null) {
				gameHandler.broadcast(getCharacter().getGameObject().getName(), "Is Cheating!!  Typed \"" + thingName + "\"");
				character.activateCheater();
				DieRoller roller = new DieRoller();
				roller.adjustDieSize(25, 6);
				roller.addRedDie();
				
				boolean clearing = false;
				boolean faceUp = true;
				boolean allSimilar = false;
				if (thingName.startsWith("up ")) {
					thingName = thingName.substring(3);
					faceUp = true;
					clearing = true;
				}
				else if (thingName.startsWith("down ")) {
					thingName = thingName.substring(5);
					faceUp = false;
					clearing = true;
				}
				else if (thingName.startsWith("all ")) {
					thingName = thingName.substring(4);
					allSimilar = true;
				}
				
				GameObject thing = character.getGameObject().getGameData().getGameObjectByName(thingName);
				if (thing != null) {
					RealmComponent rc = RealmComponent.getRealmComponent(thing);
					if (rc.isTreasure() || rc.isWeapon() || rc.isArmor() || rc.isHorse()) {
						System.out.println("CHEAT - Steal treasure: " + thing.getName());
						
						if (faceUp) {
							thing.setThisAttribute("facing","face_up");
							thing.setThisAttribute(Constants.TREASURE_SEEN);
						}
						else {
							thing.setThisAttribute("facing","face_dn");
						}
						if (clearing) {
							ClearingUtility.moveToLocation(thing,character.getCurrentLocation());
						}
						else {
							character.getGameObject().add(thing);
							rc.setCharacterTimestamp(character);
							thing.setThisAttribute(Constants.TREASURE_NEW);
							character.checkInventoryStatus(gameHandler.getMainFrame(),thing,gameHandler.getUpdateFrameListener());
						}
						gameHandler.updateCharacterFrames();
					}
					else if (rc.isMonster() || rc.isTreasureLocation() || rc.isGoldSpecial()) {
						System.out.println("CHEAT - Summon to clearing: " + thing.getName());
						if (allSimilar) {
							for (GameObject sim:character.getGameObject().getGameData().getGameObjectsByName(thingName)) {
								RealmComponent arc = RealmComponent.getRealmComponent(sim);
								if (arc.isMonster()) {
									SetupCardUtility.resetDenizen(arc.getGameObject());
								}
								if (arc.isMonster() || arc.isTreasureLocation() || arc.isGoldSpecial()) {
									character.getCurrentLocation().clearing.add(sim,null);
								}
							}
						}
						else {
							character.getCurrentLocation().clearing.add(thing,null);
						}
					}
					else if (rc.isNative()) {
						SetupCardUtility.resetDenizen(rc.getGameObject());
						int ret;
						if (rc.getHorse()!=null) {
							ret = JOptionPane.showConfirmDialog(this,"Include the "+rc.getHorse().getGameObject().getName()+"?","Cheating",JOptionPane.YES_NO_OPTION);
							if (ret==JOptionPane.NO_OPTION) {
								rc.getHorse().getGameObject().setThisAttribute(Constants.DEAD);
							}
						}
						ret = JOptionPane.showConfirmDialog(this,"Hire the "+thing.getName()+"?","Cheating",JOptionPane.YES_NO_OPTION);
						if (ret==JOptionPane.YES_OPTION) {
							System.out.println("CHEAT - Hireing: " + thing.getName());
							character.addHireling(thing);
							if (thingName.endsWith("HQ")) {
								character.getCurrentLocation().clearing.add(thing,null);
							}
							else {
								character.getGameObject().add(thing);
							}
						}
						else {
							character.getCurrentLocation().clearing.add(thing,null);
						}
					}
				}
				else if ("heavies".equals(thingName)) {
					GamePool pool = gameHandler.getGamePool();
					Collection<GameObject> heavies = pool.find("weapon,weight=H");
					for (GameObject h : heavies) {
						character.getCurrentLocation().clearing.add(h,null);
					}
				}
				else if ("n_death".equals(thingName)) {
					TileLocation tl = getCharacter().getCurrentLocation();
					Collection<RealmComponent> c = tl.clearing.getDeepClearingComponents();
					for (RealmComponent rc : c) {
						if (rc.isNative() && !rc.getGameObject().hasThisAttribute(Constants.DEAD)) {
							RealmUtility.makeDead(rc);
							System.out.println("Killed "+rc.getGameObject().getName());
						}
					}
				}
				else if ("nh_death".equals(thingName)) {
					TileLocation tl = getCharacter().getCurrentLocation();
					Collection<RealmComponent> c = tl.clearing.getDeepClearingComponents();
					for (RealmComponent rc : c) {
						if (rc.isNativeHorse() && !rc.getGameObject().hasThisAttribute(Constants.DEAD)) {
							rc.getGameObject().setThisAttribute(Constants.DEAD);
							System.out.println("Killed "+rc.getGameObject().getName());
						}
					}
				}
				else if (thingName.startsWith("roll")) {
					if (thingName.length()>4) {
						thingName = thingName.substring(4);
					}
					else {
						thingName = "";
					}
					roller = DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createRoller(thingName);
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(),thingName,"Die Roll",JOptionPane.INFORMATION_MESSAGE,roller.getIcon());
				}
				else if ("pinkslip".equals(thingName)) {
					for (RealmComponent rc:character.getAllHirelings()) {
						rc.addTermOfHire(1-rc.getTermOfHire());
					}
				}
				else if ("pray".equals(thingName)) {
					character.removeAllCurses();
					updateActiveCurses();
				}
				else if ("eyemist".equals(thingName)) {
					(new Curse(gameHandler.getMainFrame(), character.getGameObject())).applyOne(character);
					updateActiveCurses();
				}
				else if ("squeak".equals(thingName)) {
					(new Curse(gameHandler.getMainFrame(), character.getGameObject())).applyTwo(character);
					updateActiveCurses();
				}
				else if ("wither".equals(thingName)) {
					(new Curse(gameHandler.getMainFrame(), character.getGameObject())).applyThree(character);
					updateActiveCurses();
				}
				else if ("illhealth".equals(thingName)) {
					(new Curse(gameHandler.getMainFrame(), character.getGameObject())).applyFour(character);
					updateActiveCurses();
				}
				else if ("ashes".equals(thingName)) {
					(new Curse(gameHandler.getMainFrame(), character.getGameObject())).applyFive(character);
					updateActiveCurses();
				}
				else if ("disgust".equals(thingName)) {
					(new Curse(gameHandler.getMainFrame(), character.getGameObject())).applySix(character);
					updateActiveCurses();
				}
				else if (thingName.startsWith("findhe")) {
					if (thingName.length()==6) {
						character.setFoundHiddenEnemies(true);
					}
					else {
						String name = thingName.substring(6).trim();
						GameObject go = character.getGameObject().getGameData().getGameObjectByName(name);
						if (go!=null) {
							character.addFoundHiddenEnemy(go);
						}
					}
					updateControls();
				}
				else if ("wish".equals(thingName)) {
					Wish wish = new Wish(gameHandler.getMainFrame());
					roller.setValue(0, 1);
					wish.apply(character, roller);
				}
				else if ("wishyou".equals(thingName)) {
					Wish wish = new Wish(gameHandler.getMainFrame());
					roller.setValue(0, 2);
					wish.apply(character, roller);
				}
				else if ("vision".equals(thingName)) {
					Wish wish = new Wish(gameHandler.getMainFrame());
					roller.setValue(0, 3);
					wish.apply(character, roller);
				}
				else if ("peace".equals(thingName)) {
					Wish wish = new Wish(gameHandler.getMainFrame());
					roller.setValue(0, 4);
					wish.apply(character, roller);
				}
				else if ("strength".equals(thingName)) {
					Wish wish = new Wish(gameHandler.getMainFrame());
					roller.setValue(0, 6);
					wish.apply(character, roller);
				}
				else if ("peerany".equals(thingName)) {
					character.setPeerAny(true);
				}
				else if ("info".equals(thingName)) {
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(), "Location: " + character.getCurrentLocation() + "\nPlanned: " + character.getPlannedLocation());
				}
				else if ("rich".equals(thingName)) {
					character.addGold(1000);
				}
				else if ("score".equals(thingName)) {
					character.addFame(500);
					character.addNotoriety(500);
				}
				else if (thingName.startsWith("fame")) {
					try {
						character.setFame(Integer.parseInt(thingName.substring(4)));
					}
					catch (Exception ex) {
					}
				}
				else if (thingName.startsWith("notoriety")) {
					try {
						character.setNotoriety(Integer.parseInt(thingName.substring(9)));
					}
					catch (Exception ex) {
					}
				}
				else if (thingName.startsWith("gold")) {
					try {
						character.setGold(Integer.parseInt(thingName.substring(4)));
					}
					catch (Exception ex) {
					}
				}
				else if ("oof".equals(thingName)) {
					for (CharacterActionChitComponent chit : character.getActiveChits()) {
						if (chit.getEffortAsterisks() > 0) {
							chit.makeFatigued();
						}
						else {
							chit.makeWounded();
						}
					}
				}
				else if (thingName.startsWith("wounds")) {
					int wounds = 0;
					try {
						wounds = Integer.parseInt(thingName.substring(6));
					}
					catch (Exception ex) {
					}
					character.setExtraWounds(wounds);
				}
				else if ("smack".equals(thingName)) {
					for (CharacterActionChitComponent chit : character.getActiveChits()) {
						if (chit.getEffortAsterisks() > 0) {
							chit.makeWounded();
						}
					}
				}
				else if ("crunch".equals(thingName)) {
					for(GameObject go:character.getActiveInventory()) {
						RealmComponent rc = RealmComponent.getRealmComponent(go);
						if (rc.isArmor()) {
							ArmorChitComponent armor = (ArmorChitComponent)rc;
							if (armor.isDamaged()) {
								go.setThisAttribute(Constants.DEAD);
								TreasureUtility.handleDestroyedItem(character,go);
							}
							else {
								armor.setIntact(false);
							}
						}
					}
				}
				else if (thingName.startsWith("summon")) {
					int monsterdie = -1;
					try {
						monsterdie = Integer.parseInt(thingName.substring(6));
					}
					catch (Exception ex) {
					}
					if (monsterdie!=-1) {
						boolean ns = DebugUtility.isNoSummon();
						DebugUtility.NO_SUMMON = false;
						SetupCardUtility.summonMonsters(hostPrefs,new ArrayList<GameObject>(),character, monsterdie,0);
						DebugUtility.NO_SUMMON = ns;
					}
				}
				else if (thingName.startsWith("reset")) {
					int monsterdie = -1;
					try {
						monsterdie = Integer.parseInt(thingName.substring(5));
					}
					catch (Exception ex) {
					}
					if (monsterdie!=-1) {
						boolean ns = DebugUtility.isNoSummon();
						DebugUtility.NO_SUMMON = false;
						SetupCardUtility.resetDenizens(character.getGameObject().getGameData(), monsterdie, hostPrefs.hasPref(Constants.SR_HORSES_REGENERATION));
						DebugUtility.NO_SUMMON = ns;
					}
				}
				else if (thingName.startsWith("roads")) {
					character.getGameObject().setThisAttribute(Constants.KNOWS_ROADS);
					character.generalInitialization();
				}
				else if (thingName.startsWith("pop")) {
					int roll = -1;
					try {
						roll = Integer.parseInt(thingName.substring(3));
					}
					catch (Exception ex) {
					}
					if (roll!=-1) {
						PowerOfThePit pop = new PowerOfThePit(gameHandler.getMainFrame(), character.getGameObject(), new Speed(0));
						roller.setValue(0, roll);
						pop.apply(character, roller);
					}
				}
				else if (thingName.startsWith("magicsight")) {
					int roll = -1;
					try {
						roll = Integer.parseInt(thingName.substring(10));
					}
					catch (Exception ex) {
					}
					if (roll!=-1) {
						MagicSight ms = new MagicSight(gameHandler.getMainFrame());
						roller.setValue(0, roll);
						ms.apply(character, roller);
					}
				}
				else if (thingName.equals("discoverall")) {
					Collection<RealmComponent> c = character.getCurrentLocation().clearing.getClearingComponents();
					for (RealmComponent rc : c) {
						if (rc.isTreasureLocation() || (rc.isTreasure() && rc.getGameObject().hasThisAttribute("treasure_location"))) {
							character.addTreasureLocationDiscovery(rc.getGameObject().getName());
						}
						else if (rc.isGate() || rc.isGuild()) {
							character.addOtherChitDiscovery(rc.getGameObject().getName());
						}
					}
				}
				else if (thingName.equals("seeall")) {
					// Shows the contents of EVERY treasure site
					Collection<GameObject> all = gameHandler.getGamePool().find("print");
					StringBuffer sb = new StringBuffer();
					for (GameObject site : all) {
						sb.append(site.getName());
						sb.append(" ======================\n");
						for (GameObject item : site.getHold()) {
							sb.append("  - ");
							sb.append(item.getName());
							sb.append("\n");
						}
					}
					JTextArea area = new JTextArea(sb.toString());
					area.setLineWrap(false);
					area.setEditable(false);
					JScrollPane pane = new JScrollPane(area);
					ComponentTools.lockComponentSize(pane, new Dimension(400, 600));
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(), pane);
				}
				else if (thingName.equals("manifest")) {
					Hashtable<String, String> hash = new Hashtable<>();
					ArrayList<String> list = new ArrayList<>();
					Collection<GameObject> all = gameHandler.getGamePool().find("print");
					for (GameObject site : all) {
						for (GameObject item : site.getHold()) {
							String name = item.getName();
							if (item.hasThisAttribute("traveler")) {
								name = "Traveler -> "+name;
							}
							hash.put(name, site.getName());
						}
					}
					for (String name : hash.keySet()) {
						String at;
						StringBuffer sb = new StringBuffer(name);
						while ((at = hash.get(name)) != null) {
							sb.append(" -> ");
							sb.append(at);
							name = at;
						}
						list.add(sb.toString());
					}
					Collections.sort(list);
					JList showList = new JList(list.toArray());
					JScrollPane pane = new JScrollPane(showList);
					ComponentTools.lockComponentSize(pane, new Dimension(400, 600));
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(), pane);
				}
				else if (thingName.startsWith("setday")) {
					try {
						int day = Integer.parseInt(thingName.substring(6));
						GameWrapper game = gameHandler.getGame();
						game.setDay(day);
					}
					catch (Exception ex){
					}
				}
				else if (thingName.startsWith("weather")) {
					int result = -1;
					try {
						result = Integer.parseInt(thingName.substring(7));
					}
					catch (Exception ex) {
					}
					if (result!=-1) {
						RealmCalendar cal = RealmCalendar.getCalendar(gameHandler.getClient().getGameData());
						cal.setWeatherResult(result);
						boolean freezing = cal.isFreezingWeather(character.getCurrentMonth());
						RealmUtility.updateWaterClearings(gameHandler.getClient().getGameData(),freezing);
						updateCharacter();
					}
				}
				else if (thingName.startsWith("nice")) {
					String group = thingName.substring(4);
					character.changeRelationship(Constants.GAME_RELATIONSHIP,group,1, false);
					updateCharacter();
				}
				else if (thingName.startsWith("mean")) {
					String group = thingName.substring(4);
					character.changeRelationship(Constants.GAME_RELATIONSHIP,group,-1, false);
					updateCharacter();
				}
				else if (thingName.equals("block")) {
					character.setBlocked(true);
					updateCharacter();
				}
				else if (thingName.equals("doom")) {
					RaiseDead raiseDead = new RaiseDead(gameHandler.getMainFrame());
					//DieRoller deadRoller = DieRollBuilder.getDieRollBuilder(gameHandler.getMainFrame(),character).createRoller(raiseDead);
					//raiseDead.apply(character,deadRoller);
					raiseDead.applySix(character);
				}
				else if (thingName.equals("path")) {
					ArrayList<String> history = character.getMoveHistory();
					StringBuilder sb = new StringBuilder();
					for (int i=0;i<history.size();i++) {
						String location = history.get(i);
						if (CharacterWrapper.MOVE_HISTORY_DAY.equals(location)) continue; // always ignore the days
						if (sb.length()>0) sb.append(" ");
						sb.append(location);
					}
					JTextArea area = new JTextArea(sb.toString());
					area.setFont(new Font("monospaced", Font.PLAIN, 12));
					area.setLineWrap(false);
					area.setEditable(false);
					JScrollPane pane = new JScrollPane(area);
					ComponentTools.lockComponentSize(pane, new Dimension(640, 600));
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(), pane);
				}
				else if (thingName.equals("remark")) {
					ClearingUtility.markBorderlandConnectedClearings(hostPrefs,gameHandler.getClient().getGameData());
				}
				else if (thingName.equals("help")) {
					ArrayList<String> list = new ArrayList<>();
					list.add("manifest    - Shows where everything is hidden");
					list.add("seeall      - Shows each site, and what it contains");
					list.add("discoverall - Discover all treasure locations in the current clearing");
					list.add("magicsight# - Do a Magic Sight roll, where # is the die roll");
					list.add("pop#        - Do a Power of the Pit roll, where # is the die roll");
					list.add("roads       - Discover all paths/passages on the map");
					list.add("reset#      - Resets the board based on the provided monster roll #");
					list.add("summon#     - Summons based on the provided monster roll #");
					list.add("rich        - Gain 1000 gold");
					list.add("score       - Gain 500 Fame, 500 Notoriety");
					list.add("goldXX      - Set to XX gold");
					list.add("fameXX      - Set to XX fame");
					list.add("notorietyXX - Set to XX notoriety");
					list.add("oof         - Fatigue all your active asterisk chits, and wound the others");
					list.add("smack       - Wound all your active asterisk chits");
					list.add("crunch      - Damage all your active armor, and destroy all active damaged armor");
					list.add("info        - Shows current location, in words");
					list.add("peerany     - Turns PeerAny ON, so that you can peer any clearing");
					list.add("wish        - Transport to ANY clearing");
					list.add("wishyou     - Transport other to ANY clearing");
					list.add("vision      - Forces a wish roll result of 3");
					list.add("peace       - Forces a wish roll result of 4");
					list.add("strength    - Forces a wish roll result of 6");
					list.add("eyemist     - Get the EYEMIST curse");
					list.add("squeak      - Get the SQUEAK curse");
					list.add("ashes       - Get the ASHES curse");
					list.add("wither      - Get the WITHER curse");
					list.add("illhealth   - Get the ILLHEALTH curse");
					list.add("disgust     - Get the DISGUST curse");
					list.add("pray        - Removes all curses");
					list.add("findhe      - Find Hidden Enemies");
					list.add("block       - Block yourself");
					list.add("doom        - Two enemy undead are raised");
					list.add("path        - Show the entire character path (can be long)");
					list.add("remark      - Remark all clearing connections to the starting tile, default: Borderland");
					list.add("pinkslip    - Force all your hirelings to have only 1 day left on their term");
					list.add("niceXXXX    - Increase friendliness by one level with stated native group (lowercase)");
					list.add("meanXXXX    - Decrease friendliness by one level with stated native group (lowercase)");
					list.add("heavies     - Causes all heavy weapons in the game to appear in the clearing");
					list.add("n_death     - Causes all natives in the clearing to die");
					list.add("nh_death    - Causes all native horses in the clearing to die");
					list.add("rollXXXX    - Where XXXX is the name of a roll (hide,missile,etc)");
					list.add("setdayXX    - Where XX is the day of the month you want it to be.");
					list.add("weatherX    - Where X is a number from 1-6, indicating the final result of the weather you want.");
					list.add("help        - This list");
					StringBuffer sb = new StringBuffer();
					sb.append("<itemname> - Get that item\n");
					sb.append("up <itemname> - That item appears face up in the clearing.\n");
					sb.append("down <itemname> - That item appears face down (unseen) in the clearing.\n");
					sb.append("<monstername> - Summon that monster\n");
					sb.append("all <monstername> - Summon every monster of that name\n");
					sb.append("<nativename> - Summon and Hire that native\n");
					sb.append("<treasurelocationname> - Bring the treasure location to the current clearing\n");
					sb.append("\nOTHER CHEAT COMMANDS:\n");
					sb.append("=================\n");
					for (String val : list) {
						sb.append(val + "\n");
					}
					JTextArea area = new JTextArea(sb.toString());
					area.setFont(new Font("monospaced", Font.PLAIN, 12));
					area.setLineWrap(false);
					area.setEditable(false);
					JScrollPane pane = new JScrollPane(area);
					ComponentTools.lockComponentSize(pane, new Dimension(640, 600));
					JOptionPane.showMessageDialog(gameHandler.getMainFrame(), pane);
				}
				gameHandler.getInspector().redrawMap();
				gameHandler.updateCharacterFrames();
			}
		}
		catch (Exception ex) {
			// Don't want any exceptions killing me here!
			ex.printStackTrace();
		}
	}

	private void showCharCard() {
		if (FrameManager.getFrameManager().hasFrame(FrameManager.DEFAULT_FRAME_KEY)) {
			FrameManager.getFrameManager().disposeFrame(FrameManager.DEFAULT_FRAME_KEY);
			return;
		}
		CharacterSpyPanel panel = new CharacterSpyPanel(gameHandler, character);
		FrameManager.showDefaultManagedFrame(gameHandler.getMainFrame(), panel, character.getGameObject().getName() + " Spy", null, false);
	}

	public CharacterActionPanel getActionPanel() {
		if (actionPanel == null) {
			actionPanel = new CharacterActionPanel(this);
		}
		return actionPanel;
	}

	private CharacterChitPanel getChitPanel() {
		if (chitPanel == null) {
			chitPanel = new CharacterChitPanel(this);
		}
		return chitPanel;
	}

	private CharacterInventoryPanel getInventoryObjectPanel() {
		if (inventoryPanel == null) {
			inventoryPanel = new CharacterInventoryPanel(this);
		}
		return inventoryPanel;
	}

	private CharacterFramePanel getSpellsPanel() {
		if (spellsPanel == null) {
			spellsPanel = new CharacterSpellsPanel(this);
		}
		return spellsPanel;
	}

	private CharacterDiscoveriesPanel getDiscoveriesPanel() {
		if (discoveriesPanel == null) {
			discoveriesPanel = new CharacterDiscoveriesPanel(this);
		}
		return discoveriesPanel;
	}

	private CharacterRelationshipPanel getRelationshipPanel() {
		if (relationshipPanel == null) {
			relationshipPanel = new CharacterRelationshipPanel(this);
		}
		return relationshipPanel;
	}

	private JPanel getHirelingPanel() {
		if (hirelingPanel == null) {
			hirelingPanel = new CharacterHirelingPanel(this);
		}
		return hirelingPanel;
	}

	private CharacterVictoryPanel getVictoryPanel() {
		if (victoryPanel == null) {
			victoryPanel = new CharacterVictoryPanel(this);
		}
		return victoryPanel;
	}

	private CharacterQuestPanel getQuestPanel() {
		if (questPanel == null) {
			questPanel = new CharacterQuestPanel(this);
		}
		return questPanel;
	}
	
	private CharacterNotesPanel getNotesPanel() {
		if (notesPanel == null) {
			notesPanel = new CharacterNotesPanel(this);
		}
		return notesPanel;
	}

	private CharacterExpansionOnePanel getExpansionOnePanel() {
		if (expansionOnePanel == null) {
			expansionOnePanel = new CharacterExpansionOnePanel(this);
		}
		return expansionOnePanel;
	}
	
	private CharacterChatPanel getChatPanel() {
		if (chatPanel==null) {
			chatPanel = new CharacterChatPanel(this);
		}
		return chatPanel;
	}
	
	/**
	 * Resize according to a set strategy
	 */
	public void organize(JDesktopPane desktop) {
		Dimension size = desktop.getSize();
		int w = size.width >> 1;
		if (forceWidth!=null) {
			w = forceWidth.intValue();
		}
		int h = ((size.height * 3) / 4) - BOTTOM_HEIGHT;
		int y = size.height - h;
		setSize(w, h);
		setLocation(0, y);
		try {
			setIcon(false);
		}
		catch (PropertyVetoException ex) {
			ex.printStackTrace();
		}
	}
	public boolean onlyOneInstancePerGame() {
		return false;
	}
	public String getFrameTypeName() {
		return "Character";
	}

	public RealmTurnPanel getTurnPanel() {
		return turnPanel;
	}

	public boolean showingTurn() {
		return turnPanel != null;
	}

	public void showYourTurn(RealmTurnPanel panel) {
		if (!showingTurn()) { // don't show it twice!
			turnPanel = panel;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tabs.addTab(null,ImageCache.getIcon("tab/turn"),turnPanel,RealmTurnPanel.TAB_NAME);
					tabs.setSelectedIndex(tabs.getTabCount()-1);
					updateControls();
				 }
			});
		}
	}

	public void showActionPanel() {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			if ("Record Actions".equals(tabs.getToolTipTextAt(i))) {
				tabs.setSelectedIndex(i);
				break;
			}
		}
	}

	public void hideYourTurn() {
		SwingUtilities.invokeLater(new Runnable() { // This should get rid of the ArrayIndexOutOfBoundsException problem
			public void run() {
				for (int i = 0; i < tabs.getTabCount(); i++) {
					if (RealmTurnPanel.TAB_NAME.equals(tabs.getToolTipTextAt(i))) {
						tabs.removeTabAt(i);
						break;
					}
				}
				turnPanel = null;
				tabs.setSelectedIndex(0);
				updateControls();
			}
		});
	}

	public void showGameOver() {
		if (gameOverPanel==null) {
			if (getCharacter().isCharacter()) { // Only show game over if character is a character (not a hired leader or minion)
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						gameOverPanel = new GameOverPanel(character.getGameObject(),hostPrefs);
						tabs.addTab(null, ImageCache.getIcon("tab/gameover"), gameOverPanel, GameOverPanel.TAB_NAME);
						tabs.setSelectedIndex(tabs.getTabCount() - 1);
					}
				});
			}
		}
	}

	public void hideGameOver() {
		SwingUtilities.invokeLater(new Runnable() { // This should get rid of the ArrayIndexOutOfBoundsException problem
			public void run() {
				for (int i = 0; i < tabs.getTabCount(); i++) {
					if (GameOverPanel.TAB_NAME.equals(tabs.getToolTipTextAt(i))) {
						tabs.removeTabAt(i);
						tabs.setSelectedIndex(0);
						break;
					}
				}
				gameOverPanel = null;
			}
		});
	}
	
	private void doShoutOut(JFrame parent) {
		if (hostPrefs.hasPref(Constants.SR_NO_INFORMATION_SHARING)) {
			JOptionPane.showMessageDialog(parent, "You cannot share information about location of secret passages, hidden paths, treasure sites and TWT. This information is not transferable and can only be learned by following.", "Cannot share information", JOptionPane.ERROR_MESSAGE);
		}
		ArrayList<RealmComponent> chars = getTradeAvailableChars(gameHandler.getMainFrame(),showingTurn() || character.isFollowingCharacterPlayingTurn());
		if (chars!=null) {
			RealmObjectChooser chooser = new RealmObjectChooser("Shout discoveries out to who?",character.getGameObject().getGameData(),false);
			chooser.addComponentsToChoose(chars);
			chooser.setVisible(true);
			if (chooser.pressedOkay()) {
				for(GameObject go:chooser.getChosenObjects()) {
					CharacterWrapper shareWith = new CharacterWrapper(go);
					for(String tl:character.getCurrentClearingKnownTreasureLocations(false)) {
						if (!shareWith.hasTreasureLocationDiscovery(tl)) {
							shareWith.addTreasureLocationDiscovery(tl);
						}
					}
					for(String path:character.getCurrentClearingKnownHiddenPaths()) {
						if (!shareWith.hasHiddenPathDiscovery(path)) {
							shareWith.addHiddenPathDiscovery(path);
						}
					}
					for(String passage:character.getCurrentClearingKnownSecretPassages()) {
						if (!shareWith.hasSecretPassageDiscovery(passage)) {
							shareWith.addSecretPassageDiscovery(passage);
						}
					}
					for(String other:character.getCurrentClearingKnownOtherChits()) {
						if (!shareWith.hasOtherChitDiscovery(other)) {
							shareWith.addOtherChitDiscovery(other);
						}
					}
				}
			}
		}
	}

	public void doCharacterTrade(JFrame parent,boolean activePlayer) {
		if (character.isBlocked()) {
			JOptionPane.showMessageDialog(parent, "You cannot trade because you were blocked this turn.", "Cannot trade", JOptionPane.ERROR_MESSAGE);
			return;
		}
		CharacterTradeFrame frame = gameHandler.getCharacterTradeFrame();
		if (frame == null) {
			ArrayList<RealmComponent> chars = getTradeAvailableChars(parent,activePlayer);
			if (chars!=null) {
				RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parent, "Trade with whom?", true);
				chooser.addRealmComponents(chars, true);
				chooser.setVisible(true);
				if (chooser.getSelectedText() != null) {
					RealmComponent rc = chooser.getFirstSelectedComponent();
					CharacterWrapper trader = new CharacterWrapper(rc.getGameObject());
					gameHandler.broadcast(getCharacter().getGameObject().getName(), "Trades with " + trader.getGameObject().getName());
					RealmDirectInfoHolder info = new RealmDirectInfoHolder(gameHandler.getClient().getGameData(), getCharacter(), trader);
					info.setCommand(RealmDirectInfoHolder.TRADE_INIT);
					gameHandler.getClient().sendInfoDirect(trader.getPlayerName(), info.getInfo());
					gameHandler.createCharacterTradeFrame(getCharacter(), trader);
				}
			}
		}
		else {
			frame.toFront();
		}
	}
	
	private ArrayList<RealmComponent> getTradeAvailableChars(JFrame parent,boolean activePlayer) {
		ArrayList<RealmComponent> chars = ClearingUtility.findAllAwakeUnblockedCharactersInClearing(getCharacter());
		boolean areChars = !chars.isEmpty();
		// If the character is not playing a turn, then they may only trade with the one character currently playing
		if (!activePlayer) {
			// Now we need to filter out those characters that are not playing their turn
			ArrayList<RealmComponent> toKeep = new ArrayList<>();
			for (RealmComponent rc:chars) {
				CharacterWrapper trader = new CharacterWrapper(rc.getGameObject());
				if (trader.isPlayingTurn() || trader.isFollowingCharacterPlayingTurn() || trader.isDayEndTradingActive() || character.isDayEndTradingActive()) {
					toKeep.add(rc);
				}
			}
			chars = toKeep;
		}

		if (!chars.isEmpty()) {
			return chars;
		}
		if (areChars) {
			JOptionPane.showMessageDialog(
					parent,
					"None of the characters in the clearing are available for trading.",
					"Trade/Share",JOptionPane.WARNING_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(
					parent,
					"There are no characters in the clearing to trade with.",
					"Trade/Share",JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}
	
	protected void doAdvancement() {
		boolean change = false;
		ArrayList<CharacterActionChitComponent> list = character.getAdvancementChits();
		if (list.isEmpty()) {
			// No more advancement chits?  The extra chit marker increment is enough
			change = true;
		}
		else {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(gameHandler.getMainFrame(),"Advancement - Choose a New Chit",true);
			chooser.addRealmComponents(list,false);
			chooser.setVisible(true);
			if (chooser.getSelectedText()==null) {
				return;
			}
			CharacterActionChitComponent chit = (CharacterActionChitComponent)chooser.getFirstSelectedComponent();
			chit.getGameObject().setThisAttribute(Constants.CHIT_EARNED);
			// Make sure WITHER is applied (if cursed)
			if (chit.getEffortAsterisks()>0 && character.hasCurse(Constants.WITHER)) {
				chit.makeFatigued();
			}
			// Make sure any chit mods are applied
			character.updateChitEffects();
			if (chitPanel!=null) {
				chitPanel.updatePanel();
			}
			change = true;
		}
		character.setCharacterExtraChitMarkers(character.getCharacterExtraChitMarkers()+1);
		if (character.needsLevelUp()) {
			int newLevel = character.getCharacterLevel()+1;
			AbstractAction action = null;
			ArrayList<String> specialGains = new ArrayList<>();
			if (hostPrefs.hasPref(Constants.HOUSE1_ALLOW_LEVEL_GAINS_PAST_FOUR)) {
				switch(newLevel) {
					case 5:
						character.addGold(15);
						specialGains.add("15 extra gold.");
						break;
					case 6:
						action = new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								gameHandler.doPickTreasure(character);
							}
						};
						specialGains.add("Take one random Treasure card from any native group.");
						break;
					case 7:
						character.addFame(5);
						character.addNotoriety(10);
						specialGains.add("10 Notoriety and 5 Fame.");
						break;
					case 8:
						action = new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								gameHandler.doPickHorse(character);
							}
						};
						specialGains.add("Take one horse from any native group.");
						break;
					case 9:
						character.getGameObject().setThisAttribute(Constants.EXTRA_PHASE);
						String adv = "LEVEL 9 BONUS:  Gets bonus phase every day.";
						character.getGameObject().addAttributeListItem("level_4","advantages",adv);
						break;
					case 10:
						action = new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								gameHandler.doPickCounterOrSpell(character);
							}
						};
						specialGains.add("Take one weapon or armor counter from any native group; or record one extra spell of any type.");
						break;
					case 11:
						specialGains.add("The character is IMMORTAL.");
						break;
					default:
						break;
				}
			}
			
			ArrayList<String> oldAdvantages = character.getLevelAdvantages();
			ArrayList<String> oldOptAdvantages = character.getOptionalLevelAdvantages();
			character.setCharacterLevel(newLevel); // stage is controlled, so this doesn't need to be checked.
			character.updateLevelAttributes(gameHandler.getHostPrefs());
			// Make sure any chit mods are applied to bonus chits!
			character.updateChitEffects();
			if (chitPanel!=null) {
				chitPanel.updatePanel();
			}
			
			if (newLevel==3 && gameHandler.getHostPrefs().hasPref(Constants.EXP_DEV_3RD_REL)) {
				character.initRelationships(gameHandler.getHostPrefs(),true);
			}
			ArrayList<String> advantages = character.getLevelAdvantages();
			advantages.removeAll(oldAdvantages);
			ArrayList<String> optAdvantages = character.getOptionalLevelAdvantages();
			optAdvantages.removeAll(oldOptAdvantages);
			
			CharacterChitComponent token = (CharacterChitComponent)RealmComponent.getRealmComponent(character.getGameObject());
			String html = getLevelHtml(advantages,optAdvantages,specialGains);
			JOptionPane.showMessageDialog(gameHandler.getMainFrame(),html,"Level Up",JOptionPane.PLAIN_MESSAGE,token.getIcon());
			if (action!=null) {
				action.actionPerformed(new ActionEvent(this,0,""));
			}
			change = true;
			updateCharLabel();
			updateBadges();
		}
		updateDevProgress();
		if (change) {
			gameHandler.submitChanges();
			updateControls();
		}
	}
	protected String getLevelHtml(ArrayList<String> newAdvantages,ArrayList<String> newOptAdvantages,ArrayList<String> specialGains) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<h3>You leveled up!  You are now level ");
		int charLevel = character.getCharacterLevel();
		sb.append(charLevel);
		if (charLevel<=4) {
			sb.append(" and will be referred henceforth as the ");
			sb.append(character.getGameObject().getName());
		}
		sb.append(".</h3>");
		if (!newAdvantages.isEmpty()) {
			sb.append("<h2>New Advantages:</h2>");
			for (String val:newAdvantages) {
				sb.append("<li><b>");
				sb.append(val);
				sb.append("</b></li>");
			}
		}
		if (!newOptAdvantages.isEmpty()) {
			sb.append("<h2>New Optional Advantages:</h2>");
			for (String val:newOptAdvantages) {
				sb.append("<li><b>");
				sb.append(val);
				sb.append("</b></li>");
			}
		}
		if (!specialGains.isEmpty()) {
			sb.append("<h2>Special Gains:</h2>");
			for (String val:specialGains) {
				sb.append("<li><b>");
				sb.append(val);
				sb.append("</b></li>");
			}
		}
		sb.append("</html>");
		return sb.toString();
	}
	
	// Inner classes
	/**
	 * Testing
	 */
	public static void main(String[] args) throws Exception {
		ComponentTools.setSystemLookAndFeel();

		JFrame frame = new JFrame("test CharacterFrame");

		RealmGameHandler handler = new RealmGameHandler(null, "ip", 47474, "name", "pass", "", "", false);
		HostPrefWrapper.createDefaultHostPrefs(handler.getClient().getGameData());

		frame.setSize(1024, 768);
		frame.getContentPane().setLayout(new BorderLayout());
		JDesktopPane desktop = new JDesktopPane();
		frame.getContentPane().add(desktop, "Center");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		frame.setVisible(true);

		GamePool pool = new GamePool(handler.getClient().getGameData().getGameObjects());
		Collection<GameObject> characters = pool.find("character");
		GameObject character = characters.iterator().next();
		CharacterWrapper charw = new CharacterWrapper(character);
		charw.setCharacterLevel(4);
		charw.applyCurse(Constants.ASHES);
		charw.applyCurse(Constants.DISGUST);
		charw.setPlayerName("Game Master (Host)");

		GameObject tile = handler.getClient().getGameData().getGameObjectByName("Borderland");
		tile.add(character);
		character.setThisAttribute("clearing", 4);
		TileComponent rcTile = (TileComponent) RealmComponent.getRealmComponent(tile);
		rcTile.setDarkSideUp();
		charw.setWantsCombat(true);

		CharacterFrame intFrame = new CharacterFrame(handler, charw, ActionIcon.ACTION_ICON_FULL_TEXT);
		//		CharacterFrame intFrame = new CharacterFrame(handler,charw,ActionIcon.ACTION_ICON_ABBREV_TEXT);
		//		CharacterFrame intFrame = new CharacterFrame(handler,charw,ActionIcon.ACTION_ICON_NORMAL);
		desktop.add(intFrame);
		intFrame.setVisible(true);
	}
}