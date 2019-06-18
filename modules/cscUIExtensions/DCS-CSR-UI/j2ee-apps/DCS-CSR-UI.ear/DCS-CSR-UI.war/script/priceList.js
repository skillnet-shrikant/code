dojo.provide( "atg.commerce.csr.pricing.priceLists" );
dojo.require("dojo.date.locale");

atg.commerce.csr.pricing.priceLists =
{

// Sets pricelist to user
selectPriceList : function (priceListFormName, priceListId) {
  if (priceListFormName == "setPriceListForm") {
    var form = document.getElementById("setPriceListForm");
    if (form) form.priceListId.value = priceListId;
  } else {
    var form = document.getElementById("setSalePriceListForm");
    if (form) form.salePriceListId.value = priceListId;
  }
  if (form) {
    atgSubmitAction({
      form: form,
      selectTabbedPanels : ["cmcProductCatalogSearchP"],
      sync: true
    });
  }
},

// Searches for price lists
searchForPriceLists:function(dateFormat){
    var incorrectStartDate = dojo.date.locale.format(dojo.date.add(new Date(),"year", 19), {datePattern: dateFormat, selector: "date"});
    var incorrectEndDate = dojo.date.locale.format(new Date(-1, 00, 01), {datePattern: dateFormat, selector: "date"});

    var startDate = document.getElementById('morePriceListsStartDateInput').value;
    var endDate = document.getElementById('morePriceListsEndDateInput').value;
	
    if (startDate != "" && startDate != dateFormat) {
      if (!dojo.date.locale.parse(startDate, {datePattern: dateFormat, selector: "date"})) {
        document.getElementById('morePriceListsStartDate').value = incorrectStartDate;
      } else {
        document.getElementById('morePriceListsStartDate').value = startDate;
      }
    } else {
      document.getElementById('morePriceListsStartDate').value = "";
    }
    if (endDate != "" && endDate != dateFormat) {
      if (!dojo.date.locale.parse(endDate, {datePattern: dateFormat, selector: "date"})) {
        document.getElementById('morePriceListsEndDate').value = incorrectEndDate;
      } else {
        var tempDateInc = dojo.date.add(dojo.date.locale.parse(endDate, {datePattern: dateFormat, selector: "date"}),"day", 1); 
        document.getElementById('morePriceListsEndDate').value = dojo.date.locale.format(tempDateInc, {datePattern: dateFormat, selector: "date"});
      }
    } else {
      document.getElementById('morePriceListsEndDate').value = "";
    }
    
    atg.commerce.csr.pricing.priceLists.morePriceLists.searchRefreshGrid();
}

};
