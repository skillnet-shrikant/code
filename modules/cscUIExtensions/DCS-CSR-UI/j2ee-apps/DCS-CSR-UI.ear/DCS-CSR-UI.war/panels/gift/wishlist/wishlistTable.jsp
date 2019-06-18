<%--
 Initializes the wish list results table using the following input parameters:
 tableConfig - the table configuration component
 isEdit - is the table to be viewed in edit mode (true) or not (false)
  
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/wishlist/wishlistTable.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/collections/filter/droplet/GiftlistSiteFilterDroplet"/>
    <dsp:importbean bean="/atg/userprofiling/ServiceCustomerProfile" var="profile" />
    <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
    <dsp:getvalueof var="tableConfig" param="tableConfig" scope="request" />
    <dsp:getvalueof var="isEdit" param="isEdit" scope="request" />
    <%-- Start Wishlist lookup --%>
    <dsp:getvalueof var="wishListId"
      bean="ServiceCustomerProfile.wishlist.id" />
    <dsp:setvalue beanvalue="ServiceCustomerProfile.wishlist"
      param="wishlist" />
    <dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
    <dsp:setvalue paramvalue="wishlist.id" param="giftlistId" />
    <dsp:getvalueof var="items" vartype="java.lang.Object" param="items" />
    <c:if test="${isMultiSiteEnabled == true}">
      <dsp:droplet name="GiftlistSiteFilterDroplet">
        <dsp:param name="collection" value="${items}"/>
        <dsp:param name="siteScope" value="all" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="items" vartype="java.lang.Object" param="filteredCollection" />
        </dsp:oparam>
      </dsp:droplet>
    </c:if>    
    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
      <table class="atg_dataTable" summary="Summary" cellspacing="0"
        cellpadding="0">
        <thead>
          <c:forEach var="column" items="${tableConfig.columns}">
            <c:if test="${column.isVisible == 'true'}">
              <c:set var="columnWidth" value="${column.width}" />
              <c:if test="${empty columnWidth}">
                <c:set var="columnWidth" value="auto" />
              </c:if>
              <th scope="col" style="width:${columnWidth}"><dsp:include
                src="${column.dataRendererPage.URL}"
                otherContext="${column.dataRendererPage.servletContext}">
                <dsp:param name="field" value="${column.field}" />
                <dsp:param name="resourceBundle"
                  value="${column.resourceBundle}" />
                <dsp:param name="resourceKey"
                  value="${column.resourceKey}" />
                <dsp:param name="isHeading" value="true" />
                <dsp:param name="isEdit" value="${isEdit}" />
              </dsp:include></th>
            </c:if>
          </c:forEach>
        </thead>
    <c:if test="${empty items and isEdit=='true'}">
      <c:set var="link">
        <a href="#" onclick="atg.commerce.csr.openPanelStackWithTabbedPanel('cmcCatalogPS','cmcProductCatalogBrowseP','commerceTab');">
      </c:set>
      <fmt:message key="giftlists.noProducts.edit">
        <fmt:param value="${link}"/>
        <fmt:param value="</a>"/>
      </fmt:message>
    </c:if>
    <c:if test="${empty items and isEdit=='false'}">
      <fmt:message key="giftlists.noProducts.view" />
    </c:if>    
    <c:if test="${not empty items}">
      <c:set var="isOrderModifiableDisableAttribute" value=" disabled='disabled'"/>
      <c:if test="${!empty shoppingCart.originalOrder}">
        <dsp:droplet name="OrderIsModifiable">
          <dsp:param name="order" value="${shoppingCart.originalOrder}"/>
          <dsp:oparam name="true">
            <c:set var="isOrderModifiableDisableAttribute" value=""/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
        <c:forEach var="giftItem" items="${items}"
          varStatus="giftItemStatus">

          <dsp:param name="giftItem" value="${giftItem}" />
          <dsp:droplet name="/atg/commerce/custsvc/catalog/CSRProductLookup">
            <dsp:param name="id" param="giftItem.productId" />

            <dsp:setvalue param="product" paramvalue="element" />
            <dsp:oparam name="output">
              <dsp:droplet name="/atg/commerce/custsvc/catalog/SKULookup">
                <dsp:param name="id" param="giftItem.catalogRefId" />
                <dsp:setvalue param="giftSku" paramvalue="element" />
                <dsp:oparam name="output">
                  <tr>
                    <c:forEach var="column"
                      items="${tableConfig.columns}">
                      <c:if test="${column.isVisible == 'true'}">
                        <td><c:if
                          test="${column.dataRendererPage != ''}">
                          <dsp:include
                            src="${column.dataRendererPage.URL}"
                            otherContext="${column.dataRendererPage.servletContext}">
                            <dsp:param name="field"
                              value="${column.field}" />
                            <dsp:param name="giftlistId"
                              value="${wishListId}" />
                            <dsp:param name="giftItem"
                              value="${giftItem}" />
                            <dsp:param name="giftSku" value="${giftSku}" />
                            <dsp:param name="isEdit" value="${isEdit}" />
                            <dsp:param name="isOrderModifiableDisableAttribute" value="${isOrderModifiableDisableAttribute}"/>
                          </dsp:include>
                        </c:if></td>
                      </c:if>
                    </c:forEach>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </c:forEach>
        </c:if>
      </table>
      </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext
          .getAttribute("exception");
      ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/wishlist/wishlistTable.jsp#1 $$Change: 946917 $--%>