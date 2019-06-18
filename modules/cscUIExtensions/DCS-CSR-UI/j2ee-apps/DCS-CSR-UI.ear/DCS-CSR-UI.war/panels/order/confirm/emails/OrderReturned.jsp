<%@ include file="/include/top.jspf"%>

<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnsDataHolder" var="returnsDataHolder"/>

<%--
    This is just a sample email template. The whole csc.css is included here.
    It is advisable to use just the necessary css templates to improve the mass email
    performances.
  --%>

<style type="text/css">
<%@ include file="../../../../css/csc.css" %>
</style>

<%/* render email content in the provided customer display locale */%>
<dsp:getvalueof var="localeString" param="confirmationInfo.customerDisplayLocaleString"/>
<c:if test="${!empty localeString}">
  <fmt:setLocale value="${localeString}" scope="request"/>
</c:if>
<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
<fmt:message key="confirmReturn.emails.returnIsComplete" var="returnIsCompleteMessage"/>
<dsp:setvalue value="${returnIsCompleteMessage}" param="messageSubject"/>
<%--
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="OrderShipped" param="mailingName"/>

<dsp:setvalue paramvalue="confirmationInfo.order" param="order"/>
<dsp:setvalue paramvalue="confirmationInfo.profile" param="profile"/>
<dsp:setvalue paramvalue="confirmationInfo.extraData.returnRequest" param="returnRequest"/>

<p>
<dsp:getvalueof param="profile.firstName" var="profileFirstName"><fmt:message key="confirmReturn.emails.valuedCustomer"/></dsp:getvalueof>
<dsp:getvalueof param="profile.lastName" var="profileLastName"/>
<fmt:message key="confirmReturn.emails.dearCustomer">
  <fmt:param value="${fn:escapeXml(profileFirstName)}"/>
  <fmt:param value="${fn:escapeXml(profileLastName)}"/>
</fmt:message>

<br/><fmt:message key="confirmReturn.emails.receivedReturnRequest"/>
<dsp:getvalueof param="order.id" var="orderIdVar"/>
<p>
<b><fmt:message key="confirmReturn.emails.reviewReturnDetails">
  <fmt:param value="${orderIdVar}"/>
</fmt:message></b>
<dsp:getvalueof param="returnRequest" var="returnObject"/>
   <table cellspacing=2 cellpadding=0 border=0>
     <c:if test="${returnObject.exchangeProcess}">
       <tr>
         <td><b><fmt:message key="confirmReturn.emails.orderConfirmationNumber"/></b></td>
         <td>${fn:escapeXml(returnObject.replacementOrder.id)}</td>
       </tr>
     </c:if>
     <tr>
       <td><b><fmt:message key="confirmReturn.emails.returnAuthorizationNumber"/></b></td>
       <td>${fn:escapeXml(returnObject.authorizationNumber)}</td>
     </tr>
     <tr>
       <td><b><fmt:message key="confirmReturn.emails.submitted"/></b></td>
       <td><web-ui:formatDate type="both" value="${returnObject.authorizationDate}" dateStyle="full" timeStyle="full"/></td>
     </tr>
     <c:if test="${returnObject.returnProcess}">
       <tr>
         <td valign="top"><b><fmt:message key="confirmReturn.emails.refunds"/></b></td>
         <td>
           <dl>
           <c:forEach var="refund" items="${returnObject.refundMethodList}">
             <dt>
               <c:choose>
                 <c:when test="${refund.refundType == 'creditCard' }">
                   <c:out value="${refund.creditCard.creditCardType}"/> <c:out value="${refund.creditCardSuffix}"/>
                 </c:when>
                 <c:when test="${refund.refundType == 'storeCredit' }">
                   <fmt:message key="confirmReturn.emails.refundTypes.storeCredit" />
                 </c:when>
                 <c:otherwise><fmt:message key="confirmReturn.emails.refundTypes.other"/></c:otherwise>
               </c:choose>
             </dt>
             <dd><csr:formatNumber value="${refund.amount}" type="currency"
                  currencyCode="${returnObject.order.priceInfo.currencyCode}"/>
             </dd>
           </c:forEach>
           </dl>
         </td>
       </tr>
     </c:if>
   </table>

<hr>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include src="/panels/order/confirm/emails/DisplayOrderSummary.jsp" otherContext="${CSRConfigurator.contextRoot}"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Returned items -->
<dsp:include src="/panels/order/confirm/emails/DisplayReturnItems.jsp" otherContext="${CSRConfigurator.contextRoot}"><dsp:param name="returnObject" value="${returnObject}"/></dsp:include>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/OrderReturned.jsp#2 $$Change: 1179550 $--%>
