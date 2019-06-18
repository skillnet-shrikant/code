package com.mff.globalmessages;

import java.util.List;


public class MessagesResponse {

	private List<GlobalAlertMessage> messages;
	private List<GlobalAlertMessage> alerts;
	
	public List<GlobalAlertMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<GlobalAlertMessage> pMessages) {
		messages = pMessages;
	}
	public List<GlobalAlertMessage> getAlerts() {
		return alerts;
	}
	public void setAlerts(List<GlobalAlertMessage> pAlerts) {
		alerts = pAlerts;
	}
	
}
