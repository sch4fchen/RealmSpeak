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