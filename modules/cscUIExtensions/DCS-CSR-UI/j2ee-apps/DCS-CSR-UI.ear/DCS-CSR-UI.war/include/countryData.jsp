<%--
 This page defines the country data in JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/countryData.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.commerce.util.CountryStateResources">
      <dsp:getvalueof var="places" bean="/atg/core/i18n/CountryList.places"/>
        <c:out value='{ "identifier": "abbreviation",' escapeXml="false"/>
        <c:out value='"items": [' escapeXml="false"/>
        <c:forEach var="country" items="${places}" varStatus="theCount">
          <c:out value='{' escapeXml="false"/>
          <c:out value='"name": "${country.displayName}",' escapeXml="false"/>
          <c:out value='"label": "${country.displayName}",' escapeXml="false"/>
          <c:out value='"abbreviation": "${country.code}"' escapeXml="false"/>
          <c:out value='}' escapeXml="false"/>
          <c:if test="${!theCount.last}">
            <c:out value=',' escapeXml="false"/>
          </c:if>
        </c:forEach>
        <c:out value=']}' escapeXml="false"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/countryData.jsp#2 $$Change: 1179550 $--%>
