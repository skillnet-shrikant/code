<%--
Renders the link to load the order 

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="../top.jspf"%>
<dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderSupportedForUpdate"/>

<dsp:getvalueof var="submitorder" param="order"/>    
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources"> 

<dsp:droplet name="IsOrderSupportedForUpdate">
<dsp:param name="order" value="${submitorder}"/>
<dsp:oparam name="true">
  <a href="#" onclick="atg.commerce.csr.order.scheduled.submitNow('${submitorder.id}');return false;"><fmt:message key="scheduleOrder.action.submitNow"/></a>                

</dsp:oparam>
</dsp:droplet>
</dsp:layeredBundle> 
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/submitNow.jsp#1 $$Change: 946917 $--%>
