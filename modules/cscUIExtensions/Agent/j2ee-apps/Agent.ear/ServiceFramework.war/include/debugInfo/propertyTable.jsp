<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<dspel:page>
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="panelDefinition" param="panelDefinition"/>
  <dspel:getvalueof var="strings" param="strings"/>
  <table summary="Customer Search Results Tickets" class="atg_dataTable" cellpadding="0"
      cellspacing="0" class="panelPropertiesTable">
    <thead>
      <tr>
        <th>Property</th>
        <th>Value</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>panelStackId</td>
        <td>${panelStackId}</td>
      </tr>
      <tr>
        <td>panelId</td>
        <td>${panelId}</td>
      </tr>
      <tr>
        <td>contentUrl</td>
        <td>${panelDefinition.contentUrl}</td>
      </tr>
      <tr>
        <td>otherContext</td>
        <td>${panelDefinition.otherContext}</td>
      </tr>
      <tr>
        <td>resourceBundle</td>
        <td>${strings}</td>
      </tr>
      <tr>
        <td>titleKey</td>
        <td>${panelDefinition.titleKey}</td>
      </tr>
    </tbody>
  </table>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/debugInfo/propertyTable.jsp#1 $$Change: 946917 $--%>
