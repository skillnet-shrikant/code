<%--
 This page defines the returns panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returns.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ViewOrderHolder" var="viewOrder"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsReturnable"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler" />
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnRequestDroplet"/>

  <%--
        Determine if the returns should be displayed as read only, compare the
        value of:

          /atg/commerce/custsvc/ShoppingCart.current
        to
          /atg/commerce/custsvc/ViewOrderHolder.current

        If the orders are the same, then the receive returns controls should be
        displayed.  If not, it should be read-only.
  --%>

  <c:set var="order" value="${viewOrder.current}"/>
  <c:choose>
    <c:when test="${cart.current.id == viewOrder.current.id}">
      <c:set var="readOnlyView" value="false"/>
    </c:when>
    <c:otherwise>
      <c:set var="readOnlyView" value="true"/>
    </c:otherwise>
  </c:choose>

            
              <%--  If this order already has return request then render them, if
                    not then render the 'create' buttons  --%>
              <dsp:droplet name="ReturnDroplet">
                <dsp:param name="orderId" value="${order.id}"/>
                <dsp:param name="resultName" value="returns"/>
                <dsp:oparam name="error">
                  <tr><td><dsp:valueof param="errorMessage"><fmt:message key="returns.couldNotRetrieveReturnList" /></dsp:valueof></td></tr>
                </dsp:oparam>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="returnList" param="returns"/>
                  <c:choose>
                    <c:when test="${ empty returnList }" >

                      <div class="atg_commerce_csr_corePanelData">
                        <dsp:include src="/include/return/existingReturnNoReturns.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
                      </div>
                      <!--#########  Existing Order Returns empty panel end  ###########-->

                    </c:when>
                    <c:otherwise>

                      <!--#########  Existing Order View panel start  #########-->
                      <div class="atg_commerce_csr_coreExistingOrderView">

                        <ul class="atg_commerce_csr_panelToolBar">

                          <%--  If there are still items to receive then set 
                                itemsToReturn to true.  To figure out if there
                                are still items to return we loop through the
                                returnList.  If any return item for any of the
                                returns is still in awaiting_return state then
                                there are still items outstanding.  --%>
                          <c:set var="itemsToReturn" value="false"/>
                          <c:forEach var="request" items="${returnList}">
                            <dsp:droplet name="/atg/commerce/custsvc/returns/AdminReturnRequestLookup">
                              <dsp:param name="returnRequestId" value="${request.repositoryId}"/>
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="returnObject" param="result"/>
                                <c:forEach var="returnItem" items="${returnObject.returnItemList}">
                                  <c:if test="${returnItem.state == 'AWAITING_RETURN' || returnItem.state == 'PARTIAL_RETURN'}">
                                    <c:set var="itemsToReturn" value="true"/>
                                  </c:if>
                                </c:forEach>
                              </dsp:oparam>
                            </dsp:droplet>
                          </c:forEach>
                            
                          <%--  Edit link should only appear when there are
                                returns to receive and the current order is 
                                not the same as the view order.  There must 
                                be at least one return, and if there is at 
                                least one, there must be at least one that 
                                still has an outstanding item to receive  --%>
                          <c:if test="${readOnlyView && itemsToReturn}">                                   
                            <li class="atg_commerce_csr_last"><a href="#" onclick="atg.commerce.csr.order.loadExistingOrder('${order.id}','${order.stateAsString}');return false;"><fmt:message key="common.edit"/></a></li>
                          </c:if>
                          
                        </ul>

                        <dsp:form name="csrReceiveReturns" id="csrReceiveReturns" formid="csrReceiveReturns">
                          <svc-ui:frameworkUrl var="successURL" panelStacks="cmcExistingOrderPS"/>
                          <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcExistingOrderPS"/>
                          <dsp:input bean="ReturnFormHandler.receiveReturnItemsSuccessURL"
                            value="${successURL}" type="hidden" />
                          <dsp:input bean="ReturnFormHandler.receiveReturnItemsErrorURL"
                            value="${errorURL}" type="hidden" />
                          <dsp:input bean="ReturnFormHandler.receiveReturnItems"
                            type="hidden" priority="-10" value=""/>

                          <div class="atg_commerce_csr_corePanelData">

                            <table class="atg_dataTable" cellpadding="0" cellspacing="0">
                              <thead>
                                <tr>
                                  <th scope="col"><fmt:message key="existingReturns.table.header.return.title"/></th>
                                  <th scope="col"><fmt:message key="existingReturns.table.header.dateSubmitted.title"/></th>
                                  <th scope="col"><fmt:message key="existingReturns.table.header.type.title"/></th>
                                  <th scope="col" class="atg_numberValue"><fmt:message key="existingReturns.table.header.numberOfItems.title"/></th>
                                  <th scope="col" class="atg_numberValue"><fmt:message key="existingReturns.table.header.totalAmount.title"/></th>
                                  <th scope="col"><fmt:message key="existingReturns.table.header.status.title"/></th>
                                </tr>
                              </thead>
                              <tbody>
                                <c:forEach var="request" items="${returnList}" varStatus="rowCounter">

                                  <dsp:tomap var="returnRequestMap" value="${request}"/>
                                  
                                  <dsp:droplet name="/atg/commerce/custsvc/returns/AdminReturnRequestLookup">
                                    <dsp:param name="returnRequestId" value="${request.repositoryId}"/>
                                    <dsp:oparam name="output">

                                      <dsp:getvalueof var="returnObject" param="result"/>

                                      <dsp:include src="/include/return/existingReturnRow.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                                        <dsp:param name="returnRequest" value="${returnObject}" />
                                        <dsp:param name="returnRequestIndex" value="${rowCounter.index}" />
                                        <dsp:param name="readOnlyView" value="${readOnlyView}" />
                                      </dsp:include>

                                    </dsp:oparam>
                                  </dsp:droplet>
                                </c:forEach>

                              </tbody>
                            </table>
                          </div>
                          <div class="atg_commerce_csr_panelFooter">
                            <c:if test="${!readOnlyView && itemsToReturn}">
                              <fmt:message key="existingReturns.receiveReturn" var="receiveReturns"/>
                              <input type="button" value="${receiveReturns}"
                                onclick="atgSubmitAction({form: document.getElementById('csrReceiveReturns')});"/>
                              </c:if>
                          </div>
                        </dsp:form>
                      </div>
                      <!--#########  Existing Order View panel end  ###########-->

                    </c:otherwise>
                  </c:choose>
                </dsp:oparam>
              </dsp:droplet>


  </dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returns.jsp#1 $$Change: 946917 $--%>
