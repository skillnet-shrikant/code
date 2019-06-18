<%--
Display the buttons that are needed by finishOrder.jsp

Expected params
currentOrder : The order.
includeForm : A boolean flag.
              These buttons and forms are repeated in the same page and the form repetition is causing the bug 152772.
              In order to avoid this problem, the form repetition is avoided by passing the includeForm flag.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderButtons.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">
  
  <dsp:importbean var="urlDroplet" bean="/atg/svc/droplet/FrameworkUrlDroplet" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/CommitOrderFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CancelOrderFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderSubmitted"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <dsp:getvalueof var="order" param="currentOrder"/>    
  <dsp:getvalueof var="includeForm" param="includeForm"/>    
  
  
  <c:set var="submitButtonName">
    <dsp:droplet name="IsOrderIncomplete">
      <dsp:oparam name="true">
        <fmt:message key='common.submit.order'/>
      </dsp:oparam>
      <dsp:oparam name="false">
        <%-- <fmt:message key='common.update'/> --%>
      </dsp:oparam>
    </dsp:droplet>
  </c:set>
  
  <%-- Check if we are dealing with an Exchange--%>
    <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
     <dsp:oparam name="true">
      <dsp:getvalueof var="processName" param="returnRequest.processName"/>
        <c:choose>
          <%-- Dealing with an Exchange--%>
          <c:when test="${processName == 'Exchange'}">
            <%-- Create success and error urls for Submit Exchange --%>
            <svc-ui:frameworkUrl var="submitSuccessURL" panelStacks="cmcConfirmReturnPS"/>
            <svc-ui:frameworkUrl var="submitErrorURL"/>
            <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
              <fmt:message key='common.submitExchange' var="submitButtonName"/>
            </dsp:layeredBundle>
            <csr:displayCheckoutPanelFooter
            nextIconOnclickURL="atg.commerce.csr.order.finish.submitExchange(); return false;"            
            cancelActionErrorURL="${cancelErrorURL}"
            order="${order}"
            includeForm="${includeForm}"
            submitActionButtonName="${submitButtonName}"
           />
                       
            <%-- Exchange form --%>          
            <c:if test="${includeForm == true}">

            <dsp:form 
              id="atg_commerce_csr_submitExchangeForm"
              formid="atg_commerce_csr_submitExchangeForm">          
              
              <dsp:input type="hidden" priority="-10" value=""
                         name="handleSubmitExchange" 
                         bean="ReturnFormHandler.confirmReturn" />
              <dsp:input type="hidden" value="${submitErrorURL }"
                bean="ReturnFormHandler.confirmReturnErrorURL" />          
              <dsp:input type="hidden" value="${submitSuccessURL }"
                bean="ReturnFormHandler.confirmReturnSuccessURL" />                            
            </dsp:form>
            </c:if>            
          </c:when>
         </c:choose>
      </dsp:oparam>
      <dsp:oparam name="false">
        <%-- Not dealing with an Exchange. Must be a New or Existing order!--%>                 
          <svc-ui:frameworkUrl var="processTemplateSuccessURL" panelStacks="cmcScheduleCreatePS"/>
          <svc-ui:frameworkUrl var="processTemplateErrorURL"/>
          <svc-ui:frameworkUrl var="submitAndScheduleSuccessURL" panelStacks="cmcConfirmOrderWithSchedulePS"/>
          <svc-ui:frameworkUrl var="submitSuccessURL" panelStacks="cmcConfirmOrderPS"/>
          <svc-ui:frameworkUrl var="submitErrorURL"/>
          
          <%-- If the order is Incomplete then just submit it normally. --%>
          <dsp:droplet name="IsOrderIncomplete">
            <dsp:oparam name="true">      
              <csr:displayCheckoutPanelFooter
                nextIconOnclickURL="atg.commerce.csr.order.finish.submitOrder('atg_commerce_csr_finishOrderSubmitForm'); return false;"
                submitAndScheduleOnclickURL="atg.commerce.csr.order.finish.submitAndScehduleOrder('atg_commerce_csr_finishOrderSubmitAndScheduleForm'); return false;"
                scheduleOnclickURL="atg.commerce.csr.order.finish.scheduleOrder('atg_commerce_csr_scheduleOrderForm'); return false;"
                cancelActionErrorURL="${cancelErrorURL}"
                order="${order}"
                includeForm="${includeForm}"
                submitActionButtonName="${submitButtonName}"
               />       
                          

              <%-- Schedule Order Form --%>
              <c:if test="${includeForm == true}">
              
              <dsp:form style="display:none" id="atg_commerce_csr_scheduleOrderForm"
                formid="atg_commerce_csr_scheduleOrderForm">          
                
                <dsp:input type="hidden" priority="-10" value=""
                  bean="CommitOrderFormHandler.processTemplate" />

                <dsp:input type="hidden" value="${processTemplateErrorURL }"
                  bean="CommitOrderFormHandler.processTemplateErrorURL" />
            
                <dsp:input type="hidden" value="${processTemplateSuccessURL }"
                  bean="CommitOrderFormHandler.processTemplateSuccessURL" />      
              </dsp:form> <%-- End Schedule Order Form --%>          
              
              
              <%-- Finish Order Submit Form --%>
              <dsp:form style="display:none" id="atg_commerce_csr_finishOrderSubmitAndScheduleForm"
                formid="atg_commerce_csr_finishOrderSubmitAndScheduleForm">          
                
                <dsp:input type="hidden" priority="-10" value=""
                  bean="CommitOrderFormHandler.commitOrder" />
            
                <dsp:input type="hidden" value="${order.id}" 
                  bean="CommitOrderFormHandler.orderId" />
                
                <dsp:input type="hidden" value="true" name="scheduleOrder" 
                  bean="CommitOrderFormHandler.createTemplateFromSubmittedOrder" />
                  
                <dsp:input type="hidden" value="NEW_ORDER" 
                  bean="CommitOrderFormHandler.templateToUse" />
            
                <dsp:input type="hidden" value="${submitErrorURL }"
                  bean="CommitOrderFormHandler.commitOrderErrorURL" />
            
                <dsp:input type="hidden" value="${submitAndScheduleSuccessURL }"
                  bean="CommitOrderFormHandler.commitOrderSuccessURL" />      
              </dsp:form> <%-- End Finish Order Submit Form --%>          
              <%-- Finish Order Submit Form --%>

              <dsp:form style="display:none" id="atg_commerce_csr_finishOrderSubmitForm"
                formid="atg_commerce_csr_finishOrderSubmitForm">          
                
                <dsp:input type="hidden" priority="-10" value=""
                  bean="CommitOrderFormHandler.commitOrder" />
            
                <dsp:input type="hidden" value="${order.id}" 
                  bean="CommitOrderFormHandler.orderId" />
                
                <dsp:input type="hidden" value="NEW_ORDER" 
                  bean="CommitOrderFormHandler.templateToUse" />
            
                <dsp:input type="hidden" value="${submitErrorURL }"
                  bean="CommitOrderFormHandler.commitOrderErrorURL" />
            
                <dsp:input type="hidden" value="${submitSuccessURL }"
                  bean="CommitOrderFormHandler.commitOrderSuccessURL" />      
              </dsp:form> <%-- End Finish Order Submit Form --%>          
            </c:if>
            </dsp:oparam>
            <dsp:oparam name="false">
              <%-- The order is not Incomplete it must be Submitted so 
              reconcile it. --%>        
              <<!-- <csr:displayCheckoutPanelFooter
              nextIconOnclickURL="atg.commerce.csr.order.finish.submitOrder('atg_commerce_csr_existingOrderReconcileForm'); return false;"
              cancelActionErrorURL="${cancelErrorURL}"
              order="${order}"
              includeForm="${includeForm}"
              submitActionButtonName="${submitButtonName}"
             />
              
              <svc-ui:frameworkUrl var="concurrentUpdateErrorURL" panelStacks="cmcExistingOrderPS,globalPanels"/>
            	<c:if test="${includeForm == true}">
              <%-- Existing Order Reconcile Form --%>
              <dsp:form style="display:none" id="atg_commerce_csr_existingOrderReconcileForm"
                formid="atg_commerce_csr_existingOrderReconcileForm">
                <dsp:input type="hidden" priority="-10" value="${concurrentUpdateErrorURL}" 
                  bean="CommitOrderFormHandler.concurrentUpdateErrorURL" />
                
                <dsp:input type="hidden" priority="-10" value=""
                  bean="CommitOrderFormHandler.commitOrderUpdates" />
            
                <dsp:input type="hidden" value="${order.id}" 
                  bean="CommitOrderFormHandler.orderId" />
                  
                <dsp:input type="hidden" value="ORDER_UPDATE" 
                  bean="CommitOrderFormHandler.templateToUse" />
            
                <dsp:input type="hidden" value="${submitErrorURL }"
                  bean="CommitOrderFormHandler.commitOrderUpdatesErrorURL" />
            
                <dsp:input type="hidden" value="${submitSuccessURL }"
                  bean="CommitOrderFormHandler.commitOrderUpdatesSuccessURL" />          
              </dsp:form>  <%-- End Existing Order Reconcile Form --%> 
              </c:if> -->                         
            </dsp:oparam>
          </dsp:droplet> <%-- End IsOrderIncomplete --%>   
      </dsp:oparam>
    </dsp:droplet>    
  </dsp:layeredBundle>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderButtons.jsp#1 $$Change: 946917 $--%>
