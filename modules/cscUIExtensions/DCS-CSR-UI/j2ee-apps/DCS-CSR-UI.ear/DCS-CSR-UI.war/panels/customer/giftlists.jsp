<%--
 Customer Gift Lists Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/giftlists.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">
  <dsp:getvalueof var="isGiftlists" bean="/atg/commerce/custsvc/util/CSRConfigurator.usingGiftlists"/>
  
  <script type="text/javascript">
  dojo.provide( "atg.commerce.csr.order.gift" );
  atg.commerce.csr.order.gift.isGiftlists = "${isGiftlists}";
  
  _container_.onLoadDeferred.addCallback(
      function(){
        dojo.debug("giftlists.jsp: executing addOnLoad");
        dojo.debug("giftlists.jsp: CSRConfigurator.usingGiftlists is " + atg.commerce.csr.order.gift.isGiftlists);
        if ( atg.commerce.csr.order.gift.isGiftlists == 'false') {
          dojo.debug("giftlists.jsp: hiding panel");
          _container_.domNode.style.display="none";
        }
        dojo.debug("giftlists,: finished addOnLoad");
      });
  </script>
  
  <div style="height:100%">
    <dsp:include src="/panels/customer/giftlistsView.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
    </dsp:include>
  </div>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/giftlists.jsp#1 $$Change: 946917 $--%>