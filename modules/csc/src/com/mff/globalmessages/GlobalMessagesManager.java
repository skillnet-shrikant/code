package com.mff.globalmessages;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class GlobalMessagesManager extends GenericService {
	
	Repository mGlobalMessagesRepository;
	private static final String ITEM_DESCRIPTOR_GLOBAL_MESSAGES = "globalMessages";
	private static final String MESSAGE_TYPE="messageType";
	private static final String MESSAGE_TEXT="messageText";
	private static final String MESSAGE_DATE="messageStartDate";
	private static final String MESSAGE_TYPE_ALERT="Alert";
	private static final String MESSAGE_TYPE_MSG="Message";
	
	private static final String MESSAGE_ALERT_RQL_QUERY = "(messageDestination = 0 or messageDestination =?0) and (messageStartDate is null or messageStartDate<= ?1)"
			+ "and (messageEndDate is null or messageEndDate>=?1) order by messageStartDate desc";
	
	public MessagesResponse getGlobalMessageAndAlerts(String pMsgDestination,String pQuery) throws RepositoryException, Exception {
		if(pQuery==null || pQuery.trim().isEmpty()){
			return getGlobalMessageAndAlerts(pMsgDestination);
		}
		else {
			vlogDebug("getGlobalMessageAndAlerts invoked from  GlobalMessagesManager: MsgDestination {0} pQuery {1}", pMsgDestination,pQuery);
			MessagesResponse mMessagesResponse=null;
			RepositoryItemDescriptor globalMsgItemDesc = getGlobalMessagesRepository().getItemDescriptor(ITEM_DESCRIPTOR_GLOBAL_MESSAGES);
			RepositoryView globalMsgRepView = globalMsgItemDesc.getRepositoryView();
			RqlStatement statement = RqlStatement.parseRqlStatement(pQuery);
			Object params[] = new Object[2];
			Calendar lCal=new GregorianCalendar();
		    java.util.Date lToday=lCal.getTime();
			params[0] = pMsgDestination;
			params[1]=lToday;
			vlogDebug("Executing query :{0}", statement.getQuery().toString());
			RepositoryItem[] globalMsgs = statement.executeQuery(globalMsgRepView, params);
			
			vlogDebug("Results having no of items :{0}", globalMsgs!=null?globalMsgs.length:0);
			if(globalMsgs==null || globalMsgs.length==0) {
				vlogDebug("No Global Messages or Alerts at this moment");
			}else {
				mMessagesResponse=new MessagesResponse();
				ArrayList<GlobalAlertMessage>  mAlertList= new ArrayList<GlobalAlertMessage>();
				ArrayList<GlobalAlertMessage>  mMsgsList= new ArrayList<GlobalAlertMessage>();
				for (RepositoryItem globalMessage : globalMsgs) {
					String messageType=(String) globalMessage.getPropertyValue(MESSAGE_TYPE);
					GlobalAlertMessage mGlobalAlert=new GlobalAlertMessage();
					mGlobalAlert.setMessageType(messageType);
					mGlobalAlert.setMessageText((String) globalMessage.getPropertyValue(MESSAGE_TEXT));
					Object startDate=globalMessage.getPropertyValue(MESSAGE_DATE);
					if(startDate!=null){
						LocalDate localDate;
						if(startDate instanceof java.sql.Date){
							localDate= ((java.sql.Date)startDate).toLocalDate();
						}
						else {
							localDate = ((Date)startDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						}
						int year  = localDate.getYear();
						int month = localDate.getMonthValue();
						int day   = localDate.getDayOfMonth();
						String startDateInString=month+"/"+day+"/"+year;
						mGlobalAlert.setMessageStartDate(startDateInString);
					}
					if(messageType.equalsIgnoreCase(MESSAGE_TYPE_ALERT)) {
						mAlertList.add(mGlobalAlert);
					}else if(messageType.equalsIgnoreCase(MESSAGE_TYPE_MSG)) {
						mMsgsList.add(mGlobalAlert);
					}
			     }
				mMessagesResponse.setAlerts(mAlertList);
				mMessagesResponse.setMessages(mMsgsList);
				vlogDebug("Prepared Message Response :{0}", (mMessagesResponse.getAlerts()!=null && mMessagesResponse.getAlerts().size()>0)?mMessagesResponse.getAlerts().get(0).getMessageText():"NoAlerts");
			}	
			return mMessagesResponse;
		}
	}
	public MessagesResponse getGlobalMessageAndAlerts(String pMsgDestination) throws RepositoryException, Exception {
		vlogDebug("getGlobalMessageAndAlerts invoked from  GlobalMessagesManager: MsgDestination {0}", pMsgDestination);
		MessagesResponse mMessagesResponse=null;
		RepositoryItemDescriptor globalMsgItemDesc = getGlobalMessagesRepository().getItemDescriptor(ITEM_DESCRIPTOR_GLOBAL_MESSAGES);
		RepositoryView globalMsgRepView = globalMsgItemDesc.getRepositoryView();
		RqlStatement statement = RqlStatement.parseRqlStatement(MESSAGE_ALERT_RQL_QUERY);
		Object params[] = new Object[2];
		Calendar lCal=new GregorianCalendar();
	    Date lToday=lCal.getTime();
		params[0] = pMsgDestination;
		params[1]=lToday;
		vlogDebug("Executing query :{0}", statement.getQuery().toString());
		RepositoryItem[] globalMsgs = statement.executeQuery(globalMsgRepView, params);
		
		vlogDebug("Results having no of items :{0}", globalMsgs!=null?globalMsgs.length:0);
		if(globalMsgs==null || globalMsgs.length==0) {
			vlogDebug("No Global Messages or Alerts at this moment");
		}else {
			mMessagesResponse=new MessagesResponse();
			ArrayList<GlobalAlertMessage>  mAlertList= new ArrayList<GlobalAlertMessage>();
			ArrayList<GlobalAlertMessage>  mMsgsList= new ArrayList<GlobalAlertMessage>();
			for (RepositoryItem globalMessage : globalMsgs) {
				String messageType=(String) globalMessage.getPropertyValue(MESSAGE_TYPE);
				GlobalAlertMessage mGlobalAlert=new GlobalAlertMessage();
				mGlobalAlert.setMessageType(messageType);
				mGlobalAlert.setMessageText((String) globalMessage.getPropertyValue(MESSAGE_TEXT));
				Object startDate=globalMessage.getPropertyValue(MESSAGE_DATE);
				if(startDate!=null){
					LocalDate localDate;
					if(startDate instanceof java.sql.Date){
						localDate= ((java.sql.Date)startDate).toLocalDate();
					}
					else {
						localDate = ((Date)startDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					}
					int year  = localDate.getYear();
					int month = localDate.getMonthValue();
					int day   = localDate.getDayOfMonth();
					String startDateInString=month+"/"+day+"/"+year;
					mGlobalAlert.setMessageStartDate(startDateInString);
				}
				if(messageType.equalsIgnoreCase(MESSAGE_TYPE_ALERT)) {
					mAlertList.add(mGlobalAlert);
				}else if(messageType.equalsIgnoreCase(MESSAGE_TYPE_MSG)) {
					mMsgsList.add(mGlobalAlert);
				}
		     }
			mMessagesResponse.setAlerts(mAlertList);
			mMessagesResponse.setMessages(mMsgsList);
			vlogDebug("Prepared Message Response :{0}", (mMessagesResponse.getAlerts()!=null && mMessagesResponse.getAlerts().size()>0)?mMessagesResponse.getAlerts().get(0).getMessageText():"NoAlerts");
		}	
		return mMessagesResponse;
	}

	public Repository getGlobalMessagesRepository() {
		return mGlobalMessagesRepository;
	}

	public void setGlobalMessagesRepository(Repository pGlobalMessagesRepository) {
		mGlobalMessagesRepository = pGlobalMessagesRepository;
	}
}
