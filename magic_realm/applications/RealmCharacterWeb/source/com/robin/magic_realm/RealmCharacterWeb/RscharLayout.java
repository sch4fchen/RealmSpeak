package com.robin.magic_realm.RealmCharacterWeb;

import java.io.File;

import org.jdom.Element;

import com.robin.general.io.FileUtilities;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RscharLayout implements Comparable<RscharLayout> {
	
	private String webFolder;
	private File file;
	private RealmCharacterBuilderModel model;
	
	private String status = null;
	
	public RscharLayout(File file) {
		this.file = file;
		webFolder = "Unknown";
		model = RealmCharacterBuilderModel.createFromFile(file);
	}
	public RscharLayout(File folder,Element element) {
		String path = folder.getAbsolutePath()+File.separator+element.getAttributeValue("file");
		file = new File(path);
		model = RealmCharacterBuilderModel.createFromFile(file);
		webFolder = element.getAttributeValue("webfolder");
	}
	public void clearStatus() {
		status = null;
	}
	public void setStatus(String s) {
		status = s;
	}
	public String getStatus() {
		return status;
	}
	public String toString() {
		return "RSCHAR: "+getFileName()+", "+webFolder;
	}
	public RealmCharacterBuilderModel getModel() {
		return model;
	}
	public int compareTo(RscharLayout rl) {
		int ret = webFolder.compareToIgnoreCase(rl.webFolder);
		if (ret==0) {
			ret = file.compareTo(rl.file);
		}
		return ret;
	}
	public CharacterWrapper getCharacter() {
		return model.getCharacter();
	}
	public void setWebFolder(String text) {
		webFolder = text;
	}
	public String getWebFolder() {
		return webFolder;
	}
	public File getFile() {
		return file;
	}
	public String getFileName() {
		return FileUtilities.getFilename(file,true);
	}
	public Element getElement() {
		Element element = new Element("rschar");
		element.setAttribute("file",FileUtilities.getFilename(file,true));
		element.setAttribute("webfolder",webFolder);
		return element;
	}
}
/*

<webLayout>
   <rschar file="Bard.rschar" webfolder="Robin" />
</webLayout>

*/