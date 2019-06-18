<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/outboundMessageTypeColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>


<%@  include file="/include/top.jspf" %>
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
<dspel:page xml="true">
  <span class="textLeft_iconRight">
    <c:choose>
      <c:when test="${activity.channel == 'email'}">
	  <fmt:message var="email" key="response.channel.email" />
        <div class="atgServiceFrameworkEmailChannelIcon" title="${email}"></div>
      </c:when>
      <c:when test="${activity.channel == 'SMS'}">
	  <fmt:message var="sms" key="response.channel.sms" />
        <div class="atgServiceFrameworkSmsChannelIcon" title="${sms}"></div>
      </c:when>
      <c:when test="${activity.channel == 'MMS'}">
	  <fmt:message var="mms" key="tooltip.mmsChannel" />
        <div class="atgServiceFrameworkMmsChannelIcon" title="${mms}"></div>
      </c:when>
      <c:when test="${activity.channel == 'telephony'}">
	  <fmt:message var="phonecall" key="tooltip.phonecall" />
        <div class="atgServiceFrameworkPhoneIcon" title="${phonecall}"></div>
      </c:when>
      <c:when test="${activity.channel == 'webPage'}">
	  <fmt:message var="webpage" key="tooltip.webpage" />
        <div class="atgServiceFrameworkWebPageIcon" title="${webpage}"></div>
      </c:when>
      <c:otherwise>
	  <fmt:message var="other" key="tooltip.other" />
        <div class="atgServiceFrameworkOtherIcon" title="${other}"></div>
      </c:otherwise>
    </c:choose>   
   
    <dspel:img src="${UIConfig.contextRoot}/${activityInfo.directionIcon}" width="21" height="21" align="absmiddle"/>
    <c:if test="${activity.hasAttachments}">
      <dspel:img src="${UIConfig.contextRoot}/${activityInfo.attachmentIcon}" width="21" height="21" align="absmiddle"/>
    </c:if>
  </span>
</dspel:page>
</dspel:layeredBundle>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/outboundMessageTypeColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/outboundMessageTypeColRenderer.jsp#1 $$Change: 946917 $--%>
