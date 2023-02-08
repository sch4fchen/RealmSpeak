package com.robin.magic_realm.components;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.DebugUtility;

public abstract class StateChitComponent extends SquareChitComponent {

	public static final String FACE_UP = DARK_SIDE_UP;
	public static final String FACE_DOWN = LIGHT_SIDE_UP;
	
	protected StateChitComponent(GameObject obj) {
		super(obj);
		lightColor = MagicRealmColor.WHITE;
	}
	public String getLightSideStat() {
		return "this";
	}
	public String getDarkSideStat() {
		return "this";
	}
	protected void explode() {
		// this implementation does nothing
	}
	public int getChitSize() {
		return S_CHIT_SIZE;
	}
	public boolean isFaceUp() {
		return isDarkSideUp();
	}
	public ImageIcon getNotesIcon() {
		if (isFaceDown()) {
			return new ImageIcon(getFlipSideImage().getScaledInstance(50,50,Image.SCALE_DEFAULT));
		}
		return getMediumIcon();
	}
	public Image getFaceUpImage() {
		if (!isFaceUp()) {
			return getFlipSideImage();
		}
		return getImage();
	}
	public boolean isFaceDown() {
		return isLightSideUp();
	}
	public void setFaceUp() {
		setDarkSideUp();
		explode();
		if (!getGameObject().hasThisAttribute("seen")) {
			getGameObject().setThisAttribute("seen");
		}
	}
	public void addSummonedToday(int dieResult) {
		getGameObject().addThisAttributeListItem(Constants.SUMMONED_TODAY,String.valueOf(dieResult));
	}
	public boolean hasSummonedToday(int dieResult) {
		if (DebugUtility.isSummonMultiple()) return false;
		return getGameObject().hasThisAttributeListItem(Constants.SUMMONED_TODAY,String.valueOf(dieResult));
	}
	public void clearSummonedToday() {
		if (getGameObject().hasThisAttribute(Constants.SUMMONED_TODAY)) {
			getGameObject().removeThisAttribute(Constants.SUMMONED_TODAY);
		}
	}
	public boolean hasBeenSeen() {
		return getGameObject().hasThisAttribute("seen");
	}
	/**
	 * Not sure how else to do this - this version of the method will not allow the RedSpecialChitComponent to explode itself
	 */
	public void setFaceUpWithoutExplode() {
		setDarkSideUp();
	}
	public void setFaceDown() {
		setLightSideUp();
	}
	protected String getExtraBoardShadingType() {
		// State chits should NOT reveal their board affiliation when they are face down
		if (isFaceUp()) {
			return super.getExtraBoardShadingType();
		}
		return null;
	}
}