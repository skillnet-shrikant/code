<%@  include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <c:set var="actvPre" value="alt" scope="request"/>
    <c:set var="actvFormHandler" scope="request" value="/atg/svc/ui/formhandlers/TicketActivityListFormHandler"/>
    <%--
      unused currently
    --%>
    <c:set var="ticketHolder" value="/atg/svc/ticketing/ViewTicketHolder"/>

    <dspel:importbean 
      scope="page"
      var="ticketActivityListFormHandler"
      bean="${actvFormHandler}" />

    <%-- List results top control bar --%>

    <%-- ActivityList results table --%>
    <table>
      <thead style="bgGray pad5">
        <%-- Expandomatic --%>
        <th style="w5p">&nbsp;</th> 

        <%-- Type --%>
        <th style="left"><fmt:message key="panel.ticketActivity.type"/></th>

        <%-- Source --%>
        <th style="left"><fmt:message key="panel.ticketActivity.source"/></th>

        <%-- 'Activity' (description?) --%>
        <th style="left"><fmt:message key="panel.ticketActivity.activity"/></th>

        <%-- Date --%>
        <th style="left"><fmt:message key="panel.ticketActivity.date"/></th>
      </thead>

      <dspel:importbean
          scope="request"
          var="defaultActivityInfo"
          bean="/atg/ticketing/activities/default"/>

      <c:forEach items="${ticketActivityListFormHandler.allActivities}"
                 var="result"
                 varStatus="count">
        <dspel:importbean
          scope="request"
          var="activityInfo"
          bean="/atg/ticketing/activities/${result.item.type}"/>

          <c:if test="${(count.count % 2) == 0}">
          <tr class="row">
          </c:if>
          <c:if test="${(count.count % 2) == 1}">
          <tr class="alternateRow">
          </c:if>

          <%-- set these variables so they're available in the various column renderers --%>
          <c:set var="activity" scope="request" value="${result.item}"/>
          <c:set var="activityItem" scope="request" value="${result.repositoryItem}"/>
          <c:set var="imageId" value="${result.item.id}"/>

          <%-- Expandomatic --%>
          <td style="w5p">
            <%-- 
              Changes to how the detail page is found must be reflected in 
              activityDetailContainer.jsp
            --%>
            <c:set var="detailPage" value="${activityInfo.detailPage}"/>
            <c:set var="ctx" value="${activityInfo.detailPageContext}"/>

          </td>

          <%-- 
            use defaultActivityInfo for activityInfo if original activityInfo 
            is null. Do this after the Expandomatic column, so no detail page
            option shows for activity types with no detail page.
          --%>
          <c:if test="${ empty activityInfo }">
            <c:set var="activityInfo" value="${defaultActivityInfo}" scope="request"/>
          </c:if>

          <%-- Type --%>
          <td class="padLeft5 w10p">
            <c:set var="renderer" value="${activityInfo.typeColRenderer}"/>
            <c:set var="ctx" value="${activityInfo.typeColRendererContext}"/>
            <c:if test="${ empty renderer }">
              <c:set var="renderer" value="${defaultActivityInfo.typeColRenderer}"/>
              <c:set var="ctx" value="${defaultActivityInfo.typeColRendererContext}"/>
            </c:if>
            <dspel:include otherContext="${ctx}" src="${renderer}" />
          </td>

          <%-- Source --%>
          <td style="width: 20%">
            <c:set var="renderer" value="${activityInfo.sourceColRenderer}"/>
            <c:set var="ctx" value="${activityInfo.sourceColRendererContext}"/>
            <c:if test="${ empty renderer }">
              <c:set var="renderer" value="${defaultActivityInfo.sourceColRenderer}"/>
              <c:set var="ctx" value="${defaultActivityInfo.sourceColRendererContext}"/>
            </c:if>
            <dspel:include otherContext="${ctx}" src="${renderer}" />
          </td>

          <%-- 'Activity' (Description/heading) --%>
          <td style="width: 45%">
            <c:set var="renderer" value="${activityInfo.activityColRenderer}"/>
            <c:set var="ctx" value="${activityInfo.activityColRendererContext}"/>
  	  <c:if test="${ empty renderer }">
              <c:set var="renderer" value="${defaultActivityInfo.activityColRenderer}"/>
              <c:set var="ctx" value="${defaultActivityInfo.activityColRendererContext}"/>
            </c:if>
            <dspel:include src="${renderer}"  otherContext="${UIConfig.contextRoot}"/>
          </td>

          <%-- Date --%>
          <td style="width: 20%">
            <fmt:formatDate value="${result.item.creationTime}"
              type="both" dateStyle="short" timeStyle="short"/>
          </td>

        </tr>

        <%-- activity detail rendered to this div --%>
        <c:if test="${!isPrinting}"><c:set var="activityStyle" value="display:none"/></c:if>
        <c:if test="${isPrinting}"><c:set var="activityStyle" value=""/></c:if>
        <div id="<c:out value="${actvPre}Close${imageId}"/>" style="<c:out escapeXml='false' value='${activityStyle}'/>">
        <c:if test="${ ! empty detailPage }">
          <dspel:include src="/include/activities/activityDetailContainerDirect.jsp" otherContext="${UIConfig.contextRoot}">
            <dspel:param name="activityId" value="${result.item.id}"/>
            <dspel:param name="activityTicketHolder" value="${ticketHolder}"/>
            <dspel:param name="containerDivId" value="${actvPre}Close${imageId}"/>
          </dspel:include>
        </c:if>
      </div>

      </c:forEach>
    </table>

  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/baseTicketActivityListResultsNoTreeTable.jsp#1 $$Change: 946917 $--%>
