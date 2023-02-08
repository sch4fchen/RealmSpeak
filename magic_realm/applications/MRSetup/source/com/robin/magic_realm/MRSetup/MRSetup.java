package com.robin.magic_realm.MRSetup;

import java.io.*;
import java.util.*;
import javax.swing.*;

import com.robin.game.objects.*;
import com.robin.game.GameSetupEncoder.*;
import com.robin.general.io.*;

public class MRSetup {
	public static final String LAST_DIR = "last_dir";
	
	public static void main(String[] args) {
		ArgumentParser ap = new ArgumentParser(args);
		String filename = ap.getValueForKey("file");
		Encoder encoder = new Encoder();
		if (filename==null) {
			encoder.loadFromStream(ResourceFinder.getInputStream("data/MagicRealmData.xml"));
		}
		else {
			encoder.loadFromPath(filename);
		}
			
			PrintGrouping tiles = new PrintGrouping("TILES");
			tiles.setProperty("print","");
			tiles.setProperty("tile","");
		encoder.addPrintGrouping(tiles);			
			PrintGrouping special = new PrintGrouping("CITY/CASTLE");
			special.setProperty("print","");
			special.setProperty("red_special","");
		encoder.addPrintGrouping(special);			
			PrintGrouping tls = new PrintGrouping("TREASURE LOCATIONS");
			tls.setProperty("print","");
			tls.setProperty("treasure_location","");
		encoder.addPrintGrouping(tls);			
			PrintGrouping dws = new PrintGrouping("DWELLINGS");
			dws.setProperty("print","");
			dws.setProperty("dwelling","");
		encoder.addPrintGrouping(dws);
			PrintGrouping trs = new PrintGrouping("TREASURES");
			trs.setProperty("print","");
			trs.setProperty("treasure","");
			trs.setProperty("!treasure_location","");
		encoder.addPrintGrouping(trs);
			Coding treasure = new Coding("TREASURE","TR_",3);
			treasure.setProperty("treasure","");
		encoder.addCoding(treasure);
			Coding spells = new Coding("SPELLS","SP_",3);
			spells.setProperty("spell","");
		encoder.addCoding(spells);
			Coding chits = new Coding("CHITS","CH_",3);
			chits.setProperty("chit","");
		encoder.addCoding(chits);
		
		encoder.setFilter(GamePool.makeKeyVals("original_game")); // shouldn't be hardcoded
		
		PreferenceManager prefs = new PreferenceManager("MRSetup","MRSetup.cfg") {
			protected void createDefaultPreferences(Properties props) {
				props.put(LAST_DIR,System.getProperty("user.home"));
			}
		};
		prefs.loadPreferences();
		
		// Query User
		String[] names = encoder.getGameData().getGameSetupNames();
		String setupName = (String)JOptionPane.showInputDialog(null,
									"Select",
									"Setup",
									JOptionPane.QUESTION_MESSAGE,
									null,
									names,
									null);
		if (setupName!=null) {
			File lastPath = new File(prefs.get(LAST_DIR));
			JFileChooser chooser = new JFileChooser(lastPath);
			if (chooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (encoder.writeFile(setupName,file.getPath())) {
					prefs.set(LAST_DIR,file.getPath());
					prefs.savePreferences();
				}
			}
		}
		System.exit(0);
	}
}