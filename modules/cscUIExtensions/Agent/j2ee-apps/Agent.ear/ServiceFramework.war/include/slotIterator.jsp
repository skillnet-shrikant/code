<%--

Iterates over the slots in a panel

@version $$
@updated $$

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="panelId" param="panelId"/>

  <fw-beans:panelDefinitionFindByPanelId appId="workspace"
                                         panelId="${panelId}"
                                         var="panelDefinition"/>

  <%-- Iterate over all panel slot/renderer combos --%>
  <c:forEach items="${panelDefinition.slotRendererCombos}"
             var="slotRendererCombo">
    <c:set scope="request"
           value="${slotRendererCombo}"
           var="slotRendererCombo"/>

    <%-- Import slot --%>
    <dspel:importbean bean="${slotRendererCombo.slot.path}"
                      scope="request"
                      var="slot"/>

    <c:choose>
    <c:when test="${slotRendererCombo.view eq 'customer'}">
      <svc-agent:pushCustomerProfile>

        <dspel:setvalue bean="${slotRendererCombo.slot.path}.messageSourcePath" value="/atg/scenario/DSSMessageSource"/>

        <%-- Include the targeting page --%>
        <dspel:include src="/include/slotTargeter.jsp" otherContext="${UIConfig.contextRoot}"/>

      </svc-agent:pushCustomerProfile>
    </c:when>
    <c:otherwise>
      <dspel:setvalue bean="${slotRendererCombo.slot.path}.messageSourcePath" value="/atg/scenario/InternalDSSMessageSource"/>

      <%-- Include the targeting page --%>
      <dspel:include src="/include/slotTargeter.jsp" otherContext="${UIConfig.contextRoot}"/>

    </c:otherwise>
    </c:choose>

  </c:forEach>

</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/slotIterator.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/slotIterator.jsp#1 $$Change: 946917 $--%>
