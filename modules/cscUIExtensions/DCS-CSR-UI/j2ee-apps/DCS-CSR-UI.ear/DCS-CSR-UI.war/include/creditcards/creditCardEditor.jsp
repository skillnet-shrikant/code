<%--
  Credit Card Editor 

@creditCardId - Optional, the ID of an credit card to edit. If not
  supplied, creates a new credit card.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/creditcards/creditCardEditor.jsp#2 $$Change: 953229 $
@updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean var="creditCardForm" bean="/atg/commerce/custsvc/ui/fragments/CreditCardForm"/>

    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <c:set var="creditCardFormHandlerPath"
        value="/atg/commerce/custsvc/repository/CreditCardFormHandler"/>
      <dsp:importbean var="ccfh" bean="${creditCardFormHandlerPath}"/>
       <c:if test="${not empty param.creditCardId}">
        <dsp:setvalue value="${param.creditCardId}"  bean="CreditCardFormHandler.repositoryId"/>
    <c:set var="ccItem" value="${ccfh.creditCardMetaInfo.creditCard}"/>
    <c:choose>
      <c:when test="${ccItem.repositoryId eq param.creditCardId}">
        <c:set var="isDeleted" value="false"/>
      </c:when>
      <c:otherwise>
        <c:set var="isDeleted" value="true"/>
      </c:otherwise>
    </c:choose>
     </c:if>
      <svc-ui:frameworkPopupUrl var="url"
        value="/include/creditcards/creditCardEditor.jsp"
        context="${CSRConfigurator.contextRoot}"
        creditCardId="${param.creditCardId}"
        windowId="${windowId}"/>
      <svc-ui:frameworkPopupUrl var="successUrl"
        value="/include/creditcards/creditCardEditor.jsp"
        context="${CSRConfigurator.contextRoot}"
        success="true"
        creditCardId="${param.creditCardId}"
        windowId="${windowId}"/>
      <svc-ui:frameworkPopupUrl var="errorUrl"
        value="/include/creditcards/creditCardEditor.jsp"
        context="${CSRConfigurator.contextRoot}"
        success="true"
        windowId="${windowId}"/>
      <script type="text/javascript">
        atg.commerce.csr.order.billing.initializeCreditCardTypeDataContainer();
        <dsp:droplet name="ForEach">
          <dsp:param name="array" bean="/atg/commerce/payment/CreditCardTools.cardCodesMap" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="cardType" param="key"/>
            <dsp:getvalueof var="cardCode" param="element"/>
                atg.commerce.csr.order.billing.addCreditCardTypeData("<c:out value='${cardType}'/>", "<c:out value='${cardCode}'/>");
          </dsp:oparam>
        </dsp:droplet>
      </script>
      <div class="atg_commerce_csr_popupPanel">
        <c:choose>
          <c:when test="${param.success}">
            <script type="text/javascript">
              hidePopupWithResults( 'atg_commerce_csr_editCreditCard', {result : 'save'});
            </script>
          </c:when>
          <c:otherwise>
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param name="targeter" bean="/atg/registry/Slots/UserMessagingSlot"/>
              <dsp:oparam name="outputStart"> 
                <span class="atg_commerce_csr_common_content_alert">
                  <ul>
              </dsp:oparam>
              <dsp:oparam name="output"> 
                <dsp:getvalueof var="details" param="element.messageDetails" />
                <li>${fn:escapeXml(details[0].description)}</li>
              </dsp:oparam>
              <dsp:oparam name="outputEnd"> 
                  </ul>
                </span>
              </dsp:oparam>
              <dsp:oparam name="empty">
              </dsp:oparam>
            </dsp:droplet>
          </c:otherwise>
        </c:choose>
        <c:set var="formId" value="editCreditCardForm"/>
        <dsp:form formid="${formId}" id="${formId}">
      <c:if test="${isDeleted == false or empty isDeleted}">
          <dsp:input type="hidden" value="${successUrl}"
            bean="CreditCardFormHandler.createSuccessURL"/>
          <dsp:input type="hidden" value="${url}"
            bean="CreditCardFormHandler.createErrorURL"/>
          <dsp:input type="hidden" value="${successUrl}"
            bean="CreditCardFormHandler.updateSuccessURL"/>
          <dsp:input type="hidden" value="${errorUrl}"
            bean="CreditCardFormHandler.updateErrorURL"/>
          <c:choose>
            <c:when test="${not empty param.creditCardId}">
              <dsp:setvalue value="${param.creditCardId}"
                 bean="CreditCardFormHandler.repositoryId"/>
              <dsp:input type="hidden" value="${param.creditCardId}"
                 priority="1000" bean="CreditCardFormHandler.repositoryId"/>
              <h2 id="atg_commerce_csr_editCreditCard">
                <fmt:message key="creditCard.editTitle"/>
              </h2>
            </c:when>
            <c:otherwise>
              <h2 id="atg_commerce_csr_editCreditCard">
                <fmt:message key="creditCard.addNewTitle"/>
              </h2>
            </c:otherwise>
          </c:choose>
          <ul class="atg_commerce_csr_paymentForm">
            <dsp:include src="${creditCardForm.URL}" otherContext="${creditCardForm.servletContext}">
              <dsp:param name="formId" value="${formId}"/>
              <dsp:param name="creditCardBean" value="/atg/commerce/custsvc/repository/CreditCardFormHandler.value"/>
              <dsp:param name="creditCardAddressBean" value="/atg/commerce/custsvc/repository/CreditCardFormHandler.newAddressMetaInfo.address"/>
              <dsp:param name="creditCardFormHandler" value="/atg/commerce/custsvc/repository/CreditCardFormHandler"/>
              <dsp:param name="submitButtonId" value="saveChoice"/>
              <dsp:param name="isMaskCardNumber" value="${!empty param.creditCardId ? true : false}"/>
              <dsp:param name="isUseExistingAddress" value="${false}"/>
            </dsp:include>
            <li>
           <c:forEach var="editor" 
              items="${ccfh.creditCardMetaInfo.params.defaultCreditCardEditorOptions}">
              <c:if test="${not empty editor.value.customEditorPage}">
                <dsp:include src="${editor.value.customEditorPage}" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="options" value="${editor.value}"/>
                  <dsp:param name="formHandlerPath" value="${creditCardFormHandlerPath}"/>
                  <dsp:param name="formHandler" value="${ccfh}"/>
                </dsp:include>
              </c:if>
            </c:forEach>          
            </li>
          </ul>
          
          <%-- create action --%>
          <dsp:input type="hidden" value="--" priority="-100"
            name="createCreditCardAction" bean="CreditCardFormHandler.create"/>
          <%-- update action --%>
          <dsp:input type="hidden" value="--" priority="-100"
            name="updateCreditCardAction" bean="CreditCardFormHandler.update"/>
          <dsp:param name="maskedCreditCardNumber" bean="/atg/commerce/custsvc/repository/CreditCardFormHandler.value.creditCardNumber" 
            converter="creditCard" maskcharacter="*" numcharsunmasked="4"/>
          <dsp:getvalueof var="maskedCreditCardNumberVar" param="maskedCreditCardNumber"/>
          <div class="atg_commerce_csr_panelFooter">
            <input value="<fmt:message key='creditCard.save.label'/>" 
              type="button" id="saveChoice" name="saveChoice" dojoType="atg.widget.validation.SubmitButton"
              onClick="<c:choose><c:when test="${empty param.creditCardId ? 'true' : 'false'}">
                  document.getElementById('${formId}').updateCreditCardAction['disabled'] = true;
                  document.getElementById('${formId}').createCreditCardAction['disabled'] = false;
                </c:when><c:otherwise>document.getElementById('${formId}')['/atg/commerce/custsvc/repository/CreditCardFormHandler.value.creditCardNumber']['disabled'] = 
                    ('${maskedCreditCardNumberVar}' == dijit.byId('${formId}_maskedCreditCardNumber').getValue());
                  document.getElementById('${formId}').createCreditCardAction['disabled'] = true;
                  document.getElementById('${formId}').updateCreditCardAction['disabled'] = false;
                </c:otherwise></c:choose>atgSubmitPopup({url: '${url}', 
                  form: document.getElementById('${formId}'),
                  popup: getEnclosingPopup('atg_commerce_csr_editCreditCard')});
                return false;"/>
            <input value="<fmt:message key='creditCard.cancel.label'/>" 
              type="button" name="cancelChoice"
              onClick="hidePopupWithResults('atg_commerce_csr_editCreditCard', {result:'cancel'}); 
              return false;"/>
          </div>
      </c:if>
      <c:if test="${isDeleted}">
        <div  style="font-size:1.1667em; font-weight:bold;">This credit card is already deleted!<br></div>
            <div class="atg_commerce_csr_panelFooter">
            <input value="Return" 
              type="button" name="saveChoice"
              onClick="hidePopupWithResults('creditCardPopup', {result:'save'}); 
              return false;"/>
            </div>        
      </c:if>
        </dsp:form>
      </div>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/creditcards/creditCardEditor.jsp#2 $$Change: 953229 $--%>
