
<%
/* 
 * This file is used to hold global popup definitions that are rendered only once when main.jsp is first rendered. It is 
 * included in main.jsp through configuration of /atg/svc/agent/ui/AgentUIConfiguration. These popups are global as opposed to 
 * a popup that's included in a panel definition and rendered every time the panel is rendered.
 */
%>


<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:include src="/panels/promotion/promotionsBrowser.jsp" otherContext="${CSRConfigurator.contextRoot}"/>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/globalPopups.jsp#1 $$Change: 946917 $--%>
