<%--

This JSP fragment is for displaying order by id

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/sideFindOrderById.jsp#1 $$Change: 946917 $

@updated $DateTime: 2015/01/26 17:26:27 $$Author:Rahul Gupta

--%>

<%@  include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
    <div parseWidgets="false">
      <dsp:droplet name="HasAccessRight">
        <dsp:oparam name="accessGranted">
          <dsp:param name="accessRight" value="commerceTab"/>
            <svc-ui:frameworkUrl var="successURL" panelStacks="cmcExistingOrderPS" tab="commerceTab"/>
            <svc-ui:frameworkUrl var="successScheduledURL" panelStacks="cmcScheduledOrderPS" tab="commerceTab"/>
            <svc-ui:frameworkUrl var="errorURL"/>
            <dsp:form style="display:none" action="#" id="atg_commerce_csr_globalFindOrderByIdForm" formid="atg_commerce_csr_globalFindOrderByIdForm">
              <dsp:input type="hidden" name="viewOrderId" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.viewOrderId" value=""/>
              <dsp:input type="hidden" name="successScheduledURL" value="${successScheduledURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.successScheduledURL" />
              <dsp:input type="hidden" name="errorURL" value="${errorURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.errorURL" />
              <dsp:input type="hidden" name="successURL" value="${successURL}" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.successURL" />
              <dsp:input type="hidden" priority="-10" bean="/atg/commerce/custsvc/order/ViewOrderFormHandler.findOrderId" value=""/>
            </dsp:form>
            <div parseWidgets="false">
              <form name="OPBIDOrder" action="#" id="OPBIDOrder" class="atg-csc-base-table">
                <div class="atg-csc-base-table-row">
                  <div class="atg-csc-base-table-cell">
                    <label class="ticketLabel" nowrap="true">
                      <fmt:message key="sidePanel.openById.order.label"/>
                    </label>
                    <input type="text"
                           name="OPBIDOrderText"
                           id="atg_next_steps_find_by_id_order_input"
                           onkeydown="eventOrderFind(event);"
                           class="atg_navigationHighlight inputTxtField" style="max-width:70px !important;margin-right: 5px;"/>
                  </div>
                  <div class="atg-csc-base-table-cell">
                    <input type="button" value="<fmt:message key='search.findButton.label'/>"
                             id="OPBIDOrder_button"
                             class="atg_next_steps_find_by_id_button"
                             onclick="atg.commerce.csr.order.findByIdOrder(escape(document.getElementById('OPBIDOrder').OPBIDOrderText.value));"/>
                   
                  </div>
                </div>
              </form>
            </div>
          </dsp:oparam>
        </dsp:droplet>
      </div>
  </dsp:layeredBundle>
</dsp:page>

<!-- $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/sideFindOrderById.jsp#1 $ $Change: 946917 $ $DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/sideFindOrderById.jsp#1 $$Change: 946917 $--%>
