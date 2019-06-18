dojo.provide("atg.commerce.csr.order.appeasement");

/**
 * This function overrides the OOTB functionality that validates the fields
 * entered by the CSR on the appeasements page.
 */
atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount = function() {
	
	console.log("Item balance set to " + dojo.byId("itemBalance").value);
	console.log("Shipping balance set to" + dojo.byId("shippingBalance").value);
	console.log("Appeasement type set to " + dojo.byId("atg_commerce_csr_appeasement_appeasementType").value);
	if (dojo.byId("atg_commerce_csr_appeasement_appeasementType").value === "taxes") {
		var currencyCode = document.getElementById("atg_commerce_csr_order_appeasements_activeCurrencyCode").value;
		var spanTotal = dojo.byId("atg_commerce_csr_appeasement_total");
		var balance;
		var result = 0;
		var disable = true;
		var validAmount = false;

		// MFF CSC implementation does not support appeasements by percentage.
		// We go straight to the amount
		var amountValue = dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue").value;
		var amountOff = atg.commerce.csr.order.billing.parseAmount(amountValue);
		balance = dojo.byId("taxBalance").value;
		//test
		//balance = 100;
		
		console.log("Tax balance set to " + balance);
		
		if (!isNaN(amountOff) && (amountOff <= balance) && (amountOff > 0)) {
			var formattedAmount = atg.commerce.csr.order.billing.formatAmount(amountOff, container.currencyCode);
	        if (spanTotal.textContent !== undefined) {
	            spanTotal.textContent = formattedAmount;
	          } else {
	            spanTotal.innerText = formattedAmount;
	          }
	        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = atg.commerce.csr.order.billing.roundAmount(amountOff);
	        validAmount = true;
	        console.log("Valid amountoff  " + amountOff);
		} else {
	        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = 0;
	        if (spanTotal.textContent !== undefined) {
	          spanTotal.textContent = "";
	        } else {
	          spanTotal.innerText = "";
	        }			
		}
		var appeasementReasonCodeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
		var codeSelection = appeasementReasonCodeSelect.options[appeasementReasonCodeSelect.selectedIndex].value;
		console.log("The Reason Code selection is " + codeSelection + " valid amount " + validAmount);
		if (validAmount && dojo.string.trim(codeSelection).length > 0) {
			console.log("Enabling the apply appeasements button");
			disable = false;
		}
		dojo.byId("applyAppeasementValuesButton").disabled = disable;
	} else {
		//call out of the box validation script
		atg.commerce.csr.order.appeasement.displayAppeasementAmount();
	}
};
