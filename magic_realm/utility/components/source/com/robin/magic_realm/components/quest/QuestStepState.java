package com.robin.magic_realm.components.quest;

import javax.swing.ImageIcon;

import com.robin.general.swing.ImageCache;

public enum QuestStepState {
	None,		// This only happens when you are building quests!
	Pending,	// Gray - Still steps that must be completed before this step can be considered
	Ready,		// Gold - Requirements have not been met yet, but should be checked each time
	Finished,	// Gold with green check - Requirements have been met, and rewards have been divied out
	Failed,		// Gold with red X - Another step that preempts this step was already completed (also, if step becomes impossible...)
	;
	public static int tokenSizePercent = 60;
	private static ImageIcon readyIcon = ImageCache.getIcon("quests/token",tokenSizePercent);
	private static ImageIcon pendingIcon = ImageCache.getIcon("quests/tokenpending",tokenSizePercent);
	private static ImageIcon finishedIcon = ImageCache.getIcon("quests/tokendone",tokenSizePercent);
	private static ImageIcon failedIcon = ImageCache.getIcon("quests/tokenfail",tokenSizePercent);
	
	public ImageIcon getIcon() {
		switch(this) {
			case None:			return readyIcon;
			case Pending:		return pendingIcon;
			case Ready:			return readyIcon;
			case Finished:		return finishedIcon;
			case Failed:		return failedIcon;
		}
		return null;
	}
	public String getTooltip() {
		switch(this) {
			case Pending:		return "PENDING - Previous steps must be completed first.";
			case Ready:			return "READY - Meet requirements to get reward, and move to next step.";
			case Finished:		return "FINISHED - Step has been completed.";
			case Failed:		return "FAILED - Step was failed.";
		}
		return null;
	}
}