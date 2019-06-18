<%--
Display the buttons that are needed by finishOrder.jsp

Expected params
currentOrder : The order.

@version $Id: 
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">
  
  <dsp:importbean var="urlDroplet" bean="/atg/svc/droplet/FrameworkUrlDroplet" />  
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
    
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">  
  
  <%-- Create Return/Exchange Success and Error Urls--%>
  <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
   <dsp:oparam name="true">
    <dsp:getvalueof var="processName" param="returnRequest.processName"/>
      <c:choose>
        <%-- Dealing with a Return--%>
        <c:when test="${processName == 'Return'}">
          <svc-ui:frameworkUrl var="successURL" panelStacks="cmcConfirmReturnPS,globalPanels"/>
        </c:when>
        <%-- Dealing with an Exchange--%>
        <c:otherwise>
          <svc-ui:frameworkUrl var="successURL" panelStacks="cmcConfirmOrderPS,globalPanels"/>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
      
  <%-- Create success/error urls --%>
  <svc-ui:frameworkUrl var="cancelReturnRequestSuccessURL" panelStacks="cmcExistingOrderPS,globalPanels"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcCompleteReturnPS"/>
  <%-- Return/Exchange form --%>
  <div class="atg_commerce_csr_panelFooter">    
    <dsp:form 
      id="atg_commerce_csr_submitReturnForm"
      formid="atg_commerce_csr_submitReturnForm">          
      
      <dsp:input type="hidden" priority="-10" value=""
                 name="handleSubmitReturnRequest" 
                 bean="ReturnFormHandler.confirmReturn" />
                  
      <dsp:input type="hidden" value="${errorURL }"
        bean="ReturnFormHandler.confirmReturnErrorURL" />
  
      <dsp:input type="hidden" value="${successURL }"
        bean="ReturnFormHandler.confirmReturnSuccessURL" />  
        
      <input type="button" name="atg_commerce_csr_submitReturnButton" 
                    name="atg_commerce_csr_submitReturnButton"
                    onclick="atg.commerce.csr.order.returns.submitReturn(); return false;"
                    value="<fmt:message key='common.submitReturn'/>"/>     
                    
      <dsp:input bean="ReturnFormHandler.cancelReturnRequestSuccessURL" value="${cancelReturnRequestSuccessURL}" type="hidden" />
      <dsp:input bean="ReturnFormHandler.cancelReturnRequestErrorURL"   value="${errorURL}" type="hidden" />
      <dsp:input name="handleCancelReturnRequest" bean="ReturnFormHandler.cancelReturnRequest" type="hidden" priority="-10" value="" />                        
        
      <input type="button" name="atg_commerce_csr_cancelReturnButton" 
                    name="atg_commerce_csr_cancelReturnButton"
                    onclick="atg.commerce.csr.order.returns.cancelReturnRequestInCompletePage(); return false;"
                    value="<fmt:message key='common.cancel'/>"/>
    </dsp:form>
             
  </div>      
 
  </dsp:layeredBundle>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/finishReturnExchangeButtons.jsp#1 $$Change: 946917 $--%>
