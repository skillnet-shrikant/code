<%--
 Displays a single field heading

 
 headingTitle - The title to be displayed
 fieldName - The name of the field to be sorted
 resourceBundle - The Resource Bundle from where the resource keys are defined
 resourceKey - The key to that maps to the resource string

 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerSearchResultSortHeading.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>

<dspel:page xml="true">

  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler"/>
  <dspel:getvalueof var="resourceBundle" param="resourceBundle" />
  <dspel:getvalueof var="resourceKey" param="resourceKey" />
  <dspel:getvalueof var="fieldName" param="fieldName" />
  <dspel:importbean var="formHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler"/>
  <dspel:getvalueof var="searchResponse" value="${formHandler.searchResponse}"/>
  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dspel:layeredBundle basename="${resourceBundle}">
  <c:choose>
  
    <c:when test="${fn:toLowerCase(searchResponse.docSortProp) eq fn:toLowerCase(fieldName)}">
  
    <a  href="#" id="atg_service_customer_search${fieldName}Sort" onclick="atg.service.customer.search.handleSort('${fieldName}');return false;"><fmt:message key="${resourceKey}"/></a>

    <script type="text/javascript">
      dojo.byId('atg_service_customer_search${fieldName}Sort').style.fontWeight ='bold';
    </script>
    
    <c:choose>
    
      <c:when test="${searchResponse.docSortOrder eq 'ascending'}">
      
        <img src="${UIConfig.contextRoot}/images/sortArrowUp.gif">
      
      </c:when>
      
      <c:otherwise>
      
        <img src="${UIConfig.contextRoot}/images/sortArrowDown.gif">
      
      </c:otherwise>
    
    </c:choose>

    </c:when>
    
    <c:otherwise>
    
    <a  href="#" id="atg_service_customer_search${fieldName}Sort" onclick="atg.service.customer.search.handleSort('${fieldName}');return false;"><fmt:message key="${resourceKey}"/></a>

    
    </c:otherwise>
    
  </c:choose>

  </dspel:layeredBundle>
    
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerSearchResultSortHeading.jsp#1 $$Change: 946917 $--%>