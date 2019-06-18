<%--
Header details of an Inbound Mesage Activity.

Expected params
msg : The InboundMessage object to display headers for
containerId : The ID of the container DIV to use for expandable area

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/otherHeaderDetails.jsp#1 $$Change: 946917 $
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
          <fmt:message key="response.message.allheaders.label" />
        </a>
      </dt>
      <dd></dd>
    </dl>

    <%-- Hidden div for details --%>
    <div id="<c:out value='${containerId}'/>" style="display:none;" >

      <dl class="activityOtherHeaders">
         <c:forEach var='item' items='${msg.headers}'>
            <dt><c:out value="${item.key}:"/></dt>
            <dd><c:out value="${item.value}"/></dd>
         </c:forEach>
      </dl>

    </div><%-- Hidden div for details --%>

  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/otherHeaderDetails.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/otherHeaderDetails.jsp#1 $$Change: 946917 $--%>
