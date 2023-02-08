package com.robin.game.GameSetupEncoder;

import java.util.*;

import com.robin.general.util.*;
import com.robin.game.objects.*;

public class Coding extends Properties {

	private static final String BLOCK_NAME = "com.robin.game.GameSetupEncoder.Coding";
	private static final String CODE_VALUE = "com.robin.game.GameSetupEncoder.Value";

	protected String codingName;
	protected String codePrefix;
	protected int numLength;
	
	public Coding(String codingName,String codePrefix,int numLength) {
		this.codingName = codingName;
		this.codePrefix = codePrefix;
		this.numLength = numLength;
	}
	public Collection<String> getKeyVals() {
		ArrayList<String> keyVals = new ArrayList<>();
		for (Enumeration e=keys();e.hasMoreElements();) {
			String key = (String)e.nextElement();
			String val = getProperty(key);
			if (val.trim().length()==0) {
				keyVals.add(key);
			}
			else {
				keyVals.add(key+"="+val);
			}
		}
		return keyVals;
	}
	public String encode(GamePool pool) {
		StringBuffer sb = new StringBuffer(codingName);
		sb.append(Encoder.LINE_END);
		sb.append(StringUtilities.getRepeatString("-",79));
		sb.append(Encoder.LINE_END);
		Collection<GameObject> c = pool.find(getKeyVals());
		pool = new GamePool(c);
		pool.shuffle();
		int n=1;
		for (GameObject object : pool) {
			String paddedNum = ""+n;
			while(paddedNum.length()<numLength) {
				paddedNum = "0"+paddedNum;
			}
			
			object.setAttribute(BLOCK_NAME,CODE_VALUE,codePrefix+paddedNum);
			sb.append(codePrefix+paddedNum+" = "+object+Encoder.LINE_END);
			
			n++;
		}
		sb.append(Encoder.LINE_END);
		return sb.toString();
	}
	public static String getCode(GameObject object) {
		String val = object.getAttribute(BLOCK_NAME,CODE_VALUE);
		if (val==null) {
			val="";
		}
		else {
			val=val+" ";
		}
		return val;
	}
}