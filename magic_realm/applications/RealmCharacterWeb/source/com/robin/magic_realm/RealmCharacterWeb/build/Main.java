package com.robin.magic_realm.RealmCharacterWeb.build;

import java.io.*;

import javax.swing.ImageIcon;

import com.robin.general.swing.IconFactory;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;

public class Main extends Builder {
	public Main() {
	}
	public void create(File folder) {
		createIcon(folder,"images/rlmback2.gif","backing.png");
		createIcon(folder,"images/arrow.gif","arrow.png");
		createIcon(folder,"images/arrowoff.gif","arrowoff.png");
		
		createTitle(new File(folder.getAbsolutePath()+File.separator+"title.htm"));
		createMain(new File(folder.getAbsolutePath()+File.separator+"main.htm"));
	}
	private static void createIcon(File folder,String iconPath,String name) {
		ImageIcon icon = IconFactory.findIcon(iconPath);
		RealmCharacterBuilderModel.exportImage(new File(folder.getAbsolutePath()+File.separator+name),icon);
	}
	private void createTitle(File file) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body background=\"backing.png\"></body></html>");
		dumpString(file,sb.toString());
	}
	private void createMain(File file) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><title>Custom RealmSpeak Characters</title>");
		sb.append("</head><frameset cols=\"220,*\" border=5 resize=\"false\">");
		sb.append("<frame src=\"menu.htm\" scrolling=\"on\" NORESIZE>");
		sb.append("<frame src=\"title.htm\" name=\"Data\" scrolling=\"auto\">");
		sb.append("</frameset><noframes>");
		sb.append("<body>Oops. This browser does not support frames.</body></noframes></html>");
		dumpString(file,sb.toString());
	}
}
/*

title.htm
<html><body background="backing.png"></body></html>

main.htm
<html><head><title>Custom RealmSpeak Characters</title>
</head><frameset cols="220,*" border=5 resize="false">
<frame src="menu.htm" scrolling="on" NORESIZE>
<frame src="title.htm" name="Data" scrolling="auto">
</frameset><noframes>
<body>Oops. This browser does not support frames.</body></noframes></html>


*/