<%--
Renders the link to split item
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
<dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>

<dsp:getvalueof var="commerceItemId" param="commerceItemId"/>  

 
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <a href="#" onclick="submitSplitItems('${commerceItemId}');return false;">Split Item</a></span>
</dsp:layeredBundle>
	      
</dsp:page>
