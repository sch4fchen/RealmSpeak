/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.general.io;

import java.util.*;
import javax.swing.event.*;

public class ModifyableObject {

	protected static long cum_barcode = 0;
	protected long barcode = cum_barcode++;
	protected boolean modified = false;
	protected ArrayList<ChangeListener> changeListeners;		// fired when modified status changes
	
	public void setModified(boolean val) {
		modified = val;
		fireChange();
	}
	public boolean isModified() {
		return modified;
	}
	protected void copyChangeListeners(ModifyableObject mo) {
		if (mo.changeListeners!=null) {
			for (ChangeListener i : mo.changeListeners) {
				addChangeListener(i);
			}
		}
	}
	public void addChangeListener(ChangeListener listener) {
		if (changeListeners==null) {
			changeListeners = new ArrayList<>();
		}
		changeListeners.add(listener);
	}
	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners!=null) {
			changeListeners.remove(listener);
			if (changeListeners.size()==0) {
				changeListeners = null;
			}
		}
	}
	protected void fireChange() {
		if (changeListeners!=null) {
			ChangeEvent event = new ChangeEvent(this);
			for (ChangeListener listener : changeListeners) {
				listener.stateChanged(event);
			}
		}
	}
	/**
	 * To get a unique key
	 */
	public String getBarcode() {
		return "BARCODE"+barcode;
	}
}