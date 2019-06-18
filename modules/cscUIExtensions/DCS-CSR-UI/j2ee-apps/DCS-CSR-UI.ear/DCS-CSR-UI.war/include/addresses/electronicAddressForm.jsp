<%--
 This page provides the option to add electronic shipping group.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/electronicAddressForm.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>
    <dsp:getvalueof var="formId" param="formId"/>
    <dsp:getvalueof var="addressBean" param="addressBean"/>
    <dsp:getvalueof var="submitButtonId" param="submitButtonId"/>

        <li class="atg_commerce_csr_address">
          <span class="atg_commerce_csr_fieldTitle">
            <label class="atg_messaging_requiredIndicator">
               <span class="requiredStar">*</span>
                  <fmt:message key="newAddress.email" />
            </label>
          </span>

          <dsp:input id="${formId}_emailAddress" type="text" bean="${addressBean}.emailAddress" size="25" maxlength="50">
            <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dsp:tagAttribute name="validator" value="dojox.validate.isEmailAddress"/>
            <dsp:tagAttribute name="trim" value="true" />
            <dsp:tagAttribute name="required" value="true" />
          </dsp:input>
        </li>

      <script type="text/javascript">
        var ${formId}Validate = function () {
          var disable = false;
          <c:if test="${!empty isDisableSubmit}">disable = ${isDisableSubmit}();</c:if>
          <c:if test="${!empty validateIf}">if (${validateIf}) {</c:if>
            if (!dijit.byId("${formId}_emailAddress").isValid()) disable = true;
          <c:if test="${!empty validateIf}">}</c:if>
          dojo.byId("${formId}").${submitButtonId}.disabled = disable;
        };
        _container_.onLoadDeferred.addCallback(function () {
          ${formId}Validate();
          atg.service.form.watchInputs("${formId}", ${formId}Validate);
          atg.keyboard.registerFormDefaultEnterKey("${formId}", "${submitButtonId}", "buttonClick");
        });
        _container_.onUnloadDeferred.addCallback(function () {
          atg.service.form.unWatchInputs('${formId}');
          atg.keyboard.unRegisterFormDefaultEnterKey("${formId}");
        });
      </script>

  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/electronicAddressForm.jsp#2 $$Change: 1179550 $--%>
