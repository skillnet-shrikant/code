<%--
 This is a confirmation page for confirming force allocation
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  
    <p>
      This action can not be modified. Are you sure you want to manually allocate this order? 
    </p>
    
    <div class="atg_commerce_csr_panelFooter">
    
    <input value="<fmt:message key='approvals.reject.rejectButton'/>" 
      type="button" id="rejectChoice"
      onClick="hidePopupWithResults('approvalRejectPopup', {result:'reject'}); 
      return false;"/>
            
    <input value="<fmt:message key='approvals.reject.cancelButton'/>" 
          type="button" id="cancelChoice"
          onClick="hidePopupWithResults('approvalRejectPopup', {result:'cancel'}); 
          return false;"/>
          
    </div>
    
  </dsp:layeredBundle>
</dsp:page>
