<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

      <dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
    
{identifier:"abbreviation",
items: [
      <dsp:getvalueof var="defaultStartYear" bean="CurrentDate.year"/>
      <c:set var="defaultEndYear" value="2100"/>
    
{name:"<fmt:message key="common.year.title"/>",label:"<fmt:message key="common.year.title"/>",abbreviation:""}
    
      <c:forEach var="year" begin="${(!empty startYear) ? startYear : defaultStartYear}" end="${(!empty endYear) ? endYear : defaultEndYear}">
,{name:"${year}",label:"${year}",abbreviation:"${year}"}
      </c:forEach>
]}
    
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/yearData.jsp#1 $$Change: 946917 $--%>
