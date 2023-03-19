package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.swing.*;
import com.robin.magic_realm.components.MagicRealmColor;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class RealmCalendarViewer extends JFrame implements ManagedFrame {
	
	private RealmCalendar realmCalendar;
	private GameWrapper game;
	private HostPrefWrapper hostPrefs;
	private JPanel calendar;
	private JPanel[] dayPanel;
	private JLabel seasonIconLabel;
	private JLabel seasonNameLabel;
	private JLabel weatherNameLabel;
	
	private JButton prevMonthButton;
	private JButton nowMonthButton;
	private JButton nextMonthButton;
	private JButton seasonDetailButton;
	
	private JButton closeButton;
	
	private int monthOffset = 0;
	
	public RealmCalendarViewer(GameData gameData) {
		realmCalendar = RealmCalendar.getCalendar(gameData);
		game = GameWrapper.findGame(gameData);
		hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		initComponents();
	}
	public void showSeasonDetail() {
		showSeasonInfo();
	}
	private void initComponents() {
		setSize(800,600);
		setIconImage(IconFactory.findIcon("images/interface/calendar.gif").getImage());
		calendar = new JPanel(new GridLayout(4,7));
		dayPanel = new JPanel[4*7];
		int dn = 0;
		for (int w=0;w<4;w++) {
			for (int d=0;d<7;d++) {
				dayPanel[dn] = new JPanel(new FlowLayout());
				dayPanel[dn].setBorder(BorderFactory.createTitledBorder(String.valueOf(dn+1)));
				dayPanel[dn].setBackground(MagicRealmColor.LIGHTBLUE);
				calendar.add(dayPanel[dn]);
				dn++;
			}
		}
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(calendar,"Center");
		
		// Header
		JPanel header = new JPanel(new BorderLayout());
		seasonIconLabel = new JLabel();
		ComponentTools.lockComponentSize(seasonIconLabel,1,100);
		header.add(seasonIconLabel,"West");
		
		Box midHeader = Box.createVerticalBox();
		midHeader.add(Box.createVerticalGlue());
		seasonNameLabel = new JLabel("",SwingConstants.CENTER);
		seasonNameLabel.setFont(new Font("Dialog",Font.BOLD,36));
		JPanel p1 = new JPanel();
		p1.add(seasonNameLabel);
		midHeader.add(p1);
		midHeader.add(Box.createVerticalGlue());
		JPanel p2 = new JPanel();
		weatherNameLabel = new JLabel("",SwingConstants.CENTER);
		weatherNameLabel.setFont(new Font("Dialog",Font.BOLD,16));
		p2.add(weatherNameLabel);
		midHeader.add(p2);
		midHeader.add(Box.createVerticalGlue());
		header.add(midHeader,"Center");
		
		if (hostPrefs.isUsingSeasons()) {
			ComponentTools.lockComponentSize(seasonIconLabel,100,100);
			
			seasonDetailButton = new JButton("<html>Show<br>Detail</html>");
			seasonDetailButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					showSeasonInfo();
				}
			});
			header.add(seasonDetailButton,"East");
		}
		getContentPane().add(header,"North");
		
		// Footer
		Box footer = Box.createHorizontalBox();
		footer.add(Box.createHorizontalGlue());
		prevMonthButton = new JButton("<< Previous");
		prevMonthButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				monthOffset--;
				updateCalendar();
			}
		});
		ComponentTools.lockComponentSize(prevMonthButton,120,25);
		footer.add(prevMonthButton);
		footer.add(Box.createHorizontalGlue());
		
		nowMonthButton = new JButton("Today");
		nowMonthButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				monthOffset = 0;
				updateCalendar();
			}
		});
		ComponentTools.lockComponentSize(nowMonthButton,120,25);
		footer.add(nowMonthButton);
		footer.add(Box.createHorizontalGlue());
		
		nextMonthButton = new JButton("Next >>");
		nextMonthButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				monthOffset++;
				updateCalendar();
			}
		});
		ComponentTools.lockComponentSize(nextMonthButton,120,25);
		footer.add(nextMonthButton);
		footer.add(Box.createHorizontalGlue());
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				FrameManager.getFrameManager().disposeFrame(getKey());
			}
		});
		ComponentTools.lockComponentSize(closeButton,80,25);
		footer.add(closeButton);
		
		getContentPane().add(footer,"South");
		
		getRootPane().setDefaultButton(closeButton);
		
		updateCalendar();
	}
	private void updateCalendar() {
		// Clear all days
		boolean usingSeasons = hostPrefs.isUsingSeasons();
		boolean usingWeather = usingSeasons && hostPrefs.hasPref(Constants.OPT_WEATHER);
		int day = game.getDay();
		int month = game.getMonth()+monthOffset;
		for (int i=0;i<28;i++) {
			dayPanel[i].removeAll();
			if (monthOffset==0 && (i+1)==day) {
				dayPanel[i].setOpaque(true);
			}
			else {
				dayPanel[i].setOpaque(false);
			}
			if (usingWeather && i%7==0) {
				dayPanel[i].add(new JLabel("Weather Changes"));
			}
			if (i%7==6) {
				dayPanel[i].add(new JLabel("Denizen Reset"));
			}
			ArrayList<ColorMagic> colors = realmCalendar.getColorMagic(month,i+1);
			for (ColorMagic cm:colors) {
				dayPanel[i].add(new JLabel(cm.getSmallIcon()));
			}
		}
		
		seasonIconLabel.setIcon(realmCalendar.getFullSeasonIcon(month));
		seasonNameLabel.setText(realmCalendar.getSeasonName(month));
		
		if (usingWeather && monthOffset==0) {
			weatherNameLabel.setText(realmCalendar.getWeatherName(month));
		}
		else {
			weatherNameLabel.setText("");
		}
		
		setTitle("Magic Realm Calendar - Month "+month);
		
		updateControls();
		repaint();
	}
	private void updateControls() {
		boolean usingSeasons = hostPrefs.isUsingSeasons();
		
		prevMonthButton.setEnabled(monthOffset>0 && usingSeasons);
		nextMonthButton.setEnabled(usingSeasons);
		nowMonthButton.setEnabled(monthOffset!=0);
	}
	private JEditorPane createSeasonInfoPane() {
		int month = game.getMonth()+monthOffset;
		
		GameObject season = realmCalendar.getCurrentSeason(month);
		
		String rowHeaderStart = "<tr><td align=\"right\" bgcolor=\"#33cc00\"><b>";
		String rowContentStart = "</b></td><td><b>";
		String rowEnd = "</b></td></tr>";
		
		StringBuffer text = new StringBuffer();
		text.append("<html><body><font size=\"-1\" face=\"Helvetical, Arial, sans-serif\">");
		text.append("<table cellspacing=\"2\">");
		
		// Season Name
		text.append(rowHeaderStart);
		text.append("Season:");
		text.append(rowContentStart);
		text.append(season.getName());
		text.append(rowEnd);
		
		text.append(rowHeaderStart);
		text.append("Description:");
		text.append(rowContentStart);
		text.append(season.getThisAttribute("description") == null ? "---" : season.getThisAttribute("description"));
		text.append(rowEnd);
		
		text.append(rowHeaderStart);
		text.append("VPs/Month:");
		text.append(rowContentStart);
		text.append(season.getThisAttribute("vps"));
		text.append(rowEnd);
		
		Collection<ColorMagic> c = realmCalendar.getColorMagic(month,7);
		text.append(rowHeaderStart);
		text.append("7th Day Color");
		text.append(c.size()==1?"s":"");
		text.append(":");
		text.append(rowContentStart);
		StringBuffer colors = new StringBuffer();
		for (ColorMagic cm : c) {
			if (colors.length()>0) {
				colors.append(", ");
			}
			colors.append(cm.getColorName());
		}
		text.append(colors);
		text.append(rowEnd);
		
		text.append(rowHeaderStart);
		text.append("Mountain Cost:");
		text.append(rowContentStart);
		text.append(season.getThisAttribute("mountain_cost"));
		text.append(rowEnd);
		
		text.append(rowHeaderStart);
		text.append("Mission Rewards:");
		text.append(rowContentStart);
		text.append(season.getThisAttribute("reward"));
		text.append(rowEnd);
		
		if(!hostPrefs.usesSuperRealm()) {
			text.append(rowHeaderStart);
			text.append("Food/Ale Targets:");
			text.append(rowContentStart);
			text.append(season.getThisAttribute("food_ale"));
			text.append(rowEnd);
			
			text.append(rowHeaderStart);
			text.append("Escort Party Targets:");
			text.append(rowContentStart);
			text.append(season.getThisAttribute("escort_party"));
			text.append(rowEnd);
		}
		else {
			text.append(rowHeaderStart);
			text.append("Food/Ale Targets:");
			text.append(rowContentStart);
			text.append(season.getThisAttribute("food_ale_sr"));
			text.append(rowEnd);
			
			text.append(rowHeaderStart);
			text.append("Escort Party Targets:");
			text.append(rowContentStart);
			text.append(season.getThisAttribute("escort_party_sr"));
			text.append(rowEnd);
			
			text.append(rowHeaderStart);
			text.append("Books Art Targets:");
			text.append(rowContentStart);
			text.append(season.getThisAttribute("books_art"));
			text.append(rowEnd);
			
			text.append(rowHeaderStart);
			text.append("Tour Guide Targets:");
			text.append(rowContentStart);
			text.append(season.getThisAttribute("tour_guide"));
			text.append(rowEnd);
		}
		
		text.append("</table>");
		
		if (hostPrefs.hasPref(Constants.OPT_WEATHER)) {
			String[] weather = {"clear","showers","storm","special"};
			text.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"0\">");
			text.append("<th>Die Roll</th><th>Weather</th><th>Days</th><th>Basic</th><th>Sunlight</th><th>Sheltered</th><th>Special</th>\n");
			for (int i=weather.length-1;i>=0;i--) {
				boolean thisWeather = monthOffset==0 && realmCalendar.getWeatherTypeName(month).toLowerCase().equals(weather[i]);
				if (thisWeather) {
					text.append("<tr bgcolor=\"#ffff99\">");
				}
				else {
					text.append("<tr>");
				}
				text.append("<td align=\"center\" valign=\"top\">");
				switch(i) {
					case 0: text.append("6"); break;
					case 1: text.append("5"); break;
					case 2: text.append("4"); break;
					default: text.append("1-3"); break;
				}
				text.append("</td><td align=\"center\" valign=\"top\">");
				String name = season.getAttribute(weather[i],"name");
				if (!name.equals(weather[i])) {
					name = name+" ("+weather[i]+")";
				}
				text.append(name);
				text.append("</td><td align=\"center\" valign=\"top\">");
				text.append(filter(season.getAttribute(weather[i],"days")));
				text.append("</td><td align=\"center\" valign=\"top\">");
				text.append(filter(season.getAttribute(weather[i],"basic")));
				text.append("</td><td align=\"center\" valign=\"top\">");
				text.append(filter(season.getAttribute(weather[i],"sunlight")));
				text.append("</td><td align=\"center\" valign=\"top\">");
				text.append(filter(season.getAttribute(weather[i],"sheltered")));
				text.append("</td><td align=\"left\" valign=\"top\">");
				text.append(filter(season.getAttribute(weather[i],"description")));
				text.append("</td></tr>");
			}
			text.append("</table>");
		}
		
		text.append("</font></body></html>");
		
		JEditorPane pane = new JEditorPane("text/html",text.toString()) {
			public boolean isFocusTraversable() {
				return false;
			}
		};
		pane.setEditable(false);
		pane.setOpaque(false);
		return pane;
	}
	protected void showSeasonInfo() {
		JOptionPane.showMessageDialog(this,createSeasonInfoPane(),"Season/Weather Detail",JOptionPane.PLAIN_MESSAGE);
	}

	protected String filter(String in) {
		return in==null?"":in;
	}
	
	public void cleanup() {
	}
	public String getKey() {
		return "calendar";
	}
	/**
	 * For testing only
	 */
	public static void main(String[] args) {
		RealmLoader loader = new RealmLoader();
		HostPrefWrapper hostPrefs = HostPrefWrapper.createDefaultHostPrefs(loader.getData());
		hostPrefs.setStartingSeason("Random");
		hostPrefs.setPref(Constants.OPT_WEATHER,true);
		GameWrapper game = GameWrapper.findGame(loader.getData());
		game.setDay(13);
		ComponentTools.setSystemLookAndFeel();
		RealmCalendarViewer view = new RealmCalendarViewer(loader.getData());
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}
}