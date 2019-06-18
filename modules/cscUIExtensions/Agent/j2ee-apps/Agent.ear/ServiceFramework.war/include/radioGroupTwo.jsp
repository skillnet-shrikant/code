<%--


@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/radioGroupTwo.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>


<%@ include file="../include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources" >
  
  <dspel:importbean bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler" />
  <dspel:getvalueof var="labelYes" param="labelYes" />  
  <dspel:getvalueof var="labelNo" param="labelNo" />    
  <dspel:getvalueof var="option" param="option" />  
  <dspel:getvalueof bean="UserOptionsFormHandler.options.${option}.value" var="value"/> 	
  
  <c:if test="${value=='0' or value=='true'}">
    <c:set var="value0" value="checked"/>
    <c:set var="value1" value=""/>	
  </c:if>
  
  <c:if test="${value=='1'or value=='false'}">
    <c:set var="value0" value=""/>
    <c:set var="value1" value="checked"/>	
  </c:if>
  
  <c:if test="${value=='0' or value=='1'}">  
    <c:set var="value3" value="0"/>  
    <c:set var="value4" value="1"/>  	
  </c:if>

  <c:if test="${value=='true' or value=='false'}">  
    <c:set var="value3" value="true"/>  
    <c:set var="value4" value="false"/>  	
  </c:if>
  
  <table>
    <tr>
    <td>
      <input id='one<c:out value="${option}"/>' name='<c:out value="${option}"/>' type="radio" 
	  <c:out value="${value0}"/> onClick='setOption("hid<c:out value='${option}'/>","<c:out value='${value3}'/>")'>
    </td>
	<td>
      <c:out value="${labelYes}"/>
	</td>
    <td>
      <input id='two<c:out value="${option}"/>' name='<c:out value="${option}"/>' type="radio" 
	  <c:out value="${value1}"/> onClick='setOption("hid<c:out value='${option}'/>","<c:out value='${value4}'/>")'>
    </td>
	<td>
      <c:out value="${labelNo}"/>
	</td>
    </tr>	
  </table>
    <dspel:input type="hidden" bean="UserOptionsFormHandler.options.${option}.value" id="hid${option}" value="${value}"/>
</dspel:layeredBundle> 
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/radioGroupTwo.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/radioGroupTwo.jsp#1 $$Change: 946917 $--%>
