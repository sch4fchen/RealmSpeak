package com.robin.magic_realm.components.utility;

import com.robin.game.server.GameClient;
import com.robin.magic_realm.components.swing.RealmLogWindow;

public class RealmLogging {
	public static final String BATTLE = "__battle__";
	public static final String LOG_INDENT = "__log_indent__";
	
	public static final String LOG_INDENT_CLEAR = "clear";
	public static final String LOG_INDENT_INCREMENT = "increment";
	
	
	public static void logMessage(String key,String message) {
		if (GameClient.GetMostRecentClient()!=null) {
			GameClient.broadcastClient(key,message);
		}
		else {
			// No game client (combat simulator)?  Report it directly to the log window.
			RealmLogWindow.getSingleton().addMessage(key,message);
		}
	}
	public static void clearIndent() {
		logMessage(LOG_INDENT,LOG_INDENT_CLEAR);
	}
	public static void incrementIndent() {
		logMessage(LOG_INDENT,LOG_INDENT_INCREMENT);
	}
}