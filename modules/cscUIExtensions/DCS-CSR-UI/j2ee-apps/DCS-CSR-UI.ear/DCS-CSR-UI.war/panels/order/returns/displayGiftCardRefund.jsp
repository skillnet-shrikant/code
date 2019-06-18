<%--
Display the appropriate details for the gift card
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">


   <dsp:getvalueof var="propertyName" param="propertyName"/>
   <dsp:getvalueof var="displayValue" param="displayValue"/>
   <dsp:getvalueof var="displayHeading" param="displayHeading"/>
   <dsp:getvalueof var="paymentGroup" param="refundMethod.paymentGroup"/>
   <dsp:getvalueof var="returnRequest" param="returnRequest"/>

   <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

   <c:if test="${propertyName == 'value1'}">
      <c:if test="${displayHeading == true}">
         <fmt:message key='billingSummary.commerceItem.header.type'/>
      </c:if>
      <c:if test="${displayValue == true}">
         Gift Card
      </c:if>
   </c:if>


   <c:if test="${propertyName == 'value2'}">
      <c:if test="${displayHeading == true}">
         Card Number
      </c:if>
      <c:if test="${displayValue == true}">
      	 <c:choose>
      	 	<c:when test="${empty paymentGroup.cardNumber}">
      	 		To be issued
      	 	</c:when>
      	 	<c:otherwise>
      	 		<c:out value="${paymentGroup.cardNumber}"/>
      	 	</c:otherwise>
      	 </c:choose>
      </c:if>
   </c:if>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
	<c:out value="${exception}" />
</c:if>