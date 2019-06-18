<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

  <%-- This page removes the Selected Item from Gift List 
      Parameters - 
      - giftId - Id of the item to be deleted from the Gift List
      - giftlistId - Id of the Gift List from which item is to be deleted 
  --%>
  
  <dsp:getvalueof var="giftId" param="giftId" scope="page"/>
  <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request"/>
  
  
  <dsp:droplet name="/atg/commerce/gifts/RemoveItemFromGiftlist">
    <dsp:param name="giftlistId" value="${giftlistId}"/>
    <dsp:param name="giftId" value="${giftId}"/>
    <dsp:oparam name="error">
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/removeItemFromGiftList.jsp#1 $$Change: 946917 $--%>