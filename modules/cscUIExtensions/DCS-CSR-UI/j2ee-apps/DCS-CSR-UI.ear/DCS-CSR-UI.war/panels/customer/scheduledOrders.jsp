<%--
 Customer Order History
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/scheduledOrders.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">
<script type="text/javascript">
_container_.onLoadDeferred.addCallback(
    function(){
      dojo.debug("scheduledOrder.jsp: executing addOnLoad");
      dojo.debug("scheduledOrder.jsp: CSRConfigurator.usingScheduledOrders is " + atg.commerce.csr.order.scheduled.isScheduledOrders);
      if ( atg.commerce.csr.order.scheduled.isScheduledOrders == 'false') {
        dojo.debug("scheduledOrder.jsp: hiding panel");
        _container_.domNode.style.display="none";
      }
      dojo.debug("scheduledOrder.jsp: finished addOnLoad");
    });
</script>
<div style="height:100%">

  <dsp:include src="/panels/customer/scheduledOrdersSearchResults.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
  </dsp:include>
   
</div>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/scheduledOrders.jsp#1 $$Change: 946917 $--%>
