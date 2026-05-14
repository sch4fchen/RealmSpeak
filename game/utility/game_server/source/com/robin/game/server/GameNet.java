package com.robin.game.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public abstract class GameNet extends Thread {

	// BUG: DEFAULT_TIMEOUT_MS = 10000 (10 seconds) is far too short for gameplay.
	//
	// SO_TIMEOUT is set on every client socket (GameClient.java, GameConnector.java) and fires
	// when a blocking read() call does not receive data within the timeout period. During normal
	// play the GameClient thread polls continuously via REQUEST_IDLE, so the socket rarely idles
	// long enough to trigger it. However, modal combat dialogs (e.g. the End Battle yes/no dialog
	// in CombatFrame) can block the client long enough for the timeout to fire:
	//
	//   1. One combatant clicks End — the server broadcasts a vote request to all participants.
	//   2. Each client shows the End Battle dialog (blocking the EDT for that player).
	//   3. A player takes more than 10 seconds to respond.
	//   4. The socket read times out → SocketTimeoutException → GameClient.run() catches it
	//      as an unexpected disconnect → the server calls removeServer() → fireServerLost().
	//   5. The remaining clients receive "Broken pipe" when they try to write to the now-closed
	//      socket, or the server broadcasts a game-over notice.
	//   6. The End Battle vote count never reaches quorum → all combat windows show "Observing"
	//      with no End/Next buttons, and the combat is permanently frozen.
	//
	// Fix options (to be implemented):
	//   (A) Raise DEFAULT_TIMEOUT_MS to a value that is safely longer than any realistic player
	//       think time (e.g. 5 minutes = 300_000 ms). Simple and low-risk.
	//   (B) Set SO_TIMEOUT to 0 (no timeout) for the game socket and rely on the application-
	//       level disconnect detection instead. Requires companion keepalive or heartbeat logic
	//       so the server can still detect genuinely dead clients.
	//   (C) Have the client send a lightweight keepalive message while a modal dialog is open,
	//       resetting the idle clock on the server side.
	//
	// Option A is the lowest-risk change. Option B or C would be cleaner long-term but require
	// more rework, especially if the client-reconnect feature is also being integrated.
	public static int DEFAULT_TIMEOUT_MS = 10000; // TODO: raise this — 10 s is too short for gameplay dialogs
	
	protected Socket connection;
	protected ObjectOutputStream out = null;
	protected ObjectInputStream in = null;
	
	protected ObjectOutputStream getOutputStream() throws IOException {
		if (out==null) {
			out = new ObjectOutputStream(connection.getOutputStream());
		}
		return out;
	}
	protected ObjectInputStream getInputStream() throws IOException {
		if (in==null) {
			in = new ObjectInputStream(connection.getInputStream());
		}
		return in;
	}
	protected void flush() throws IOException {
		getOutputStream().flush();
		getOutputStream().reset();
	}
	protected void writeCollection(ArrayList c) throws IOException {
		getOutputStream().writeInt(c.size());
		while(!c.isEmpty()) {
			getOutputStream().writeObject(c.remove(0));
		}
		flush();
	}
	protected ArrayList readCollection() throws IOException,ClassNotFoundException {
		int size = getInputStream().readInt();
		ArrayList list = new ArrayList();
		for (int i=0;i<size;i++) {
			list.add(getInputStream().readObject());
		}
		return list;
	}
}