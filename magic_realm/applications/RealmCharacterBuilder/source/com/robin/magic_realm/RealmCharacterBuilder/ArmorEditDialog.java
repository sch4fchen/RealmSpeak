package com.robin.magic_realm.RealmCharacterBuilder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.general.io.FileManager;
import com.robin.general.swing.*;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.ArmorChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TemplateLibrary;

public class ArmorEditDialog extends AggressiveDialog {
	
	private RealmCharacterBuilderModel model;
	private GameObject armor = null;
	private ArmorChitComponent armorComponent = null;
	private boolean reservedArmor;
	
	private Box infoPanel;
	private JPanel sideEditPanels;
	
	private JLabel[] sideLabel;
	private JButton loadIconButton;
	private SideEditPanel[] sideEditPanel;
	private JButton clearButton;
	private JButton pickButton;
	private JButton doneButton;
	private JButton newButton;
	
	private JLabel nameField;
	private ButtonPanel weightChoice;
	private IntegerField priceField;
	private JRadioButton shieldButton;
	private JRadioButton helmetButton;
	private JRadioButton breastplateButton;
	private JRadioButton fullArmorButton;
	private JRadioButton choiceButton;
	private JRadioButton thrustButton;
	private JRadioButton swingButton;
	private JRadioButton smashButton;
	
	private ArrayList<JRadioButton> slButtons;
	
	private FileManager graphicsManager;
	
