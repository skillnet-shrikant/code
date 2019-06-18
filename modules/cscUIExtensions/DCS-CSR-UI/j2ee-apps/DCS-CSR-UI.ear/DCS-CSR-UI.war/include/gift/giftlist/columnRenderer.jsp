<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/columnRenderer.jsp#2 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $

--%>
<%@ include file="/include/top.jspf"%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler" />
  <dsp:importbean bean="/atg/commerce/catalog/AllCatalogSKULookup" />
  <dsp:importbean bean="/atg/commerce/custsvc/gifts/GetGiftItemQuantityDroplet" />
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <dsp:getvalueof var="formHandler" param="formHandler" />
    <dsp:getvalueof var="rowCounter" param="rowCounter"/>
    <dsp:getvalueof var="field" param="field" />
    <dsp:getvalueof var="giftlistId" param="giftlistId" />
    <dsp:getvalueof var="giftItem" param="giftItem" />
    <dsp:getvalueof var="giftSku" param="giftSku" />
    <dsp:getvalueof var="resourceBundle" param="resourceBundle" />
    <dsp:getvalueof var="resourceKey" param="resourceKey" />
    <dsp:getvalueof var="isHeading" param="isHeading" />
    <dsp:getvalueof var="isEdit" param="isEdit" />
    <c:if test="${empty isHeading}">
      <c:set var="isHeading" value="false" />
    </c:if>
    <c:if test="${empty isEdit}">
      <c:set var="isEdit" value="false" />
    </c:if>
    <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
    <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
    <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />
    <c:choose>
      <c:when test="${field=='site' and isHeading=='true' and isMultiSiteEnabled=='true' }">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='site' and isHeading=='false' and isMultiSiteEnabled=='true'}">
        <dsp:getvalueof var="siteId" param="giftItem.siteId" />
        <csr:siteIcon siteId="${siteId}" />
      </c:when>

      <c:when test="${field=='itemImage' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='itemImage' and isHeading=='false'}">
       <dsp:getvalueof var="productId" param="giftItem.productId" />
        <dsp:droplet name="/atg/commerce/custsvc/catalog/CSRProductLookup">
          <dsp:param name="id" value="${productId}" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="productItem" param="element" />
            <dsp:getvalueof var="productId" param="item.productId" />
            <dsp:tomap var="productMap" value="${productItem}" />
            <dsp:tomap var="smallImage" value="${productMap.smallImage}" />
          </dsp:oparam>
        </dsp:droplet>
        <c:choose>
          <c:when test="${!empty smallImage.url}">
            <img src="${smallImage.url}" id="atg_commerce_csr_gift_product_info_image" iclass="atg_commerce_csr_gift_ProductViewImg" width="60" height="60" border="0" />
          </c:when>
          <c:otherwise>
            <c:url context='/agent' value='/images/icon_confirmationLarge.gif' var="defaultImageURL"/>
            <img src="${defaultImageURL}" id="atg_commerce_csr_gift_product_info_image" iclass="atg_commerce_csr_gift_ProductViewImg" width="60" height="60" border="0" />
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='itemDescription' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='itemDescription' and isHeading=='false'}">
        <dsp:getvalueof var="giftDisplayName" param="giftItem.displayName" />
        <dsp:getvalueof var="giftProductId" param="giftItem.productId" />
        <c:set var="giftItemDisplayName" value="${fn:escapeXml(giftDisplayName)}"/> 
        <script type="text/javascript">
          if (!dijit.byId("editLineItemPopup")) {
              new dojox.Dialog( {
                id :"editLineItemPopup",
                cacheContent :"false",
                executeScripts :"true",
                scriptHasHooks :"true",
                duration :100,
                "class" :"atg_commerce_csr_popup"
              });
          }
        </script>
        <ul class="atg_commerce_csr_itemDesc">
          <li>
            <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
              <svc-ui:frameworkPopupUrl
              var="shippingCommerceItemPopup"
              value="/include/order/product/productReadOnly.jsp"
              context="${CSRConfigurator.contextRoot}"
              windowId="${windowId}" productId="${giftProductId}" /> <a
              title="<fmt:message key='cart.items.quickView'/>"
              href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({popupPaneId:'editLineItemPopup',title:'${fn:escapeXml(giftItemDisplayName)}',url:'${shippingCommerceItemPopup}',onClose:function( args ){  }} )"><c:out
              value="${giftDisplayName}" /></a>
            </dsp:layeredBundle>
          </li>
          <li><dsp:valueof param="giftItem.catalogRefId" /></li>
        </ul>
      </c:when>

      <c:when
        test="${field=='status' and isHeading=='true' and isEdit=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='status' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when
        test="${field=='status' and isHeading=='false' and isEdit=='true'}">
        <dsp:droplet name="AllCatalogSKULookup">
          <dsp:param name="id" param="giftItem.catalogRefId" />
          <dsp:setvalue param="giftSku" paramvalue="element" />
          <dsp:oparam name="output">
            <dsp:tomap var="skumap" param="giftSku" />
            <csr:inventoryStatus commerceItemId="${skumap.id}" />
          </dsp:oparam>
        </dsp:droplet>
      </c:when>


      <c:when test="${field=='status' and isHeading=='false'}">
        <dsp:droplet name="AllCatalogSKULookup">
          <dsp:param name="id" param="giftItem.catalogRefId" />
          <dsp:setvalue param="giftSku" paramvalue="element" />
          <dsp:oparam name="output">
            <dsp:tomap var="skumap" param="giftSku" />
            <csr:inventoryStatus commerceItemId="${skumap.id}" />
          </dsp:oparam>
        </dsp:droplet>
        <%-- SKU Lookup --%>
      </c:when>

      <c:when
        test="${field=='price' and isHeading=='true' and isEdit=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
      
      <c:when test="${field=='price' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='price' and isHeading=='false' and isEdit=='true'}">
        <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId"/>
        <dsp:getvalueof var="productId" param="giftItem.productId"/>
        <dsp:include src="/include/catalog/displaySkuPrice.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="product" value="${productId}"/>
          <dsp:param name="sku" value="${catalogRefId}"/>
        </dsp:include>   
      </c:when>


      <c:when test="${field=='price' and isHeading=='false'}">
        <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId"/>
        <dsp:getvalueof var="productId" param="giftItem.productId"/>
        <dsp:include src="/include/catalog/displaySkuPrice.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="product" value="${productId}"/>
          <dsp:param name="sku" value="${catalogRefId}"/>
        </dsp:include>   
      </c:when>

      <c:when test="${field=='desired' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='desired' and isHeading=='false' and isEdit=='false'}">
        <dsp:droplet name="GetGiftItemQuantityDroplet">
          <dsp:param name="giftItem" value="${giftItem}" />
          <dsp:param name="quantityType" value="desired" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="quantityDesired" param="quantity" />
            <web-ui:formatNumber value="${quantityDesired}"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:when>


      <c:when
        test="${field=='desired' and isHeading=='false' and isEdit=='true'}">
        <dsp:droplet name="GetGiftItemQuantityDroplet">
          <dsp:param name="giftItem" value="${giftItem}" />
          <dsp:param name="quantityType" value="desired" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="quantityDesired" param="quantity" />
          </dsp:oparam>
        </dsp:droplet>
        <dsp:getvalueof var="giftItemId" param="giftItem.id"/>

        <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
          <dsp:param name="product" value="${giftItem.productId}"/>
          <dsp:param name="sku" value="${giftItem.catalogRefId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="isFractional" param="fractional"/>
          </dsp:oparam>
        </dsp:droplet>

        <c:choose>
          <c:when test="${isFractional == true}">
            <input type="text" value="${quantityDesired}" id="${giftItemId}" size="5" maxlength="9" class="quantity-input"
                   dojoType="dijit.form.NumberTextBox"
                   constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                   invalidMessage="${fractionalValidationMessage}" />
          </c:when>
          <c:otherwise>
            <input type="text" value="${quantityDesired}" id="${giftItemId}" size="5" maxlength="5" class="quantity-input"
                   dojoType="dijit.form.NumberTextBox"
                   constraints="{places:0, pattern: '#####'}"
                   invalidMessage="Please enter a valid value."/>
          </c:otherwise>
        </c:choose>

      </c:when>      
      
      <c:when test="${field=='purchased' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='purchased' and isHeading=='false'}">
        <dsp:droplet name="GetGiftItemQuantityDroplet">
          <dsp:param name="giftItem" value="${giftItem}" />
          <dsp:param name="quantityType" value="purchased" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="quantityPurchased" param="quantity" />
            <web-ui:formatNumber value="${quantityPurchased}"/>
          </dsp:oparam>
        </dsp:droplet>

      </c:when>

      <c:when test="${field=='removeItem' and isHeading=='true' and isEdit=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when
        test="${field=='removeItem' and isHeading=='false' and isEdit=='true'}">
        <dsp:getvalueof var="giftItemId" param="giftItem.id"/>
        <dsp:form style="display:none" action="#" id="atg_commerce_csr_removeItemFromGiftlist" formid="atg_commerce_csr_removeItemFromGiftlist">
          <input name="atg.successMessage" type="hidden" value=""/>
          <dsp:input type="hidden" name="giftItemId" bean="CSRGiftlistFormHandler.giftItemId"/>
          <dsp:input type="hidden" name="giftlistId" bean="CSRGiftlistFormHandler.giftlistId"/>
          <dsp:input type="hidden" bean="CSRGiftlistFormHandler.removeItemFromGiftlist" priority="-10" value="" />
        </dsp:form>
        <a href="#" class="atg_commerce_csr_propertyClear"
          onclick="atg.commerce.csr.order.gift.removeItemFromGiftlist('atg_commerce_csr_removeItemFromGiftlist','${giftItemId}','${giftlistId}');"></a>
      </c:when>
    </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/columnRenderer.jsp#2 $$Change: 1179550 $--%>
