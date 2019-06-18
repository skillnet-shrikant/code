<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<dsp:page xml="true">
 
 <script type="text/javascript">

 dojo.provide( "atg.commerce.csr.order.scheduled" );

 atg.commerce.csr.order.scheduled.isScheduledOrders = "<dsp:valueof bean='/atg/commerce/custsvc/util/CSRConfigurator.usingScheduledOrders'/>";

 dojo.require('dojox.Dialog');
 
 dojo.addOnLoad(function() {
   if (!dijit.byId("cancelOrderPopup")) {
    new dojox.Dialog({
      id: "cancelOrderPopup",
      cacheContent: "false", 
      executeScripts: "true",
      scriptHasHooks: "true",
      duration: 100,
      "class": "atg_commerce_csr_popup"
    });
   }
 });
 
 </script>
 
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/globalJS.jsp#1 $$Change: 946917 $--%>