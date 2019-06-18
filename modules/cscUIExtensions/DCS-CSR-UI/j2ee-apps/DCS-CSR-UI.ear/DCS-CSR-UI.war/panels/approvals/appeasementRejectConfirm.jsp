<%--
 This is a confirmation page for rejection of an approval


 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/approvals/appeasementRejectConfirm.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  
    <p>
      <fmt:message key='approvals.appeasements.reject.rejectConfirmMessage'/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/approvals/appeasementRejectConfirm.jsp#1 $$Change: 1179550 $--%>
