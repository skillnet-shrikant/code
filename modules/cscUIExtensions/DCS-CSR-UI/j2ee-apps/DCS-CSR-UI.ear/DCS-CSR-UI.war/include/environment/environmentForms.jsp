<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/environment/environmentForms.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $

--%>
<%
/* 
 * This file is used to hold global environment changing form definitions that are rendered only once when main.jsp is first rendered. It is 
 * included in globalForms.jsp, which is included through configuration of /atg/svc/agent/ui/AgentUIConfiguration. These forms are global as opposed to 
 * a form that's included in a panel definition and rendered every time the panel is rendered.
 *
 * Note that forms in this file will be loaded at login and hence, slightly degrade login performance.
 */
%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<svc-ui:frameworkUrl var="noOpErrorURL"/>
<svc-ui:frameworkUrl var="noOpSuccessURL"/>

  <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCatalogPS,globalPanels" tab="commerceTab"/>
  

  <%/*form used to load a new order into the working environment */%>
  <dsp:form style="display:none" id="envNewOrderForm" formid="envNewOrderForm">
    <dsp:input name="changeEnvironment" type="hidden" value="" bean="/atg/commerce/custsvc/environment/CreateNewOrder.changeEnvironment" priority="-10"/>
    <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/commerce/custsvc/environment/CreateNewOrder.successURL" />
    <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/commerce/custsvc/environment/CreateNewOrder.errorURL" />
  </dsp:form>

  <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
  <svc-ui:frameworkUrl var="successfulOrderLoadURL" panelStacks="globalPanels,cmcExistingOrderPS" tab="commerceTab"/>
  <%/*form used to load an order into the view order holder */%>
  <dsp:form style="display:none" id="atg_commerce_csr_loadExistingOrderForm"
    formid="atg_commerce_csr_loadExistingOrderForm">
  <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.errorURL" />
  <dsp:input type="hidden" name="successURL" value="${successfulOrderLoadURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.successURL" />
  <dsp:input type="hidden" name="orderId" bean="/atg/commerce/custsvc/environment/ChangeOrder.inputParameters.newOrderId" value=""/>
  <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/environment/ChangeOrder.changeEnvironment" value=""/>
  </dsp:form>


  <%/*form used to load an order into the view order holder */%>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcExistingOrderPS" tab="commerceTab"/>
  <svc-ui:frameworkUrl var="errorURL"/>
  <dsp:form style="display:none" id="atg_commerce_csr_viewExistingOrderForm"
    formid="atg_commerce_csr_viewExistingOrderForm">
     <dsp:input type="hidden" name="viewOrderId" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.viewOrderId" value=""/>
     <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.changeViewOrder" value=""/>
     <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.errorURL" />
     <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.successURL" />
  </dsp:form> 
  
  
  
  <dsp:importbean bean="/atg/commerce/custsvc/environment/EditOrder"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
  <svc-ui:frameworkUrl var="successfulCartURL" panelStacks="globalPanels,cmcShoppingCartPS"/>
  <%/*Load Existing Order Form For Edit shopping cart link on the existing order view page */%>
  <dsp:form style="display:none" id="atg_commerce_csr_finish_editExistingOrderCartForm"
    formid="atg_commerce_csr_finish_editExistingOrderCartForm">
    <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="EditOrder.errorURL" />
    <dsp:input type="hidden" name="successURL" value="${successfulCartURL}" bean="EditOrder.successURL" />
    <dsp:input type="hidden" name="orderId" bean="EditOrder.inputParameters.newOrderId" value="${order.id}"/>    
    <dsp:input type="hidden" priority="-10" bean="EditOrder.changeEnvironment" value=""/>
  </dsp:form>
   
   <svc-ui:frameworkUrl var="successfulShippingURL" panelStacks="globalPanels,cmcShippingAddressPS"/>
   <%/*Load Existing Order Form For Shipping Edit on the existing order view page*/%>
   <dsp:form style="display:none" id="atg_commerce_csr_finish_editExistingOrderShippingForm"
     formid="atg_commerce_csr_finish_editExistingOrderShippingForm">
     <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="EditOrder.errorURL" />
     <dsp:input type="hidden" name="successURL" value="${successfulShippingURL}" bean="EditOrder.successURL" />
     <dsp:input type="hidden" name="orderId" bean="EditOrder.inputParameters.newOrderId" value="${order.id}"/>    
     <dsp:input type="hidden" priority="-10" bean="EditOrder.changeEnvironment" value=""/>
   </dsp:form>

    <svc-ui:frameworkUrl var="successfulShippingMethodURL" panelStacks="globalPanels,cmcShippingMethodPS"/>
    <%/*Load Existing Order Form For Shipping Method Edit on the existing order view page*/%>
    <dsp:form style="display:none" id="atg_commerce_csr_finish_editExistingOrderShippingMethodForm"
      formid="atg_commerce_csr_finish_editExistingOrderShippingMethodForm">
      <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="EditOrder.errorURL" />
      <dsp:input type="hidden" name="successURL" value="${successfulShippingMethodURL}" bean="EditOrder.successURL" />
      <dsp:input type="hidden" name="orderId" bean="EditOrder.inputParameters.newOrderId" value="${order.id}"/>    
      <dsp:input type="hidden" priority="-10" bean="EditOrder.changeEnvironment" value=""/>
    </dsp:form>
    
    <svc-ui:frameworkUrl var="successfulBillingURL" panelStacks="globalPanels,cmcBillingPS"/>
    <%/* Load Existing Order Form For Billing Edit on the existing order view page*/%>
    <dsp:form style="display:none" id="atg_commerce_csr_finish_editExistingOrderBillingForm"
      formid="atg_commerce_csr_finish_editExistingOrderBillingForm">
      <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="EditOrder.errorURL" />
      <dsp:input type="hidden" name="successURL" value="${successfulBillingURL}" bean="EditOrder.successURL" />
      <dsp:input type="hidden" name="orderId" bean="EditOrder.inputParameters.newOrderId" value="${order.id}"/>    
      <dsp:input type="hidden" priority="-10" bean="EditOrder.changeEnvironment" value=""/>
    </dsp:form>

    <%/*Load Existing Order Form For Start Return/Exchange process on the existing order view page and purchased item history grid*/%>
    <svc-ui:frameworkUrl var="successURL" panelStacks="cmcReturnsPS,globalPanels" tab="commerceTab"/>
    <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
    <dsp:form id="csrCreateReturnRequest" formid="csrCreateReturnRequest">
      <dsp:input name="successURL" bean="/atg/commerce/custsvc/returns/StartReturnExchangeProcess.successURL" value="${successURL}" type="hidden" />
      <dsp:input name="errorURL" bean="/atg/commerce/custsvc/returns/StartReturnExchangeProcess.errorURL"  value="${errorURL}" type="hidden" />
      <dsp:input bean="/atg/commerce/custsvc/returns/StartReturnExchangeProcess.changeEnvironment"  type="hidden" priority="-10" value=""/>
      <dsp:input name="orderId" bean="/atg/commerce/custsvc/returns/StartReturnExchangeProcess.inputParameters.newOrderId" type="hidden" />
    </dsp:form>
    
   <%/*Catalog value for window scope form */%>
   <dsp:importbean bean="atg/commerce/custsvc/catalog/AddProductsByIdConfigurator" var="addProductsByIdConfigurator"/>
   <dsp:form formid="addProductsByIdWindowScopeForm" style="display:none;" id="atg_commerce_csr_catalog_addProductsByIdWindowScopeForm">
     <dsp:input name="search" type="hidden" value="" bean="AddProductsByIdConfigurator.storeInWindowScope" priority="-10"/>
     <dsp:input bean="AddProductsByIdConfigurator.valueForWindowScope" name="atg_commerce_csr_catalog_valueForWindowScope" value="" type="hidden"/>
   </dsp:form>
   
   <%/*Commerce Context Area drop-down Site selection form */%>
   <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
   <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCatalogPS,globalPanels" contentHeader="true" tab="commerceTab"/> 
   <dsp:form style="display:none" id="atg_commerce_csr_loadExistingSiteForm" formid="atg_commerce_csr_loadExistingSiteForm">
     <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.errorURL" />
     <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.successURL" />
     <dsp:input type="hidden" name="siteId" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.inputParameters.siteId" value=""/>
     <dsp:input type="hidden" priority="-10" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.changeEnvironment" value=""/>
   </dsp:form>
   
   <%/* Commerce Context Area drop-down Catalog selection form*/%>
   <dsp:importbean bean="/atg/commerce/custsvc/environment/ChangeCatalogAndPriceList"/>
   <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCatalogPS,globalPanels" contentHeader="true"/>
   <svc-ui:frameworkUrl var="errorURL"/>
   <c:if test="${CSRConfigurator.customCatalogs}">
     <dsp:form method="post" id="setCatalogForm" name="setCatalogForm" formid="setCatalogForm" style="display:none">
       <dsp:input bean="ChangeCatalogAndPriceList.changeEnvironment" type="hidden" value="" priority="-10"/>
       <dsp:input bean="ChangeCatalogAndPriceList.inputParameters.catalogId" name="catalogId" id="catalogId" value="" type="hidden"/>
       <dsp:input name="successURL" bean="ChangeCatalogAndPriceList.successURL" value="${successURL}" type="hidden" />
       <dsp:input name="errorURL" bean="ChangeCatalogAndPriceList.errorURL"  value="${errorURL}" type="hidden" />
     </dsp:form>
   </c:if>

   <%/* Commerce Context Area drop-down Price List selection form*/%>
   <dsp:form method="post" action="#" id="setPriceListForm" name="setPriceListForm" formid="setPriceListForm" style="display:none">
     <dsp:input bean="ChangeCatalogAndPriceList.changeEnvironment" type="hidden" value="" priority="-10"/>
     <dsp:input bean="ChangeCatalogAndPriceList.inputParameters.priceListId" name="priceListId" id="priceListId" value="" type="hidden"/>
     <dsp:input name="successURL" bean="ChangeCatalogAndPriceList.successURL" value="${successURL}" type="hidden" />
     <dsp:input name="errorURL" bean="ChangeCatalogAndPriceList.errorURL"  value="${errorURL}" type="hidden" />
   </dsp:form>

    <%/* MultiSite Product Details form*/%>
   <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
   <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels" contentHeader="true" tab="commerceTab"/> 
   <dsp:form style="display:none" id="atg_commerce_csr_productDetailsForm" formid="atg_commerce_csr_productDetailsForm">
     <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.errorURL" />
     <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.successURL" />
     <dsp:input type="hidden" name="siteId" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.inputParameters.siteId" value=""/>
     <dsp:input type="hidden" priority="-10" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.changeEnvironment" value=""/>
   </dsp:form>
   
    <%/* MultiSite Change Site View Product Catalog form*/%>
   <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
   <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCatalogPS,globalPanels" selectTabbedPanels="cmcProductCatalogBrowseP" contentHeader="true" tab="commerceTab"/> 
   <dsp:form style="display:none" id="atg_commerce_csr_changeSiteProductCatalog" formid="atg_commerce_csr_changeSiteProductCatalog">
     <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.errorURL" />
     <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.successURL" />
     <dsp:input type="hidden" name="siteId" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.inputParameters.siteId" value=""/>
     <dsp:input type="hidden" priority="-10" bean="/atg/svc/agent/environment/ChangeSiteFormHandler.changeEnvironment" value=""/>
   </dsp:form>

   <%/* form used to change the current sale price list in the environment */%>
   <dsp:form method="post" action="#" id="setSalePriceListForm" name="setSalePriceListForm" formid="setSalePriceListForm" style="display:none">
     <dsp:input bean="ChangeCatalogAndPriceList.changeEnvironment" type="hidden" value="" priority="-10"/>
     <dsp:input bean="ChangeCatalogAndPriceList.inputParameters.salePriceListId" name="salePriceListId" id="salePriceListId" value="" type="hidden"/>
     <dsp:input name="successURL" bean="ChangeCatalogAndPriceList.successURL" value="${successURL}" type="hidden" />
     <dsp:input name="errorURL" bean="ChangeCatalogAndPriceList.errorURL"  value="${errorURL}" type="hidden" />
   </dsp:form>
   
   <%/* form used to generate a copy of an order and load it into the environment */%>
   <dsp:importbean bean="/atg/commerce/custsvc/order/DuplicateOrder"/>
   <svc-ui:frameworkUrl var="duplicateOrderSuccessURL" panelStacks="globalPanels,cmcShoppingCartPS"/>
   <svc-ui:frameworkUrl var="duplicateOrderErrorURL" panelStacks="globalPanels"/>
   <dsp:form style="display:none" id="atg_commerce_csr_copyOrder"
     formid="atg_commerce_csr_copyOrder">
      <dsp:input type="hidden" name="orderId" bean="DuplicateOrder.inputParameters.orderToDuplicate" value=""/>
      <dsp:input type="hidden" priority="-10" bean="DuplicateOrder.changeEnvironment" value=""/>
      <dsp:input name="errorURL" type="hidden" value="${duplicateOrderErrorURL}" bean="DuplicateOrder.errorURL" />
      <dsp:input name="successURL" type="hidden" value="${duplicateOrderSuccessURL}" bean="DuplicateOrder.successURL" />      
   </dsp:form>
      
   <%
   /*
    * The following forms are used when scheduled order are in use.
    */
   %>
   <dsp:droplet name="/atg/dynamo/droplet/Switch">
   <dsp:param bean="/atg/commerce/custsvc/util/CSRConfigurator.usingScheduledOrders" name="value"/>
   <dsp:oparam name="true">

   <%/*form used to load an order into the view order holder */%>
   <svc-ui:frameworkUrl var="successURL" panelStacks="cmcScheduledOrderPS" tab="commerceTab"/>
   <svc-ui:frameworkUrl var="errorURL"/>
   <dsp:form style="display:none" id="atg_commerce_csr_viewScheduledOrderForm"
     formid="atg_commerce_csr_viewScheduledOrderForm">
      <dsp:input type="hidden" name="viewOrderId" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.viewOrderId" value=""/>
      <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.changeViewOrder" value=""/>
      <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.errorURL" />
      <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.successURL" />
   </dsp:form> 
   
     <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcConfirmUpdateSchedulePS"/>
     <svc-ui:frameworkUrl var="errorURL"/>
     <%/* form used to populate activate a scheduled order */%>
     <dsp:importbean bean="/atg/commerce/custsvc/order/scheduled/ActivateSchedule"/>
     <dsp:form style="display:none" id="atg_commerce_csr_scheduled_activateSchedule"
       formid="atg_commerce_csr_scheduled_activateSchedule">
        <dsp:input type="hidden" name="orderId" bean="ActivateSchedule.inputParameters.newOrderId" value=""/>
        <dsp:input type="hidden" name="scheduledOrderId" bean="ActivateSchedule.inputParameters.scheduledOrderId" value=""/>
        <dsp:input type="hidden" name="activateAction" bean="ActivateSchedule.inputParameters.action" value="activate"/>
        <dsp:input type="hidden" priority="-10" bean="ActivateSchedule.changeEnvironment" value=""/>
        <dsp:input type="hidden" name="activateErrorURL" value="${errorURL}" bean="ActivateSchedule.errorURL" />
        <dsp:input type="hidden" name="activateSuccessURL" value="${successURL}" bean="ActivateSchedule.successURL" />      
     </dsp:form>
  
     <%/* form used to populate deactivate a scheduled order */%>
     <dsp:importbean bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule"/>
     <dsp:form style="display:none" id="atg_commerce_csr_scheduled_deactivateSchedule"
       formid="atg_commerce_csr_scheduled_deactivateSchedule">
        <dsp:input type="hidden" name="orderId" bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule.inputParameters.newOrderId" value=""/>
        <dsp:input type="hidden" name="scheduledOrderId" bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule.inputParameters.scheduledOrderId" value=""/>
        <dsp:input type="hidden" name="deactivateAction" bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule.inputParameters.action" value="deactivate"/>
        <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule.changeEnvironment" value=""/>
        <dsp:input type="hidden" name="deactivateErrorURL"  value="${errorURL}" bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule.errorURL" />
        <dsp:input type="hidden" name="deactivateSuccessURL" value="${successURL}"  bean="/atg/commerce/custsvc/order/scheduled/DeactivateSchedule.successURL" />      
     </dsp:form>
  
   
   <%/* form used to load a scheduled order as the current working order */%>
     <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
     <svc-ui:frameworkUrl var="successfulScheduledOrderLoadURL" panelStacks="globalPanels,cmcScheduledOrderPS" tab="commerceTab"/>
     <dsp:form style="display:none" id="atg_commerce_csr_loadExistingScheduledOrderForm"
       formid="atg_commerce_csr_loadExistingScheduledOrderForm">
       <dsp:input name="errorURL" type="hidden" value="${errorURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.errorURL" />
       <dsp:input name="successURL" type="hidden" value="${successfulScheduledOrderLoadURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.successURL" />
       <dsp:input type="hidden" name="orderId" bean="/atg/commerce/custsvc/environment/ChangeOrder.inputParameters.newOrderId" value=""/>
       <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/environment/ChangeOrder.changeEnvironment" value=""/>
     </dsp:form>
       
     <svc-ui:frameworkUrl var="errorURL"/>
     <svc-ui:frameworkUrl var="successfulChangeURL" panelStacks="globalPanels,cmcScheduleUpdatePS"/>
     <svc-ui:frameworkUrl var="successfulAddURL" panelStacks="globalPanels,cmcScheduleCreatePS"/>
     <%/* form used to load a scheduled order as the current working order for a schedule update */%>
    <dsp:form style="display:none" id="atg_commerce_csr_loadScheduledOrderForScheduleChange"
       formid="atg_commerce_csr_loadScheduledOrderForScheduleChange">
        <dsp:input name="errorURL" type="hidden" value="${errorURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.errorURL" />
        <dsp:input name="successURL" type="hidden" value="${successfulChangeURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.successURL" />
        <dsp:input type="hidden" name="orderId" bean="/atg/commerce/custsvc/environment/ChangeOrder.inputParameters.newOrderId" value=""/>
        <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/environment/ChangeOrder.changeEnvironment" value=""/>
     </dsp:form>
     
     <%/* form used to load a scheduled order as the current working order for a schedule add */%>
     <dsp:form style="display:none" id="atg_commerce_csr_loadScheduledOrderForScheduleAdd"
       formid="atg_commerce_csr_loadScheduledOrderForScheduleAdd">
        <dsp:input name="errorURL" type="hidden" value="${errorURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.errorURL" />
        <dsp:input name="successURL" type="hidden" value="${successfulAddURL}" bean="/atg/commerce/custsvc/environment/ChangeOrder.successURL" />
        <dsp:input type="hidden" name="orderId" bean="/atg/commerce/custsvc/environment/ChangeOrder.inputParameters.newOrderId" value=""/>
        <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/environment/ChangeOrder.changeEnvironment" value=""/>
     </dsp:form>

     <%/* form used to submit an instance of a scheduled order */%>
     <svc-ui:frameworkUrl var="duplicateAndSubmitSuccessURL" panelStacks="globalPanels,cmcConfirmOrderPS"/>
     <svc-ui:frameworkUrl var="duplicateAndSubmitErrorURL" panelStacks="globalPanels"/>
     <dsp:importbean bean="/atg/commerce/custsvc/order/scheduled/DuplicateAndSubmit"/>
     <dsp:form style="display:none" id="atg_commerce_csr_scheduled_duplicateAndSubmit"
       formid="atg_commerce_csr_scheduled_duplicateAndSubmit">
        <dsp:input type="hidden" name="orderId" bean="DuplicateAndSubmit.inputParameters.orderToDuplicate" value=""/>
        <dsp:input type="hidden" priority="-10" bean="DuplicateAndSubmit.changeEnvironment" value=""/>
        <dsp:input type="hidden" name="errorURL" value="${duplicateAndSubmitErrorURL}" bean="DuplicateAndSubmit.errorURL" />
        <dsp:input type="hidden" name="successURL" value="${duplicateAndSubmitSuccessURL}" bean="DuplicateAndSubmit.successURL" />      
     </dsp:form>
     
     
     </dsp:oparam>
   </dsp:droplet>

    <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCatalogPS,globalPanels" contentHeader="true"/>
    <svc-ui:frameworkUrl var="errorURL"/>
    <dsp:form style="display:none" action="#"
		id="syncCurrentCustomerCatalog" formid="syncCurrentCustomerCatalog">
		<dsp:input type="hidden" value="" priority="-10"
			bean="/atg/commerce/custsvc/environment/SyncToCustomerCatalog.changeEnvironment" />
		<dsp:input type="hidden" name="errorURL" value="${errorURL}"
			bean="/atg/commerce/custsvc/environment/SyncToCustomerCatalog.errorURL" />
		<dsp:input type="hidden" name="successURL" value="${successURL}"
			bean="/atg/commerce/custsvc/environment/SyncToCustomerCatalog.successURL" />
    </dsp:form>

	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param
			bean="/atg/commerce/custsvc/util/CSRConfigurator.usingPriceLists"
			name="value" />
		<dsp:oparam name="true">
            <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCatalogPS,globalPanels" tab="commerceTab" contentHeader="true"/>
            <svc-ui:frameworkUrl var="errorURL"/>
            <dsp:form style="display:none" action="#"
				id="syncCurrentCustomerPriceLists"
				formid="syncCurrentCustomerPriceLists">
				<dsp:input type="hidden" value="" priority="-10"
					bean="/atg/commerce/custsvc/environment/SyncToCustomerPriceLists.changeEnvironment" />
				<dsp:input type="hidden" name="errorURL" value="${errorURL}"
					bean="/atg/commerce/custsvc/environment/SyncToCustomerPriceLists.errorURL" />
				<dsp:input type="hidden" name="successURL" value="${successURL}"
					bean="/atg/commerce/custsvc/environment/SyncToCustomerPriceLists.successURL" />
			</dsp:form>
		</dsp:oparam>
	</dsp:droplet>
	
	
	<%/*Create a new appeasement for an order*/%>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcAppeasementsPS,globalPanels" tab="commerceTab"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
  <dsp:form id="createAppeasement" formid="createAppeasement">
    <dsp:input name="successURL" bean="/atg/commerce/custsvc/appeasement/StartAppeasementFormHandler.successURL" value="${successURL}" type="hidden" />
    <dsp:input name="errorURL" bean="/atg/commerce/custsvc/appeasement/StartAppeasementFormHandler.errorURL"  value="${errorURL}" type="hidden" />
    <dsp:input bean="/atg/commerce/custsvc/appeasement/StartAppeasementFormHandler.changeEnvironment"  type="hidden" priority="-10" value=""/>
    <dsp:input name="orderId" bean="/atg/commerce/custsvc/appeasement/StartAppeasementFormHandler.inputParameters.newOrderId" type="hidden" />
  </dsp:form>
  
  <%/*Cancel the appeasement for an order - return to order view*/%>
  <svc-ui:frameworkUrl var="cancelAppeasementErrorURL"/>
	<svc-ui:frameworkUrl var="cancelAppeasementSuccessURL" panelStacks="cmcExistingOrderPS,globalPanels"/>
	<dsp:form id="cancelAppeasement" formid="cancelAppeasement" method="post">
	  <dsp:input bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.cancelAppeasementSuccessURL" value="${cancelAppeasementSuccessURL}" type="hidden" />
	  <dsp:input bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.cancelAppeasementErrorURL"   value="${cancelAppeasementErrorURL}" type="hidden" />
	  <dsp:input name="handleCancelAppeasement" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.cancelAppeasement" type="hidden" priority="-10" value="" />
	</dsp:form>
  
  <%/*Apply the changed values to the appeasement*/%>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcAppeasementsPS,globalPanels" tab="commerceTab"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
  <dsp:form id="csrApplyAppeasementRefundValues" formid="csrApplyAppeasementRefundValues">
    <dsp:input name="successURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.successURL" value="${successURL}" type="hidden" />
    <dsp:input name="errorURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.errorURL"  value="${errorURL}" type="hidden" />
    <dsp:input bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.applyAppeasementRefunds"  type="hidden" priority="-10" value=""/>
  </dsp:form>
  
	
	
</dsp:page>
<!-- $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/environment/environmentForms.jsp#2 $$$$DateTime: 2015/07/10 11:58:13 $ -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/environment/environmentForms.jsp#2 $$Change: 1179550 $--%>
