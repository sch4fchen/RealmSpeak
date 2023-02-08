package com.robin.magic_realm.components.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.AggressiveDialog;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

/**
 * A chooser that will display a panel of buttons, each holding text and a number of RealmComponents.  Clicking
 * a button makes the decision, and the window closes.  The chooser will return the text and the components.
 */
public class RealmComponentDisplayDialog extends AggressiveDialog {
	private Font TITLE_FONT = new Font("Dialog", Font.BOLD, 14);

	private ArrayList<RealmComponent> components;
	private JPanel displayPanel;
	private JButton okayButton;

	public RealmComponentDisplayDialog(JFrame parent, String title,String message) {
		super(parent, title, true);
		components = new ArrayList<>();
		initComponents(message);
//		updateLayout();
	}

	private void initComponents(String message) {
		displayPanel = new JPanel();
		getContentPane().setLayout(new BorderLayout(5, 5));
		getContentPane().add(displayPanel, "Center");
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		okayButton = new JButton("Okay");
		okayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				cleanExit();
			}
		});
		box.add(okayButton);
		box.add(Box.createHorizontalGlue());
		getContentPane().add(box, "South");
		JLabel titleLabel = new JLabel(message);
		titleLabel.setFont(TITLE_FONT);
		getContentPane().add(titleLabel, "North");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	public void setVisible(boolean val) {
		if (val) {
			updateLayout();
		}
		super.setVisible(val);
	}
	
	public void addRealmComponent(RealmComponent rc) {
		components.add(rc);
//		updateLayout();
	}
	public void addRealmComponents(Collection<RealmComponent> rcs) {
		components.addAll(rcs);
//		updateLayout();
	}
	public void addGameObjects(Collection<GameObject> gos) {
		for (GameObject go : gos) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			components.add(rc);
		}
//		updateLayout();
	}

	private void updateLayout() {
		int totalComponents = components.size();
		int columns = (int) Math.sqrt(totalComponents);
		if (columns == 0)
			columns = 1;
		int rows = totalComponents / columns;
		if (rows == 0)
			rows = 1;
		if ((columns * rows) < totalComponents) { // make sure there is enough rows (rounding up)
			rows++;
		}
		int cellCount = rows * columns;
		displayPanel.removeAll();
		displayPanel.setLayout(new GridLayout(rows, columns));

		for (RealmComponent rc : components) {
			displayPanel.add(rc);
			cellCount--;
		}
		// Fill in the rest of the last row (if needed)
		for (int i = 0; i < cellCount; i++) {
			displayPanel.add(Box.createGlue());
		}
		pack();
		setLocationRelativeTo(null);
	}

	private void cleanExit() {
		setVisible(false);
		dispose();
	}

	public static void main(String[] args) {
		ComponentTools.setSystemLookAndFeel();

		System.out.print("loading...");
		RealmLoader loader = new RealmLoader();
		System.out.println("Done");
//		Collection chars = loader.getCharacters();
		GameObject character = loader.getData().getGameObjectByName("Wizard");
		System.out.println(character);

		// artifically fatigue and wound some chits
		CharacterWrapper wrapper = new CharacterWrapper(character);
		RealmComponentDisplayDialog display = new RealmComponentDisplayDialog(new JFrame(), "Hey!","Look at these:");
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>(wrapper.getAllChits());
		for (int i = 0; i < 10; i += 2) {
			CharacterActionChitComponent c1 = (CharacterActionChitComponent) list.get(i);
			CharacterActionChitComponent c2 = (CharacterActionChitComponent) list.get(i + 1);
			display.addRealmComponent(c1);
			display.addRealmComponent(c2);
		}
		display.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.out.println("Exiting");
				System.exit(0);
			}
		});
		display.setVisible(true);
		
		System.exit(0);
	}
}