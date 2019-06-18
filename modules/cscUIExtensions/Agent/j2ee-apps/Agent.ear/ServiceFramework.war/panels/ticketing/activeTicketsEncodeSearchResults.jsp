<%--
 Active tickets JSON object creation
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/activeTicketsEncodeSearchResults.jsp#2 $
 @updated $DateTime: 2015/02/26 10:47:28 $
--%>

<%@ include file="/include/top.jspf" %>

<dspel:page>


  <dspel:importbean var="activeTicketsSearchFormHandler" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler" scope="request" />
  <dspel:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dspel:importbean bean="/atg/dynamo/droplet/Switch"/>

<%--  
  <dspel:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
--%>
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
<json:object prettyPrint="${UIConfig.prettyPrintResponses}">
 
<json:property name="resultLength" value="${activeTicketsSearchFormHandler.totalItemCount}"/>
  <json:array name="results" items="${activeTicketsSearchFormHandler.searchResults}" var="ticketItem">
    <json:object>
 
      <json:property name="id" value='${ticketItem.item.id}'/>
      <json:property name="activities" value='${ticketItem.managerData.numberOfActivities}'/>
      <json:property name="description" escapeXml="false" value='${ticketItem.item.description}'/>
      <json:property name="group" value='${ticketItem.managerData.group}'/>
      <json:property name="escalationLevel" value='${ticketItem.item.escalationLevel}'/>
      
      <c:set var="escalationLevelString"><fmt:message key="${ticketItem.item.escalationLevel}"/></c:set>
      <json:property name="localizedEscalationLevel" value='${escalationLevelString}'/>

      <dspel:tomap var="substatus" value="${ticketItem.item.subStatus}"/>
       <c:set var="statusString"><fmt:message key="tickets.status.${ticketItem.managerData.status}"/> (<fmt:message key="tickets.status.${substatus.subStatusName}"/>)</c:set>
      <json:property name="subStatus" value='${statusString}'/>
      <json:property name="dueTime" escapeXml="false">
        <fmt:formatDate type="both" value="${ticketItem.item.dueTime}" dateStyle="short" timeStyle="short"/>
      </json:property>

      <c:choose>
        <c:when test='${result.item.creationChannel == "web"}'>
          <%-- Currently we do not have an image for web. Replace with correct image later. --%>
          <fmt:message var="selfServiceEsc" key="tooltip.self-service-esc" />
          <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_selfServiceEscalation.gif" width="21" height="21" align="absmiddle" title="${selfServiceEsc}" alt="${selfServiceEsc}" /></c:set>
        </c:when>
        <c:when test='${result.item.creationChannel == "email"}'>
          <fmt:message var="email" key="tooltip.email" />
          <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_emailChannel.gif" width="21" height="21" align="absmiddle" title="${email}" alt="${email}" /></c:set>
        </c:when>
        <c:when test='${result.item.creationChannel == "telephony"}'>
          <fmt:message var="phoneCall" key="tooltip.phonecall" />
          <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_phone.gif" width="21" height="21" align="absmiddle" title="${phoneCall}" alt="${phoneCall}" /></c:set>
        </c:when>
        <c:when test='${result.item.creationChannel == "SMS"}'>
          <fmt:message var="sms" key="tooltip.sms" />
          <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_smsChannel.gif" width="21" height="21" align="absmiddle" title="${sms}" alt="${sms}" /></c:set>
        </c:when>
        <c:when test='${result.item.creationChannel == "MMS"}'>
          <fmt:message var="mms" key="tooltip.mms" />
          <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_mmsChannel.gif" width="21" height="21" align="absmiddle" title="${mms}" alt="${mms}" /></c:set>
        </c:when>
        <c:when test='${result.item.creationChannel == "chat"}'>
          <fmt:message var="chat" key="tooltip.chat" />
          <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_chat.gif" width="21" height="21" align="absmiddle" title="${chat}" alt="${chat}" /></c:set>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test='${result.item.defaultOutboundChannel == "email"}'>
              <fmt:message var="email" key="tooltip.email" />
              <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_emailChannel.gif" width="21" height="21" align="absmiddle" title="${email}" alt="${email}" /></c:set>
            </c:when>
            <c:otherwise>
              <%-- Currently we do not have an image for unknown. Replace with correct image later. --%>
              <fmt:message var="custChannel" key="tooltip.customer-channel" />
              <c:set var="imageHtml"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_customChannel.gif" width="21" height="21" align="absmiddle" title="${custChannel}" alt="${custChannel}" /></c:set>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
      <json:property name="imageHtml" value='${imageHtml}' escapeXml="false"/>  
      <json:property name="age" value='${ticketItem.managerData.age}'/>
      <json:property name="creationTime" escapeXml="false">
        <fmt:formatDate type="both" value="${ticketItem.item.creationTime}" dateStyle="short" timeStyle="short" />
      </json:property>
    </json:object>
  </json:array>

</json:object>
</dspel:layeredBundle>
</dspel:page>

<%-- Version: $Change: 953229 $$DateTime: 2015/02/26 10:47:28 $--%>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/activeTicketsEncodeSearchResults.jsp#2 $$Change: 953229 $--%>
