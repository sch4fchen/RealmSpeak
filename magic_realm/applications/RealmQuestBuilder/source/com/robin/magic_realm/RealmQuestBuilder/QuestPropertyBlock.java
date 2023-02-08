package com.robin.magic_realm.RealmQuestBuilder;

import java.util.ArrayList;

import javax.swing.JComponent;

public class QuestPropertyBlock {

	public enum FieldType {
		Boolean, 
		ChitType, 
		GameObjectWrapperSelector, 
		NoSpacesTextLine,
		Number,
		NumberAll,
		Regex,
		RegexIgnoreChitTypes,
		SmartTextLine,
		SmartTextArea,
		StringSelector, 
		TextArea, 
		TextLine,
		CompanionSelector,
	}

	private String keyName;
	private String fieldName;
	private FieldType fieldType;
	private Object[] selections;
	private String[] keyVals;
	
	private JComponent component;
	
	public QuestPropertyBlock(String keyName, String fieldName, FieldType fieldType) {
		this(keyName, fieldName, fieldType, null);
	}

	public QuestPropertyBlock(String keyName, String fieldName, FieldType fieldType, Object[] selections) {
		this(keyName, fieldName, fieldType, selections, null);
	}

	public QuestPropertyBlock(String keyName, String fieldName, FieldType fieldType, Object[] selections, String[] keyVals) {
		this.keyName = keyName;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.selections = selections;
		this.keyVals = keyVals;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public Object[] getSelections() {
		return selections;
	}
	public ArrayList<String> getSelectionsAsStrings() {
		ArrayList<String> list = new ArrayList<>();
		if (selections!=null) {
			for (Object val:selections) {
				list.add(val.toString());
			}
		}
		return list;
	}
	
	public String[] getKeyVals() {
		return keyVals;
	}

	public JComponent getComponent() {
		return component;
	}

	public void setComponent(JComponent component) {
		this.component = component;
	}
}