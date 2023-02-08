package com.robin.game.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class GameConnection extends Thread {
	
	private Socket connection;
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public abstract void run();
	
	public void setConnection(Socket con) {
		connection = con;
	}
	public Socket getConnection() {
		return connection;
	}
	public ObjectOutputStream getOutputStream() throws Exception {
		if (out==null) {
			out = new ObjectOutputStream(connection.getOutputStream());
		}
		return out;
	}
	public ObjectInputStream getInputStream() throws Exception {
		if (in==null) {
			in = new ObjectInputStream(connection.getInputStream());
		}
		return in;
	}
}