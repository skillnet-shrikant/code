<%--

Display a list of address objects
Expected params
labelKey - key of i18n label
addressList - list of Address objects

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/addressList.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="../top.jspf"%>
<dspel:page xml="true">

  <%-- Get EL reference to dspel:param parameters --%>
  <dspel:getvalueof var="labelKey" param="labelKey"/>
  <dspel:getvalueof var="addressList" param="addressList"/>
  <dspel:getvalueof var="displayEdit" param="displayEdit"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <div class="atg-csc-base-table-row">
    <c:if test="${not empty addressList}">
      <dt class="emailActivityHeader atg-csc-base-table-cell">
      	<fmt:message key="${labelKey}" />
      </dt>
      <dd class="atg-csc-base-table-cell">
      	<c:forEach var="address" items="${addressList}" varStatus="status">
	        <c:choose>
	          <c:when test="${not empty address.personalName}">
	            <%-- JW - Implement this as a hover-over when we get the HTML --%>
	            <c:out value="${address.personalName}" /> - <c:out value="${address.address}" />
	          </c:when>
	          <c:when test="${empty address.personalName}">
	            <c:out value="${address.address}" />
	          </c:when>
	        </c:choose>
	        <c:if test="${not status.last}">,&nbsp;</c:if>
  	    </c:forEach>

  	    <c:if test="${!empty displayEdit}">
	        <span class="editbutton">
			    	<a href="#" onclick="">
	    		  	<img alt="" title='<fmt:message key="response.compose.address.editrcpts.label"/>' src="image/iconcatalog/25x22/toolbar/icon_edit.gif" width="25" height="22" />
				    </a>
		  		</span>
		  	</c:if>
      </dd>
    </c:if>
    </div>
  </dspel:layeredBundle>

</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/addressList.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/addressList.jsp#1 $$Change: 946917 $--%>
