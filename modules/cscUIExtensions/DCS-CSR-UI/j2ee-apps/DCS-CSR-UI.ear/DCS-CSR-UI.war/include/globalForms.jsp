
<%
/* 
 * This file is used to hold global form definitions that are rendered only once when main.jsp is first rendered. It is 
 * included in main.jsp through configuration of /atg/svc/agent/ui/AgentUIConfiguration. These forms are global as opposed to 
 * a form that's included in a panel definition and rendered every time the panel is rendered.
 *
 * Note that forms in this file will be loaded at login and hence, degrade login performance.
 */
%>


<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:include src="/include/environment/environmentForms.jsp" otherContext="${CSRConfigurator.contextRoot}"/>


<svc-ui:frameworkUrl var="cancelReturnRequestErrorURL"/>
<svc-ui:frameworkUrl var="cancelReturnRequestSuccessURL" panelStacks="cmcExistingOrderPS,globalPanels"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
<dsp:form id="cancelReturnRequest" formid="cancelReturnRequest" method="post">
  <dsp:input bean="ReturnFormHandler.cancelReturnRequestSuccessURL" value="${cancelReturnRequestSuccessURL}" type="hidden" />
  <dsp:input bean="ReturnFormHandler.cancelReturnRequestErrorURL"   value="${cancelReturnRequestErrorURL}" type="hidden" />
  <dsp:input name="handleCancelReturnRequest" bean="ReturnFormHandler.cancelReturnRequest" type="hidden" priority="-10" value="" />
</dsp:form>


<dsp:importbean bean="/atg/commerce/custsvc/catalog/CustomCatalogProductSearch"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/ProductSearch"/>
<c:set var="useCustomCatalogs" value="${CSRConfigurator.customCatalogs}"/>
<c:choose>
  <c:when test="${useCustomCatalogs}">
    <dsp:form name="selectTreeNode" id="selectTreeNode" formid="selectCustomTreeNode">
      <dsp:input bean="CustomCatalogProductSearch.textInput" name="textInput" type="hidden" value=""/>
      <dsp:input bean="CustomCatalogProductSearch.hierarchicalCategoryId" name="hierarchicalCategoryId" type="hidden"/>
      <dsp:input bean="CustomCatalogProductSearch.isCatalogBrowsing" name="isCatalogBrowsing" value="true" type="hidden"/>
      <dsp:input bean="CustomCatalogProductSearch.currentResultPageNum" beanvalue="CustomCatalogProductSearch.currentResultPageNum" type="hidden" name="currentResultPageNum" priority="-10"/>
      <dsp:input bean="CustomCatalogProductSearch.navigationPath" name="path" type="hidden"/>
    </dsp:form>
  </c:when>
  <c:otherwise>
    <dsp:form name="selectTreeNode" id="selectTreeNode" formid="selectStandardTreeNode">
      <dsp:input bean="ProductSearch.textInput" name="textInput" type="hidden" value=""/>
      <dsp:input bean="ProductSearch.hierarchicalCategoryId" name="hierarchicalCategoryId" type="hidden"/>
      <dsp:input bean="ProductSearch.isCatalogBrowsing" name="isCatalogBrowsing" value="true" type="hidden"/>
      <dsp:input bean="ProductSearch.currentResultPageNum" beanvalue="ProductSearch.currentResultPageNum" type="hidden" name="currentResultPageNum" priority="-10" />
      <dsp:input bean="ProductSearch.navigationPath" name="path" type="hidden"/>
    </dsp:form>
  </c:otherwise>
</c:choose>

