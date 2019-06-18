<%--
Display the appropriate details for each returned item in the return request.

Expected params
returnItem : A single item in a Return Request.

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
  
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnItemStateDescriptions"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/GetReturnItemQuantityInfoDroplet"/>

  <dsp:getvalueof var="returnItem" param="returnItem"/>      
  
  <tr class="atg_returnedRow"}>    
    <c:if test="${isMultiSiteEnabled == true}">
      <c:set var="siteId" value="${returnItem.commerceItem.auxiliaryData.siteId}"/>
      <td class="atg_commerce_csr_siteIcon">
        <csr:siteIcon siteId="${siteId}" />
      </td>
    </c:if>
    <td>
      <ul class="atg_commerce_csr_itemDesc">
        <li>
          <dsp:tomap var="sku" value="${returnItem.commerceItem.auxiliaryData.catalogRef}"/>
          <dsp:tomap var="product" value="${returnItem.commerceItem.auxiliaryData.productRef}"/>
          <c:out value="${product.displayName}"/>
        </li>
        <li>
          <c:out value="${returnItem.commerceItem.catalogRefId}"/>          
        </li>
      </ul>
    </td>
    <td>
      <dsp:droplet name="ReturnItemStateDescriptions">
        <dsp:param name="state" value="${returnItem.state}"/>
        <dsp:param name="elementName" value="stateDescription"/>
        <dsp:oparam name="output">
          <dsp:valueof param="stateDescription">
            <fmt:message key="common.notApplicable"/>
          </dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </td>    
    <td class="atg_numberValue">
      <dsp:droplet name="GetReturnItemQuantityInfoDroplet">
        <dsp:param name="item" value="${returnItem}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="quantityToReturn" param="quantityToReturn"/>
        </dsp:oparam>
      </dsp:droplet>
      <web-ui:formatNumber value="${0 - quantityToReturn}"/>
    </td>
    <td></td>
    <td></td>
    <td></td>
  </tr>

</dsp:layeredBundle>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderReturnLineItem.jsp#2 $$Change: 1179550 $--%>
