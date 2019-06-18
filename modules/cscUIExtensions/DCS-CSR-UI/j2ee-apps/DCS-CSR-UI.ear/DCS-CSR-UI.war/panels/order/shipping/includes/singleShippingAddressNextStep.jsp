<%--
The shipping form handler defines all possible urls and provides a method to figure out the destination.
If a destination is not found, the user/agent remains in the same page.

The URLs are set in this page fragment and this page fragment can be over-ridden.
Thus if you prefer instead of serving billing page as a next step,
the success url could be configured to go some intermediate step
and then the application could serve billing page.

This page fragment is used in the single shipping address page.

--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean
    bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler"/>

  <svc-ui:frameworkUrl var="completeOrderPageURL"
                       panelStacks="cmcCompleteOrderPS"
                      />
  <svc-ui:frameworkUrl var="refundMethodsPageURL"
                       panelStacks="cmcRefundTypePS"
                      />
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcBillingPS"
                       init="true"
                      />
  <svc-ui:frameworkUrl var="shippingMethodURL"
                       panelStacks="cmcShippingMethodPS" init="true"
                      />

  <dsp:input type="hidden" value="${successURL }" name="successURL"
             bean="ShippingGroupFormHandler.singleShippingGroupCheckoutSuccessURL"/>

  <dsp:input type="hidden" value="${completeOrderPageURL }"
             name="completeOrderPageURL"
             bean="ShippingGroupFormHandler.completeOrderPageURL"/>

  <dsp:input type="hidden" value="${refundMethodsPageURL }"
             name="refundMethodsPageURL"
             bean="ShippingGroupFormHandler.refundMethodsPageURL"/>

  <dsp:input type="hidden" value="${shippingMethodURL }"
             name="shippingMethodURL"
             bean="ShippingGroupFormHandler.shippingMethodURL"/>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/singleShippingAddressNextStep.jsp#1 $$Change: 946917 $--%>
