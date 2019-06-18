<%--
 A page fragment that displays a resourced value for display values in the endeca result,such as refinement menu display names or sort option labels. 

 @param contentItem - The Endeca content item containing the value to display
 @param key - the key used to look up the property name on the content item from the Configuruation's endecaResourcedValuePropertyNames Map
 @param propertyName - the property name on the content item. If not provided, the key, used to find the property name, must be provided. 

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayEndecaResourcedValue.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:getvalueof param="contentItem" var="contentItem"/>
<dsp:getvalueof param="propertyName" var="propertyName"/>
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:getvalueof var="showRawEndecaResourcedValues" value="${endecaConfig.showRawEndecaResourcedValues}"/>

<c:if test="${empty propertyName}">
	<dsp:getvalueof var="key" param="key"/>
	<dsp:getvalueof var="propertyName" value="${endecaConfig.endecaResourcedValuePropertyNames[key]}"/>
</c:if>

<c:choose>
	<c:when test="${showRawEndecaResourcedValues}">
	  <dsp:getvalueof var="displayValue" param="contentItem.${propertyName}"/>
	</c:when>
	<c:otherwise>
	  <c:if test="${!empty propertyName}">
	    <dsp:layeredBundle basename="${endecaConfig.endecaResourcedValuesResourceBundle}">
	
	    <dsp:getvalueof var="displayValueKey" param="contentItem.${propertyName}"/>
	    <fmt:message var="displayValue" key="${displayValueKey}"/>
	
	   </dsp:layeredBundle>
	  </c:if>
	</c:otherwise>
</c:choose>

<c:out value="${displayValue}"/>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayEndecaResourcedValue.jsp#1 $$Change: 946917 $--%>
