<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ page contentType="application/json; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<dspel:page xml="true">
	<dspel:importbean var="messageCalculator" bean="/atg/arm/service/channel/SmsMessageSizeCalculator"/>
	<dspel:getvalueof var="messageText" param="message"/>
	<%
	
	atg.arm.respond.util.SmsMessageSizeCalculator messageCalc = 
		(atg.arm.respond.util.SmsMessageSizeCalculator)pageContext.findAttribute("messageCalculator");
		
	String messageText = 
		(String)pageContext.findAttribute("messageText");
	
	String messageSize = Long.toString(messageCalc.calculateSMSTextSize(messageText));	
	String numParts = Integer.toString(messageCalc.getNumParts(messageText));
	
	pageContext.setAttribute("messageSize", messageSize);
	pageContext.setAttribute("noOfParts", numParts);
	%>
	
	<json:object>
	  <json:property name="size" value="${messageSize}"/>
	  <json:property name="noOfParts" value="${noOfParts}"/>
	</json:object>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/SMSMessageSizeCalc.jsp#1 $$Change: 946917 $--%>
