<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%@ attribute name="productId" required="true" type="java.lang.String" %>
<%@ attribute name="priceList" required="false" type="atg.adapter.gsa.GSAItem" %>
<%@ attribute name="max" required="false" %>

<c:catch var="exception">
  <dsp:page>
    <c:if test="${empty max}">
      <c:set var="max" value="3"/>
    </c:if>
    <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
    <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:droplet name="/atg/commerce/catalog/ProductLookup">
        <dsp:param name="repositoryKey" bean="RequestLocale.locale"/>
        <dsp:param name="id" param="productId"/>
        <dsp:oparam name="noCatalog">
          <dsp:getvalueof var="productItem" param="element"/>
        </dsp:oparam>
        <dsp:oparam name="output">
          <dsp:getvalueof var="productItem" param="element"/>
        </dsp:oparam>
      </dsp:droplet>


<!-- find unique set of crossells for list of products -->

      <jsp:useBean id="xsells" type="java.util.Map" class="java.util.HashMap"/>
      <c:forEach items="${order.commerceItems}" var="product" varStatus="vs">
        <dsp:tomap var="product" value="${product.auxiliaryData.productRef}"/>
        <c:forEach items="${product.relatedProducts}" var="relatedProduct">
          <dsp:tomap var="relatedProduct" value="${relatedProduct}"/>
          <c:set target="${xsells}" property="${relatedProduct.productId}" 
            value="${relatedProduct}"/>
        </c:forEach>
      </c:forEach>

      xsells: ${xsells}

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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/crossSells.tag#1 $$Change: 946917 $--%>
