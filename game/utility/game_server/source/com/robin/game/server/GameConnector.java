package com.robin.game.server;

import java.net.*;

public class GameConnector extends Thread {
	private static final String THREAD_NAME = "GameConnector.ThreadName";

	protected GameHost host;
	protected int port;
	protected ServerSocket listener;
	protected String ipAddress;
	
	protected boolean alive;
	
	public GameConnector(GameHost host,int port) {
		this.host = host;
		this.port = port;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch(UnknownHostException ex) {
			ex.printStackTrace();
		}
		setName(THREAD_NAME);
	}
	public int getPort() {
		return port;
	}
	public String getIPAddress() {
		return ipAddress;
	}
	public void kill() {
		alive = false; // this won't have an affect until AFTER the next accepted connection!
		// so lets force it!
		try {
			Socket sock = new Socket(ipAddress,port);
			sock.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void run() {
		try {
			listener = new ServerSocket(port);
			System.out.println("Listening on port " + listener.getLocalPort());
			alive = true;
			while(alive) {
				Socket connection = listener.accept();
				if (alive) {
					try {
						connection.setSoTimeout(GameNet.DEFAULT_TIMEOUT_MS);
						connection.setTcpNoDelay(true); // Data will be sent earlier, at the cost of an increase in bandwidth consumption.
														// See http://www.davidreilly.com/java/java_network_programming/#3.3
						host.addConnection(connection);
					}
					catch(SocketException ex) {
						System.err.println("Unable to accept connection from "+connection.getInetAddress()+".  Stack trace follows:");
						ex.printStackTrace();
					}
				}
			}
			listener.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}