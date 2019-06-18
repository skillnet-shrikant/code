<%@  include file="/include/top.jspf" %>
<%@ page import= "atg.core.util.StringUtils"   %>

<%--
   Renders from address for an inbound message. This is simlar to customerNameRenderer,
   but if the customer name is empty it should use the from from address of the message.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/fromAddressSourceColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <c:choose>
    <c:when test="${ ! empty activity[customerDetails.fullName] }">
      <c:set var="customerName" value="${ activity[customerDetails.fullName] }"/>
    </c:when>
    <c:otherwise>
      <c:set var="customerName" value="${activity[customerDetails.firstName]} ${activity[customerDetails.lastName]}"/>
    </c:otherwise>
  </c:choose>
  <c:set var="fromAddress" value="${activity.fromAddress}"/>
  <%				
		// JW TODO - Replace this nasty scriptlet with somthing nicer :-)
		String customerName = (String) pageContext.findAttribute("customerName");	
		String fromAddress = (String) pageContext.findAttribute("fromAddress");	
		String result;
		if (!StringUtils.isBlank(customerName)){
		  result=customerName;
		}
		else {
		  result=fromAddress;
		}

	  pageContext.setAttribute("result", result);		
	%>
	<dspel:img src="${UIConfig.contextRoot}${activityInfo.sourceIcon}" width="21" height="21" align="absmiddle"/>
	<c:out value="${result}"/>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/fromAddressSourceColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/fromAddressSourceColRenderer.jsp#1 $$Change: 946917 $--%>
