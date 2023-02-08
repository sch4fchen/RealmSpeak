package com.robin.game.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class will encapsulate a single change that needs to happen to the GameData
 */
public class GameAttributeListChange extends GameObjectChange {
	
	private static Logger logger = Logger.getLogger(GameAttributeListChange.class.getName());
	
	private String blockName;
	private String attributeName;
	private boolean clearList = false;
	private Collection<String> addList;
	private Collection<String> removeList;
	
	public GameAttributeListChange(GameObject go) {
		super(go);
		blockName = null;
		attributeName = null;
		addList = null;
		removeList = null;
	}
	/**
	 * Returns true if there are actual changes here (might not be if oldList and newList below are identical)
	 */
	public boolean hasChange() {
		return (clearList || (addList!=null && !addList.isEmpty()));
	}
	public void addAttributeListItem(String inBlockName,String inAttributeName,String val) {
		this.blockName = inBlockName;
		this.attributeName = inAttributeName;
		addList = new ArrayList<>();
		addList.add(val);
	}
	public void removeAttributeListItem(String inBlockName,String inAttributeName,String val) {
		this.blockName = inBlockName;
		this.attributeName = inAttributeName;
		removeList = new ArrayList<>();
		removeList.add(val);
	}
	public void setAttributeList(String blockName,String attributeName,ArrayList<String> oldList,ArrayList<String> newList) {
		logger.finer("setAttributeList Change from "+oldList+" to "+newList);
		this.blockName = blockName;
		this.attributeName = attributeName;
		
		// First, test to see if oldList is a subset of newList.  If so, then we can simply do additions.
		if (oldList!=null && newList!=null && oldList.size()<newList.size()) {
			boolean subset = true;
			for (int i=0;i<oldList.size();i++) {
				String oldItem = oldList.get(i);
				String newItem = newList.get(i);
				if (!oldItem.equals(newItem)) {
					subset = false;
					break;
				}
			}
			addList = new ArrayList<>();
			if (subset) {
				for (int i=oldList.size();i<newList.size();i++) {
					addList.add(newList.get(i));
				}
			}
			else {
				// not a subset, so clear the old list, and set all new
				clearList = true;
				addList.addAll(newList);
			}
		}
		else if (oldList!=null && newList!=null && oldList.size()>newList.size()) {
			// items were likely deleted - to keep it simple, set all new (someday may want to make this smarter)
			clearList = true;
			addList = new ArrayList<>();
			addList.addAll(newList);
		}
		else if (newList!=null && !newList.isEmpty() && (oldList==null || oldList.isEmpty())) {
			// Nothing in old list, so set all new
			clearList = true;
			addList = new ArrayList<>();
			addList.addAll(newList);
		}
		else if (newList==null || newList.isEmpty()) {
			// Nothing in new list, so clearList
			clearList = true;
			addList = null;
		}
		logger.finer("setAttributeList Change result = "+toString());
	}
	protected void applyChange(GameData data,GameObject go) {
		if (blockName!=null && attributeName!=null) {
			if (clearList) {
				go._removeAttribute(blockName,attributeName);
			}
			if (addList!=null) {
				ArrayList<String> list = go.getAttributeList(blockName,attributeName);
				if (list==null) {
					list = new ArrayList<>();
				}
				list = new ArrayList<>(list);
				logger.finer("applyChange to data "+data.getDataId()+":  adding "+addList+" to list "+list);
				list.addAll(addList);
				logger.finer("applyChange to data "+data.getDataId()+":  resulting list is "+list);
				go._setAttributeList(blockName,attributeName,list);
			}
			if (removeList!=null) {
				ArrayList<String> list = go.getAttributeList(blockName,attributeName);
				if (list!=null) {
					list = new ArrayList<>(list);
					logger.finer("applyChange to data "+data.getDataId()+":  removing "+removeList+" from list "+list);
					list.removeAll(removeList);
					logger.finer("applyChange to data "+data.getDataId()+":  resulting list is "+list);
					go._setAttributeList(blockName,attributeName,list);
				}
			}
		}
	}
	protected void rebuildChange(GameData data,GameObject go) {
		if (blockName!=null && attributeName!=null) {
			if (clearList) {
				go.removeAttribute(blockName,attributeName);
			}
			if (addList!=null) {
				ArrayList<String> list = go.getAttributeList(blockName,attributeName);
				if (list==null) {
					list = new ArrayList<>();
				}
				list = new ArrayList<>(list);
				logger.finer("applyChange to data "+data.getDataId()+":  adding "+addList+" to list "+list);
				list.addAll(addList);
				logger.finer("applyChange to data "+data.getDataId()+":  resulting list is "+list);
				go.setAttributeList(blockName,attributeName,list);
			}
			if (removeList!=null) {
				ArrayList<String> list = go.getAttributeList(blockName,attributeName);
				if (list!=null) {
					list = new ArrayList<>(list);
					logger.finer("applyChange to data "+data.getDataId()+":  removing "+removeList+" from list "+list);
					list.removeAll(removeList);
					logger.finer("applyChange to data "+data.getDataId()+":  resulting list is "+list);
					go.setAttributeList(blockName,attributeName,list);
				}
			}
		}
	}
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (blockName!=null && attributeName!=null && (addList!=null || clearList)) {
			sb.append(":  "+(clearList?"Sets":"Adds to")+" list ["+blockName+":"+attributeName+"] --> "+addList);
		}
		else {
			sb.append(":  Empty AttributeListChange action");
		}
		if (removeList!=null) {
			sb.append(":  Removes from list ["+blockName+":"+attributeName+"] --> "+removeList);
		}
		return sb.toString();
	}
}