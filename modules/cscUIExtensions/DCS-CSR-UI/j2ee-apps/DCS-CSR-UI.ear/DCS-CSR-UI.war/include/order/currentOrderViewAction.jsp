<%--
  @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/currentOrderViewAction.jsp#1 $$Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%
/*
 * This page generates the appropriate javascript call to view the current active order. It is used by various 
 * anchor tags that direct to a view of the current active order. For non-modifiable orders, it will land on the
 * existing order view page. Otherwise, it lands on the shopping cart page.   
 */
%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
<dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.originalOrder"/>    
  <dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
  <dsp:param name="order" param="${order}"/>
  <dsp:oparam name="false">
      atg.commerce.csr.order.viewExistingOrder('${order.id}','${order.stateAsString}');
  </dsp:oparam>
  <dsp:oparam name="true">
    atgSubmitAction({formId: 'viewCart' });  
  </dsp:oparam>
  </dsp:droplet>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/currentOrderViewAction.jsp#1 $$Change: 946917 $--%>
