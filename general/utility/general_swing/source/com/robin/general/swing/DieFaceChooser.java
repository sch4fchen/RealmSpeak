package com.robin.general.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * A dialog to present the user with the 6-faces of a dice for choosing
 */
public class DieFaceChooser extends AggressiveDialog {
	private static final Font FONT = new Font("Dialog",Font.BOLD,16);
	private Die template;
	private int chosenFace = -1;
	private ImageIcon icon = null;
	public DieFaceChooser(JFrame parent,String title,String message,Die template,String table) {
		super(parent,title,true);
		this.template = template;
		if (table!=null) {
			icon = ImageCache.getIcon("tables/"+table);
		}
		initComponents(message);
		setLocationRelativeTo(parent);
	}
	private void initComponents(String title) {
		getContentPane().setLayout(new BorderLayout());
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		JLabel text = new JLabel(title,SwingConstants.CENTER);
		text.setFont(FONT);
		text.setOpaque(true);
		text.setBackground(Color.orange);
//		text.setFont(UIManager.getFont("Label.font"));
		mainPane.add(text,"North");
		
		JPanel panel = new JPanel(new GridLayout(1,6));
		for (int i=1;i<=6;i++) {
			Die die = template.getCopy();
			die.setFace(i);
			FaceButton button = new FaceButton(die,i);
			panel.add(button);
		}
		mainPane.add(panel,"South");
		
		if (icon!=null) {
			JLabel iconLabel = new JLabel(icon);
			iconLabel.setBorder(BorderFactory.createEtchedBorder());
			mainPane.add(iconLabel,"Center");
		}
		
		getContentPane().add(mainPane,"Center");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pack();
	}
	public int getChosenFace() {
		return chosenFace;
	}
	private void pressedFace(int face) {
		chosenFace = face;
		setVisible(false);
		dispose();
	}
	private class FaceButton extends JButton implements ActionListener {
		private int face;
		public FaceButton(Icon icon,int face) {
			super(icon);
			this.face = face;
			if (face<1 || face>6) {
				throw new IllegalArgumentException("face must be 1-6");
			}
			addActionListener(FaceButton.this);
		}
		public void actionPerformed(ActionEvent ev) {
			pressedFace(face);
		}
	}
	public static int getRedDieFace(JFrame parent,String title,String message,String table) {
		Die die = new Die(50,12,Color.red,Color.white);
		DieFaceChooser chooser = new DieFaceChooser(parent,title,message,die,table);
		chooser.setVisible(true);
		return chooser.getChosenFace();
	}
	public static void main(String[] args) {
		int face = DieFaceChooser.getRedDieFace(new JFrame(),"Boom!","You have the ability to control the RED die.  Choose a result:",null);
		System.out.println("Choose "+face);
		System.exit(0);
	}
}