<svc-ui:frameworkUrl var="successURL" panelStacks=""/>
<svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
<dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1"/>
<dsp:form name="buyForm" action="#" formid="buyForm" id="buyForm">
  <input name="atg.successMessage" type="hidden" value=""/>
  <dsp:input bean="CartModifierFormHandler.addItemCount" name="addItemCount" type="hidden" value="1" />
  <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="hidden" value="" priority="-10" />
  <dsp:input bean="CartModifierFormHandler.items[0].siteId" type="hidden" name="siteId" value="" />
  <dsp:input bean="CartModifierFormHandler.items[0].quantity" name="quantity"  type="hidden" value="0"/>
  <dsp:input bean="CartModifierFormHandler.items[0].quantityWithFraction" name="quantityWithFraction"  type="hidden" value="0.0"/>
  <dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" name="catalogRefId" type="hidden" value="0" />
  <dsp:input bean="CartModifierFormHandler.items[0].productId" name="productId" type="hidden" value="0"/>
  <dsp:input name="errorURL" bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" value="${errorURL}"/>
  <dsp:input name="successURL" bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="${successURL}"/>
</dsp:form>


<%/* form used to request data for the order detail hover popup */%>
<dsp:importbean bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler"/>
<dsp:form action="#" id="orderDetailForm" formid="orderDetailForm" style="display:none">
  <dsp:input type="hidden" priority="-10" value="" bean="FrameworkBaseFormHandler.transform"/>
  <dsp:input type="hidden" name="orderId" value="" bean="FrameworkBaseFormHandler.parameterMap.orderId"/>
</dsp:form>

<%/* form used to request data for the order detail hover popup */%>
<dsp:importbean bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler"/>
<dsp:form action="#" id="noOpForm" formid="noOpForm" style="display:none">
  <dsp:input type="hidden" priority="-10" value="" bean="FrameworkBaseFormHandler.transform"/>
  <dsp:input type="hidden" name="errorURL" value="" bean="FrameworkBaseFormHandler.errorURL"/>
  <dsp:input type="hidden" name="successURL" value="" bean="FrameworkBaseFormHandler.successURL"/>
</dsp:form>

<%/* form used to delete an item from the cart */%>
<svc-ui:frameworkUrl var="cartURL" panelStacks="cmcShoppingCartPS"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
<dsp:form method="post" id="deleteItemForm" formid="deleteItemForm">
  <dsp:input type="hidden" name="successURL" value="${cartURL}"  bean="CartModifierFormHandler.removeItemFromOrderSuccessURL"/>
  <dsp:input type="hidden" name="errorURL" value="${cartURL}"  bean="CartModifierFormHandler.removeItemFromOrderErrorURL"/>
  <dsp:input value="" type="hidden" name="removeCommerceId" id="removeCommerceId"  bean="CartModifierFormHandler.removalCommerceIds"/>
  <dsp:input value="" type="hidden" priority="-10" bean="CartModifierFormHandler.removeItemFromOrder"/>
</dsp:form>

<%/* form used to delete an item from the cart */%>
<svc-ui:frameworkUrl var="cartURL" panelStacks="cmcShoppingCartPS"/>
<dsp:form method="post" id="deleteItemByRelationshipIdForm" formid="deleteItemByRelationshipIdForm">
  <dsp:input type="hidden" name="successURL" value="${cartURL}"  bean="CartModifierFormHandler.removeItemFromOrderByRelationshipIdSuccessURL"/>
  <dsp:input type="hidden" name="errorURL" value="${cartURL}"  bean="CartModifierFormHandler.removeItemFromOrderByRelationshipIdErrorURL"/>
  <dsp:input value="" type="hidden" name="removeRelationshipId" id="removeRelationshipId"  bean="CartModifierFormHandler.removalRelationshipIds"/>
  <dsp:input value="" type="hidden" priority="-10" bean="CartModifierFormHandler.removeItemFromOrderByRelationshipId"/>
</dsp:form>

<%/* form used to update items in a cart */%>
<svc-ui:frameworkUrl var="cartURL" panelStacks="cmcShoppingCartPS"/>
<dsp:form method="post" id="setOrderByRelationshipIdForm" formid="setOrderByRelationshipIdForm">
  <dsp:input type="hidden" name="successURL" value="${cartURL}"  bean="CartModifierFormHandler.setOrderByRelationshipIdSuccessURL"/>
  <dsp:input type="hidden" name="errorURL" value="${cartURL}"  bean="CartModifierFormHandler.setOrderByRelationshipIdErrorURL"/>
  <dsp:input value="" type="hidden" priority="-10" bean="CartModifierFormHandler.setOrderByRelationshipId"/>
