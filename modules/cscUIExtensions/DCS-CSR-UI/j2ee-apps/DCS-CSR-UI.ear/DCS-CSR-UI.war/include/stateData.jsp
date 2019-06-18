<%--
 This page defines the state data in JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/stateData.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale" var="requestLocale"/>
    <dsp:getvalueof var="countryCode" param="countryCode"/>
    <dsp:getvalueof var="isOrderSearch" param="isOrderSearch"/>
    <c:if test="${empty countryCode}">
      <c:set var="countryCode" value="${requestLocale.locale.country}"/>
    </c:if>
    
   
    
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
      <json:object prettyPrint="${UIConfig.prettyPrintResponses}">
        <json:property name="identifier" value="abbreviation"/>
        <dsp:droplet name="/atg/commerce/util/StateListDroplet">
          <dsp:param name="userLocale" value="${requestLocale.locale}"/>
          <dsp:param name="countryCode" value="${countryCode}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="states" param="states" />
            <c:if test="${fn:length(states)>0}">
              <json:array name="items" items="${states}" var="state">
                <json:object>
                  <json:property name="name" value="${state.displayName}"/>
                  <json:property name="label" value="${state.displayName}"/>
                  <json:property name="abbreviation" value="${state.code}"/>
                </json:object>
              </json:array>
            </c:if>
            <c:if test="${fn:length(states)<1}">
              <fmt:message var="fakeStateLabel" key="newAddress.fakeStateLabel" />
              <c:set var="fakeStateValue" value=""/>
              <json:array name="items">
                <json:object>
                  <json:property name="name" value="${fakeStateLabel}"/>
                  <json:property name="label" value="${fakeStateLabel}"/>
                  <json:property name="abbreviation" value="${fakeStateValue}"/>
                </json:object>
              </json:array>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>        
      </json:object>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/stateData.jsp#1 $$Change: 946917 $--%>
