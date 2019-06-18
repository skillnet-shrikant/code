<%--
 Initializes the customer search results table using the following input parameters:
 tableConfig - the table configuration component
 searchResponse - the response from the search engine
 isPopup - Indicates if the search Table is a popup, such as the customer search from the shopping cart page
 selectLinkPanelStack - The panel stack that shall be loaded when the select link is clicked in the search results
  
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerSearchTable.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>

<c:catch var="exception">
	<dspel:page xml="true">
    <%-- promote to request scope for availability to JSP includes from configuration --%>
    <dspel:importbean var="agentUIConfig"
      bean="/atg/svc/agent/ui/AgentUIConfiguration" />
    <dspel:getvalueof var="tableConfig" param="tableConfig"
      scope="request" />
    <dspel:getvalueof var="searchResponse" param="searchResponse" />
    <dspel:getvalueof var="items" value="${searchResponse.items}" />
    <dspel:getvalueof var="selectLinkPanelStack" param="selectLinkPanelStack"/>	
    <dspel:getvalueof var="isPopup" param="isPopup" />
    <c:if test="${empty isPopup}">    
      <c:set var="isPopup" value="false" />
    </c:if>          
		<c:if test="${!empty tableConfig.imageClosed}">
			<c:set var="imageClosed" value="${tableConfig.imageClosed}"
				scope="request" />
		</c:if>
		<c:if test="${!empty tableConfig.imageOpen}">
			<c:set var="imageOpen" value="${tableConfig.imageOpen}"
				scope="request" />
		</c:if>
		<c:if test="${!empty tableConfig.imagePath}">
			<c:set var="imagePath" value="${tableConfig.imagePath}"
				scope="request" />
		</c:if>
		  
  <table class="atg_dataTable" summary="Summary" cellspacing="0" cellpadding="0" >   
        <thead>
        <c:forEach var="column" items="${tableConfig.columns}">        
         <c:if test="${column.isVisible == 'true'}">
         <c:set var="columnWidth" value="${column.width}" />
         <c:if test="${empty columnWidth}">    
          <c:set var="columnWidth" value="auto" />
         </c:if>  
	        <th scope="col" style="width:${columnWidth}">	           
	            <dspel:include src="${column.dataRendererPage.URL}" otherContext="${column.dataRendererPage.servletContext}" >
	              <dspel:param name="field" value="${column.field}"  />
	              <dspel:param name="resourceBundle" value="${column.resourceBundle}" />
	              <dspel:param name="resourceKey" value="${column.resourceKey}" />
	              <dspel:param name="isHeading" value="true" />
	            </dspel:include> 
	        </th>
	        </c:if>
        </c:forEach>
        </thead>
      
        <c:forEach var="customerItem" items="${items}">
          <dspel:droplet name="/atg/targeting/RepositoryLookup">
            <dspel:param name="url" value="${customerItem.id}" />
            <dspel:oparam name="output">
              <dspel:tomap var="currentCustomer" param="element" />
            </dspel:oparam>
          </dspel:droplet>
          <tr>
          
          <c:forEach var="column" items="${tableConfig.columns}">
              <c:if test="${column.isVisible == 'true'}">
              <td>
					      <c:if test="${column.dataRendererPage != ''}">
						      <dspel:include src="${column.dataRendererPage.URL}" otherContext="${column.dataRendererPage.servletContext}" >
						        <dspel:param name="field" value="${column.field}" />
						        <dspel:param name="currentCustomer" value="${currentCustomer}" />
			              <dspel:param name="imageClosed" value="${imageClosed}" />
			              <dspel:param name="imageOpen" value="${imageOpen}" />
			              <dspel:param name="imagePath" value="${UIConfig.contextRoot}${imagePath}" />
			              <dspel:param name="isPopup" value="${isPopup}" />
			              <dspel:param name="selectLinkPanelStack" value="${selectLinkPanelStack}" />		 				        
						      </dspel:include>					     
					      </c:if>
					      </td>
					      </c:if> 
          </c:forEach>
         </tr>
        </c:forEach>
     </table>    
  </dspel:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerSearchTable.jsp#1 $$Change: 946917 $--%>
