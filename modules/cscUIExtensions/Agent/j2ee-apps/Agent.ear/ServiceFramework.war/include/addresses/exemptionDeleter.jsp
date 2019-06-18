<%--
Exemption Deleter

@nickname - The nickname of the customer exemption
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <svc-ui:frameworkPopupUrl var="url"
    value="/include/addresses/exemptionDeleter.jsp"
    context="/agent"
    nickname="${param.nickname}"
    windowId="${windowId}"/>
  <svc-ui:frameworkPopupUrl var="successUrl"
    value="/include/addresses/exemptionDeleter.jsp"
    context="/agent"
    nickname="${param.nickname}"
    success="true"
    windowId="${windowId}"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <h4>
      <fmt:message key="address.deleter.confirmDelete"/>
    </h4>
    <div class="atg_commerce_csr_popupPanel atg_commerce_csr_addressFormPopup">
      <dspel:form method="post" id="profileExemptionDeleterForm" formid="profileExemptionDeleterForm">
        <dspel:setvalue bean="CustomerProfileFormHandler.nickname" value="${param.nickname}"/>
        <dspel:input type="hidden" bean="CustomerProfileFormHandler.nickname" value="${param.nickname}"/>
        <dspel:input type="hidden" value="${successUrl}" bean="CustomerProfileFormHandler.exemptionSuccessUrl"/>
        <dspel:input type="hidden" value="${url}" bean="CustomerProfileFormHandler.exemptionErrorUrl"/>
        <dspel:input type="hidden" value="--" priority="-100" id="deleteExemptionAction" bean="CustomerProfileFormHandler.removeTaxExemption"/>
		<div>
		Exemption name: ${param.nickname}
		</div>
        <div class="atg_commerce_csr_panelFooter">
          <input value="<fmt:message key='address.delete.label'/>" 
            type="button" id="deleteChoice"
            onClick="
              atgSubmitPopup({url: '${url}', 
                form: document.getElementById('profileExemptionDeleterForm'),
                popup: getEnclosingPopup('exemptionDeletePopup')});
              return false;"/>
          <input value="<fmt:message key='address.cancel.label'/>" 
            type="button" id="cancelChoice"
            onClick="hidePopupWithResults('exemptionDeletePopup', {result:'cancel'}); 
            return false;"/>
        </div>
      </dspel:form>
    </div>
    <c:if test="${param.success}">
      <script type="text/javascript">
        hidePopupWithResults( 'exemptionDeletePopup', {result : 'delete'});
      </script>
    </c:if>
  </dspel:layeredBundle>
</dspel:page>
