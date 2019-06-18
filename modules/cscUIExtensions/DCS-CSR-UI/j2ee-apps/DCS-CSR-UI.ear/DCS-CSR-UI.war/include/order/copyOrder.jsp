<%--
Renders the link to load the order 

@version $Id:
@updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>
<%@ include file="../top.jspf"%>
<dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderSupportedForUpdate"/>
<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
<dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>

<dsp:getvalueof var="submitorder" param="order"/>  

  <dsp:droplet name="/atg/commerce/custsvc/approvals/order/IsOrderPendingApprovalDroplet">
  <dsp:param name="orderId" value="${submitorder.id}"/>
      <dsp:oparam name="false">
        <dsp:droplet name="IsOrderSupportedForUpdate">
        <dsp:param name="order" value="${submitorder}"/>
        <dsp:oparam name="true">
          
       <c:choose>
	       <c:when test="${envTools.siteAccessControlOn == 'true'}">
             <c:set var="allSitesAccessible" value="1"/>
		         <c:forEach items="${submitorder.commerceItems}" var="item" varStatus="vs">
	             <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
               <dsp:droplet name="IsSiteAccessibleDroplet">
                 <dsp:param name="siteId" value="${siteId}"/>
                 <dsp:oparam name="false">
                   <c:set var="allSitesAccessible" value="0"/>
                 </dsp:oparam>
               </dsp:droplet>
	           </c:forEach>
           
             <c:if test="${allSitesAccessible == '1'}">
               <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
                 <a href="#" onclick="atg.commerce.csr.order.copy('${submitorder.id}');return false;"><fmt:message key="view.order.copy"/></a> <span id="ea_csc_order_copy"></span>
               </dsp:layeredBundle> 
             </c:if>
	       </c:when>
	       <c:otherwise>
	         <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
	           <a href="#" onclick="atg.commerce.csr.order.copy('${submitorder.id}');return false;"><fmt:message key="view.order.copy"/></a> <span id="ea_csc_order_copy"></span>
	         </dsp:layeredBundle>
	       </c:otherwise>
        </c:choose> 
         
      </dsp:oparam>
      </dsp:droplet>  
			
	  </dsp:oparam>
  </dsp:droplet>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/copyOrder.jsp#2 $$Change: 953229 $--%>
