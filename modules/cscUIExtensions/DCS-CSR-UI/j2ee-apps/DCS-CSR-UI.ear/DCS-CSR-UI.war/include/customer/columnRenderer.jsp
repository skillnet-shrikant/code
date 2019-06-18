<%--
 Customer Data Column Renderer
 Colums overrided in CSC
 
 customerItemMap - The current customer item being rendered
 field - The current field being rendered
 resourceBundle - The Resource Bundle from where the resource keys are defined
 resourceKey - The key to that maps to the resource string
 isPopup - Indicates if the search Table is a popup, such as the customer search from the shopping cart page
 isHeading - Indicates if a heading is to be rendered or not
 selectLinkPanelStack - The panel stack that shall be loaded when the select link is clicked in the search results
 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/customer/columnRenderer.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>
  
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean var="agentUIConfig"
        bean="/atg/svc/agent/ui/AgentUIConfiguration" />
  <dsp:importbean bean="/atg/svc/agent/customer/CustomerPanelConfig"
        var="customerPanelConfig" />     
  <dsp:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dsp:getvalueof var="field" param="field"/>
  <dsp:getvalueof var="customerItemMap" param="currentCustomer"/>
  <dsp:getvalueof var="imageOpen" param="imageOpen" />
  <dsp:getvalueof var="imagePath" param="imagePath" />
  <dsp:getvalueof var="resourceBundle" param="resourceBundle" />
  <dsp:getvalueof var="resourceKey" param="resourceKey" />
  <dsp:getvalueof var="isPopup" param="isPopup" />
  <dsp:getvalueof var="isHeading" param="isHeading" />
  <c:if test="${empty isHeading}">
    <c:set var="isHeading" value="false" />
  </c:if>  
  <c:if test="${empty isPopup}">
    <c:set var="isPopup" value="false" />
  </c:if>
  <dsp:getvalueof var="selectLinkPanelStack" param="selectLinkPanelStack" />

  <c:choose>
        <c:when test="${field=='toggleLink' and isHeading=='false'}">
          <img id="<c:out value="${customerItemMap.id}"/>"src="<c:out value="${imagePath}"/><c:out value="${imageOpen}"/>"/>
            <div dojoType="dijit.Tooltip" connectId="<c:out value="${customerItemMap.id}"/>">
            <dsp:include src="/panels/customer/searchResultsDetails.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="customerId" value="${customerItemMap.id}" />
            </dsp:include>
            </div> 
        </c:when>
  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/customer/columnRenderer.jsp#1 $$Change: 946917 $--%>
