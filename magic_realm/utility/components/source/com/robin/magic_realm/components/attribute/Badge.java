package com.robin.magic_realm.components.attribute;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.robin.general.swing.ImageCache;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Badge extends JLabel {
	private static final Stroke thick = new BasicStroke(3);
	
	private ImageIcon icon;
	private String tipText;
	private boolean active = true;
	
	private Badge(ImageIcon icon,String tipText) {
		super(icon);
		this.icon = icon;
		this.tipText = tipText;
		setIcon(icon);
		setToolTipText(tipText);
	}
	public boolean isFamiliar() {
		return tipText.startsWith("FAMILIAR"); // special case
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			setIcon(icon);
		}
		else {
			BufferedImage bi = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = (Graphics2D)bi.getGraphics();
			g.drawImage(icon.getImage(),0,0,null);
			g.setColor(Color.red);
			g.setStroke(thick);
			g.drawLine(0,0,32,32);
			g.drawLine(0,32,32,0);
			setIcon(new ImageIcon(bi));
		}
	}
	
	public static String makeBadgeName(String advantage) {
		int colon = advantage.indexOf(':');
		String iconName = advantage.substring(0,colon).toLowerCase().replace(' ','_');
		return "badges/"+iconName;
	}
	
	private static Hashtable<String,Badge> cached = new Hashtable<>();
	public static Badge getBadge(CharacterWrapper character,String advantage) {
		String key = character.getGameObject().getStringId()+advantage;
		Badge badge = cached.get(key);
		if (badge==null) {
			ImageIcon icon;
			if (character.getGameObject().hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
				String badgeName = "custom/badges/one"; // default
				for (int n=1;n<=4;n++) {
					String levelKey = "level_"+n;
					ArrayList<String> list = character.getGameObject().getAttributeList(levelKey,"advantages");
					if (list!=null && list.contains(advantage)) {
						badgeName = character.getGameObject().getAttribute(levelKey,"badge_icon");
						break;
					}
					for (int i=1;i<=2;i++) {
						levelKey = "level_"+n+"_"+i;
						list = character.getGameObject().getAttributeList(levelKey,"advantages");
						if (list!=null && list.contains(advantage)) {
							badgeName = character.getGameObject().getAttribute(levelKey,"badge_icon");
							break;
						}
					}
				}
				icon = ImageCache.getIcon(badgeName);
			}
			else {
				icon = ImageCache.getIcon(makeBadgeName(advantage));
			}
			badge = new Badge(icon,advantage);
			cached.put(key,badge);
		}
		return badge;
	}
	public static Badge getGuildBadge(CharacterWrapper character) {
		String guildBadgeName = character.getCurrentGuildBadgeName();
		if (guildBadgeName!=null) {
			return new Badge(ImageCache.getIcon("badges/"+guildBadgeName),character.getCurrentGuildLevelName());
		}
		return null;
	}
	public static void clearBadgeCache() {
		cached.clear();
	}
}