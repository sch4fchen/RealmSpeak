package com.robin.magic_realm.components.quest;

public enum ChitAcquisitionType {
	Available,	// Only take if not dead, and not already hired/controlled.
	Steal,		// Take chit regardless of current whereabouts.
	Clone,		// Make a clone of the chit.
	Lose,		// Chit is lost
}