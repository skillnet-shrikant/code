<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
	<dsp:getvalueof var="order"
					bean="/atg/commerce/custsvc/order/ViewOrderHolder.current"/>
	<c:choose>				
	<c:when test ="${envTools.sendInvoiceTabOn == 'true' }">
		<dsp:layeredBundle basename="atg.commerce.csr.order.invoice.WebAppResources">
		<div class="atg_commerce_csr_subPanel">				
			<div class="atg_svc_subPanelHeader" >       
		       <ul class="atg_svc_panelToolBar">
		         <li class="atg_svc_header">
		           <h4 id="atg_commerce_csr_invoice_sendInvoice_header"><fmt:message key='invoice.confirm.label.sendInvoice'/> </h4>
		             </li>       
		       </ul>
		     </div>				
			<script type="text/javascript">
			  var validateCustomerEmail = function () {
			    var disable = false;      
			    if (!dijit.byId("atg_commerce_csr_invoice_invoiceEmailConfirm").isValid())  disable = true;  
			    dojo.byId("csrSendInvoiceMessage").sendInvoiceEmail.disabled = disable;
			  }
			  _container_.onLoadDeferred.addCallback(function() {
			    validateCustomerEmail();
			    atg.service.form.watchInputs('csrSendInvoiceMessage', validateCustomerEmail);
			        
			    atg.keyboard.registerDefaultEnterKey({form:"csrSendInvoiceMessage", name:"atg_commerce_csr_confirm_toAddress"}, 
			      dijit.byNode(dojo.byId("csrSendInvoiceMessage")["atg_commerce_csr_sendInvoiceMessageButton"]),"buttonClick");
			  });
			  _container_.onUnloadDeferred.addCallback(function() {
			    atg.service.form.unWatchInputs('csrSendInvoiceMessage');
			    atg.keyboard.unRegisterDefaultEnterKey({form:"csrSendInvoiceMessage", name:"atg_commerce_csr_confirm_toAddress"});
			  });
			</script>
			<ul>
				<li>
		      
			        <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels" tab="commerceTab"/>
			        <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
			        <dsp:form id="csrSendInvoiceMessage" formid="csrSendInvoiceMessage">
			
			          <fmt:message key='invoice.email.label.emailAddress'/>
			
			        <dsp:input 
			          id="atg_commerce_csr_invoice_invoiceEmailConfirm"
			          type="text"
			          bean="/atg/commerce/custsvc/invoice/InvoiceFormHandler.emailAddress"          
			          name="atg_commerce_csr_invoice_invoiceEmailConfirm"
			          value="${order.contactEmail}"
			          maxlength="100"
			          size="40">  
			          <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
			          <dsp:tagAttribute name="validator" value="dojox.validate.isEmailAddress"/>
			          <dsp:tagAttribute name="required" value="true" />
			          <dsp:tagAttribute name="trim" value="true" />    
			          <dsp:tagAttribute name="class" value="input-large" />    
			        </dsp:input> 
			        
			          <%-- Send Button --%>
			            <dsp:input name="successURL" bean="/atg/commerce/custsvc/invoice/InvoiceFormHandler.sendInvoiceSuccessURL" value="${successURL}" type="hidden" />
						<dsp:input name="errorURL" bean="/atg/commerce/custsvc/invoice/InvoiceFormHandler.sendInvoiceErrorURL"  value="${errorURL}" type="hidden" />
			            <dsp:input type="hidden" priority="-10" value="sendInvoice" bean="/atg/commerce/custsvc/invoice/InvoiceFormHandler.sendInvoice"/>
			            <dsp:input name="orderNumber" type="hidden" value="${order.orderNumber}" bean="/atg/commerce/custsvc/invoice/InvoiceFormHandler.orderNumber"/>
			            <dsp:input name="orderId" type="hidden" value="${order.id}" bean="/atg/commerce/custsvc/invoice/InvoiceFormHandler.orderId"/>
			            <input id="sendInvoiceEmail" type="button" 
			                   name="atg_commerce_csr_sendInvoiceMessageButton" 
			                   onclick="atg.commerce.csr.order.invoice.sendInvoiceEmail();return false;"
			                   dojoType="atg.widget.validation.SubmitButton"
			                   value="<fmt:message key='invoice.confirm.button.sendEmail'/>"
			                   tabindex="10"/>
			        </dsp:form>
			      </li>
			    </ul>
		    </div>
			
			</dsp:layeredBundle>
		</c:when>
		<c:otherwise>
			This feature is currently disabled.
		</c:otherwise>
		</c:choose>

</dsp:page>