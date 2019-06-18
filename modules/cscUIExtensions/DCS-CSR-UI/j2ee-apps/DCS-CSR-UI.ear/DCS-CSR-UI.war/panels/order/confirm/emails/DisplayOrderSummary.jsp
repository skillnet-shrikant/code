<%--
 This page defines the order summary

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayOrderSummary.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt" %>

<dsp:page>

<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>
<dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>
<dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="CSRAgentTools"/>
<c:set var="isMultiSiteEnabled" value="${CSRAgentTools.multiSiteEnabled}"/> 

<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<table cellspacing=2 cellpadding=0 border=0>
<tr>
<c:if test="${isMultiSiteEnabled == true}">
  <td></td>
</c:if>
<td><b><fmt:message key="confirmOrder.emails.orderSummary.quantity"/></b></td>
<td></td>
<td>&nbsp;&nbsp;</td>
<td><b><fmt:message key="confirmOrder.emails.orderSummary.product"/>
</b></td>
<td>&nbsp;&nbsp;</td>
<td><b><fmt:message key="confirmOrder.emails.orderSummary.sku"/></b></td>
<td>&nbsp;&nbsp;</td>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td><b><fmt:message key="confirmOrder.emails.orderSummary.isInstock"/></b></td>
    <td>&nbsp;&nbsp;</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right><b><fmt:message key="confirmOrder.emails.orderSummary.listPrice"/></b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b><fmt:message key="confirmOrder.emails.orderSummary.salePrice"/></b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b><fmt:message key="confirmOrder.emails.orderSummary.totalPrice"/></b></td>
</tr>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <tr><td colspan=14><hr size=0></td></tr>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
  </dsp:oparam>