</dsp:form>


<%/* form used to delete a manual adjustment */%>
<svc-ui:frameworkUrl var="dmaURL" panelStacks="cmcShoppingCartPS"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ManualAdjustmentsFormHandler"/>
<dsp:form formid="dma" id="dma" method="post">
  <dsp:input type="hidden" name="successURL" value="${dmaURL}" bean="ManualAdjustmentsFormHandler.deleteAdjustmentSuccessURL"/>
  <dsp:input type="hidden" name="errorURL" value="${dmaURL}" bean="ManualAdjustmentsFormHandler.deleteAdjustmentErrorURL"/>
  <dsp:input type="hidden" name="dmaId" id="dmaId" value="" bean="ManualAdjustmentsFormHandler.adjustmentId"/>
  <dsp:input type="hidden" name="dmaReason" id="dmaReason" value="" bean="ManualAdjustmentsFormHandler.adjustmentReasonCode"/>
  <dsp:input type="hidden" value="sturg" priority="-10" bean="ManualAdjustmentsFormHandler.deleteAdjustment"/>
</dsp:form>


<%
/*
 * The following form is used when Gift Lists are in use. 
 */
%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param bean="/atg/commerce/custsvc/util/CSRConfigurator.usingGiftlists" name="value"/>
<dsp:oparam name="true">

  <dsp:importbean bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler" />
  <dsp:importbean bean="/atg/commerce/custsvc/gifts/GiftlistUIState" />

  <dsp:form style="display:none" action="#" id="atg_commerce_csr_customer_gift_showSelectedGiftlist" formid="atg_commerce_csr_customer_gift_showSelectedGiftlist">
    <dsp:input type="hidden" id="giftlistId" name="giftlistId" bean="GiftlistUIState.giftlistId"/>
  </dsp:form>
  
  <dsp:form style="display:none" action="#" id="atg_commerce_csr_buyFromGiftlist" formid="atg_commerce_csr_buyFromGiftlist">
    <dsp:input type="hidden" id="purchaseGiftlistId" name="purchaseGiftlistId" bean="GiftlistUIState.purchaseGiftlistId"/>
  </dsp:form>      
  
  <dsp:form style="display:none" action="#" id="atg_commerce_csr_addProductToGiftlist" formid="atg_commerce_csr_addProductToGiftlist">
    <input name="atg.successMessage" type="hidden" value=""/>
    <dsp:input type="hidden" name="catalogRefIds" bean="CSRGiftlistFormHandler.catalogRefIds"/>
    <dsp:input type="hidden" name="productId" bean="CSRGiftlistFormHandler.productId"/>
    <dsp:input type="hidden" name="giftlistQuantity" bean="CSRGiftlistFormHandler.quantity"/>
    <dsp:input type="hidden" name="selectedGiftlistId" bean="CSRGiftlistFormHandler.giftlistId"/>
    <dsp:input type="hidden" bean="CSRGiftlistFormHandler.addItemToGiftlist" priority="-10" value="" />
  </dsp:form>

  <dsp:form style="display:none" action="#" id="deleteGiftlist" formid="deleteGiftlist">
    <dsp:input type="hidden" name="operation" bean="CSRGiftlistFormHandler.operation" value="delete" />
    <dsp:input type="hidden" name="giftlistId" bean="CSRGiftlistFormHandler.giftlistId" />
    <dsp:input type="hidden" bean="CSRGiftlistFormHandler.deleteGiftlist" priority="-10" value="" />
    <dsp:input type="hidden" value="${url}" priority="500" bean="CSRGiftlistFormHandler.deleteGiftlistSuccessURL" />
  </dsp:form>
  
  <%/* form used to control the drop down on the customer management pages to toggle between wishlist and giftlists */%>
  <dsp:form style="display:none" action="#" id="atg_commerce_csr_customer_gift_giftWishlistDropdown" formid="atg_commerce_csr_customer_gift_giftWishlistDropdown">
    <dsp:input type="hidden" name="showWishlist" id="showWishlist" bean="GiftlistUIState.showWishlist" value=""/>
  </dsp:form> 
   
  <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcGiftlistSearchPS" contentHeader="true"/> 
  <dsp:form style="display:none" id="atg_commerce_csr_giftlistChangeSiteForm" formid="atg_commerce_csr_giftlistChangeSiteForm">
    <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.errorURL" />
    <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.successURL" />
    <dsp:input type="hidden" name="siteId" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.inputParameters.siteId" value=""/>
    <dsp:input type="hidden" priority="-10" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.changeEnvironment" value=""/>
  </dsp:form>
  
  <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
  <svc-ui:frameworkUrl var="successURL" panels="cmcGiftlistsViewP" contentHeader="true"/> 
  <dsp:form style="display:none" id="atg_commerce_csr_customerGiftlistChangeSiteForm" formid="atg_commerce_csr_customerGiftlistChangeSiteForm">
    <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.errorURL" />
    <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.successURL" />
    <dsp:input type="hidden" name="siteId" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.inputParameters.siteId" value=""/>
    <dsp:input type="hidden" priority="-10" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.changeEnvironment" value=""/>
  </dsp:form>  
  
  </dsp:oparam>
  </dsp:droplet>
  
