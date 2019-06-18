<%-- 
This page fragment generates a series of dsp:option tags, one for each
state or Canadian province we want to let the user select as part of an
address.  
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
<dspel:importbean bean="/atg/dynamo/servlet/RequestLocale" var="requestLocale"/>

<dspel:getvalueof var="statePickerId" param="statePickerId"/>
<dspel:getvalueof var="formHandlerFieldName" param="formHandlerFieldName"/>
<dspel:getvalueof var="missingMessage" param="missingMessage"/>
<dspel:getvalueof var="inlineIndicator" param="inlineIndicator"/>
<dspel:getvalueof var="promptMessage" param="promptMessage"/>
<dspel:getvalueof var="selectedCountryCode" param="selectedCountryCode"/>
<c:if test="${empty selectedCountryCode}">
  <c:set var="selectedCountryCode" value="${requestLocale.locale.country}"/>
</c:if>

  <dspel:select id="${statePickerId}"
    bean="${formHandlerFieldName}">
    <dspel:tagAttribute name="dojoType" value="atg.widget.validation.SimpleComboBox" />
    <dspel:tagAttribute name="missingMessage" value="${missingMessage}" />
    <dspel:tagAttribute name="inlineIndicator" value="${inlineIndicator}" />
    <dspel:tagAttribute name="promptMessage" value="${promptMessage}" />
    
    <dspel:option value="">
    </dspel:option>
    <c:if test="${not empty selectedCountryCode}">
    <dspel:droplet name="/atg/commerce/util/StateListDroplet">
      <dspel:param name="userLocale" value="${requestLocale.locale}" />
      <dspel:param name="countryCode" value="${selectedCountryCode}" />
      <dspel:oparam name="output">
        <dspel:getvalueof var="states" param="states"/>
        <c:forEach var="state" items="${states}">
          <dspel:param name="state" value="${state}"/>
          <dspel:getvalueof var="code" vartype="java.lang.String" param="state.code">
            <dspel:option value="${code}"><dspel:valueof param="state.displayName"/></dspel:option>
          </dspel:getvalueof>
        </c:forEach>
      </dspel:oparam>
    </dspel:droplet>
    </c:if>
  </dspel:select>

</dspel:layeredBundle>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/statePicker.jsp#1 $$Change: 946917 $--%>
