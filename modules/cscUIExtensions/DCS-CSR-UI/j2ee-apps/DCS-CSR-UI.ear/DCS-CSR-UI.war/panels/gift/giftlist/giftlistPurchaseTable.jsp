<%--
 Initializes the gift list results table using the following input parameters:
 tableConfig - The table configuration component
 giftlistId - The Id of the gift list to be viewed

  
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlist/giftlistPurchaseTable.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/collections/filter/droplet/GiftlistSiteFilterDroplet"/>
    <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup" />
    <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="formHandler" />
    <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
    <dsp:importbean bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistDetailsPurchaseDefault" var="giftlistDetailsPurchaseDefault" />
    <dsp:importbean bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistDetailsPurchaseExtended" var="giftlistDetailsPurchaseExtended" />
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/svc/agent/ui/AgentUIConfiguration" var="AgentUIConfiguration"/>
    <dsp:getvalueof var="giftlistId" bean="/atg/commerce/custsvc/gifts/GiftlistUIState.purchaseGiftlistId" />
    <dsp:getvalueof var="cartShareableTypeId" bean="/atg/commerce/custsvc/util/CSRConfigurator.cartShareableTypeId"/>
    <dsp:getvalueof var="tableConfig" param="tableConfig" scope="request" />
    <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
      <dsp:param name="id" bean="/atg/commerce/custsvc/gifts/GiftlistUIState.purchaseGiftlistId" />
      <dsp:oparam name="output">
        <c:set var="ValidGiftlistId" value="true"/>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <c:set var="ValidGiftlistId" value="false"/>
      </dsp:oparam>
    </dsp:droplet>
    <%-- Start Giftlist lookup --%>
    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
      <c:if test="${not empty giftlistId and ValidGiftlistId}">
        <c:set var="giftlistId" value="${fn:escapeXml(giftlistId)}"/>
        <c:if test="${not empty giftlistDetailsPurchaseDefault.URL}">
          <dsp:include src="${giftlistDetailsPurchaseDefault.URL}"
            otherContext="${giftlistDetailsPurchaseDefault.servletContext}">
            <dsp:param name="giftlistId" value="${giftlistId}" />
          </dsp:include>
        </c:if>
        <c:if test="${not empty giftlistDetailsPurchaseExtended.URL}">
          <dsp:include src="${giftlistDetailsPurchaseExtended.URL}"
            otherContext="${giftlistDetailsPurchaseExtended.servletContext}">
            <dsp:param name="giftlistId" value="${giftlistId}" />
          </dsp:include>
        </c:if>
        <dsp:getvalueof var="giftItemId" param="giftItem.id"/>
        <dsp:getvalueof var="siteId" param="giftItem.siteId" />
        <dsp:getvalueof var="productId" param="giftItem.productId" />
        <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
        <dsp:getvalueof var="siteId" param="giftItem.siteId" />        
        <%-- Form To Add product to cart --%>
        <dsp:form name="atg_commerce_csr_customer_gift_addItemsFromGiftlistToCart" action="#" formid="atg_commerce_csr_customer_gift_addItemsFromGiftlistToCart" id="atg_commerce_csr_customer_gift_addItemsFromGiftlistToCart">
        <table class="atg_dataTable atg_commerce_csr_giftlistCart" summary="Summary" cellspacing="0"
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
                </dsp:include></th>
              </c:if>
            </c:forEach>
          </thead>
         
          <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
            <dsp:param name="id" value="${giftlistId}" />
            <dsp:oparam name="output">
              <dsp:setvalue paramvalue="element" param="giftlist" />
              <dsp:setvalue paramvalue="giftlist.giftlistItems" param="items" />
              <dsp:setvalue paramvalue="giftlist.id" param="giftlistId" />
              <dsp:getvalueof var="items" vartype="java.lang.Object" param="items" />
              <%-- Filter the giftlist items --%>
              <c:if test="${isMultiSiteEnabled == true}">
                <dsp:droplet name="GiftlistSiteFilterDroplet">
                  <dsp:param name="collection" value="${items}"/>
                  <dsp:param name="siteScope" value="all" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="items" vartype="java.lang.Object" param="filteredCollection" />
                  </dsp:oparam>
                </dsp:droplet>
              </c:if>
              <%-- If no products to display show message --%>
              <c:if test="${empty items}">
                <fmt:message key="giftlists.giftlist.noGifts" />
              </c:if>
              <%-- If products exist in the list display them --%>
              <c:if test="${not empty items}">
                <c:set var="itemsArraySize" value="${fn:length(items)}"/>
                <c:forEach var="giftItem" items="${items}" varStatus="rowCounter">
                  <dsp:param name="giftItem" value="${giftItem}" />
                  <dsp:droplet name="CSRProductLookup">
                    <dsp:param name="id" param="giftItem.productId" />
                    <dsp:setvalue param="product" paramvalue="element" />
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
                                <dsp:param name="formHandler" value="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
                                <dsp:param name="rowCounter"
                                  value="${rowCounter}" />
                                <dsp:param name="field"
                                  value="${column.field}" />
                                <dsp:param name="giftlistId"
                                  value="${giftlistId}" />
                                <dsp:param name="giftItem"
                                  value="${giftItem}" />
                                <dsp:param name="giftSku"
                                  value="${giftSku}" />
                              </dsp:include>
                            </c:if></td>
                          </c:if>
                        </c:forEach>
                      </tr>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:forEach>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </table>
          <input name="atg.successMessage" type="hidden" value=""/>
          <dsp:input type="hidden" value="${itemsArraySize}" priority="100" bean="/atg/commerce/custsvc/order/CartModifierFormHandler.addItemCount"/>
          <dsp:input bean="/atg/commerce/custsvc/order/CartModifierFormHandler.addItemToOrder" type="hidden" value="" priority="-100" />
        </dsp:form>
      <fmt:message var="productDisplayName" key="giftlists.giftlist.addToCart.message"><fmt:param value="${product.displayName}"/></fmt:message>
      <c:set var="productDisplayName" value="${fn:escapeXml(productDisplayName)}"/>  
      <c:set var="formId" value="atg_commerce_csr_customer_gift_addItemsFromGiftlistToCart"/>
      <c:set var="isOrderModifiableDisableAttribute" value=" disabled='disabled'"/>
      <c:if test="${!empty shoppingCart.originalOrder}">
        <dsp:droplet name="OrderIsModifiable">
          <dsp:param name="order" value="${shoppingCart.originalOrder}"/>
          <dsp:oparam name="true">
            <c:set var="isOrderModifiableDisableAttribute" value=""/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <div class="atg_commerce_csr_panelFooter atg_commerce_csr_panelGridFooter" >
        <c:if test="${isMultiSiteEnabled == true}">
          <dsp:droplet name="GiftlistSiteFilterDroplet">
            <dsp:param name="collection"  value="${items}"/>
            <dsp:param name="siteScope" value="${cartShareableTypeId}" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="giftItems" param="filteredCollection" />
              <dsp:getvalueof var="size" idtype="int" value="${fn:length(giftItems)}"/>
              <c:if test="${size > 0}">  
                <input id="addToCartButton" type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key='giftlists.giftlist.addToCart.label'/>" onclick="atg.commerce.csr.order.gift.addGiftlistItemsToOrder('${formId}','${giftlistId}', '${productDisplayName}');"/>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </c:if>
        <c:if test="${isMultiSiteEnabled == false}">
          <input id="addToCartButton" type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key='giftlists.giftlist.addToCart.label'/>" onclick="atg.commerce.csr.order.gift.addGiftlistItemsToOrder('${formId}','${giftlistId}', '${productDisplayName}');"/>
        </c:if>
      </div>
      </c:if>
      <c:if test="${empty giftlistId}">
        <fmt:message key='giftlists.giftlist.noGiftlistSelected'/>
      </c:if>
    </dsp:layeredBundle>
  <script type="text/javascript">
    atg.progress.update('cmcGiftlistSearchPS');
    _container_.onLoadDeferred.addCallback(function () {
      atg.keyboard.registerFormDefaultEnterKey("atg_commerce_csr_customer_gift_addItemsFromGiftlistToCart","addToCartButton");
    });
    _container_.onUnloadDeferred.addCallback(function () {
      atg.keyboard.unRegisterFormDefaultEnterKey("atg_commerce_csr_customer_gift_addItemsFromGiftlistToCart");
    });
  </script>
  </dsp:page>
  
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlist/giftlistPurchaseTable.jsp#1 $$Change: 946917 $--%>