	public ArmorEditDialog(JFrame frame,RealmCharacterBuilderModel model,FileManager graphicsManager,String weaponName) {
		super(frame,"Edit Armor",true);
		this.model = model;
		this.graphicsManager = graphicsManager;
		initComponents();
		setLocationRelativeTo(frame);
		setArmor(weaponName);
	}
	private void initComponents() {
		setSize(640,480);
		setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(new GridLayout(2,1));
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel fullChitDisplay = new JPanel(new BorderLayout());
		JPanel chitDisplay = new JPanel(new GridLayout(2,1));
		sideLabel = new JLabel[2];
		for (int i=0;i<2;i++) {
			sideLabel[i] = new JLabel();
			ComponentTools.lockComponentSize(sideLabel[i],90,80);
			sideLabel[i].setBorder(BorderFactory.createEtchedBorder());
			chitDisplay.add(sideLabel[i]);
		}
		fullChitDisplay.add(chitDisplay,"Center");
		loadIconButton = new JButton("Load Icon");
		loadIconButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				loadIcon();
				updateImages();
			}
		});
		fullChitDisplay.add(loadIconButton,"South");
		topPanel.add(fullChitDisplay,"West");
		Box line;
		int w = 300;
		UniformLabelGroup group = new UniformLabelGroup();
		infoPanel = Box.createVerticalBox();
		infoPanel.add(Box.createVerticalGlue());
			line = group.createLabelLine("Weight and Vulnerability");
			weightChoice = new ButtonPanel(RealmCharacterConstants.STRENGTHS);
			weightChoice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					armor.setThisAttribute(Constants.VULNERABILITY,ev.getActionCommand());
					armor.setThisAttribute(Constants.WEIGHT,ev.getActionCommand());
					updateImages();
				}
			});
			ComponentTools.lockComponentSize(weightChoice,w,25);
			line.add(weightChoice);
			line.add(Box.createHorizontalGlue());
		infoPanel.add(line);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(Box.createVerticalGlue());
			line = group.createLabelLine("Base Price (destroyed)");
			line.add(Box.createHorizontalStrut(5));
			priceField = new IntegerField();
			priceField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					if (priceField.getText().matches("0")) {
						armor.removeAttribute("destroyed","base_price");
					}
					else {
						armor.setAttribute("destroyed","base_price",priceField.getText());
					}
				}
			});
			priceField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent ev) {
					priceField.selectAll();
				}
				public void focusLost(FocusEvent ev) {
					if (priceField.getText().matches("0")) {
						armor.removeAttribute("destroyed","base_price");
					}
					else {
						armor.setAttribute("destroyed","base_price",priceField.getText());
					}
				}
			});
			ComponentTools.lockComponentSize(priceField,40,25);
			line.add(priceField);
		infoPanel.add(line);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(Box.createVerticalGlue());
			line = group.createLabelLine("Armor Type");
			Box armorTypeBox = Box.createHorizontalBox();
			ButtonGroup armorTypeGroup = new ButtonGroup();
			shieldButton = new JRadioButton("Shield", false);
			helmetButton = new JRadioButton("Helmet", false);
			breastplateButton = new JRadioButton("Breastplate", false);
			fullArmorButton = new JRadioButton("Full Armor", false);
			
			Box shieldBox = initShieldButtons();		
			shieldButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					armor.setThisAttribute(Constants.SHIELD);
					armor.setThisAttribute("armor_row","1");
					armor.setThisAttribute("armor_choice");
					armor.removeThisAttribute("armor_smash");
					armor.removeThisAttribute("armor_thrust");
					armor.removeThisAttribute("armor_swing");
					choiceButton.setEnabled(true);
					thrustButton.setEnabled(true);
					swingButton.setEnabled(true);
					smashButton.setEnabled(true);
					updateShieldButtons();
				}
			});
			helmetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					armor.removeThisAttribute("shield");
					armor.setThisAttribute("armor_row","2");
					armor.removeThisAttribute("armor_choice");
					armor.setThisAttribute("armor_smash");
					armor.removeThisAttribute("armor_thrust");
					armor.removeThisAttribute("armor_swing");
					disableShieldButtons();
					updateShieldButtons();
				}
			});
			breastplateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					armor.removeThisAttribute("shield");
					armor.setThisAttribute("armor_row","2");
					armor.removeThisAttribute("armor_choice");
					armor.removeThisAttribute("armor_smash");
					armor.setThisAttribute("armor_thrust");
					armor.setThisAttribute("armor_swing");
					disableShieldButtons();
					updateShieldButtons();
				}
			});
			fullArmorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					armor.removeThisAttribute("shield");
					armor.setThisAttribute("armor_row","3");
					armor.removeThisAttribute("armor_choice");
					armor.removeThisAttribute("armor_smash");
					armor.removeThisAttribute("armor_thrust");
					armor.removeThisAttribute("armor_swing");
					disableShieldButtons();
					updateShieldButtons();
				}
			});
			
			armorTypeBox.add(shieldButton);
			armorTypeGroup.add(shieldButton);
			armorTypeBox.add(helmetButton);
			armorTypeGroup.add(helmetButton);
			armorTypeBox.add(breastplateButton);
			armorTypeGroup.add(breastplateButton);
			armorTypeBox.add(fullArmorButton);
			armorTypeGroup.add(fullArmorButton);
			line.add(armorTypeBox);
		infoPanel.add(line);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(Box.createVerticalGlue());
			line = group.createLabelLine("Shield Protection");
			line.add(shieldBox);
		infoPanel.add(line);
		infoPanel.add(Box.createVerticalGlue());	
			ButtonGroup slGroup = new ButtonGroup();
			JPanel startingLocationPanel = new JPanel(new GridLayout(5,3));
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					JRadioButton button = (JRadioButton)ev.getSource();
					armor.setThisAttribute(Constants.ARMOR_START_LOCATION,button.getText());
				}
			};
			slButtons = new ArrayList<>();
			for (int i=0;i<RealmCharacterConstants.STARTING_LOCATION_OPTION.length;i++) {
				JRadioButton button = new JRadioButton(RealmCharacterConstants.STARTING_LOCATION_OPTION[i],i==0);
				startingLocationPanel.add(button);
				slGroup.add(button);
				button.addActionListener(al);
				slButtons.add(button);
			}
			startingLocationPanel.setBorder(BorderFactory.createTitledBorder("Starting Location (when not in use)"));
		infoPanel.add(startingLocationPanel);

		infoPanel.add(Box.createVerticalGlue());
		topPanel.add(infoPanel,"Center");
		mainPanel.add(topPanel);
		sideEditPanels = new JPanel(new GridLayout(1,2));
		sideEditPanel = new SideEditPanel[2];
		sideEditPanel[0] = new SideEditPanel(sideLabel[0],"intact");
		sideEditPanels.add(sideEditPanel[0]);
		sideEditPanel[1] = new SideEditPanel(sideLabel[1],"damaged");
		sideEditPanels.add(sideEditPanel[1]);
		mainPanel.add(sideEditPanels);
		add(mainPanel,"Center");
		
		Box buttonsPanel = Box.createHorizontalBox();
		buttonsPanel.add(Box.createHorizontalGlue());
		newButton = new JButton("New");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				createNewArmor();
			}
		});
		buttonsPanel.add(newButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		pickButton = new JButton("Pick");
		pickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<String> list = TemplateLibrary.getSingleton().getAllArmorNames();
				for (String val:model.getAllArmorNames()) {
					if (!list.contains(val)) {
						list.add(val);
					}
				}
				Collections.sort(list);
				String armorName = (String)JOptionPane.showInputDialog(
						parent,"Select an armor:","Pick Armor",JOptionPane.PLAIN_MESSAGE,null,list.toArray(),list.get(0));
				if (armorName!=null) {
					setArmor(armorName);
				}
			}
		});
		buttonsPanel.add(pickButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				clearControls();
				armor = null;
				armorComponent = null;
				updateControls();
			}
		});
		buttonsPanel.add(clearButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// Save to weapon library
				// If same name is already there, then ask if overwrite is okay
				setVisible(false);
			}
		});
		buttonsPanel.add(doneButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		add(buttonsPanel,"South");
		
		nameField = new JLabel("",SwingConstants.CENTER);
		ComponentTools.lockComponentSize(nameField,450,35);
		nameField.setFont(new Font("Dialog",Font.BOLD,32));
		add(nameField,"North");
		
		updateControls();
	}
	private void loadIcon() {
		File file = graphicsManager.getLoadPath();
		if (file!=null) {
			ImageIcon icon = IconFactory.findIcon(file.getAbsolutePath());
			if (icon!=null) {
				RealmCharacterBuilderModel.updateArmorIcon(armor,icon);
				repaint();
			}
		}
	}
	public String getArmorName() {
		if (armor!=null) {
			return armor.getName();
		}
		return null;
	}
	private void createNewArmor() {
		String name = JOptionPane.showInputDialog("Armor Name?");
		if (name!=null && name.trim().length()>0) {
			StringTokenizer tokens = new StringTokenizer(name.trim()," ");
			StringBuffer sb = new StringBuffer();
			while(tokens.hasMoreTokens()) {
				if (sb.length()>0) {
					sb.append(" ");
				}
				sb.append(StringUtilities.capitalize(tokens.nextToken()));
			}
			setArmor(sb.toString());
		}
	}
	private void setArmor(String name) {
		if (name!=null) {
			reservedArmor = false;
			
			// Check model first
			if (model.hasArmor(name)) {
				armor  = model.getArmor(name);
				if (TemplateLibrary.getSingleton().hasArmorTemplate(name)) {
					reservedArmor = true;
				}
			}
			else if (TemplateLibrary.getSingleton().hasArmorTemplate(name)) {
				// See if the armor is already present in the model
				GameObject template = TemplateLibrary.getSingleton().getArmorTemplate(name);
				armor = model.getData().createNewObject();
				armor.copyAttributesFrom(template);
				reservedArmor = true;
			}
			else {
				// Must be a new armor
				armor = model.getData().createNewObject();
				armor.setName(name);
				armor.setThisAttribute("armor");
				armor.setThisAttribute("item");
				armor.setThisAttribute("icon_type","question");
				armor.setThisAttribute("icon_folder",RealmCharacterConstants.CUSTOM_ICON_BASE_PATH+"armor");
				armor.setThisAttribute("facing", "dark");
				armor.setThisAttribute(Constants.VULNERABILITY,"L");
				armor.setThisAttribute(Constants.WEIGHT,"L");
				armor.setAttribute("intact","chit_color","tan");
				armor.setAttribute("intact","base_price","1");
				armor.setAttribute("damaged","chit_color","white");
				armor.setAttribute("damaged","base_price","1");
				armor.setThisAttribute(Constants.ARMOR_START_LOCATION,"Guard");
				armor.setThisAttribute("armor_row","3"); // default
			}
			
			model.addArmor(name,armor);
			
			initArmorControls();
			updateControls();
		}
		else {
			armor = null;
			armorComponent = null;
		}
	}
	private void clearControls() {
		nameField.setText("");
		weightChoice.setSelectedItem("L");
		sideLabel[0].setIcon(null);
		sideLabel[1].setIcon(null);
	}
	private void updateImages() {
		for (int i=0;i<2;i++) {
			sideEditPanel[i].updateImage();
		}
	}
	private void initArmorControls() {
		armorComponent = (ArmorChitComponent)RealmComponent.getRealmComponent(armor);
		nameField.setText(armor.getName());
		weightChoice.setSelectedItem(armor.getThisAttribute(Constants.WEIGHT));
		if (armor.hasAttribute("destroyed","base_price")) {
			priceField.setText(armor.getAttribute("destroyed","base_price"));
		}
		else {
			priceField.setText("0");
		}
		String armorRow = armor.getThisAttribute("armor_row");
		shieldButton.setSelected(armor.hasThisAttribute(Constants.SHIELD));
		helmetButton.setSelected(armorRow != null && armorRow.matches("2") && armor.hasThisAttribute("armor_smash"));
		breastplateButton.setSelected(armorRow != null && armorRow.matches("2") && !armor.hasThisAttribute("armor_smash"));
		fullArmorButton.setSelected(armorRow != null && armorRow.matches("3"));
		updateShieldButtons();
		for (int i=0;i<2;i++) {
			sideEditPanel[i].updateImage();
			sideEditPanel[i].initSideControls();
		}
		String armorStart = armor.getThisAttribute(Constants.ARMOR_START_LOCATION);
		if (armorStart!=null) {
			for (JRadioButton button:slButtons) {
				if (armorStart.equals(button.getText())) {
					button.setSelected(true);
					break;
				}
			}
		}
	}
	public void updateControls() {
		infoPanel.setVisible(armor!=null && !reservedArmor);
		sideEditPanels.setVisible(armor!=null && !reservedArmor);
		loadIconButton.setEnabled(armor!=null && !reservedArmor);
		
		newButton.setEnabled(armor==null);
		pickButton.setEnabled(armor==null);
		clearButton.setEnabled(armor!=null);
	}
	private void updateShieldButtons() {
		choiceButton.setSelected(armor.hasThisAttribute("armor_choice") || (!armor.hasThisAttribute("armor_thrust") && !armor.hasThisAttribute("armor_swing") && !armor.hasThisAttribute("armor_smash")));
		thrustButton.setSelected(armor.hasThisAttribute("armor_thrust"));
		swingButton.setSelected(armor.hasThisAttribute("armor_swing"));
		smashButton.setSelected(armor.hasThisAttribute("armor_smash"));
	}
	private Box initShieldButtons() {
		Box shieldBox = Box.createHorizontalBox();
		ButtonGroup shieldGroup = new ButtonGroup();
		choiceButton = new JRadioButton("Choice", false);
		thrustButton = new JRadioButton("Thrust", false);
		swingButton = new JRadioButton("Swing", false);
		smashButton = new JRadioButton("Smash", false);
		choiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				armor.setThisAttribute("armor_choice");
				armor.removeThisAttribute("armor_smash");
				armor.removeThisAttribute("armor_thrust");
				armor.removeThisAttribute("armor_swing");
			}
		});
		thrustButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				armor.removeThisAttribute("armor_choice");
				armor.removeThisAttribute("armor_smash");
				armor.setThisAttribute("armor_thrust");
				armor.removeThisAttribute("armor_swing");
			}
		});
		swingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				armor.removeThisAttribute("armor_choice");
				armor.removeThisAttribute("armor_smash");
				armor.removeThisAttribute("armor_thrust");
				armor.setThisAttribute("armor_swing");
			}
		});
		smashButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				armor.removeThisAttribute("armor_choice");
				armor.setThisAttribute("armor_smash");
				armor.removeThisAttribute("armor_thrust");
				armor.removeThisAttribute("armor_swing");
			}
		});
		
		shieldGroup.add(choiceButton);
		shieldBox.add(choiceButton);
		shieldGroup.add(thrustButton);
		shieldBox.add(thrustButton);
		shieldGroup.add(swingButton);
		shieldBox.add(swingButton);
		shieldGroup.add(smashButton);
		shieldBox.add(smashButton);
		return shieldBox;
	}
	private void disableShieldButtons() {
		choiceButton.setEnabled(false);
		thrustButton.setEnabled(false);
		swingButton.setEnabled(false);
		smashButton.setEnabled(false);
	}
	
	private class SideEditPanel extends JPanel {
		
		private JLabel iconLabel;
		private String sideName;
		private IntegerField priceField;
		
		public SideEditPanel(JLabel iconLabel,String sideNameIn) {
			this.iconLabel = iconLabel;
			this.sideName = sideNameIn;
			
			setLayout(new BorderLayout());
			UniformLabelGroup group = new UniformLabelGroup();
			Box line;
			Box box = Box.createVerticalBox();
			box.add(Box.createVerticalGlue());
			
			infoPanel.add(Box.createVerticalStrut(5));
			infoPanel.add(Box.createVerticalGlue());
				line = group.createLabelLine("Base Price");
				line.add(Box.createHorizontalStrut(5));
				priceField = new IntegerField();
				priceField.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						armor.setAttribute(sideName,"base_price",priceField.getText());
					}
				});
				priceField.addFocusListener(new FocusAdapter() {
					public void focusGained(FocusEvent ev) {
						priceField.selectAll();
					}
					public void focusLost(FocusEvent ev) {
						armor.setAttribute(sideName,"base_price",priceField.getText());
					}
				});
				ComponentTools.lockComponentSize(priceField,40,25);
				line.add(priceField);
			box.add(line);
			box.add(Box.createVerticalGlue());			
			add(box,"Center");
			
			setBorder(BorderFactory.createTitledBorder(StringUtilities.capitalize(sideName)));
		}
		public void initSideControls() {
			String basePrice = armor.getAttribute(sideName,"base_price");
			priceField.setText(basePrice);
		}
		public void updateImage() {
			if ("intact".equals(sideName)) {
				iconLabel.setIcon(armorComponent.getIcon());
			}
			else {
				iconLabel.setIcon(armorComponent.getFlipSideIcon());
			}
		}
	}
}