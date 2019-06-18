<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<dspel:page>
  <dspel:getvalueof var="serviceFramework" bean="atg/svc/framework/xml/FrameworkXMLUnmarshaler.definitionFile.asStream" />

  <dspel:droplet name="/atg/dynamo/droplet/xml/XMLTransform">
    <dspel:param name="input" value="${serviceFramework}"/>
    <dspel:param name="template" value="../../xsl/panelDefinitionExtractor.xsl"/>
    <dspel:param name="passParams" value="local"/>
    <dspel:param name="panelId" param="panelId"/>
    <dspel:oparam name="failure">
      Failed to transform XML document: <dsp:valueof param="input"/>
      <br/>
    </dspel:oparam>
  </dspel:droplet>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/debugInfo/panelDefinition.jsp#1 $$Change: 946917 $--%>
