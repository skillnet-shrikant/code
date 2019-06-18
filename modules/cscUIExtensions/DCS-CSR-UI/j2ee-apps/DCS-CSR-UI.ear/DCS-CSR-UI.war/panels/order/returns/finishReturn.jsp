<%--
 This page defines the complete order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/finishReturn.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
<dsp:page xml="true">

  <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.returns.WebAppResources"/>
    <!--#########  Complete Return panel return items start  #########-->

      <dsp:include src="/panels/order/returns/finishReturnExchangeButtons.jsp" otherContext="${CSRConfigurator.contextRoot}">
      </dsp:include>

      <%-- ######################### Return Items #################### --%>
      <div class="atg_commerce_csr_subPanel atg_commerce_csr_shoppingCart">
      <div class="atg_commerce_csr_subPanelHeader">
      <ul class="atg_commerce_csr_panelToolBar">
      <li class="atg_commerce_csr_header"><h4><fmt:message key='finishReturn.refundDetails.table.header'/></h4></li>
        <li class="atg_commerce_csr_last">
            <a href="#" onClick="atgNavigate({panelStack:'cmcReturnsPS'});return false;">
              <fmt:message key='common.edit'/> <fmt:message key='finishReturn.returnItems.table.header'/>
            </a>
        </li>
      </ul>
      </div>

      <dsp:include src="/panels/order/returns/refundDetails.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="returnRequest" bean="ShoppingCart.returnRequest"/>
      <dsp:param name="modifiable" value="true"/>
      <%-- The modifiable flag is set to true so that the otherAdjustments field will not be displayed in the confirmReturn page
       as returnsOrExchanges doesn't support otherAdjustments option any more but this field will continue to be displayed on the
       returnsHistory page to provide backward compatibility , and both confirm and history includes the same refund details.jsp --%>
      </dsp:include>
      </div>

     <%-- ######################### Refund Types #################### --%>
      <div class="atg_commerce_csr_subPanel">
        <div class="atg_commerce_csr_subPanelHeader">
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_header">
              <h4><fmt:message key='finishReturn.refundTypes.table.header'/></h4>
            </li>
            <li class="atg_commerce_csr_last">
                <a href="#" onClick="atgNavigate({panelStack:'cmcRefundTypePS'});return false;">
                    <fmt:message key='common.edit'/> <fmt:message key='finishReturn.refundTypes.table.header'/>
                  </a>
              </li>
            </ul>
        </div>
        <dsp:include src="/panels/order/returns/finishRefundSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="returnRequest" value="${cart.returnRequest}"/>
        </dsp:include>
      </div>

     <%-- ######################### Notes #################### --%>
      <div class="atg_commerce_csr_subPanel">

      <%-- Display Order Notes --%>
      <svc-ui:frameworkUrl var="addNoteSuccessErrorURL" panelStacks="cmcCompleteReturnPS"/>
      <dsp:include src="/include/order/note/notes.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="mode" value="edit"/>
        <dsp:param name="successURL" value="${addNoteSuccessErrorURL}" />
        <dsp:param name="psToRefresh" value="cmcCompleteReturnPS" />
        <dsp:param name="order" value="${cart.current}"/>
      </dsp:include>

    </div>
    <!--#########   Complete Return panel return items end  ###########-->
    <dsp:include src="/panels/order/returns/finishReturnExchangeButtons.jsp" otherContext="${CSRConfigurator.contextRoot}">
    </dsp:include>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/finishReturn.jsp#2 $$Change: 1179550 $--%>
