<%--


@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/checkboxItem.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>


<%@ include file="../include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources" >
  
  <dspel:importbean bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler" />
  <dspel:getvalueof var="label" param="label" />  
  <dspel:getvalueof var="option" param="option" />  

  <dspel:getvalueof bean="UserOptionsFormHandler.options.${option}.value" var="value"/> 	
  
  <c:if test="${value=='true'}">
    <c:set var="value0" value="checked"/>
  </c:if>
  <c:if test="${value=='false'}">
    <c:set var="value0" value=""/>
  </c:if>

  <table>
    <tr>
    <td>
      <input id='one<c:out value="${option}"/>' type="checkbox" 
	  <c:out value="${value0}"/> 
	  onchange="document.getElementById('<c:out value="hid${option}"/>').value=this.checked">
    </td>
	<td>
      <c:out value="${label}"/>
	</td>
    </tr>	
  </table>
  <dspel:input type="hidden" bean="UserOptionsFormHandler.options.${option}.value" id="hid${option}" value="${value}"/>
</dspel:layeredBundle> 
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/checkboxItem.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/checkboxItem.jsp#1 $$Change: 946917 $--%>
