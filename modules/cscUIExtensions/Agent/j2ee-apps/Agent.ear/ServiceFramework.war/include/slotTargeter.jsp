<%--

Performs slot targeting

@version $$
@updated $$

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

  <%-- Iterate over first N items found by current slot --%>
  <dspel:droplet name="/atg/targeting/TargetingArray">

    <%-- Specify slot by nucleus path --%>
    <dspel:param name="targeter" bean="${slotRendererCombo.slot.path}"/>

    <dspel:oparam name="output"> 
      <dspel:getvalueof var="elementsArray" param="elements"/>
      <svc-agent:truncateArray var="truncatedElements" 
                               array="${elementsArray}" 
                               size="${slotRendererCombo.maxItems}"/>

      <%-- Include the renderer page --%>
      <dspel:include src="${slotRendererCombo.renderer.path}" otherContext="">
        <dspel:param name="targetingResults" value="${truncatedElements}"/>
      </dspel:include>

    </dspel:oparam>
    <dspel:oparam name="empty">
<%--
      <c:out value="No results in slot ${slotRendererCombo.slot.path}"/><br/>
--%>
    </dspel:oparam>
  </dspel:droplet>

</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/slotTargeter.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/slotTargeter.jsp#1 $$Change: 946917 $--%>
