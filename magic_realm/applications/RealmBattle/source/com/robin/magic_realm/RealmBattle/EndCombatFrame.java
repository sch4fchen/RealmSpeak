package com.robin.magic_realm.RealmBattle;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;

import com.robin.game.server.GameClient;
import com.robin.magic_realm.components.utility.RealmDirectInfoHolder;

public class EndCombatFrame extends JFrame {
	
	public static long cum_id = 1L;
	
	private long id;
	
	private CombatFrame parent;
	private ArrayList<String> playersToRespond;
	
	private JTextArea responseArea;
	private Hashtable<String, String> responseHash;
	
	private JButton closeButton;
	private JButton cancelEndButton;
	
	public EndCombatFrame(CombatFrame parent,ArrayList<String> playersToRespond) {
		this.id = cum_id++;
		this.parent = parent;
		this.playersToRespond = playersToRespond;
		responseHash = new Hashtable<>();
		initComponents();
	}
	public long getId() {
		return id;
	}
	private void initComponents() {
		setSize(400,400);
		setTitle("End Combat");
		setLocationRelativeTo(parent);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JLabel("Waiting for responses..."),"North");
		
		responseArea = new JTextArea(10,40);
		responseArea.setEditable(false);
		responseArea.setLineWrap(false);
		getContentPane().add(new JScrollPane(responseArea),"Center");
		
		Box box = Box.createHorizontalBox();
		cancelEndButton = new JButton("Cancel");
		cancelEndButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				forceCancel();
				doClose();
			}
		});
		box.add(cancelEndButton);
		closeButton = new JButton("Done");
		closeButton.setEnabled(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doClose();
			}
		});
		box.add(Box.createHorizontalGlue());
		box.add(closeButton);
		getContentPane().add(box,"South");
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	private void forceCancel() {
		responseHash.clear();
	}
	private void doClose() {
		setVisible(false);
		dispose();
		String unanimous = getUnanimousResponse();
		if (unanimous!=null && RealmDirectInfoHolder.QUERY_RESPONSE_YES.equals(unanimous)) {
			parent.endCombatNow();
		}
		else {
			parent.cancelEndCombat();
		}
	}
	public void updateResponse(String respondingPlayer,String response) {
		int colon = response.indexOf(":");
		if (colon>=0) {
			long responseid = Long.valueOf(response.substring(0,colon)).longValue();
			if (responseid==id) { // This prevents earlier END requests from getting out of sync
				response = response.substring(colon+1);
				responseHash.put(respondingPlayer,response);
				
				boolean yes = response.equals(RealmDirectInfoHolder.QUERY_RESPONSE_YES);
				responseArea.append(respondingPlayer+" says "+(yes?"yes":"no")+".\n");
				GameClient.broadcastClient(respondingPlayer,yes?"Approves END Combat":"Cancels END Combat");
				
				if (responseHash.size()==playersToRespond.size()) {
					closeButton.setEnabled(true);
				}
			}
		}
	}
	private String getUnanimousResponse() {
		ArrayList<String> all = new ArrayList<>();
		for (String response : responseHash.values()) {
			if (!all.contains(response)) {
				all.add(response);
			}
		}
		if (all.size()==1) { // unanimous response
			return all.get(0);
		}
		return null;
	}
}