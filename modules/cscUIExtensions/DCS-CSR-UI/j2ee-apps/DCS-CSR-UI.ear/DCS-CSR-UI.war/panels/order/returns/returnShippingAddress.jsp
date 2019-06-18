<%--
 This page displays the return shipping address
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnShippingAddress.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">  
  
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

  <%--  
      This file will be rendered as the default Return Shipping Address.  It 
      can either be edited directly or configured via the 
      /atg/commerce/custsvc/ui/renderers/ReturnShippingAddress custom
      renderer. 
  --%>
  
  <%--  The return is always passed into this page.  In this sample page we 
        don't do anything with it.  --%>
  <dsp:getvalueof var="returnObject" param="returnObject"/>
  
  <ul class="atg_commerce_csr_simpleList">
    <li><strong>Ship return items to:</strong></li>

    <li>Company ABC</li>
    <li>Attn:Returns</li>
    <li>1421 Main Street</li>
    <li>Cambridge,MA</li>
    <li>02141</li>
  </ul>

      
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnShippingAddress.jsp#1 $$Change: 946917 $--%>
