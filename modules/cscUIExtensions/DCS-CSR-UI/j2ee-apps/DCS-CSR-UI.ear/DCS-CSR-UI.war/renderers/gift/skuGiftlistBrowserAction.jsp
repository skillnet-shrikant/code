<%--
 Renders the action elements to add sku items to gift/wish lists


 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/gift/skuGiftlistBrowserAction.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean bean="/atg/userprofiling/ServiceCustomerProfile"/>
      <dsp:importbean bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler" />
      <dsp:importbean bean="/atg/commerce/custsvc/collections/filter/droplet/GiftlistSiteFilterDroplet"/>
        <dsp:importbean var="GiftlistUIState" bean="/atg/commerce/custsvc/gifts/GiftlistUIState" />
      <dsp:getvalueof var="renderInfo" param="renderInfo" />
      <dsp:getvalueof var="productItem" param="product" />
      <dsp:getvalueof var="isProfileTransient" bean="/atg/userprofiling/ServiceCustomerProfile.transient" />
      <dsp:tomap var="product" value="${productItem}"/>
      <c:set var="productId" value="${product.id }"/>    
      <!-- Droplet to allow gift list controls to be enabled and dissabled -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param
          bean="/atg/commerce/custsvc/util/CSRConfigurator.usingGiftlists"
          name="value" />
        <dsp:oparam name="true">
          <div class="atg_dataTableActions">
          <div class="atg_actionTo">    
          <dsp:droplet name="GiftlistSiteFilterDroplet">
            <dsp:param name="collection"  bean="ServiceCustomerProfile.giftlists"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="giftlists" param="filteredCollection" />
              <dsp:getvalueof var="size" idtype="int" value="${fn:length(giftlists)}"/>
              <!-- if multiple valid gift lists display the select drop down (if a gift list is being worked on it will be made the default in the drop down)-->
              <c:if test="${size > 1}">
              <form id="atg_commerce_csr_selectGiftlist" formid="atg_commerce_csr_selectGiftlist">
                <select id="giftlistSelect" onchange="DisableAddToGiftlist()">
                  <option value=""><fmt:message key='giftlistRenderer.selectGiftlist'/></option>
                  <c:forEach var="giftlist" items="${giftlists}" varStatus="giftlistStatus">
                    <dsp:param name="giftlist" value="${giftlist}"/>
                    <dsp:getvalueof var="giftlistId" param="giftlist.repositoryId" />
                    <dsp:getvalueof var="eventName" param="giftlist.eventName" />
                    <c:choose>
                      <c:when test="${GiftlistUIState.workingGiftlistId == giftlistId}">
                      <option value="${giftlistId}" selected="selected"><c:out value="${eventName}"/></option>
                      </c:when>
                      <c:otherwise>
                      <option value="${giftlistId}"><c:out value="${eventName}"/></option>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                </select>
              </form>
              </c:if>
              <!-- Display only the gift list name if just one valid gift list exists -->
              <c:if test="${size == 1}">
                <c:forEach var="giftlist" items="${giftlists}" varStatus="giftlistStatus">
                  <dsp:param name="giftlist" value="${giftlist}"/>
                  <dsp:getvalueof var="giftlistId" param="giftlist.repositoryId" />
                  <dsp:getvalueof var="eventName" param="giftlist.eventName" />
                  <c:set var="availableGiftlistId" value="${giftlistId}" />
                  <c:set var="availabaleGiftlistEventName" value="${eventName}" />
                  <c:out value="${eventName}" />
                </c:forEach>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>         
          <c:set var="formId" value="atg_commerce_csr_addProductsToGiftlist"/>
          <dsp:form style="display:none" action="#" id="${formId}" formid="${formId}">
            <input name="atg.successMessage" type="hidden" value=""/>
            <dsp:input type="hidden" id="${formId}_productId" bean="CSRGiftlistFormHandler.productId"/>
            <dsp:input type="hidden" id="catalogRefIds" bean="CSRGiftlistFormHandler.catalogRefIds"/>
            <dsp:input type="hidden" id="${formId}_selectedGiftlistId" bean="CSRGiftlistFormHandler.giftlistId"/>
            <dsp:input type="hidden" bean="CSRGiftlistFormHandler.addItemToGiftlist" priority="-10" value="" />
          </dsp:form>
      
          <c:set var="productId" value="${fn:escapeXml(productId)}" />
          <!-- Display Add to Gift list control if customer has existing gift lists that can be added to the current site -->
          <dsp:droplet name="GiftlistSiteFilterDroplet">
            <dsp:param name="collection"  bean="ServiceCustomerProfile.giftlists"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="giftlists" param="filteredCollection" />
              <dsp:getvalueof var="size" idtype="int" value="${fn:length(giftlists)}"/>
              <c:if test="${size > 0}">                     
                <input type="button" id="addToGiftlist" name="submit" value="<fmt:message key='giftlistRenderer.addToGiftlist'/>"
                  onclick="atg.commerce.csr.order.gift.addItemsToGiftlist( '${formId}', '${productId}', '${fn:escapeXml(availableGiftlistId)}');" />
                <br />
                <br />
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
            
          <!-- Display Add to Wish list control only if a customer is registered -->  
          <c:if test="${isProfileTransient == 'false'}">
            <dsp:getvalueof var="wishListId"
              bean="ServiceCustomerProfile.wishlist.id" />
            <dsp:setvalue beanvalue="ServiceCustomerProfile.wishlist"
              param="wishlist" />
            <dsp:getvalueof var="wishlistId" param="wishlist.id" />            
            <input type="button" name="submit"
              value="<fmt:message key='giftlistRenderer.addToWishlist'/>"
              onclick="atg.commerce.csr.order.gift.addItemsToWishlist( '${formId}', '${productId}', '${fn:escapeXml(wishlistId)}');" />
          </c:if>
          </div>
          </div>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<script type="text/javascript">
  /**
   * DisableAddToGiftlist
   *
   * disable the Add to Gift List button if the drop down select is not satisfied
   */
  var DisableAddToGiftlist = function () {
    if(dojo.byId("giftlistSelect")) {
  	  var giftlistSelect = dojo.byId("giftlistSelect");
      if (giftlistSelect.options[giftlistSelect.selectedIndex].value == "") {
        dojo.byId("addToGiftlist").disabled=true;
      }
      else dojo.byId("addToGiftlist").disabled=false;
    }
  };
  
  _container_.onLoadDeferred.addCallback(function () {
	  DisableAddToGiftlist();
  });
</script>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext
          .getAttribute("exception");
      ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/gift/skuGiftlistBrowserAction.jsp#1 $$Change: 946917 $--%>      