<%--
Header details of an Inbound Mesage Activity.

Expected params
msg : The InboundMessage object to display headers for
containerId : The ID of the container DIV to use for expandable area

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/headerDetails.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="../top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <%-- Get EL reference to dspel:param parameters --%>
    <dspel:getvalueof var="msg" param="msg"/>
    <dspel:getvalueof var="containerId" param="containerId"/>
    <c:set var="arrowId" value="${containerId}_arrow"/>

    <dl>
      <dt class="trigger">
        <a href="#" onclick="toggle('<c:out value="${containerId}"/>', '<c:out value="${arrowId}"/>');return false;">
          <dspel:img id="${arrowId}" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/>
          <fmt:message key="response.message.headerdetails.label" />
        </a>
      </dt>
      <dd></dd>
    </dl>

    <%-- Hidden div for details --%>
    <div id="<c:out value='${containerId}'/>" style="display:none;" >

      <dl class="activityOtherHeaders">
        <%-- Addresses --%>


    <!-- Fix for the bug 141129  -->
      <table>
        <c:forEach var='item' items='${msg.headers}'>
          <tr>
            <td><c:out value="${item.key}:"/></td>
            <td><c:out value="${item.value}"/></td>
          </tr>
        </c:forEach>
      

		
        <c:if test="${not empty msg.encoding }">
		<tr>
          <td><fmt:message key="response.message.encoding.label" /></td>
          <td><c:out value="${msg.encoding}"><fmt:message key="response.message.unknown.label"/></c:out>
            <%-- Commenting out for Q3 release
            <a href="#" title="<fmt:message key="response.message.change.encoding.label" />">
              <img alt="<fmt:message key="response.message.change.encoding.label" />"
                src="image/iconcatalog/25x22/agent_utilities/icon_changeEncoding.gif" width="25" height="22" />
            </a>
            --%>
          </td>
		  <tr>
        </c:if>
		

        <c:if test="${msg.inbound && !empty msg.classifiedResults}">
		<tr>
           <td><fmt:message key="response.message.classifications.label" /></td>
           <td>
             <table class="data autoWidth">
               <tr>
                 <th><fmt:message key="response.message.ticketqueue.label" /></th>
                 <th><fmt:message key="response.message.classification.label" /></th>
                 <th><fmt:message key="response.message.confidence.label" /></th>
               </tr>
               <c:forEach var="classification" items="${msg.classifiedResults}" varStatus="status">
                 <%-- Set alternate table row styles --%>
                 <c:choose>
                   <c:when test="${(status.count mod 2) == 0}">
                     <c:set var="styleClass" value="alt"/>
                   </c:when>
                   <c:otherwise>
                     <c:set var="styleClass" value=""/>
                   </c:otherwise>
                 </c:choose>

                 <tr class="<c:out value='${styleClass}'/>">
                   <td>
                     <c:out value="${classification.RMTarget.ticketQueueIncludeInherited.name}"/>
                   </td>
                   <td>
                     <c:out value="${classification.RMTarget.name}"/>
                   </td>
                   <td>
                     <span class="score"><span class="percent">
                       <span class="bar" style="width:<c:out value='${classification.confidence}'/>%"></span>
                     </span>
                     <span class="title"><c:out value="${classification.confidence}"/>%</span>
                     </span>
                     <div style="clear:both"></div>
                   </td>
                 </tr>
               </c:forEach>
             </table>
           </td>
		   </tr>
        </c:if>
		</table>
      </dl>

    </div><%-- Hidden div for details --%>

  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/headerDetails.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/headerDetails.jsp#1 $$Change: 946917 $--%>
