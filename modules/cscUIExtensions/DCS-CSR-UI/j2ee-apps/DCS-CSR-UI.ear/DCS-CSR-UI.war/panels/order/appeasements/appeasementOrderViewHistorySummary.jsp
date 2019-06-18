<%--
This page display a summary of the appeasement details in table view. E.g.

ID        State       Type       Amount      Reason                Comments
app0001   Pending     Items      USD10.00    Goodwill Gesture      Order 4 days late
          Approval

or

ID        State      Type       Amount      Reason                Comments
app0001   Failed     Shipping   USD10.00    Goodwill Gesture      Order 4 days late

Expected params
appeasement - the appeasement object used for rendering the details on the page

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:getvalueof var="appeasement" param="appeasement"/>
  <c:set var="currencyCode" value="${appeasement.originatingOrder.priceInfo.currencyCode }"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">

    <div class="atg_commerce_csr_statusView">
      <h4>
        <fmt:message key="appeasement.history.summary.idHeader"/>
      </h4>
      <ul>
        <li>
          <c:out value="${appeasement.appeasementId}"/>
        </li>
      </ul>
    </div>

    <div class="atg_commerce_csr_statusView atg_commerce_csr_statusViewShortened">
      <h4>
        <fmt:message key="appeasement.history.summary.stateHeader"/>
      </h4>
      <ul>
        <li>
          <dsp:droplet name="/atg/commerce/custsvc/order/IsHighlightedState">
            <dsp:param name="obj" value="${appeasement}"/>
            <dsp:oparam name="true">
              <span class="atg_commerce_csr_dataHighlight atg-csc-base-table-cell"><dsp:valueof param="appeasement.stateAsUserResource"/></span>
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:valueof param="appeasement.stateAsUserResource"/>
            </dsp:oparam>
          </dsp:droplet>
        </li>
      </ul>
    </div>

    <div class="atg_commerce_csr_statusView atg_commerce_csr_statusViewShortened">
      <h4>
        <fmt:message key="appeasement.history.summary.typeHeader"/>
      </h4>
      <ul>
        <li>
          <fmt:message key="appeasement.history.summary.type.${appeasement.appeasementType}"/>
        </li>
      </ul>
    </div>

    <div class="atg_commerce_csr_statusView atg_commerce_csr_statusViewShortened">
      <h4>
        <fmt:message key="appeasement.history.summary.amountHeader"/>
      </h4>
      <ul>
        <li>
          <csr:formatNumber value="${appeasement.appeasementAmount}" type="currency" currencyCode="${currencyCode}" />
        </li>
      </ul>
    </div>

    <div class="atg_commerce_csr_statusView">
      <h4>
        <fmt:message key="appeasement.history.summary.reasonHeader"/>
      </h4>
      <ul>
        <li>
          <dsp:droplet name="/atg/commerce/custsvc/appeasement/AppeasementReasonLookupDroplet">
            <dsp:param name="id" param="appeasement.reasonCode"/>
            <dsp:param name="elementName" value="appeasementReason"/>
            <dsp:oparam name="output">
              <dsp:valueof param="appeasementReason.readableDescription"/>
            </dsp:oparam>
          </dsp:droplet>
        </li>
      </ul>
    </div>

    <div class="atg_commerce_csr_statusView atg_commerce_csr_statusViewExtended">
      <h4>
        <fmt:message key="appeasement.history.summary.commentsHeader"/>
      </h4>
      <ul>
        <li>
          <c:out value="${appeasement.appeasementNotes}"/>
        </li>
      </ul>
    </div>


  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasementOrderViewHistorySummary.jsp#1 $$Change: 1179550 $--%>