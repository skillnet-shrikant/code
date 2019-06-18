<%--
 This is a confirmation page for changing the order's returnable in pps condition
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  
    <p>
      This action can not be modified. Are you sure you want to change the Returnable in PPS condition? 
    </p>
    
    <div class="atg_commerce_csr_panelFooter">
    <input value="<fmt:message key='approvals.reject.rejectButton'/>" 
      type="button" id="rejectChoice"
      onClick="hidePopupWithResults('approveChangePopup', {result:'confirm'}); 
      return false;"/>
            
    <input value="<fmt:message key='approvals.reject.cancelButton'/>" 
          type="button" id="cancelChoice"
          onClick="hidePopupWithResults('approveChangePopup', {result:'cancel'}); 
          return false;"/>
    </div>
    
  </dsp:layeredBundle>
</dsp:page>
