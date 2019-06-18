<%--

Displays Slots panel contents

@version $$
@updated $$

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="panelId" param="panelId"/>
  <div  parseWidgets="false">
  <dspel:include src="/include/slotIterator.jsp" otherContext="${UIConfig.contextRoot}">
    <dspel:param name="panelId" value="${panelId}"/>
  </dspel:include>
  </div>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/slotsPanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/slotsPanel.jsp#1 $$Change: 946917 $--%>
