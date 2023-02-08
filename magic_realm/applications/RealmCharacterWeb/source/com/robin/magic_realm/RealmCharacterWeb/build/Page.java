package com.robin.magic_realm.RealmCharacterWeb.build;

import java.io.*;

import com.robin.general.io.ZipUtilities;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;
import com.robin.magic_realm.RealmCharacterWeb.RscharLayout;
import com.robin.magic_realm.components.CharacterInfoCard;

public class Page extends Builder {
	
	private String name;
	private RscharLayout layout;
	private File htmlFile;
	
	public Page(RscharLayout layout) {
		this.layout = layout;
		name = layout.getModel().getCharacter().getGameObject().getName();
	}
	public RscharLayout getLayout() {
		return layout;
	}
	public File getHtml() {
		return htmlFile;
	}
	public void create(File folder) {
		createZip(new File(folder.getAbsolutePath()+File.separator+name+".zip"));
		createImage(new File(folder.getAbsolutePath()+File.separator+name+".jpg"));
		htmlFile = new File(folder.getAbsolutePath()+File.separator+name+"_final.html");
		createHtml(htmlFile);
	}
	private void createImage(File file) {
		CharacterInfoCard card = layout.getModel().getCard();
		RealmCharacterBuilderModel.exportImage(file,card.getImageIcon(false),"JPG");
	}
	private void createZip(File file) {
		File[] source = new File[1];
		source[0] = layout.getFile();
		ZipUtilities.zip(file,source);
	}
	private void createHtml(File file) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body background=\"../../backing.png\">\n");
		sb.append("<center><img SRC=\"");
		sb.append(name);
		sb.append(".jpg\">\n");
		sb.append("<br><b><font size=+3>");
		sb.append(name);
		sb.append("</font></b>\n");
		sb.append("<br>");
		sb.append("<font size=+2><a href=\"");
		sb.append(name);
		sb.append(".zip\"");
		sb.append(">Download Now!</a></font>\n");
		sb.append("</center></body></html>");
		dumpString(file,sb.toString());
	}
}
/*
<html><body
background="../../rlmback2.gif">
<center><img SRC="bard.jpg">
<br><b><font size=+3>Bard</font></b>
<br>
<font size=+2><a href="Bard.zip">Download Now!</a></font>
</center></body></html>
*/