<%--
 This UI fragment defines the Order View Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/orderViewUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/custsvc/repository/CustSvcRepositoryItemServlet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderSubmitted"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderItemLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>
  <dsp:importbean var="customerProfile" bean="/atg/userprofiling/ServiceCustomerProfile"/>
  <dsp:importbean	bean="/com/mff/commerce/order/MFFCSCTasksOnOrderFormHandler"/> 
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CommitOrderFormHandler" var="commitOrderFormHandler"/>
  
  <script type="text/javascript">
	if (!dijit.byId("approveChangePopup")) {
		new dojox.Dialog( {
			id :"approveChangePopup",
			cacheContent :"false",
			executeScripts :"true",
			scriptHasHooks :"true"
		});
	}
	function submitChangeReturnablePPSFlag() {
		popupUrl='${CSRConfigurator.contextRoot}' + '/panels/approvals/readyForReturnableInPPSChange.jsp?_windowid=${windowId}';
	    atg.commerce.csr.common.showPopupWithReturn({
		      popupPaneId: 'approveChangePopup',
		      title: "Confirm Returnable in PPS change condition",url: popupUrl,
		      onClose: function(args) {
		        if (args.result == 'confirm') {
					atgSubmitAction({
						form : dojo.byId("returnablePPSform"),
				        panels: ["cmcExistingOrderP"],
				        panelStack : ['cmcExistingOrderPS','globalPanels']
					});
		        }
		    	}});
		return false;
	}
	function orderConfirmEmail() {
	  atgSubmitAction({
	    form: dojo.byId('orderConfirmEmailForm'),
	    panels: ["cmcExistingOrderP"],
        panelStack : ['cmcExistingOrderPS','globalPanels'],
	    sync: true
	    
	  });
	}
  </script>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <svc-ui:frameworkUrl var="successErrorURL" panelStacks="globalPanels,cmcExistingOrderPS" contentHeader="true" />
  <dsp:getvalueof var="order" param="currentOrder"/>
  <c:set var="returnablePPSform" value="returnablePPSform"/>
  
  <div id="atg_commerce_csr_order_orderDetails_subPanel" class="atg_commerce_csr_orderDetails">
    <div class="atg-csc-base-table">
      <div class="atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-first-label"><fmt:message key='intrinsicAttributes.orderNumber.field'/></span>
        <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
          <c:out value="${order.id}" />
        </span>

        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label"><fmt:message key='intrinsicAttributes.orderSubmissionDate.field'/></span>
        <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
          <web-ui:formatDate value="${order.submittedDate}" type="both" dateStyle="medium" timeStyle="short"/>
        </span>
        
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label"><fmt:message key='intrinsicAttributes.customOrderNumber.field'/></span>
        <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
          <c:out value="${order.orderNumber}" />
        </span>
      </div>
      
      <div class="atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-first-label"><fmt:message key='intrinsicAttributes.name.field'/></span>
        <span  class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
      
        <dsp:tomap var="customerProfileMap" value="${customerProfile}"/>
        <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
       <c:choose>
          <c:when test="${!empty customerProfileMap.lastName || !empty customerProfileMap.firstName}">
            <fmt:message var="name" key="lastname-firstname">
            <fmt:param value="${customerProfileMap.lastName}"/>
            <fmt:param value="${customerProfileMap.firstName}"/>
            </fmt:message>
            <c:out value="${name}"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="no-name"/>
          </c:otherwise>
        </c:choose>
        </dsp:layeredBundle>
        
        </span>

        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label"><fmt:message key='intrinsicAttributes.status.field'/></span>
        <dsp:droplet name="OrderStateDescriptions">
          <dsp:param name="state" value="${order.stateAsString}"/>
          <dsp:param name="elementName" value="stateDescription"/>
          <dsp:oparam name="output">
            <dsp:droplet name="IsHighlightedState">
            <dsp:param name="obj" value="${order}"/>
            <dsp:oparam name="true">
              <span class="atg_commerce_csr_dataHighlight atg-csc-base-table-cell"><dsp:valueof param="stateDescription"></dsp:valueof></span>
            </dsp:oparam>        
            <dsp:oparam name="false">
              <dsp:valueof param="stateDescription"></dsp:valueof>
            </dsp:oparam>        
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </div>
      <div class="atg_commerce_csr_orderSubmitDate atg-csc-base-table-row">
        <c:if test="${isMultiSiteEnabled == true}">
            <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-first-label"><fmt:message key='intrinsicAttributes.orderSite.field'/></span>
            <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
              <dsp:droplet name="GetSiteDroplet">
                <dsp:param name="siteId" value="${order.siteId}"/>
                <dsp:oparam name="output">
                  <dsp:tomap var="site" param="site"/>
                  <dsp:valueof value="${site.name}"/>
                </dsp:oparam>
                </dsp:droplet>
            </span>
        </c:if>    

        <dsp:droplet name="IsOrderSubmitted">
        <dsp:param name="order" value="${order}"/>
        <dsp:oparam name="true">
            <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label"><fmt:message key='intrinsicAttributes.orderSubmissionDate.field'/></span>
            <web-ui:formatDate value="${order.submittedDate}" type="both" dateStyle="medium" timeStyle="short"/>
        </dsp:oparam>        
        <dsp:oparam name="false">
            <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label"><fmt:message key='intrinsicAttributes.dateLastModified.field'/></span>
            <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
                <web-ui:formatDate value="${order.lastModifiedDate}" type="both" dateStyle="medium" timeStyle="short"/>
            </span>
        </dsp:oparam>
        </dsp:droplet>
      </div>
      <dsp:droplet name="IsEmpty">
      <dsp:param name="value" value="${order.createdByOrderId}"/>
      <dsp:oparam name="false">
        <dsp:droplet name="OrderItemLookup">
        <dsp:param name="id" value="${order.createdByOrderId}"/>
        <dsp:oparam name="output">
          <dsp:tomap var="orderItem" param="element"/>
          <div class="atg-csc-base-table-row">
            <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-first-label"><fmt:message key='intrinsicAttributes.submittedFrom.field'/></span>
            <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell"><a href="#" onclick="atg.commerce.csr.order.viewExistingOrder('${order.createdByOrderId}','${orderItem.state}')">${order.createdByOrderId}</a></span>
          </div>
        </dsp:oparam>        
        </dsp:droplet>
      </dsp:oparam>        
      </dsp:droplet>
    <div class="atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-first-label"> </span>
        <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
        </span>
    	<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label">Contact Email:</span>
    	<span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">${order.contactEmail}</span>
    	<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-label">Returnable PPS:</span>
    	<span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
		  <dsp:form method="post" name="${returnablePPSform}" id="${returnablePPSform}" formid="${returnablePPSform}">
			<dsp:input bean="MFFCSCTasksOnOrderFormHandler.orderId" type="hidden" value="${order.id}" />
			<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderSuccessUrl" type="hidden" value="${successErrorURL}" />
			<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderErrorUrl" type="hidden" value="${successErrorURL}" />
			<dsp:input type="hidden" priority="-10" value="" bean="MFFCSCTasksOnOrderFormHandler.changeReturnableFlag"/>
			<dsp:getvalueof var="currentValue" value="${order.returnablePPS}"/>
			<dsp:select id="returnInPPS" name="returnInPPS" bean="MFFCSCTasksOnOrderFormHandler.returnableFlag" onchange="submitChangeReturnablePPSFlag();" iclass="input-small">
				<dsp:option value="true" selected="${currentValue == 'true'}">true</dsp:option>
				<dsp:option value="false" selected="${currentValue == 'false'}">false</dsp:option>
			</dsp:select>
		  </dsp:form>
    	</span>
    </div>
    <c:if test="${cart.current.id == order.id}">
	    <div class="atg-csc-base-table-row">
	        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-order-view-first-label"> </span>
	        <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
	        </span>
	        <span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
	        </span>
	    	<span class="atg_commerce_csr_fieldData atg-csc-base-table-cell">
	    	<a href="#" onclick="orderConfirmEmail()">Re-Send Order Confirmation Email</a></span>
	    	<dsp:form style="display:none" action="#" id="orderConfirmEmailForm" formid="orderConfirmEmailForm">
	    		<dsp:input type="hidden" value="${successErrorURL}"bean="CommitOrderFormHandler.commitOrderUpdatesErrorURL" />          
	        	<dsp:input type="hidden" value="${successErrorURL}"bean="CommitOrderFormHandler.commitOrderUpdatesSuccessURL" /> 
	    		<dsp:input type="hidden" priority="-10" value="" bean="CommitOrderFormHandler.sendOrderConfirmationEmail"/>
	  		</dsp:form>
	     </div>
	</c:if>
	</div>
  </div>
  
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/orderViewUIFragment.jsp#1 $$Change: 946917 $--%>
