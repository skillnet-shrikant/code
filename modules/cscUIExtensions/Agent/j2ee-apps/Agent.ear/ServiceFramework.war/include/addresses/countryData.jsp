<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dspel:page xml="true">
    <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

{identifier:"abbreviation",
items: [
  {name:"<fmt:message key="common.country.canada"/>",label:"<fmt:message key="common.country.canada"/>",abbreviation:"CAN"},
  {name:"<fmt:message key="common.country.usa"/>",label:"<fmt:message key="common.country.usa"/>",abbreviation:"USA"}
]}

    </dspel:layeredBundle>
  </dspel:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/countryData.jsp#1 $$Change: 946917 $--%>
