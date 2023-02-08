package com.robin.game.server;

import java.util.ArrayList;
import com.robin.game.objects.GameObjectChange;

public class RequestObject {
	private Object obj;
	private ArrayList<GameObjectChange> list;
	private int request;
	public RequestObject(int val) {
		request = val;
		obj = null;
		list = null;
	}
	public RequestObject(int val,String[] o) {
		request = val;
		obj = o;
		list = null;
	}
	public RequestObject(int val,InfoObject o) {
		request = val;
		obj = o;
		list = null;
	}
	public RequestObject(int val,ArrayList<GameObjectChange> l) {
		request = val;
		obj = null;
		list = new ArrayList<GameObjectChange>();
		if (l!=null) {
			list.addAll(l);
		}
	}
	public void finalize() throws Throwable {
		if (list!=null) {
			list.clear();
		}
		list = null;
		obj = null;
		super.finalize();
	}
	public String toString() {
		return "RequestObject: "+request+":"+obj;
	}
	public int getRequest() {
		return request;
	}
	public Object getObject() {
		return list==null?obj:list;
	}
	public boolean isIdle() {
		return request == GameClient.REQUEST_IDLE;
	}
}