
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete" />
    <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable" />
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
      <dsp:getvalueof var="order" param="currentOrder" />
      <div id="atg_commerce_csr_order_shippingSummary_subPanel" class="atg_commerce_csr_subPanel">
        <div class="atg_commerce_csr_subPanelHeader">
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_header">
              <h4><fmt:message key='finishOrder.shippingSummary.header'/></h4>
            </li>
            <%-- Only display Edit link if the order can be modified --%>
            <dsp:droplet name="OrderIsModifiable">
              <dsp:param name="order" value="${order}" />
              <dsp:oparam name="true">
               <%-- Order can be modified but if it's incomplete, don't diplay Shipping edit link --%>
                <dsp:droplet name="IsOrderIncomplete">
                  <dsp:oparam name="false">
                    <c:set var="hardgoodShippingGroupCount" value="${0}"/>
                    <c:forEach items="${order.shippingGroups}" var="shippingGroup" varStatus="status">
                      <c:if test="${shippingGroup.shippingGroupClassType == 'hardgoodShippingGroup'}">
                        <c:set var="hardgoodShippingGroupCount" value="${hardgoodShippingGroupCount + 1}"/>
                      </c:if>
                    </c:forEach>
                      <c:choose>
                      <c:when test="${hardgoodShippingGroupCount == 0}">
                        <li class="atg_commerce_csr_last"><a href="#"
                          onClick="atg.commerce.csr.order.finish.editExistingOrder('atg_commerce_csr_finish_editExistingOrderShippingForm','${order.id}')"><fmt:message
                              key='common.shippingAddress.edit' /></a></li>
                      </c:when>
                      <c:when test="${hardgoodShippingGroupCount > 0}">
                        <li class="atg_commerce_csr_last"><a href="#"
                          onClick="atg.commerce.csr.order.finish.editExistingOrder('atg_commerce_csr_finish_editExistingOrderShippingMethodForm','${order.id}')"><fmt:message
                          key='common.shippingMethod.edit' /></a></li>
                        <li><a href="#"
                          onClick="atg.commerce.csr.order.finish.editExistingOrder('atg_commerce_csr_finish_editExistingOrderShippingForm','${order.id}')"><fmt:message
                              key='common.shippingAddress.edit' /></a></li>
                      </c:when>
                      </c:choose>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </ul>
        </div>
        <dsp:include src="/panels/order/finish/shippingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="currentOrder" value="${order}" />
          <dsp:param name="isExistingOrderView" value="${true}" />
        </dsp:include>
      </div>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/shippingViewPanel.jsp#1 $$Change: 946917 $--%>
