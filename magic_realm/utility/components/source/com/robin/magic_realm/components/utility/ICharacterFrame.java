package com.robin.magic_realm.components.utility;

import javax.swing.JFrame;

import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public interface ICharacterFrame {
	public void doCharacterTrade(JFrame parent,boolean activePlayer);
	public CharacterWrapper getCharacter();
}