package com.robin.magic_realm.components.wrapper;

import java.util.ArrayList;

import com.robin.game.objects.*;

public class SummaryEventWrapper extends GameObjectWrapper {

	public SummaryEventWrapper(GameObject obj) {
		super(obj);
	}
	public String getBlockName() {
		return "summev";
	}
	public void addSummaryEvent(String val) {
		addListItem("event",val);
	}
	public ArrayList<String> getSummaryEvents() {
		ArrayList<String> ret = new ArrayList<>();

		ArrayList<String> list = getList("event");
		if (list!=null) {
			for (String  val : list) {
				ret.add(val);
			}
		}
		
		return ret;
	}
	
	private static final String SUMMARY_EVENT_WRAPPER = "__SummEventWrapper_";
	public static Long SEW_ID = null;
	public static SummaryEventWrapper getSummaryEventWrapper(GameData data) {
		if (SEW_ID==null) {
			GamePool pool = new GamePool(data.getGameObjects());
			ArrayList<GameObject> list = pool.find(SUMMARY_EVENT_WRAPPER);
			GameObject gm = null;
			if (list!=null && list.size()==1) {
				gm = list.iterator().next();
			}
			if (gm==null) {
				gm = data.createNewObject();
				gm.setName(SUMMARY_EVENT_WRAPPER);
				gm.setThisAttribute(SUMMARY_EVENT_WRAPPER);
			}
			SEW_ID = Long.valueOf(gm.getId());
			return new SummaryEventWrapper(gm);
		}
		return new SummaryEventWrapper(data.getGameObject(SEW_ID));
	}
}