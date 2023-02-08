package com.robin.magic_realm.components.utility;

import com.robin.general.sound.SoundBlock;
import com.robin.general.sound.SoundCache;

public class SoundUtility {
	
	private static int BUFFER = 3;
	
	private static SoundBlock AttentionSound = new SoundBlock("attention_quick",BUFFER);
	private static SoundBlock ClickSound = new SoundBlock("click3",BUFFER);
	
	public static void setSoundEnabled(boolean val) {
		SoundCache.setSoundEnabled(val);
	}
	public static void playAttention() {
		SoundCache.playClip(AttentionSound.getNextClip());
	}
	public static void playClick() {
		SoundCache.playClip(ClickSound.getNextClip());
	}
}