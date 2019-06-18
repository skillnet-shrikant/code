<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/commerce/custsvc/util/I18nMonthNames" />
    
{identifier:"abbreviation",
items: [
      <dsp:droplet name="I18nMonthNames">
        <dsp:oparam name="output">
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="monthNames" />
            <dsp:oparam name="outputStart">
{name:"<fmt:message key="common.month.title"/>",label:"<fmt:message key="common.month.title"/>",abbreviation:""}
            </dsp:oparam>
            <dsp:oparam name="output">
              <dsp:getvalueof var="month" param="element"/>
              <c:if test="${!empty month }">
                <dsp:getvalueof var="count" param="count" idtype="java.lang.Integer"/>
,{name:"<dsp:valueof param="element" />",label:"<dsp:valueof param="element" />",abbreviation:"${count}"}
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
]}
    
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/monthData.jsp#1 $$Change: 946917 $--%>
