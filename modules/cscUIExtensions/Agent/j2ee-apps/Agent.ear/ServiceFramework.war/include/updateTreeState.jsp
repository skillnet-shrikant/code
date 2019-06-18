<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/updateTreeState.jsp#1 $$Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
  --%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:getvalueof param="baseTopicIds" var="baseTopicIds"/>
<dspel:getvalueof param="componentName" var="componentName"/>

<web-ui:invoke componentPath="/atg/web/tree/${componentName}" method="setCheckedNodeIdsString">
  <web-ui:parameter value="${baseTopicIds}"/>
</web-ui:invoke>

</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/updateTreeState.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/updateTreeState.jsp#1 $$Change: 946917 $--%>
