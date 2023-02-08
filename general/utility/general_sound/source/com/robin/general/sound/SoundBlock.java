package com.robin.general.sound;

import javax.sound.sampled.Clip;

/**
 * This class will manage multiple copies of the sound, so that multiple sounds can be heard (if supported)
 */
public class SoundBlock {
	
	private int index;
	private Clip[] clip;
	
	public SoundBlock(String soundName,int copies) {
		// Load multiple copies of the sound
		clip = new Clip[copies];
		for (int i=0;i<copies;i++) {
			clip[i] = SoundCache.loadClip("sounds/"+soundName+".wav");
		}
		index = 0;
	}
	public Clip getNextClip() {
		Clip ret = clip[index];
		index++;
		index %= clip.length;
		return ret;
	}
}