package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.io.ArgumentParser;
import com.robin.general.io.PreferenceManager;
import com.robin.general.io.ResourceFinder;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.IconFactory;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.TileEditComponent;
import com.robin.magic_realm.components.utility.GameFileFilters;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.utility.RealmUtility;

public class TileEditFrame extends JFrame {
	private static final String MetalLookAndFeel = "MLAF";
	private static final String TilesDisplayStyle = "TS";	
	
	private PreferenceManager prefs;
	protected GameData data;
	public static String dataFilename = null;
	
	protected JButton saveButton;
		
	protected JPanel tileView;
	protected TileEditComponent activeTile;
	protected JButton flipButton;
	protected JButton applyButton;
	protected JButton toggleDetailButton;
	protected JList<GameObject> tileList;
	
	protected boolean selectionLock = false;
	protected boolean editOffroad = false;
	protected boolean editClearing = false;
	protected boolean editPath = false;
	
	protected JLabel nameLabel;
	protected JLabel changeWarningLabel;
	
	protected JPanel clearingView;
		protected JList<ClearingDetail> clearingList;
		protected Box clearingControls;
			protected ButtonGroup clearingTypeGroup;
				protected JRadioButton normalClearingType;
				protected JRadioButton woodsClearingType;
				protected JRadioButton mountainClearingType;
				protected JRadioButton caveClearingType;
				protected JRadioButton waterClearingType;
			
				protected JCheckBox whiteClearingMagic;
				protected JCheckBox grayClearingMagic;
				protected JCheckBox goldClearingMagic;
				protected JCheckBox purpleClearingMagic;
				protected JCheckBox blackClearingMagic;
				protected JCheckBox variedClearingMagic;

				protected JButton addClearingButton;
				protected JButton removeClearingButton;
				
	protected JPanel pathView;
		protected JList<PathDetail> pathList;
		protected JPanel pathControls;
			protected ButtonGroup pathTypeGroup;
				protected JRadioButton normalPathType;
				protected JRadioButton hiddenPathType;
				protected JRadioButton secretPathType;
				protected JRadioButton cavePathType;
				protected JRadioButton riverPathType;
			protected JButton addPathButton;
			protected JButton removePathButton;
			protected JButton moveUp;
			protected JButton moveDn;
			protected JButton clearArc;
	protected JToggleButton markOffroad;
	
	private boolean changed = false;

