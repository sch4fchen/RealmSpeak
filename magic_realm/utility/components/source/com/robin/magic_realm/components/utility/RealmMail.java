package com.robin.magic_realm.components.utility;

import java.util.ArrayList;

import javax.mail.MessagingException;

import com.robin.general.io.SendMail;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class RealmMail {
	public static String sendMail(HostPrefWrapper hostPrefs,ArrayList<String> recipients,String subtitle,String message) {
		return sendMail(hostPrefs.getSmtpHost(),hostPrefs.getHostEmail(),recipients,hostPrefs.getGameTitle(),subtitle,message);
	}
	public static String sendMail(String smtpHost,String hostEmail,ArrayList<String> recipients,String gameTitle,String subtitle,String message) {
		SendMail sm = new SendMail(smtpHost,hostEmail,recipients,null,null);
		sm.setSubject("["+gameTitle+"] - "+subtitle);
		sm.setMessage(message);
		String error = null;
		try {
			if (!sm.postMail()) {
				error = sm.getError();
			}
		}
		catch(MessagingException ex) {
			error = ex.toString();
		}
		return error;
	}
}