<%
/*
 * The following forms are used when scheduled orders are in use.  
 */
%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param bean="/atg/commerce/custsvc/util/CSRConfigurator.usingScheduledOrders" name="value"/>
<dsp:oparam name="true">

  <%/* form used to populate the customer's scheduled order panel */%>
  <dsp:form style="display:none" id="scheduledOrdersListForm" formid="scheduledOrdersListForm">
    <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/order/scheduled/ScheduledOrderTableFormHandler.search"/>
    <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/order/scheduled/ScheduledOrderTableFormHandler.currentPage"/>
    <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/order/scheduled/ScheduledOrderTableFormHandler.sortField"/>
    <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/order/scheduled/ScheduledOrderTableFormHandler.sortDirection"/>
  </dsp:form> 
  
  <%/* form used to populate the scheduled order's submitted order panel */%>
  <dsp:form style="display:none" id="submittedOrdersListForm" formid="submittedOrdersListForm">
    <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/order/scheduled/SubmittedOrderTableFormHandler.search"/>
    <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/order/scheduled/SubmittedOrderTableFormHandler.currentPage"/>
    <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/order/scheduled/SubmittedOrderTableFormHandler.sortField"/>
    <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/order/scheduled/SubmittedOrderTableFormHandler.sortDirection"/>
  </dsp:form>
  
</dsp:oparam>
</dsp:droplet>


<%/* form used to save the current working order */%>
<svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels"/>
<svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
<dsp:form style="display:none" id="globalCommitOrderForm" action="#">
  <dsp:input type="hidden" value="" priority="-10" bean="/atg/commerce/custsvc/order/CommitOrderFormHandler.persistOrder"/>
  <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/commerce/custsvc/order/CommitOrderFormHandler.commitOrderUpdatesSuccessURL"/>
  <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/commerce/custsvc/order/CommitOrderFormHandler.commitOrderUpdatesErrorURL"/>
  
</dsp:form>


<%/* form used to populate the customer order history panel */%>
<dsp:form style="display:none" id="orderHistoryListForm" formid="orderHistoryListForm">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/order/OrderHistoryTableFormHandler.search"/>
  <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/order/OrderHistoryTableFormHandler.currentPage"/>
  <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/order/OrderHistoryTableFormHandler.sortField"/>
  <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/order/OrderHistoryTableFormHandler.sortDirection"/>
</dsp:form> 


<%/* form used to populate the order's related order panel */%>
<dsp:form style="display:none" id="relatedOrdersListForm" formid="relatedOrdersListForm">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/order/RelatedOrdersTableFormHandler.search"/>
  <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/order/RelatedOrdersTableFormHandler.currentPage"/>
  <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/order/RelatedOrdersTableFormHandler.sortField"/>
  <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/order/RelatedOrdersTableFormHandler.sortDirection"/>
</dsp:form> 


