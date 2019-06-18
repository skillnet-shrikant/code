<%--
 This page defines the confirm order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/confirm.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:importbean bean="/atg/userprofiling/ActiveCustomerProfile" var="activeCustomerProfile"/>
  
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnsDataHolder" var="returnsDataHolder"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ConfirmationInfo"  var="confirmationInfo"/>
  <dsp:getvalueof var="order" value="${confirmationInfo.order}"/>
  
             
    <dsp:include src="/panels/order/returns/confirmReturn.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="returnObject" value="${returnsDataHolder.returnRequest}" />
    </dsp:include>
  
  <%--  include file="/include/order/confirm/sendConfirm.jspf" --%>
  <%@ include file="/include/order/confirm/createNewProfile.jspf"%>

  </dsp:layeredBundle>
  <%-- This snippet is added to highlight the Products in the progress bar.. --%>
  <script type="text/javascript">
    atg.progress.update('cmcConfirmReturnPS');
  </script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/confirm.jsp#1 $$Change: 946917 $--%>
