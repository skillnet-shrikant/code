<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
      <dsp:importbean var="cctools" bean="/atg/commerce/payment/CreditCardTools" />
        {identifier:"abbreviation",
        items: [
          <c:forEach var="card" items="${cctools.cardTypesMap}" varStatus="status">
            <c:if test="${status.index > 0}">,</c:if>{name:"<fmt:message key="${fn:toLowerCase(card.key)}"/>",label:"<fmt:message key="${fn:toLowerCase(card.key)}"/>",abbreviation:"${card.key}"}
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/cardData.jsp#1 $$Change: 946917 $--%>
