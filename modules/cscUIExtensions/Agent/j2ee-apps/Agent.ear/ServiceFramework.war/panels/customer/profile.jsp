<%--
 This page defines the Customer Information Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/profile.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:droplet name="/atg/dynamo/droplet/Switch">
    <dspel:param param="mode" name="value"/>
    <dspel:oparam name="edit">
      <dspel:include src="/panels/customer/profile_edit.jsp" otherContext="${UIConfig.contextRoot}"/>
    </dspel:oparam>
    <dspel:oparam name="view">
      <dspel:include src="/panels/customer/profile_view.jsp" otherContext="${UIConfig.contextRoot}"/>
    </dspel:oparam>
  </dspel:droplet>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/profile.jsp#1 $$Change: 946917 $--%>
