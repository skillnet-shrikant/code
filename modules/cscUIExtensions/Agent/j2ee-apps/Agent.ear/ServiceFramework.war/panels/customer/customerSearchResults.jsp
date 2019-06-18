<%--
 This page defines the Customer Search Results
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerSearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <div class="panel">
      <div class="panelHeader popup_panelHeader panelHeaderBackground">
        <h3 class="header3" style="padding-left:5px">
          <fmt:message key="customer.customerSelection.customerSearchResults"/>
        </h3>
        <div class="panelIcons">
        </div>
      </div>
      <div class="panelContent popup_Results" id="customerSearchResults">
        <dspel:include src="/panels/customer/searchResults.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="isPopup" value="true"/>
        </dspel:include>
      </div>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerSearchResults.jsp#1 $$Change: 946917 $--%>
