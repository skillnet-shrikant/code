 <%--
 This page renders the forms and the actions to approve and reject an approval
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/approvalsAction.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<script type="text/javascript">
  if (!dijit.byId("approvalRejectPopup")) {
      new dojox.Dialog( {
        id :"approvalRejectPopup",
        cacheContent :"false",
        executeScripts :"true",
        scriptHasHooks :"true"
      });
  }
</script>

<dsp:importbean var="formHandler" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler"/>
  

<dsp:form formid="atg_commerce_csr_approve" id="atg_commerce_csr_approve" method="post">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.approve"/>
  <dsp:input type="hidden" id="approvalId" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.inputParameters.approvalId" value=""/>
  <dsp:input type="hidden" id="approveSuccessUL" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.successURL" value=""/>
  <dsp:input type="hidden" id="approveErrorUL" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.errorURL" value=""/>
  <input type="hidden" id="customerId" name="${formHandler.customerProfileIdParameterName}" value=""/>
</dsp:form>

<dsp:form formid="atg_commerce_csr_reject" id="atg_commerce_csr_reject" method="post">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.reject"/>
  <dsp:input type="hidden" id="rejectApprovalId" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.inputParameters.approvalId" value=""/>
  <dsp:input type="hidden" id="rejectSuccessUL" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.successURL" value=""/>
  <dsp:input type="hidden" id="rejectErrorUL" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalFormHandler.errorURL" value=""/>
  <input type="hidden" id="rejectCustomerId" name="${formHandler.customerProfileIdParameterName}" value=""/>
</dsp:form>

<script type="text/javascript">
  
  function submitApproval(params) {
    dojo.byId("approvalId").value=params.approvalId;
    dojo.byId("customerId").value=params.customerId;
    dojo.byId("approveSuccessUL").value=params.successURL;
    dojo.byId("approveErrorUL").value=params.errorURL;
    var theForm = dojo.byId("atg_commerce_csr_approve");
  
    atgSubmitAction({
      form: theForm
    }); 
  }
  
  function submitReject(params) {
    popupUrl='${CSRConfigurator.contextRoot}' + '/panels/approvals/rejectConfirm.jsp?_windowid=${windowId}';
  
    dojo.byId("rejectSuccessUL").value=params.successURL;
    dojo.byId("rejectErrorUL").value=params.errorURL;
    dojo.byId("rejectApprovalId").value=params.approvalId;
    dojo.byId("rejectCustomerId").value=params.customerId;
  
    atg.commerce.csr.common.showPopupWithReturn({
      popupPaneId: 'approvalRejectPopup',
      title: "<fmt:message key='approvals.rejectConfirm.title'/>",url: popupUrl,
      onClose: function(args) {
        if (args.result == 'reject') {          
          var theForm = dojo.byId("atg_commerce_csr_reject");
          
          atgSubmitAction({
            form: theForm
          }); 
        }
    }});
    return false;
  }
  
  </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/approvalsAction.jsp#2 $$Change: 1179550 $--%>
