dojo.hitch(_inst, "createHoverToggler",
           imagePath,
           {open: imageOpen, closed: imageClosed },
           "${gridInstanceId}",
           "detail",
           detailFormId,
           {orderId: dojo.hitch(_inst, "getCellData", "id")},
           detailSuccessURL)
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/getToggleCell.jsp#1 $$Change: 946917 $--%>