</dsp:droplet>
<dsp:getvalueof var="currencyCode" param="order.priceInfo.currencyCode"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.commerceItems"/>
  <dsp:param name="elementName" value="item"/>
  <dsp:oparam name="output">
    <dsp:tomap var="itemMap" param="item"/>
    <tr valign=top>
      <c:if test="${isMultiSiteEnabled == true}">
        <c:set var="siteId" value="${itemMap.auxiliaryData.siteId}"/>
        <td style="width:16px">
          <dsp:droplet name="GetSiteDroplet">
            <dsp:param name="siteId" value="${siteId}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="siteName" param="site.name"/>
              <c:choose>
                <c:when test="${!empty siteName}">
                  <c:out value="${siteName}" />
                </c:when>
                <c:otherwise>
                  &nbsp;
                </c:otherwise>
              </c:choose>  
            </dsp:oparam>
            
            <dsp:oparam name="empty">
              &nbsp;
            </dsp:oparam>
            
            <dsp:oparam name="error">
              <c:set var="isSiteDeleted" value="true"/>
            </dsp:oparam>
            
          </dsp:droplet>  
        </td>
      </c:if>
      
      <td>
        <dsp:valueof param="item.quantity"><fmt:message key="confirmOrder.emails.orderSummary.noQuantity"/></dsp:valueof>
      </td>
      <td></td>
      <td>&nbsp;&nbsp;</td>
      <td>
        <dsp:valueof param="item.auxiliaryData.productRef.displayName"><fmt:message key="confirmOrder.emails.orderSummary.unknownProduct"/></dsp:valueof>
      </td>
      <td>&nbsp;&nbsp;</td>
      <td>
        <dsp:valueof param="item.auxiliaryData.catalogRef.displayName"><fmt:message key="confirmOrder.emails.orderSummary.unknownCatalog"/></dsp:valueof>
      </td>
      <td>&nbsp;&nbsp;</td>
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="displayStockStatus"/>
        <dsp:oparam name="true">    
          <td>
            <dsp:droplet name="InventoryLookup">
              <dsp:param name="itemId" param="item.catalogRefId"/>
              <dsp:param name="useCache" value="true"/>
              <dsp:oparam name="output">
                <dsp:droplet name="Switch">
                  <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>
                  <dsp:oparam name="1000">
                    <b><fmt:message key="confirmOrder.emails.orderSummary.availabilityYes"/></b>
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <fmt:message key="confirmOrder.emails.orderSummary.availabilityNo"/>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </td>
          <td>&nbsp;&nbsp;</td>
        </dsp:oparam>
      </dsp:droplet>

      <td align=right>
        <csr:formatNumber value="${itemMap.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}"/>
      </td>
      <td>&nbsp;&nbsp;</td>
      <td align=right>
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="item.priceInfo.onSale"/>
          <dsp:oparam name="true">
            <csr:formatNumber value="${itemMap.priceInfo.salePrice}" type="currency" currencyCode="${currencyCode}"/>
          </dsp:oparam>
          <dsp:oparam name="false">
            <fmt:message key="confirmOrder.emails.orderSummary.onSaleFalse"/>
          </dsp:oparam>
        </dsp:droplet>
      </td>
      <td>&nbsp;&nbsp;</td>
      <td align=right>
        <csr:formatNumber value="${itemMap.priceInfo.amount}" type="currency" currencyCode="${currencyCode}"/>
      </td>
    </tr>
    <tr>
      <%
        /*
         * Display breakdown if commerce item is associated with a giftlist
         */
      %>
      <c:set var="customerTotalQuantity" value="${itemMap.quantity}" />
      <c:set var="giftlistTotalQuantity" value="0" />
      <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
        <dsp:param name="commerceItemId" value="${itemMap.id}" />
        <dsp:param name="order" param="order" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="key" param="key" />
          <dsp:getvalueof var="value" param="element" />
          <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
            <dsp:param name="id" param="key" />
            <dsp:oparam name="output">
              <c:set var="customerTotalQuantity" value="${customerTotalQuantity - value }" />
              <c:set var="giftlistTotalQuantity" value="${giftlistTotalQuantity + value }" />
            </dsp:oparam>
          </dsp:droplet><%-- End GiftlistLookupDroplet --%>
        </dsp:oparam>
      </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
      <c:if test="${customerTotalQuantity > '0' && giftlistTotalQuantity > '0'}">
        <tr>
          <td></td>
          <td>
            <c:out value="${customerTotalQuantity}" /><%-- Display Customer Quantity --%>
          </td>
          <td></td>
          <td></td>
          <td>
            <fmt:message key="confirmOrder.emails.orderSummary.currentCustomer"/><%-- Display Current Customer label --%>
          </td>
          </td>
        </tr>
      </c:if>
      <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
        <dsp:param name="commerceItemId" value="${itemMap.id}" />
        <dsp:param name="order" param="order" />
        <dsp:oparam name="output">
          <tr>
            <dsp:getvalueof var="key" param="key" />
            <dsp:getvalueof var="value" param="element" />
            <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
              <dsp:param name="id" param="key" />
              <dsp:oparam name="output">
                <td></td>
                <td class="atg_numberValue">
                  <c:out value="${value}" /><%-- Display Gift Recipient Quantity --%>
                </td>
                <td></td>
                <td></td>
                <td>
                  <dsp:getvalueof var="eventName" vartype="java.lang.String" param="item.eventName" />
                  <dsp:valueof param="item.owner.firstName" />&nbsp;
                  <dsp:valueof param="item.owner.lastName" />, 
                  <c:out value="${eventName}" /><%-- Display Gift Recipient Name and Event Name --%>
                </td>
                <td></td>
              </dsp:oparam>
            </dsp:droplet><%-- End GiftlistLookupDroplet --%>
          </tr>
        </dsp:oparam>
      </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
    </tr>
  </dsp:oparam>

  <dsp:oparam name="empty"><tr colspan=10 valign=top><td><fmt:message key="confirmOrder.emails.orderSummary.noItems"/></td></tr></dsp:oparam>
</dsp:droplet>

<dsp:tomap var="orderMap" param="order"/>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <tr><td colspan=14><hr size=0></td></tr>
    <tr>
      <td colspan=13 align=right><fmt:message key="confirmOrder.emails.orderSummary.subtotal"/></td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
    <tr>
      <td colspan=11 align=right><fmt:message key="confirmOrder.emails.orderSummary.subtotal"/></td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
  <csr:formatNumber value="${orderMap.priceInfo.amount}" type="currency" currencyCode="${currencyCode}"/>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right><fmt:message key="confirmOrder.emails.orderSummary.shipping"/></td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right><fmt:message key="confirmOrder.emails.orderSummary.shipping"/></td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
  <csr:formatNumber value="${orderMap.priceInfo.shipping}" type="currency" currencyCode="${currencyCode}"/>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right><fmt:message key="confirmOrder.emails.orderSummary.tax"/></td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right><fmt:message key="confirmOrder.emails.orderSummary.tax"/></td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
  <csr:formatNumber value="${orderMap.priceInfo.tax}" type="currency" currencyCode="${currencyCode}"/>
</td>
</tr>
        
<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right><b><fmt:message key="confirmOrder.emails.orderSummary.total"/></b></td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right><b><fmt:message key="confirmOrder.emails.orderSummary.total"/></b></td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
  <b><csr:formatNumber value="${orderMap.priceInfo.total}" type="currency" currencyCode="${currencyCode}"/></b>
</td>
</tr>
</table>
<br>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayOrderSummary.jsp#2 $$Change: 1179550 $--%>
