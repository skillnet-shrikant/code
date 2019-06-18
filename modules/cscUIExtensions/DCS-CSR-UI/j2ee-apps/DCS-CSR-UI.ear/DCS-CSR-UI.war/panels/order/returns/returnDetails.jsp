<%--
 This page allows the user to specify the various amounts that are to be refunded
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnDetails.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
<c:set var="returnObject" value="${cart.returnRequest}"/>

<script type="text/javascript">
  <%--  The framework will call this function if it is defined.  Here we
        check that a return or exchange is in progress, if not then hide 
        the panel  --%>
  _container_.onLoadDeferred.addCallback( function (pParams) {
      var isExchange = ${! empty returnObject};
      if ( !isExchange ) {
        dojo.debug("Hiding cmcReturnDetails panel, a return or exchange is not taking place.");
        dojo.style(_container_.domNode, "display", "none");
      }
    } );
</script>


<c:if test="${! empty returnObject}">

  <dsp:getvalueof var="panelStackId" param="panelStackId"/>
  

  <dsp:include src="/panels/order/returns/refundDetails.jsp" otherContext="${CSRConfigurator.contextRoot}">
  <dsp:param name="returnRequest" bean="ShoppingCart.returnRequest"/>
  <dsp:param name="modifiable" value="true"/>
  </dsp:include>
</c:if>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnDetails.jsp#1 $$Change: 946917 $--%>
