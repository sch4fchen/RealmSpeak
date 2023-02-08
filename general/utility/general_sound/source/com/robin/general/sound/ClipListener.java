package com.robin.general.sound;

import javax.sound.sampled.*;

public class ClipListener implements LineListener {
	private Clip clip;
	public ClipListener(Clip clip) {
		this.clip = clip;
	}
	public void update(LineEvent ev) {
        if (ev.getType() == LineEvent.Type.STOP) { 
        	clip.stop();
        	clip.setFramePosition(0);
        }
	}
}