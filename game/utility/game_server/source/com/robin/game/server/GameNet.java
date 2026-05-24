package com.robin.game.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public abstract class GameNet extends Thread {
	
	// Root cause of the End Battle combat freeze: DEFAULT_TIMEOUT_MS = 10000 (10 seconds).
	//
	// SO_TIMEOUT is set on every server-side socket (via GameConnector) and every client-side
	// socket (via GameClient). It fires when a blocking read() call does not receive data within
	// the timeout period. During normal play the GameClient thread polls continuously via
	// REQUEST_IDLE, so the socket rarely idles long enough to trigger it. However, when
	// handleDirectInfo() showed a modal combat dialog directly on the GameClient thread, it
	// blocked that thread and caused the server-side timeout to fire:
	//
	//   1. One combatant clicks End — the server broadcasts a QUERY_YN to all participants.
	//   2. Each client's GameClient thread enters handleDirectInfo() and (originally) called
	//      JOptionPane.showConfirmDialog() there, blocking that thread.
	//   3. Blocked GameClient thread sends no REQUEST_IDLE messages to the server.
	//   4. The server-side SO_TIMEOUT fires (set by GameConnector) → SocketTimeoutException
	//      in GameServer.processNextRequest() → GameServer's run() loop terminates →
	//      host.removeServer() → fireServerLost() → "Game over!!" broadcast.
	//   5. Remaining clients receive "Broken pipe" when they try to write their responses.
	//   6. The End Battle vote never reaches quorum → all combat windows show "Observing"
	//      with no End/Next buttons, and combat is permanently frozen.
	//
	// Fix (applied in RealmGameHandler.handleDirectInfo, QUERY_YN branch): dispatch the dialog
	// to the EDT via SwingUtilities.invokeLater(). handleDirectInfo() returns immediately,
	// the GameClient thread keeps polling normally, and the server timeout is never hit
	// regardless of how long the player takes. DEFAULT_TIMEOUT_MS is left unchanged.
	public static int DEFAULT_TIMEOUT_MS = 10000;
	
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