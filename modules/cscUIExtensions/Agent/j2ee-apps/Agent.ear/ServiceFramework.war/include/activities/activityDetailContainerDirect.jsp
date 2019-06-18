<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/activityDetailContainerDirect.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>


<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
    <dspel:droplet name="/atg/targeting/RepositoryLookup">
      <dspel:param name="repository" bean="/atg/ticketing/TicketingRepository"/>
      <dspel:param name="itemDescriptor" value="ticketActivity"/>
      <dspel:param name="id" value="${param.activityId}"/>
      <dspel:oparam name="output">
        <dspel:getvalueof var="activityItem" param="element"/>
        <c:set var="activityItem" scope="request" value="${activityItem}"/>
        <dspel:tomap var="activity" param="element"/>
        <c:set var="activity" scope="request" value="${activity}"/>
        <dspel:importbean
          scope="request"
          var="activityInfo"
          bean="/atg/ticketing/activities/${activity.type}"/>
        <dspel:importbean
          scope="request"
          var="defaultActivityInfo"
          bean="/atg/ticketing/activities/default"/>
        <c:if test="${ empty activityInfo }">
          <c:set var="activityInfo" value="${defaultActivityInfo}"/>
        </c:if>

        <%-- 
          Changes to how the detail page is found must be reflected in 
          ticketActivityListResults.jsp  
        --%>
        <c:set var="detailPage" value="${activityInfo.detailPage}"/>
        <c:set var="ctx" value="${activityInfo.detailPageContext}"/>
        <c:if test="${ empty detailPage }">
          <c:set var="detailPage" value="${defaultActivityInfo.detailPage}"/>
          <c:set var="ctx" value="${defaultActivityInfo.detailPageContext}"/>
        </c:if>
        <dspel:include otherContext="${ctx}" src="${activityInfo.detailPage}"/>
      </dspel:oparam>
    </dspel:droplet>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/activityDetailContainerDirect.jsp#1 $$Change: 946917 $--%>
