<%--
 This page defines the exchange summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/exchangeSummary.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
<dsp:page xml="true">
    <dsp:getvalueof var="panelStackId" param="panelStackId"/>
    <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
    <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>

    <c:set var="returnReq" value="${cart.returnRequest}"/>
    
    <script type="text/javascript">
      <%--  The framework will call this function if it is defined.  Here we
            check that the return is in the exchangeProcess, if not then hide 
            the panel  --%>
      _container_.onLoadDeferred.addCallback( function (pParams) {
          var isExchange = ${ (! empty returnReq) ? returnReq.exchangeProcess : 'false' };
          if ( !isExchange ) {
            dojo.debug("Hiding cmcExchangeSummaryP panel, an exchange is not taking place.");
            dojo.style(_container_.domNode, "display", "none");
          }
        } );
      
      if (!dijit.byId("atg_commerce_csr_catalog_productQuickViewPopup")) {
        new dojox.Dialog({ id: "atg_commerce_csr_catalog_productQuickViewPopup",
                           cacheContent: "false",
                           executeScripts: "true",
                           scriptHasHooks: "true",
                           duration: 100,
                           "class": "atg_commerce_csr_popup"});
      }
      
    </script>
    
 <c:if test="${!empty returnReq}">
   <div style="display:none;" id="atg_commerce_csr_catalog_addToCartContainer"></div>
    <!--#########  View Return Items start  #########-->
    
      <div class="atg_commerce_csr_refundItems">
        <table class="atg_dataTable">
        <tbody>
         <c:forEach var="returnItem" items="${returnReq.returnItemList}" varStatus="rowCounter">
            <%@ include file="returnSummaryLineItem.jspf"%> 
          </c:forEach>
        </tbody>
        </table>

        <dsp:include src="/panels/order/returns/promotionsLost.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="returnRequest" value="${returnReq}"/>
        </dsp:include>
       
        <div class="atg_commerce_csr_orderSummary">
          <table class="atg_dataForm" id="atg_commerce_csr_neworder_orderSummaryData">           
           <tr>
             <td>
               <label class="atg_commerce_csr_orderSummaryTotal">
               <fmt:message key="promotionsLost.returnCredit"/>
               </label>
              </td>
              <td class="arg_commerce_csr_orderSummaryAmount">
                <span class="atg_commerce_csr_orderSummaryTotal atg_csc_negativeBalance">
                  <csr:formatNumber value="${-returnReq.totalRefundAmount}" type="currency"  currencyCode="${returnReq.order.priceInfo.currencyCode}"/>
                </span>
              </td>
            </tr>
          </table>
       </div>   
    </div>
    <!--#########  View Return Items end  ###########-->
  </c:if>
  </dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/exchangeSummary.jsp#2 $$Change: 1179550 $--%>
