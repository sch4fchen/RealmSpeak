package com.robin.magic_realm.components.utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.attribute.TileLocation;

public class RealmLogParser {
	private ArrayList<String> lines;
	public RealmLogParser(String detailLog) {
		lines = StringUtilities.stringToCollection(detailLog,"\n");
	}
	public String getLogFor(int month,int day) {
		return "";
	}
	public String getBattleLogFor(int month,int day) {
		return getBattleLogFor(month,day,null,0);
	}
	public String getBattleLogFor(int month,int day,TileLocation tl) {
		return getBattleLogFor(month,day,tl,0);
	}
	public String getBattleLogFor(int month,int day,TileLocation tl,int round) {
		String battleStartPattern = "<br>[\\w\\W]* - Evening of month "+month+", day "+day+", in clearing ";
		if (tl!=null) {
			battleStartPattern += tl.tile.getGameObject().getName()+" "+tl.clearing.getNum()+"$";
		}
		int logStart = getLineNumberFor(battleStartPattern)-1;
		if (round>0) {
			int roundStart = getLineNumberFor("<br>[\\w\\W]*Combat Round "+round+"$",logStart)-1;
			return StringUtilities.collectionToString(lines.subList(roundStart,lines.size()-1),"\n");
		}
		return StringUtilities.collectionToString(lines.subList(logStart,lines.size()-1),"\n");
	}
	private int getLineNumberFor(String pattern) {
		return getLineNumberFor(pattern,0);
	}
	private int getLineNumberFor(String pattern,int lineStart) {
		Pattern p = Pattern.compile(pattern);
		for (int i=lineStart;i<lines.size();i++) {
			String line = lines.get(i).trim();
			Matcher m = p.matcher(line);
			if (m.matches()) {
				return i;
			}
		}
		return -1;
	}
}