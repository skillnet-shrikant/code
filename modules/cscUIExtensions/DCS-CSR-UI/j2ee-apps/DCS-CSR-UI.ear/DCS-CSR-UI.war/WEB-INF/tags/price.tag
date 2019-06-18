<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags" %>

<%@ attribute name="productId" required="true" type="java.lang.String" %>
<%@ attribute name="priceList" required="false" type="atg.adapter.gsa.GSAItem" %>
<%@ attribute name="rangeSeparator " required="false" type="java.lang.String" %>

<c:catch var="exception">
  <dsp:page>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean var="cat"
        bean="/atg/commerce/custsvc/util/CSRAgentTools" />
     <csr:getCurrencyCode>
      <c:set var="currencyCode" value="${currencyCode}" scope="request" />
     </csr:getCurrencyCode> 
        
      <dsp:droplet name="/atg/commerce/pricing/PriceRangeDroplet">
        <dsp:param name="productId" value="${productId}"/>
        <dsp:param name="priceList" value="${priceList}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof param="lowestPrice" var="lowestPrice"/>
          <dsp:getvalueof param="highestPrice" var="highestPrice"/>
          <c:choose>
            <c:when test="${lowestPrice eq highestPrice and empty lowestPrice}">
              <fmt:message key="catalogBrowse.searchResults.noPrice"/>
            </c:when>
            <c:when test="${lowestPrice eq highestPrice and !empty lowestPrice}">
              <csr:formatNumber param="lowestPrice" type="currency" currencyCode="${currencyCode}"/>
            </c:when>
            <c:otherwise>
              <csr:formatNumber param="lowestPrice" type="currency" currencyCode="${currencyCode}"/>
              ${empty rangeSeparator ? "-" : rangeSeparator}
              <csr:formatNumber param="highestPrice" type="currency" currencyCode="${currencyCode}"/>
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <% 
    Exception ee = (Exception) jspContext.getAttribute("exception"); 
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/price.tag#2 $$Change: 1179550 $--%>