<%/* form used to populate the customer purchased items panel */%>
<dsp:form style="display:none" id="purchasedItemsForm" formid="purchasedItemsForm">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/order/PurchasedItemsHistoryTableFormHandler.search"/>
  <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/order/PurchasedItemsHistoryTableFormHandler.currentPage"/>
  <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/order/PurchasedItemsHistoryTableFormHandler.sortField"/>
  <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/order/PurchasedItemsHistoryTableFormHandler.sortDirection"/>
</dsp:form> 

<!-- Form To Add wish list product to cart -->
<svc-ui:frameworkUrl var="successURL"  contentHeader="true"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1"/>
<dsp:form name="atg_commerce_csr_customer_gift_addItemFromWishlistToCart" action="#" formid="atg_commerce_csr_customer_gift_addItemFromWishlistToCart" id="atg_commerce_csr_customer_gift_addItemFromWishlistToCart">
  <input name="atg.successMessage" type="hidden" value=""/>
  <dsp:input bean="CartModifierFormHandler.addItemCount" name="addItemCount" type="hidden" value="1" />
  <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="hidden" value="" priority="-100" />
  <dsp:input bean="CartModifierFormHandler.items[0].siteId" type="hidden" name="siteId" value="" />
  <dsp:input bean="CartModifierFormHandler.items[0].quantity" name="quantity"  type="hidden" value="1" id="${catalogRefId}"/>
  <dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" name="catalogRefId" id="catalogRefId" type="hidden" value="" />
  <dsp:input bean="CartModifierFormHandler.items[0].productId" name="productId" id="productId" type="hidden" value=""/>
  <dsp:input name="successURL" bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="${successURL}"/>
</dsp:form>
<!-- End Form -->


<%/* form used to request data for the order detail hover popup */%>
<svc-ui:frameworkUrl var="successURL" panelStacks="cmcShoppingCartPS" tab="commerceTab"/>
<dsp:importbean bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler"/>
<dsp:form action="#" id="viewCart" formid="noOpForm" style="display:none">
  <dsp:input type="hidden" priority="-10" value="" bean="FrameworkBaseFormHandler.transform"/>
  <dsp:input type="hidden" name="errorURL" value="" bean="FrameworkBaseFormHandler.errorURL"/>
  <dsp:input type="hidden" name="successURL" value="${successURL}" bean="FrameworkBaseFormHandler.successURL"/>
</dsp:form>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
<svc-ui:frameworkUrl var="successURL" panelStacks="cmcShoppingCartPS"/>
<dsp:importbean bean="/atg/commerce/custsvc/promotion/GiftWithPurchaseFormHandler"/>
<dsp:form formid="gwpMakeGiftSelection" id="gwpMakeGiftSelection">
  <input name="atg.successMessage" type="hidden" value="<fmt:message key='cart.items.successfully.added'/>"/>
  <dsp:input bean="GiftWithPurchaseFormHandler.promotionId" name="promotionId" type="hidden" value=""/>
  <dsp:input bean="GiftWithPurchaseFormHandler.giftHashCode" name="giftHashCode" type="hidden" value=""/>
  <dsp:input bean="GiftWithPurchaseFormHandler.productId" name="productId" type="hidden" value=""/>
  <dsp:input bean="GiftWithPurchaseFormHandler.skuId" name="skuId" type="hidden" value=""/>
  <dsp:input bean="GiftWithPurchaseFormHandler.quantity" name="quantity" type="hidden" value=""/>
  <dsp:input bean="GiftWithPurchaseFormHandler.currentSelectedItemId" name="currentSelectedItemId" type="hidden" value=""/>
  <dsp:input bean="GiftWithPurchaseFormHandler.makeGiftSelectionErrorURL" name="errorURL" type="hidden" value="${errorURL}" />
  <dsp:input bean="GiftWithPurchaseFormHandler.makeGiftSelectionSuccessURL" name="successURL"  type="hidden" value="${successURL}" />
  <dsp:input bean="GiftWithPurchaseFormHandler.makeGiftSelection" type="hidden" value="" priority="-10"/>
</dsp:form>
</dsp:layeredBundle>



</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/globalForms.jsp#2 $$Change: 1179550 $--%>
