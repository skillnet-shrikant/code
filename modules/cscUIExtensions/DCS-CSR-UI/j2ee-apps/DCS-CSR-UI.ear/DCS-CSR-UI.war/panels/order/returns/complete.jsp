<%--
 This page defines the complete order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/complete.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">  
  
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
    <dsp:include src="/panels/order/returns/finishReturn.jsp" otherContext="${CSRConfigurator.contextRoot}">
    </dsp:include>
      
  </dsp:layeredBundle>
  <%-- This snippet is added to highlight the complete step in the progress bar. --%>
  <script type="text/javascript">
    atg.progress.update('cmcCompleteReturnPS');
  </script>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/complete.jsp#1 $$Change: 946917 $--%>
