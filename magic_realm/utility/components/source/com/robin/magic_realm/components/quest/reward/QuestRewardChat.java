package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmSpeak.RealmSpeakFrame;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardChat extends QuestReward {
	
	public static final String SENDER = "_sender";
	public static final String TEXT = "_text";
	
	public QuestRewardChat(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		for (int i = 0; i < 8; i++) {
			  if (frame instanceof RealmSpeakFrame) {
				  ((RealmSpeakFrame)frame).getGameHandler().broadcastChatNamed(sender(), text());
				  break;
			  }
			}
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Chat;
	}
	@Override
	public String getDescription() {
		return "Sends a chat message.";
	}
	
	public String sender() {
		return getString(SENDER);
	}
	public String text() {
		return getString(TEXT);
	}
}