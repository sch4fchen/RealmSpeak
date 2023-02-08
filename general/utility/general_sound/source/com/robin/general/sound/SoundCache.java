package com.robin.general.sound;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.sound.sampled.*;

import com.robin.general.io.ResourceFinder;

public class SoundCache {
	private static Hashtable<String,Clip> cache = new Hashtable<>();
	
	private static double currentGain = 0.5;
	
	private static boolean useSound = false;
	
	public static void setSoundEnabled(boolean val) {
		useSound = val;
	}
	public static boolean isSoundEnabled() {
		return useSound;
	}
	
	public static Clip getClip(String name) {
		String soundPath = "sounds/"+name;
		Clip clip = cache.get(soundPath);
		if (clip==null) {
			clip = loadClip(soundPath);
			cache.put(soundPath,clip);
		}
		
		return clip;
	}
	
	protected static Clip loadClip(String soundPath) {
		Clip clip = null;
		try {
			InputStream stream = ResourceFinder.getInputStream(soundPath);
			if (stream!=null) {
				InputStream bufferedStream = new BufferedInputStream(stream);
				AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedStream);
				clip = loadClip(ais);
				adjustVolume(clip,currentGain);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			useSound = false; // issue with sound here, then just turn it off
		}
		if (clip==null) {
			System.out.println("failed to read sound at "+soundPath);
//			(new Exception()).printStackTrace(); // so I can see where this happens!
		}
		return clip;
	}
	private static Clip loadClip(AudioInputStream ais) {
		AudioFormat format = ais.getFormat();

		DataLine.Info info = new DataLine.Info(
		                  Clip.class, 
		                  ais.getFormat(), 
		                  ((int) ais.getFrameLength() *
		                      format.getFrameSize()));

		try {
			Clip clip = (Clip) AudioSystem.getLine(info);
	        clip.addLineListener(new ClipListener(clip));
			clip.open(ais);
			return clip;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			useSound = false; // issue with sound here, then just turn it off
		}
		return null;
	}
	public static void playClip(Clip clip) {
		if (clip!=null && useSound) {
			clip.stop();
			clip.flush();
			clip.setFramePosition(0);
			clip.start();
		}
	}
	public static void playSound(String name) {
		if (useSound && name!=null) {
			String soundPath = "sounds/"+name;
			Clip clip = cache.get(name);
			if (clip==null) {
				try {
					InputStream stream = ResourceFinder.getInputStream(soundPath);
					AudioInputStream ais = AudioSystem.getAudioInputStream(stream);
					AudioFormat format = ais.getFormat();

					DataLine.Info info = new DataLine.Info(
					                  Clip.class, 
					                  ais.getFormat(), 
					                  ((int) ais.getFrameLength() *
					                      format.getFrameSize()));

					clip = (Clip) AudioSystem.getLine(info);
	                clip.addLineListener(new ClipListener(clip));
					clip.open(ais);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				if (clip!=null) {
					cache.put(name,clip);
				}
				else {
					System.out.println("failed to read "+soundPath);
					(new Exception()).printStackTrace(); // so I can see where this happens!
				}
			}
			playClip(clip);
		}
	}
	public static double getVolume() {
		return currentGain;
	}
	public static void setVolume(double gain) {
		if (gain!=currentGain) {
			if (gain<0.0 || gain>1.0) {
				throw new IllegalArgumentException("gain must be from 0 to 1");
			}
			currentGain = gain;
			for (Clip clip : cache.values()) {
				adjustVolume(clip,currentGain);
			}
		}
	}
	/**
	 * @param clip		The clip to be adjusted
	 * @param gain		A number between 0 (quiet) and 1 (loud)
	 */
	private static void adjustVolume(Clip clip,double gain) {
		// Set Volume
		FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
//		double gain = .5D;    // number between 0 and 1 (loudest)
		float dB = (float)(Math.log(gain)/Math.log(10.0)*20.0);
		gainControl.setValue(dB);
		    
//		// Mute On
//		BooleanControl muteControl = (BooleanControl)clip.getControl(BooleanControl.Type.MUTE);
//		muteControl.setValue(true);
//		    
//		// Mute Off
//		muteControl.setValue(false);
	}
	
	public static void main(String[] args) {
		SoundCache.setSoundEnabled(true);
		SoundCache.playSound("test.wav");
		SoundCache.playSound("boing.wav");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}