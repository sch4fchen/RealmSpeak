package com.robin.general.swing;

import javax.swing.*;
import javax.swing.text.*;

public class IntegerField extends JTextField {
	public IntegerField() {
		this("");
	}
	public IntegerField(int num) {
		this(""+num);
	}
	public IntegerField(Integer num) {
		this(num.toString());
	}
	public IntegerField(String text) {
		setDocument(new PlainDocument() {
			public void insertString (int offset, String  str, AttributeSet attr) throws BadLocationException {
				if (str == null) return;
				StringBuffer temp=new StringBuffer();
				for (int i=0;i<str.length();i++) {
					if (str.charAt(i)>='0'&&str.charAt(i)<='9')
						temp.append(str.charAt(i));
				}
				if (temp.length()>0)
					super.insertString(offset,temp.toString(),attr);
			}
		});
		setText(text);
	}
	public int getInt() {
		String text = getText();
		if (text.length()>0) {
			return Integer.valueOf(getText()).intValue();
		}
		return 0;
	}
	public Integer getInteger() {
		String text = getText().trim();
		if (text.length()>0) {
			return Integer.valueOf(text);
		}
		return Integer.valueOf(0);
	}
}