package com.robin.game.objects;

import java.io.*;
import java.util.*;
import org.jdom.*;

import com.robin.general.io.*;

public abstract class GameCommand extends ModifyableObject implements Serializable {
	
	protected String newPool = "";
	protected String from=GameSetup.ALL;
	protected String to=GameSetup.ALL;
	protected GameObject targetObject;
	protected String attribute;
	protected String value;
	protected int count=0;
	protected int transferType=GamePool.RANDOM;
	protected ArrayList<String> keyVals = new ArrayList<String>();

	protected GameSetup parent;
	
	public abstract String getTypeName();
	protected abstract String process(ArrayList<GameObject> allGameObjects);
	
	public GameCommand(GameSetup setup) {
		parent = setup;
	}
	
	public GameSetup getGameSetup() {
		return parent;
	}
	public boolean usesNewPool() {
		return false;
	}
	public boolean usesFrom() {
		return false;
	}
	public boolean usesTo() {
		return false;
	}
	public boolean usesTargetObject() {
		return false;
	}
	public boolean usesAttribute() {
		return false;
	}
	public boolean usesValue() {
		return false;
	}
	public boolean usesCount() {
		return false;
	}
	public boolean usesTransferType() {
		return false;
	}
	public boolean usesKeyVals() {
		return false;
	}
	public boolean isCreate() {
		return false;
	}
	public boolean isExtract() {
		return false;
	}
	public static GameCommand getCommandForName(GameSetup gameSetup,String val) {
		GameCommand command = null;
		if (GameCommandAddTo.NAME.equals(val)) {
			command = new GameCommandAddTo(gameSetup);
		}
		else if (GameCommandCreate.NAME.equals(val)) {
			command = new GameCommandCreate(gameSetup);
		}
		else if (GameCommandDistribute.NAME.equals(val)) {
			command = new GameCommandDistribute(gameSetup);
		}
		else if (GameCommandExtract.NAME.equals(val)) {
			command = new GameCommandExtract(gameSetup);
		}
		else if (GameCommandMove.NAME.equals(val)) {
			command = new GameCommandMove(gameSetup);
		}
		else if (GameCommandAlter.NAME.equals(val)) {
			command = new GameCommandAlter(gameSetup);
		}
		else if (GameCommandOrderSetup.NAME.equals(val)) {
			command = new GameCommandOrderSetup(gameSetup);
		}
		else {
			throw new IllegalArgumentException("Invalid command type");
		}
		return command;
	}
	public void setNewPool(String pool) {
		newPool = pool.toUpperCase();
	}
	public String getNewPool() {
		return newPool;
	}
	public void setFrom(String pool) {
		from = pool;
	}
	public String getFrom() {
		return from;
	}
	public void setTo(String pool) {
		to = pool;
	}
	public String getTo() {
		return to;
	}
	public void setTargetObject(GameObject object) {
		targetObject = object;
	}
	public GameObject getTargetObject() {
		return targetObject;
	}
	public void setAttribute(String att) {
		attribute = att;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setValue(String val) {
		value = val;
	}
	public String getValue() {
		return value;
	}
	public void setCount(int val) {
		count = val;
	}
	public int getCount() {
		return count;
	}
	public void setTransferType(int val) {
		if (val==GamePool.RANDOM || val==GamePool.FROM_BEGINNING || val==GamePool.FROM_END) {
			transferType = val;
		}
		else {
			throw new IllegalArgumentException("Invalid command transfer type");
		}
	}
	public int getTransferType() {
		return transferType;
	}
	public void addKeyVal(String keyVal) {
		keyVals.add(keyVal);
	}
	public void addKeyVal(String key,String val) {
		keyVals.add(key+"="+val);
	}
	public void setKeyVals(ArrayList<String> val) {
		keyVals = val;
	}
	public ArrayList<String> getKeyVals() {
		return keyVals;
	}
	public String getKeyValString() {
		StringBuffer sb = new StringBuffer();
		for (String keyVal:keyVals) {
			if (sb.length()>0) {
				sb.append(",");
			}
			sb.append(keyVal);
		}
		return sb.toString();
	}
	public void setKeyValString(String string) {
		StringTokenizer tokens = new StringTokenizer(string,",");
		keyVals = new ArrayList<String>();
		while(tokens.hasMoreElements()) {
			keyVals.add(tokens.nextToken().trim());
		}
	}
	/**
	 * Processes the command, and returns a result string (for debugging setups)
	 */
	public String doCommand(ArrayList<GameObject> allGameObjects) {
		StringBuffer result = new StringBuffer();
		result.append("---> "+toString()+"\n");
		result.append(process(allGameObjects));
		return result.toString();
	}
	public void copyFrom(GameCommand command) {
		setNewPool(command.getNewPool());
		setFrom(command.getFrom());
		setTo(command.getTo());
		setTargetObject(command.getTargetObject());
		setAttribute(command.getAttribute());
		setValue(command.getValue());
		setCount(command.getCount());
		setTransferType(command.getTransferType());
		setKeyVals(new ArrayList<String>(command.getKeyVals()));
	}
	public Element getXML() {
		Element element = new Element(getTypeName());
		if (usesNewPool()) element.setAttribute(new Attribute("newPool",newPool));
		if (usesFrom()) element.setAttribute(new Attribute("from",from));
		if (usesTo()) element.setAttribute(new Attribute("to",to));
		if (usesTargetObject()) {
			if (targetObject!=null) {
				element.setAttribute(new Attribute("targetObjectID",""+targetObject.getId()));
			}
		}
		if (usesAttribute()) {
			element.setAttribute(new Attribute("attribute",""+attribute));
		}
		if (usesValue()) {
			element.setAttribute(new Attribute("value",""+value));
		}
		if (usesCount()) {
			element.setAttribute(new Attribute("count",""+count));
		}
		if (usesTransferType()) {
			element.setAttribute(new Attribute("transferType",GamePool.getTransferName(transferType)));
		}
		if (usesKeyVals()) {
			element.setAttribute(new Attribute("keyVals",getKeyValString()));
		}
		return element;
	}
	public static GameCommand createFromXML(GameSetup gameSetup,Element element) {
		GameCommand command = getCommandForName(gameSetup,element.getName());
		command.setXmlNow(element);
		return command;
	}
	public void setXmlNow(Element element) { // temporary stupid name
		if (usesNewPool()) newPool = element.getAttribute("newPool").getValue();
		if (usesFrom()) from = element.getAttribute("from").getValue();
		if (usesTo()) to = element.getAttribute("to").getValue();
		if (usesTargetObject()) {
			Attribute att = element.getAttribute("targetObjectID");
			if (att!=null) {
				try {
					String idString = att.getValue();
					Long id = Long.valueOf(idString);
					targetObject = parent.getGameData().getGameObject(id);
				}
				catch(NumberFormatException ex) {
				}
			}
		}
		if (usesAttribute()) {
			String string = element.getAttribute("attribute").getValue();
			attribute = string;
		}
		if (usesValue()) {
			String string = element.getAttribute("value").getValue();
			value = string;
		}
		if (usesKeyVals()) {
			String keyValsString = element.getAttribute("keyVals").getValue();
			StringTokenizer tokens = new StringTokenizer(keyValsString,",");
			keyVals.clear();
			while(tokens.hasMoreElements()) {
				keyVals.add(tokens.nextToken().trim());
			}
		}
		if (usesCount()) {
			Attribute countAtt = element.getAttribute("count");
			if (countAtt!=null) { // allows backward compatibility
				String countString = element.getAttribute("count").getValue();
				try {
					int n = Integer.parseInt(countString);
					count = n;
				}
				catch(NumberFormatException ex) {
				}
			}
			else {
				count = 0;
			}
		}
		if (usesTransferType()) {
			String transferTypeName = element.getAttribute("transferType").getValue();
			transferType = GamePool.getTransferType(transferTypeName);
		}
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getTypeName()+" ");
		if (usesNewPool()) sb.append(newPool);
		if (usesFrom()) sb.append("from "+from+" ");
		if (usesTo()) sb.append("to "+to+" ");
		if (usesAttribute()) sb.append(" sets "+(attribute==null?"NULL":attribute));
		if (usesTargetObject()) sb.append(" to "+(targetObject==null?"NULL":targetObject.toString()));
		if (usesValue()) sb.append(" with value "+(value==null?"NULL":value));
		if (usesCount()) sb.append(", "+count+" ");
		if (usesTransferType()) sb.append(GamePool.getTransferName(transferType));
		if (usesKeyVals()) sb.append("using ["+getKeyValString()+"]");
		return sb.toString();
	}
	// Serializable interface
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
}