package com.robin.game.GameBuilder;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import com.robin.game.objects.*;

public class GameObjectTreeView extends JFrame {
	protected JTree tree;
	
	public GameObjectTreeView(Collection<GameObject> gameObjects) {
		init(gameObjects);
	}
	private void init(Collection<GameObject> gameObjects) {
		setSize(400,500);
		getContentPane().setLayout(new BorderLayout());
			DefaultMutableTreeNode top = new DefaultMutableTreeNode("top");
			
			// Add all base objects (not held by anything)
			Hashtable<String, DefaultMutableTreeNode> hash = new Hashtable<>();
			for (GameObject object : gameObjects) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(object);
				if (object.getHeldBy()==null) {
					top.add(node);
				}
				hash.put(object.toString(),node);
			}
			
			// Now use the hash to add all the branches
			for (GameObject object : gameObjects) {
				DefaultMutableTreeNode node = hash.get(object.toString());
				for (GameObject heldObject : object.getHold()) {
					DefaultMutableTreeNode child = hash.get(heldObject.toString());
					node.add(child);
				}
			}
			
			tree = new JTree(top);
			tree.setRootVisible(false);
			tree.setShowsRootHandles(true);
		getContentPane().add(new JScrollPane(tree));
	}
}