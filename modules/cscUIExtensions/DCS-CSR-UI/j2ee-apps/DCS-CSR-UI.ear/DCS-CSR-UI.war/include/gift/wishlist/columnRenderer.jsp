<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/wishlist/columnRenderer.jsp#2 $$Change: 953229 $
@updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $

--%>
<%@ include file="/include/top.jspf"%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler" />
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup" />
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
    <dsp:getvalueof var="cartShareableTypeId" bean="/atg/commerce/custsvc/util/CSRConfigurator.cartShareableTypeId"/>
    <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
    <dsp:getvalueof var="field" param="field" />
    <dsp:getvalueof var="giftlistId" param="giftlistId" />
    <dsp:getvalueof var="giftItem" param="giftItem" />
    <dsp:getvalueof var="giftSku" param="giftSku" />
    <dsp:getvalueof var="resourceBundle" param="resourceBundle" />
    <dsp:getvalueof var="resourceKey" param="resourceKey" />  
    <dsp:getvalueof var="isHeading" param="isHeading" />
    <dsp:getvalueof var="isEdit" param="isEdit" />    
    <dsp:getvalueof var="giftlistItemId" param="giftItem.id" />
    <dsp:getvalueof var="isOrderModifiableDisableAttribute" param="isOrderModifiableDisableAttribute" />
    <c:if test="${empty isHeading}">
      <c:set var="isHeading" value="false" />
    </c:if>  
    <c:if test="${empty isEdit}">
      <c:set var="isEdit" value="false" />
    </c:if>  

    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <c:choose>
      <c:when test="${field=='site' and isHeading=='true'}">
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

      <c:when test="${field=='site' and isHeading=='false'}">
        <c:if test="${isMultiSiteEnabled == true}">
          <dsp:getvalueof var="siteId" param="giftItem.siteId" />
          <csr:siteIcon siteId="${siteId}" />
        </c:if>
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
              <svc-ui:frameworkPopupUrl var="shippingCommerceItemPopup"
              value="/include/order/product/productReadOnly.jsp"
              context="${CSRConfigurator.contextRoot}"
              windowId="${windowId}" productId="${giftProductId}" /> <a
              title="<fmt:message key='cart.items.quickView'/>"
              href="#" onclick="javascript:atg.commerce.csr.common.showPopupWithReturn({popupPaneId:'editLineItemPopup',title:'${fn:escapeXml(giftItemDisplayName)}',url:'${shippingCommerceItemPopup}',onClose:function( args ){  }} )"><c:out
              value="${giftDisplayName}" /></a>
            </dsp:layeredBundle>
          </li>
          <li>
            <dsp:valueof param="giftItem.catalogRefId" />
          </li>
        </ul> 
      </c:when>

      <c:when test="${field=='status' and isHeading=='true' and isEdit=='true'}">
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

      <c:when test="${field=='status' and isHeading=='false' and isEdit=='true'}">
        <dsp:droplet name="SKULookup">
          <dsp:param name="id" param="giftItem.catalogRefId" />
          <dsp:setvalue param="giftSku" paramvalue="element" />
            <dsp:oparam name="output">       
              <dsp:tomap var="skumap" param="giftSku"/>
              <csr:inventoryStatus commerceItemId="${skumap.id}"/>   
            </dsp:oparam>
        </dsp:droplet>  
      </c:when>
      
      <c:when test="${field=='price' and isHeading=='true' and isEdit=='true'}">
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
        
      <c:when test="${field=='addToCart' and isHeading=='true' and isEdit=='true'}">
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

      <c:when test="${field=='addToCart' and isHeading=='false' and isEdit=='true'}">
			<c:choose>
			 <c:when test ="${envTools.siteAccessControlOn == 'true' }">
			   <!-- Site Access Controls to only allow items to be added from sites the agent has access to -->
			   <dsp:getvalueof var="siteId" param="giftItem.siteId"/>
			   <dsp:droplet name="IsSiteAccessibleDroplet">
			     <dsp:param name="siteId" value="${siteId}"/>
		       <dsp:oparam name="true">
		         <c:if test="${isMultiSiteEnabled == true}">
		          <dsp:getvalueof var="giftSiteId" param="giftItem.siteId" />
		          <dsp:droplet
		            name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
		            <dsp:param name="siteId" value="${currentSiteId}" />
		            <dsp:param name="otherSiteId" value="${giftSiteId}" />
		            <dsp:param name="shareableTypeId" value="${cartShareableTypeId}" />
		            <dsp:oparam name="true">
		              <dsp:getvalueof var="giftItemId" param="giftItem.id"/>
		              <dsp:getvalueof var="productId" param="giftItem.productId" />
		              <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
		              <dsp:getvalueof var="siteId" param="giftItem.siteId" />
		              <c:set var="giftItemId" value="${fn:escapeXml(giftItemId)}"/>
		              <c:set var="productId" value="${fn:escapeXml(productId)}"/>
		              <c:set var="catalogRefId" value="${fn:escapeXml(catalogRefId)}"/>
		              <c:set var="siteId" value="${fn:escapeXml(siteId)}"/>
		              <fmt:message var="addToOrderMsg" key="giftlists.wishlist.addToCart.message" />
		              <c:set var="formId" value="atg_commerce_csr_customer_gift_addItemFromWishlistToCart"/>
		                <input type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key='giftlists.wishlist.addToCart.label'/>" onclick="atg.commerce.csr.order.gift.addWishlistItemToOrder('${formId}', '${catalogRefId}','${productId}','${fn:escapeXml(giftlistId)}' , '${giftItemId}' , '${addToOrderMsg}' , '${siteId}');"/>
		            </dsp:oparam>
		            <dsp:oparam name="false">
		              <dsp:getvalueof var="siteId" param="giftItem.siteId" /> 
		              <c:set var="siteId" value="${fn:escapeXml(siteId)}"/>
		              <input type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key="giftlists.wishlist.ChangeSite.label"/>" onclick="atg.commerce.csr.order.gift.changeSiteContext('${siteId}','atg_commerce_csr_customerGiftlistChangeSiteForm');" />
		            </dsp:oparam>
		          </dsp:droplet>
		        </c:if>
		       </dsp:oparam>
		       <dsp:oparam name="false">
		         &nbsp;
		       </dsp:oparam>
		     </dsp:droplet>
		   </c:when>
		   <c:otherwise>
		     <c:if test="${isMultiSiteEnabled == true}">
          <dsp:getvalueof var="giftSiteId" param="giftItem.siteId" />
          <dsp:droplet
            name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
            <dsp:param name="siteId" value="${currentSiteId}" />
            <dsp:param name="otherSiteId" value="${giftSiteId}" />
            <dsp:param name="shareableTypeId" value="${cartShareableTypeId}" />
            <dsp:oparam name="true">
              <dsp:getvalueof var="giftItemId" param="giftItem.id"/>
              <dsp:getvalueof var="productId" param="giftItem.productId" />
              <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
              <dsp:getvalueof var="siteId" param="giftItem.siteId" />
              <c:set var="giftItemId" value="${fn:escapeXml(giftItemId)}"/>
              <c:set var="productId" value="${fn:escapeXml(productId)}"/>
              <c:set var="catalogRefId" value="${fn:escapeXml(catalogRefId)}"/>
              <c:set var="siteId" value="${fn:escapeXml(siteId)}"/>
              <fmt:message var="addToOrderMsg" key="giftlists.wishlist.addToCart.message" />
              <c:set var="formId" value="atg_commerce_csr_customer_gift_addItemFromWishlistToCart"/>
                <input type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key='giftlists.wishlist.addToCart.label'/>" onclick="atg.commerce.csr.order.gift.addWishlistItemToOrder('${formId}', '${catalogRefId}','${productId}','${fn:escapeXml(giftlistId)}' , '${giftItemId}' , '${addToOrderMsg}' , '${siteId}');"/>
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:getvalueof var="siteId" param="giftItem.siteId" /> 
              <c:set var="siteId" value="${fn:escapeXml(siteId)}"/>
              <input type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key="giftlists.wishlist.ChangeSite.label"/>" onclick="atg.commerce.csr.order.gift.changeSiteContext('${siteId}','atg_commerce_csr_customerGiftlistChangeSiteForm');" />
            </dsp:oparam>
          </dsp:droplet>
        </c:if>
      </c:otherwise>
    </c:choose>
      
        <c:if test="${isMultiSiteEnabled == false}">
    
          <dsp:getvalueof var="giftItemId" param="giftItem.id"/>
          <dsp:getvalueof var="productId" param="giftItem.productId" />
          <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
          <c:set var="giftItemId" value="${fn:escapeXml(giftItemId)}"/>
          <c:set var="productId" value="${fn:escapeXml(productId)}"/>
          <c:set var="catalogRefId" value="${fn:escapeXml(catalogRefId)}"/>
          <%-- Form To Add product to cart --%>
          <svc-ui:frameworkUrl var="successURL" panelStacks="customerPanels,globalPanels" contentHeader="true"/>
          <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
          <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1"/>
          <dsp:form name="atg_commerce_csr_customer_gift_addItemFromWishlistToCart" action="#" formid="atg_commerce_csr_customer_gift_addItemFromWishlistToCart" id="atg_commerce_csr_customer_gift_addItemFromWishlistToCart">
            <input name="atg.successMessage" type="hidden" value=""/>
            <dsp:input bean="CartModifierFormHandler.addItemCount" name="addItemCount" type="hidden" value="1" />
            <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="hidden" value="" priority="-100" />
            <dsp:input bean="CartModifierFormHandler.items[0].quantity" name="quantity"  type="hidden" value="1" id="${catalogRefId}"/>
            <dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" name="catalogRefId" id="catalogRefId" type="hidden" value="" />
            <dsp:input bean="CartModifierFormHandler.items[0].productId" name="productId" id="productId" type="hidden" value=""/>
            <dsp:input name="successURL" bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="${successURL}"/>
          </dsp:form>
          <%-- End Form --%>      
          <fmt:message var="addToOrderMsg" key="giftlists.wishlist.addToCart.message" />
          <c:set var="formId" value="atg_commerce_csr_customer_gift_addItemFromWishlistToCart"/>
          <input type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> name="submit" value="<fmt:message key='giftlists.wishlist.addToCart.label'/>" onclick="atg.commerce.csr.order.gift.addWishlistItemToOrder('${formId}', '${catalogRefId}','${productId}', '${giftlistId}' , '${giftItemId}' , '${addToOrderMsg}');"/>
        </c:if>
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

      <c:when test="${field=='removeItem' and isHeading=='false' and isEdit=='true'}">
        <dsp:form style="display:none" action="#" id="atg_commerce_csr_removeItemFromGiftlist" formid="atg_commerce_csr_removeItemFromGiftlist">
          <input name="atg.successMessage" type="hidden" value=""/>
          <dsp:input type="hidden" name="giftItemId" bean="CSRGiftlistFormHandler.giftItemId"/>
          <dsp:input type="hidden" name="giftlistId" bean="CSRGiftlistFormHandler.giftlistId"/>
          <dsp:input type="hidden" bean="CSRGiftlistFormHandler.removeItemFromGiftlist" priority="-10" value="" />
        </dsp:form>
        <a href="#" class="atg_commerce_csr_propertyClear"
          onclick="atg.commerce.csr.order.gift.removeItemFromGiftlist('atg_commerce_csr_removeItemFromGiftlist','${giftlistItemId}','${giftlistId}');"></a>
      </c:when>
    </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/wishlist/columnRenderer.jsp#2 $$Change: 953229 $--%>