	public TileEditFrame(GameData data) {
		this.data = data;
		initComponents();
	}
	private void initComponents() {
		setTitle("Tile Editor");
		setIconImage(IconFactory.findIcon("images/interface/build.gif").getImage());
		setSize(900,600);
		getContentPane().setLayout(new BorderLayout());
		setLocation(50,50);
		
		prefs = new PreferenceManager("RealmSpeak","TileEditor");
		prefs.loadPreferences();
		setJMenuBar(buildMenuBar());
		
		Box box;
		JScrollPane sp;
		
		tileList = new JList<>(getTiles());
		tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tileList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				if (activeTile!=null && activeTile.isChanged()) {
					int ret = 
						JOptionPane.showConfirmDialog(TileEditFrame.this,"You haven't applied your changes.  Apply them now?","Warning!",JOptionPane.YES_NO_OPTION);
					if (ret==JOptionPane.YES_OPTION) {
						applyButton.doClick();
					}
				}
				updateTileView();
				editOffroad = false;
				editClearing = false;
				editPath = false;
			}
		});
		sp = new JScrollPane(tileList);
		ComponentTools.lockComponentSize(sp,200,100);
		getContentPane().add(sp,"West");
			JPanel editPanel = new JPanel(new GridLayout(2,1));
				JPanel clearingEditPanel = new JPanel(new BorderLayout());
				clearingEditPanel.add(new JLabel("CLEARINGS:"),"North");
					clearingList = new JList<>();
					clearingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					clearingList.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent ev) {
							if (!selectionLock) {
								selectionLock = true;
								updateClearingButtons();
								pathList.clearSelection();
								markOffroad.setSelected(false);
								editOffroad = false;
								editClearing = true;
								editPath = false;
								selectionLock = false;
							}
						}
					});
					sp = new JScrollPane(clearingList);
					ComponentTools.lockComponentSize(sp,220,100);
				clearingEditPanel.add(sp,"Center");
					clearingControls = Box.createVerticalBox();
						JPanel clearingTypeButtons = new JPanel(new GridLayout(2,2));
							clearingTypeGroup = new ButtonGroup();
								normalClearingType = new JRadioButton("Normal");
								normalClearingType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updateClearings();
									}
								});
							clearingTypeGroup.add(normalClearingType);
							clearingTypeButtons.add(normalClearingType);
								woodsClearingType = new JRadioButton("Woods");
								woodsClearingType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updateClearings();
									}
								});
							clearingTypeGroup.add(woodsClearingType);
							clearingTypeButtons.add(woodsClearingType);
								mountainClearingType = new JRadioButton("Mountain");
								mountainClearingType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updateClearings();
									}
								});
							clearingTypeGroup.add(mountainClearingType);
							clearingTypeButtons.add(mountainClearingType);
								caveClearingType = new JRadioButton("Caves");
								caveClearingType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updateClearings();
									}
								});
							clearingTypeGroup.add(caveClearingType);
							clearingTypeButtons.add(caveClearingType);
							waterClearingType = new JRadioButton("Water");
							waterClearingType.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updateClearings();
								}
							});
						clearingTypeGroup.add(waterClearingType);
						clearingTypeButtons.add(waterClearingType);
					clearingControls.add(clearingTypeButtons);
					clearingControls.add(new JSeparator());
						JPanel clearingColorButtons = new JPanel(new GridLayout(2,3));
							whiteClearingMagic = new JCheckBox("White");
							whiteClearingMagic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updateClearings();
								}
							});
						clearingColorButtons.add(whiteClearingMagic);
							grayClearingMagic = new JCheckBox("Gray");
							grayClearingMagic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updateClearings();
								}
							});
						clearingColorButtons.add(grayClearingMagic);
							goldClearingMagic = new JCheckBox("Gold");
							goldClearingMagic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updateClearings();
								}
							});
						clearingColorButtons.add(goldClearingMagic);
							purpleClearingMagic = new JCheckBox("Purple");
							purpleClearingMagic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updateClearings();
								}
							});
						clearingColorButtons.add(purpleClearingMagic);
							blackClearingMagic = new JCheckBox("Black");
							blackClearingMagic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updateClearings();
								}
							});
						clearingColorButtons.add(blackClearingMagic);
						variedClearingMagic = new JCheckBox("Varied");
						variedClearingMagic.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								updateClearings();
							}
						});
						clearingColorButtons.add(variedClearingMagic);
					clearingControls.add(clearingColorButtons);
						JPanel clearingEditButtons = new JPanel(new GridLayout(1,2));
							addClearingButton = new JButton("Add");
							addClearingButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									addClearing();
								}
							});
						clearingEditButtons.add(addClearingButton);
							removeClearingButton = new JButton("Remove");
							removeClearingButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									removeClearing();
								}
							});
							clearingEditButtons.add(removeClearingButton);
					clearingControls.add(clearingEditButtons);
					clearingControls.add(new JSeparator());
					clearingControls.add(new JSeparator());
					clearingControls.add(new JSeparator());
				clearingEditPanel.add(clearingControls,"South");
			editPanel.add(clearingEditPanel);
				JPanel pathEditPanel = new JPanel(new BorderLayout());
				pathEditPanel.add(new JLabel("PATHS:"),"North");
					pathList = new JList<>();
					pathList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					pathList.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent ev) {
							if (!selectionLock) {
								selectionLock = true;
								updatePathButtons();
								clearingList.clearSelection();
								markOffroad.setSelected(false);
								editOffroad = false;
								editClearing = false;
								editPath = true;
								selectionLock = false;
							}
						}
					});
					sp = new JScrollPane(pathList);
					ComponentTools.lockComponentSize(sp,220,100);
				pathEditPanel.add(sp,"Center");
					pathControls = new JPanel(new BorderLayout());
						JPanel pathTypeButtons = new JPanel(new GridLayout(2,3));
						pathTypeGroup = new ButtonGroup();
								normalPathType = new JRadioButton("Normal");
								normalPathType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updatePaths();
									}
								});
							pathTypeGroup.add(normalPathType);
							pathTypeButtons.add(normalPathType);
								hiddenPathType = new JRadioButton("Hidden");
								hiddenPathType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updatePaths();
									}
								});
							pathTypeGroup.add(hiddenPathType);
							pathTypeButtons.add(hiddenPathType);
								secretPathType = new JRadioButton("Secret");
								secretPathType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updatePaths();
									}
								});
							pathTypeGroup.add(secretPathType);
							pathTypeButtons.add(secretPathType);
								cavePathType = new JRadioButton("Caves");
								cavePathType.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ev) {
										updatePaths();
									}
								});
							pathTypeGroup.add(cavePathType);
							pathTypeButtons.add(cavePathType);
							riverPathType = new JRadioButton("River");
							riverPathType.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									updatePaths();
								}
							});
							pathTypeGroup.add(riverPathType);
							pathTypeButtons.add(riverPathType);
					pathControls.add(pathTypeButtons,"Center");
					JPanel pathEditButtons = new JPanel(new GridLayout(2,3));
							addPathButton = new JButton("Add");
							addPathButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									addPath();
								}
							});
							removePathButton = new JButton("Remove");
							removePathButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									removePath();
								}
							});
							clearArc = new JButton("ClearARC");
							clearArc.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									addPathArcPoint(null);
								}
							});
							moveUp = new JButton("Up");
							moveUp.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									movePathUp();
								}
							});
							moveDn = new JButton("Down");
							moveDn.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ev) {
									movePathDown();
								}
							});
						pathEditButtons.add(addPathButton);
						pathEditButtons.add(removePathButton);
						pathEditButtons.add(clearArc);
						pathEditButtons.add(moveUp);
						pathEditButtons.add(moveDn);
					pathControls.add(pathEditButtons,"South");
				pathEditPanel.add(pathControls,"South");
			editPanel.add(pathEditPanel);
		getContentPane().add(editPanel,"East");
		
			tileView = new JPanel(new BorderLayout());
				box = Box.createHorizontalBox();
					flipButton = new JButton("Flip Tile");
					flipButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							if (activeTile!=null) {
								activeTile.flip();
								updateClearingList();
								updatePathList();
								repaint();
							}
						}
					});
				box.add(flipButton);
				box.add(Box.createHorizontalGlue());
				nameLabel = new JLabel("",SwingConstants.CENTER);
				changeWarningLabel = new JLabel("Tile has changes!",SwingConstants.CENTER);
				changeWarningLabel.setFont(new Font("Dialog",Font.BOLD,18));
				changeWarningLabel.setForeground(Color.red);
				changeWarningLabel.setVisible(false);
				JPanel namePanel = new JPanel(new GridLayout(2,1));
				namePanel.add(nameLabel);
				namePanel.add(changeWarningLabel);
				box.add(namePanel);
				box.add(Box.createHorizontalGlue());
					toggleDetailButton = new JButton("Toggle Detail");
					toggleDetailButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							RealmComponent.fullDetail = !RealmComponent.fullDetail;
							repaint();
						}
					});
				box.add(toggleDetailButton);
			tileView.add(box,"North");
				box = Box.createHorizontalBox();
					saveButton = new JButton("Save File");
					saveButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							changed = false;
							saveFile();
							updateControls();
						}
					});
				box.add(saveButton);
				box.add(Box.createHorizontalGlue());
					markOffroad = new JToggleButton("Mark Offroad",false);
					markOffroad.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							if (activeTile!=null) {
								selectionLock = true;
								clearingList.clearSelection();
								pathList.clearSelection();
								editOffroad = true;
								editClearing = false;
								editPath = false;
								selectionLock = false;
								
								updateClearingList();
								updatePathList();
							}
						}
					});
				box.add(markOffroad);
				box.add(Box.createHorizontalGlue());
					applyButton = new JButton("Apply");
					applyButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							if (activeTile!=null) {
								activeTile.applyChanges();
								if (activeTile.getFacingIndex()==1) {
									activeTile.flip();
									activeTile.repaint();
								}
								changed = true;
								updateControls();
							}
						}
					});
				box.add(applyButton);
			tileView.add(box,"South");
			MouseInputAdapter mouse = new MouseInputAdapter() {
				public void mousePressed(MouseEvent ev) {
					Point origin = activeTile.getLocation();
					Point mp = ev.getPoint();
					mp.x -= origin.x;
					mp.y -= origin.y;
					if (editClearing) {
						changeClearingPos(mp);
					}
					else if (editPath) {
						addPathArcPoint(mp);
					}
					else if (editOffroad) {
						setOffroadPos(mp);
					}
				}
				public void mouseDragged(MouseEvent ev) {
					mousePressed(ev);
				}
			};
			tileView.addMouseListener(mouse);
			tileView.addMouseMotionListener(mouse);
		getContentPane().add(tileView,"Center");
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		
		updateControls();
	}
	public void updateControls() {
		updateClearingButtons();
		updatePathButtons();
		changeWarningLabel.setVisible(activeTile!=null && activeTile.isChanged());
		if (activeTile!=null) {
			nameLabel.setText(activeTile+" ("+activeTile.getGameObject().getThisAttribute("code")+")"+" - Type: "+activeTile.getGameObject().getThisAttribute("tile_type"));
		}
		saveButton.setEnabled(changed);
		applyButton.setEnabled(activeTile!=null && activeTile.isChanged());
	}
	public void updateTileView() {
		if (activeTile!=null) {
			tileView.remove(activeTile);
		}
		GameObject tile = tileList.getSelectedValue();
		if (tile!=null) {
			activeTile = new TileEditComponent(tile);
			activeTile.initSize();
			tileView.add(activeTile,"Center");
			tileView.revalidate();
		}
		else {
			activeTile=null;
		}
		updatePathList();
		updateClearingList();
		updateControls();
		repaint();
	}
	public void updateClearingList() {
		if (activeTile!=null) {
			clearingList.setListData(new Vector<>(activeTile.getClearingDetail()));
		}
		else {
			clearingList.setListData(new Vector<>());
		}
		clearingList.revalidate();
		clearingList.repaint();
	}
	public void updateClearingButtons() {
		ClearingDetail selected = null;
		if (activeTile!=null) {
			selected = clearingList.getSelectedValue();
			if (selected!=null) {
				String type = selected.getType();
				if (type.equals("normal")) {
					normalClearingType.setSelected(true);
				}
				else if (type.equals("woods")) {
					woodsClearingType.setSelected(true);
				}
				else if (type.equals("mountain")) {
					mountainClearingType.setSelected(true);
				}
				else if (type.equals("caves")) {
					caveClearingType.setSelected(true);
				}
				else if (type.equals("water")) {
					waterClearingType.setSelected(true);
				}
				
				whiteClearingMagic.setSelected(selected.getMagic(ClearingDetail.MAGIC_WHITE));
				grayClearingMagic.setSelected(selected.getMagic(ClearingDetail.MAGIC_GRAY));
				goldClearingMagic.setSelected(selected.getMagic(ClearingDetail.MAGIC_GOLD));
				purpleClearingMagic.setSelected(selected.getMagic(ClearingDetail.MAGIC_PURPLE));
				blackClearingMagic.setSelected(selected.getMagic(ClearingDetail.MAGIC_BLACK));
				variedClearingMagic.setSelected(selected.getMagic(ClearingDetail.MAGIC_VARIED));
			}
		}
		
		normalClearingType.setEnabled(activeTile!=null && selected!=null);
		woodsClearingType.setEnabled(activeTile!=null && selected!=null);
		mountainClearingType.setEnabled(activeTile!=null && selected!=null);
		caveClearingType.setEnabled(activeTile!=null && selected!=null);
		waterClearingType.setEnabled(activeTile!=null && selected!=null);
		
		whiteClearingMagic.setEnabled(activeTile!=null && selected!=null);
		grayClearingMagic.setEnabled(activeTile!=null && selected!=null);
		goldClearingMagic.setEnabled(activeTile!=null && selected!=null);
		purpleClearingMagic.setEnabled(activeTile!=null && selected!=null);
		blackClearingMagic.setEnabled(activeTile!=null && selected!=null);
		variedClearingMagic.setEnabled(activeTile!=null && selected!=null);
	}
	public void updateClearings() {
		ClearingDetail selected = null;
		if (activeTile!=null) {
			selected = clearingList.getSelectedValue();
			if (selected!=null) {
				if (normalClearingType.isSelected()) {
					selected.setType("normal");
				}
				else if (woodsClearingType.isSelected()) {
					selected.setType("woods");
				}
				else if (mountainClearingType.isSelected()) {
					selected.setType("mountain");
				}
				else if (caveClearingType.isSelected()) {
					selected.setType("caves");
				}
				else if (waterClearingType.isSelected()) {
					selected.setType("water");
				}
				
				selected.setMagic(ClearingDetail.MAGIC_WHITE,whiteClearingMagic.isSelected());
				selected.setMagic(ClearingDetail.MAGIC_GRAY,grayClearingMagic.isSelected());
				selected.setMagic(ClearingDetail.MAGIC_GOLD,goldClearingMagic.isSelected());
				selected.setMagic(ClearingDetail.MAGIC_PURPLE,purpleClearingMagic.isSelected());
				selected.setMagic(ClearingDetail.MAGIC_BLACK,blackClearingMagic.isSelected());
				selected.setMagic(ClearingDetail.MAGIC_VARIED,variedClearingMagic.isSelected());
				
				activeTile.repaint();
				
				activeTile.didChange();
				updateControls();
			}
		}
	}
	public void changeClearingPos(Point p) {
		if (activeTile!=null) {
			ClearingDetail selected = clearingList.getSelectedValue();
			if (selected!=null) {
				activeTile.didChange();
				updateControls();
				selected.setPosition(p);
				activeTile.repaint();
			}
		}
	}
	public void addClearing() {
		if (activeTile==null) return;
		
		ArrayList<ClearingDetail> allClearings = (ArrayList<ClearingDetail>) activeTile.getClearingDetail();
		if (allClearings.size()>=6) return;
		
		int side = 0;
		if (activeTile.isDarkSideUp()) {
			side = 1;
		}
		ArrayList<Integer> allClearingsNums = new ArrayList<>();
		int num = 1;
		for (ClearingDetail cl : allClearings) {
			allClearingsNums.add(cl.getNum());
		}
		while (num<=6) {
			if (!allClearingsNums.contains(num)) break;
			num++;
		}
		
		allClearings.add(new ClearingDetail(activeTile,num,"normal",new Point(50,50),side));
		activeTile.setClearingDetail(allClearings);
		
		clearingList.clearSelection();
		updateClearingList();
		activeTile.repaint();
		activeTile.didChange();
		updateControls();
	}
	public void removeClearing() {
		if (activeTile == null) return;
		ClearingDetail selected = clearingList.getSelectedValue();
		if (selected == null) return;
		
		ArrayList<ClearingDetail> allClearings = (ArrayList<ClearingDetail>) activeTile.getClearingDetail();
		for (ClearingDetail cl : allClearings) {
			if (cl.getNum()==selected.getNum()) {
				allClearings.remove(cl);
				break;
			}
		}
		ArrayList<PathDetail> paths = new ArrayList<>(activeTile.getPathDetail());
		ArrayList<PathDetail> validPaths = new ArrayList<>();
		for (PathDetail path : paths) {
			if (path.getTo().getNum()!=selected.getNum() && path.getFrom().getNum()!=selected.getNum()) {
				validPaths.add(path);
			}
		}
		
		activeTile.setClearingDetail(allClearings);
		activeTile.setPathDetail(validPaths);
		clearingList.clearSelection();
		updateClearingList();
		updatePathList();
		activeTile.repaint();
		activeTile.didChange();
		updateControls();
	}
	public void updatePathList() {
		updatePathList(-1);
	}
	public void updatePathList(int newIndex) {
		if (activeTile!=null) {
			pathList.setListData(new Vector<>(activeTile.getPathDetail()));
		}
		else {
			pathList.setListData(new Vector<>());
		}
		if (newIndex>=0) {
			pathList.setSelectedIndex(newIndex);
		}
		pathList.revalidate();
		pathList.repaint();
	}
	public void updatePathButtons() {
		PathDetail selected = null;
		if (activeTile!=null) {
			selected = pathList.getSelectedValue();
			if (selected!=null) {
				String type = selected.getType();
				if (type.equals("normal")) {
					normalPathType.setSelected(true);
				}
				else if (type.equals("hidden")) {
					hiddenPathType.setSelected(true);
				}
				else if (type.equals("secret")) {
					secretPathType.setSelected(true);
				}
				else if (type.equals("caves")) {
					cavePathType.setSelected(true);
				}
				else if (type.equals("river")) {
					riverPathType.setSelected(true);
				}
			}
		}
		
		normalPathType.setEnabled(activeTile!=null && selected!=null);
		hiddenPathType.setEnabled(activeTile!=null && selected!=null);
		secretPathType.setEnabled(activeTile!=null && selected!=null);
		cavePathType.setEnabled(activeTile!=null && selected!=null);
		riverPathType.setEnabled(activeTile!=null && selected!=null);
		
		addPathButton.setEnabled(activeTile!=null);
		removePathButton.setEnabled(activeTile!=null && selected!=null);
	}
	public void updatePaths() {
		PathDetail selected = null;
		if (activeTile!=null) {
			selected = pathList.getSelectedValue();
			if (selected!=null) {
				if (normalPathType.isSelected()) {
					selected.setType("normal");
				}
				else if (hiddenPathType.isSelected()) {
					selected.setType("hidden");
				}
				else if (secretPathType.isSelected()) {
					selected.setType("secret");
				}
				else if (cavePathType.isSelected()) {
					selected.setType("caves");
				}
				else if (riverPathType.isSelected()) {
					selected.setType("river");
				}
				activeTile.repaint();
				
				activeTile.didChange();
				updateControls();
			}
		}
	}
	public void addPath() {
		if (activeTile!=null) {
			ArrayList<Object> list = new ArrayList<>(activeTile.getClearingDetail());
			ButtonOptionDialog chooser = new ButtonOptionDialog(this,null,"From which clearing?","");
			chooser.addSelectionObjects(list);
			chooser.setVisible(true);
			if (chooser.getSelectedObject()!=null) {
				ClearingDetail c1 = (ClearingDetail)chooser.getSelectedObject();
				
				// FIXME Should eliminate paths that are already there!
				chooser = new ButtonOptionDialog(this,null,"To which clearing/edge?","");
				list.add("N");
				list.add("NE");
				list.add("SE");
				list.add("S");
				list.add("SW");
				list.add("NW");
				chooser.addSelectionObjects(list);
				chooser.setVisible(true);
				if (chooser.getSelectedObject()!=null) {
					String c2Name;
					ClearingDetail c2;
					Object o2 = chooser.getSelectedObject();
					if (o2 instanceof ClearingDetail) {
						c2 = (ClearingDetail)o2;
						c2Name = c2.getName();
					}
					else {
						String edge = (String)o2;
						c2Name = edge;
						Hashtable<String, Point> edgePositionHash = TileComponent.getEdgePositionHash();
						c2 = new ClearingDetail(activeTile,edge,edgePositionHash.get(edge),activeTile.getFacingIndex());
					}
					ArrayList<PathDetail> paths = new ArrayList<>(activeTile.getPathDetail());
					PathDetail path = new PathDetail(activeTile,paths.size()+1,c1.getName(),c2Name,c1,c2,null,"normal",activeTile.getFacingName());
					paths.add(path);
					activeTile.setPathDetail(paths);
					updatePathList(paths.size()-1);
				}
			}
		}
	}
	public void removePath() {
		if (activeTile!=null) {
			int index = pathList.getSelectedIndex();
			if (index>=0) {
				ArrayList<PathDetail> list = new ArrayList<>(activeTile.getPathDetail());
				list.remove(index);
				activeTile.setPathDetail(list);
				updatePathList(index);
			}
		}
	}
	public void movePathUp() {
		if (activeTile!=null) {
			int index = pathList.getSelectedIndex();
			
			if (index>0) {
				ArrayList<PathDetail> list = new ArrayList<>(activeTile.getPathDetail());
				
				PathDetail selected = list.get(index);
				PathDetail toSwap = list.get(index-1);
				
				list.set(index-1,selected);
				list.set(index,toSwap);
				
				activeTile.setPathDetail(list);
				
				updatePathList(index-1);
			}
		}
	}
	public void movePathDown() {
		if (activeTile!=null) {
			int index = pathList.getSelectedIndex();
			
			if ((index+1)<pathList.getModel().getSize()) {
				ArrayList<PathDetail> list = new ArrayList<>(activeTile.getPathDetail());
				
				PathDetail selected = list.get(index);
				PathDetail toSwap = list.get(index+1);
				
				list.set(index+1,selected);
				list.set(index,toSwap);
				
				activeTile.setPathDetail(list);
				
				updatePathList(index+1);
			}
		}
	}
	public void addPathArcPoint(Point p) {
		if (activeTile!=null) {
			PathDetail selected = pathList.getSelectedValue();
			if (selected!=null) {
				activeTile.didChange();
				updateControls();
				selected.setArcPoint(p);
				activeTile.repaint();
			}
		}
	}
	public Vector<GameObject> getTiles() {
		Vector<GameObject> tiles = new Vector<>();
		for (GameObject go : data.getGameObjects()) {
			if (go.hasKey("tile") || go.hasKey("a_tile")) {
				tiles.addElement(go);
			}
		}
		return tiles;
	}
	public void setOffroadPos(Point pos) {
		if (activeTile!=null) {
			activeTile.didChange();
			updateControls();
			activeTile.setOffroadPos(pos);
			activeTile.repaint();
		}
	}
	public void saveFile() {
		data.saveToFile(new File(dataFilename));
	}
	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openGameData = new JMenuItem("Open Game Data");
		openGameData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				boolean gameLoaded = setGameDataFile();
				if (!gameLoaded) return;
				GameData data = new GameData();
				dataFilename = RealmLoader.DATA_PATH;
				data.loadFromPath(dataFilename);
				updateGameData(data);
				tileList.setListData(getTiles());
			}
		});
		fileMenu.add(openGameData);
		menuBar.add(fileMenu);
		JMenu optionMenu = new JMenu("Options");
		final JCheckBoxMenuItem toggleLookAndFeel = new JCheckBoxMenuItem("Cross Platform Look and Feel",prefs.getBoolean(MetalLookAndFeel));
		toggleLookAndFeel.setSelected(true);
		toggleLookAndFeel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(MetalLookAndFeel,toggleLookAndFeel.isSelected());
				updateLookAndFeel();
			}
		});
		optionMenu.add(toggleLookAndFeel);		
		optionMenu.add(getTilesOptionsPanel());
		menuBar.add(optionMenu);
		return menuBar;
	}
	private void updateLookAndFeel() {
		if (prefs.getBoolean(MetalLookAndFeel)) {
			ComponentTools.setMetalLookAndFeel();
		}
		else {
			ComponentTools.setSystemLookAndFeel();
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	private JPanel getTilesOptionsPanel() {
		int selected = prefs.getInt(TilesDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Tiles Style"));
		ButtonGroup group = new ButtonGroup();
		JRadioButton classicTilesOption = new JRadioButton("Classic");
		classicTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_CLASSIC);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_CLASSIC) {
			classicTilesOption.setSelected(true);
		}
		group.add(classicTilesOption);
		panel.add(classicTilesOption);
		JRadioButton legendaryTilesOption = new JRadioButton("Legendary Realm");
		legendaryTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_LEGENDARY) {
			legendaryTilesOption.setSelected(true);
		}
		group.add(legendaryTilesOption);
		panel.add(legendaryTilesOption);
		JRadioButton legendaryWithIconsTilesOption = new JRadioButton("Legendary Realm (with Icons)");
		legendaryWithIconsTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS) {
			legendaryWithIconsTilesOption.setSelected(true);
		}
		group.add(legendaryWithIconsTilesOption);
		panel.add(legendaryWithIconsTilesOption);
		return panel;
	}
	private void setTilesStyle() {
		switch(prefs.getInt(TilesDisplayStyle)) {
		case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY;
			break;
		case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS;
			break;
		default:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_CLASSIC;
			break;
		}
	}
	private void updateTilesStyle() {
		setTilesStyle();
		updateTileView();
	}
	private boolean setGameDataFile() {
		JFileChooser chooser = new JFileChooser(new File("./"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(GameFileFilters.createGameDataFileFilter());
		if (chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
			RealmLoader.DATA_PATH = chooser.getSelectedFile().toPath().toString();
			return true;
		}
		return false;
	}
	private void updateGameData(GameData data) {
		data.ignoreRandomSeed = true;
		this.data = data;
	}
	
	public static void main(String[]args) {
		RealmUtility.setupTextType();
		ArgumentParser ap = new ArgumentParser(args);
		dataFilename = ap.getValueForKey("file");
		GameData data = new GameData();
		
		if (dataFilename==null) {
			dataFilename = "data/MagicRealmData.xml";
			data.loadFromStream(ResourceFinder.getInputStream(dataFilename));
		}
		else {
			data.loadFromPath(dataFilename);
		}
		data.ignoreRandomSeed = true;
	
		new TileEditFrame(data).setVisible(true);
	}
}