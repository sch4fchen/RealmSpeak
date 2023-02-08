package com.robin.magic_realm.components.quest;

import javax.swing.ImageIcon;

import com.robin.general.swing.ImageCache;

public enum QuestState {
	New,
	Assigned,
	Active,
	Failed,
	Complete,
	;
	public static int tokenSizePercent = 80;
	private static ImageIcon assignedIcon = ImageCache.getIcon("quests/tokenpending",tokenSizePercent);
	private static ImageIcon activeIcon = ImageCache.getIcon("quests/token",tokenSizePercent);
	private static ImageIcon failedIcon = ImageCache.getIcon("quests/tokenfail",tokenSizePercent);
	private static ImageIcon completeIcon = ImageCache.getIcon("quests/tokendone",tokenSizePercent);
	
	private static ImageIcon smallFailedIcon = ImageCache.getIcon("quests/tokenfail",30);
	private static ImageIcon smallCompleteIcon = ImageCache.getIcon("quests/tokendone",30);
	
	public ImageIcon getIcon() {
		switch(this) {
			case New:			return assignedIcon; // not really used anyway
			case Assigned:		return assignedIcon;
			case Active:		return activeIcon;
			case Failed:		return failedIcon;
			case Complete:		return completeIcon;
		}
		return null;
	}
	public ImageIcon getSmallIcon() {
		switch(this) {
			case Failed:		return smallFailedIcon;
			case Complete:		return smallCompleteIcon;
			default: 			return null;
		}
	}
	public boolean isFinished() {
		return this==Failed || this==Complete;
	}
}