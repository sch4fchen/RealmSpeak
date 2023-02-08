package com.robin.magic_realm.RealmCharacterWeb.build;

import java.io.*;

public class Builder {
	protected void dumpString(File file,String string) {
		try {
			PrintStream dump = new PrintStream(file);
			dump.println(string);
			dump.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}