<%--
 This page defines the order checkout footer
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/checkoutFooter.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:getvalueof var="goBackLabel" param="goBackLabel" />
    <dsp:getvalueof var="goBackStack" param="goBackStack" />
    <dsp:getvalueof var="nextButtonOnClick" param="nextButtonOnClick" />
    <dsp:getvalueof var="nextButtonLabel" param="nextButtonLabel" />
    <dsp:getvalueof var="nextButtonFormId" param="nextButtonFormId" />
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <a class="atg_commerce_csr_return" href="#"
       onclick="atgSubmitAction({panelStack: '${goBackStack}',
                form: document.getElementById('transformForm')});
    ">${goBackLabel}</a>
    <c:if test="${!empty nextButtonLabel}">
      <input type="button" name="checkoutFooterNextButton" id="checkoutFooterNextButton"
             class="atg_commerce_csr_activeButton"
             onclick="<c:out value='${nextButtonOnClick}'/>"
             value="${nextButtonLabel}" form="${nextButtonFormId}"
             dojoType="atg.widget.validation.SubmitButton"/>
    </c:if>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/checkoutFooter.jsp#1 $$Change: 946917 $--%>
