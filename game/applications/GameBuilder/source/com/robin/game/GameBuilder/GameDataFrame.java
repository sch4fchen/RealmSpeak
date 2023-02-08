package com.robin.game.GameBuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GameSetup;
import com.robin.general.io.Closeable;
import com.robin.general.io.Modifyable;
import com.robin.general.io.Saveable;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.ListManagerPane;

public class GameDataFrame extends JInternalFrame implements Modifyable,Saveable,Closeable {

	protected long cumulative_id = 0;
	private boolean zipfile;

	// Game
	protected JTextArea gameDescField;
	protected JLabel statusField;	
	
	protected File lastPath; // should this be static or not?
	protected File filePath;
	
	protected GameBuilderFrame parent;
	protected GameData data;
	protected Hashtable<String, GameObjectFrame> gameObjectFrames;
	protected Hashtable<String, GameSetupFrame> gameSetupFrames;
	
	public JTextField objectsFilterField;
	protected ListManagerPane objectsPane;
	
	protected ListManagerPane setupPane;
	
	public GameDataFrame(GameBuilderFrame parent,GameData data) {
		super("",true,true,true,true);
		this.parent = parent;
		this.data = data;
		lastPath = null;
		filePath = null;
		gameObjectFrames = new Hashtable<String, GameObjectFrame>();
		gameSetupFrames = new Hashtable<String, GameSetupFrame>();
		data.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				updateControls();
			}
		});
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent ev) {
				GameDataFrame.this.parent.updateMenu();
			}
			public void focusLost(FocusEvent ev) {
				// nothing
			}
		});
		initComponents();
		setModified(true);
	}
	public GameData getGameData() {
		return data;
	}
	public void setModified(boolean val) {
		data.setModified(true);
	}
	public boolean isModified() {
		return data.isModified();
	}
	public void appClosing(Component component) {
		if (isModified()) {
			int ret = JOptionPane.showConfirmDialog(
					component,
					"Do you want to save changes to "+data.getGameName()+" before closing?",
					data.getGameName()+" has been modified",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret==JOptionPane.YES_OPTION) {
				save(component);
			}
		}
	}
	public void close(Component component) {
		if (isModified()) {
			int ret = JOptionPane.showConfirmDialog(
					component,
					"Save before closing?",
					data.getGameName()+" has been modified.",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
					
			if (ret==JOptionPane.YES_OPTION) {
				save(component);
			}
		}
		for (GameObjectFrame frame : gameObjectFrames.values()) {
			frame.setVisible(false);
			parent.getDesktop().remove(frame);
		}
		setVisible(false);
		parent.removeDataFrame(this);
	}
	public void updateLastPath() {
		if (lastPath==null) {
			lastPath = parent.getLastPath();
		}
	}
	public boolean load(Component component) {
		updateLastPath();
		JFileChooser chooser = new JFileChooser(lastPath);
		if (chooser.showOpenDialog(component)==JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file!=null) {
				if (data.loadFromFile(file)) {
					zipfile = false;
					updateControls();
					setFile(file);
					return true;
				}
				else if (data.zipFromFile(file)) {
					zipfile = true;
					updateControls();
					setFile(file);
					return true;
				}
			}
		}
		return false;
	}
	public boolean save(Component component) {
		updateLastPath();
		if (filePath!=null) {
			if (!zipfile) {
				return data.saveToFile(filePath);
			}
			return data.zipToFile(filePath);
		}
		return saveAs(component);
	}
	public boolean saveAs(Component component) {
		updateLastPath();
		JFileChooser chooser = new JFileChooser(lastPath);
		if (chooser.showSaveDialog(component)==JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file!=null) {
				if (!zipfile) {
					if (data.saveToFile(file)) {
						setFile(file);
					}
				}
				else {
					if (data.zipToFile(file)) {
						setFile(file);
					}
				}
			}
		}
		return false;
	}
	public boolean revert() {
		updateLastPath();
		if (filePath!=null) {
			return data.loadFromFile(filePath);
		}
		return false;
	}
	private void setFile(File file) {
		filePath = file;
		String path = file.getPath();
		if (!path.endsWith(File.separator)) {
			int pathEnd = path.lastIndexOf(File.separator);
			if (pathEnd!=-1) {
				path = path.substring(0,pathEnd+1);
			}
		}
		lastPath = new File(path);
		parent.setLastPath(lastPath);
	}
	/**
	 * This is an ugly hack - how better to do it?  I'm getting an IllegalAccessException
	 * if I try to do a gameDescField.setText(...)  Maybe I need to disable the CaretListener...
	 */
	public void resetGameDescription() {
			Box top = Box.createVerticalBox();
				Box box = Box.createHorizontalBox();
				box.add(new JLabel("Game Description:"));
				box.add(Box.createHorizontalGlue());
			top.add(box);
				box = Box.createHorizontalBox();
					gameDescField = new JTextArea(data.getGameDescription());
					gameDescField.addCaretListener(new CaretListener() {
						public void caretUpdate(CaretEvent ev) {
							data.setGameDescription(gameDescField.getText());
						}
					});
					JScrollPane scroll = new JScrollPane(gameDescField);
					scroll.setMinimumSize(new Dimension(120,60));
					scroll.setPreferredSize(new Dimension(120,60));
				box.add(scroll);
			top.add(box);
		getContentPane().add(top,"North");
	}
	private void initComponents() {
		Box box;
		JPanel panel;
		JButton button;
		setSize(500,500);
		setContentPane(new JPanel());
		getContentPane().setLayout(new BorderLayout(5,5));
			Box top = Box.createVerticalBox();
				box = Box.createHorizontalBox();
				box.add(new JLabel("Game Description:"));
				box.add(Box.createHorizontalGlue());
			top.add(box);
				box = Box.createHorizontalBox();
					gameDescField = new JTextArea(data.getGameDescription());
					gameDescField.addCaretListener(new CaretListener() {
						public void caretUpdate(CaretEvent ev) {
							data.setGameDescription(gameDescField.getText());
						}
					});
					JScrollPane scroll = new JScrollPane(gameDescField);
					scroll.setMinimumSize(new Dimension(120,60));
					scroll.setPreferredSize(new Dimension(120,60));
				box.add(scroll);
			top.add(box);
		getContentPane().add(top,"North");
			JTabbedPane pane = new JTabbedPane();
			// Game Objects Pane
				panel = new JPanel(new BorderLayout());
					box = Box.createHorizontalBox();
						button = new JButton("Filter");
						ComponentTools.lockComponentSize(button,80,25);
						button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								String filter = objectsFilterField.getText();
								data.setFilterString(filter);
							}
						});
					box.add(button);
						objectsFilterField = new JTextField();
						objectsFilterField.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								String filter = objectsFilterField.getText();
								data.setFilterString(filter);
							}
						});
					box.add(objectsFilterField);
						button = new JButton("Clear");
						ComponentTools.lockComponentSize(button,80,25);
						button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								objectsFilterField.setText("");
								data.clearFilterAndExcludeList();
							}
						});
					box.add(button);
				panel.add(box,"North");
					objectsPane = new ListManagerPane(null,new GameObjectTableModel(data.getFilteredGameObjects())) {
						public void add() {
							GameObject obj = data.createNewObject();
							GameObjectFrame of = getObjectFrame(obj);
							popUpInternalFrame(of);
						}
						public void duplicate() {
							int[] row = getSelectedRows();
							
							ArrayList<GameObject> objectsToDuplicate = new ArrayList<>();
							for (int i=0;i<row.length;i++) {
								GameObject obj = data.getFilteredGameObjects().get(row[i]);
								objectsToDuplicate.add(obj);
							}
							
							for (GameObject selObj : objectsToDuplicate) {
								GameObject obj = data.createNewObject();
								obj.copyAttributesFrom(selObj);
							}
							data.rebuildFilteredGameObjects();
							// no need to pop up frames
						}
						public void delete() {
							int[] row = getSelectedRows();
							
							// First get all selected objects
							ArrayList<GameObject> delObjects = new ArrayList<>();
							for (int i=0;i<row.length;i++) {
								GameObject obj = data.getFilteredGameObjects().get(row[i]);
								delObjects.add(obj);
							}
							
							// Delete references
							for (GameObject del : delObjects) {
								GameObject parent = del.getHeldBy();
								if (parent!=null) {
									parent.remove(del);
								}
							}
							
							// Now delete them
							for (GameObject obj : delObjects) {
								data.removeObject(obj);
								GameObjectFrame of = getObjectFrame(obj);
								if (parent.getDesktop().getIndexOf(of)!=-1) {
									parent.getDesktop().remove(of);
								}
							}
							setModified(true);
						}
						public void edit() {
							int row = getSelectedRow();
							GameObject obj = data.getFilteredGameObjects().get(row);
							GameObjectFrame of = getObjectFrame(obj);
							popUpInternalFrame(of);
						}
						public void globalEdit(boolean removingChange) {
							String blockName = JOptionPane.showInputDialog(this,"BlockName");
							if (blockName!=null) {
								String key = JOptionPane.showInputDialog(this,"Key");
								if (key!=null) {
									String val;
									if (removingChange) {
										val = "";
									}
									else {
										val = JOptionPane.showInputDialog(this,"Value");
									}
									
									if (val!=null) {
										ArrayList<GameObject> editObjects = new ArrayList<>(data.getFilteredGameObjects());
										int[] row = getSelectedRows();
										if (removingChange) {
											// Remove attribute from all selected objects
											for (int i=0;i<row.length;i++) {
												GameObject obj = editObjects.get(row[i]);
												obj.removeAttribute(blockName,key);
											}
										}
										else {
											// Add attribute to all selected objects
											for (int i=0;i<row.length;i++) {
												GameObject obj = editObjects.get(row[i]);
												obj.setAttribute(blockName,key,val);
											}
										}
										setModified(true);
									}
								}
							}
						}
						public void shiftBlock(int direction) {
							int[] row = getSelectedRows();
							
							// First get all selected objects
							int min = Integer.MAX_VALUE;
							int max = Integer.MIN_VALUE;
							ArrayList<GameObject> shiftObjects = new ArrayList<>();
							for (int i=0;i<row.length;i++) {
								GameObject obj = data.getFilteredGameObjects().get(row[i]);
								shiftObjects.add(obj);
								min = Math.min(row[i],min);
								max = Math.max(row[i],max);
							}
							
							if (direction==1) {
								// Down
								if ((max+1)<data.getFilteredGameObjects().size()) {
									GameObject obj = data.getFilteredGameObjects().get(max+1);
									data.moveObjectsAfter(shiftObjects,obj);
									updateSelection(shiftObjects);
								}
							}
							else {
								// Up
								if ((min-1)>=0) {
									GameObject obj = data.getFilteredGameObjects().get(min-1);
									data.moveObjectsBefore(shiftObjects,obj);
									updateSelection(shiftObjects);
								}
							}
							
						}
						public void updateSelection(ArrayList<GameObject> objects) {
							int[] row = new int[objects.size()];
							int n=0;
							for (Iterator<GameObject> i=objects.iterator();i.hasNext();) {
								row[n++] = data.getFilteredGameObjects().indexOf(i.next());
							}
							setSelectedRows(row);
						}
					};
				panel.add(objectsPane,"Center");
			pane.addTab("Objects",panel);
			// Game Setup Pane
				setupPane = new ListManagerPane(null,new GameSetupTableModel(data.getGameSetups())) {
					public void add() {
						GameSetup setup = data.createNewSetup();
						GameSetupFrame sf = getSetupFrame(setup);
						popUpInternalFrame(sf);
					}
					public void duplicate() {
						int row = getSelectedRow();
						GameSetup selSetup = data.getGameSetups().get(row);
						GameSetup setup = data.createNewSetup();
						setup.copyCommandsFrom(selSetup);
						// no need to pop up a frame
					}
					public void delete() {
						int[] row = getSelectedRows();
						
						// First get all selected objects
						ArrayList<GameSetup> delSetups = new ArrayList<>();
						for (int i=0;i<row.length;i++) {
							GameSetup setup = data.getGameSetups().get(row[i]);
							delSetups.add(setup);
						}
						
						// Now delete them
						for (GameSetup setup : delSetups) {
							data.removeSetup(setup);
							GameSetupFrame sf = getSetupFrame(setup);
							if (parent.getDesktop().getIndexOf(sf)!=-1) {
								parent.getDesktop().remove(sf);
							}
						}
						setModified(true);
					}
					public void edit() {
						int row = getSelectedRow();
						GameSetup setup = data.getGameSetups().get(row);
						GameSetupFrame sf = getSetupFrame(setup);
						popUpInternalFrame(sf);
					}
				};
			pane.addTab("Setup",setupPane);
		getContentPane().add(pane,"Center");
			statusField = new JLabel(" ");
		getContentPane().add(statusField,"South");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent ev) {
				close(parent);
			}
		});
	}
	public void popUpInternalFrame(JInternalFrame frame) {
		frame.setVisible(true);
		if (parent.getDesktop().getIndexOf(frame)==-1) {
			parent.getDesktop().add(frame);
		}
		frame.moveToFront();
		try {
			frame.setSelected(true);
		}
		catch(PropertyVetoException ex) {
		}
	}
	public void updateControls() {
		objectsPane.fireChange();
		setupPane.fireChange();
		setTitle(data.getGameName()+(data.isModified()?"*":""));
		statusField.setText("   "+data.getFilteredGameObjects().size()+" out of "+data.getGameObjects().size());
		parent.updateMenu();
	}
	public GameObjectFrame getObjectFrame(GameObject obj) {
		GameObjectFrame frame = null;
		if (obj!=null) {
			frame = gameObjectFrames.get(obj.getBarcode());
		}
		if (frame==null) {
			frame = createNewObjectFrame(obj);
		}
		return frame;
	}
	
	public GameObjectFrame createNewObjectFrame(GameObject obj) {
		GameObjectFrame frame = new GameObjectFrame(this,obj);
		Point p = getLocation();
		p.x += 20;
		p.y += 20;
		frame.setLocation(p);
		gameObjectFrames.put(obj.getBarcode(),frame);
		return frame;
	}
	public GameSetupFrame getSetupFrame(GameSetup setup) {
		GameSetupFrame frame = null;
		if (setup!=null) {
			frame = gameSetupFrames.get(setup.getBarcode());
		}
		if (frame==null) {
			frame = createNewSetupFrame(setup);
		}
		return frame;
	}
	
	public GameSetupFrame createNewSetupFrame(GameSetup setup) {
		GameSetupFrame frame = new GameSetupFrame(this,setup);
		Point p = getLocation();
		p.x += 20;
		p.y += 20;
		frame.setLocation(p);
		gameSetupFrames.put(setup.getBarcode(),frame);
		return frame;
	}
	public String toString() {
		return "GameDataFrame for "+data;
	}
}