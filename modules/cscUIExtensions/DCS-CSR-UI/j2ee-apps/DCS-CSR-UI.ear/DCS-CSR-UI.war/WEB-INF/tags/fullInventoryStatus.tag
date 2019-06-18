<%@ tag language="java" %>

<%@ attribute name="commerceItem" required="false" type="atg.adapter.gsa.GSAItem" %>
<%@ attribute name="commerceItemId" required="false" %>

<%@ variable name-given="fullInventoryStatus"
    variable-class="java.lang.String"
    scope="AT_BEGIN"
    description="A string description of the status" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<c:catch var="exception">
  <dsp:page>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <c:if test="${empty commerceItemId}">
      <dsp:tomap var="ci" value="${commerceItem}"/>
      <c:set var="commerceItemId" value="${ci.id}"/>
    </c:if>
    <dsp:droplet name="InventoryLookup">
      <dsp:param name="itemId" value="${commerceItemId}"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>  
          <dsp:oparam name="1001">
            <fmt:message key="global.product.availabilityStatus.outOfStock"/>
            <c:set var="inventoryStatus" value="outofstock"/>
          </dsp:oparam>
          <dsp:oparam name="1002">
            <fmt:message key="global.product.availabilityStatus.preorder"/>
            <c:set var="inventoryStatus" value="preorder"/>
          </dsp:oparam>
          <dsp:oparam name="1003">
            <fmt:message key="global.product.availabilityStatus.backorder"/>
            <c:set var="inventoryStatus" value="backorder"/>
          </dsp:oparam>  
          <dsp:oparam name="1000">
            <b>
              <dsp:valueof param="inventoryInfo.stockLevel"/>
            </b>
            <fmt:message key="global.product.availabilityStatus.inStock"/>
            <c:set var="inventoryStatus" value="instock"/>
            
            <dsp:droplet name="IsNull">
              <dsp:param name="value" param="inventoryInfo.availableToPromise"/>
              <dsp:oparam name="false">
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="inventoryInfo.availableToPromise.availabilityDates" />
                  <dsp:param name="elementName" value="atp" />
                  <dsp:oparam name="output">
                    <br />+
                    <dsp:valueof param="atp.quantity">0</dsp:valueof>
                     on <dsp:valueof param="atp.date" date="MM/dd/yy">No Date</dsp:valueof>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/fullInventoryStatus.tag#1 $$Change: 946917 $--%>
