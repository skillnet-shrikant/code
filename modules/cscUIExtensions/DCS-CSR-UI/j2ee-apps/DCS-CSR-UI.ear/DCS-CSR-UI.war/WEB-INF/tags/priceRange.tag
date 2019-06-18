<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%@ attribute name="productId" required="true" type="java.lang.String" %>
<%@ attribute name="priceList" required="false" type="atg.adapter.gsa.GSAItem" %>
<%@ attribute name="salePriceList" required="false" type="atg.adapter.gsa.GSAItem" %>
<%@ attribute name="rangeSeparator " required="false" type="java.lang.String" %>


<%@ attribute name="lowPrice" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="lowPrice"
    variable-class="java.lang.Double"
    alias="lowRange" scope="AT_END" %>

<%@ attribute name="highPrice" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="highPrice"
    variable-class="java.lang.Double"
    alias="highRange" scope="AT_END" %>



<c:catch var="exception">
  <dsp:page>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean var="cat"
        bean="/atg/commerce/custsvc/util/CSRAgentTools" />
      <dsp:droplet name="/atg/commerce/pricing/PriceRangeDroplet">
        <dsp:param name="productId" value="${productId}"/>
        <dsp:param name="priceList" value="${priceList}"/>
        <dsp:param name="salePriceList" value="${salePriceList}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof param="lowestPrice" var="lowRange"/>
          <dsp:getvalueof param="highestPrice" var="highRange"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/priceRange.tag#1 $$Change: 946917 $--%>
