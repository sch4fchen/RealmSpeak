package com.robin.magic_realm.components.quest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.robin.general.swing.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestView extends JPanel implements Scrollable {

	private JLabel questName;
	private JTextArea questDescription;
	private JList<QuestJournalEntry> questJournalList;
	private DefaultListModel<QuestJournalEntry> model;
	private Quest quest;

	private JSplitPaneImproved splitPane;
	
	private ActionListener actionListener;

	public QuestView() {
		this(null);
	}
	public QuestView(ActionListener listener) {
		initComponents(listener);
	}

	private QuestStepView tree;
	private JLabel stepLabel;
	private void initComponents(ActionListener listener) {
		actionListener = listener;
		setLayout(new BorderLayout());
		Box box = Box.createHorizontalBox();
		questName = new JLabel();
		questName.setFont(QuestGuiConstants.QuestTitleFont);
		box.add(questName);
		box.add(Box.createHorizontalGlue());
		if (actionListener!=null) {
			JButton activateQuest = new JButton("Activate");
			activateQuest.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					ev = new ActionEvent(quest,0,"");
					actionListener.actionPerformed(ev);
				}
			});
			activateQuest.setToolTipText("Activate the Quest");
			box.add(activateQuest);
		}
		JButton showTreeButton = new JButton(ImageCache.getIcon("quests/token",30));
		showTreeButton.setToolTipText("Quest Steps Overview");
		showTreeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (quest==null) return;
				JPanel panel = new JPanel(new BorderLayout());
				stepLabel = new JLabel();
				stepLabel.setFont(QuestGuiConstants.QuestStepNameFont);
				panel.add(stepLabel,BorderLayout.NORTH);
				tree = new QuestStepView();
				tree.setOrientation(SwingConstants.VERTICAL);
				tree.updateSteps(quest.getSteps());
				tree.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						stepLabel.setText(tree.getSelectedStep().getName());
					}
				});
				tree.setFirstSelectedStep();
				panel.add(tree,BorderLayout.CENTER);
				Dimension minSize = panel.getPreferredSize();
				ComponentTools.lockComponentSize(panel,Math.max(minSize.width,640),Math.max(minSize.height,720));
				JOptionPane.showMessageDialog(QuestView.this,panel,"Quest Steps Overview - "+quest.getName(),JOptionPane.INFORMATION_MESSAGE,ImageCache.getIcon("quests/token"));
			}
		});
		ComponentTools.lockComponentSize(showTreeButton,24,24);
		box.add(showTreeButton);
		add(box, BorderLayout.NORTH);

		questDescription = new JTextArea();
		questDescription.setLineWrap(true);
		questDescription.setWrapStyleWord(true);
		questDescription.setEditable(false);
		questDescription.setFont(QuestGuiConstants.QuestDescriptionFont);
		questDescription.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		model = new DefaultListModel<>();
		questJournalList = new JList<>(model);
		questJournalList.setCellRenderer(new JournalLineRenderer());
		splitPane = new JSplitPaneImproved(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(questDescription), new JScrollPane(questJournalList));
		add(splitPane, BorderLayout.CENTER);
	}
	
	public void clear() {
		this.quest = null;
		questName.setText("");
		questDescription.setText("");
		model.clear();
		splitPane.setDividerLocation(1.0);
		repaint();
		revalidate();
	}

	public void updatePanel(Quest quest, CharacterWrapper character) {
		this.quest = quest;
		questName.setText(quest == null ? "" : quest.getName());
		String description = quest == null ? "" : quest.getDescription();
		String charactersName = character.getName() == null ? "adventurer" : character.getPlayerName();
		description = description.replaceAll(Constants.CHARACTERS_NAME_PLACEHOLDER, charactersName);
		questDescription.setText(description);
		model.clear();
		for(QuestJournalEntry entry:quest.getJournalEntries()) {
			model.addElement(entry);
		}
		splitPane.setDividerLocation(questJournalList.getModel().getSize() == 0 ? 1.0 : 0.5);
		repaint();
		revalidate();
	}

	// Scrollable interface
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return Math.max(visibleRect.height * 9 / 10, 1);
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return Math.max(visibleRect.height / 10, 1);
	}

	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	private class JournalLineRenderer extends JLabel implements ListCellRenderer {

		public JournalLineRenderer() {
			setOpaque(true);
			setIconTextGap(12);
			setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createEtchedBorder(),
							BorderFactory.createEmptyBorder(4,4,4,4))
					);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			QuestJournalEntry entry = (QuestJournalEntry) value;
			setText(entry.getText());
			setIcon(entry.getEntryType().getIcon());
			if (isSelected) {
				setBackground(HIGHLIGHT_COLOR);
				setForeground(Color.white);
			}
			else {
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}

	public static void main(String[] args) {
		final QuestView view = new QuestView();
		final Quest quest = QuestLoader.loadQuestByName("Dragon Slayer");
		view.updatePanel(quest, null);
		JDialog frame = new JDialog();
		frame.setLayout(new BorderLayout());
		frame.setSize(600, 800);
		frame.setLocationRelativeTo(null);
		frame.setModal(true);
		frame.add(view);
		JButton button = new JButton("update");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				quest.addJournalEntry("adsf", QuestStepState.Pending, "Foobar!");
				quest.addJournalEntry("adsfs", QuestStepState.Finished, "Foobarasdf !");
				quest.addJournalEntry("adsfss", QuestStepState.Failed, "Foobar! 123 ");
				view.updatePanel(quest, null);
			}
		});
		frame.add(button,BorderLayout.SOUTH);
		frame.setVisible(true);
		System.exit(0);
	}
}