<%--
This page defines the address view

This page is shared across the entire CSC application. This page is used wherever an address is
displayed such as credit card,hard good shipping group pages.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/addressView.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>	
  <dsp:getvalueof var="address" param="address"/>
  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <c:if test="${!empty address}">
    <c:if test="${!empty address.companyName }">
      <li>
        <c:out value="${address.companyName}"/>
      </li>
    </c:if>
    <li>
      <c:choose>
        <c:when test="${!empty address.middleName }">
          <fmt:message key="customer.name.first.middle.last">
            <fmt:param value="${fn:escapeXml(address.firstName)}"/>
            <fmt:param value="${fn:escapeXml(address.middleName)}"/>
            <fmt:param value="${fn:escapeXml(address.lastName)}"/>
          </fmt:message>
        </c:when>
        <c:otherwise>
          <fmt:message key="customer.name.first.last">
            <fmt:param value="${fn:escapeXml(address.firstName)}"/>
            <fmt:param value="${fn:escapeXml(address.lastName)}"/>
          </fmt:message>
        </c:otherwise>
      </c:choose>
    </li>
    <c:if test="${shippingGroup != null && shippingGroup.specialInstructions != null && not empty shippingGroup.specialInstructions}">
     	<c:forEach var="entry" items="${shippingGroup.specialInstructions}">
     	  <c:if test="${entry.key == 'instructions'}">	
	     	  <li>
		  		Attention : <c:out value="${entry.value}"/>
		  	  </li>
	  	  </c:if>
	</c:forEach>
     </c:if>	
    <c:if test="${!empty address.address1 }">
      <li>
        <c:out value="${address.address1}"/>
      </li>
    </c:if>

    <c:if test="${!empty address.address2 }">
      <li>
        <c:out value="${address.address2}"/>
      </li>
    </c:if>

    <c:if test="${!empty address.city }">
      <li>
        <c:out value="${address.city}"/>
        ,
        <c:if test="${!empty address.state }">
          <c:out value="${address.state}"/>
        ,
        </c:if>
        <c:out value="${address.postalCode}"/>
      </li>
      <li>
        <c:out value="${address.country}"/>
      </li>
    </c:if>

    <c:if test="${!empty address.phoneNumber }">
      <li>
        <c:out value="${address.phoneNumber}"/>
      </li>
    </c:if>
  </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/addressView.jsp#1 $$Change: 946917 $--%>
