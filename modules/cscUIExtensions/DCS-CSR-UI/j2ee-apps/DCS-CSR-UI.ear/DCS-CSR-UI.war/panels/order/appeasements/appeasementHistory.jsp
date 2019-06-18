<%--
 This is displayed in the order view and contains information on the
 appeasements given on the order.
 The left displays links that can be selected to get further information on each appeasement.
 The panel on the right displays the appeasement details. (2 included jsp files).

Expected params
historyAppeasementId - (Optional) The id of an appeasement.
 If the page param historyAppeasementId is specified then that id will be used to
 display information in the details panel.

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasementHistory.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">

  <dsp:getvalueof var="viewOrderId" bean="atg/commerce/custsvc/order/ViewOrderHolder.current.id"/>
  <dsp:getvalueof var="historyAppeasementId" param="historyAppeasementId"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">

    <dsp:droplet name="/atg/commerce/custsvc/appeasement/GetOrderAppeasementsDroplet">
      <dsp:param value="${viewOrderId}" name="orderId" />
      <dsp:oparam name="output">
        <div class="atg_store_csr_returnOrderHistory">

        <%-- start of left side selection list --%>
        <ul class="atg_commerce_csr_orderHistoryOrderList">

          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param param="appeasements" name="array" />
            <dsp:param value="appeasement" name="elementName" />
            <dsp:param name="sortProperties" value="-creationDate"/>
            <dsp:oparam name="output">

              <dsp:tomap var="appeasementItemMap" param="appeasement"/>

              <c:choose>
                <c:when test="${!empty historyAppeasementId}">
                  <%-- Only display 2 columns when a selection has been made --%>
                  <c:choose>
                    <c:when test="${historyAppeasementId eq appeasementItemMap.appeasementId}">
                      <%-- If the current selection is the same as the displayed item then do nothing
                        show a link to the appeasement --%>
                      <li  class="active">
                        <web-ui:formatDate type="date" value="${appeasementItemMap.creationDate}" dateStyle="short"  />
                        <c:out value="${appeasementItemMap.appeasementId}"/>
                      </li>
                    </c:when>
                    <c:otherwise>
                      <%-- A selection has been made but it is not this item - display a link --%>
                      <li>
                        <web-ui:formatDate type="date" value="${appeasementItemMap.creationDate}" dateStyle="short"  />
                        <a href="#" onclick="atg.commerce.csr.order.appeasement.selectAppeasementHistory('<c:out value="${appeasementItemMap.appeasementId}"/>');return false;">
                                            <c:out value="${appeasementItemMap.appeasementId}"/>
                                            </a>
                      </li>
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:otherwise>
                  <%-- No selection has been made but display a link for all items. Display 4 columns --%>
                  <li>
                    <web-ui:formatDate type="date" value="${appeasementItemMap.creationDate}" dateStyle="short"  />
                    <a href="#" onclick="atg.commerce.csr.order.appeasement.selectAppeasementHistory('<c:out value="${appeasementItemMap.appeasementId}"/>');return false;">
                    <c:out value="${appeasementItemMap.appeasementId}"/>
                    </a>
                    <csr:formatNumber value="${appeasementItemMap.appeasementAmount}" type="currency" currencyCode="${appeasementItemMap.order.priceInfo.currencyCode}" />

                    <dsp:droplet name="/atg/commerce/custsvc/appeasement/LoadAppeasementDroplet">
                      <dsp:param name="appeasementId" value="${appeasementItemMap.appeasementId}" />
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="appeasement" param="appeasement"/>
                        <dsp:droplet name="/atg/commerce/custsvc/order/IsHighlightedState">
                          <dsp:param name="obj" value="${appeasement}"/>
                          <dsp:oparam name="true">
                            <span class="atg_commerce_csr_dataHighlight"><dsp:valueof param="appeasement.stateAsUserResource"/></span>
                          </dsp:oparam>
                          <dsp:oparam name="false">
                            <dsp:valueof param="appeasement.stateAsUserResource"/>
                          </dsp:oparam>
                        </dsp:droplet>
                      </dsp:oparam>
                    </dsp:droplet>
                  </li>
                </c:otherwise>
              </c:choose>

              <!-- end ForEach -->
            </dsp:oparam>
          </dsp:droplet>
        </ul>
        </div>
        <!-- end of left side selection list -->

        <div class="atg_commerce_csr_orderHistoryOrderDetails">

        <c:choose>
          <%-- historyAppeasementId is only populated when a selection has been made in the list
              Display the full details of the appeasement --%>
          <c:when test="${!empty historyAppeasementId}">

            <dsp:droplet name="/atg/commerce/custsvc/appeasement/LoadAppeasementDroplet">
              <dsp:param name="appeasementId" value="${historyAppeasementId}" />
              <dsp:oparam name="output">
                <dsp:getvalueof var="appeasement" param="appeasement"/>

                  <div class="atg_commerce_csr_corePanelData">
                    <dsp:include src="/panels/order/appeasements/appeasementOrderViewHistorySummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
                      <dsp:param name="appeasement" value="${appeasement}"/>
                    </dsp:include>
                  </div>

                  <div class="atg_commerce_csr_corePanelData">
                    <dsp:include src="/panels/order/appeasements/finishAppeasementSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
                      <dsp:param name="appeasement" value="${appeasement}"/>
                      <dsp:param name="displayPaymentTitle" value="${true}"/>
                    </dsp:include>
                  </div>
              </dsp:oparam>
            </dsp:droplet>
          </c:when>
          <c:otherwise>
            <!-- this is what's displayed when nothing is selected in the list -->

            <div class="atg_commerce_csr_corePanelData">
              <fmt:message key="appeasement.history.selectLink"/>
            </div>
          </c:otherwise>
        </c:choose>

        </div>

      </dsp:oparam>

      <dsp:oparam name="empty">
        <div class="atg_commerce_csr_corePanelData">
          <fmt:message key="appeasement.history.noAppeasements"/>
        </div>
      </dsp:oparam>
    </dsp:droplet>

  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasementHistory.jsp#1 $$Change: 1179550 $--%>
