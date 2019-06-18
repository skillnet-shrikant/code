<%--
 This page defines the complete order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayReturnItems.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>
<dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>

<dsp:page xml="true">  
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
  <dsp:getvalueof var="returnObject" param="returnObject"/>
  <c:if test="${returnObject.returnProcess}">
    <b><fmt:message key="confirmReturn.emails.returnFollowingItems"/><b>
      <table cellspacing=2 cellpadding=0 border=0>
          <tr>
            <c:if test="${isMultiSiteEnabled == true}">
              <td></td>
            </c:if>
            <td><b><fmt:message key="confirmReturn.emails.returnItemSummary.table.itemDescription"/></b></td>
            <td>&nbsp;&nbsp;</td>
            <td><b><fmt:message key="confirmReturn.emails.returnItemSummary.table.quantity"/></b></td>
          </tr>
          <tr><td colspan=3><hr size=0/></td></tr>
          <c:forEach var="returnItem" items="${returnObject.returnItemList}" varStatus="rowCounter">
            <tr>
              <c:if test="${isMultiSiteEnabled == true}">
                <c:set var="siteId" value="${returnItem.commerceItem.auxiliaryData.siteId}"/>
                <td style="width:16px">
                  <dsp:droplet name="GetSiteDroplet">
                    <dsp:param name="siteId" value="${siteId}"/>
  
                    <dsp:oparam name="output">
                      <dsp:getvalueof param="site" var="site"/>
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
              
              <td><dsp:tomap var="catalogRef" value="${returnItem.commerceItem.auxiliaryData.catalogRef}"/>
                ${fn:escapeXml(catalogRef.displayName)}<br/>
                <c:out value="${returnItem.commerceItem.catalogRefId}"/>
              </td>
              <td>&nbsp;&nbsp;</td>
              <td><web-ui:formatNumber value="${returnItem.quantityToReturn}"/></td>
            </tr>
          </c:forEach>
          <tr><td colspan=3><hr size=0/></td></tr>
      </table>
  </c:if>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayReturnItems.jsp#1 $$Change: 946917 $--%>
