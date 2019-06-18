<%--
 This page defines the customer search results panel
 
isPopup - Indicates if the search Table is a popup, such as the customer search from the shopping cart page
 selectLinkPanelStack - The panel stack that shall be loaded when the select link is clicked in the search results

 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/searchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">

	<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
		<dspel:importbean
			bean="/atg/svc/agent/ui/CustomerProfileSearchUIConfiguration"
			var="tableConfig" />
		<dspel:importbean var="formHandler"
			bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler" />
			
		<dspel:importbean var="agentSearchRequestTracker" bean="/atg/svc/agent/ui/AgentProfileSearchRequestTracker" /> 	
		<dspel:getvalueof var="isPopup" param="isPopup" />
		<c:if test="${empty isPopup}">
			<c:set var="isPopup" value="false" />
		</c:if>

		<c:if test="${isPopup}">
			<div style="width: 100%; height: 100%; overflow-y: auto">
		</c:if>
		<c:if test="${!isPopup}">
			<div class="atg_commerce_csr_content">
		</c:if>
		<dspel:getvalueof var="searchResponse"
			value="${agentSearchRequestTracker.searchResponse}" />
    <dspel:getvalueof var="selectLinkPanelStack" param="selectLinkPanelStack"/>
    <c:if test="${empty selectLinkPanelStack}">
      <c:set var="selectLinkPanelStack" value="['globalPanels']" />  
    </c:if>
    <dspel:getvalueof var="selectLinkPanelStack" param="selectLinkPanelStack"/>
    <c:if test="${empty selectLinkPanelStack}">
      <c:set var="selectLinkPanelStack" value="['globalPanels']" />  
    </c:if>
		<c:choose>
			<c:when test="${null != searchResponse}">
				<dspel:getvalueof var="items" value="${searchResponse.items}" />
				<svc-agent:arrayListSize array="items" param="size" />
				<c:choose>
					<c:when test="${size != 0}">
						<%-- customerDetailForm --%>
						<dspel:importbean
							bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler" />
						<dspel:form action="#" id="customerDetailForm"
							formid="customerDetailForm" style="display:none">
							<dspel:input type="hidden" priority="-10" value=""
								bean="FrameworkBaseFormHandler.transform" />
							<dspel:input type="hidden" name="customerId" value=""
								bean="FrameworkBaseFormHandler.parameterMap.customerId" />
						</dspel:form>
						<dspel:form action="#" id="linkCustomerForm"
							formid="linkCustomerForm" style="display:none">
							<dspel:input type="hidden" priority="-10" value=""
								bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.changeEnvironment" />
							<dspel:input type="hidden" name="linkCustomerId"
								bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.inputParameters.changeProfileId"
								iclass="tickets" />
						</dspel:form>
						<dspel:importbean
							bean="/atg/svc/agent/customer/CustomerPanelConfig"
							var="customerPanelConfig" />
						<dspel:include
							src="${UIConfig.contextRoot }/panels/customer/searchResultsPaging.jsp">
							<dspel:param name="formHandler" value="${formHandler}" />
							<dspel:param name="searchResponse"
								value="${searchResponse}" />
						</dspel:include>
						<dspel:include src="${tableConfig.tablePage.URL}"
							otherContext="${tableConfig.tablePage.servletContext}">
							<dspel:param name="tableConfig" value="${tableConfig}" />
							<dspel:param name="searchResponse"
								value="${searchResponse}" />
							<dspel:param name="isPopup" value="${isPopup}" />
							<dspel:param name="selectLinkPanelStack" value="${selectLinkPanelStack}" />
						</dspel:include>
					</c:when>		
					<c:otherwise>
						<fmt:message key="customer.results.none" />
					</c:otherwise>
				</c:choose>
			</c:when>

			<c:otherwise>
				<div style="padding-left:5px"><fmt:message key="searchResults.noSearch.label" /></div>
			</c:otherwise>
		</c:choose>
		</div>
	</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/searchResults.jsp#1 $$Change: 946917 $--%>

