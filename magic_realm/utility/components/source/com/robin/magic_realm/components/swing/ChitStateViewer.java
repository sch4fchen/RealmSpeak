package com.robin.magic_realm.components.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.ChitComponent;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ChitStateViewer extends ChitManager {
	public ChitStateViewer(JFrame parent,CharacterWrapper character) {
		this(parent,character,true);
	}
	public ChitStateViewer(JFrame parent,CharacterWrapper character,boolean modal) {
		super(parent,character.getCharacterName()+" Chits",modal,character,0);
		initialize();
	}
	protected void finishedChitUpdate(ArrayList<ChitComponent> chits) {
		// override, so that nothing is logged here
	}
	protected String getActionName() {
		return "viewing";
	}
	protected boolean canClickNonActionChits() {
		return true;
	}
	protected boolean includeAlertedChits() {
		return true;
	}
//	private void updateColors() {
//		Collection colors = character.getChitColorSources();
//		colors.addAll(character.getInfiniteColorSources());
//		
//		// Update the GUI
//		Box box = Box.createHorizontalBox();
//		box.add(Box.createHorizontalGlue());
//		for (Iterator i=colors.iterator();i.hasNext();) {
//			ColorMagic cm = (ColorMagic)i.next();
//			JLabel cl = new JLabel();
//			cl.setIcon(cm.getIcon());
//			box.add(cl);
//		}
//		box.add(Box.createHorizontalGlue());
//		
//		southDisplay.add(box,"Center");
//	}
	protected boolean canPressOkay() {
		return true;
	}
	protected int totalPossibleCount() {
		return 0;
	}
	protected boolean canClickActive(CharacterActionChitComponent clickedChit) {
		return true;
	}
	protected boolean canClickFatigue(CharacterActionChitComponent clickedChit) {
		return true;
	}
	protected boolean canClickWound(CharacterActionChitComponent clickedChit) {
		return true;
	}
	protected void activeClick(CharacterActionChitComponent clickedChit) {
		// does nothing
	}
	protected void fatigueClick(CharacterActionChitComponent clickedChit) {
		// does nothing
	}
	protected void woundClick(CharacterActionChitComponent clickedChit) {
		// does nothing
	}
	protected void updateStatusLabel(JLabel label) {
		// does nothing
	}
	/*
	 * Testing
	 */
	public static void main(String[] args) {
		RealmUtility.setupTextType();
		ComponentTools.setSystemLookAndFeel();
		
		System.out.print("loading...");
		RealmLoader loader = new RealmLoader();
		System.out.println("Done");
//		Collection chars = loader.getCharacters();
		GameObject character = loader.getData().getGameObjectByName("Wizard");
		System.out.println(character);
		
		CharacterWrapper wrapper = new CharacterWrapper(character);
		
		// artifically fatigue and wound some chits
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>(wrapper.getAllChits());
		for (int i=2;i<5;i+=2) {
			CharacterActionChitComponent aChit = list.get(i);
			aChit.makeFatigued();
		}
		for (int i=8;i<10;i++) {
			CharacterActionChitComponent aChit = list.get(i);
			aChit.makeWounded();
		}
//		(new Curse(new JFrame())).applyThree(wrapper);
		
		ChitStateViewer man = new ChitStateViewer(new JFrame(),wrapper);
		man.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.out.println("Exiting");
				System.exit(0);
			}
		});
		System.out.println("Count Too Large = "+man.isCountTooLarge());
		man.setVisible(true);
	}
}