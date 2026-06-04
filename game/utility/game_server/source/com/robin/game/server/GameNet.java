package com.robin.game.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public abstract class GameNet extends Thread {
	
	public static int DEFAULT_TIMEOUT_MS = 20000; // 20 seconds — allows for large RESPOND_NEED_UPDATE transfers and GC pauses
	
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