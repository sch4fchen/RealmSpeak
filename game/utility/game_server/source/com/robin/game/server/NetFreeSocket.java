package com.robin.game.server;

import java.io.*;
import java.net.Socket;

/**
 * A class to act like a socket, but not use the network!
 */
public class NetFreeSocket extends Socket {
	
	private PipedOutputStream outStream;
	private PipedInputStream inStream;
	
	public NetFreeSocket() {
		outStream = new PipedOutputStream();
		inStream = new PipedInputStream();
	}
	public void connect(NetFreeSocket socket) {
		try {
			outStream.connect(socket.inStream);
			inStream.connect(socket.outStream);
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public InputStream getInputStream() throws IOException {
		return inStream;
	}

	public OutputStream getOutputStream() throws IOException {
		return outStream;
	}
	
	public static void main(String[] args) {
		// Can I connect a server and client without using a connected socket?  Let's try!
		try {
			NetFreeSocket server = new NetFreeSocket();
			NetFreeSocket client = new NetFreeSocket();
			server.connect(client);
			System.out.println("writing...");
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			out.writeObject("Test");
			out.writeObject("One");
			out.writeObject("Three");
			System.out.println("done");
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			System.out.println("read="+in.readObject());
			System.out.println("read="+in.readObject());
			System.out.println("read="+in.readObject());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}