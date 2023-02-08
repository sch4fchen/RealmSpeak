package com.robin.game.objects;

import java.util.ArrayList;
import java.util.Iterator;

import com.robin.general.util.OrderedHashtable;

public class GameObjectBlockManager {
	private static final String PREFIX = "_GOBM_XX_";
	
	private String prefix;
	private GameObject gameObject;
	
	public GameObjectBlockManager(GameObject gameObject) {
		this(gameObject,PREFIX);
	}
	public GameObjectBlockManager(GameObject gameObject,String attributeObscuringPrefix) {
		this.gameObject = gameObject;
		this.prefix = attributeObscuringPrefix;
	}
	public void storeGameObjectInBlocks(GameObject go,String baseBlockKey) {
		gameObject.setAttribute(baseBlockKey,prefix+"Name",go.getName());
		for (String blockName : go.getAttributeBlockNames()) {
			String newBlockName = baseBlockKey+blockName;
			OrderedHashtable block = go.getAttributeBlock(blockName);
			for (Iterator n=block.keySet().iterator();n.hasNext();) {
				String key = (String)n.next();
				Object val = block.get(key);
				if (val instanceof ArrayList) {
					ArrayList copy = new ArrayList((ArrayList)val);
					gameObject.setAttributeList(newBlockName,prefix+key,copy);
				}
				else {
					gameObject.setAttribute(newBlockName,prefix+key,(String)val);
				}
			}
		}
	}
	public GameObject extractGameObjectFromBlocks(String baseBlockKey,boolean freeStandingGameObject) {
		return extractGameObjectFromBlocks(baseBlockKey,freeStandingGameObject,true);
	}
	public GameObject extractGameObjectFromBlocks(String baseBlockKey,boolean freeStandingGameObject,boolean requireAttributes) {
		GameObject go = freeStandingGameObject?GameObject.createEmptyGameObject():gameObject.getGameData().createNewObject();
		go.setName(gameObject.getAttribute(baseBlockKey,prefix+"Name"));
		boolean foundAttributes = false;
		for (String blockName : gameObject.getAttributeBlockNames()) {
			if (!blockName.equals(baseBlockKey) && blockName.startsWith(baseBlockKey)) {
				foundAttributes = true;
				String originalBlockName = blockName.substring(baseBlockKey.length());
				OrderedHashtable block = gameObject.getAttributeBlock(blockName);
				for (Iterator n=block.keySet().iterator();n.hasNext();) {
					String key = (String)n.next();
					Object val = block.get(key);
					String unobscuredKey = key.substring(prefix.length());
					if (val instanceof ArrayList) {
						ArrayList copy = new ArrayList((ArrayList)val);
						go.setAttributeList(originalBlockName,unobscuredKey,copy);
					}
					else {
						go.setAttribute(originalBlockName,unobscuredKey,(String)val);
					}
				}
			}
		}
		return foundAttributes||!requireAttributes ? go : null;
	}
	public void clearBlocks(String baseBlockKey) {
		ArrayList<String> remove = new ArrayList<String>();
		for (String blockName : gameObject.getAttributeBlockNames()) {
			if (blockName.startsWith(baseBlockKey)) {
				remove.add(blockName);
			}
		}
		for (String blockName:remove) {
			gameObject.removeAttributeBlock(blockName);
		}
	}
}