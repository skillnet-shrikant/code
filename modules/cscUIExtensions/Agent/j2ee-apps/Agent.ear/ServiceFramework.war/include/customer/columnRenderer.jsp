<%--
 Customer Data Column Renderer
 
 customerItemMap - The current customer item being rendered
 field - The current field being rendered
 resourceBundle - The Resource Bundle from where the resource keys are defined
 resourceKey - The key to that maps to the resource string
 isPopup - Indicates if the search Table is a popup, such as the customer search from the shopping cart page
 isHeading - Indicates if a heading is to be rendered or not
 selectLinkPanelStack - The panel stack that shall be loaded when the select link is clicked in the search results
 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/columnRenderer.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%><%@ include file="/include/top.jspf"%>
<dspel:page>
  <dspel:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration" />
  <dspel:importbean bean="/atg/svc/agent/customer/CustomerPanelConfig" var="customerPanelConfig" />     
  <dspel:getvalueof var="field" param="field"/>
  <dspel:getvalueof var="customerItemMap" param="currentCustomer"/>
  <dspel:getvalueof var="imageClosed" param="imageClosed" />
  <dspel:getvalueof var="imageOpen" param="imageOpen" />
  <dspel:getvalueof var="imagePath" param="imagePath" />
  <dspel:getvalueof var="field" param="field" />
  <dspel:getvalueof var="resourceBundle" param="resourceBundle" />
  <dspel:getvalueof var="resourceKey" param="resourceKey" />
  <dspel:getvalueof var="isPopup" param="isPopup" />
  <dspel:getvalueof var="isHeading" param="isHeading" />
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <c:if test="${empty isHeading}">
      <c:set var="isHeading" value="false" />
    </c:if>  
    <c:if test="${empty isPopup}">
      <c:set var="isPopup" value="false" />
    </c:if>
    <dspel:getvalueof var="selectLinkPanelStack" param="selectLinkPanelStack" />
    <c:choose>
      <c:when test="${field=='toggleLink' and isHeading=='false'}">
        <img id="<c:out value="${customerItemMap.id}"/>"src="<c:out value="${imagePath}"/><c:out value="${imageOpen}"/>"/>
          <div dojoType="dijit.Tooltip" connectId="<c:out value="${customerItemMap.id}"/>">
          <dspel:importbean bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler"/>
          <dspel:include src="/panels/customer/searchResultsDetails.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="customerId" value="${customerItemMap.id}" />
          </dspel:include>
          </div> 
      </c:when>

      <c:when test="${field=='viewLink' and isHeading}">
        <dspel:include src="/panels/customer/customerSearchResultSortHeading.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="resourceBundle" value="${resourceBundle}"/>
          <dspel:param name="resourceKey" value="${resourceKey}"/>
          <dspel:param name="fieldName" value="login"/>
        </dspel:include>
      </c:when>
              
      <c:when test="${field=='viewLink' and isHeading=='false'}">
        <a href="#" class="blueU"
          title="<fmt:message key="view-customer"/>"
          onclick="viewCustomerFromSearch('<c:out value="${customerItemMap.id}"/>');"
          id="atg_commerce_csr_customer_viewMoreCustomerInfotheCustomerId"><c:out
          value="${customerItemMap.login}" /></a>
      </c:when>

      <c:when test="${field=='lastName' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dspel:include src="/panels/customer/customerSearchResultSortHeading.jsp" otherContext="${UIConfig.contextRoot}">
              <dspel:param name="resourceBundle" value="${resourceBundle}"/>
              <dspel:param name="resourceKey" value="${resourceKey}"/>
              <dspel:param name="fieldName" value="lastName"/>
            </dspel:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
      
      <c:when test="${field=='lastName' and isHeading=='false'}">
        <c:out value="${customerItemMap.lastName}" />
      </c:when>

      <c:when test="${field=='firstName' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dspel:include src="/panels/customer/customerSearchResultSortHeading.jsp" otherContext="${UIConfig.contextRoot}">
              <dspel:param name="resourceBundle" value="${resourceBundle}"/>
              <dspel:param name="resourceKey" value="${resourceKey}"/>
              <dspel:param name="fieldName" value="firstName"/>
            </dspel:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>          
        <c:when test="${field=='firstName' and isHeading=='false'}">
          <c:out value="${customerItemMap.firstName}" />
        </c:when>

      <c:when test="${field=='phoneNumber' and isHeading}">     
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dspel:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dspel:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

        <c:when test="${field=='phoneNumber' and isHeading=='false'}">
          <dspel:tomap var="address"
            value="${customerItemMap[agentUIConfig.customerAddressPropertyName]}" />
          <c:out value="${address.phoneNumber}" />
        </c:when>

      <c:when test="${field=='email' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dspel:include src="/panels/customer/customerSearchResultSortHeading.jsp" otherContext="${UIConfig.contextRoot}">
              <dspel:param name="resourceBundle" value="${resourceBundle}"/>
              <dspel:param name="resourceKey" value="${resourceKey}"/>
              <dspel:param name="fieldName" value="email"/>
            </dspel:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
    
      <c:when test="${field=='email' and isHeading=='false'}">
        <c:out value="${customerItemMap.email}" />
      </c:when>

      <c:when test="${field=='postalCode' and isHeading}">     
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dspel:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dspel:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='postalCode' and isHeading=='false'}">
        <dspel:tomap var="address"
          value="${customerItemMap[agentUIConfig.customerAddressPropertyName]}" />
        <c:out value="${address.postalCode}" />
      </c:when>

      <c:when test="${field=='selectLink' and isPopup and isHeading=='false'}">
        <a href="#" class="blueU"
          title="<fmt:message key='select-customer'/>"
          onclick="linkCustomerNoSwitch('<c:out value="${customerItemMap.id}"/>',['globalPanels', 'cmcShoppingCartPS']);atg.commerce.csr.common.hidePopupWithReturn('atg_commerce_csr_catalog_customerSelectionPopup');"
          id="atg_commerce_csr_customer_selectCustomertheCustomerId"><fmt:message
          key="table.options.select" /></a>
      </c:when>
      
      <c:when test="${field=='selectLink' and isPopup=='false' and isHeading=='false'}">
        <a href="#" class="blueU"
          title="<fmt:message key='select-customer'/>"
          onclick="linkCustomerNoSwitch('<c:out value="${customerItemMap.id}"/>',['globalPanels']);"
          id="atg_commerce_csr_customer_selectCustomertheCustomerId"><fmt:message
          key="table.options.select" /></a>
      </c:when>
      <c:otherwise>
      </c:otherwise>
    </c:choose>
  </dspel:layeredBundle>
</dspel:page>
<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/columnRenderer.jsp#1 $$Change: 946917 $--%>
