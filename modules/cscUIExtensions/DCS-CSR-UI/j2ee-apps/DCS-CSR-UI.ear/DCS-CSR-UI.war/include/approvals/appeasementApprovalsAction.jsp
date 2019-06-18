 <%--
 This page renders the forms and the actions to approve or reject an appeasement.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/appeasementApprovalsAction.jsp#1 $
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

<dsp:importbean var="formHandler" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler"/>

<%--
 Set up the form that will be used for the approval of the appeasement
--%>
<dsp:form formid="atg_commerce_csr_approve_appeasement" id="atg_commerce_csr_approve_appeasement" method="post">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.approve"/>
  <dsp:input type="hidden" id="appeasementApprovalId" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.inputParameters.approvalId" value=""/>
  <dsp:input type="hidden" id="appeasementApproveSuccessUL" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.successURL" value=""/>
  <dsp:input type="hidden" id="appeasementApproveErrorUL" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.errorURL" value=""/>
  <input type="hidden" id="appeasementCustomerId" name="${formHandler.customerProfileIdParameterName}" value=""/>
</dsp:form>

<%--
 Set up the form that will be used for the rejection of the appeasement
--%>
<dsp:form formid="atg_commerce_csr_reject_appeasement" id="atg_commerce_csr_reject_appeasement" method="post">
  <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.reject"/>
  <dsp:input type="hidden" id="appeasementRejectApprovalId" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.inputParameters.approvalId" value=""/>
  <dsp:input type="hidden" id="appeasementRejectSuccessUL" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.successURL" value=""/>
  <dsp:input type="hidden" id="appeasementRejectErrorUL" bean="/atg/commerce/custsvc/approvals/appeasement/AppeasementApprovalFormHandler.errorURL" value=""/>
  <input type="hidden" id="appeasementRejectCustomerId" name="${formHandler.customerProfileIdParameterName}" value=""/>
</dsp:form>

<script type="text/javascript">
  
  function submitAppeasementApproval(params) {
	  console.log("submitAppeasementApproval " + params.approvalId);
    dojo.byId("appeasementApprovalId").value=params.approvalId;
    dojo.byId("appeasementCustomerId").value=params.customerId;
    dojo.byId("appeasementApproveSuccessUL").value=params.successURL;
    dojo.byId("appeasementApproveErrorUL").value=params.errorURL;
    var theForm = dojo.byId("atg_commerce_csr_approve_appeasement");
  
    atgSubmitAction({
      form: theForm
    }); 
  }
  
  function submitAppeasementReject(params) {
	console.log("submitAppeasementReject " + params.approvalId);
    popupUrl='${CSRConfigurator.contextRoot}' + '/panels/approvals/appeasementRejectConfirm.jsp?_windowid=${windowId}';
  
    dojo.byId("appeasementRejectSuccessUL").value=params.successURL;
    dojo.byId("appeasementRejectErrorUL").value=params.errorURL;
    dojo.byId("appeasementRejectApprovalId").value=params.approvalId;
    dojo.byId("appeasementRejectCustomerId").value=params.customerId;
  
    atg.commerce.csr.common.showPopupWithReturn({
      popupPaneId: 'approvalRejectPopup',
      title: "<fmt:message key='approvals.appeasements.rejectConfirm.title'/>",url: popupUrl,
      onClose: function(args) {
        if (args.result == 'reject') {          
          var theForm = dojo.byId("atg_commerce_csr_reject_appeasement");
          
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/appeasementApprovalsAction.jsp#1 $$Change: 1179550 $--%>
